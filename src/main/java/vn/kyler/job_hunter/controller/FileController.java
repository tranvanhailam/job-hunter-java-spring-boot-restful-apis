package vn.kyler.job_hunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.kyler.job_hunter.domain.response.ResUploadFileDTO;
import vn.kyler.job_hunter.service.FileService;
import vn.kyler.job_hunter.service.exception.StorageException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;

@RestController
public class FileController {
    private final FileService fileService;

    @Value("${job-hunter.upload-file.base-uri}")
    private String baseUri;
    @Value("${job-hunter.upload-file.base-uri-in-project}")
    private String baseUriInProject;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/files")
    public ResponseEntity<Resource> downloadFile(@RequestParam(name = "fileName", required = false) String fileName, @RequestParam(name = "folder", required = false) String folder) throws StorageException, URISyntaxException, FileNotFoundException {
        if (fileName == null || folder == null) {
            throw new StorageException("Missing parameters fileName or folder");
        }
        long fileLength = this.fileService.validateAndGetFileLengthWhenDownload(baseUri + folder, fileName);
        if (fileLength == 0) {
            throw new StorageException("File not found");
        }
        InputStreamResource inputStreamResource = this.fileService.getInputStreamResource(baseUri + folder, fileName);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename= \"" + fileName)
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(inputStreamResource);
    }


    @PostMapping("/files")
    public ResponseEntity<ResUploadFileDTO> uploadFile(@RequestParam(name = "file", required = false) MultipartFile file, @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {
        // validate
        this.fileService.handleValidate(file);
        //Create a directory if not exists
        this.fileService.createDirectory(baseUri + folder);
        //Store file
        String fileName = this.fileService.storeFile(file, baseUri + folder);
        ResUploadFileDTO resUploadFileDTO = new ResUploadFileDTO(fileName, Instant.now());
        return ResponseEntity.status(HttpStatus.OK).body(resUploadFileDTO);
    }

    @PostMapping("/files/project")
    public ResponseEntity<ResUploadFileDTO> uploadFileInProject(@RequestParam(name = "file", required = false) MultipartFile file, @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {
        // validate
        this.fileService.handleValidate(file);
        //Create a directory if not exists
        this.fileService.createDirectoryInProject(baseUriInProject + folder);
        //Store file
        String fileName = this.fileService.storeFileInProject(file, baseUriInProject + folder);
        ResUploadFileDTO resUploadFileDTO = new ResUploadFileDTO(fileName, Instant.now());
        System.out.println(System.getProperty("user.dir"));
        return ResponseEntity.status(HttpStatus.OK).body(resUploadFileDTO);
    }
}
