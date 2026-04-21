package riccardogulin.u5d12.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import riccardogulin.u5d12.entities.User;
import riccardogulin.u5d12.exceptions.UnauthorizedException;
import riccardogulin.u5d12.services.UsersService;

import java.io.IOException;
import java.util.UUID;

@Component
public class TokenFilter extends OncePerRequestFilter {

	private final TokenTools tokenTools;
	private final UsersService usersService;

	public TokenFilter(TokenTools tokenTools, UsersService usersService) {
		this.tokenTools = tokenTools;
		this.usersService = usersService;
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


		// ************************************************* AUTORIZAZZIONE ***********************************************

		// L'obbiettivo è associare l'utente che sta effettuando la richiesta alla richiesta stessa. In modo tale che quando
		// la richiesta arriverà agli endpoint avremo le informazioni che ci servono su quell'utente (es. ruoli dell'utente, ma anche
		// conoscere l'id di chi sta facendo la richiesta è fondamentale per certi tipi di regole di autorizzazione)
		// Questo mi può tornare utile non solo per controllare che solo gli admin accedano ad un certo endpoint, ma anche ad esempio
		// per controllare che un'operazione di lettura/modifica/cancellazione venga fatta solo dall'effettivo proprietario di quella risorsa
		// Inoltre, questo meccanismo, ci serve anche per associare durante la creazione di una nuova risorsa all'effettivo proprietario
		// di tale risorsa (non leggendo quindi l'id dal payload!)

		// 1. Cerchiamo l'utente nel db
		// 1.1 Estraiamo l'id dal token
		UUID userId = this.tokenTools.extractIdFromToken(accessToken);

		// 1.2 FindById
		User authenticatedUser = this.usersService.findById(userId);

		// 2. Associamo l'utente al Security Context
		Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// 3. Se tutto è OK -> Andiamo avanti con la catena (o un prossimo filtro o direttamente il controller)
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
