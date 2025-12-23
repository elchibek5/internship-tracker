package com.elchibek.internshiptracker.application;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor

public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String company;

    @NotBlank
    @Column(nullable = false)
    private String roleTitle;

    private String location;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    private LocalDate appliedDate;

    @Column(length = 2000)
    private String notes;
}
