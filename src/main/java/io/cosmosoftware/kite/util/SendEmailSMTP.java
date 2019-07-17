package io.cosmosoftware.kite.util;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.KiteLogger;
import io.cosmosoftware.kite.report.Status;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static io.cosmosoftware.kite.util.ReportUtils.getStackTrace;

public class SendEmailSMTP {
  
  private final KiteLogger logger = KiteLogger.getLogger(this.getClass().getName());

  private final String smtpServer;
  private final String port;
  private final String username;
  private final String password;
  private final String from;
  private final String to;
  private final String subject;
  private final String text;


  public SendEmailSMTP(JsonValue jsonValue) throws KiteTestException {
    String missingKey = "";
    JsonObject jsonObject = (jsonValue.getValueType() == JsonValue.ValueType.STRING) 
      ? TestUtils.readJsonFile(((JsonString)jsonValue).getString()) : jsonValue.asJsonObject();    
    
    try {
      missingKey = "smtpServer";
      this.smtpServer = jsonObject.getString(missingKey);
      missingKey = "port";
      this.port = jsonObject.getString(missingKey);      
      missingKey = "username";
      this.username = jsonObject.getString(missingKey);
      missingKey = "password";
      this.password = jsonObject.getString(missingKey);
      missingKey = "from";
      this.from = jsonObject.getString(missingKey);
      missingKey = "to";
      this.to = jsonObject.getString(missingKey);

      this.subject = jsonObject.getString("subject", "Email from KITE");
      this.text =
          jsonObject.getString(
              "text", "\r\nThis is an automatically generated email from KITE.\r\n\r\n");

    } catch (NullPointerException e) {
      throw new KiteTestException(
          "Error in SendEmailSMTP json config, the key " + missingKey + " is missing.",
          Status.BROKEN,
          e);
    }
  }

  /**
   * Sends an email with the subject and text from the config file.
   */
  public void send() {
    send(subject, text);
  }

  /**
   * Sends an email with the subject from the config and the text emailText
   * 
   * @param emailText the text of the email.
   */
  public void send(String emailText) {
    send(subject, this.text + emailText);
  }


  /**
   * Sends an email with the subject emailSubject and the text emailText
   *
   * @param emailSubject the subject of the email.
   * @param emailText the text of the email.
   */
  public void send(String emailSubject, String emailText) {

    Properties prop = System.getProperties();
    prop.put("mail.smtp.host", smtpServer); // optional, defined in SMTPTransport
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.port", port);
    prop.put("mail.debug", "true");
    prop.put("mail.smtp.socketFactory.port", port);
    prop.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
    prop.put("mail.smtp.socketFactory.fallback", "false");
    prop.put("mail.smtp.starttls.enable", "true");
    
    Session session = Session.getDefaultInstance(prop,
      new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(username, password);
        }
      });

    Message msg = new MimeMessage(session);

    try {

      msg.setFrom(new InternetAddress(from));
      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
      msg.setSubject(emailSubject);
      msg.setText(emailText);
      msg.setSentDate(new Date());
      Transport t = session.getTransport("smtps");
      t.connect(smtpServer, Integer.parseInt(port), username, password);
      t.sendMessage(msg, msg.getAllRecipients());
      t.close();
    } catch (MessagingException e) {
      logger.error(getStackTrace(e));
    }
  }  
  
  @Override
  public String toString() {
    return "smtpServer = " + smtpServer;
  }
  
}
