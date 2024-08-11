package com.cafe.controller;

import com.cafe.repository.BillRepository;
import com.cafe.repository.CategoryRepository;
import com.cafe.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DashboardController {
    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private BillRepository billRepo;
    @GetMapping("/getDashBoardCount")
    ResponseEntity<Map<String,Object>> getCount(){
        Map<String, Object> map = new HashMap<>();
        map.put("category",categoryRepo.getCount());
        map.put("product", productRepo.getCount());
        map.put("bill", billRepo.getCount());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


}
