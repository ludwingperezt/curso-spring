package dev.ludwing.mobileappws;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// Habilita CORS para un endpoint dentro de un controlador específico
		// registry.addMapping("/users/email-verification");  
		
		// Habilita CORS para TODOS los controladores y sus rutas con los métodos permitidos separados por comas.
		// registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE");
		
		// Habilita CORS para TODOS los controladores y sus endpoints para todos los dominios.
		registry.addMapping("/**").allowedMethods("*").allowedOrigins("*");
	}
}
