package com.wjy35.fileserver;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

@Repository
public class ChunkRepository {
    public long getLengthByPath(String path){
        try {
            return tryToGetLengthByPath(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long tryToGetLengthByPath(String path) throws IOException {
        return new ClassPathResource("static/"+path).getFile().length();
    }

    public Chunk findByPath(String path,long startOffset,long endOffset){
        try {
            return tryToFindByPath(path,startOffset,endOffset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Chunk tryToFindByPath(String path,long startOffset,long endOffset) throws IOException {
        // ToDo Test Cache Performance
        File file = new ClassPathResource("static/"+path).getFile();
        RandomAccessFile raf = new RandomAccessFile(file,"r");
        raf.seek(startOffset);

        // ToDo Validate chunkSize
        int chunkSize = (int)(endOffset - startOffset);

        byte[] buffer = new byte[chunkSize];
        int readByte = raf.read(buffer);

        return Chunk.builder()
                .data(readByte<chunkSize ? Arrays.copyOf(buffer,readByte) : buffer)
                .fileLength(raf.length())
                .build();
    }
}
