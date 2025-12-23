package com.elchibek.internshiptracker.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository repository;

    public Application create(Application application) {
        application.setId(null);
        return repository.save(application);
    }

    public Page<Application> list(ApplicationStatus status, String company, Pageable pageable) {
        boolean hasStatus = status != null;
        boolean hasCompany = company != null && !company.isBlank();

        if (hasStatus && hasCompany) {
            return repository.findByStatusAndCompanyContainingIgnoreCase(status, company, pageable);
        }
        if (hasStatus) {
            return repository.findByStatus(status, pageable);
        }
        if (hasCompany) {
            return repository.findByCompanyContainingIgnoreCase(company, pageable);
        }
        return repository.findAll(pageable);
    }
}
