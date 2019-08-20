package io.cosmosoftware.kite.usrmgmt;

import io.cosmosoftware.kite.report.KiteLogger;
import io.cosmosoftware.kite.util.TestUtils;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static io.cosmosoftware.kite.util.ReportUtils.getStackTrace;

public class EmailSender {
  
  private final KiteLogger logger = KiteLogger.getLogger(this.getClass().getName());

  private final Email email;

  public EmailSender(JsonValue jsonValue) {    
    JsonObject jsonObject = (jsonValue.getValueType() == JsonValue.ValueType.STRING) 
      ? TestUtils.readJsonFile(((JsonString)jsonValue).getString()) : jsonValue.asJsonObject();
    this.email = new Email(jsonObject);
  }
  
  public EmailSender(Email email) {
    this.email = email;
  }

  /**
   * Sends an email with the subject and text from the config file.
   */
  public void send() {
    send(email.getSubject(), email.getText());
  }

  /**
   * Sends an email with the subject from the config and the text emailText
   * 
   * @param emailText the text of the email.
   */
  public void send(String emailText) {
    send(email.getSubject(), email.getText() + emailText);
  }


  /**
   * Sends an email with the subject emailSubject and the text emailText
   *
   * @param emailSubject the subject of the email.
   * @param emailText the text of the email.
   */
  public void send(String emailSubject, String emailText) {

    Properties prop = System.getProperties();
    prop.put("mail.smtp.host", email.getSmtpServer()); // optional, defined in SMTPTransport
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.port", email.getPort());
    prop.put("mail.debug", "true");
    prop.put("mail.smtp.socketFactory.port", email.getPort());
    prop.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
    prop.put("mail.smtp.socketFactory.fallback", "false");
    prop.put("mail.smtp.starttls.enable", "true");
    
    Session session = Session.getDefaultInstance(prop,
      new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(email.getUsername(), email.getPassword());
        }
      });

    Message msg = new MimeMessage(session);

    try {

      msg.setFrom(new InternetAddress(email.getFrom()));
      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getToAsString(), false));
      msg.setSubject(emailSubject);
      msg.setText(emailText);
      msg.setSentDate(new Date());
      Transport t = session.getTransport("smtps");
      t.connect(email.getSmtpServer(), email.getPort(), email.getUsername(), email.getPassword());
      t.sendMessage(msg, msg.getAllRecipients());
      t.close();
      logger.info("Email sent to " + email.getToAsString());
    } catch (MessagingException e) {
      logger.error(getStackTrace(e));
    }
  }  
  
  @Override
  public String toString() {
    return "smtpServer = " + email.getSmtpServer();
  }
  
}
