package dev.codescreen.CodeScreen_rpatlddg.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class AuthorizationResponse {

    private String userId;

    private String messageId;

    private String responseCode;

    private TransactionAmount balance;
}

