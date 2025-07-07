package vn.kyler.job_hunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.kyler.job_hunter.domain.Company;
import vn.kyler.job_hunter.domain.Job;
import vn.kyler.job_hunter.domain.Skill;
import vn.kyler.job_hunter.domain.response.ResJobDTO;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.repository.JobRepository;
import vn.kyler.job_hunter.service.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillService skillService;
    private final CompanyService companyService;

    public JobService(JobRepository jobRepository, SkillService skillService, CompanyService companyService) {
        this.jobRepository = jobRepository;
        this.skillService = skillService;
        this.companyService = companyService;
    }

    public Job handleCreateJob(Job job) throws NotFoundException {
        if (job.getCompany() != null) {
            Company company = this.companyService.handleGetCompany(job.getCompany().getId());
            job.setCompany(company);
        }
        if (job.getSkills() != null) {
            job.setSkills(
                    job.getSkills().stream()
                            .map(skill -> {
                                try {
                                    return this.skillService.handleGetSkillById(skill.getId());
                                } catch (NotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toList())
            );
        }
        return this.jobRepository.save(job);
    }

    public ResultPaginationDTO handleGetAllJob(Specification<Job> specification, Pageable pageable) {
        Page<Job> jobPage = jobRepository.findAll(specification, pageable);

        //set job response
        List<ResJobDTO> jobDTOs = jobPage.getContent().stream()
                .map(job -> {
                    ResJobDTO jobDTO = new ResJobDTO();
                    jobDTO.setId(job.getId());
                    jobDTO.setName(job.getName());
                    jobDTO.setLocation(job.getLocation());
                    jobDTO.setSalary(job.getSalary());
                    jobDTO.setQuantity(job.getQuantity());
                    jobDTO.setLevel(job.getLevel());
                    jobDTO.setDescription(job.getDescription());
                    jobDTO.setStartDate(job.getStartDate());
                    jobDTO.setEndDate(job.getEndDate());
                    jobDTO.setActive(job.isActive());
                    jobDTO.setCreatedAt(job.getCreatedAt());
                    jobDTO.setUpdatedAt(job.getUpdatedAt());
                    jobDTO.setCreatedBy(job.getCreatedBy());
                    jobDTO.setUpdatedBy(job.getUpdatedBy());

                    //set company
                    if (job.getCompany() != null) {
                        ResJobDTO.Company company = new ResJobDTO.Company();
                        company.setId(job.getCompany().getId());
                        company.setName(job.getCompany().getName());
                        jobDTO.setCompany(company);
                    }

                    //set skills
                    if (job.getSkills() != null) {
                        jobDTO.setSkills(
                                job.getSkills().stream()
                                        .map(skill -> {
                                            ResJobDTO.Skill skillDTO = new ResJobDTO.Skill();
                                            skillDTO.setId(skill.getId());
                                            skillDTO.setName(skill.getName());
                                            return skillDTO;
                                        }).collect(Collectors.toList())
                        );
                    }
                    return jobDTO;
                })
                .collect(Collectors.toList());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPageNumber(jobPage.getNumber() + 1);
        meta.setPageSize(jobPage.getSize());
        meta.setTotalPages(jobPage.getTotalPages());
        meta.setTotalElements(jobPage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(jobDTOs);
        return resultPaginationDTO;
    }

    public Job handleGetJobById(long id) throws NotFoundException {
        Optional<Job> jobOptional = this.jobRepository.findById(id);
        if (!jobOptional.isPresent()) {
            throw new NotFoundException("Job with id " + id + " not found");
        }
        return jobOptional.get();
    }

    public Job handleUpdateJob(Job job) throws NotFoundException {
        Optional<Job> jobOptional = this.jobRepository.findById(job.getId());
        if (!jobOptional.isPresent()) {
            throw new NotFoundException("Job with id " + job.getId() + " not found");
        }
        Job jobToUpdate = jobOptional.get();
        jobToUpdate.setName(job.getName());
        jobToUpdate.setLocation(job.getLocation());
        jobToUpdate.setSalary(job.getSalary());
        jobToUpdate.setQuantity(job.getQuantity());
        jobToUpdate.setLevel(job.getLevel());
        jobToUpdate.setDescription(job.getDescription());
        jobToUpdate.setStartDate(job.getStartDate());
        jobToUpdate.setEndDate(job.getEndDate());
        jobToUpdate.setActive(job.isActive());

        if (job.getCompany() != null) {
            Company company = this.companyService.handleGetCompany(job.getCompany().getId());
            jobToUpdate.setCompany(company);
        } else jobToUpdate.setCompany(null);

        if (job.getSkills() != null) {
            jobToUpdate.setSkills(
                    job.getSkills().stream()
                            .map(skill -> {
                                try {
                                    return this.skillService.handleGetSkillById(skill.getId());
                                } catch (NotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }).collect(Collectors.toList())
            );
        } else jobToUpdate.setSkills(null);

        return this.jobRepository.save(jobToUpdate);
    }

    public void handleDeleteJob(long id) throws NotFoundException {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (!jobOptional.isPresent()) {
            throw new NotFoundException("Job with id " + id + " not found");
        }
        jobRepository.deleteById(id);
    }

    public ResJobDTO handleConvertToJobDTO(Job job) {
        ResJobDTO jobDTO = new ResJobDTO();
        jobDTO.setId(job.getId());
        jobDTO.setName(job.getName());
        jobDTO.setLocation(job.getLocation());
        jobDTO.setSalary(job.getSalary());
        jobDTO.setQuantity(job.getQuantity());
        jobDTO.setLevel(job.getLevel());
        jobDTO.setDescription(job.getDescription());
        jobDTO.setStartDate(job.getStartDate());
        jobDTO.setEndDate(job.getEndDate());
        jobDTO.setActive(job.isActive());
        jobDTO.setCreatedAt(job.getCreatedAt());
        jobDTO.setUpdatedAt(job.getUpdatedAt());
        jobDTO.setCreatedBy(job.getCreatedBy());
        jobDTO.setUpdatedBy(job.getUpdatedBy());

        if (job.getCompany() != null) {
            ResJobDTO.Company company = new ResJobDTO.Company();
            company.setId(job.getCompany().getId());
            company.setName(job.getCompany().getName());
            jobDTO.setCompany(company);
        }
        if (job.getSkills() != null) {
            jobDTO.setSkills(
                    job.getSkills().stream()
                            .map(skill -> {
                                ResJobDTO.Skill skillDTO = new ResJobDTO.Skill();
                                skillDTO.setId(skill.getId());
                                skillDTO.setName(skill.getName());
                                return skillDTO;
                            }).collect(Collectors.toList())
            );
        }
        return jobDTO;
    }
}
