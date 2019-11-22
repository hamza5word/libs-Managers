package com.pro.managers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieManager {
	
	public static final int DEFAULT_MAX_AGE = 3600*24; //1 DAY
	
	public static void setCookie(HttpServletResponse resp, String name, Object value) {
		Cookie cookie = new Cookie(name, value.toString());
		cookie.setMaxAge(DEFAULT_MAX_AGE);
		resp.addCookie(cookie);
	}
	
	public static void setCookie(HttpServletResponse resp, String name, Object value, int max_age) {
		Cookie cookie = new Cookie(name, value.toString());
		cookie.setMaxAge(max_age);
		resp.addCookie(cookie);
	}
	
	public static String getCookie(HttpServletRequest req, String name) {
		String ret = null;
		Cookie[] cookies = req.getCookies();
		for(Cookie c : cookies) {
			if(c != null && c.getName().equals(name)) ret = c.getValue();
		}
		return ret;
	}
	
	public static void showCookies(HttpServletRequest req) {
		for(Cookie c : req.getCookies()) 
			System.out.println(c.getName());
	}
	
}
