package gr.uoa.di.ships.configurations.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfigurer implements WebMvcConfigurer {

  private final Environment env;

  public CorsConfigurer(Environment env) {
    this.env = env;
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    String urls = env.getProperty("cors.urls");
    CorsRegistration reg = registry.addMapping("/api/**");
    for (String url : urls.split(",")) {
      reg.allowedOrigins(url);
    }
  }
}
