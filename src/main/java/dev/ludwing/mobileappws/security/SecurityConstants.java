package dev.ludwing.mobileappws.security;

public class SecurityConstants {

	public static final long EXPIRATION_TIME = 864000000; // Tiempo de validez del token: 10 dias
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users";
	public static final String TOKEN_SECRET = "asdfwf34";

}
