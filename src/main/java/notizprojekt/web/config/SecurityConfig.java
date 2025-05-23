package notizprojekt.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomPasswordEncoder customPasswordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/images/**", "/register", "/login", "/api/auth/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin().disable()
            .logout()
                .logoutSuccessUrl("/login?logout")
                .permitAll();
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return customPasswordEncoder;
    }
}