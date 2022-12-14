package dev.ludwing.mobileappws.shared;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Component;

import dev.ludwing.mobileappws.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Esta clase queda en el paquete "shared" porque se utilizará para definir funciones que tienen que
 * estar disponibles para toda la aplicación.  En esta clase hay funciones que se utilizarán en todo
 * el código porque son funciones auxiliares.
 * 
 * Se agrega la etiqueta @Component porque esto indica que se podrá inyectar esta clase en la clase 
 * UserSeriviceImpl.
 * 
 * @author ludwingp
 *
 */
@Component
public class Utils {
	
	private final Random RANDOM = new SecureRandom();
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	
	public String generateUserId(int length) {
        return generateRandomString(length);
    }
	
	public String generateAddressId(int length) {
        return generateRandomString(length);
    }
	
	private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }
	
	/**
	 * Verifica el token enviado por correo electrónico al cliente cuando ha
	 * @param token
	 * @return
	 */
	public static boolean hasTokenExpired(String token) {
		SignatureAlgorithm mAlgorithm = SignatureAlgorithm.HS512;
		
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SecurityConstants.getTokenSecret());
		Key mKey = new SecretKeySpec(apiKeySecretBytes, mAlgorithm.getJcaName());
		
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(mKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
		
		Date tokenExpirationDate = claims.getExpiration();
		Date today = new Date();
		
		return tokenExpirationDate.before(today);
	}
	
	/**
	 * Retorna el token de verificación de email.
	 * @param publicUserId
	 * @return
	 */
	public static String generateEmailVerificationToken(String publicUserId) {
		
		SignatureAlgorithm mAlgorithm = SignatureAlgorithm.HS512;
		
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SecurityConstants.getTokenSecret());
		Key mKey = new SecretKeySpec(apiKeySecretBytes, mAlgorithm.getJcaName());
		
		String token = Jwts.builder()
				.setSubject(publicUserId)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(mKey, mAlgorithm)
				.compact();
		
		return token;
	}
}
