package com.meshgroup.task.authencation;

import com.meshgroup.task.AbstractTests;
import com.meshgroup.task.MeshGroupTaskApplication;
import com.meshgroup.task.entity.RefreshToken;
import com.meshgroup.task.repository.RefreshTokenRepository;
import com.meshgroup.task.util.Constants;
import com.meshgroup.task.util.WithCustomUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MeshGroupTaskApplication.class)
@SqlGroup({
        @Sql(scripts = "classpath:sql/insert_user.sql"),
        @Sql(scripts = "classpath:sql/delete_all_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)})
class TokenServiceIntegrationTests extends AbstractTests {

    @Autowired
    private TokenServiceImpl tokenService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void testCreateTokenResponse() {
        TokenResponse tokenResponse = tokenService.createTokenResponse(1L);

        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getTokenType());
        assertNotNull(tokenResponse.getAccessToken());
        assertNotNull(tokenResponse.getExpiresIn());
        assertNotNull(tokenResponse.getRefreshToken());
        assertEquals(Constants.BEARER, tokenResponse.getTokenType());
    }

    @Test
    @WithCustomUserDetails
    void testRefreshTokens() throws Exception {
        RefreshToken refreshToken = getObjectFromJson("json/refresh_token.json", RefreshToken.class);
        refreshToken = refreshTokenRepository.save(refreshToken);

        TokenResponse tokenResponse = tokenService.refreshTokens(refreshToken.getToken());

        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getAccessToken());
        assertNotNull(tokenResponse.getExpiresIn());
        assertNotNull(tokenResponse.getRefreshToken());
        assertEquals(Constants.BEARER, tokenResponse.getTokenType());
        assertNotEquals(refreshToken.getToken(), tokenResponse.getRefreshToken());
    }

    @Test
    void testDeleteRefreshTokensByExpireDate() throws Exception {
        RefreshToken refreshToken = getObjectFromJson("json/refresh_token.json", RefreshToken.class);
        refreshTokenRepository.save(refreshToken);

        tokenService.deleteRefreshTokensByExpireDate();
        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(refreshToken.getToken());

        assertTrue(tokenOptional.isEmpty());
    }

}
