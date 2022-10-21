package com.meshgroup.task.controller;

import com.meshgroup.task.authencation.TokenResponse;
import com.meshgroup.task.authencation.TokenService;
import com.meshgroup.task.controller.request.EmailRequest;
import com.meshgroup.task.controller.request.PhoneRequest;
import com.meshgroup.task.controller.request.UserRequest;
import com.meshgroup.task.dto.UserDto;
import com.meshgroup.task.exception.TokenValidationException;
import com.meshgroup.task.service.UserService;
import com.meshgroup.task.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@WithMockUser
class AuthenticationControllerTests extends BasicControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserCache userCache;

    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
    }

    @Test
    void testSignUp() throws Exception {
        UserRequest userRequest = getObjectFromJson("json/user_request.json", UserRequest.class);
        UserDto userDto = getObjectFromJson("json/user_dto.json", UserDto.class);
        TokenResponse tokenResponse = getObjectFromJson("json/token_response.json", TokenResponse.class);

        given(userService.saveUser(any(UserRequest.class))).willReturn(userDto);
        given(tokenService.createTokenResponse(anyLong())).willReturn(tokenResponse);

        mockMvc.perform(post("/auth/sign/up")
                        .with(csrf())
                        .content(asJsonString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type", is(tokenResponse.getTokenType())))
                .andExpect(jsonPath("$.access_token", is(tokenResponse.getAccessToken())))
                .andExpect(jsonPath("$.expires_in").value(tokenResponse.getExpiresIn()))
                .andExpect(jsonPath("$.refresh_token", is(tokenResponse.getRefreshToken())));

        verify(userService).saveUser(any(UserRequest.class));
        verify(tokenService).createTokenResponse(anyLong());
    }

    @Test
    void testPhoneSignIn() throws Exception {
        PhoneRequest phoneRequest = getObjectFromJson("json/phone_request.json", PhoneRequest.class);
        UserDto userDto = getObjectFromJson("json/user_dto.json", UserDto.class);
        TokenResponse tokenResponse = getObjectFromJson("json/token_response.json", TokenResponse.class);

        given(userService.getUserByPhone(anyString())).willReturn(userDto);
        willDoNothing().given(userCache).removeUserFromCache(isA(String.class));
        given(tokenService.createTokenResponse(anyLong())).willReturn(tokenResponse);

        mockMvc.perform(post("/auth/sign/in/phone")
                        .with(csrf())
                        .content(asJsonString(phoneRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type", is(tokenResponse.getTokenType())))
                .andExpect(jsonPath("$.access_token", is(tokenResponse.getAccessToken())))
                .andExpect(jsonPath("$.expires_in").value(tokenResponse.getExpiresIn()))
                .andExpect(jsonPath("$.refresh_token", is(tokenResponse.getRefreshToken())));

        verify(userService).getUserByPhone(anyString());
        verify(userCache).removeUserFromCache(anyString());
        verify(tokenService).createTokenResponse(anyLong());
    }

    @Test
    void testEmailSignIn() throws Exception {
        EmailRequest emailRequest = getObjectFromJson("json/email_request.json", EmailRequest.class);
        UserDto userDto = getObjectFromJson("json/user_dto.json", UserDto.class);
        TokenResponse tokenResponse = getObjectFromJson("json/token_response.json", TokenResponse.class);

        given(userService.getUserByEmail(anyString())).willReturn(userDto);
        willDoNothing().given(userCache).removeUserFromCache(isA(String.class));
        given(tokenService.createTokenResponse(anyLong())).willReturn(tokenResponse);

        mockMvc.perform(post("/auth/sign/in/email")
                        .with(csrf())
                        .content(asJsonString(emailRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type", is(tokenResponse.getTokenType())))
                .andExpect(jsonPath("$.access_token", is(tokenResponse.getAccessToken())))
                .andExpect(jsonPath("$.expires_in").value(tokenResponse.getExpiresIn()))
                .andExpect(jsonPath("$.refresh_token", is(tokenResponse.getRefreshToken())));

        verify(userService).getUserByEmail(anyString());
        verify(userCache).removeUserFromCache(anyString());
        verify(tokenService).createTokenResponse(anyLong());
    }

    @Test
    void testSignOut() throws Exception {
        willDoNothing().given(userCache).removeUserFromCache(isA(String.class));

        mockMvc.perform(post("/auth/sign/out")
                        .with(csrf())
                        .content(asJsonString(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userCache).removeUserFromCache(anyString());
    }

    @Test
    void testRefreshTokens() throws Exception {
        TokenResponse tokenResponse = getObjectFromJson("json/token_response.json", TokenResponse.class);

        given(tokenService.refreshTokens(anyString())).willReturn(tokenResponse);

        mockMvc.perform(get("/auth/" + Constants.REFRESH_TOKEN)
                        .headers(httpHeaders())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(request -> {
                            request.setAttribute(Constants.TOKEN, UUID.randomUUID().toString());
                            return request;
                        }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type", is(tokenResponse.getTokenType())))
                .andExpect(jsonPath("$.access_token", is(tokenResponse.getAccessToken())))
                .andExpect(jsonPath("$.expires_in").value(tokenResponse.getExpiresIn()))
                .andExpect(jsonPath("$.refresh_token", is(tokenResponse.getRefreshToken())));

        verify(tokenService).refreshTokens(anyString());
    }

    @Test
    void testHandleRuntimeExceptions() throws Exception {
        given(tokenService.refreshTokens(anyString())).willThrow(new BadCredentialsException(""),
                new TokenValidationException(""));

        mockMvc.perform(get("/auth/" + Constants.REFRESH_TOKEN)
                        .headers(httpHeaders())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(request -> {
                            request.setAttribute(Constants.TOKEN, UUID.randomUUID().toString());
                            return request;
                        }))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(tokenService).refreshTokens(anyString());
    }

}
