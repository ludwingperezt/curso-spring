package dev.ludwing.mobileappws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	Contact contact = new Contact("Ludwing Perez", "https://example.com", "ludwing@example.com");
	
	List<VendorExtension> vendors = new ArrayList<>();
	
	ApiInfo apiInfo = new ApiInfo("Photo App web service documentation",
			"This pages documents Photo app RESTful web service endpoints",
			"1.0",
			"https://example.com/terms-of-service.html",
			contact,
			"Apache 2.0",
			"http://www.apache.org/licenses/LICENSE-2.0",
			vendors);

	/**
	 * Lo que hace este metodo es configurar la documentación de la API
	 * @return
	 */
	@Bean
	public Docket apiDocket() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2)
				.protocols(new HashSet<>(Arrays.asList("HTTP", "HTTPs"))) // indica a swagger qué protocolos están disponibles para usar en la API
				.apiInfo(apiInfo)
				.select()
				.apis(RequestHandlerSelectors.basePackage("dev.ludwing.mobileappws")) // Aqui se coloca el paquete base para que la librería explore todas las clases que necesita a partir de ese paquete 
				.paths(PathSelectors.any())  // Todas las paths colocadas aquí se pondrán en la documentación de la API
				.build();

		return docket;
	}
}
