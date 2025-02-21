import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class Downloader {
    protected static final String DOWNLOAD_DIRECTORY_PATH = System.getProperty("user.home")+"/Downloads/";
    protected static final String BASE_URL = "http://localhost:8080/";
    protected static final int CHUNK_SIZE = 128;
    private final HttpClient httpClient;
    protected final RandomAccessFile raf;
    private final String filePath;

    public Downloader(String name){
        this.filePath = DOWNLOAD_DIRECTORY_PATH+name;
        this.httpClient = HttpClient.newHttpClient();
        try {
            raf = new RandomAccessFile(new File(filePath), "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    abstract public void download();

    protected final long requestFileSize(){
        try {
            return tryToRequestFileSize();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected final long tryToRequestFileSize() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest fileSizeRequest = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "file-size"))
                .GET()
                .build();

        HttpResponse<String> fileSizeResponse = httpClient.send(fileSizeRequest, HttpResponse.BodyHandlers.ofString());

        return Long.parseLong(fileSizeResponse.body());
    }

    protected final byte[] downloadChunk(long startOffset, long endOffset){
        try {
            return tryToDownLoadChunk(startOffset, endOffset);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected final byte[] tryToDownLoadChunk(long startOffset, long endOffset) throws IOException, InterruptedException, URISyntaxException {
        String urlWithParam = getUrlWithParam(startOffset,endOffset);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlWithParam))
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        return response.body();
    }

    protected abstract void writeChunk(byte[] data, long startOffset);

    protected final String getUrlWithParam(long startOffset,long endOffset){
        return BASE_URL + "?startOffset=" + startOffset + "&endOffset="+endOffset;
    }

    public String getFilePath(){
        return filePath;
    }
}
