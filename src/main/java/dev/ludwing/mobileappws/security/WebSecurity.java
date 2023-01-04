package dev.ludwing.mobileappws.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
	
	public WebSecurity(UserService detailService, BCryptPasswordEncoder encoder) {
		this.userDetailService = detailService;
		this.encoder = encoder;
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
		final AuthorizationFilter filterAuthorization = new AuthorizationFilter(authManager);
		
		// CSRF se desactiva porque no se utiliza en stateless API's
		// Luego se configura que todas las peticiones POST a /users no requieran autenticación Y requieren
		// Todas las peticiones GET a "/" (raiz) de la aplicación deben permitirse porque estas son de status para AWS beanstalk.
		// que se use el filtro de autenticación declarado en la clase AuthenticationFilter.
		// Todas las demás peticiones Sí deben ir autenticadas
		// Se agregan los filtros de autenticación y autorización
		// Se configura el framework para que la API sea stateless.
		http.csrf().disable()
			.authorizeHttpRequests().antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll()
			.antMatchers(HttpMethod.GET, SecurityConstants.VERIFICATION_EMAIL_URL).permitAll()
			.antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_REQUEST_URL).permitAll()
			.antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_URL).permitAll()
			.antMatchers(SecurityConstants.H2_CONSOLE).permitAll()
			.antMatchers(HttpMethod.GET, "/").permitAll()
			.anyRequest().authenticated()
			.and().addFilter(filter).addFilter(filterAuthorization)
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		// Desactivar para usar H2 console, pero jamás debe subirse esto así a la versión de producción
		// por seguridad.
		// http.headers().frameOptions().disable();
		
		return http.build();
	}
}