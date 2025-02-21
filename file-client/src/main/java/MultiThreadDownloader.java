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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadDownloader {
    public static final String FILE_SAVE_PATH = System.getProperty("user.home")+"/Downloads/multi-thread.jpeg";
    public static final String baseUrl = "http://localhost:8080/";
    public static final int CHUNK_SIZE = 128;
    private final HttpClient httpClient;
    private final RandomAccessFile raf;
    private final ExecutorService executorService;

    public MultiThreadDownloader(){
        this.httpClient = HttpClient.newHttpClient();
        this.executorService = Executors.newFixedThreadPool(4);
        try {
            raf = new RandomAccessFile(new File(FILE_SAVE_PATH), "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void download(){
        long fileSize = requestFileSize();
        for(long offset = 0; offset<fileSize; offset+=CHUNK_SIZE){
            final long startOffset = offset;
            executorService.execute(()->{
                byte[] chunk = downloadChunk(startOffset);
                synchronizedWriteChunk(chunk,startOffset);
            });
        }

        executorService.shutdown();
    }

    private long requestFileSize(){
        try {
            return tryToRequestFileSize();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private long tryToRequestFileSize() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest fileSizeRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "file-size"))
                .GET()
                .build();
        HttpResponse<String> fileSizeResponse = httpClient.send(fileSizeRequest, HttpResponse.BodyHandlers.ofString());

        return Long.parseLong(fileSizeResponse.body());
    }

    private byte[] downloadChunk(long startOffset){
        try {
            return tryToDownLoadChunk(startOffset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] tryToDownLoadChunk(long startOffset) throws IOException, InterruptedException, URISyntaxException {
        String urlWithParam = baseUrl + "?startOffset=" + startOffset + "&chunkSize="+CHUNK_SIZE;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlWithParam))
                .GET()
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        return response.body();
    }

    private synchronized void synchronizedWriteChunk(byte[] data, long startOffset) {
        try {
            raf.seek(startOffset);
            raf.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getAllData(){
        try {
            return Files.readAllBytes(Path.of(FILE_SAVE_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
