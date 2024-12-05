package com.instagram.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO extends BaseResponseDTO {


    private UUID id;
    private String userName;
    private String email;
    private String password;
    private Long mobileNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    private  List<PostDTO> postsDTOList;

    private List<CommentDTO> commentsDTOList;

    private LikesDTO likeDto;

}
