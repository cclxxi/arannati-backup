package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.file-storage.base-path:uploads}")
    private String baseStoragePath;

    @Override
    public String storeFile(MultipartFile file, String directory) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file");
        }

        // Create directory if it doesn't exist
        Path directoryPath = Paths.get(baseStoragePath, directory);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // Generate a unique filename to prevent collisions
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + fileExtension;

        // Copy the file to the target location
        Path targetPath = directoryPath.resolve(newFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        log.info("Stored file {} in directory {}", newFilename, directory);
        return newFilename;
    }

    @Override
    public Path getFilePath(String filename, String directory) {
        return Paths.get(baseStoragePath, directory, filename);
    }

    @Override
    public boolean deleteFile(String filename, String directory) {
        try {
            Path filePath = getFilePath(filename, directory);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting file {} from directory {}: {}", filename, directory, e.getMessage());
            return false;
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}