package com.wjy35.fileserver;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import java.io.File;
import java.io.IOException;

@Repository
public class FileRepository {
    public File findByPath(String path){
        try {
            return tryToFindByPath(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File tryToFindByPath(String path) throws IOException {
        return new ClassPathResource("static/"+path).getFile();
    }
}
