package dev.codescreen.CodeScreen_rpatlddg.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString

public class LoadRequest {

    private String userId;

    private String messageId;

    private TransactionAmount transactionAmount;

}
