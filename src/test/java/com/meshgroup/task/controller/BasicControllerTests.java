package com.meshgroup.task.controller;

import com.meshgroup.task.AbstractTests;
import com.meshgroup.task.authencation.jwt.JwtTokenUtil;
import com.meshgroup.task.entity.User;
import com.meshgroup.task.security.UserDetailsImpl;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@MockBean(JpaMetamodelMappingContext.class)
abstract class BasicControllerTests extends AbstractTests {

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    void setUp() throws Exception {
        User user = getObjectFromJson("json/user.json", User.class);
        given(jwtTokenUtil.extractUsername(anyString())).willReturn("admin");
        given(userDetailsService.loadUserByUsername(anyString())).willReturn(UserDetailsImpl.builder().user(user).build());
        given(jwtTokenUtil.validateToken(anyString(), any(UserDetails.class))).willReturn(true);
    }

    HttpHeaders httpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(UUID.randomUUID().toString());
        return httpHeaders;
    }

    String asJsonString(Object obj) {
        try {
            return getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
