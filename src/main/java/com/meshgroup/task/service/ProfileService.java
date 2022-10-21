package com.meshgroup.task.service;

import com.meshgroup.task.dto.ProfileDto;

import java.util.List;

public interface ProfileService {

    void calculateCashPercents();

    List<ProfileDto> getProfiles(int page, int size);

    ProfileDto getProfile(Long userId);

    ProfileDto saveProfile(Long userId, ProfileDto profileDto);

    ProfileDto updateProfile(Long userId, Long profileId, ProfileDto profileDto);

    void deleteProfile(Long userId, Long profileId);

}
