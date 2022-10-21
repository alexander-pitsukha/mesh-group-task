package com.meshgroup.task.repositoty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshgroup.task.entity.RefreshToken;
import com.meshgroup.task.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class RefreshTokenRepositoryTests extends AbstractRepositoryTests {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFindByToken() throws IOException {
        RefreshToken refreshToken = saveRefreshToken();

        RefreshToken entity = refreshTokenRepository.findByToken(refreshToken.getToken()).orElseThrow();

        assertEntry(refreshToken, entity);
    }

    @Test
    void testFindByUserId() throws IOException {
        RefreshToken refreshToken = saveRefreshToken();

        RefreshToken entity = refreshTokenRepository.findByUserId(1L).orElseThrow();

        assertEntry(refreshToken, entity);
    }

    @Test
    void testDeleteByExpireDate() throws IOException {
        RefreshToken refreshToken = saveRefreshToken();

        refreshTokenRepository.deleteByExpireDate(LocalDateTime.now());
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByToken(refreshToken.getToken());

        assertTrue(optionalRefreshToken.isEmpty());
    }

    private RefreshToken saveRefreshToken() throws IOException {
        return saveTestEntity("json/refresh_token.json", RefreshToken.class);
    }

    private void assertEntry(RefreshToken refreshToken, RefreshToken entity) {
        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(refreshToken.getToken(), entity.getToken());
        assertEquals(refreshToken.getExpireDate(), entity.getExpireDate());
        assertEquals(refreshToken.getUser().getId(), entity.getUser().getId());
    }

}
