package dev.ludwing.mobileappws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import dev.ludwing.mobileappws.security.AppProperties;

/**
 * Esta es la clase principal del proyecto.
 * Se ha extendido de SpringBootServletInitializer para poder empaquetar en un archivo
 * WAR y desplegar en un servidor tomcat existente.  Para esto también se sobreescribe
 * el método configure()
 * 
 * @author ludwingp
 *
 */
@SpringBootApplication
public class MobileAppWsApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(MobileAppWsApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(MobileAppWsApplication.class);
	}
	
	/**
	 * Esta función se utiliza para la encriptación de la contraseña del usuario.
	 * 
	 * Se declara como un @Bean para que pueda ser  inyectada con @Autowired
	 * 
	 * @return
	 */
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * Aquí se declara la creación del application context que sirve para obtener otros beans.
	 * @return
	 */
	@Bean
	public SpringApplicationContext springApplicationContext() {
		return new SpringApplicationContext();
	}
	
	@Bean(name="AppProperties")
	public AppProperties getAppProperties() {
		return new AppProperties();
	}

}
