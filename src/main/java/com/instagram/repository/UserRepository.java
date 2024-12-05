package com.instagram.repository;

import com.instagram.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User , UUID> {

    User findByIdAndIsDeleted(UUID userId , Boolean isDeleted);

    @Query(value = "select * from user where is_deleted= 0" , nativeQuery = true)
    List<User> findAllUsersAndNotDeleted();

    User findByUserNameAndIsDeleted(String userName, Boolean isDeleted);


    @Query("SELECT u FROM User u WHERE (u.userName = :userName OR u.email = :email) AND u.password = :password AND u.isDeleted=false")
    User findByUserNameOrEmailAndPassword(@Param("userName") String userName, @Param("email") String email, @Param("password") String password);

    @Query(value = "select * from user where user_name like %?1%", nativeQuery = true)
    List<User> findMatchedUser(String userName);

    User findByEmail(String email);
}
