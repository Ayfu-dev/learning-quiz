package com.example.demo.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

public class AdminAuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {

		HttpSession session = request.getSession(false);

		if (session != null
				&& Boolean.TRUE.equals(
						session.getAttribute("adminAuthenticated"))) {

			return true;
		}

		response.sendRedirect("/quiz/admin/login");

		return false;
	}
}