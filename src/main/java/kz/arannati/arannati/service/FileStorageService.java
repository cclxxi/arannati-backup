package kz.arannati.arannati.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Service for handling file storage operations
 */
public interface FileStorageService {

    /**
     * Store a file in the file system
     * @param file The file to store
     * @param directory The directory to store the file in
     * @return The path to the stored file
     * @throws IOException If an I/O error occurs
     */
    String storeFile(MultipartFile file, String directory) throws IOException;

    /**
     * Get the path to a file
     * @param filename The name of the file
     * @param directory The directory where the file is stored
     * @return The path to the file
     */
    Path getFilePath(String filename, String directory);

    /**
     * Delete a file from the file system
     * @param filename The name of the file to delete
     * @param directory The directory where the file is stored
     * @return true if the file was deleted successfully
     */
    boolean deleteFile(String filename, String directory);
}