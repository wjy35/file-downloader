package com.wjy35.fileserver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FileService {
    private final ChunkRepository chunkRepository;
    private static final String DEFAULT_FILE_NAME = "god.png";

    public long readFileSize(String name){
        return chunkRepository.getLengthByPath(name);
    }

    public Chunk readChunk(String name, long startOffset,long endOffset){
        return chunkRepository.findByPath(name,startOffset,endOffset);
    }

    public long readDefaultFileSize(){
        return readFileSize(DEFAULT_FILE_NAME);
    }

    public Chunk readDefaultFileChunk(long startOffset,long endOffset){
        return readChunk(DEFAULT_FILE_NAME,startOffset,endOffset);
    }
}
