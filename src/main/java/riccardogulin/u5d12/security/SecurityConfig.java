package riccardogulin.u5d12.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

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

		return httpSecurity.build();
	}
}
