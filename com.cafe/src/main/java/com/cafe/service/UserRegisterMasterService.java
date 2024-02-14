package com.cafe.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cafe.model.User;
import com.cafe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserRegisterMasterService {
	@Autowired
	private UserRepository userRepo;
	
	public Map<String,Object> loggerDetails(String loger) {
		Map<String, Object> returnMap = new HashMap<>();
		List<Map<String, Object>> list=new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		User userdetails = userRepo.findByEmail(loger);
		//User userdetails = userRepo.findByEmail(loger);
		if(userdetails!=null) {
			map=new HashMap();
			map.put("id", userdetails.getUserId());
			map.put("username", userdetails.getEmail());
			map.put("email", userdetails.getEmail());
			map.put("status", userdetails.getStatus());
			List<String> list1 = new ArrayList<>();
			returnMap.put("list", map);
			List<String> rolesList = new ArrayList<>(userdetails.getRoles());
			returnMap.put("roles", rolesList);
		}else {
			returnMap.put("error", "user not found");
		}
		
		
		return returnMap;
	}

}
