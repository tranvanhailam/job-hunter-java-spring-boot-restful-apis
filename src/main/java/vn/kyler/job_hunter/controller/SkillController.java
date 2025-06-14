package vn.kyler.job_hunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.kyler.job_hunter.domain.Company;
import vn.kyler.job_hunter.domain.Skill;
import vn.kyler.job_hunter.domain.response.RestResponse;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.service.SkillService;
import vn.kyler.job_hunter.service.exception.ExistsException;
import vn.kyler.job_hunter.service.exception.NotFoundException;

import java.util.Optional;

@RestController
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws ExistsException {
        Skill skillCreated = this.skillService.handleCreateSkill(skill);
        return ResponseEntity.status(HttpStatus.CREATED).body(skillCreated);
    }

    @GetMapping("/skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(Pageable pageable,
                                                            @Filter Specification<Skill> specification) {
        ResultPaginationDTO resultPaginationDTO = this.skillService.handleGetAllSkills(specification, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(resultPaginationDTO);
    }

    @PutMapping("/skills")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill) throws NotFoundException, ExistsException {
        Skill skillUpdated = this.skillService.handleUpdateSkill(skill);
        return ResponseEntity.status(HttpStatus.OK).body(skillUpdated);
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable("id") long id) throws NotFoundException {
        this.skillService.handleDeleteSkill(id);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.OK.value());
        restResponse.setMessage("Delete skill successfully!");
        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }


}
