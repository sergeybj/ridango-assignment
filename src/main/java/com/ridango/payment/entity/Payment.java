package com.ridango.payment.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_account_id")
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "receiver_account_id")
    private Account receiver;

    @Column
    private BigDecimal amount;

    @Column(name="timestamp")
    private LocalDateTime timestamp;

//    @Column(name="started_processing")
//    private Boolean startedProcessing;
//
//    @Column(name="finished_processing")
//    private Boolean finishedProcessing;

}
