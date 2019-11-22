package com.jee.managers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletManager {
	
	public static void forward(HttpServletRequest req, HttpServletResponse resp, String view) {
		try {
			req.getServletContext().getRequestDispatcher(view).forward(req, resp);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void redirect(HttpServletRequest req, HttpServletResponse resp, String url) {
		try {
			resp.sendRedirect(req.getContextPath() + url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
