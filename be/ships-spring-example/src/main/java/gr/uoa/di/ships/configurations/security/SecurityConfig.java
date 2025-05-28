package gr.uoa.di.ships.configurations.security;

import gr.uoa.di.ships.persistence.model.enums.RoleEnum;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final UserDetailsService userDetailsService;
  private final JwtFilter jwtFilter;

  public SecurityConfig(UserDetailsService userDetailsService, JwtFilter jwtFilter) {
    this.userDetailsService = userDetailsService;
    this.jwtFilter = jwtFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(request -> request
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // let the browser send OPTIONS to any URL (including /socket/info)
            .requestMatchers("/auth/login", "/auth/register").permitAll()
            .requestMatchers("/admin", "/admin/**").hasAuthority(RoleEnum.ADMINISTRATOR.name())
            .requestMatchers("/migration", "/migration/**").hasAuthority(RoleEnum.ADMINISTRATOR.name())
            .requestMatchers("/registered-user", "/registered-user/**").hasAnyAuthority(RoleEnum.ADMINISTRATOR.name(), RoleEnum.REGISTERED_USER.name())
            .requestMatchers("/notification", "/notification/**").hasAnyAuthority(RoleEnum.ADMINISTRATOR.name(), RoleEnum.REGISTERED_USER.name())
            .requestMatchers("/zone-of-interest/", "/zone-of-interest/**").hasAnyAuthority(RoleEnum.ADMINISTRATOR.name(), RoleEnum.REGISTERED_USER.name())
            .requestMatchers("/vessel", "/vessel/**").permitAll()
            .requestMatchers("/filters", "/filters/**").permitAll()
            .requestMatchers("/socket/**", "/topic/**", "/app/**").permitAll() // allow SockJS/WebSocket handshakes
            .anyRequest()
            .authenticated()
        )
        .exceptionHandling(exception -> exception
           .accessDeniedHandler(new CustomAccessDeniedHandler())
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .anonymous(Customizer.withDefaults())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource(@Value("${cors.urls}") String[] allowedOrigins) {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(encoder());
    provider.setUserDetailsService(userDetailsService);
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }
}
