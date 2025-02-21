import java.io.IOException;

public class SingleThreadDownloader extends Downloader{

    public SingleThreadDownloader() {
        super("single-thread.png");
    }

    @Override
    public void download() {
        long fileSize = requestFileSize();

        for(long startOffset = 0; startOffset<fileSize; startOffset+=CHUNK_SIZE){
            byte[] chunk = downloadChunk(startOffset,startOffset+CHUNK_SIZE);
            writeChunk(chunk,startOffset);
        }
    }

    @Override
    protected void writeChunk(byte[] data, long startOffset) {
        try {
            raf.seek(startOffset);
            raf.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
