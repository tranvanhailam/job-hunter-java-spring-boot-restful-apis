package vn.kyler.job_hunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.kyler.job_hunter.domain.Job;
import vn.kyler.job_hunter.domain.Skill;

import java.util.List;

public interface JobRepository extends JpaRepository<Job,Long> , JpaSpecificationExecutor<Job> {
    List<Job> findBySkillsIn(List<Skill> skills);
}
