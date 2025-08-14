package com.dpm05.user.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OIDCPayload {
    private String subject;
    private String email;
    private String picture;
    private String name;
}