package com.ridango.payment.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDTO {

    private String id;

    private String name;

    private String balance;

    private String transactionInProgress;

}
