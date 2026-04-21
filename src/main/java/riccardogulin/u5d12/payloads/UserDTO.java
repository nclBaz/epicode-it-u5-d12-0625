package riccardogulin.u5d12.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserDTO(
		@NotBlank(message = "Il nome proprio è obbligatorio e non può essere una stringa vuota")
		@Size(min = 2, max = 30, message = "Il nome proprio deve essere compreso tra i 2 e i 30 caratteri")
		String name,
		@NotBlank(message = "Il cognome è obbligatorio e non può essere una stringa vuota")
		@Size(min = 2, max = 30, message = "Il cognome deve essere compreso tra i 2 e i 30 caratteri")
		String surname,
		@NotBlank(message = "L'email è obbligatoria e non può essere una stringa vuota")
		@Email(message = "L'email inserita non è nel formato corretto")
		String email,
		@NotBlank(message = "La password è obbligatoria")
		@Size(min = 4, message = "La password deve avere almeno 4 caratteri")
		// @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{4,}$", message = "La password deve contenere almeno una maiuscola, una minuscola,....")
		String password,
		@Past
		LocalDate dateOfBirth) {
}
