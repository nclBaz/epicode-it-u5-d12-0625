package riccardogulin.u5d12.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@ToString
@Getter
@Setter
@JsonIgnoreProperties({"accountNonExpired", "accountNonLocked", "authorities", "credentialsNonExpired", "enabled"})
public class User implements UserDetails {

	@Id
	@GeneratedValue
	@Setter(AccessLevel.NONE)
	private UUID userId;

	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String surname;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false)
	@JsonIgnore
	private String password;
	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;
	@Column(name = "avatar_url")
	private String avatarURL;
	@Enumerated(EnumType.STRING)
	private Role role;

	public User(String name, String surname, String email, String password, LocalDate dateOfBirth) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.dateOfBirth = dateOfBirth;
		this.avatarURL = "https://ui-avatars.com/api?name=" + name + "+" + surname;
		this.role = Role.USER; // Di default tutti quelli che si registrano sulla piattaforma saranno utenti semplici
		// Poi eventualmente un ADMIN o un SUPERADMIN avrà il potere di cambiare il ruolo
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Questo metodo dovrà restituire una collection di Authorities, cioè i RUOLI
		// (in altre applicazioni gli utenti potrebbero avere anche più di un ruolo alla volta)
		// SimpleGrantedAuthority implementa GrantedAuthority quindi ci consente di creare degli oggetti "ruolo"
		// compatibili con questa collection. Dobbiamo semplicemente passare il valore dell'enum al costruttore
		// di SimpleGrantedAuthority
		return List.of(new SimpleGrantedAuthority(this.role.name()));
	}

	@Override
	public String getUsername() {
		return this.email;
	}
}
