package vn.kyler.job_hunter.controller;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.kyler.job_hunter.domain.Company;
import vn.kyler.job_hunter.domain.response.RestResponse;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.service.CompanyService;
import vn.kyler.job_hunter.service.exception.NotFoundException;
import vn.kyler.job_hunter.util.annotation.ApiMessage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PutMapping;

import java.time.Duration;

@RestController
public class CompanyController {
    private final CompanyService companyService;
    private final Bucket bucket;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
        Refill refill = Refill.intervally(60, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(60, refill);
        Bucket bucket = Bucket.builder().addLimit(limit).build();
        this.bucket = bucket;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        Company companyCreated = this.companyService.handleCreateCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(companyCreated);
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch all companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            Pageable pageable,
            @Filter Specification<Company> specification) {
        if (this.bucket.tryConsume(1)) {
            ResultPaginationDTO resultPaginationDTO = this.companyService.handleGetAllCompanies(specification, pageable);
            return ResponseEntity.status(HttpStatus.OK).body(resultPaginationDTO);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompany(@PathVariable("id") long id) throws NotFoundException {
        Company company = this.companyService.handleGetCompanyById(id);
        return ResponseEntity.status(HttpStatus.OK).body(company);
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) throws NotFoundException {
        Company companyUpdated = this.companyService.handleUpdateCompany(company);
        return ResponseEntity.status(HttpStatus.OK).body(companyUpdated);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable("id") long id) throws NotFoundException {
        this.companyService.handleDeleteCompany(id);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.OK.value());
        restResponse.setMessage("Delete company successfully!");
        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }
}
