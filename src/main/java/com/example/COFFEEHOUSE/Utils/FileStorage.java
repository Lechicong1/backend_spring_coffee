package com.example.COFFEEHOUSE.Utils;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {
    public String saveFile(MultipartFile file, String folder);
    public void deleteFile(String folder, String fileName);
}
