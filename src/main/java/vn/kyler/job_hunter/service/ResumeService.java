package vn.kyler.job_hunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.kyler.job_hunter.domain.Job;
import vn.kyler.job_hunter.domain.Resume;
import vn.kyler.job_hunter.domain.User;
import vn.kyler.job_hunter.domain.response.ResJobDTO;
import vn.kyler.job_hunter.domain.response.ResResumeDTO;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.repository.ResumeRepository;
import vn.kyler.job_hunter.service.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserService userService;
    private final JobService jobService;

    public ResumeService(ResumeRepository resumeRepository, UserService userService, JobService jobService) {
        this.resumeRepository = resumeRepository;
        this.userService = userService;
        this.jobService = jobService;
    }

    public Resume handleCreateResume(Resume resume) throws NotFoundException {
        if (resume.getUser() != null) {
            User user = this.userService.handleGetUser(resume.getUser().getId());
            resume.setUser(user);
        }
        if (resume.getJob() != null) {
            Job job = this.jobService.handleGetJobById(resume.getJob().getId());
            resume.setJob(job);
        }
        return this.resumeRepository.save(resume);
    }

    public ResultPaginationDTO handleGetAllResume(Specification<Resume> specification, Pageable pageable) {
        Page<Resume> resumePage = resumeRepository.findAll(specification, pageable);

        //set job response
        List<ResResumeDTO> resResumeDTOS = resumePage.getContent().stream()
                .map(resume -> {
                    ResResumeDTO resumeDTO = new ResResumeDTO();
                    resumeDTO.setId(resume.getId());
                    resumeDTO.setEmail(resume.getEmail());
                    resumeDTO.setUrl(resume.getUrl());
                    resumeDTO.setStatus(resume.getStatus());
                    resumeDTO.setCreatedAt(resume.getCreatedAt());
                    resumeDTO.setUpdatedAt(resume.getUpdatedAt());
                    resumeDTO.setCreatedBy(resume.getCreatedBy());
                    resumeDTO.setUpdatedBy(resume.getUpdatedBy());

                    if (resume.getUser() != null) {
                        ResResumeDTO.User user = new ResResumeDTO.User();
                        user.setId(resume.getUser().getId());
                        user.setName(resume.getUser().getName());
                        resumeDTO.setUser(user);
                    }

                    if (resume.getJob() != null) {
                        ResResumeDTO.Job job = new ResResumeDTO.Job();
                        job.setId(resume.getJob().getId());
                        job.setName(resume.getJob().getName());
                        resumeDTO.setJob(job);
                    }
                    return resumeDTO;
                })
                .collect(Collectors.toList());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPageNumber(resumePage.getNumber() + 1);
        meta.setPageSize(resumePage.getSize());
        meta.setTotalPages(resumePage.getTotalPages());
        meta.setTotalElements(resumePage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(resResumeDTOS);
        return resultPaginationDTO;
    }

    public Resume handleGetResumeById(long id) throws NotFoundException {
        Optional<Resume> resumeOptional = this.resumeRepository.findById(id);
        if (!resumeOptional.isPresent()) {
            throw new NotFoundException("Resume with id " + id + " not found");
        }
        return resumeOptional.get();
    }

    public Resume handleUpdateResume(Resume resume) throws NotFoundException {
        Optional<Resume> resumeOptional = this.resumeRepository.findById(resume.getId());
        if (!resumeOptional.isPresent()) {
            throw new NotFoundException("Resume with id " + resume.getId() + " not found");
        }
        Resume resumeToUpdate = resumeOptional.get();
        resumeToUpdate.setEmail(resume.getEmail());
        resumeToUpdate.setUrl(resume.getUrl());
        resumeToUpdate.setStatus(resume.getStatus());

        if (resume.getUser() != null) {
            User user = this.userService.handleGetUser(resume.getUser().getId());
            resumeToUpdate.setUser(user);
        } else resumeToUpdate.setUser(null);

        if (resume.getJob() != null) {
            Job job = this.jobService.handleGetJobById(resume.getJob().getId());
            resumeToUpdate.setJob(job);
        } else resumeToUpdate.setJob(null);
        Resume updatedResume = this.resumeRepository.save(resumeToUpdate);
        return updatedResume;
    }

    public void handleDeleteResume(long id) throws NotFoundException {
        Optional<Resume> resume = this.resumeRepository.findById(id);
        if (!resume.isPresent()) {
            throw new NotFoundException("Resume with id " + id + " not found");
        }
        this.resumeRepository.deleteById(id);
    }

    public ResResumeDTO handleConvertToResumeDTO(Resume resume) {
        ResResumeDTO resumeDTO = new ResResumeDTO();
        resumeDTO.setId(resume.getId());
        resumeDTO.setEmail(resume.getEmail());
        resumeDTO.setUrl(resume.getUrl());
        resumeDTO.setStatus(resume.getStatus());
        resumeDTO.setCreatedAt(resume.getCreatedAt());
        resumeDTO.setUpdatedAt(resume.getUpdatedAt());
        resumeDTO.setCreatedBy(resume.getCreatedBy());
        resumeDTO.setUpdatedBy(resume.getUpdatedBy());

        if (resume.getUser() != null) {
            ResResumeDTO.User user = new ResResumeDTO.User();
            user.setId(resume.getUser().getId());
            user.setName(resume.getUser().getName());
            resumeDTO.setUser(user);
        }

        if (resume.getJob() != null) {
            ResResumeDTO.Job job = new ResResumeDTO.Job();
            job.setId(resume.getJob().getId());
            job.setName(resume.getJob().getName());
            resumeDTO.setJob(job);
        }
        return resumeDTO;
    }
}
