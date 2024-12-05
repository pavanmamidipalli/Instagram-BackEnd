package com.instagram.service.intrface;

import com.instagram.dto.BaseResponseDTO;
import com.instagram.dto.UserDTO;
import com.instagram.entity.User;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {

    ResponseEntity<BaseResponseDTO> registerUser(UserDTO userDTO);

    ResponseEntity<UserDTO> validateUser(String userNameOrEmail , String password);

    ResponseEntity<BaseResponseDTO> updateUser(UserDTO userDTO);

    ResponseEntity<BaseResponseDTO> deleteUser(UUID userId);
    ResponseEntity<BaseResponseDTO> deleteUser(String userName);

    ResponseEntity<UserDTO> fetchByUserName(String userName);
//    ResponseEntity<BaseResponseDTO> saveCredentials(User user);
//    ResponseEntity<BaseResponseDTO> logIn(String userName,String password);

    public ResponseEntity<List<UserDTO>> getAllMatchedUsers(String userName);

}
