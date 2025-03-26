package org.sysc4907.votingsystem.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
/**
 * Configures Spring Security for the project, to restrict access of endpoints based on authentication status of the user,
 * and their role (if authenticated). It also handles the creation of default accounts.
 */
public class WebSecurityConfig {
    private final RateLimiter rateLimiter;

    public WebSecurityConfig(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }
    @Bean
    public AuthenticationProvider customAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        return new CustomAuthenticationProvider(rateLimiter, userDetailsService, passwordEncoder);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider customAuthenticationProvider) {
        return new ProviderManager(customAuthenticationProvider);
    }


    @Bean
    /**
     * Specifying the access control for the endpoints of the system.
     */
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/submit-ballot-transactions"),
                                new AntPathRequestMatcher("/fabric/submit")
                        ))
                // CSRF protection is disabled for this endpoint because the request originates from JavaScript,
                // and despite multiple attempts to pass the CSRF token correctly, Spring Security rejects it as invalid.
                // This is not ideal from a security standpoint, as it leaves the endpoint vulnerable to CSRF attacks.
                // A better long-term solution would be to debug the CSRF token issue or explore alternative security measures,
                // such as requiring an authentication token instead.
                .authorizeHttpRequests((requests) -> requests
                        // Paths accessible to everyone
                        .requestMatchers( "/css/**", "/images/**", "/templates/**", "/test-elections",
                                "/set-test-election", "/registration-key", "/register-credentials",
                                "/ledger", "/download-ledger", "/view-election-details", "/fabric/evaluate", "/").permitAll()

                        // Paths for VOTER only
                        .requestMatchers("/threeBallot", "/verify-signing-key","/submit-ballot-transactions","/fabric/submit").hasRole("VOTER")

                        // Paths for ADMIN only
                        .requestMatchers("/create-election", "/election").hasRole("ADMIN")

                        // anything not specified, requires you to be authenticated (as either voter or admin)
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home")
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll());

        return http.build();
    }

    /**
     * The choice of password encryption for user credentials.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creation of one voter and one admin account as default accounts.
     * @param passwordEncoder configured password encoder to use for storing the password
     */
    @Bean
    public UserDetailsManager userDetailsManager(PasswordEncoder passwordEncoder) {
        // Voter user
        UserDetails voter = User.withUsername("voter")
                .password(passwordEncoder.encode("voter"))
                .roles("VOTER")
                .build();

        // Admin user
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN")
                .build();

        System.out.println("Stored password for voter: " + voter.getPassword());
        System.out.println("Stored password for admin: " + admin.getPassword());
        return new InMemoryUserDetailsManager(voter, admin);
    }
}