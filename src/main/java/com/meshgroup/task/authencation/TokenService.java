package com.meshgroup.task.authencation;

public interface TokenService {

    TokenResponse createTokenResponse(Long userId);

    TokenResponse refreshTokens(String token);

}
