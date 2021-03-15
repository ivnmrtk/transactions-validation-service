package com.github.ivnmrtk.transactionsvalidationservice.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalKafkaTransactionDto {
    @JsonProperty("PID")
    private Integer pid;
    @JsonProperty("PAMOUNT")
    private BigDecimal pAmount;
    @JsonProperty("PDATA")
    private Long pData;
}
