package com.packagedfood.recommendation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "health_condition_rules")
@Getter
@Setter
@NoArgsConstructor
public class HealthConditionRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "health_condition_id", nullable = false)
    private HealthCondition healthCondition;

    @Column(nullable = false, length = 100)
    private String factor;

    @Column(length = 50)
    private String operator;

    private Double thresholdValue;

    @Column(length = 30)
    private String unit;

    @Column(nullable = false, length = 30)
    private String severity;

    @Column(nullable = false, length = 100)
    private String recommendation;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(length = 100)
    private String source;

    @Column(length = 1000)
    private String sourceUrl;

    public HealthConditionRule(
            HealthCondition healthCondition,
            String factor,
            String operator,
            Double thresholdValue,
            String unit,
            String severity,
            String recommendation,
            String reason,
            String source,
            String sourceUrl) {

        this.healthCondition = healthCondition;
        this.factor = factor;
        this.operator = operator;
        this.thresholdValue = thresholdValue;
        this.unit = unit;
        this.severity = severity;
        this.recommendation = recommendation;
        this.reason = reason;
        this.source = source;
        this.sourceUrl = sourceUrl;
    }
}