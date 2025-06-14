package vn.kyler.job_hunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.kyler.job_hunter.domain.Company;
import vn.kyler.job_hunter.domain.User;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.repository.CompanyRepository;
import vn.kyler.job_hunter.repository.UserRepository;
import vn.kyler.job_hunter.service.exception.NotFoundException;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public List<Company> handleGetAllCompanies() {
        return this.companyRepository.findAll();
    }

    public ResultPaginationDTO handleGetAllCompanies(Specification<Company> specification, Pageable pageable) {
        Page<Company> companyPage = this.companyRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPageNumber(companyPage.getNumber() + 1);
        meta.setPageSize(companyPage.getSize());
        meta.setTotalPages(companyPage.getTotalPages());
        meta.setTotalElements(companyPage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(companyPage.getContent());
        return resultPaginationDTO;
    }

    public Company handleGetCompanyById(long id) throws NotFoundException {
        Optional<Company> company = this.companyRepository.findById(id);
        if (!company.isPresent()) {
            throw new NotFoundException("Company with id " + id + " not found");
        }
        return company.get();
    }

    public Company handleUpdateCompany(Company company) throws NotFoundException {
        Optional<Company> companyOptional = this.companyRepository.findById(company.getId());
        if (companyOptional.isPresent()) {
            Company companyToUpdate = companyOptional.get();
            companyToUpdate.setName(company.getName());
            companyToUpdate.setDescription(company.getDescription());
            companyToUpdate.setAddress(company.getAddress());
            companyToUpdate.setLogo(company.getLogo());
            return this.companyRepository.save(companyToUpdate);
        } else {
            throw new NotFoundException("Company with id " + company.getId() + " not found");
        }
    }

    public void handleDeleteCompany(long id) throws NotFoundException {
        if (!this.companyRepository.existsById(id)) {
            throw new NotFoundException("Company with id " + id + " not found");
        }
        List<User> users = this.userRepository.findAllByCompany_Id(id);
        for (User user : users) {
            user.setCompany(null);
        }
        this.userRepository.saveAll(users);
        this.companyRepository.deleteById(id);
    }
}
