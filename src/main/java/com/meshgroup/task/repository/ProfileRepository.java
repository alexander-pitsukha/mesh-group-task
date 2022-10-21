package com.meshgroup.task.repository;

import com.meshgroup.task.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query("select p from Profile p where p.user.id = :userId")
    Optional<Profile> findByUserId(@Param("userId") Long userId);

    @Query("select case when count(p) > 0 then true else false end from Profile p where p.user.id = :userId and p.id = :profileId")
    boolean existByUserIdAndProfileId(@Param("userId") Long userId, @Param("profileId") Long profileId);

}
