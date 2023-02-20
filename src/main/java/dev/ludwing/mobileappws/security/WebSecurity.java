package dev.ludwing.mobileappws.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import dev.ludwing.mobileappws.io.repositories.UserRepository;
import dev.ludwing.mobileappws.service.UserService;

/**
 * El código de esta sección está comentado porque WebSecurityConfigurerAdapter fue deprecado,
 * y quedó aquí solo por razones demostrativas.  La implementación correcta es la que se encuentra
 * por debajo de ésta sección comentada.
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	
	private final UserService userDetailService;  // Aqu{i se puede usar UserService porque esa clase heredó de UserDetailsService
	private final BCryptPasswordEncoder encoder;
	
	public WebSecurity(UserService detailService, BCryptPasswordEncoder encoder) {
		this.userDetailService = detailService;
		this.encoder = encoder;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// En esta línea se indica que toda petición POST hecha al endpoint /users
		// debe estar accesible sin autenticación.
		// Para cualquier otro endpoint, la autenticación debe estar disponible.
		http.csrf().disable().authorizeRequests().antMatchers(HttpMethod.POST, "/users").permitAll().anyRequest().authenticated();
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailService).passwordEncoder(encoder);
	}

}
*/

@EnableWebSecurity
public class WebSecurity {
	
	private final UserService userDetailService;  // Aqu{i se puede usar UserService porque esa clase heredó de UserDetailsService
	private final BCryptPasswordEncoder encoder;
	private final UserRepository userRepository;
	
	public WebSecurity(UserService detailService, BCryptPasswordEncoder encoder, UserRepository userRepository) {
		this.userDetailService = detailService;
		this.encoder = encoder;
		this.userRepository = userRepository;
	}
	
	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		// Aquí se configura el objeto AuthenticationManager.
		// Como en la clase WebSecurity no hereda de WebSecurityConfigureAdapter se debe hacer de esta
		// manera:
		AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
		
		// Aquí se configura el builder del AuthenticationManager con el servicio que obtiene los
		// datos del usuario de la base de datos y con el objeto que cifra las contraseñas.
		builder.userDetailsService(userDetailService).passwordEncoder(encoder);
		
		// El AuthenticationManager es un objeto que se puede usar en las filter classes. 
		AuthenticationManager authManager = builder.build();
		http.authenticationManager(authManager);
		
		// Configuración del AuthenticationFilter para que use una URI diferente a la que provee
		// Spring framework por defecto para hacer login, la cual es /login
		// Con esta configuración el URI a utilizar es /users/login
		final AuthenticationFilter filter = new AuthenticationFilter(authManager);
		filter.setFilterProcessesUrl("/users/login");
		
		// Creación del filtro de autorización, el cual parsea el token JWT y obtiene el nombre
		// de usuario al que le pertenece.
		// Para el manejo de roles y authorities se modificó el constructor de AuthorizationFilter
		// para poder inyectar el userRepository
		final AuthorizationFilter filterAuthorization = new AuthorizationFilter(authManager, userRepository);
		
		// Las peticiones pre-flight de cors deben procesarse antes que cualquier cosa.
		// CSRF se desactiva porque no se utiliza en stateless API's
		// Luego se configura que todas las peticiones POST a /users no requieran autenticación Y requieren
		// Todas las peticiones GET a "/" (raiz) de la aplicación deben permitirse porque estas son de status para AWS beanstalk.
		// que se use el filtro de autenticación declarado en la clase AuthenticationFilter.
		// Todas las demás peticiones Sí deben ir autenticadas
		// Se agregan los filtros de autenticación y autorización
		// Se configura el framework para que la API sea stateless.
		// La línea .antMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN") lo que hace es validar que para cada solicitud
		// 	hecha a con método DELETE a cualquier endpoint que esté bajo la raíz /users (incluyendo subdirectorios)
		// 	entonces se requiera el rol de admin.  Para ello se usa la función hasRole("ADMIN") En este caso no es necesario
		// 	poner "ROLE_ADMIN" porque Spring lo autocompleta de forma automática cuando se usa la función hasRole().
		//  También es posible verificar por authority en lugar de rol. Para ello solo hace falta cambiar la función
		//	.hasRole("ADMIN") por .hasAuthority("DELETE_AUTHORITY")
		http
			.cors().and()
			.csrf().disable()
			.authorizeHttpRequests().antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll()
			.antMatchers(HttpMethod.GET, SecurityConstants.VERIFICATION_EMAIL_URL).permitAll()
			.antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_REQUEST_URL).permitAll()
			.antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_URL).permitAll()
			.antMatchers(SecurityConstants.H2_CONSOLE).permitAll()
			.antMatchers(HttpMethod.GET, "/").permitAll()
			.antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**") // Aqui se configuran las rutas usadas por swagger para que sean accesibles sin necesidad de autenticación.
	        .permitAll()
	        //.antMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
	        .antMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("DELETE_AUTHORITY")
			.anyRequest().authenticated()
			.and().addFilter(filter).addFilter(filterAuthorization)
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		// Desactivar para usar H2 console, pero jamás debe subirse esto así a la versión de producción
		// por seguridad.
		// http.headers().frameOptions().disable();
		
		return http.build();
	}

	/**
	 * Este es un requerimiento para configurar CORS en la cadena de filtros de seguridad.
	 * 
	 * Se hace esto para que las peticiones pre-flight no se den por inválidas en la cadena de seguridad
	 * ya que esas peticiones no tienen cookies o headers de autenticación.  Esto funciona en conjunto
	 * con el procesamiento de las peticiones CORS (pre-flight) en la cadena de seguridad.
	 * 
	 * Ver: https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/cors.html
	 * 
	 * @return
	 */
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		
		final CorsConfiguration conf = new CorsConfiguration();
		
		// IMPORTANTE: cuando se usa setAllowCredentials(true) no se puede usar setAllowedOrigins(Arrays.asList("*"))
		// Se tiene que especificar la lista de dominios válidos.
		// conf.setAllowedOrigins(Arrays.asList("*"));
		conf.setAllowedOrigins(Arrays.asList("http://localhost:8091")); //http://localhost:8091 es el dominio donde está el servicio auxiliar para verificar tokens de email. 
		conf.setAllowedMethods(Arrays.asList("*"));
		conf.setAllowCredentials(true);
		conf.setAllowedHeaders(Arrays.asList("*"));
		
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", conf);
		
		return source;
	}
}