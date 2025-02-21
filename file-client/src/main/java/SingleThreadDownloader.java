import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

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
