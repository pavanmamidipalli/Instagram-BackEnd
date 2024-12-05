package com.instagram.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instagram.dto.BaseResponseDTO;
import com.instagram.dto.CommentDTO;
import com.instagram.dto.LikesDTO;
import com.instagram.dto.PostDTO;
import com.instagram.dto.UserDTO;
import com.instagram.entity.Post;
import com.instagram.entity.User;
import com.instagram.repository.PostRepository;
import com.instagram.repository.UserRepository;
import com.instagram.service.intrface.PostService;
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
public class PostServiceImplementation implements PostService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private Convertions convertion;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public ResponseEntity<BaseResponseDTO> savePostByUserName(PostDTO postDTO, String userName) {
        BaseResponseDTO baseResponseDTO = new BaseResponseDTO();
        try {
            if (!ObjectUtils.isEmpty(postDTO) && !StringUtils.isEmpty(postDTO.getContent()) && StringUtils.isNotEmpty(userName)) {
                Post convertedPost = convertion.convertToEntity(postDTO, Post.class);
                User user = userRepository.findByUserNameAndIsDeleted(userName,Boolean.FALSE);
                if (!ObjectUtils.isEmpty(user))
                {
                    convertedPost.setUser(user);
                    postRepository.save(convertedPost);
                    baseResponseDTO.setMessage(ApplicationConstants.POST_POSTED_SUCCESS);
                    return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
                }
                baseResponseDTO.setMessage(ApplicationConstants.USER_NOT_FOUND);
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            baseResponseDTO.setMessage(ApplicationConstants.ERROR_POSTING_POST + e.getMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<List<PostDTO>> getAllPosts() {

        PostDTO responsePostDTO = new PostDTO();
        try {
            List<Post> postList = postRepository.findAllAndIsNotDeleted();
            if (!CollectionUtils.isEmpty(postList)) {
                List<PostDTO> postDTOList = postList.stream().filter(post -> !post.getUser().getIsDeleted()).map(post -> {
                    PostDTO postDTO = convertion.convertToDto(post, PostDTO.class);
                    postDTO.setUserDto(convertion.convertToDto(post.getUser(), UserDTO.class));
                    postDTO.setCommentsDTOList(post.getComments().stream().filter(comment -> !comment.getIsDeleted()).map(comment -> convertion.convertToDto(comment, CommentDTO.class)).toList());
                    postDTO.setLikesDTOList(post.getLikes().stream().map(likes -> {
                        LikesDTO likesDTO = convertion.convertToDto(likes, LikesDTO.class);
                        likesDTO.setUserDto(convertion.convertToDto(likes.getUser(), UserDTO.class));
                        return  likesDTO;
                    }).toList());
                    return postDTO;
                }).toList();
                return new ResponseEntity<>(postDTOList, HttpStatus.OK);
            }
            responsePostDTO.setMessage(ApplicationConstants.POST_NOT_FOUND);
            return new ResponseEntity<>(List.of(responsePostDTO), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            responsePostDTO.setMessage(ApplicationConstants.ERROR_FETCHING_POST);
            return new ResponseEntity<>(List.of(responsePostDTO), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<List<PostDTO>> getAllPostByUserName(String userName) {
        PostDTO responsePostDTO = new PostDTO();
        try {
            if (StringUtils.isNotEmpty(userName)) {
                List<Post> postList = postRepository.findByUserUserNameAndIsDeleted(userName,Boolean.FALSE );
                if (!CollectionUtils.isEmpty(postList)) {
                    List<PostDTO> postDTOList = postList.stream().filter(post->!post.getUser().getIsDeleted()).map(post -> {
                        PostDTO postDTO = convertion.convertToDto(post, PostDTO.class);
                        postDTO.setUserDto(convertion.convertToDto(post.getUser(), UserDTO.class));
                        postDTO.setCommentsDTOList(post.getComments().stream().map(comment -> convertion.convertToDto(comment, CommentDTO.class)).toList());
                        postDTO.setLikesDTOList(post.getLikes().stream().map(likes -> {
                            LikesDTO likesDTO = convertion.convertToDto(likes, LikesDTO.class);
                            likesDTO.setUserDto(convertion.convertToDto(likes.getUser(), UserDTO.class));
                            return  likesDTO;
                        }).toList());
                        return postDTO;
                    }).toList();
                    return new ResponseEntity<>(postDTOList, HttpStatus.OK);
                }
                responsePostDTO.setMessage(ApplicationConstants.POST_NOT_FOUND);
                return new ResponseEntity<>(List.of(responsePostDTO), HttpStatus.BAD_REQUEST);
            }
            responsePostDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(List.of(responsePostDTO), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            responsePostDTO.setMessage(ApplicationConstants.ERROR_FETCHING_POST + e.getMessage());
            return new ResponseEntity<>(List.of(responsePostDTO), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<BaseResponseDTO> updatePost(PostDTO postDTO) {
        BaseResponseDTO baseResponseDTO = new BaseResponseDTO();
        try {
            if (!ObjectUtils.isEmpty(postDTO.getId())) {
                Post retrievedPost = postRepository.findByIdAndIsDeleted(postDTO.getId(),Boolean.FALSE );
                if (!ObjectUtils.isEmpty(retrievedPost)) {
                    Post updatedPost = objectMapper.readerForUpdating(retrievedPost).readValue(objectMapper.writeValueAsBytes(postDTO));
                    postRepository.save(updatedPost);
                    baseResponseDTO.setMessage(ApplicationConstants.POST_UPDATED_SUCCESS);
                    return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
                }
                baseResponseDTO.setMessage(ApplicationConstants.POST_NOT_FOUND);
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            baseResponseDTO.setMessage(ApplicationConstants.ERROR_FETCHING_POST + e.getMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<BaseResponseDTO> deletePost(UUID postId) {
        BaseResponseDTO baseResponseDTO = new BaseResponseDTO();
        try {
            if (!ObjectUtils.isEmpty(postId)) {
                Post retrievedPost = postRepository.findByIdAndIsDeleted(postId,Boolean.FALSE );
                if (!ObjectUtils.isEmpty(retrievedPost)) {
                    retrievedPost.setIsDeleted(Boolean.TRUE);
                    postRepository.save(retrievedPost);
                    baseResponseDTO.setMessage(ApplicationConstants.POST_DELETED_SUCCESS);
                    return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
                }
                baseResponseDTO.setMessage(ApplicationConstants.POST_NOT_FOUND);
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            baseResponseDTO.setMessage(ApplicationConstants.ERROR_FETCHING_POST + e.getMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<PostDTO> getPostById(UUID postId) {
        PostDTO postDTO= new PostDTO();
       try{
       Post post= postRepository.findById(postId).orElseThrow(RuntimeException::new);
       if(post.getIsDeleted().equals(Boolean.TRUE)){
           postDTO.setMessage(ApplicationConstants.POST_NOT_FOUND);
           return new ResponseEntity<>(postDTO,HttpStatus.BAD_REQUEST);
       }
       PostDTO convertedPost= convertion.convertToDto(post,PostDTO.class);
       convertedPost.setMessage(ApplicationConstants.POST_FOUND_SUCCESS);
       return new ResponseEntity<>(convertedPost,HttpStatus.OK);
        }
       catch (Exception e){
           log.error(e.getLocalizedMessage());
           postDTO.setMessage(e.getMessage());
           return new ResponseEntity<>(postDTO,HttpStatus.BAD_REQUEST);
       }
    }



}
