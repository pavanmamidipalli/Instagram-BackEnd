package com.instagram.service.implementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.instagram.dto.BaseResponseDTO;
import com.instagram.dto.LikesDTO;
import com.instagram.dto.UserDTO;
import com.instagram.dto.UserLikesDTO;
import com.instagram.entity.Likes;
import com.instagram.entity.Post;
import com.instagram.entity.User;
import com.instagram.repository.LikesRepository;
import com.instagram.repository.PostRepository;
import com.instagram.repository.UserRepository;
import com.instagram.service.intrface.LikesService;
import com.instagram.util.ApplicationConstants;
import com.instagram.util.Convertions;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class LikesServiceImplementation  implements LikesService {

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Convertions convertion;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
     private PostRepository postRepository;
    @Override
    public ResponseEntity<BaseResponseDTO> createLike(LikesDTO likesDTO,String userName) {
        BaseResponseDTO baseResponseDTO = new BaseResponseDTO();
        try {
            if(!ObjectUtils.isEmpty(likesDTO.getPostDto().getId()) && StringUtils.isNotEmpty(userName) )
            {
                User user = userRepository.findByUserNameAndIsDeleted(userName, Boolean.FALSE);
                if(!ObjectUtils.isEmpty(user))
                {
                    Likes inputLike = convertion.convertToEntity(likesDTO, Likes.class);
                    inputLike.setUser(user);
                    likesRepository.save(inputLike);
                    baseResponseDTO.setMessage(ApplicationConstants.LIKE_SAVED_SUCCESS);
                    return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
                }
                baseResponseDTO.setMessage(ApplicationConstants.USER_NOT_FOUND);
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            log.error(e.getLocalizedMessage());
            baseResponseDTO.setMessage(ApplicationConstants.ERROR_SAVING_LIKE);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<List<LikesDTO>> getAllLikesOfAPost(UUID postId) {
        LikesDTO responseLikesDTO = new LikesDTO();
        try{
            if(!ObjectUtils.isEmpty(postId))
            {
                List<Likes> likesList = likesRepository.findByPostIdAndIsDeleted(postId ,Boolean.FALSE);
                if(!CollectionUtils.isEmpty(likesList))
                {
                    List<LikesDTO> likesDTOList = likesList.stream().map(likes -> {
                        LikesDTO likesDTO = convertion.convertToDto(likes, LikesDTO.class);
                        likesDTO.setUserDto(convertion.convertToDto(likes.getUser(), UserDTO.class));
                        return likesDTO;
                    }).toList();
                    return new ResponseEntity<>(likesDTOList, HttpStatus.OK);
                }
                responseLikesDTO.setMessage(ApplicationConstants.LIKE_NOT_FOUND);
                return new ResponseEntity<>(List.of(responseLikesDTO), HttpStatus.BAD_REQUEST);

            }
            responseLikesDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(List.of(responseLikesDTO), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            log.error(e.getLocalizedMessage());
            responseLikesDTO.setMessage(ApplicationConstants.ERROR_FETCHING_LIKE);
            return new ResponseEntity<>(List.of(responseLikesDTO), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<BaseResponseDTO> updateLikeByPostIdAndUserId(LikesDTO likesDTO) {
        BaseResponseDTO baseResponseDTO = new BaseResponseDTO();
        try {
            if(!ObjectUtils.isEmpty(likesDTO.getId()) )
            {
                Likes likes =  likesRepository.findById(likesDTO.getId()).orElseThrow(()-> new RuntimeException(ApplicationConstants.LIKE_NOT_FOUND));
                likes.setIsDeleted(!likes.getIsDeleted());
                likesRepository.save(likes);
                baseResponseDTO.setMessage(ApplicationConstants.LIKE_UPDATED_SUCCESS);
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            }
            baseResponseDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            log.error(e.getLocalizedMessage());
            baseResponseDTO.setMessage(ApplicationConstants.ERROR_SAVING_LIKE + e.getLocalizedMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<List<UserLikesDTO>> userLikes(String userName) {
        UserLikesDTO responseUserLikesDTO = new UserLikesDTO();
        try {
            if (StringUtils.isNotEmpty(userName)) {
                List<Post> postList = postRepository.findByIsDeletedAndUserIsDeleted(Boolean.FALSE, Boolean.FALSE);
                List<UserLikesDTO> userLikesDTOList = postList.stream().map(post -> {
                    Likes like = likesRepository.findByUserUserNameAndPostId(userName, post.getId());
                    UserLikesDTO userLikesDTO = new UserLikesDTO();
                    if (!ObjectUtils.isEmpty(like)) {
                        userLikesDTO.setPostId(post.getId());
                        userLikesDTO.setIsUserLiked(!like.getIsDeleted());
                    }
                    else {
                        userLikesDTO.setPostId(post.getId());
                        userLikesDTO.setIsUserLiked(Boolean.FALSE);
                    }
                    return userLikesDTO;
                }).toList();

                return new ResponseEntity<>(userLikesDTOList, HttpStatus.OK);
            }
            responseUserLikesDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(List.of(responseUserLikesDTO), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            responseUserLikesDTO.setMessage(ApplicationConstants.ERROR_FETCHING_LIKE + e.getLocalizedMessage());
            return new ResponseEntity<>(List.of(responseUserLikesDTO), HttpStatus.BAD_REQUEST);
        }
    }

}
