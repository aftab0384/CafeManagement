package com.cafe.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtSecurityService {
	@Autowired
	private JwtUserDetailsService userDetailsService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private TokenManager tokenManager;
	
	public boolean authenticate(String username, String password) throws Exception{
	System.out.println("jwt security "+ username + " " + password);
	try {
        authenticationManager.authenticate(
           new UsernamePasswordAuthenticationToken(username,password));
		return true;
     } catch (DisabledException e) {
        throw new Exception("USER_DISABLED", e);
     } catch (BadCredentialsException e) {
        throw new Exception("INVALID_CREDENTIALS", e);
     }
	}
	
	public String authToken(String username,String password) throws Exception{
		authenticate(username, password);
		 final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		 System.out.println("login details is : "+ userDetails.getUsername() + " " + userDetails.getPassword());
		 final String token = tokenManager.generateJwtToken(userDetails);

		if (authenticate(username, password)) {
			if (userDetailsService.userdetail().getStatus()) {
				System.out.println("in getStatus in jwtSecurityService java");
				return token;
			}
		}
		System.out.println("after authentication in jwtSecurityService java");
		 return "false";
	}
	
	public ResponseEntity<?> createAuthToken(String token){
		System.out.println(new JwtResponseModel(token));
        return ResponseEntity.ok(new JwtResponseModel(token));
    }
	
}
