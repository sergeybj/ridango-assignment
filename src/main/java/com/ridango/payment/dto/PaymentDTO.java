package com.ridango.payment.dto;

import com.ridango.payment.entity.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentDTO {

    private String senderAccountId;

    private String receiverAccountId;

    private String amount;

    private String timestamp;

}
