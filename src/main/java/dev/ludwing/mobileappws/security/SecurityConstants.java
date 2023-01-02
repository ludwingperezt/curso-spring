package dev.ludwing.mobileappws.security;

import dev.ludwing.mobileappws.SpringApplicationContext;

public class SecurityConstants {

	public static final long EXPIRATION_TIME = 864000000; // Tiempo de validez del token: 10 dias (en milisegundos)
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users";
	public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
	public static final String PASSWORD_RESET_URL = "/users/reset-password-request";
//	public static final String TOKEN_SECRET = "IvUUmQsYxKLEkPJEoDludyG7mK4nzCv1l9lOoGp5qTDTm2yMPVCOr+ngwOKKhP/xdD9Q9lDWdRrLFsaiaPkWb5KIjIPXuBXcC48swcmKlk5Pq4ETh1SGLIp5qVYoHAVaK/VF3m9yZewcBIWjLdjPQfhQQ+rUg/9WVoJnS4tW441bDqaiDVVJyeITHp8ZjL/WzdAzhR+Ht7UWhDLz4lzYgBHHiF7qwZE1syCzxQ==";
	
	public static final long PASSWORD_RESET_EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora.
	
	/**
	 * Esta función se utiliza para acceder al componente AppProperties, ya que 
	 * el mismo no se está inyectando en los filtros de autorización y autenticación.
	 *
	 * @return
	 */
	public static String getTokenSecret() {
		// En este caso ha funcionado el llamar al bean "AppProperties" porque en su declaración en la
		// clase MobileAppWsApplication, a ese bean se le dió ese mismo nombre.
		AppProperties appProperties = (AppProperties)SpringApplicationContext.getBean("AppProperties");
		return appProperties.getTokenSecret();
	}

}
