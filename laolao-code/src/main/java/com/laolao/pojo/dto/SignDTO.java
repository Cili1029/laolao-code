package com.laolao.pojo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SignDTO {
    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
