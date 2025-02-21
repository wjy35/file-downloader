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
    @Autowired
    FileService fileService;

    @GetMapping("/")
    public ResponseEntity<byte[]> read(@RequestParam long startOffset,@RequestParam int chunkSize) {
        Chunk chunk = fileService.readDefaultFileChunk(startOffset,chunkSize);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Length", String.valueOf(chunk.getData().length));
        headers.set("Content-Type", "application/octet-stream");
        headers.set("Content-Range", "bytes " + startOffset + "-" + (startOffset+chunk.getData().length-1) + "/" + chunk.getFileLength());

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
