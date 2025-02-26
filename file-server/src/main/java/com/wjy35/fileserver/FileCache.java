package com.wjy35.fileserver;

import org.springframework.stereotype.Component;

import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class FileCache {
    private final Map<String, RandomAccessFile> nameToRandomAccessFile;

    public FileCache() {
        this.nameToRandomAccessFile = new HashMap<>();
    }

    public Optional<RandomAccessFile> findByName(String name){
        return Optional.ofNullable(nameToRandomAccessFile.get(name));
    }

    public void save(String name,RandomAccessFile randomAccessFile){
        nameToRandomAccessFile.put(name,randomAccessFile);
    }
}
