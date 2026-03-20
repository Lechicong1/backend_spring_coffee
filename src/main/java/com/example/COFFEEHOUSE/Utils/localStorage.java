package com.example.COFFEEHOUSE.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component("localStorage")
@Slf4j
public class localStorage implements FileStorage {
    @Value("${app.upload-file.base-path}")
    private String uploadBasePath;

    @Override
    public String saveFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File rỗng hoặc không tồn tại.");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new RuntimeException("Tên file null.");
        }
        String fileName = UUID.randomUUID() + "_" + originalName;
        try {
            Path uploadPath = Paths.get(uploadBasePath, folder);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn đầy đủ để lưu vào database
            String fullPath = filePath.toAbsolutePath().toString();
            log.info("File đã lưu tại: {}", fullPath);
            return fullPath;
        } catch (IOException e) {
            log.error("Không thể lưu file: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể lưu file: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String folder, String filePath) {
        try {
            Path path;

            // Nếu filePath là đường dẫn đầy đủ (absolute path)
            if (filePath.startsWith("/") || filePath.contains(uploadBasePath)) {
                path = Paths.get(filePath);
            } else {
                // Nếu chỉ là tên file, build path từ base path
                path = Paths.get(uploadBasePath, folder, filePath);
            }

            if (Files.deleteIfExists(path)) {
                log.info("Đã xóa file: {}", path);
            } else {
                log.warn("File không tồn tại để xóa: {}", path);
            }
        } catch (IOException e) {
            log.error("Không thể xóa file: {}", e.getMessage(), e);
        }
    }
}
