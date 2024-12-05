package com.instagram.service.intrface;

import com.instagram.dto.BaseResponseDTO;
import com.instagram.dto.LikesDTO;
import com.instagram.dto.UserLikesDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface LikesService {
    ResponseEntity<BaseResponseDTO> createLike(LikesDTO likesDTO,String userName);

    ResponseEntity<List<LikesDTO>> getAllLikesOfAPost(UUID postId);

    ResponseEntity<BaseResponseDTO> updateLikeByPostIdAndUserId(LikesDTO likesDTO );
    ResponseEntity<List<UserLikesDTO>> userLikes(String userName);
}
