package riccardogulin.u5d12.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import riccardogulin.u5d12.entities.User;
import riccardogulin.u5d12.exceptions.ValidationException;
import riccardogulin.u5d12.payloads.LoginDTO;
import riccardogulin.u5d12.payloads.LoginRespDTO;
import riccardogulin.u5d12.payloads.NewUserRespDTO;
import riccardogulin.u5d12.payloads.UserDTO;
import riccardogulin.u5d12.services.AuthService;
import riccardogulin.u5d12.services.UsersService;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;
	private final UsersService usersService;

	public AuthController(AuthService authService, UsersService usersService) {

		this.authService = authService;
		this.usersService = usersService;
	}

	@PostMapping("/login")
	public LoginRespDTO login(@RequestBody LoginDTO body) {
		return new LoginRespDTO(this.authService.checkCredentialsAndGenerateToken(body));
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED) // 201
	public NewUserRespDTO saveUser(@RequestBody @Validated UserDTO body, BindingResult validationResult) {

		if (validationResult.hasErrors()) {
			List<String> errors = validationResult.getFieldErrors().stream().map(error -> error.getDefaultMessage()).toList();
			throw new ValidationException(errors);
		}

		User newUser = this.usersService.save(body);
		return new NewUserRespDTO(newUser.getUserId());
	}

}
