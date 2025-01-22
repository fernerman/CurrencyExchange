package org.project.cursexchange.servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class FilterResponse implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding("UTF-8");
        if (servletResponse instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            String requestUri = httpRequest.getRequestURI();


            if (requestUri.endsWith(".css") || requestUri.endsWith(".js") || requestUri.endsWith(".jpg") || requestUri.endsWith(".png")) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            httpResponse.setContentType("text/html;charset=UTF-8");

            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PATCH,OPTIONS, DELETE");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        }
        // Продолжение цепочки фильтров
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
