package dev.ludwing.mobileappws.io.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

/**
 * Esta clase define la tabla que se usará en la base de datos y sus campos.
 * 
 * El nombre de la tabla será "users".
 * 
 * @author ludwingp
 *
 */
@Entity
@Table(name="users")
public class UserEntity implements Serializable {

	private static final long serialVersionUID = -8769262141680012810L;

	// id es un campo numérico autogenerado
	@Id
	@GeneratedValue
	private long id;

	@Column(nullable = false)
	private String userId;

	// Los campos firstName, lastName y email definen un tamaño máximo para
	// que no usen el máximo de caracteres posibles (255).
	@Column(nullable = false, length = 50)
	private String firstName;

	@Column(nullable = false, length = 50)
	private String lastName;

	@Column(nullable = false, length = 120)
	private String email;

	// el campo encryptedPassword no tiene límite de caracteres porque eso
	// depende del algorítmo de cifrado.
	@Column(nullable = false)
	private String encryptedPassword;

	// emailVerificationToken no será obligatorio
	private String emailVerificationToken;

	// Para que el valor por defecto al crear el registro en la base de datos
	// sea false, la propiedad se iguala a false.
	@Column(nullable = false)
	private Boolean emailVerificationStatus = false;
	
	// mappedBy: Debe tener el nombre del campo (en la clase/tabla hija) que hace
	// 	referencia al objeto padre, es decir a esta misma clase.
	// cascade: Indica que los cambios se propagan hacia clases hijas (tablas relacionadas).
	// 	por ejemplo, cuando se guarda un usuario, también se guarda la lista de direcciones
	//  asociadas a ese usuario.
	@OneToMany(mappedBy="userDetails", cascade=CascadeType.ALL)
	private List<AddressEntity> addresses;
	
	// Aqui se define una relación Muchos a Muchos con una tabla de roles.
	// Esta es la definición de la tabla intermedia entre User y Rol.
	@ManyToMany(cascade= {CascadeType.PERSIST}, fetch=FetchType.EAGER)
	@JoinTable(name="users_roles", 
			joinColumns=@JoinColumn(name="users_id", referencedColumnName="id"), 
			inverseJoinColumns=@JoinColumn(name="roles_id", referencedColumnName="id"))
	private Collection<RoleEntity> roles;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getEmailVerificationToken() {
		return emailVerificationToken;
	}

	public void setEmailVerificationToken(String emailVerificationToken) {
		this.emailVerificationToken = emailVerificationToken;
	}

	public Boolean getEmailVerificationStatus() {
		return emailVerificationStatus;
	}

	public void setEmailVerificationStatus(Boolean emailVerificationStatus) {
		this.emailVerificationStatus = emailVerificationStatus;
	}

	public List<AddressEntity> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressEntity> addresses) {
		this.addresses = addresses;
	}

	public Collection<RoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(Collection<RoleEntity> roles) {
		this.roles = roles;
	}

	
}
