package com.instagram.controller;

import com.instagram.dto.BaseResponseDTO;
import com.instagram.dto.CommentDTO;
import com.instagram.service.intrface.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@CrossOrigin("http://localhost:5173/")
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/save/{userName}")
    public ResponseEntity<BaseResponseDTO> create(@PathVariable String userName , @RequestBody CommentDTO commentDTO)
    {
        return commentService.createComment(commentDTO,userName);
    }

    @GetMapping("/get-all-comments-by-post-id/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable UUID postId)
    {
        return commentService.getAllCommentsOfAPost(postId);
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponseDTO> updateComment(@RequestBody CommentDTO commentDTO)
    {
        return commentService.updateComment(commentDTO);
    }
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<BaseResponseDTO> deleteComment(@PathVariable UUID commentId)
    {
        return commentService.deleteComment(commentId);
    }


}
