package com.elchibek.internshiptracker.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Application create(@Valid @RequestBody Application application) {
        return service.create(application);
    }

    @GetMapping
    public Page<Application> list(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String company,
            Pageable pageable
    ) {
        return service.list(status, company, pageable);
    }

}
