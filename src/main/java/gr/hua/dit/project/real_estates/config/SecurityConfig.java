package gr.hua.dit.project.real_estates.config;

import gr.hua.dit.project.real_estates.exceptions.PendingApprovalException;
import gr.hua.dit.project.real_estates.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import jakarta.servlet.ServletException;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private UserService userService;

    private UserDetailsService userDetailsService;

    private BCryptPasswordEncoder passwordEncoder;

    public SecurityConfig(UserService userService, UserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                org.springframework.security.core.AuthenticationException exception) throws IOException, ServletException {
                String errorMessage;
                if (exception instanceof PendingApprovalException) {
                    errorMessage = "Invalid credentials";
                } else {
                    errorMessage = "Invalid username or password";
                }
                request.getSession().setAttribute("error_message", errorMessage);
                super.setDefaultFailureUrl("/login?error");
                super.onAuthenticationFailure(request, response, exception);
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/home", "/register", "/saveUser", "/images/**", "/js/**", "/css/**").permitAll()
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/users/**").hasRole("ADMIN")
                                .requestMatchers("/user/**").hasRole("ADMIN")
                                .requestMatchers("/tenants").hasRole("ADMIN")
                                .requestMatchers("/owner/owners").hasRole("ADMIN")
                                .requestMatchers("/pending-users/**").hasRole("ADMIN")
                                //.requestMatchers("reports/**").hasRole("ADMIN")
                                .requestMatchers("/owner/my-properties").hasRole("OWNER")
                                .requestMatchers("estate/new").hasRole("OWNER")
                                .requestMatchers("owner/applications").hasRole("OWNER")
                                .requestMatchers("/tenant/**").hasRole("TENANT")
                                .requestMatchers("/tenants/estates/search").hasRole("TENANT")
                                .requestMatchers("/applications/create/**").hasRole("TENANT")
                                .requestMatchers("/reports/my-reports").hasRole("TENANT")
                                .requestMatchers("/reports/create").hasRole("TENANT")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout((logout) -> logout.permitAll());
        return http.build();
    }


}
