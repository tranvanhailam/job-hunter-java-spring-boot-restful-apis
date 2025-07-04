package vn.kyler.job_hunter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.kyler.job_hunter.domain.Company;
import vn.kyler.job_hunter.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    Optional<User> findByRefreshTokenAndEmail(String refreshToken, String email);

    Optional<User> findByCompany(Company company);
    List<User> findAllByCompany(Company company);
    List<User> findAllByCompany_Id(long companyId);
}
