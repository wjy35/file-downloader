import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileDownloadTest {
    @Test
    void Compare_download_speed_MultiThread_vs_SingleThread(){
        // given
        String baseUrl = "http://localhost:8080/";
        String requestName = "hi.png";
        String savePath = System.getProperty("user.home")+"/Downloads";
        int chunkSize = 128;
        MultiThreadDownloader multiThreadDownloader = new MultiThreadDownloader(
                baseUrl,
                requestName,
                savePath,
                "multi",
                chunkSize
                );
        SingleThreadDownloader singleThreadDownloader = new SingleThreadDownloader(
                baseUrl,
                requestName,
                savePath,
                "single",
                chunkSize
        );
        multiThreadDownloader.cacheForTest();
        singleThreadDownloader.cacheForTest();

        // when
        long multiStart = System.currentTimeMillis();
        multiThreadDownloader.download();
        long multiEnd = System.currentTimeMillis();
        long multiDuration = multiEnd-multiStart;
        byte[] multiDownloadData = FileUtil.getAllData(multiThreadDownloader.getFilePath());

        long singleStart = System.currentTimeMillis();
        singleThreadDownloader.download();
        long singleEnd = System.currentTimeMillis();
        long singleDuration = singleEnd-singleStart;
        byte[] singleDownloadData = FileUtil.getAllData(singleThreadDownloader.getFilePath());

        // then
        System.out.println("singleDuration = " + singleDuration + "ms");
        System.out.println("multiDuration = " + multiDuration + "ms");
        assertTrue(multiDuration<singleDuration);
        assertArrayEquals(multiDownloadData,singleDownloadData);

        FileUtil.delete(multiThreadDownloader.getFilePath());
        FileUtil.delete(singleThreadDownloader.getFilePath());
    }
}
