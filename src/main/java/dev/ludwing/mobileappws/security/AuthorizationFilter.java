package dev.ludwing.mobileappws.security;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import dev.ludwing.mobileappws.io.entity.UserEntity;
import dev.ludwing.mobileappws.io.repositories.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthorizationFilter extends BasicAuthenticationFilter {

	private final UserRepository userRepository;
	
	public AuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
		
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest req,
									HttpServletResponse res,
									FilterChain chain) throws IOException, ServletException {
		
		String header = req.getHeader(SecurityConstants.HEADER_STRING);
		
		if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			chain.doFilter(req, res);
			return;
		}
		
		UsernamePasswordAuthenticationToken auth = getAuthentication(req);
		SecurityContextHolder.getContext().setAuthentication(auth);
		chain.doFilter(req, res);
		
	}
	
	/**
	 * Esta es una función personalizada que sirve para extraer y procesar el nombre del usuario
	 * del token JWT recibido en el header Authorization.
	 * 
	 * @param request
	 * @return
	 */
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		
		String token = request.getHeader(SecurityConstants.HEADER_STRING);
		
		if (token != null) {
			token = token.replace(SecurityConstants.TOKEN_PREFIX, "");
			
			// SNIPPET: Este snippet lo agregué porque la función signWith() de Jwts.builder está deprecada
			// Fuente: https://github.com/oktadev/okta-java-jwt-example/blob/master/src/main/java/com/okta/createverifytokens/JWTDemo.java
			// https://developer.okta.com/blog/2018/10/31/jwts-with-java
			SignatureAlgorithm mAlgorithm = SignatureAlgorithm.HS512;
			
			byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SecurityConstants.getTokenSecret());
			Key mKey = new SecretKeySpec(apiKeySecretBytes, mAlgorithm.getJcaName());
			
			// FIN SNIPPET.
			
			String user = null;
			
			// Debido a que la función parserBuilder() lanza una excepción de tipo ExpiredJwtException cuando
			// el token ya ha expirado, es necesario encerrar el parsing en un bloque try-catch.  Si la excepción
			// salta, entonces el usuario queda como null y entonces no se hace la autenticación
			try {
				user = Jwts.parserBuilder()
						.setSigningKey(mKey)
						.build()
						.parseClaimsJws(token)
						.getBody()
						.getSubject();
			}
			catch (ExpiredJwtException ex) {
				// Enviar a log que el token ya expiró.
			}
			
			
			if (user != null) {
				UserEntity userEntity = this.userRepository.findUserByEmail(user);
				UserPrincipal userPrincipal = new UserPrincipal(userEntity);
				return new UsernamePasswordAuthenticationToken(user,  null, userPrincipal.getAuthorities());
			}
		}
		return null;
	}

}
