package com.cafe.controller;

import com.cafe.jwt.JwtFilter;
import com.cafe.model.User;
import com.cafe.repository.UserRepository;
import com.cafe.utils.CafeCommon;
import com.cafe.utils.EmailUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.cafe.utils.CafeCommon.SOMETHING_WENT_WRONG;

@RestController
public class UserGeneralDetailsController {
    @Autowired
    UserRepository userRepo;
    @Autowired
    JwtFilter jwtfilter;
    @Autowired
    EmailUtils emailUtils;
    //---------------- get user details---------------
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

    private void sendMailToAllAdmin(boolean status, String email, List<String> allAdmin) {
        System.out.println("send mail function");
        System.out.println(CafeCommon.getLoggedinUserName());
        allAdmin.remove(CafeCommon.getLoggedinUserName());
        if (status) {
            emailUtils.sendGroupMail(CafeCommon.getLoggedinUserName(), "Account Approved", "USER:- " + email + " \nApproved by \nADMIN:- " + CafeCommon.getLoggedinUserName(), allAdmin);
        }else {
            emailUtils.sendGroupMail(CafeCommon.getLoggedinUserName(), "Account disabled", "USER:- " + email + " \nApproved by \nADMIN:- " + CafeCommon.getLoggedinUserName(), allAdmin);

        }
    }

    //---------------- change password ---------------
    @PostMapping("/changePassword")
    ResponseEntity<?> changePassword(HttpServletRequest request){
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String oldPassword = request.getParameter("oldPassword");
            String newPassword = request.getParameter("newPassword");
            User user = userRepo.findByEmail(CafeCommon.getLoggedinUserName());
            if (user != null) {
                if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepo.save(user);
                    emailUtils.sendGroupMail(CafeCommon.getLoggedinUserName(),"Password changed","Hi " + user.getName()+" Your password has been changed.",null);
                    return ResponseEntity.ok().body("password updated successfully");
                }
                return ResponseEntity.badRequest().body("incorrect old password");
            }
            return ResponseEntity.badRequest().body(SOMETHING_WENT_WRONG);
        }catch (Exception e){
            e.fillInStackTrace();
        }
            return ResponseEntity.badRequest().body(SOMETHING_WENT_WRONG);
        }
}
