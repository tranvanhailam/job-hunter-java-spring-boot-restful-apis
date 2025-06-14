package vn.kyler.job_hunter.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.kyler.job_hunter.service.exception.StorageException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@Service
public class FileService {

    public void createDirectory(String baseUri_folder) throws URISyntaxException {
        URI uri = new URI(baseUri_folder);
        Path path = Paths.get(uri);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
                System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(">>> SKIP MAKING DIRECTORY, ALREADY EXISTS");
        }
    }

    public String storeFile(MultipartFile file, String baseUri_folder) throws URISyntaxException, IOException {
        // create unique filename
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        URI uri = new URI(baseUri_folder + "/" + fileName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path,
                    StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
    }

    public void handleValidate(MultipartFile file) throws StorageException {
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValidExtension = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValidExtension) {
            throw new StorageException("Invalid file extension, only support pdf, jpg, jpeg, doc, docx");
        }
    }

    public void createDirectoryInProject(String baseUri_folder) {
        // Tạo đường dẫn thư mục tuyệt đối dựa trên thư mục gốc của project
        Path path = Paths.get(System.getProperty("user.dir"), baseUri_folder);

        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path); // tạo cả thư mục cha nếu chưa có
                System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + path);
            } else {
                System.out.println(">>> SKIP MAKING DIRECTORY, ALREADY EXISTS: " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String storeFileInProject(MultipartFile file, String baseUri_folder) throws IOException {
        // Tạo tên file duy nhất
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        // Đường dẫn thư mục tuyệt đối
        Path folderPath = Paths.get(System.getProperty("user.dir"), baseUri_folder);
        // Đường dẫn tới file đầy đủ
        Path filePath = folderPath.resolve(fileName);
        // Lưu file
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return fileName;
    }

    public long validateAndGetFileLengthWhenDownload(String baseUri_folder, String fileName) throws URISyntaxException {
        URI uri = new URI(baseUri_folder + "/" + fileName);
        System.out.println(uri);
        Path path = Paths.get(uri);
        if (!Files.exists(path) || Files.isDirectory(path)) return 0;
        return path.toFile().length();
    }

    public InputStreamResource getInputStreamResource(String baseUri_folder, String fileName) throws URISyntaxException, FileNotFoundException {
        URI uri = new URI(baseUri_folder + "/" + fileName);
        Path path = Paths.get(uri);
        File file = new File(path.toString());
        return new InputStreamResource(new FileInputStream(file));
    }


}
