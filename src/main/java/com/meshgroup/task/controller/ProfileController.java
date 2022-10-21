package com.meshgroup.task.controller;

import com.meshgroup.task.dto.ProfileDto;
import com.meshgroup.task.service.ProfileService;
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

import java.util.List;

@Tag(name = "Profile Controller")
@RestController
@RequestMapping("profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "Get Profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return List<ProfileDto>",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileDto.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @GetMapping
    public ResponseEntity<List<ProfileDto>> getProfile(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(profileService.getProfiles(page, size));
    }

    @Operation(summary = "Get Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return ProfileDto",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileDto.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @GetMapping("user/{userId}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @Operation(summary = "Save Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Return ProfileDto",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileDto.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PostMapping("user/{userId}")
    @PreAuthorize("@userSecurity.hasUserId(authentication, #userId)")
    public ResponseEntity<ProfileDto> saveProfile(@PathVariable Long userId,
                                                  @RequestBody @Validated ProfileDto profileDto) {
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(userId).toUri()).body(profileService.saveProfile(userId, profileDto));
    }

    @Operation(summary = "Update Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Return ProfileDto",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileDto.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PutMapping("{profileId}/user/{userId}")
    @PreAuthorize("@userSecurity.hasUserId(authentication, #userId)")
    public ResponseEntity<ProfileDto> updateProfile(@PathVariable Long userId, @PathVariable Long profileId,
                                                    @RequestBody @Validated ProfileDto profileDto) {
        return ResponseEntity.accepted().body(profileService.updateProfile(userId, profileId, profileDto));
    }

    @Operation(summary = "Delete Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Return HttpStatus",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatus.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @DeleteMapping("{profileId}/user/{userId}")
    @PreAuthorize("@userSecurity.hasUserId(authentication, #userId)")
    public ResponseEntity<HttpStatus> deleteProfile(@PathVariable Long userId, @PathVariable Long profileId) {
        profileService.deleteProfile(userId, profileId);
        return ResponseEntity.noContent().build();
    }

}
