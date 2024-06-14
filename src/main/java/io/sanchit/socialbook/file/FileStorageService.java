package io.sanchit.socialbook.file;

import io.sanchit.socialbook.book.Book;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${spring.application.file.upload.photos-output-path}")
    private String uploadPath;

    public String saveFile(@NotNull MultipartFile file,
                           @NotNull Integer userId) {
        final String subPath = "users" + File.separator + userId;
        return uploadFile(file, subPath);
    }

    private String uploadFile(@NotNull MultipartFile file, @NotNull String subPath) {
        final String fileUploadPath = uploadPath + File.separator + subPath;

        File targetFolder = new File(fileUploadPath);
        if (!targetFolder.exists()) {
            boolean mkdirs = targetFolder.mkdirs();

            if (!mkdirs) {
                log.warn("Failed to create directory: {}", targetFolder.getAbsolutePath());
                return null;
            }
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        String targetFilePath = fileUploadPath
                + File.separator
                + System.currentTimeMillis()
                + "."
                + fileExtension;
        Path targetPath = Paths.get(targetFilePath);

        try {
            Files.write(targetPath, file.getBytes());
            log.info("Successfully uploaded file: {}", targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("File was not uploaded: {}", targetFilePath);
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        int lastIndexOf = fileName.lastIndexOf(".");

        if (lastIndexOf == -1) {
            return "";
        }

        return fileName.substring(lastIndexOf + 1).toLowerCase();
    }
}
