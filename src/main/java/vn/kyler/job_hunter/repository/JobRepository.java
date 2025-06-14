package vn.kyler.job_hunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.kyler.job_hunter.domain.Job;

public interface JobRepository extends JpaRepository<Job,Long> , JpaSpecificationExecutor<Job> {
}
