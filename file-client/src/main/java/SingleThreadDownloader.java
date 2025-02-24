import java.io.IOException;

public class SingleThreadDownloader extends Downloader{

    public SingleThreadDownloader(String baseUrl, String requestName, String savePath, String saveName, int chunkSize) {
        super(baseUrl, requestName, savePath, saveName, chunkSize);
    }

    @Override
    public void download() {
        long fileSize = requestFileSize();

        for(long startOffset = 0; startOffset<fileSize; startOffset+= chunkSize){
            byte[] chunk = downloadChunk(startOffset,startOffset+ chunkSize);
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
