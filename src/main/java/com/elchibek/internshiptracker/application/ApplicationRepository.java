package com.elchibek.internshiptracker.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);

    Page<Application> findByCompanyContainingIgnoreCase(String company, Pageable pageable);

    Page<Application> findByStatusAndCompanyContainingIgnoreCase(ApplicationStatus status, String company, Pageable pageable);
}
