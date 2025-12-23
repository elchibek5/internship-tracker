package com.elchibek.internshiptracker.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

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

    @GetMapping("/{id}")
    public Application getOne(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Application update(@PathVariable Long id, @Valid @RequestBody Application application) {
        return service.update(id, application);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

}
