package com.djimgou.security.service;

import com.djimgou.session.enums.SessionKeys;
import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static com.djimgou.core.util.AppUtils.has;


@Log4j2
@Component
public class TokenAuthenticationService {
    //static final long EXPIRATIONTIME = 864_000_000; // 10 days
    static final long EXPIRATIONTIME = 2_592_000_000L; // 30 days

    static final String SECRET = "ThisIsASecret";

    static final String TOKEN_PREFIX = "Bearer";

    public static final String HEADER_STRING = "Authorization";

    private Long tokenExpireTime;

    public TokenAuthenticationService(@Value("${auth.jwt.tokenExpireTime:}") String tokenExpireTime) {
        this.tokenExpireTime = has(tokenExpireTime) ? Long.valueOf(tokenExpireTime.replaceAll("[L_]", "")) : null;
    }

    public static String addAuthentication(HttpServletResponse res, String username, Long tokenExpireTime) {
        return addAuthentication(res, username, null, tokenExpireTime);
    }

    public String addAuthentication2(HttpServletResponse res, String username) {
        return addAuthentication2(res, username, null, tokenExpireTime);
    }

    public String addAuthentication2(HttpServletResponse res, String username, String tenantId, Long tokenExpireTime) {
        return addAuthentication(res, username, tenantId, tokenExpireTime);
    }

    public String addAuthentication2(HttpServletResponse res, String username, String tenantId) {
        return addAuthentication(res, username, tenantId, tokenExpireTime);
    }

    public static String addAuthentication(HttpServletResponse res, String username, String tenantId, Long tokenExpireTime) {
        final JwtBuilder jwtBuilder = Jwts.builder().setSubject(username);
        String jwt = jwtBuilder.claim(SessionKeys.TENANT_ID, tenantId)
                .setExpiration(new Date(System.currentTimeMillis() + (tokenExpireTime != null ? tokenExpireTime : EXPIRATIONTIME)))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + jwt);
        return jwt;
    }

    public String parseJwt2(HttpServletRequest request) {
        return parseJwt(request);
    }

    public static String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(TokenAuthenticationService.HEADER_STRING);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }
        return null;
    }

    public static String getUserNameFromJwtToken(String token) {
        return decodeToken(token).getSubject();
    }

    public static Claims decodeToken(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();
    }

/*    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            String user = getUserNameFromJwtToken(token);

            return user != null ? new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList()) : null;
        }
        return null;
    }*/

    public static boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
