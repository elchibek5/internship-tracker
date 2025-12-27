package com.elchibek.internshiptracker.application;

import com.elchibek.internshiptracker.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByIdAndUser(Long id, User user);

    Page<Application> findByUser(User user, Pageable pageable);

    Page<Application> findByUserAndStatus(User user, ApplicationStatus status, Pageable pageable);

    Page<Application> findByUserAndCompanyContainingIgnoreCase(User user, String company, Pageable pageable);

    Page<Application> findByUserAndStatusAndCompanyContainingIgnoreCase(
            User user,
            ApplicationStatus status,
            String company,
            Pageable pageable
    );

    void deleteByIdAndUser(Long id, User user);
}
