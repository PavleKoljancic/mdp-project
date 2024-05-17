package service;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class MailService {
	
	private Properties authProperties;
	private Properties mailProperties;
	private Logger logger;
	public MailService() throws FileNotFoundException, IOException 
	{	
		
		logger = Logger.getLogger("OrderApp Logger");
		try {
			logger.addHandler(new FileHandler("Main.log"));
		} catch (SecurityException | IOException einner) {
			logger.log(Level.WARNING, "Failed to add handler.",einner);
			
		} 
		authProperties = new Properties();
		authProperties.load(new FileInputStream("mail/mail.auth"));
		mailProperties = new Properties();
	    mailProperties.load(new FileInputStream("mail/mail.propeties"));
	    

	}
	
	public boolean sendMail(String reciverEmail,String subject, String body) 
	{	
		try {
	    Session session = Session.getInstance(mailProperties,
	            new javax.mail.Authenticator() {
	                protected PasswordAuthentication getPasswordAuthentication() {
	                    return new PasswordAuthentication(authProperties.getProperty("username"), authProperties.getProperty("appPassword"));
	                }
	            });
		Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("from@gmail.com"));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(reciverEmail)
        );
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
        return true;
		}
		catch (MessagingException e) {
			logger.log(Level.INFO, "Exception sending mail.....",e);
		}
        return false;
	}
	
	
}
