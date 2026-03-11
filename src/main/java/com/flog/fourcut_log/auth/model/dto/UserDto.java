package com.flog.fourcut_log.auth.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
    private String name;
    private String role;
}
