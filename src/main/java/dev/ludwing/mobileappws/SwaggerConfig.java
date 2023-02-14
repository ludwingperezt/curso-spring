package dev.ludwing.mobileappws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	/**
	 * Lo que hace este metodo es configurar la documentación de la API
	 * @return
	 */
	@Bean
	public Docket apiDocket() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("dev.ludwing.mobileappws")) // Aqui se coloca el paquete base para que la librería explore todas las clases que necesita a partir de ese paquete 
				.paths(PathSelectors.any())  // Todas las paths colocadas aquí se pondrán en la documentación de la API
				.build();

		return docket;
	}
}
