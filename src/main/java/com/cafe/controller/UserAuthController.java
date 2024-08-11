package com.cafe.controller;

import com.cafe.jwt.*;
import com.cafe.model.User;
import com.cafe.repository.UserRepository;
import com.cafe.service.UserAuthService;
import com.cafe.service.UserRegisterMasterService;
import com.cafe.utils.CafeCommon;
import com.cafe.utils.EmailUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import org.mortbay.util.ajax.JSON;


@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001", "http://localhost:4000"})
public class UserAuthController {
    @Autowired
    UserRepository userRepo;
    @Autowired
    private JwtSecurityService jwtSecurityService;
    @Autowired
    private UserRegisterMasterService userRegisterService;
    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    JwtFilter jwtfilter;
    @Autowired
    TokenManager tokenManager;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/signup")
    ResponseEntity<?> signupUser(@RequestBody Map<String, String> requestMap) {
        System.out.println("controller is working....");
        System.out.println(requestMap.get("name"));
        System.out.println(requestMap.get("email"));
        System.out.println(requestMap.get("contactNumber"));
        Map<String, Object> returnMap = new HashMap<>();
        try {
            if (requestMap.get("name") == null && requestMap.get("email") == null && requestMap.get("contactNumber") == null) {
                return ResponseEntity.badRequest().body(CafeCommon.INVALID_DATA);
            }
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String password = requestMap.get("password");
            String encryptPwd = passwordEncoder.encode(password);
            User user = null;
            user = userRepo.findByEmail(requestMap.get("email"));
            if (user != null) {
                returnMap.put("result", "error");
                returnMap.put("message", "Username is already exist!!");

            } else {
                user = new User();
                user.setName(requestMap.get("name"));
                user.setEmail(requestMap.get("email"));
                user.setContactNumber(requestMap.get("contactNumber"));
                user.setPassword(encryptPwd);
                String role = "USER";
                Set<String> roles = Arrays.stream(role.split(","))
                        .map(String::trim)
                        .collect(Collectors.toSet());

                user.setRoles(roles);
                user.setStatus(false);
                userRepo.save(user);
                returnMap.put("result", "success");
                returnMap.put("message", "user details saved successfully");
            }

        } catch (Exception e) {
            e.fillInStackTrace();
            return ResponseEntity.badRequest().body(HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return ResponseEntity.ok(returnMap);
    }

    /*====================login=========================*/
    @PostMapping("/login")
    public ResponseEntity<?> loginMethod(@RequestBody JwtRequestModel
                                                 request, HttpServletRequest httpRequest) throws Exception {
        System.out.println("api is working fine--------------");
        System.out.println(request.getUsername() + " " + request.getPassword());
        String token = jwtSecurityService.authToken(request.getUsername(), request.getPassword());
        if (token.equalsIgnoreCase("false")) {
            return ResponseEntity.badRequest().body("please wait for admin approval");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("data", userRegisterService.loggerDetails(request.getUsername()));
        String json = JSON.toString(map);
        return jwtSecurityService.createAuthToken(json);
    }

    @PostMapping("/forgetPassword")
    ResponseEntity<?> forgetPassword(@RequestBody Map<String, String> requestMap) throws MessagingException {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            String email = requestMap.get("email");
            User user = userRepo.findByEmail(email);
            System.out.println("username");

                responseMap = userAuthService.userForgotPassword(email);
                if ((boolean)responseMap.get("status")) {
                    return ResponseEntity.ok(responseMap);
                } else {
                    return ResponseEntity.status(401).body(responseMap);
                }
        }catch (Exception e){
          e.fillInStackTrace();
            responseMap.put("status", false);
            responseMap.put("message", "something went wrong");
            return ResponseEntity.internalServerError().body(responseMap);
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> verifyOtpAndChangePassword(@RequestBody Map<String, String> requestMap) {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            String userEmail = requestMap.get("userEmail");
            String userOtp = requestMap.get("userOtp");
            String userPassword = requestMap.get("userPassword");

            returnMap = userAuthService.changePassword(userEmail, userOtp, userPassword);

            if ((boolean) returnMap.get("status")) {
                return ResponseEntity.ok().body(returnMap);
            } else {
                return ResponseEntity.status(401).body(returnMap);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(returnMap);
        }
    }

    @GetMapping("/verifyJwtToken")
    public ResponseEntity<?> verifyJwtToken(HttpServletRequest request) {
        String tokenHeader = request.getHeader("Authorization");
       // System.out.println("in verify tokenheader-> "+tokenHeader);
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
           // System.out.println("in verify token-> "+token);
            String username;
            try {
                username = tokenManager.getUsernameFromToken(token);
                if (username != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (tokenManager.validateJwtToken(token, userDetails)) {
                        return ResponseEntity.ok("Token is valid");
                    }
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing token");
        }

    }

}
