package br.gov.mt.seplag.igorzannattasaraiva032377.mapper.user;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.response.AppUserResponse;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.user.AppUserEntity;
import org.springframework.stereotype.Component;

@Component
public class AppUserMapper {

    public AppUserResponse toResponse(AppUserEntity entity) {
        return new AppUserResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getLastLogin()
        );
    }
}