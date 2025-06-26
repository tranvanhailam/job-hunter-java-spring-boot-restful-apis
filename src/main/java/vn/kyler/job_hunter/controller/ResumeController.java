package vn.kyler.job_hunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.kyler.job_hunter.domain.Resume;
import vn.kyler.job_hunter.domain.response.ResResumeDTO;
import vn.kyler.job_hunter.domain.response.RestResponse;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.service.ResumeService;
import vn.kyler.job_hunter.service.exception.NotFoundException;

@RestController
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/resumes")
    public ResponseEntity<ResResumeDTO> createResume(@Valid @RequestBody Resume resume) throws NotFoundException {
        this.resumeService.handleCreateResume(resume);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.handleConvertToResumeDTO(resume));
    }

    @GetMapping("/resumes/{id}")
    public ResponseEntity<ResResumeDTO> getResume(@PathVariable("id") long id) throws NotFoundException {
        Resume resume = this.resumeService.handleGetResumeById(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.handleConvertToResumeDTO(resume));
    }

    @GetMapping("/resumes")
    public ResponseEntity<ResultPaginationDTO> getAllResume(Pageable pageable, @Filter Specification<Resume> specification) {
        ResultPaginationDTO resultPaginationDTO = this.resumeService.handleGetAllResume(specification, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(resultPaginationDTO);
    }

    @PutMapping("/resumes")
    public ResponseEntity<ResResumeDTO> updateResume(@Valid @RequestBody Resume resume) throws NotFoundException {
        Resume resumeUpdated = this.resumeService.handleUpdateResume(resume);
        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.handleConvertToResumeDTO(resumeUpdated));
    }

    @DeleteMapping("/resumes/{id}")
    public ResponseEntity<?> deleteResume(@PathVariable("id") long id) throws NotFoundException {
        this.resumeService.handleDeleteResume(id);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.OK.value());
        restResponse.setMessage("Delete resume successfully!");
        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }
}
