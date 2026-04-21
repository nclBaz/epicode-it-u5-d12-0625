package riccardogulin.u5d12.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import riccardogulin.u5d12.exceptions.UnauthorizedException;

import java.io.IOException;

@Component
public class TokenFilter extends OncePerRequestFilter {

	private final TokenTools tokenTools;

	public TokenFilter(TokenTools tokenTools) {
		this.tokenTools = tokenTools;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		// Questo metodo viene eseguito ad ogni richiesta
		// Sarà quindi questo metodo che sarà responsabile del controllo token

		// 1. Verifichiamo se la richiesta contiene l'header Authorization e questo deve contenere il token nel formato
		// "Bearer eyJhbGciOiJIUzM4NCJ9.eyJpYXQiOjE3NzY2ODEwOTYsImV4cCI6MTc3NzI4NTg5Niwic3ViIjoiMTc3ZTc1M2EtMjg5Ny00MjY2LTg3ZmQtNjhhMDg5MjAxOTU0In0.OqTlCJrqWm-_R-L4xW5xIF8vPbCinabOGbSKdNOTGM7kZIuOMmEORar1u73UZHiX"
		// Se Auth header non c'è oppure se il suo valore non è nel formato giusto --> Errore
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer "))
			throw new UnauthorizedException("Inserire il token nell'authorization header nel formato corretto");

		// 2. Estraiamo il token dall'header
		// authHeader = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJpYXQiOjE3NzY2ODEwOTYsImV4cCI6MTc3NzI4NTg5Niwic3ViIjoiMTc3ZTc1M2EtMjg5Ny00MjY2LTg3ZmQtNjhhMDg5MjAxOTU0In0.OqTlCJrqWm-_R-L4xW5xIF8vPbCinabOGbSKdNOTGM7kZIuOMmEORar1u73UZHiX"
		String accessToken = authHeader.replace("Bearer ", "");

		// 3. Verifichiamo che il token sia OK (verifichiamo la firma e che non sia scaduto), se c'è qualche problema --> Errore
		tokenTools.verifyToken(accessToken);

		// 4. Se tutto è OK -> Andiamo avanti con la catena (o un prossimo filtro o direttamente il controller)
		filterChain.doFilter(request, response);
	}

	@Override
	// Tramite l'Override del metodo shouldNotFilter vado a specificare in quali casi non debba venir chiamato il nostro filtro custom
	// Qua posso dirgli ad esempio di non filtrare richieste di login o di registrazione
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// /auth/login, /users, /users/{userId}

		// return request.getServletPath().equals("/auth/login") ||  request.getServletPath().equals("/auth/register");

		return new AntPathMatcher().match("/auth/**", request.getServletPath());
		// non controllare i token per tutte le richieste rivolte al controller /auth

	}
}
