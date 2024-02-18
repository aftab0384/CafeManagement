package com.cafe.controller;

import com.cafe.jwt.JwtFilter;
import com.cafe.model.Category;
import com.cafe.repository.CategoryRepository;
import com.cafe.utils.CafeCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CategoryController {
    @Autowired
    JwtFilter jwtfilter;
    @Autowired
    CategoryRepository categoryRepo;
    //---------- add category api -----------//
    @PostMapping("/addCategory")
    ResponseEntity<?> addCategory(@RequestBody Map<String,String> requestMap){
        try {
            if (jwtfilter.isAdmin()) {
                String categoryName = requestMap.get("categoryName");
                Category category = new Category();
                if (categoryName != null) {
                    category.setCategoryName(categoryName);
                    categoryRepo.save(category);
                    return ResponseEntity.ok().body("category added successfully added");
                }
            } else {
                return ResponseEntity.badRequest().body("Unauthorized person");
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }
        return ResponseEntity.badRequest().body(CafeCommon.SOMETHING_WENT_WRONG);
    }

    //---------- update category api -----------//
    @PostMapping("/updateCategory")
    ResponseEntity<?> updateCategory(@RequestBody Map<String,String> requestMap){
        try {
            if (jwtfilter.isAdmin()) {
                int id = Integer.parseInt(requestMap.get("categoryId"));
                String categoryName = requestMap.get("categoryName");
                Category category = categoryRepo.findByCategoryId(id);
                if (category != null) {
                    category.setCategoryName(categoryName);
                    categoryRepo.save(category);
                    return ResponseEntity.ok().body("category updated successfully");
                }
            } else {
                return ResponseEntity.badRequest().body("Unauthorized person");
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }

            return ResponseEntity.badRequest().body(CafeCommon.SOMETHING_WENT_WRONG);
    }


    //---------- get all category api -----------//
    @PostMapping("/getCategory")
    ResponseEntity<List<Category>> getCategory(){
        try {
            List<Category> category = categoryRepo.getAllCategory();
            if (category != null) {
                //return  ResponseEntity.ok().body(category);
                return new ResponseEntity<List<Category>>(category, HttpStatus.OK);
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }
            return new ResponseEntity<List<Category>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
