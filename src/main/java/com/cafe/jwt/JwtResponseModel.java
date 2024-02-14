package com.cafe.jwt;

import java.io.Serializable;

public class JwtResponseModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3431521698185863946L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private final String jwt;
	public JwtResponseModel(String jwt) {
		this.jwt = jwt;
	}
	public String getJwt() {
	      return jwt;
	   }

}
