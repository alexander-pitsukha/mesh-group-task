package com.meshgroup.task.service;

import com.meshgroup.task.MeshGroupTaskApplication;
import com.meshgroup.task.dto.ProfileDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MeshGroupTaskApplication.class)
class ProfileServiceIntegrationTests extends AbstractServiceTests {

    @Autowired
    private ProfileService profileService;

    @Test
    void testCalculateCashPercents() {
        profileService.calculateCashPercents();
        ProfileDto profileDto = profileService.getProfile(1L);

        assertEquals(profileDto.getCash().multiply(BigDecimal.valueOf(0.1)).setScale(2, RoundingMode.HALF_UP),
                profileDto.getPercent());
    }

    @ParameterizedTest
    @MethodSource("provideGetProfiles")
    void testGetProfiles(int page, int size) {
        List<ProfileDto> profileDtos = profileService.getProfiles(page, size);

        assertEquals(1, profileDtos.size());
    }

    @Test
    void testGetProfile() {
        ProfileDto profileDto = profileService.getProfile(1L);

        assertNotNull(profileDto);
    }

    @Test
    void testUpdateProfile() {
        ProfileDto profileDto = profileService.getProfile(1L);
        BigDecimal cash = BigDecimal.valueOf(999.99);
        profileDto.setCash(cash);
        profileDto = profileService.updateProfile(1L, profileDto.getId(), profileDto);

        assertNotNull(profileDto);
        assertEquals(cash, profileDto.getCash());
    }

    @Test
    void testDeleteProfile() {
        profileService.deleteProfile(1L, 1L);

        assertThrows(NoSuchElementException.class, () -> profileService.getProfile(1L),
                "NoSuchElementException error was expected");
    }

    private static Stream<Arguments> provideGetProfiles() {
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(0, 1)
        );
    }

}
