package com.meshgroup.task.repositoty;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meshgroup.task.entity.Phone;
import com.meshgroup.task.repository.PhoneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PhoneRepositoryTests extends AbstractRepositoryTests {

    @Autowired
    private PhoneRepository phoneRepository;

    @Test
    void testFindAllByUserId() throws IOException {
        List<Phone> phones = getObjectMapper().readValue(new ClassPathResource("json/phones.json").getInputStream(),
                new TypeReference<>() {
                });
        phones.forEach(phone -> phoneRepository.save(phone));

        phones = phoneRepository.findAllByUserId(1L);

        assertEquals(3, phones.size());
    }

    @Test
    void testExistByUserIdAndPhoneId() throws IOException {
        List<Phone> phones = getObjectMapper().readValue(new ClassPathResource("json/phones.json").getInputStream(),
                new TypeReference<>() {
                });
        phones.forEach(phone -> phoneRepository.save(phone));

        boolean result = phoneRepository.existByUserIdAndPhoneId(1L, 1L);

        assertTrue(result);
    }

}
