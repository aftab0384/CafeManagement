package com.cafe.repository;

import com.cafe.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Category findByCategoryId(int categoryId);
    @Query(value = "select * from Category;", nativeQuery = true)
    List<Category> getAllCategory();

    @Query(value = "select count(*) from Category;", nativeQuery = true)
    Integer getCount();
}
