package com.instagram.controller;

import com.instagram.dto.BaseResponseDTO;
import com.instagram.dto.UserDTO;
import com.instagram.entity.User;
import com.instagram.repository.PostRepository;
import com.instagram.service.intrface.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
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
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/save")
    public ResponseEntity<BaseResponseDTO> saveUser(@RequestBody UserDTO userDTO){
        return  userService.registerUser(userDTO);
    }

    @PostMapping("/login/{userNameOrEmail}/{password}")
    public ResponseEntity<UserDTO> loginUser(@PathVariable String userNameOrEmail , @PathVariable String password){
        return  userService.validateUser(userNameOrEmail,password);
    }

    @GetMapping("/get-by-user-name/{userName}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String userName){
        return  userService.fetchByUserName(userName);
    }

    @PutMapping("/update-user")
    public ResponseEntity<BaseResponseDTO> updateUser(@RequestBody UserDTO userDTO){
        return userService.updateUser(userDTO);
    }

    @DeleteMapping("/delete-By-id/{userId}")
    public ResponseEntity<BaseResponseDTO> updateUser(@PathVariable UUID userId){
        return userService.deleteUser(userId);
    }
//    @PostMapping("/login-user")
//    public ResponseEntity<BaseResponseDTO> saveCredentials(@RequestBody User user){
//        return userService.saveCredentials(user);
//    }
//    @PostMapping("/log-in/{userName}/{password}")
//    public ResponseEntity<BaseResponseDTO> logIn(@PathVariable String userName,@PathVariable String password){
//        return userService.logIn(userName,password);
//    }

    @GetMapping("/get-matched-user/{userName}")
    public ResponseEntity<List<UserDTO>> getMatchedUsers(@PathVariable String userName){
        return  userService.getAllMatchedUsers(userName);
    }

    @DeleteMapping("/delete-user-by-userName/{userName}")
    public ResponseEntity<BaseResponseDTO> deleteUser(@PathVariable String userName){
        return userService.deleteUser(userName);
    }
}
