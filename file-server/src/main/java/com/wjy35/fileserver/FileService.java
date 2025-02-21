package com.wjy35.fileserver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private static final String DEFAULT_FILE_PATH = "hi.jpeg";

    public long readFileSize(String path){
        File file = fileRepository.findByPath(path);

        return file.length();
    }

    public long readDefaultFileSize(){
        return readFileSize(DEFAULT_FILE_PATH);
    }

    public Chunk readDefaultFileChunk(long startOffset,int chunkSize){
        return readChunk(DEFAULT_FILE_PATH,startOffset,chunkSize);
    }

    public Chunk readChunk(String path, long startOffset,int chunkSize){
        File file = fileRepository.findByPath(path);

        try{
            return tryToReadChunk(file,startOffset,chunkSize);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private Chunk tryToReadChunk(File file, long startOffset,int chunkSize) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file,"r");
        raf.seek(startOffset);

        byte[] buffer = new byte[chunkSize];
        int readByte = raf.read(buffer);
        return Chunk.builder()
                .data(readByte<chunkSize ? Arrays.copyOf(buffer,readByte) : buffer)
                .startOffset(startOffset)
                .endOffset(startOffset+readByte-1)
                .fileLength(file.length())
                .build();
    }
}
