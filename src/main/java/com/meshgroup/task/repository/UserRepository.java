package com.meshgroup.task.repository;

import com.meshgroup.task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("select u from User u where u.id = (select p.user.id from Phone p where value = :value)")
    User findByPhone(@Param("value") String value);

    @Query("select u from User u where u.email = :email")
    User findByEmail(@Param("email") String email);

}
