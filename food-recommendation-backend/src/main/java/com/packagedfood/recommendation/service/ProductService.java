package com.packagedfood.recommendation.service;

import com.packagedfood.recommendation.client.OpenFoodFactsClient;
import com.packagedfood.recommendation.dto.OpenFoodFactsProductDTO;
import com.packagedfood.recommendation.entity.Product;
import com.packagedfood.recommendation.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OpenFoodFactsClient openFoodFactsClient;

    public ProductService(
            ProductRepository productRepository,
            OpenFoodFactsClient openFoodFactsClient) {

        this.productRepository = productRepository;
        this.openFoodFactsClient = openFoodFactsClient;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String productName) {

        List<OpenFoodFactsProductDTO> products =
                openFoodFactsClient.searchProducts(productName);

        return products.stream()
                .map(this::convertToProduct)
                .toList();
    }

    private Product convertToProduct(
            OpenFoodFactsProductDTO dto) {

        Product product = new Product();

        product.setBarcode(dto.getCode());
        product.setProductName(dto.getProduct_name());
        product.setBrands(dto.getBrands());
        product.setCategories(dto.getCategories());
        product.setIngredientsText(dto.getIngredients_text());
        product.setImageUrl(dto.getImage_url());

        product.setEnergy100g(dto.getEnergy_100g());
        product.setFat100g(dto.getFat_100g());
        product.setSaturatedFat100g(dto.getSaturated_fat_100g());
        product.setCarbohydrates100g(dto.getCarbohydrates_100g());
        product.setSugars100g(dto.getSugars_100g());
        product.setFiber100g(dto.getFiber_100g());
        product.setProteins100g(dto.getProteins_100g());
        product.setSalt100g(dto.getSalt_100g());

        return product;
    }
}