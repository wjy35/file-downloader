package com.wjy35.fileserver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FileService {
    private final ChunkRepository chunkRepository;
    private static final String DEFAULT_FILE_PATH = "hi.png";

    public long readFileSize(String path){
        return chunkRepository.getLengthByPath(path);
    }

    public Chunk readChunk(String path, long startOffset,long endOffset){
        return chunkRepository.findByPath(path,startOffset,endOffset);
    }

    public long readDefaultFileSize(){
        return readFileSize(DEFAULT_FILE_PATH);
    }

    public Chunk readDefaultFileChunk(long startOffset,long endOffset){
        return readChunk(DEFAULT_FILE_PATH,startOffset,endOffset);
    }
}
