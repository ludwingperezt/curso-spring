package dev.ludwing.mobileappws.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import dev.ludwing.mobileappws.io.entity.AuthorityEntity;
import dev.ludwing.mobileappws.io.entity.RoleEntity;
import dev.ludwing.mobileappws.io.entity.UserEntity;

/**
 * Clase que implementa el manejo de roles y authorities durante la autenticación.
 * @author ludwingp
 *
 */
public class UserPrincipal implements UserDetails {

	private static final long serialVersionUID = 4085694078557675664L;
	
	UserEntity userEntity;

	public UserPrincipal(UserEntity userEntity) {
		// TODO Auto-generated constructor stub
		this.userEntity = userEntity;
	}

	/**
	 * Retorna la lista de roles y authorities asignados al usuario (ambos)
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		List<AuthorityEntity> authorityEntities = new ArrayList<>();
		
		// Obener roles de usuario
		Collection<RoleEntity> roles = userEntity.getRoles();
		
		if (roles == null) return authorities;
		
		// Recorrer la lista de roles asignados y agrega el nombre de cada uno a la
		// lista de authorities del usuario.
		roles.forEach((role) -> {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
			authorityEntities.addAll(role.getAuthorities());
		});
		
		// Recorrer todas las authorities asignadas al usuario (a través de los roles)
		// y agregarlas a la lista que se va a retornar.
		authorityEntities.forEach((authorityEntity) -> {
			authorities.add(new SimpleGrantedAuthority(authorityEntity.getName()));
		});
		
		return authorities;
		
	}

	@Override
	public String getPassword() {
		return userEntity.getEncryptedPassword();
	}

	@Override
	public String getUsername() {
		return this.userEntity.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.userEntity.getEmailVerificationStatus();
	}

}
