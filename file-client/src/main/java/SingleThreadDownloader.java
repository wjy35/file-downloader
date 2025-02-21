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

public class SingleThreadDownloader {
    public static final String FILE_SAVE_PATH = System.getProperty("user.home")+"/Downloads/single-thread.jpeg";
    public static final String baseUrl = "http://localhost:8080/";
    public static final int CHUNK_SIZE = 128;
    private final HttpClient httpClient;
    private final RandomAccessFile raf;

    public SingleThreadDownloader(){
        this.httpClient = HttpClient.newHttpClient();
        try {
            raf = new RandomAccessFile(new File(FILE_SAVE_PATH), "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void download(){
        long fileSize = requestFileSize();
        for(long startOffset = 0; startOffset<fileSize; startOffset+=CHUNK_SIZE){
            byte[] chunk = downloadChunk(startOffset);
            writeChunk(chunk,startOffset);
        }
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

    private void writeChunk(byte[] data, long startOffset) {
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
