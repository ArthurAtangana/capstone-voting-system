package org.sysc4907.votingsystem.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final RateLimiter rateLimiter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(RateLimiter rateLimiter, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.rateLimiter = rateLimiter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();

        // Check if user is rate-limited
        if (!rateLimiter.tryConsume(username)) {
            session.setAttribute("errorMessage", "Too many failed attempts. Try again in 1 minute.");
            throw new LockedException("Rate limit exceeded.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null || !passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            session.setAttribute("errorMessage", rateLimiter.getRateLimitMessage(username));
            throw new BadCredentialsException("Incorrect username and password");
        }
        // Reset rate limiter on successful login
        rateLimiter.resetBucket(username);
        session.removeAttribute("errorMessage");

        return new UsernamePasswordAuthenticationToken(userDetails, rawPassword, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
