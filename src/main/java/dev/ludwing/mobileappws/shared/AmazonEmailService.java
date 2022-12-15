package dev.ludwing.mobileappws.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

import dev.ludwing.mobileappws.shared.dto.UserDto;

public class AmazonEmailService {

	final String FROM = "alianzakemomtzij@gmail.com";
	
	final String SUBJECT = "Verificar email";
	
	final String HTMLBODY = "<h1>Verifica tu email</h1><p> <a href='http://localhost:8080/verification-service/email-verification.html?token=$tokenValue'>aquí</a> </p>";
	
	final String TEXTBODY = "Verifica tu email aqui: http://localhost:8080/verification-service/email-verification.html?token=$tokenValue";
	
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
}
