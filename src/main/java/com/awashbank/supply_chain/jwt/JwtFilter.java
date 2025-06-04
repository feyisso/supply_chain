package com.awashbank.supply_chain.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUserDetailsService userDetailsService;
    private final TokenManager tokenManager;

    public JwtFilter(JwtUserDetailsService userDetailsService, TokenManager tokenManager) {
        this.userDetailsService = userDetailsService;
        this.tokenManager = tokenManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // ✅ Skip JWT authentication for register & login endpoints
        if (requestURI.contains("/scf/api/v1/auth/getToken")

                || requestURI.contains("/swagger-ui/**")
                || requestURI.contains("/swagger-ui.html")
                || requestURI.contains("/api-docs/**")
                || requestURI.contains("/swagger-resources/**")
                || requestURI.contains("/webjars/**")

                || requestURI.contains( "/test/status")

        ) {
            log.info("⏩ Skipping JWT filter for: " + requestURI);
            filterChain.doFilter(request, response);  // Continue without authentication
            return;
        }

        // Normal token processing
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("❌ No Bearer token found in request.");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);  // Get token from header
        String username = tokenManager.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (tokenManager.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("✅ Authentication successful for: " + username);
            } else {
                log.error("❌ Token validation failed for: " + username);
            }
        }

        filterChain.doFilter(request, response);  // Continue with the request
    }



}