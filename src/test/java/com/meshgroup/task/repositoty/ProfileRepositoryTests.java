package com.meshgroup.task.repositoty;

import com.meshgroup.task.entity.Profile;
import com.meshgroup.task.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ProfileRepositoryTests extends AbstractRepositoryTests {

    @Autowired
    private ProfileRepository profileRepository;

    @BeforeEach
    void setUp() throws Exception {
        saveTestEntity("json/profile.json", Profile.class);
    }

    @Test
    void testFindByUserId() throws IOException {
        Profile profile = profileRepository.findByUserId(1L).orElseThrow();

        assertNotNull(profile);
        assertNotNull(profile.getId());
        assertEquals(BigDecimal.valueOf(123.45), profile.getCash());
    }

    @Test
    void testExistByUserIdAndProfileId() throws IOException {
        Profile profile = profileRepository.findByUserId(1L).orElseThrow();

        boolean result = profileRepository.existByUserIdAndProfileId(1L, profile.getId());

        assertTrue(result);
    }

}
