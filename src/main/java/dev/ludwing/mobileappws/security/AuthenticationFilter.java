package dev.ludwing.mobileappws.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ludwing.mobileappws.ui.model.request.UserLoginRequestModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authManager;
	
	public AuthenticationFilter(AuthenticationManager authManager) {
		this.authManager = authManager;		
	}
	
	/**
	 * Este método se ejecuta cuando el usuario solicita un nuevo token de autenticación (login).
	 * 
	 * De forma automática, Spring provee el endpoint /login para poder hacer login en la aplicación.
	 * Esta función intentará hacer match entre los datos que vienen de la request con el modelo POJO
	 * de los datos de login, definidos en la clase UserLoginRequestModel.
	 * 
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, 
			HttpServletResponse res) throws AuthenticationException {
		
		try {
			// En esta parte se convierte el stream de bytes que viene de la Request
			// a un objeto de la clase que hemos definido para contener los datos
			// de autenticación: Email y contraseña.
			UserLoginRequestModel credentials = new ObjectMapper()
					.readValue(req.getInputStream(), UserLoginRequestModel.class);
			
			// En esta parte, Spring hace todo el proceso de buscar en la base de datos
			// el usuario en base a su email usando el método "loadUserByUsername()" definido
			// en UserServiceImpl.  Luego intentará autenticar al usuario usando su email y password
			// y si la autenticación es correcta, desencadenará el método "succesfulAuthentication()"
			return authManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							credentials.getEmail(),
							credentials.getPassword(),
							new ArrayList<>()));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/** 
	 * Este método se ejecuta cuando la autenticación es correcta. Lo que hace es generar el token
	 * de autenticación.
	 */
	protected void successfulAuthentication(HttpServletRequest req, 
											HttpServletResponse res, 
											FilterChain chain, 
											Authentication auth) throws IOException, ServletException {
		
		String username = ((User) auth.getPrincipal()).getUsername();
		
		// SNIPPET: Este snippet lo agregué porque la función signWith() de Jwts.builder está deprecada
		// Fuente: https://github.com/oktadev/okta-java-jwt-example/blob/master/src/main/java/com/okta/createverifytokens/JWTDemo.java
		// https://developer.okta.com/blog/2018/10/31/jwts-with-java
		SignatureAlgorithm mAlgorithm = SignatureAlgorithm.HS512;
		
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SecurityConstants.TOKEN_SECRET);
		Key mKey = new SecretKeySpec(apiKeySecretBytes, mAlgorithm.getJcaName());
		
		// FIN SNIPPET.
		
		String token = Jwts.builder()
				.setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(mKey, mAlgorithm)
				.compact();
		
		/** Si lo anterior no funciona, probar esta solución que da el instructor
		 * 
		SecretKeySpec secret_key = new SecretKeySpec(env.getProperty("token.secret").getBytes("UTF-8"), "HmacSHA256");
 
		String token=Jwts.builder().setSubject(userdetails.getUserid())
		.setExpiration(new Date(System.currentTimeMillis()+Long.parseLong(env.getProperty("token.expiration_time"))))
		.signWith(secret_key)
		.compact();
		 
		res.addHeader("token", token);
		res.addHeader("userid", userdetails.getUserid());
		 */
		
		// Una vez generado el token, éste se envía en los headers de la respuesta y el cliente debe
		// almacenarlo para usarlo posteriormente.
		res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
	}
}
