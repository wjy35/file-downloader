package com.wjy35.fileserver;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

@Repository
@RequiredArgsConstructor
public class ChunkRepository {
    private final FileCache cache;

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

    public Chunk findByPath(String name,long startOffset,long endOffset){
        try {
            return tryToFindByPath(name,startOffset,endOffset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Chunk tryToFindByPath(String name,long startOffset,long endOffset) throws IOException {
        int chunkSize = (int)(endOffset - startOffset);
        byte[] buffer = new byte[chunkSize];
        int readByte = 0;
        RandomAccessFile raf = cache.findByName(name).orElseGet(()->createRandomAccessFile(name));

        synchronized (raf){
            raf.seek(startOffset);
            readByte = raf.read(buffer);
        }

        return Chunk.builder()
                .data(readByte<chunkSize ? Arrays.copyOf(buffer,readByte) : buffer)
                .fileLength(raf.length())
                .build();
    }

    private RandomAccessFile createRandomAccessFile(String name){
        try {
            RandomAccessFile raf = new RandomAccessFile(new ClassPathResource("static/"+name).getFile(),"r");

            cache.save(name,raf);

            return raf;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
