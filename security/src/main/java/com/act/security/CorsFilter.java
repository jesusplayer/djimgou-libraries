package com.act.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.act.core.util.AppUtils.has;

/**
 * DJIMGOU NKENNE DANY MARC 08/2020- 09/2021
 */
@Log4j2
public class CorsFilter extends GenericFilterBean {
    @Value("#{'${auth.allowOrigins}'.split(',')}")
    private final List<String> allowedOrigins = Arrays.asList("http://localhost:4200");

    @Autowired
    HttpSession httpSession;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // https://stackoverflow.com/questions/43114750/header-in-the-response-must-not-be-the-wildcard-when-the-requests-credentia
        // Lets make sure that we are working with HTTP (that is, against HttpServletRequest and HttpServletResponse objects)
        if (servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            // request.getRequestURI().replace(request.getContextPath(),"")
            // Access-Control-Allow-Origin
            String origin = request.getHeader("Origin");
            final String ACCES_CONTROL = "Access-Control-Allow-Origin";
            if(!has(response.getHeader(ACCES_CONTROL))){
                response.setHeader(ACCES_CONTROL, allowedOrigins.contains(origin) ? origin : "");
                response.setHeader("Vary", "Origin");

                // Access-Control-Max-Age
                response.setHeader("Access-Control-Max-Age", "3600");

                // Access-Control-Allow-Credentials
                response.setHeader("Access-Control-Allow-Credentials", "true");

                // Access-Control-Allow-Methods
                response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");

                // Access-Control-Allow-Headers
                response.setHeader("Access-Control-Allow-Headers",
                        "Origin, X-Requested-With, Content-Type, Accept, X-CSRF-TOKEN, X-TenantId, X-SessionId, X-Username");
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);


        //System.out.println("WebConfig; "+request.getRequestURI());
        /*res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With,observe");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Expose-Headers", "Authorization");
        res.addHeader("Access-Control-Expose-Headers", "responseType");
        res.addHeader("Access-Control-Expose-Headers", "observe");
        //System.out.println("Request Method: "+request.getMethod());
        if (!(request.getMethod().equalsIgnoreCase("OPTIONS"))) {
            try {
                filterChain.doFilter(request, res);
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            return;

        }*/

       /* res.setHeader("Access-Control-Allow-Origin", "*");
        //       response.addHeader("X-FRAME-OPTIONS", "SAMEORIGIN");

        if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {

            if (log.isDebugEnabled()) {
                log.debug("do pre flight...");
            }

            res.setHeader("Access-Control-Allow-Methods", "POST,GET,HEAD,OPTIONS,PUT,DELETE");
            res.setHeader("Access-Control-Max-Age", "3600");
            res.setHeader("Access-Control-Allow-Headers", "x-requested-with,Content-Type,Accept,x-auth-token,x-xsrf-token,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,Access-Control-Allow-Origin");
            //response.setHeader("Access-Control-Expose-Headers", "Access-Control-Allow-Origin,x-auth-token");
        } else {
            res.setHeader("Access-Control-Allow-Origin", "localhost");
            res.setHeader("Access-Control-Allow-Methods", "POST,GET,HEAD,OPTIONS,PUT,DELETE");
            res.setHeader("Access-Control-Max-Age", "3600");
            res.setHeader("Access-Control-Allow-Headers", "x-requested-with,Content-Type,Accept,x-auth-token,x-xsrf-token,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,Access-Control-Allow-Origin");
            filterChain.doFilter(request, res);
        }*/
    }
}
