package dev.ludwing.mobileappws.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;

import dev.ludwing.mobileappws.shared.dto.UserDto;

public class AmazonEmailService {

	final String FROM = "alianzakemomtzij@gmail.com";
	
	final String SUBJECT = "Verificar email";
	
	final String HTMLBODY = "<h1>Verifica tu email</h1><p> <a href='http://localhost:8080/verification-service/email-verification.html?token=$tokenValue'>aquí</a> </p>";
	
	final String TEXTBODY = "Verifica tu email aqui: http://localhost:8080/verification-service/email-verification.html?token=$tokenValue";
	
	final String PASS_RESET_SUBJECT = "Solicitud de restrablecer contraseña";
	
	final String PASS_RESET_HTMLBODY = "<h1>Solicitud para regenerar su contraseña</h1> <p> Hola $firstName, puedes regenerar tu contraseña aqui: <a href='http://localhost:8080/verification-service/password-reset.html?token=$tokenValue'>Aquí</a> </p> <p>Gracias!</p>";
	
	final String PASS_RESET_TEXTBODY = "Hola $firstName, puedes regenerar tu contraseña aqui http://localhost:8080/verification-service/password-reset.html?token=$tokenValue Gracias!";
	
	public void verifyEmail(UserDto user) {
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
				.standard()
				.withRegion(Regions.US_EAST_2) // ohio
				.build();
		
		String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", user.getEmailVerificationToken());
		String textBodyWithToken = TEXTBODY.replace("$tokenValue", user.getEmailVerificationToken());
		
		SendEmailRequest request = new SendEmailRequest()
				.withDestination(new Destination().withToAddresses(user.getEmail()))
				.withMessage(new Message()
						.withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
						.withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
						.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
				.withSource(FROM);
		
		client.sendEmail(request);
		
		System.out.println("Email sent to " + user.getEmail());
	}
	
	public boolean sendPasswordResetRequest(String firstName, String email, String token) {
		boolean returnValue = false;
		
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
				.standard()
				.withRegion(Regions.US_EAST_2) // ohio
				.build();
		
		String htmlBodyWithToken = PASS_RESET_HTMLBODY.replace("$tokenValue", token);
		String textBodyWithToken = PASS_RESET_TEXTBODY.replace("$tokenValue", token);
		
		htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstName);
		textBodyWithToken = textBodyWithToken.replace("$firstName", firstName);
		
		SendEmailRequest request = new SendEmailRequest()
				.withDestination(new Destination().withToAddresses(email))
				.withMessage(new Message()
						.withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
						.withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
						.withSubject(new Content().withCharset("UTF-8").withData(PASS_RESET_SUBJECT)))
				.withSource(FROM);
		
		System.out.println(textBodyWithToken);
		
		// SendEmailResult result = client.sendEmail(request);
		
		//if (result != null && (result.getMessageId() != null && !result.getMessageId().isEmpty())) {
		//	returnValue = true;
		//}
		
		returnValue = true;
		
		return returnValue;
	}
}
