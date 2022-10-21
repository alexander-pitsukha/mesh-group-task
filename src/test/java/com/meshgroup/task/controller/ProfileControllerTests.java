package com.meshgroup.task.controller;

import com.meshgroup.task.dto.ProfileDto;
import com.meshgroup.task.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@WithMockUser
class ProfileControllerTests extends BasicControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
    }

    @Test
    void testGetProfiles() throws Exception {
        ProfileDto profileDto = getObjectFromJson("json/profile.json", ProfileDto.class);

        given(profileService.getProfiles(anyInt(), anyInt())).willReturn(Collections.singletonList(profileDto));

        mockMvc.perform(get("/profiles")
                        .param("page", "1")
                        .param("size", "1")
                        .headers(httpHeaders())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));

        verify(profileService).getProfiles(anyInt(), anyInt());
    }

    @Test
    void testGetProfile() throws Exception {
        ProfileDto profileDto = getObjectFromJson("json/profile.json", ProfileDto.class);

        given(profileService.getProfile(anyLong())).willReturn(profileDto);

        mockMvc.perform(get("/profiles/user/{userId}", anyLong())
                        .headers(httpHeaders())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cash", is(profileDto.getCash().doubleValue())));

        verify(profileService).getProfile(anyLong());
    }

    @Test
    void testSaveProfile() throws Exception {
        ProfileDto profileDto = getObjectFromJson("json/profile_dto.json", ProfileDto.class);

        given(profileService.saveProfile(anyLong(), any(ProfileDto.class))).willReturn(profileDto);

        mockMvc.perform(post("/profiles/user/{userId}", profileDto.getUserId())
                        .with(csrf())
                        .headers(httpHeaders())
                        .content(asJsonString(profileDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("http://*/profiles/user/" + profileDto.getUserId()))
                .andExpect(jsonPath("$.cash", is(profileDto.getCash().doubleValue())));

        verify(profileService).saveProfile(anyLong(), any(ProfileDto.class));
    }

    @Test
    void testUpdateProfile() throws Exception {
        ProfileDto profileDto = getObjectFromJson("json/profile_dto.json", ProfileDto.class);

        given(profileService.updateProfile(anyLong(), anyLong(), any(ProfileDto.class))).willReturn(profileDto);

        mockMvc.perform(put("/profiles/{profileId}/user/{userId}", profileDto.getId(),
                        profileDto.getUserId())
                        .with(csrf())
                        .headers(httpHeaders())
                        .content(asJsonString(profileDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.cash", is(profileDto.getCash().doubleValue())));

        verify(profileService).updateProfile(anyLong(), anyLong(), any(ProfileDto.class));
    }

    @Test
    void testDeleteProfile() throws Exception {
        willDoNothing().given(profileService).deleteProfile(anyLong(), anyLong());

        mockMvc.perform(delete("/profiles/{profileId}/user/{userId}", 1, anyLong())
                        .with(csrf())
                        .headers(httpHeaders())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(profileService).deleteProfile(anyLong(), anyLong());
    }

}
