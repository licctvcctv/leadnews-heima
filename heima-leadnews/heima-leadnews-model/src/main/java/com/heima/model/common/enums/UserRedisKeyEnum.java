package com.heima.model.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRedisKeyEnum {
    USERLOVE("user:likes:"),

    USERREADCOUNT("user:read:"),

    USERFOLLOW("user:follow:"),

    USERNOLOVE("user:nolovearticle"),

    USERCOLLECTION("user:collection");

    String valus ;

}
