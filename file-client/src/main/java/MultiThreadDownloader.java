import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadDownloader extends Downloader{
    private static final int THREAD_POOL_COUNT = 4;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final ReentrantLock lock = new ReentrantLock();

    public MultiThreadDownloader(String baseUrl, String requestName, String savePath, String saveName, int chunkSize) {
        super(baseUrl, requestName, savePath, saveName, chunkSize);
    }

    @Override
    public void download() {
        try {
            tryToDownload();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryToDownload() throws InterruptedException {
        long fileSize = requestFileSize();
        List<Callable<Void>> callableList = new ArrayList<>((int) (fileSize/chunkSize+1));

        for(long offset = 0; offset<fileSize; offset+= chunkSize){
            final long startOffset = offset;
            callableList.add(()->{
                byte[] chunk = downloadChunk(startOffset, startOffset+chunkSize);

                writeChunk(chunk,startOffset);
                return null;
            });
        }

        executorService.invokeAll(callableList);
    }

    @Override
    protected void writeChunk(byte[] data, long startOffset) throws IOException {
        lock.lock();

        raf.seek(startOffset);
        raf.write(data);

        lock.unlock();
    }
}
