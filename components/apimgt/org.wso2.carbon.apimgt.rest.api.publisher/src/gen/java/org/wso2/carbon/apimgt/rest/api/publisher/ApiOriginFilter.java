package org.wso2.carbon.apimgt.rest.api.publisher;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

@javax.annotation.Generated(value = "org.wso2.maven.plugins.JavaMSF4JServerCodegen", date = "2017-04-06T17:02:03.158+05:30")
public class ApiOriginFilter implements javax.servlet.Filter {
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        res.addHeader("Access-Control-Allow-Origin", "*");
        res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        res.addHeader("Access-Control-Allow-Headers", "Content-Type");
        chain.doFilter(request, response);
    }

    public void destroy() {}

    public void init(FilterConfig filterConfig) throws ServletException {}
}