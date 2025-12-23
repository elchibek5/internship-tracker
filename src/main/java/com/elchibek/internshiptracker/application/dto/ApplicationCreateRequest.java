package com.elchibek.internshiptracker.application.dto;

import com.elchibek.internshiptracker.application.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ApplicationCreateRequest(
        @NotBlank String company,
        @NotBlank String roleTitle,
        String location,
        @NotNull ApplicationStatus status,
        LocalDate appliedDate,
        String notes
) {}

