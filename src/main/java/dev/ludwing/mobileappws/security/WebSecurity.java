package dev.ludwing.mobileappws.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import dev.ludwing.mobileappws.service.UserService;

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
