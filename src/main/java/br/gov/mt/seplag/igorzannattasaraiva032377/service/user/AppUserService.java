package br.gov.mt.seplag.igorzannattasaraiva032377.service.user;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.request.CreateAppUserRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.request.UpdateAppUserRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.request.UpdatePasswordRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.response.AppUserResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppUserService {

    AppUserResponse create(CreateAppUserRequest request);

    AppUserResponse findById(UUID id);

    AppUserResponse findByEmail(String email);

    List<AppUserResponse> listAll();

    AppUserResponse update(UUID id, UpdateAppUserRequest request);

    void updatePassword(UUID id, UpdatePasswordRequest request);

    void deactivate(UUID id);

    void recordLogin(UUID id, LocalDateTime loginAt);
}