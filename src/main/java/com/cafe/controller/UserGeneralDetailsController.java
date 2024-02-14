package com.cafe.controller;

import com.cafe.jwt.JwtFilter;
import com.cafe.model.User;
import com.cafe.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class UserGeneralDetailsController {
    @Autowired
    UserRepository userRepo;
    @Autowired
    JwtFilter jwtfilter;
    @PostMapping("/getUserDetails")
    ResponseEntity<?> getUser(){
        Map<String, Object> returnMap = new HashMap<>();
        System.out.println("get  user details=================");
        if(jwtfilter.isAdmin()) {
            List<Object[]> userDetails = userRepo.getAllUser();
            List<Map> list = new ArrayList<>();
            for (Object[] ud : userDetails) {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", ud[0]);
                map.put("name", ud[1]);
                map.put("contactDetails", ud[2]);
                map.put("email", ud[3]);
                map.put("status", ud[4]);
                list.add(map);
            }
            returnMap.put("data", list);
            return ResponseEntity.ok(returnMap);
        }else {
            return ResponseEntity.badRequest().body("you are not authorized person");
        }

    }

    //---------------- approve user ---------------
    @PostMapping("/approveUser")
    ResponseEntity<?> approveUser(HttpServletRequest request){
        Map<String, Object> returnMap = new HashMap<>();
        System.out.println("get user details=================");
    try {
        int userId = Integer.parseInt(request.getParameter("userId"));
        boolean status = Boolean.parseBoolean(request.getParameter("status"));
        System.out.println("userId and status is: "+ userId + " " + status);
        if (jwtfilter.isAdmin()) {
            User user = userRepo.findByUserId(userId);
            if (user != null) {
                System.out.println("if user exists==");
                user.setStatus(status);
                userRepo.save(user);
                sendMailToAllAdmin(status, user.getEmail(),userRepo.getAllAdmin());
                returnMap.put("result", "status updated successfully");
            } else {
                return ResponseEntity.badRequest().body("user not exists");
            }
        } else {
            return ResponseEntity.badRequest().body("Unauthorized person");
        }
    }catch (Exception e){
        e.fillInStackTrace();
    }
    return ResponseEntity.ok(returnMap);
    }

    private void sendMailToAllAdmin(boolean status, String email, List<Object[]> allAdmin) {
        System.out.println("send mail function");
    }


}
