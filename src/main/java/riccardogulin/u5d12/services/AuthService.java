package riccardogulin.u5d12.services;

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

	public AuthService(UsersService usersService, TokenTools tokenTools) {

		this.usersService = usersService;
		this.tokenTools = tokenTools;
	}

	public String checkCredentialsAndGenerateToken(LoginDTO body) {
		// 1. Controllo credenziali
		// 1.1 Controllo se esiste utente con quell'email
		try {
			User found = this.usersService.findByEmail(body.email());
			// 1.2 Controllo se password corrisponde
			// TODO: Migliorare gestione password
			if (found.getPassword().equals(body.password())) {
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
