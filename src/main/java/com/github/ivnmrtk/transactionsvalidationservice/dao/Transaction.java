package com.github.ivnmrtk.transactionsvalidationservice.dao;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactions_id_seq")
    @SequenceGenerator(name="transactions_id_seq", sequenceName = "transactions_id_seq", allocationSize = 1)
    private Integer id;

    @Column
    private BigDecimal amount;
}

