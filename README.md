**Fix**
* Python을 통한 성능 테스트 시각화
<img width="580" alt="Screenshot 2025-03-03 at 6 37 58 PM" src="https://github.com/user-attachments/assets/1eb19bc7-6d2b-4cf5-9f35-4bfccbb7c3cb" />
<br>
<br>

* Netty를 사용해 client에 Multiplexing을 사용하도록 개선 <br>
  
![Screenshot 2025-03-02 at 9 21 37 PM](https://github.com/user-attachments/assets/dd904dae-4929-4aa0-9dcb-654b98791d5e)
<br>

* 성능 테스트를 위해 서버를 배포<br>
* https://hub.docker.com/r/wangsun7/file-downloader <br>
```
sudo docker run -d \
  --name file-downloader \
  -p 80:8080 \
  wangsun7/file-downloader:latest
```  
![multiplexing drawio](https://github.com/user-attachments/assets/71f3b069-cc41-4f47-b795-b7b600a748ec)
<br>

* UI 수정

**Challenges**

* Synchronized 를 ReentrantLock으로 변경 <br>
* ExcutorService의 Thread Pool을 효율적으로 사용하도록 개선 <br>
* RandomAccessFile Cache 적용 (성능 비교) <br>
<img width="388" alt="Screenshot 2025-02-26 at 4 46 25 PM" src="https://github.com/user-attachments/assets/b6b589ea-846c-4aa6-8bf5-0db03f8e753d" />
<br>반복적으로 사용할수록 JVM 역시 RandomAccessFile Instance를 캐싱해 성능이 개선됨<br>
<br>
<br>

**ToDo**
* 대용량 파일에서 RandomAccessFile은 생성 비용이 커짐, 캐시 성능 테스트 해보기

<br>

**Preview** <br>
![god](https://github.com/user-attachments/assets/9cb28506-f098-446a-804c-1cec112b5674)

```java
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

```
