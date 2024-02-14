package com.cafe.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{
	 @Autowired
	 private JwtUserDetailsService userDetailsService;
	 @Autowired
	 private TokenManager tokenManager;
	 @Override
	 protected void doFilterInternal(HttpServletRequest request,
		      HttpServletResponse response, FilterChain filterChain)
		      throws ServletException, IOException{
		  String tokenHeader = request.getHeader("Authorization");
	      String username = null;
	      String token = null;
	   
	      //System.out.println("bearer found and token with it: " + tokenHeader);
	      if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
	          token = tokenHeader.substring(7);
	          try {
	             username = tokenManager.getUsernameFromToken(token);
	          } catch (IllegalArgumentException e) { 
	        	  e.fillInStackTrace();
	        	  } catch (ExpiredJwtException e) {   
	        	  e.fillInStackTrace();
	          }
	       } else {
	          System.out.println("Bearer String not found in token");
	       }
	      
	      if (null != username &&SecurityContextHolder.getContext().getAuthentication() == null) {
	          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
	          if (tokenManager.validateJwtToken(token, userDetails)) {
	             UsernamePasswordAuthenticationToken
	             authenticationToken = new UsernamePasswordAuthenticationToken(
	             userDetails, null,
	             userDetails.getAuthorities());
	             authenticationToken.setDetails(new
	             WebAuthenticationDetailsSource().buildDetails(request));
	             SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	          }
	       }
	      filterChain.doFilter(request, response);
	 }

	public boolean isAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.getAuthorities().stream()
				.anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
	}

	public boolean isUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.getAuthorities().stream()
				.anyMatch(role -> role.getAuthority().equals("ROLE_USER"));
	}

}
