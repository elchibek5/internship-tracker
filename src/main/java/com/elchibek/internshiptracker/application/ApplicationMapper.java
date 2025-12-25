package com.elchibek.internshiptracker.application;

import com.elchibek.internshiptracker.application.dto.ApplicationCreateRequest;
import com.elchibek.internshiptracker.application.dto.ApplicationResponse;
import com.elchibek.internshiptracker.application.dto.ApplicationUpdateRequest;

public class ApplicationMapper {
    public static Application toEntity(ApplicationCreateRequest req) {
        Application a = new Application();
        a.setCompany(req.company());;
        a.setRoleTitle(req.roleTitle());
        a.setLocation(req.location());
        a.setStatus(req.status());
        a.setAppliedDate(req.appliedDate());
        a.setNotes(req.notes());
        return a;
    }

    public static void applyUpdate(Application existing, ApplicationUpdateRequest req) {
        existing.setCompany(req.company());
        existing.setRoleTitle(req.roleTitle());
        existing.setLocation(req.location());
        existing.setStatus(req.status());
        existing.setAppliedDate(req.appliedDate());
        existing.setNotes(req.notes());
    }

    public static ApplicationResponse toResponse(Application a) {
        return new ApplicationResponse(
                a.getId(),
                a.getCompany(),
                a.getRoleTitle(),
                a.getLocation(),
                a.getStatus(),
                a.getAppliedDate(),
                a.getNotes()
        );
    }
}
