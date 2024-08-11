package com.cafe.repository;

import com.cafe.model.Category;
import com.cafe.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends  JpaRepository<Product, Integer> {
    Product findByProductId(int productId);
    void deleteByProductId(int productId);
    @Query(value = "select * from Product;", nativeQuery = true)
    List<Product> getAllProduct();

    @Query(value = "SELECT * FROM product WHERE category_id = ?1 and status=true;", nativeQuery = true)
    List<Product> getProductByCategory(int categoryId);
    @Query(value = "SELECT count(*) FROM product WHERE status=true;", nativeQuery = true)
    Integer getCount();



}
