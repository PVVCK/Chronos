package com.airtribe.chronos.entities;

import com.airtribe.chronos.dtos.retry.RetryPolicyDTO;
import com.airtribe.chronos.enums.RetryStrategyKey;
import jakarta.persistence.Entity;


import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "retry_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetryPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private RetryStrategyKey strategy;

    private long backoffInitialMs;

    private double backoffFactor;

    private int maxAttempts;

    private long jitterMs;


    public RetryPolicyDTO toDTO() {
        return new RetryPolicyDTO(
                this.id,
                this.strategy,
                this.backoffInitialMs,
                this.backoffFactor,
                this.maxAttempts,
                this.jitterMs
        );
    }

}
