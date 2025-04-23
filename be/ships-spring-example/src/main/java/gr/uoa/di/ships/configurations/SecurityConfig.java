package gr.uoa.di.ships.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

//   @Bean
//   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//        return http.requiresChannel(channel ->
//                channel.anyRequest().requiresSecure())
//                .build();
//    }

  //TODO: replace the two following methods when starting to use JWT
  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails admin = User.withUsername("admin")
        .password("{noop}admin123") // {noop} means no encoding
        .roles("ADMIN") // gives ROLE_ADMIN
        .build();

    return new InMemoryUserDetailsManager(admin);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.requiresChannel(channel -> channel.anyRequest().requiresSecure()) // Enforce HTTPS
               .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for APIs
               .authorizeHttpRequests(auth -> auth // Public endpoints (e.g., login/register)
                   .requestMatchers("/auth/**", "/public/**").permitAll()
                   .requestMatchers("/vessels/create", "/admin/**").hasRole("ADMIN") // Admin-only endpoints
                   .anyRequest().authenticated() // All others require authentication
        )
        .httpBasic(Customizer.withDefaults()) // or use JWT later
        .build();
  }
}
