package com.packagedfood.recommendation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String barcode;

    private String productName;

    private String brands;

    private String categories;

    @Column(columnDefinition = "TEXT")
    private String ingredientsText;

    private String imageUrl;

    // Nutrition values per 100g
    private Double energy100g;

    private Double fat100g;

    private Double saturatedFat100g;

    private Double carbohydrates100g;

    private Double sugars100g;

    private Double fiber100g;

    private Double proteins100g;

    private Double salt100g;

    // Predicted Nutri-Score
    private String nutritionGrade;
}