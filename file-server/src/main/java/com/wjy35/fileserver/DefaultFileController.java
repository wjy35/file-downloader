package com.wjy35.fileserver;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DefaultFileController {
    private final FileService fileService;

    @GetMapping("/")
    public ResponseEntity<byte[]> read(@RequestParam long startOffset, @RequestParam long endOffset) {
        Chunk chunk = fileService.readDefaultFileChunk(startOffset, endOffset);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Length", String.valueOf(chunk.getData().length));
        headers.set("Content-Type", "application/octet-stream");
        headers.set("Content-Range", "bytes " + startOffset + "-" + endOffset + "/" + chunk.getFileLength());

        System.out.println("startOffset = " + startOffset);
        System.out.println("endOffset = " + endOffset);

        return ResponseEntity
                .status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .body(chunk.data);
    }

    @GetMapping("/file-size")
    public ResponseEntity<Long> fileSize() {
        long fileSize = fileService.readDefaultFileSize();

        return ResponseEntity
                .ok()
                .body(fileSize);
    }

}
