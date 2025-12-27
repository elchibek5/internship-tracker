package com.elchibek.internshiptracker.application.dto;

import com.elchibek.internshiptracker.application.Application;
import com.elchibek.internshiptracker.application.ApplicationStatus;

import java.time.LocalDate;

public record ApplicationResponse(
        Long id,
        String company,
        String roleTitle,
        String location,
        ApplicationStatus status,
        LocalDate appliedDate,
        String note
) {}