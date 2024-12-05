package com.instagram.repository;

import com.instagram.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LikesRepository extends JpaRepository<Likes ,UUID> {
    List<Likes> findByPostIdAndIsDeleted(UUID postId, Boolean aFalse);

    Likes findByUserUserNameAndPostId(String userName, UUID id);
}
