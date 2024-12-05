package com.instagram.repository;

import com.instagram.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Comment findByIdAndIsDeleted(UUID commentId,Boolean isDeleted);

    List<Comment> findByPostIdAndIsDeleted(UUID postId, Boolean isDeleted);
}
