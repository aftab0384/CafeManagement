package com.cafe.service;

import com.cafe.model.User;
import com.cafe.repository.UserRepository;
import com.cafe.utils.CafeCommon;
import com.cafe.utils.EmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserAuthService {
    @Autowired
    private EmailUtils emailUtils;
    @Autowired
    UserRepository userRepo;
    public Map<String, Object> userForgotPassword(String email){
        Map<String, Object> map = new HashMap<>();
        try{
        User user = userRepo.findByEmail(email);
        System.out.println("username");

        if (user != null && user.getEmail() != null) {
            try {
                String userName = user.getName();
                String otp = CafeCommon.generateOTP();
                user.setUserOtp(otp);
                user.setOtpCreatedAt(CafeCommon.getCurrentDate());
                user.setOtpExpiredAt(CafeCommon.getOtpExpirationDate());
                userRepo.save(user);
                String message = "<p>Hi " + userName + ",<br>Your OTP for forgot password is: <b>" + otp + "</b></p>";
                String subject = "Forget Password";
                emailUtils.sendIndividualMail(email, subject, message);
                map.put("status", true);
                map.put("message", "Otp send successfully");
            }catch (Exception e){
                e.printStackTrace();
                map.put("status", false);
                map.put("message", "Otp not send");
            }
        }else {
            map.put("status", false);
            map.put("message", "please enter valid email");
        }
        }catch (Exception e){
            e.printStackTrace();
            map.put("status", false);
            map.put("message", CafeCommon.INTERNAL_SERVER_ERROR);
        }
        return map;
    }

    public Map<String, Object> changePassword(String email, String userOtp, String password) {
        Map<String, Object> map = new HashMap<>();
        try {
            User user = userRepo.findByEmail(email);
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(password);
            if (user != null) {
                String existingOtp = user.getUserOtp();
                Date otpExpiredAt = user.getOtpExpiredAt();
                if (userOtp.equalsIgnoreCase(existingOtp) && otpExpiredAt.after(CafeCommon.getCurrentDate())) {
                   try {
                       user.setPassword(encodedPassword);
                       user.setUpdatedAt(CafeCommon.getCurrentDate());
                       user.setUpdatedBy(CafeCommon.getLoggedinUserName());
                       userRepo.save(user);
                       map.put("status", true);
                       map.put("message", "Otp verified and password changed successfully");
                   }catch (Exception e){
                       e.fillInStackTrace();
                       map.put("status", false);
                       map.put("message", "Failed to changed password!!");
                   }

                } else {
                   // System.out.println("otp invalid or expired section--------------");
                    map.put("status", false);
                    map.put("message", "Otp expired or incorrect!");
                }
            } else {
                map.put("status", false);
                map.put("message", "Invalid email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status", false);
            map.put("message", CafeCommon.INTERNAL_SERVER_ERROR);
        }
        return map;
    }

}
