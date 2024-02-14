package com.cafe.controller;

import com.cafe.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {
    @Autowired
    JwtFilter jwtfilter;
    @PostMapping("/getTest")
    String test(){
        if(jwtfilter.isAdmin()){
            System.out.println("it is admin");
            System.out.println("it is admin");System.out.println("it is admin");



            return "hi i am admin";
        }else if(jwtfilter.isUser()){
            return "hi i am user";
        }else {
            return "both is there";
        }

       // return "Hello this is test api";
    }


}
