package com.packagedfood.recommendation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "additives")
@Getter
@Setter
@NoArgsConstructor
public class Additive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 30)
    private String riskLevel;

    @Column(length = 40)
    private String thresholdType;

    private Double thresholdValue;

    @Column(length = 30)
    private String thresholdUnit;

    @Column(length = 100)
    private String foodCategory;

    @Column(length = 100)
    private String jurisdiction;

    @Column(length = 100)
    private String source;

    @Column(length = 1000)
    private String sourceUrl;

    public Additive(
            String code,
            String name,
            String category,
            String description,
            String riskLevel,
            String thresholdType,
            Double thresholdValue,
            String thresholdUnit,
            String foodCategory,
            String jurisdiction,
            String source,
            String sourceUrl) {

        this.code = code;
        this.name = name;
        this.category = category;
        this.description = description;
        this.riskLevel = riskLevel;
        this.thresholdType = thresholdType;
        this.thresholdValue = thresholdValue;
        this.thresholdUnit = thresholdUnit;
        this.foodCategory = foodCategory;
        this.jurisdiction = jurisdiction;
        this.source = source;
        this.sourceUrl = sourceUrl;
    }
}