# file-downloader
💾 파일의 청크를 나누어 멀티 스레드로 저장하기 for 한국인

**Fix**
* 비동기 함수가 완료되기 전 속도를 측정했던 오류 정상화
* 테스트 코드가 반복 실행 되어도 같은 결과를 반환하도록 수정
* Http 캐시를 테스트 전에 자동으로 수행하도록 수정

**ToDo**
* Netty 적용해보기
* RandomAccessFile Cache 적용해보기 

**Preview** <br>
![Screenshot 2025-02-21 at 10 08 03 PM](https://github.com/user-attachments/assets/069c9d8e-9ed7-48fd-818f-bf596c9d93d5)

![Screenshot 2025-02-21 at 10 09 09 PM](https://github.com/user-attachments/assets/ac58a6f7-f3d5-4ecd-9690-27e054b256a8)
