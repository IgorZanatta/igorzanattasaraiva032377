package br.gov.mt.seplag.igorzannattasaraiva032377.service.user;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AppUserService {

    void recordLogin(UUID id, LocalDateTime loginAt);
}