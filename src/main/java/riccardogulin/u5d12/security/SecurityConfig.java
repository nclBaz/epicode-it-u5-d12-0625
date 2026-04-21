package riccardogulin.u5d12.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity // <-- Questa classe non sarà una normale classe di configurazione per Bean ma sarà fatta appositamente
// per customizzare le impostazioni di Spring Security
@EnableMethodSecurity // IMPORTANTE: Se voglio applicare le regole di autorizzazione sui vari endpoint tramite Annotazioni @PreAuthorize
// devo usare questa @EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
		// In questo Bean potrò configurare tutte le impostazioni di Spring Security

		// Posso disabilitare dei comportamenti di default che non mi servono
		httpSecurity.sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		// Siccome utilizzeremo JWT che è stateless, non useremo le sessioni
		httpSecurity.formLogin(formLogin -> formLogin.disable());

		httpSecurity.csrf(csrf -> csrf.disable()); // Disabilitiamo la protezione da attacchi CSRF perché non serve
		// nel caso di autenticazione tramite JWT, anzi ci complicherebbe la vita, anche lato FE.

		// Posso personalizzare il comportamento di funzionalità pre-esistenti
		httpSecurity.authorizeHttpRequests(req -> req.requestMatchers("/**").permitAll());
		// Siccome andremo ad implementare dei controlli di autenticazione custom, non deve intervenire Spring Security per
		// controllare l'autenticazione sulle nostre richieste, quindi sblocchiamo tutte le richieste ("/**" vuol dire su qualsiasi
		// URL di quest'applicazione)

		// Posso aggiungere anche ulteriori funzionalità custom

		httpSecurity.cors(Customizer.withDefaults()); // <--------------- OBBLIGATORIO SE VOGLIAMO USARE LA CONFIGURAZIONE CORS SOTTOSTANTE

		return httpSecurity.build();
	}

	@Bean
	public PasswordEncoder getBCrypt() {
		return new BCryptPasswordEncoder(12); // 12 è il numero di rounds. BCrypt eseguirà l'algoritmo 2^12 volte (4096 volte)
		// mettendoci il doppio del tempo rispetto 11 rounds. Più è lento e più sarà sicuro
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of("http://localhost:5173", "https://mywonderfulfe.com"));
		// Stiamo definendo una WHITELIST, una lista di indirizzi FRONTEND che voglio possano accedere a questo backend
		// senza incappare in problemi CORS
		// Volendo invece della lista potrei mettere '*' però questo sarebbe troppo poco restrittivo perché permetterebe
		// l'accesso a tutti (utile magari nel caso di API pubbliche)
		configuration.setAllowedMethods(List.of("*"));
		configuration.setAllowedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
