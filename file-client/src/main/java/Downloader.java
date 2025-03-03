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
    protected final String baseUrl;
    protected final String host;
    protected final int port;
    protected final String requestName;
    protected final int chunkSize;
    protected final String filePath;
    private final HttpClient httpClient;

    protected RandomAccessFile raf;
    public Downloader(String host,int port,String requestName,String savePath, String saveName, int chunkSize){
        this.baseUrl = "http://"+host+":"+port+"/";
        this.host = host;
        this.port = port;
        this.requestName = requestName;
        this.filePath = savePath + "/" +saveName;
        this.chunkSize = chunkSize;
        this.httpClient = HttpClient.newHttpClient();

    }

    public void startDownload(){
        try {
            raf = new RandomAccessFile(new File(filePath), "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        download();
    }

    protected abstract void download();
    protected abstract void writeChunk(byte[] data, long startOffset) throws IOException;

    protected final long fetchFileSize(){
        try {
            return tryToRequestFileSize();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected final long tryToRequestFileSize() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest fileSizeRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "file-size?name="+requestName))
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
        String urlWithParam = getUrlWithParam(startOffset, endOffset);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlWithParam))
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        return response.body();
    }

    protected final String getUrlWithParam(long startOffset,long endOffset){
        return baseUrl +  "?name=" + requestName +"&startOffset=" + startOffset + "&endOffset="+endOffset;
    }

    public final void cacheForTest(){
        for(int i=0; i<10; i++) {
            downloadChunk(0,0);
        }
    }

    public String getFilePath(){
        return filePath;
    }
}
