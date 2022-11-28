package dev.ludwing.mobileappws.security;

public class SecurityConstants {

	public static final long EXPIRATION_TIME = 864000000; // Tiempo de validez del token: 10 dias (en milisegundos)
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users";
	public static final String TOKEN_SECRET = "IvUUmQsYxKLEkPJEoDludyG7mK4nzCv1l9lOoGp5qTDTm2yMPVCOr+ngwOKKhP/xdD9Q9lDWdRrLFsaiaPkWb5KIjIPXuBXcC48swcmKlk5Pq4ETh1SGLIp5qVYoHAVaK/VF3m9yZewcBIWjLdjPQfhQQ+rUg/9WVoJnS4tW441bDqaiDVVJyeITHp8ZjL/WzdAzhR+Ht7UWhDLz4lzYgBHHiF7qwZE1syCzxQ==";

}
