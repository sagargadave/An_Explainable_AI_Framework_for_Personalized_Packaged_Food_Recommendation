package com.packagedfood.recommendation.repository;

import com.packagedfood.recommendation.entity.ProductAdditive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductAdditiveRepository
        extends JpaRepository<ProductAdditive, Long> {

    List<ProductAdditive> findByProductId(Long productId);

    void deleteByProductId(Long productId);
}