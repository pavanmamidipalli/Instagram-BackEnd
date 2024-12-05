package com.instagram.repository;

import com.instagram.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query(value = "select * from post where is_deleted=0 order by updated_at desc" , nativeQuery = true)
    List<Post> findAllAndIsNotDeleted();

    List<Post> findByUserIdAndIsDeleted(UUID userId, Boolean isDeleted);

    Post findByIdAndIsDeleted(UUID postId, Boolean aFalse);

    List<Post> findByUserUserNameAndIsDeleted(String userName, Boolean aFalse);

    List<Post> findByIsDeletedAndUserIsDeleted(Boolean aFalse, Boolean aFalse1);
}
