package com.cafe.controller;

import com.cafe.jwt.JwtFilter;
import com.cafe.model.Bill;
import com.cafe.model.User;
import com.cafe.repository.BillRepository;
import com.cafe.repository.UserRepository;
import com.cafe.service.GenerateDocuments;
import com.cafe.utils.CafeCommon;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class BillController {
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private GenerateDocuments generateDocuments;
    @Autowired
    private JwtFilter jwtFilter;

    @PostMapping("/generateBill")
    ResponseEntity<String> generateBill(@RequestBody Map<String, Object> requestMap){
       try {
           if (!requestMap.containsKey("name") || !requestMap.containsKey("contactDetails") || !requestMap.containsKey("email") || !requestMap.containsKey("paymentMethod") || !requestMap.containsKey("productDetails") || !requestMap.containsKey("totalPrice")) {
               return ResponseEntity.badRequest().body("Missing or null value found");
           }
           String name = requestMap.get("name").toString();
           String contact = requestMap.get("contactDetails").toString();
           String email = requestMap.get("email").toString();
           int paymentMethod = Integer.parseInt(requestMap.get("paymentMethod").toString());
           String productDetails = requestMap.get("productDetails").toString();
           int totalPrice = Integer.parseInt(requestMap.get("totalPrice").toString());
           User user = userRepo.findByEmail(CafeCommon.getLoggedinUserName());
           System.out.println(user.getUserId());
           Bill bill = new Bill();
           bill.setName(name);
           bill.setContactNumber(contact);
           bill.setEmail(email);
           bill.setPaymentMethod(paymentMethod);
           bill.setProductDetails(productDetails);
           bill.setTotal(totalPrice);
           bill.setUuid(CafeCommon.generateUuid(name));
           bill.setCreatedBy(user.getUserId());
           bill.setCreatedOn(CafeCommon.getCurrentDate());
           bill = billRepository.save(bill);
           GenerateDocuments.generateBill(requestMap);

       }catch (Exception e){
           e.fillInStackTrace();
           ResponseEntity.badRequest().body(CafeCommon.SOMETHING_WENT_WRONG);
       }
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("/getBill")
    ResponseEntity<List<Bill>> getBill(HttpServletRequest request){
        System.out.println("get bill api working-------");
        List<Bill> bills=null;
        if(jwtFilter.isAdmin()){
           bills =  billRepository.getAllBill();
        }else {
            bills = billRepository.getBillByUserId(Integer.parseInt(request.getParameter("userId")));
        }

        return ResponseEntity.ok().body(bills);
    }


}
