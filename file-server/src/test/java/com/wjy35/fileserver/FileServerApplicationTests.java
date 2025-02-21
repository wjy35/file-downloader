package com.wjy35.fileserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FileServerApplicationTests {

    @Autowired
    DefaultFileController defaultFileController;


    @Autowired
    FileService fileService;

    @Autowired
    ChunkRepository chunkRepository;
}
