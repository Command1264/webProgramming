package com.github.command1264.webProgramming.dao;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Component
public class FileDao {

    public boolean saveMultipartFile(String path,  MultipartFile file) {
        return saveMultipartFile(new File(path), file);
    }

    public boolean saveMultipartFile(File saveFile, MultipartFile file) {
        if (!saveFile.getParentFile().exists())
            if (!saveFile.getParentFile().mkdirs())
                return false;

        try {
            file.transferTo(saveFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
