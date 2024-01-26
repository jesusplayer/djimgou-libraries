package com.djimgou.security.service;

import com.djimgou.security.AuthSuccessHandler;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthSuccessProvider {
    void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication, AuthSuccessHandler thisHandler) throws IOException, ServletException;
}
