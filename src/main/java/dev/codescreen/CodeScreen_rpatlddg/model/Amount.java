package dev.codescreen.CodeScreen_rpatlddg.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Amount {
    @Column
    private String amount;

    @Column
    private String currency;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userId;

}
