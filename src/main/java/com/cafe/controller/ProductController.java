package com.cafe.controller;

import com.cafe.jwt.JwtFilter;
import com.cafe.model.Category;
import com.cafe.model.Product;
import com.cafe.repository.CategoryRepository;
import com.cafe.repository.ProductRepository;
import com.cafe.utils.CafeCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ProductController {
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    CategoryRepository categoryRepo;
    @Autowired
    ProductRepository productRepo;
    //---------- add product api -----------//
    @PostMapping("/addProduct")
    ResponseEntity<?> addProduct(@RequestBody Map<String,String> requestMap){
        try {
            if (jwtFilter.isAdmin()) {
                String productName = requestMap.get("productName");
                int productCategory = Integer.parseInt(requestMap.get("productCategory"));
                String description = requestMap.get("description");
                int productPrice = Integer.parseInt(requestMap.get("productPrice"));
                Category category = categoryRepo.findByCategoryId(productCategory);
                if(category==null){
                    return ResponseEntity.badRequest().body("Catogory not available. please change category");
                }
                Product product = new Product();
                    product.setProductName(productName);
                    product.setCategory(category);
                    product.setDescription(description);
                    product.setPrice(productPrice);
                    productRepo.save(product);
                    return ResponseEntity.ok().body("product successfully added");

            } else {
                return ResponseEntity.badRequest().body("Unauthorized person");
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }
        return ResponseEntity.badRequest().body(CafeCommon.SOMETHING_WENT_WRONG);
    }

    //---------- update product api -----------//
    @PostMapping("/updateProduct")
    ResponseEntity<?> updateProduct(@RequestBody Map<String,String> requestMap){
        try {
            if (jwtFilter.isAdmin()) {
                int id = Integer.parseInt(requestMap.get("productId"));
                String productName = requestMap.get("productName");
                int productCategory = Integer.parseInt(requestMap.get("productCategory"));
                String description = requestMap.get("description");
                int productPrice = Integer.parseInt(requestMap.get("productPrice"));
                Category category = categoryRepo.findByCategoryId(productCategory);
                Product product = productRepo.findByProductId(id);
                if (product != null) {
                    product.setProductName(productName);
                    product.setCategory(category);
                    product.setDescription(description);
                    product.setPrice(productPrice);
                    productRepo.save(product);
                    return ResponseEntity.ok().body("product updated successfully");
                }
            } else {
                return ResponseEntity.badRequest().body("Unauthorized person");
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }
        return ResponseEntity.badRequest().body(CafeCommon.SOMETHING_WENT_WRONG);
    }

    //---------- get all product api -----------//
    @PostMapping("/getAllProduct")
    ResponseEntity<List<Product>> getProduct(){
        System.out.println("get all product-----");
        try {
            List<Product> product = productRepo.getAllProduct();
            if (product != null) {
                return new ResponseEntity<List<Product>>(product, HttpStatus.OK);
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }
        return new ResponseEntity<List<Product>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
