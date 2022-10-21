package com.meshgroup.task.controller;

import com.meshgroup.task.controller.request.PhoneRequest;
import com.meshgroup.task.controller.request.EmailRequest;
import com.meshgroup.task.authencation.TokenResponse;
import com.meshgroup.task.authencation.TokenService;
import com.meshgroup.task.controller.request.UserRequest;
import com.meshgroup.task.dto.UserDto;
import com.meshgroup.task.exception.TokenValidationException;
import com.meshgroup.task.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.meshgroup.task.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

@Tag(name = "Authentication Controller")
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final UserService userService;
    private final TokenService tokenService;
    private final UserCache userCache;

    @Operation(summary = "Sign up to Application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return access token and refresh token",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @PostMapping("sign/up")
    public ResponseEntity<TokenResponse> signUp(@RequestBody @Validated UserRequest userRequest) {
        UserDto userDto = userService.saveUser(userRequest);
        return ResponseEntity.ok(tokenService.createTokenResponse(userDto.getId()));
    }

    @Operation(summary = "Login by phone to Application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return access token and refresh token",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @PostMapping("sign/in/phone")
    public ResponseEntity<TokenResponse> signIn(@RequestBody @Validated PhoneRequest phoneRequest) {
        var userDto = userService.getUserByPhone(phoneRequest.getValue());
        userCache.removeUserFromCache(userDto.getName());
        return ResponseEntity.ok(tokenService.createTokenResponse(userDto.getId()));
    }

    @Operation(summary = "Login by Email to Application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return access token and refresh token",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content)})
    @PostMapping("sign/in/email")
    public ResponseEntity<TokenResponse> signIn(@RequestBody @Validated EmailRequest phoneRequest) {
        var userDto = userService.getUserByEmail(phoneRequest.getEmail());
        userCache.removeUserFromCache(userDto.getEmail());
        return ResponseEntity.ok(tokenService.createTokenResponse(userDto.getId()));
    }

    @PostMapping("sign/out")
    public ResponseEntity<HttpStatus> signOut(@RequestBody @NotBlank String username) {
        userCache.removeUserFromCache(username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Refresh tokens by refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return access token and refresh token",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Token expired", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @GetMapping(Constants.REFRESH_TOKEN)
    public ResponseEntity<TokenResponse> refreshTokens(HttpServletRequest request) {
        String refreshToken = (String) request.getAttribute(Constants.TOKEN);
        return ResponseEntity.ok(tokenService.refreshTokens(refreshToken));
    }

    @ExceptionHandler({BadCredentialsException.class, TokenValidationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> handleRuntimeExceptions(RuntimeException e) {
        log.info(e.getClass().getName() + " handler.");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

}
