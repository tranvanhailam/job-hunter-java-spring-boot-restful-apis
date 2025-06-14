package vn.kyler.job_hunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.kyler.job_hunter.domain.Company;
import vn.kyler.job_hunter.domain.Skill;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.repository.SkillRepository;
import vn.kyler.job_hunter.service.exception.ExistsException;
import vn.kyler.job_hunter.service.exception.NotFoundException;

import java.util.Optional;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleGetSkillById(long id) throws NotFoundException {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if (!skillOptional.isPresent()) {
            throw new NotFoundException("Skill with id " + id + " not found");
        }
        return skillOptional.get();
    }

    public Skill handleCreateSkill(Skill skill) throws ExistsException {
        Optional<Skill> skillOptional = this.skillRepository.findByNameIgnoreCase(skill.getName());
        if (skillOptional.isPresent()) {
            throw new ExistsException("Skill name already exists");
        }
        return this.skillRepository.save(skill);
    }

    //
    public void handleDeleteSkill(long id) throws NotFoundException {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if (!skillOptional.isPresent()) {
            throw new NotFoundException("Skill not found");
        }
        this.skillRepository.delete(skillOptional.get());
    }

    public Skill handleUpdateSkill(Skill skill) throws NotFoundException, ExistsException {
        Optional<Skill> skillOptional = this.skillRepository.findById(skill.getId());
        if (!skillOptional.isPresent()) {
            throw new NotFoundException("Skill with id " + skill.getId() + " not found");
        }
        Optional<Skill> skillOptionalCheckNameExists = this.skillRepository.findByNameIgnoreCase(skill.getName());
        if (skillOptionalCheckNameExists.isPresent()) {
            throw new ExistsException("Skill name already exists");
        }
        return this.skillRepository.save(skill);
    }

    public ResultPaginationDTO handleGetAllSkills(Specification<Skill> specification, Pageable pageable) {
        Page<Skill> skillPage = this.skillRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPageNumber(skillPage.getNumber() + 1);
        meta.setPageSize(skillPage.getSize());
        meta.setTotalPages(skillPage.getTotalPages());
        meta.setTotalElements(skillPage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(skillPage.getContent());
        return resultPaginationDTO;
    }
}
