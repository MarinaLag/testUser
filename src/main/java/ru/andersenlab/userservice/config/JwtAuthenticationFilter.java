package ru.andersenlab.userservice.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.andersenlab.userservice.constants.SecurityConstants;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Algorithm algorithm;

    public JwtAuthenticationFilter(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            log.warn("Authorization header is missing or doesn't start with Bearer");
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);

            String username = jwt.getSubject();
            String roleFromJwt = jwt.getClaim("access_level_id").asString();

            log.info("Token verified. Username: {}, Role: {}", username, roleFromJwt);

            String role = null;
            if (roleFromJwt != null) {
                switch (roleFromJwt) {
                    case "Руководитель отделения" -> role = SecurityConstants.ROLE_DIRECTOR;
                    case "СуперЮзер" -> role = SecurityConstants.ROLE_SUPERUSER;
                    case "Сотрудник банка" -> role = SecurityConstants.ROLE_EMPLOYEE;
                    default -> log.warn("Unknown level ID: {}", roleFromJwt);
                }
            }

            if (username != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + role));

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, token, authorities);

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                if (!hasAccess(authorities)) {
                    log.warn("Access denied for user: {}", username);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Недостаточно прав");
                    response.getWriter().flush();
                    return;
                }
            }
        } catch (JWTVerificationException e) {
            log.error("JWT verification failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: JWT verification failed");
            response.getWriter().flush();
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean hasAccess(Collection<? extends GrantedAuthority> authorities) {
        authorities.forEach(authority -> log.info("Authority: {}", authority.getAuthority()));
        return authorities.stream().anyMatch(authority ->
                authority.getAuthority().equals("ROLE_" + SecurityConstants.ROLE_SUPERUSER) ||
                        authority.getAuthority().equals("ROLE_" + SecurityConstants.ROLE_DIRECTOR) ||
                        authority.getAuthority().equals("ROLE_" + SecurityConstants.ROLE_EMPLOYEE)
        );
    }
}
