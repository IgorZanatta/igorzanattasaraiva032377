package br.gov.mt.seplag.igorzannattasaraiva032377.service.user;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.user.AppUserEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.user.AppUserRepository;

@ExtendWith(MockitoExtension.class)
class UserDetailServiceImplTest {

    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @Test
    void loadUserByUsername_shouldNormalizeAndReturnUserDetails() {
        AppUserEntity entity = new AppUserEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("User");
        entity.setEmail("user@example.com");
        entity.setPasswordHash("hash");
        entity.setActive(true);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(entity));

        UserDetails details = userDetailService.loadUserByUsername(" USER@EXAMPLE.COM ");

        assertNotNull(details);
        assertEquals("user@example.com", details.getUsername());
        assertEquals("hash", details.getPassword());
    }

    @Test
    void loadUserByUsername_shouldThrowWhenNotFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailService.loadUserByUsername("user@example.com"));
    }
}
