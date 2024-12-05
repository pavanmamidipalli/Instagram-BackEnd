package com.instagram.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instagram.dto.BaseResponseDTO;
import com.instagram.dto.UserDTO;
import com.instagram.entity.User;
import com.instagram.repository.UserRepository;
import com.instagram.service.intrface.UserService;
import com.instagram.util.ApplicationConstants;
import com.instagram.util.Convertions;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.security.PermitAll;
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
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Convertions convertion;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ResponseEntity<BaseResponseDTO> registerUser(UserDTO userDTO) {
        BaseResponseDTO baseResponseDTO = new BaseResponseDTO();
        try {
            if (!ObjectUtils.isEmpty(userDTO) && !StringUtils.isEmpty(userDTO.getEmail()) && !ObjectUtils.isEmpty(userDTO.getMobileNumber())) {
                User user = userRepository.findByUserNameAndIsDeleted(userDTO.getUserName(), Boolean.FALSE);

                if (ObjectUtils.isEmpty(user)) {
                    if(ObjectUtils.isEmpty(userRepository.findByEmail(userDTO.getEmail()))){
                        User convertedUser = convertion.convertToEntity(userDTO, User.class);
                        userRepository.save(convertedUser);
                        baseResponseDTO.setMessage(ApplicationConstants.USER_SAVE_SUCCESS);
                        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
                    }
                    baseResponseDTO.setMessage(ApplicationConstants.EMAIL_NAME_NOT_AVAILABLE);
                    return  new ResponseEntity<>(baseResponseDTO,HttpStatus.BAD_REQUEST);
                }
                baseResponseDTO.setMessage(ApplicationConstants.USER_ALREADY_EXISTS);
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setMessage(ApplicationConstants.ERROR_SAVING_USER + e.getMessage());
            log.error(e.getLocalizedMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ResponseEntity<UserDTO> validateUser(String userNameOrEmail, String password) {

        UserDTO responseUserDTO = new UserDTO();
        try {
            if (!StringUtils.isEmpty(userNameOrEmail) && !StringUtils.isEmpty(password)) {
                User user = userRepository.findByUserNameOrEmailAndPassword(userNameOrEmail, userNameOrEmail, password);
                if (!ObjectUtils.isEmpty(user)) {
                    UserDTO userDTO = convertion.convertToDto(user, UserDTO.class);
                    return new ResponseEntity<>(userDTO, HttpStatus.OK);
                } else {
                    responseUserDTO.setMessage(ApplicationConstants.USER_NOT_FOUND);
                    return new ResponseEntity<>(responseUserDTO, HttpStatus.NOT_FOUND);
                }
            } else {
                responseUserDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
                return new ResponseEntity<>(responseUserDTO, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            responseUserDTO.setMessage(ApplicationConstants.ERROR_FETCHING_USER + e.getLocalizedMessage());
            return new ResponseEntity<>(responseUserDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<UserDTO> fetchByUserName(String userName) {

        UserDTO responseUserDTO = new UserDTO();
        try {
            if (!StringUtils.isEmpty(userName)) {
                User user = userRepository.findByUserNameAndIsDeleted(userName, Boolean.FALSE);
                if (!ObjectUtils.isEmpty(user)) {
                    UserDTO userDTO = convertion.convertToDto(user, UserDTO.class);
                    return new ResponseEntity<>(userDTO, HttpStatus.OK);
                }
                responseUserDTO.setMessage(ApplicationConstants.USER_NOT_FOUND);
                return new ResponseEntity<>(responseUserDTO, HttpStatus.BAD_REQUEST);
            }
            responseUserDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(responseUserDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            responseUserDTO.setMessage(ApplicationConstants.ERROR_FETCHING_USER + e.getLocalizedMessage());
            return new ResponseEntity<>(responseUserDTO, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<BaseResponseDTO> updateUser(UserDTO userDTO) {
        BaseResponseDTO baseResponseDTO = new BaseResponseDTO();
        try {
            if (!ObjectUtils.isEmpty(userDTO.getId())) {
                User fetchedUser = userRepository.findById(userDTO.getId()).orElseThrow(() -> new RuntimeException(ApplicationConstants.USER_NOT_FOUND));
                User duplicateUser = userRepository.findByUserNameAndIsDeleted(userDTO.getUserName(), Boolean.FALSE);
                if (ObjectUtils.isEmpty(duplicateUser)) {
                    User user = objectMapper.readerForUpdating(fetchedUser).readValue(objectMapper.writeValueAsBytes(userDTO));
                    userRepository.save(user);
                    baseResponseDTO.setMessage(ApplicationConstants.USER_UPDATED_SUCCESS);
                    return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
                }
                baseResponseDTO.setMessage(ApplicationConstants.USER_NAME_NOT_AVAILABLE);
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            baseResponseDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
        baseResponseDTO.setMessage(ApplicationConstants.ERROR_UPDATING_USER);
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<BaseResponseDTO> deleteUser(UUID userId) {
        BaseResponseDTO baseResponseDTO = new BaseResponseDTO();
        try {
            if (!ObjectUtils.isEmpty(userId)) {
                User fetchedUser = userRepository.findByIdAndIsDeleted(userId, Boolean.FALSE);
                if (!ObjectUtils.isEmpty(fetchedUser)) {
                    fetchedUser.setIsDeleted(Boolean.TRUE);
                    userRepository.save(fetchedUser);
                    baseResponseDTO.setMessage(ApplicationConstants.USER_DELETED_SUCCESS);
                    return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
                }
                baseResponseDTO.setMessage(ApplicationConstants.USER_NOT_FOUND);
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setMessage(ApplicationConstants.EMPTY_INPUT);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            baseResponseDTO.setMessage(ApplicationConstants.ERROR_DELETING_USER + e.getMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ResponseEntity<BaseResponseDTO> deleteUser(String userName) {
        BaseResponseDTO baseResponseDTO=new BaseResponseDTO();
        try{
         User user=userRepository.findByUserNameAndIsDeleted(userName,Boolean.FALSE);
            if(ObjectUtils.isEmpty(user)){
                baseResponseDTO.setMessage(ApplicationConstants.USER_NOT_FOUND);
                return  new ResponseEntity<>(baseResponseDTO,HttpStatus.BAD_REQUEST);
            }
            user.setIsDeleted(true);
            userRepository.save(user);
            baseResponseDTO.setMessage(ApplicationConstants.USER_DELETED_SUCCESS);
            return new ResponseEntity<>(baseResponseDTO,HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getLocalizedMessage());
            baseResponseDTO.setMessage(ApplicationConstants.USER_NOT_FOUND);
            return new ResponseEntity<>(baseResponseDTO,HttpStatus.BAD_REQUEST);
        }

    }
//
//    BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder(10);
//    public ResponseEntity<BaseResponseDTO> saveCredentials(User user){
//        BaseResponseDTO baseResponseDTO= new BaseResponseDTO();
//        try{
//
//            if(!ObjectUtils.isEmpty(user)) {
//                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//                userRepository.save(user);
//                baseResponseDTO.setMessage(ApplicationConstants.USER_SAVE_SUCCESS);
//                return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
//            }
//        }catch (Exception e){
//            baseResponseDTO.setMessage(e.getLocalizedMessage());
//            return new ResponseEntity<>(baseResponseDTO,HttpStatus.BAD_REQUEST);
//        }
//        baseResponseDTO.setMessage(ApplicationConstants.USER_NOT_FOUND);
//        return new ResponseEntity<>(baseResponseDTO,HttpStatus.BAD_REQUEST);
//    }

//BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//   public ResponseEntity<BaseResponseDTO> logIn(String userName,String password){
//        BaseResponseDTO baseResponseDTO= new BaseResponseDTO();
//        try{
//         User user=userRepository.findByUserNameAndIsDeleted(userName,Boolean.FALSE);
//            if(ObjectUtils.isEmpty(user)){
//                throw new UsernameNotFoundException("User not found");
//            }
//            if(!passwordEncoder.matches(password,user.getPassword())){
//                baseResponseDTO.setMessage(ApplicationConstants.INVALID_CREDENTIALS);
//                return new ResponseEntity<>(baseResponseDTO,HttpStatus.OK);
//
//            }
//            user.setLogIn(Boolean.TRUE);
//            baseResponseDTO.setMessage(ApplicationConstants.LOGIN_SUCCESS);
//          return new ResponseEntity<>(baseResponseDTO,HttpStatus.OK);
//        }catch (Exception e){
//            baseResponseDTO.setMessage(ApplicationConstants.INVALID_CREDENTIALS);
//            return new ResponseEntity<>(baseResponseDTO,HttpStatus.BAD_REQUEST);
//        }

//   }


    @Override
    public ResponseEntity<List<UserDTO>> getAllMatchedUsers(String userName) {
        UserDTO responseUserDTO = new UserDTO();
        try {
            List<User> userList = userRepository.findMatchedUser(userName);
            if(!CollectionUtils.isEmpty(userList))
            {
                List<UserDTO> userDTOList = userList.stream().map(user -> convertion.convertToDto(user, UserDTO.class)).toList();
                return new ResponseEntity<>(userDTOList,HttpStatus.OK);
            }
            responseUserDTO.setMessage(ApplicationConstants.USER_NOT_FOUND);
            return new ResponseEntity<>(List.of(responseUserDTO),HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            log.error(e.getLocalizedMessage());
            responseUserDTO.setMessage(ApplicationConstants.ERROR_FETCHING_USER+ e.getLocalizedMessage());
            return new ResponseEntity<>(List.of(responseUserDTO),HttpStatus.BAD_REQUEST);
        }
    }


}
