package com.meshgroup.task.controller;

import com.meshgroup.task.dto.PhoneDto;
import com.meshgroup.task.dto.UserDto;
import com.meshgroup.task.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Tag(name = "User Controller")
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return List<UserDto>",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam int page, @RequestParam int size,
                                                  @RequestParam String key, @RequestParam String operation,
                                                  @RequestParam Object value) {
        return ResponseEntity.ok(userService.getUsers(page, size, key, operation, value));
    }

    @Operation(summary = "Get User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return UserDto",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @GetMapping("{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(summary = "Get Phones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return List<PhoneDto>",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @GetMapping("{userId}/phones")
    public ResponseEntity<List<PhoneDto>> getPhones(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getPhones(userId));
    }

    @Operation(summary = "Save User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Return UserDto",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> saveUser(@RequestBody @Validated UserDto userDto) {
        userDto = userService.saveUser(userDto);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(userDto.getId()).toUri()).body(userDto);
    }

    @Operation(summary = "Save Phones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Return List<PhoneDto>",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PostMapping("{userId}/phones")
    @PreAuthorize("@userSecurity.hasUserId(authentication, #userId)")
    public ResponseEntity<List<PhoneDto>> savePhones(@PathVariable Long userId,
                                                     @RequestBody @Validated List<PhoneDto> phoneDtos) {
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(userId).toUri()).body(userService.savePhones(userId, phoneDtos));
    }

    @Operation(summary = "Update User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Return UserDto",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PutMapping("{userId}")
    @PreAuthorize("@userSecurity.hasUserId(authentication, #userId)")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody @Validated UserDto userDto) {
        return ResponseEntity.accepted().body(userService.updateUser(userId, userDto));
    }

    @Operation(summary = "Update User password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Return HttpStatus",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatus.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PutMapping("{userId}/password")
    @PreAuthorize("@userSecurity.hasUserId(authentication, #userId) or hasAuthority('ADMIN')")
    public ResponseEntity<HttpStatus> updateUser(@PathVariable Long userId, @RequestBody @NotBlank String password) {
        userService.updatePassword(userId, password);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Update Phone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Return PhoneDto",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PhoneDto.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PutMapping("{userId}/phones/{phoneId}")
    @PreAuthorize("@userSecurity.hasUserId(authentication, #userId)")
    public ResponseEntity<PhoneDto> updatePhone(@PathVariable Long userId, @PathVariable Long phoneId,
                                                @RequestBody @Validated PhoneDto phoneDto) {
        return ResponseEntity.accepted().body(userService.updatePhone(userId, phoneId, phoneDto));
    }

    @Operation(summary = "Delete User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Return HttpStatus",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatus.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @DeleteMapping("{userId}")
    @PreAuthorize("@userSecurity.hasUserId(authentication, #userId) or hasAuthority('ADMIN')")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete Phone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Return HttpStatus",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatus.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @DeleteMapping("{userId}/phones/{phoneId}")
    @PreAuthorize("@userSecurity.hasUserId(authentication, #userId)")
    public ResponseEntity<HttpStatus> deletePhone(@PathVariable Long userId, @PathVariable Long phoneId) {
        userService.deletePhone(userId, phoneId);
        return ResponseEntity.noContent().build();
    }

}
