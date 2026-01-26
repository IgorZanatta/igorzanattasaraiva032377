package br.gov.mt.seplag.igorzannattasaraiva032377.controller.user;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.request.CreateAppUserRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.request.UpdateAppUserRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.request.UpdatePasswordRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.response.AppUserResponse;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.AppUserService;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService service;

    @PostMapping
    public ResponseEntity<AppUserResponse> create(@Valid @RequestBody CreateAppUserRequest request) {
        var created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUserResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/by-email")
    public ResponseEntity<AppUserResponse> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.findByEmail(email));
    }

    @GetMapping("/me")
    public ResponseEntity<AppUserResponse> me(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(service.findById(user.getId()));
    }

    @GetMapping
    public ResponseEntity<List<AppUserResponse>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUserResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAppUserRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PutMapping("/me")
    public ResponseEntity<AppUserResponse> updateMe(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Valid @RequestBody UpdateAppUserRequest request
    ) {
        return ResponseEntity.ok(service.update(user.getId(), request));
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        service.updatePassword(id, request);
    }

    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePasswordMe(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        service.updatePassword(user.getId(), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID id) {
        service.deactivate(id);
    }
}
