package vn.kyler.job_hunter.repository;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.kyler.job_hunter.domain.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>,JpaSpecificationExecutor<Company> {
}
