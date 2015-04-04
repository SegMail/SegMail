/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.jsf;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author LeeKiatHaw
 */
//@WebFilter(filterName = "ProgramFilter", urlPatterns = {"/program/*"})
public class ProgramFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        String pathInfo = req.getPathInfo();
        String url = req.getRequestURI();
        
        //Check if pathInfo ends with "/"
        if(pathInfo != null && !pathInfo.isEmpty() && !pathInfo.endsWith("/")){
            resp.sendRedirect(req.getContextPath()+req.getServletPath()+req.getPathInfo()+"/");
            return;
        }
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        
    }
    
    
    
}
