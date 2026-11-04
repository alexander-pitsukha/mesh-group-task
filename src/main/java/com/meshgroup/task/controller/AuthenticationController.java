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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

@Tag(name = "Authentication Controller")
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
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
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                phoneRequest.getValue(), phoneRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
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
    public ResponseEntity<TokenResponse> signIn(@RequestBody @Validated EmailRequest emailRequest) {
        var userDto = userService.getUserByEmail(emailRequest.getEmail());
        userCache.removeUserFromCache(userDto.getEmail());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                emailRequest.getEmail(), emailRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
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
