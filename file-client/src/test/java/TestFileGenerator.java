import org.junit.jupiter.api.Test;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TestFileGenerator {
    // ToDo test data init을 load 하기

    final String HOST = "";
    final int PORT = 80;
    final String REQUEST_NAME = "god.png";
    final String SAVE_PATH = System.getProperty("user.home")+"/a-file-tmp/";
    final int CHUNK_SIZE = 1024;
    final String TEST_RESULT_FILE_NAME = "performance-test.csv";

    final int TEST_COUNT = 10;

    @Test
    void createPerformanceTestFile(){
        SingleThreadDownloader singleThreadDownloader = new SingleThreadDownloader(
                HOST,
                PORT,
                REQUEST_NAME,
                SAVE_PATH,
                "single-thread.png",
                CHUNK_SIZE
        );

        MultiThreadDownloader multiThreadDownloader = new MultiThreadDownloader(
                HOST,
                PORT,
                REQUEST_NAME,
                SAVE_PATH,
                "multi-thread.png",
                CHUNK_SIZE
        );

        MultiplexingDownloader multiplexingDownloader = new MultiplexingDownloader(
                HOST,
                PORT,
                REQUEST_NAME,
                SAVE_PATH,
                "multiplexing.png",
                CHUNK_SIZE
        );

        singleThreadDownloader.cacheForTest();
        multiThreadDownloader.cacheForTest();
        multiplexingDownloader.cacheForTest();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SAVE_PATH+TEST_RESULT_FILE_NAME))) {
            bw.write("Single Thread,Multi Thread,Multiplexing");
            bw.newLine();

            for(int i=0; i<TEST_COUNT; i++){
                long[] durations = new long[3];
                durations[0] = getDuration(singleThreadDownloader::startDownload);
                durations[1] = getDuration(multiThreadDownloader::startDownload);
                durations[2] = getDuration(multiplexingDownloader::startDownload);

                bw.write(arrayToCsvString(durations));
                bw.newLine();

                FileUtil.delete(singleThreadDownloader.getFilePath());
                FileUtil.delete(multiThreadDownloader.getFilePath());
                FileUtil.delete(multiplexingDownloader.getFilePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long getDuration(Runnable downloader){
        long start = System.currentTimeMillis();
        downloader.run();
        long end = System.currentTimeMillis();

        return end-start;
    }

    private String arrayToCsvString(long[] array){
        StringBuilder sb = new StringBuilder();

        if(array.length==0) throw new RuntimeException();

        sb.append(array[0]);
        for(int i=1; i<array.length; i++){
            sb.append(",").append(array[i]);
        }

        return sb.toString();
    }
}
