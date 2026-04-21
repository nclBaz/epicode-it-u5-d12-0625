package riccardogulin.u5d12.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import riccardogulin.u5d12.entities.User;
import riccardogulin.u5d12.exceptions.NotFoundException;
import riccardogulin.u5d12.exceptions.UnauthorizedException;
import riccardogulin.u5d12.payloads.LoginDTO;
import riccardogulin.u5d12.security.TokenTools;

@Service
public class AuthService {
	private final UsersService usersService;
	private final TokenTools tokenTools;
	private final PasswordEncoder bcrypt;

	public AuthService(UsersService usersService, TokenTools tokenTools, PasswordEncoder bcrypt) {

		this.usersService = usersService;
		this.tokenTools = tokenTools;
		this.bcrypt = bcrypt;
	}

	public String checkCredentialsAndGenerateToken(LoginDTO body) {
		// 1. Controllo credenziali
		// 1.1 Controllo se esiste utente con quell'email
		try {
			User found = this.usersService.findByEmail(body.email());
			// 1.2 Controllo se password corrisponde
			if (this.bcrypt.matches(body.password(), found.getPassword())) {
				// 2. Se credenziali OK -> Generiamo Token e ritorniamolo
				return this.tokenTools.generateToken(found);

			} else {
				// 3. Altrimenti -> Error
				throw new UnauthorizedException("Credenziali errate");
			}
		} catch (NotFoundException ex) {
			throw new UnauthorizedException("Credenziali errate");
		}
	}
}
