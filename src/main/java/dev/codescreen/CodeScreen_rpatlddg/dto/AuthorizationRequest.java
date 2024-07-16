package dev.codescreen.CodeScreen_rpatlddg.dto;


import dev.codescreen.CodeScreen_rpatlddg.model.Transaction;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class AuthorizationRequest {

    private String userId;

    private String messageId;

    private TransactionAmount transactionAmount;
}
