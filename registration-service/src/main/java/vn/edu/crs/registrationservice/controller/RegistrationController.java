package vn.edu.crs.registrationservice.controller;

import vn.edu.crs.registrationservice.dto.RegistrationRequestDTO;
import vn.edu.crs.registrationservice.entity.Registration;
import vn.edu.crs.registrationservice.security.AuthUserPrincipal;
import vn.edu.crs.registrationservice.service.RegistrationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @GetMapping("/my")
    public List<Registration> getMyRegistrations(
            @AuthenticationPrincipal AuthUserPrincipal principal
    ) {
        return registrationService.getByStudentId(principal.userId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Registration register(
            @Valid @RequestBody RegistrationRequestDTO dto,
            @AuthenticationPrincipal AuthUserPrincipal principal
    ) {
        if (!principal.userId().equals(dto.getStudentId())) {
            throw new AccessDeniedException("Ban khong the dang ky cho tai khoan khac");
        }
        return registrationService.register(dto);
    }

    @DeleteMapping("/{id}")
    public void cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthUserPrincipal principal
    ) {
        registrationService.cancel(id, principal.userId());
    }
}