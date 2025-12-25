package com.elchibek.internshiptracker.application;

import com.elchibek.internshiptracker.application.dto.ApplicationCreateRequest;
import com.elchibek.internshiptracker.application.dto.ApplicationResponse;
import com.elchibek.internshiptracker.application.dto.ApplicationUpdateRequest;
import com.elchibek.internshiptracker.user.User;
import com.elchibek.internshiptracker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository repository;
    private final UserRepository userRepository;

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new NotFoundException("No authenticated user");
        }

        String email = auth.getPrincipal().toString();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));
    }

    public ApplicationResponse create(ApplicationCreateRequest request) {
        User user = currentUser();

        Application entity = ApplicationMapper.toEntity(request);
        entity.setId(null);
        entity.setUser(user);

        return ApplicationMapper.toResponse(repository.save(entity));
    }

    public Page<ApplicationResponse> list(ApplicationStatus status, String company, Pageable pageable) {
        User user = currentUser();

        boolean hasStatus = status != null;
        boolean hasCompany = company != null && !company.isBlank();

        Page<Application> page;
        if (hasStatus && hasCompany) {
            page = repository.findByUserAndStatusAndCompanyContainingIgnoreCase(user, status, company, pageable);
        } else if (hasStatus) {
            page = repository.findByUserAndStatus(user, status, pageable);
        } else if (hasCompany) {
            page = repository.findByUserAndCompanyContainingIgnoreCase(user, company, pageable);
        } else {
            page = repository.findByUser(user, pageable);
        }

        return page.map(ApplicationMapper::toResponse);
    }

    public ApplicationResponse getById(Long id) {
        User user = currentUser();

        Application entity = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Application not found: " + id));

        return ApplicationMapper.toResponse(entity);
    }

    public ApplicationResponse update(Long id, ApplicationUpdateRequest request) {
        User user = currentUser();

        Application existing = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Application not found: " + id));

        ApplicationMapper.applyUpdate(existing, request);

        return ApplicationMapper.toResponse(repository.save(existing));
    }

    public void delete(Long id) {
        User user = currentUser();

        // Option A: load then delete (gives clean 404 if not yours)
        Application existing = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Application not found: " + id));

        repository.delete(existing);

        // Option B: direct delete (faster but doesn't tell you if it existed)
        // repository.deleteByIdAndUser(id, user);
    }
}
