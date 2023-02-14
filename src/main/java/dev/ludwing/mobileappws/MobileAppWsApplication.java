package dev.ludwing.mobileappws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import dev.ludwing.mobileappws.security.AppProperties;

/**
 * Esta es la clase principal del proyecto. Se ha extendido de
 * SpringBootServletInitializer para poder empaquetar en un archivo WAR y
 * desplegar en un servidor tomcat existente. Para esto también se sobreescribe
 * el método configure()
 * 
 * @author ludwingp
 *
 */
@SpringBootApplication
public class MobileAppWsApplication extends SpringBootServletInitializer {

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
	 * Se declara como un @Bean para que pueda ser inyectada con @Autowired
	 * 
	 * @return
	 */
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Aquí se declara la creación del application context que sirve para obtener
	 * otros beans.
	 * 
	 * @return
	 */
	@Bean
	public SpringApplicationContext springApplicationContext() {
		return new SpringApplicationContext();
	}

	@Bean(name = "AppProperties")
	public AppProperties getAppProperties() {
		return new AppProperties();
	}

	/**
	 * Este @Bean es un workaround hallado aqui:
	 * https://segmentfault.com/a/1190000041908876/en
	 * 
	 * Porque al agregar swagger la aplicación ya no encendía.
	 * 
	 * @param webEndpointsSupplier
	 * @param servletEndpointsSupplier
	 * @param controllerEndpointsSupplier
	 * @param endpointMediaTypes
	 * @param corsProperties
	 * @param webEndpointProperties
	 * @param environment
	 * @return
	 */
	@Bean
	public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
			ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier,
			EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties,
			WebEndpointProperties webEndpointProperties, Environment environment) {
		List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
		Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
		allEndpoints.addAll(webEndpoints);
		allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
		allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
		String basePath = webEndpointProperties.getBasePath();
		EndpointMapping endpointMapping = new EndpointMapping(basePath);
		boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment,
				basePath);
		return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
				corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath),
				shouldRegisterLinksMapping, null);
	}

	private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment,
			String basePath) {
		return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath)
				|| ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
	}

}
