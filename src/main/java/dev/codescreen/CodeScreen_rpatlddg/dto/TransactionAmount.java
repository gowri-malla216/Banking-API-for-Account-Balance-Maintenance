package dev.codescreen.CodeScreen_rpatlddg.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class TransactionAmount {
    private String amount;

    private String currency;

    private String debitOrCredit;
}
