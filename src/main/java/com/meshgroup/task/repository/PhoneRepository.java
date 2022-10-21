package com.meshgroup.task.repository;

import com.meshgroup.task.entity.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {

    @Query("select p from Phone p where p.user.id = :userId")
    List<Phone> findAllByUserId(@Param("userId") Long userId);

    @Query("select case when count(p) > 0 then true else false end from Phone p where p.user.id = :userId and p.id = :phoneId")
    boolean existByUserIdAndPhoneId(@Param("userId") Long userId, @Param("phoneId") Long phoneId);

}
