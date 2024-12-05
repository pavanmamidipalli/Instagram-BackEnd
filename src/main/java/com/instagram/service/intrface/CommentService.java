package com.instagram.service.intrface;

import com.instagram.dto.BaseResponseDTO;
import com.instagram.dto.CommentDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface CommentService {


    ResponseEntity<BaseResponseDTO> createComment(CommentDTO commentDTO , String userName);

    ResponseEntity<List<CommentDTO>> getAllCommentsOfAPost(UUID postId);

    ResponseEntity<BaseResponseDTO> updateComment(CommentDTO commentDTO);

    ResponseEntity<BaseResponseDTO> deleteComment(UUID commentId);


}
