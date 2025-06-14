package vn.kyler.job_hunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.kyler.job_hunter.domain.Job;
import vn.kyler.job_hunter.domain.User;
import vn.kyler.job_hunter.domain.response.ResJobDTO;
import vn.kyler.job_hunter.domain.response.RestResponse;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.service.JobService;
import vn.kyler.job_hunter.service.exception.NotFoundException;

@RestController
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    public ResponseEntity<ResJobDTO> createJob(@Valid @RequestBody Job job) throws NotFoundException {
        this.jobService.handleCreateJob(job);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleConvertToJobDTO(job));
    }

    @GetMapping("/jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJob(Pageable pageable, @Filter Specification<Job> specification) {
        ResultPaginationDTO resultPaginationDTO = this.jobService.handleGetAllJob(specification, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(resultPaginationDTO);
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<ResJobDTO> getJobById(@PathVariable long id) throws NotFoundException {
        Job job = this.jobService.handleGetJobById(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.handleConvertToJobDTO(job));
    }

    @PutMapping("/jobs")
    public ResponseEntity<ResJobDTO> updateJob(@Valid @RequestBody Job job) throws NotFoundException {
        Job jobUpdated = this.jobService.handleUpdateJob(job);
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.handleConvertToJobDTO(jobUpdated));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable("id") long id) throws NotFoundException {
        this.jobService.handleDeleteJob(id);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.OK.value());
        restResponse.setMessage("Delete job successfully!");
        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }
}
