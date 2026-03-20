package com.example.COFFEEHOUSE.Utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.NCKH_TRAFFICAPP.Exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component("cloudinaryFileStorage")
@RequiredArgsConstructor
@Slf4j
public class CloudinaryFileStorage implements FileStorage {

    private final Cloudinary cloudinary;

    @Override
    public String saveFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BusinessLogicException("File rỗng hoặc không tồn tại.");
        }

        try {
            // Tạo public_id duy nhất (bỏ originalName để tránh conflict và tăng tốc)
            String publicId = folder + "/" + UUID.randomUUID();

            // Upload RAW file lên Cloudinary - KHÔNG có bất kỳ transformation nào
            // → Tốc độ tối đa, FE sẽ xử lý optimize ảnh
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "public_id", publicId,
                    "resource_type", "auto"
                    // Bỏ "folder" param vì đã có trong public_id
                    // Bỏ hết transformations để upload nhanh nhất
                )
            );

            // Trả về URL gốc
            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("✅ Uploaded: {} ({}KB)", secureUrl, file.getSize() / 1024);

            return secureUrl;

        } catch (IOException e) {
            log.error("❌ Upload failed: {}", e.getMessage());
            throw new BusinessLogicException("Không thể lưu file: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String folder, String fileName) {
        try {
            // Nếu fileName là full URL, extract public_id
            String publicId;
            if (fileName.startsWith("http://") || fileName.startsWith("https://")) {
                // Extract public_id từ URL Cloudinary
                // URL format: https://res.cloudinary.com/{cloud_name}/image/upload/v{version}/{public_id}.{format}
                String[] parts = fileName.split("/upload/");
                if (parts.length > 1) {
                    String pathAfterUpload = parts[1];
                    // Remove version number (v1234567890/)
                    pathAfterUpload = pathAfterUpload.replaceFirst("v\\d+/", "");
                    // Remove file extension
                    publicId = pathAfterUpload.substring(0, pathAfterUpload.lastIndexOf('.'));
                } else {
                    publicId = folder + "/" + fileName;
                }
            } else {
                publicId = folder + "/" + fileName;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("File deleted from Cloudinary. Public ID: {}, Result: {}", publicId, result.get("result"));

        } catch (Exception e) {
            log.error("Không thể xóa file từ Cloudinary: {}", e.getMessage(), e);
            throw new BusinessLogicException("Không thể xóa file: " + e.getMessage());
        }
    }
}
