package com.ridango.payment.entity;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "account")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Account {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private BigDecimal balance;

    @Column(name = "transaction_in_progress")
    private Boolean transactionInProgress;
//
//    @OneToMany
//    @JoinColumn (name = "sender_account_id", insertable = false, updatable = false)
//    @Fetch(value = FetchMode.JOIN)
//    private List<Payment> paymentsFrom;
//
//    @OneToMany
//    @JoinColumn (name = "receiver_account_id", insertable = false, updatable = false)
//    @Fetch(value = FetchMode.JOIN)
//    private List<Payment> paymentsTo;

}
