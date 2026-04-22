package riccardogulin.u5d12.tools;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import riccardogulin.u5d12.entities.User;

@Component
public class EmailSender {
	private final String domainName;
	private final String apiKey;

	public EmailSender(@Value("${mailgun.domainName}") String domainName, @Value("${mailgun.apiKey}") String apiKey) {
		this.domainName = domainName;
		this.apiKey = apiKey;
	}

	public void sendRegistrationEmail(User recipient) {
		HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domainName + "/messages")
				.basicAuth("api", this.apiKey)
				.queryString("from", "riccardo.gulin@gmail.com")
				.queryString("to", recipient.getEmail()) // <-- DEVE ESSERE IL DESTINATARIO VERIFICATO!
				.queryString("subject", "Benvenuto sulla nostra piattaforma!")
				.queryString("text", "Ciao " + recipient.getName() + ", la tua registrazione è andata a buon fine!")
				.asJson();

		System.out.println(response.getBody()); // <-- Consiglio di fare questo log per ispezionare la risposta dei server
		// di Mailgun per debuggare eventuali problemi
	}
}
