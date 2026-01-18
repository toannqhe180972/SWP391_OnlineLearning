package com.fa.training.service;

import com.fa.training.constant.FileConstants;
import com.fa.training.message.ErrorMessages;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    public String uploadAvatar(MultipartFile file) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.FILE_EMPTY);
        }

        if (file.getSize() > FileConstants.MAX_AVATAR_SIZE) {
            throw new IllegalArgumentException(ErrorMessages.FILE_TOO_LARGE);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        if (!isValidExtension(extension)) {
            throw new IllegalArgumentException(ErrorMessages.FILE_INVALID_TYPE);
        }

        // Generate unique filename
        String newFilename = UUID.randomUUID().toString() + "." + extension;

        // Create directory if not exists
        Path uploadPath = Paths.get(FileConstants.UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return public URL
        return FileConstants.UPLOAD_URL_PREFIX + newFilename;
    }

    private String getFileExtension(String filename) {
        if (filename == null)
            return "";
        int lastDot = filename.lastIndexOf('.');
        return (lastDot == -1) ? "" : filename.substring(lastDot + 1).toLowerCase();
    }

    private boolean isValidExtension(String extension) {
        for (String allowed : FileConstants.ALLOWED_IMAGE_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
