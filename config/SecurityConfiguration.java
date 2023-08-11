package antifraud.config;

import antifraud.businesslayer.enums.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf().disable()
                .exceptionHandling(handing -> handing
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                )
                .headers(headers -> headers.frameOptions().disable())
                .authorizeHttpRequests(requests -> requests
                    .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/auth/user/{username}").hasRole(UserRole.ADMINISTRATOR.name())
                    .requestMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole(UserRole.ADMINISTRATOR.name(), UserRole.SUPPORT.name())
                    .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole(UserRole.MERCHANT.name())
                    .requestMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasRole(UserRole.SUPPORT.name())
                    .requestMatchers(HttpMethod.GET, "/api/antifraud/history/{number}").hasRole(UserRole.SUPPORT.name())
                    .requestMatchers(HttpMethod.GET, "/api/antifraud/history").hasRole(UserRole.SUPPORT.name())
                    .requestMatchers(HttpMethod.POST, "/api/antifraud/stolencard").hasRole(UserRole.SUPPORT.name())
                    .requestMatchers(HttpMethod.DELETE, "/api/antifraud/stolencard/{number}").hasRole(UserRole.SUPPORT.name())
                    .requestMatchers(HttpMethod.GET, "/api/antifraud/stolencard").hasRole(UserRole.SUPPORT.name())
                    .requestMatchers(HttpMethod.POST, "/api/antifraud/suspicious-ip").hasRole(UserRole.SUPPORT.name())
                    .requestMatchers(HttpMethod.DELETE, "/api/antifraud/suspicious-ip/{ip}").hasRole(UserRole.SUPPORT.name())
                    .requestMatchers(HttpMethod.GET, "/api/antifraud/suspicious-ip").hasRole(UserRole.SUPPORT.name())
                    .requestMatchers(HttpMethod.PUT, "/api/auth/access").hasRole(UserRole.ADMINISTRATOR.name())
                    .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole(UserRole.ADMINISTRATOR.name())
                    .requestMatchers("/actuator/shutdown").permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                )
                .build();
    }
}