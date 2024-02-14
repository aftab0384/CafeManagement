package com.cafe.controller;

import com.cafe.consents.CafeCommon;
import com.cafe.jwt.JwtFilter;
import com.cafe.jwt.JwtRequestModel;
import com.cafe.jwt.JwtSecurityService;
import com.cafe.model.User;
import com.cafe.repository.UserRepository;
import com.cafe.service.UserRegisterMasterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.mortbay.util.ajax.JSON;


@RestController
@RequestMapping("/api/public")
public class SignUpController {
    @Autowired
    UserRepository userRepo;
    @Autowired
    private JwtSecurityService jwtSecurityService;
    @Autowired
    private UserRegisterMasterService userRegisterService;

    @Autowired
    JwtFilter jwtfilter;

    @PostMapping("/signup")
    ResponseEntity<?> signupUser(@RequestBody Map<String, String> requestMap){
            System.out.println("controller is working....");
            //System.out.println(requestMap.get("name"));
        Map<String, Object> returnMap = new HashMap<>();
        try{
            if(requestMap.get("name")==null && requestMap.get("email")==null && requestMap.get("contactNumber")==null){
                return ResponseEntity.badRequest().body(CafeCommon.INVALID_DATA);
            }
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String password = requestMap.get("password");
            String encryptPwd = passwordEncoder.encode(password);
            User user =null;
                  user  = userRepo.findByEmail(requestMap.get("email"));
            if(user!=null){
                returnMap.put("result", "error");
                returnMap.put("reason", "Username is already exist!!");

            }else{
                user = new User();
                user.setName(requestMap.get("name"));
                user.setEmail(requestMap.get("email"));
                user.setContactNumber(requestMap.get("contactNumber"));
                user.setPassword(encryptPwd);
                Set<String> roles = Arrays.stream(requestMap.get("role").split(","))
                        .map(String::trim)
                        .collect(Collectors.toSet());

                user.setRoles(roles);
                user.setStatus(false);
                userRepo.save(user);
                returnMap.put("result", "success");
                returnMap.put("reason", "user details saved successfully");
            }

        }catch(Exception e){
            e. fillInStackTrace();
            return ResponseEntity.badRequest().body(HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return ResponseEntity.ok(returnMap);
    }

    /*====================login=========================*/
    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestBody JwtRequestModel
                                                 request, HttpServletRequest httpRequest) throws Exception{
        System.out.println("login controller is working************" + request);

        String token=jwtSecurityService.authToken(request.getUsername(),request.getPassword());
        if(token.equalsIgnoreCase("false")){
            return ResponseEntity.badRequest().body("please wait for admin approval");
        }
        Map<String,Object> map=new HashMap<>();
        map.put("token",token);
        map.put("data", userRegisterService.loggerDetails(request.getUsername()));
        String json= JSON.toString(map);
        return jwtSecurityService.createAuthToken(json);
    }

}
