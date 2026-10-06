package com.packagedfood.recommendation.dto;

import com.packagedfood.recommendation.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResponse {

    private List<Product> products;

    private int count;
}