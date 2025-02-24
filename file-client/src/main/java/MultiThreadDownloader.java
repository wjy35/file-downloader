import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiThreadDownloader extends Downloader{
    private final ExecutorService executorService;
    private static final int THREAD_POOL_COUNT = 4;

    public MultiThreadDownloader(String baseUrl, String requestName, String savePath, String saveName, int chunkSize) {
        super(baseUrl, requestName, savePath, saveName, chunkSize);

        // ToDo Thread Pool 효율적으로 관리
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_COUNT);
    }

    @Override
    public void download() {
        long fileSize = requestFileSize();

        for(long offset = 0; offset<fileSize; offset+= chunkSize){
            final long startOffset = offset;
            executorService.execute(()->{
                byte[] chunk = downloadChunk(startOffset, startOffset+chunkSize);

                writeChunk(chunk,startOffset);
            });
        }

        executorService.shutdown();
        try {
            if(executorService.awaitTermination(100, TimeUnit.SECONDS)){
                executorService.shutdown();
            }
        } catch (InterruptedException e) {
            executorService.shutdown();
        }
    }

    @Override
    protected synchronized void writeChunk(byte[] data, long startOffset) {
        try {
            raf.seek(startOffset);
            raf.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
