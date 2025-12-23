package com.elchibek.internshiptracker.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Objects;

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

    public Application getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found: " + id));
    }

    public Application update(Long id, Application updated) {
        Application existing = getById(id);

        existing.setCompany(updated.getCompany());
        existing.setRoleTitle(updated.getRoleTitle());
        existing.setLocation(updated.getLocation());
        existing.setStatus(updated.getStatus());
        existing.setAppliedDate(updated.getAppliedDate());
        existing.setNotes(updated.getNotes());

        return repository.save(existing);
    }

    public void delete(Long id) {
        Application existing = getById(id);
        repository.delete(existing);
    }
}
