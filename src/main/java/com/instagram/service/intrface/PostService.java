package com.instagram.service.intrface;

import com.instagram.dto.BaseResponseDTO;
import com.instagram.dto.PostDTO;
import com.instagram.dto.UserLikesDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface PostService {

    ResponseEntity<BaseResponseDTO> savePostByUserName(PostDTO postDTO, String userName);

    ResponseEntity<List<PostDTO>> getAllPosts();

    ResponseEntity<List<PostDTO>> getAllPostByUserName(String userName);

    ResponseEntity<BaseResponseDTO> updatePost(PostDTO postDTO);

    ResponseEntity<BaseResponseDTO> deletePost(UUID postId);

    ResponseEntity<PostDTO> getPostById(UUID postId);


}
