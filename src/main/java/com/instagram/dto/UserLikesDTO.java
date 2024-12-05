package com.instagram.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserLikesDTO extends BaseResponseDTO {

    private UUID postId;
    private Boolean isUserLiked;
}