package com.meshgroup.task.service.impl;

import com.meshgroup.task.dto.ProfileDto;
import com.meshgroup.task.entity.Profile;
import com.meshgroup.task.entity.User;
import com.meshgroup.task.mapper.ProfileMapper;
import com.meshgroup.task.repository.ProfileRepository;
import com.meshgroup.task.repository.UserRepository;
import com.meshgroup.task.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.meshgroup.task.util.Constants.INVALID_USER_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Transactional
    @Scheduled(fixedRate = 20000)
    @Override
    public void calculateCashPercents() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            Optional<Profile> optionalProfile = profileRepository.findByUserId(user.getId());
            optionalProfile.ifPresent(this::calculateCashPercents);
        });
    }

    @Override
    public List<ProfileDto> getProfiles(int page, int size) {
        log.info("Get all profiles = {}, size = {}", page, size);
        Stream<Profile> stream;
        if (page == 0 && size == 0) {
            List<Profile> profiles = profileRepository.findAll();
            stream = profiles.stream();
        } else {
            Pageable pageable = PageRequest.of(page, size);
            Page<Profile> profiles = profileRepository.findAll(pageable);
            stream = profiles.stream();
        }
        return stream.map(profileMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ProfileDto getProfile(Long userId) {
        log.info("Get profile by userId {}", userId);
        var profile = profileRepository.findByUserId(userId).orElseThrow();
        return profileMapper.toDto(profile);
    }

    @Transactional
    @Override
    public ProfileDto saveProfile(Long userId, ProfileDto profileDto) {
        log.info("Save user profile for userId {}", userId);
        var user = userRepository.getById(userId);
        var profile = profileMapper.toEntity(profileDto);
        profile.setUser(user);
        profile = profileRepository.save(profile);
        return profileMapper.toDto(profile);
    }

    @Transactional
    @Override
    public ProfileDto updateProfile(Long userId, Long profileId, ProfileDto profileDto) {
        log.info("Update profile id {}", profileId);
        if (!profileRepository.existByUserIdAndProfileId(userId, profileId)) {
            throw new IllegalArgumentException(INVALID_USER_ID + userId);
        }
        var profile = profileRepository.getById(profileId);
        profile.setCash(profileDto.getCash());
        return profileMapper.toDto(profile);
    }

    @Transactional
    @Override
    public void deleteProfile(Long userId, Long profileId) {
        log.info("Delete profile id {}", profileId);
        if (!profileRepository.existByUserIdAndProfileId(userId, profileId)) {
            throw new IllegalArgumentException(INVALID_USER_ID + userId);
        }
        profileRepository.deleteById(profileId);
    }

    private void calculateCashPercents(Profile profile) {
        if (profile.getCash() != null && profile.getCash().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal maxPercent = profile.getCash().multiply(BigDecimal.valueOf(1.07))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal percent = profile.getCash().multiply(BigDecimal.valueOf(0.1))
                    .setScale(2, RoundingMode.HALF_UP);
            if (profile.getPercent() != null) {
                percent = profile.getPercent().add(percent);
            }
            if (percent.compareTo(maxPercent) < 1) {
                profile.setPercent(profile.getPercent() != null ? profile.getPercent().add(percent) : percent);
            }
        }
    }

}
