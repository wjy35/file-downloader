import java.io.IOException;
import java.net.URI;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

public class MultiplexingDownloader extends Downloader {
    private final int MAX_CONNECTION_COUNT = 10;
    private final int MAX_THREAD_COUNT = 4;
    private final ReentrantLock lock = new ReentrantLock();
    private final Semaphore semaphore = new Semaphore(MAX_CONNECTION_COUNT);
    private final NioEventLoopGroup group;
    private final Bootstrap bootstrap;
    private CountDownLatch latch;

    public MultiplexingDownloader(String host, int port, String requestName, String savePath, String saveName, int chunkSize) {
        super(host, port, requestName, savePath, saveName, chunkSize);

        group = new NioEventLoopGroup(MAX_THREAD_COUNT);
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
                        pipeline.addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws IOException {
                                long startOffset = getStartOffset(response);
                                byte[] chunk = readChunk(response);

                                writeChunk(chunk,startOffset);
                                latch.countDown();
                            }
                        });
                    }
                });
    }

    private void tryToDownload() throws Exception {
        long fileSize = fetchFileSize();

        int chunkCount = (int)fileSize/chunkSize + (fileSize%chunkSize==0 ? 0:1);
        latch = new CountDownLatch(chunkCount);
        for(long startOffset=0; startOffset<fileSize; startOffset+=chunkSize){
            sendHttpRequest(bootstrap,startOffset);
        }
        latch.await();
        group.shutdownGracefully();
    }

    private void sendHttpRequest(Bootstrap bootstrap,long startOffset) throws InterruptedException {
        URI uri = URI.create(baseUrl+"?name="+requestName+"&startOffset="+startOffset+"&endOffset="+(startOffset+chunkSize));

        semaphore.acquire();
        ChannelFuture future = bootstrap.connect(host, port);
        future.addListener((ChannelFutureListener) channelFuture -> {
            try{
                if (!channelFuture.isSuccess()) return;
                Channel channel = channelFuture.channel();
                FullHttpRequest request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath() + "?" + uri.getQuery());
                request.headers().set(HttpHeaderNames.HOST, host);
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

                channel.writeAndFlush(request);
            }finally {
                semaphore.release();
            }
        });
    }

    private long getStartOffset(FullHttpResponse response){
        StringTokenizer st = new StringTokenizer(response.headers().get(HttpHeaderNames.CONTENT_RANGE)," -");
        st.nextToken();
        return Long.parseLong(st.nextToken());
    }

    private byte[] readChunk(FullHttpResponse response){
        ByteBuf byteBuf = response.content();
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);

        return data;
    }

    @Override
    public void download() {
        try {
            tryToDownload();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeChunk(byte[] data, long startOffset) throws IOException {
        lock.lock();
        try {
            raf.seek(startOffset);
            raf.write(data);
        } finally {
            lock.unlock();
        }
    }
}