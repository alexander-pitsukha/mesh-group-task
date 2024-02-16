package com.meshgroup.task.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meshgroup.task.dto.PhoneDto;
import com.meshgroup.task.dto.UserDto;
import com.meshgroup.task.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

@WebMvcTest(UserController.class)
@WithMockUser
class UserControllerTests extends BasicControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
    }

    @Test
    void testGetUsers() throws Exception {
        UserDto userDto = getObjectFromJson("json/user_dto.json", UserDto.class);

        given(userService.getUsers(anyInt(), anyInt(), anyString(), anyString(), any()))
                .willReturn(Collections.singletonList(userDto));

        mockMvc.perform(get("/users")
                        .param("page", "1")
                        .param("size", "1")
                        .param("key", "id")
                        .param("operation", "=")
                        .param("value", "1")
                        .headers(httpHeaders())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId().intValue())));

        verify(userService).getUsers(anyInt(), anyInt(), anyString(), anyString(), any());
    }

    @Test
    void testGetUser() throws Exception {
        UserDto userDto = getObjectFromJson("json/user_dto.json", UserDto.class);

        given(userService.getUserById(anyLong())).willReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userDto.getId())
                        .headers(httpHeaders())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.age", is(userDto.getAge())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.role", is(userDto.getRole().name())));

        verify(userService).getUserById(anyLong());
    }

    @Test
    void testGePhones() throws Exception {
        List<PhoneDto> phoneDtos = getObjectMapper().readValue(new ClassPathResource("json/phones.json").getInputStream(),
                new TypeReference<>() {
                });

        given(userService.getPhones(anyLong())).willReturn(phoneDtos);

        mockMvc.perform(get("/users/{userId}/phones/", anyLong())
                        .headers(httpHeaders())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));

        verify(userService).getPhones(anyLong());
    }

    @Test
    void testSaveUser() throws Exception {
        UserDto userDto = getObjectFromJson("json/user_dto.json", UserDto.class);

        given(userService.saveUser(any(UserDto.class))).willReturn(userDto);

        mockMvc.perform(post("/users/")
                        .with(csrf())
                        .headers(httpHeaders())
                        .content(asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("http://*/users/*"))
                .andExpect(jsonPath("$.id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.age", is(userDto.getAge())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.role", is(userDto.getRole().name())));

        verify(userService).saveUser(any(UserDto.class));
    }

    @Test
    void testSavePhone() throws Exception {
        List<PhoneDto> phoneDtos = getObjectMapper().readValue(new ClassPathResource("json/phone_dtos.json").getInputStream(),
                new TypeReference<>() {
                });

        given(userService.savePhones(anyLong(), anyList())).willReturn(phoneDtos);

        mockMvc.perform(post("/users/{userId}/phones", anyLong())
                        .with(csrf())
                        .headers(httpHeaders())
                        .content(asJsonString(phoneDtos))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("http://*/users/*/phones"))
                .andExpect(jsonPath("$.*", hasSize(2)));

        verify(userService).savePhones(anyLong(), anyList());
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto userDto = getObjectFromJson("json/user_dto.json", UserDto.class);

        given(userService.updateUser(anyLong(), any(UserDto.class))).willReturn(userDto);

        mockMvc.perform(put("/users/{userId}", anyLong())
                        .with(csrf())
                        .headers(httpHeaders())
                        .content(asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.age", is(userDto.getAge())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.role", is(userDto.getRole().name())));

        verify(userService).updateUser(anyLong(), any(UserDto.class));
    }

    @Test
    void testUpdatePassword() throws Exception {
        willDoNothing().given(userService).updatePassword(anyLong(), anyString());

        mockMvc.perform(put("/users/{userId}/password/", anyLong())
                        .with(csrf())
                        .headers(httpHeaders())
                        .content(asJsonString(UUID.randomUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isAccepted());

        verify(userService).updatePassword(anyLong(), anyString());
    }

    @Test
    void testUpdatePhone() throws Exception {
        List<PhoneDto> phoneDtos = getObjectMapper().readValue(new ClassPathResource("json/phone_dtos.json").getInputStream(),
                new TypeReference<>() {
                });
        PhoneDto phoneDto = phoneDtos.get(0);

        given(userService.updatePhone(anyLong(), anyLong(), any(PhoneDto.class))).willReturn(phoneDto);

        mockMvc.perform(put("/users/{userId}/phones/{phoneId}", 1, anyLong())
                        .with(csrf())
                        .headers(httpHeaders())
                        .content(asJsonString(phoneDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.value", is(phoneDto.getValue())));

        verify(userService).updatePhone(anyLong(), anyLong(), any(PhoneDto.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        willDoNothing().given(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/{userId}", anyLong())
                        .with(csrf())
                        .headers(httpHeaders())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(anyLong());
    }

    @Test
    void testDeletePhone() throws Exception {
        willDoNothing().given(userService).deletePhone(anyLong(), anyLong());

        mockMvc.perform(delete("/users/{userId}/phones/{phoneId}", 1, anyLong())
                        .with(csrf())
                        .headers(httpHeaders())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(userService).deletePhone(anyLong(), anyLong());
    }

}
