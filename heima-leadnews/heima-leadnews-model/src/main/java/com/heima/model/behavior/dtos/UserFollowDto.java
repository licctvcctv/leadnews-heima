package com.heima.model.behavior.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFollowDto {
    private Long articleId;

    private  int authorId;

    private Short operation;

}
