package org.project.cursexchange;

import org.project.cursexchange.exceptions.DataAccesException;
import org.project.cursexchange.models.ErrorResponse;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class FilterResponse implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding("UTF-8");
        if (servletResponse instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setContentType("application/json;charset=UTF-8");
        }
        // Продолжение цепочки фильтров
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
