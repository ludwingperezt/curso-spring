package dev.ludwing.mobileappws.shared;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Component;

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
	
	private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }
}
