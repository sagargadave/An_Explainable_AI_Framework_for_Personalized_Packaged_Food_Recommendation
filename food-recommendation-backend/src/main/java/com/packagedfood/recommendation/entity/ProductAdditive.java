package com.packagedfood.recommendation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "product_additives",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_additive",
                        columnNames = {"product_id", "additive_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ProductAdditive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "additive_id", nullable = false)
    private Additive additive;

    /*
     * Open Food Facts ingredient text usually does not provide
     * the actual quantity of an individual additive.
     *
     * Therefore these fields remain nullable until a reliable
     * quantity is available.
     */
    private Double detectedAmount;

    @Column(length = 30)
    private String detectedUnit;

    @Column(nullable = false, length = 50)
    private String detectionMethod;

    public ProductAdditive(
            Product product,
            Additive additive,
            Double detectedAmount,
            String detectedUnit,
            String detectionMethod) {

        this.product = product;
        this.additive = additive;
        this.detectedAmount = detectedAmount;
        this.detectedUnit = detectedUnit;
        this.detectionMethod = detectionMethod;
    }
}