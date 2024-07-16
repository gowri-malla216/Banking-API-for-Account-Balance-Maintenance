package dev.codescreen.CodeScreen_rpatlddg.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "Transactions")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column
    private String transactionAmount;

    @Column
    private String currency;

    @Column
    private DebitCredit debitOrCredit;

    @Column
    private String balance;

    @Column
    private ResponseCode responseCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private Amount amount;
}
