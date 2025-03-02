import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileDownloadTest {
    final String host = "";
    final int port = 80;
    final String requestName = "god.png";
    final String savePath = System.getProperty("user.home")+"/Downloads";
    final int chunkSize = 1024;

    @Test
    void Compare_download_speed_MultiThread_vs_SingleThread(){
        // given
        MultiThreadDownloader multiThreadDownloader = new MultiThreadDownloader(
                host,
                port,
                requestName,
                savePath,
                "multi.png",
                chunkSize
                );
        SingleThreadDownloader singleThreadDownloader = new SingleThreadDownloader(
                host,
                port,
                requestName,
                savePath,
                "single.png",
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

    @Test
    void Compare_download_speed_MultiThread_vs_Multiplexing(){
        // given
        MultiThreadDownloader multiThreadDownloader = new MultiThreadDownloader(
                host,
                port,
                requestName,
                savePath,
                "multi-thread.png",
                chunkSize
        );

        MultiplexingDownloader multiplexingDownloader = new MultiplexingDownloader(
                host,
                port,
                requestName,
                savePath,
                "multiplexing.png",
                chunkSize
        );
        multiThreadDownloader.cacheForTest();
        multiplexingDownloader.cacheForTest();

        // when
        long multiplexingStart = System.currentTimeMillis();
        multiplexingDownloader.download();
        long multiplexingEnd = System.currentTimeMillis();
        long multiplexingDuration = multiplexingEnd-multiplexingStart;
        byte[] multiplexingData = FileUtil.getAllData(multiplexingDownloader.getFilePath());

        long multiThreadStart = System.currentTimeMillis();
        multiThreadDownloader.download();
        long multiThreadEnd = System.currentTimeMillis();
        long multiThreadDuration = multiThreadEnd-multiThreadStart;
        byte[] multiThreadData = FileUtil.getAllData(multiThreadDownloader.getFilePath());


        // then
        System.out.println("multiThreadDuration = " + multiThreadDuration+"ms");
        System.out.println("multiplexingDuration = " + multiplexingDuration+"ms");
        assertTrue(multiplexingDuration<multiThreadDuration);
        assertArrayEquals(multiThreadData,multiplexingData);

        FileUtil.delete(multiplexingDownloader.getFilePath());
        FileUtil.delete(multiThreadDownloader.getFilePath());
    }
}
