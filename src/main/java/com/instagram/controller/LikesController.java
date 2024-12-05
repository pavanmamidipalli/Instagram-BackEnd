package com.instagram.controller;

import com.instagram.dto.BaseResponseDTO;
import com.instagram.dto.LikesDTO;
import com.instagram.dto.UserLikesDTO;
import com.instagram.service.intrface.LikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("/api/likes")
public class LikesController {

    @Autowired
    private LikesService likesService;

    @PostMapping("/save/{userName}")
    public ResponseEntity<BaseResponseDTO> createLike(@RequestBody LikesDTO likesDTO,@PathVariable String userName)
    {
        return likesService.createLike(likesDTO,userName);
    }
    @GetMapping("/get-all-likes-by-post-id/{postId}")
    public ResponseEntity<List<LikesDTO>> getAllLikesByPostId(@PathVariable UUID postId)
    {
        return likesService.getAllLikesOfAPost(postId);
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponseDTO> updateLikeByPostIdAndUserId(@RequestBody LikesDTO likesDTO)
    {
        return likesService.updateLikeByPostIdAndUserId( likesDTO);
    }
    @GetMapping("/get-user-likes/{userName}")
    public ResponseEntity<List<UserLikesDTO>> userLikes(@PathVariable String userName)
    {
        return likesService.userLikes(userName);
    }


}
