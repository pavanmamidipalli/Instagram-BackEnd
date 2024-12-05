package com.instagram.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instagram.dto.BaseResponseDTO;
import com.instagram.dto.CommentDTO;
import com.instagram.dto.UserDTO;
import com.instagram.entity.Comment;
import com.instagram.entity.User;
import com.instagram.repository.CommentRepository;
import com.instagram.repository.UserRepository;
import com.instagram.service.intrface.CommentService;
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
public class CommentServiceImplementation implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Convertions convertion;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public ResponseEntity<BaseResponseDTO> createComment(CommentDTO commentDTO , String userName) {
        BaseResponseDTO baseResponseDTO = new BaseResponseDTO();
        try {
            if (!ObjectUtils.isEmpty(commentDTO) && StringUtils.isNotEmpty(commentDTO.getCommentText()) && StringUtils.isNotEmpty(userName)) {
                Comment comment = convertion.convertToEntity(commentDTO, Comment.class);
                User user = userRepository.findByUserNameAndIsDeleted(userName,Boolean.FALSE);
                if(!ObjectUtils.isEmpty(user))
                {
                    comment.setUser(user);
                    commentRepository.save(comment);
                    baseResponseDTO.setMessage(ApplicationConstants.COMMENT_SAVED_SUCCESS);
                    return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
                }
                baseResponseDTO.setMessage(ApplicationConstants.USER_NOT_FOUND);
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            baseResponseDTO.setMessage(ApplicationConstants.ERROR_SAVING_COMMENT + e.getLocalizedMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<List<CommentDTO>> getAllCommentsOfAPost(UUID postId) {
        CommentDTO responseCommentDTO = new CommentDTO();
        try {
            if (!ObjectUtils.isEmpty(postId)) {
                List<Comment> commentList = commentRepository.findByPostIdAndIsDeleted(postId, Boolean.FALSE);
                if (!CollectionUtils.isEmpty(commentList)) {
                    List<CommentDTO> commentDTOList = commentList.stream().map(comment -> {
                        CommentDTO commentDTO = convertion.convertToDto(comment, CommentDTO.class);
                        commentDTO.setUserDto(convertion.convertToDto(comment.getUser(), UserDTO.class));
                        return commentDTO;
                    }).toList();
                    return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
                }
                responseCommentDTO.setMessage(ApplicationConstants.COMMENT_NOT_FOUND);
                return new ResponseEntity<>(List.of(responseCommentDTO), HttpStatus.BAD_REQUEST);
            }
            responseCommentDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(List.of(responseCommentDTO), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            responseCommentDTO.setMessage(ApplicationConstants.ERROR_FETCHING_COMMENT);
            return new ResponseEntity<>(List.of(responseCommentDTO), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<BaseResponseDTO> updateComment(CommentDTO commentDTO) {

        BaseResponseDTO baseResponseDTO = new BaseResponseDTO();
        try {
            if (!ObjectUtils.isEmpty(commentDTO.getId())) {
                Comment comment = commentRepository.findByIdAndIsDeleted(commentDTO.getId(), Boolean.FALSE);
                if (!ObjectUtils.isEmpty(comment)) {
                 Comment updatedComment=   objectMapper.readerForUpdating(comment).readValue(objectMapper.writeValueAsBytes(commentDTO));
                    commentRepository.save(updatedComment);
                    baseResponseDTO.setMessage(ApplicationConstants.COMMENT_UPDATED_SUCCESS);
                    return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
                }
                baseResponseDTO.setMessage(ApplicationConstants.COMMENT_NOT_FOUND);
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            baseResponseDTO.setMessage(ApplicationConstants.ERROR_UPDATING_COMMENT);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<BaseResponseDTO> deleteComment(UUID commentId) {
        BaseResponseDTO baseResponseDTO = new BaseResponseDTO();
        try {
            if (!ObjectUtils.isEmpty(commentId)) {
                Comment comment = commentRepository.findByIdAndIsDeleted(commentId, Boolean.FALSE);
                if (!ObjectUtils.isEmpty(comment)) {
                    comment.setIsDeleted(Boolean.TRUE);
                    commentRepository.save(comment);
                    baseResponseDTO.setMessage(ApplicationConstants.COMMENT_DELETED_SUCCESS);
                    return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
                }
                baseResponseDTO.setMessage(ApplicationConstants.COMMENT_NOT_FOUND);
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            baseResponseDTO.setMessage(ApplicationConstants.ERROR_DELETING_COMMENT);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }
}
