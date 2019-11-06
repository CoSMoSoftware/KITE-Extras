/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.usrmgmt;


import io.cosmosoftware.kite.config.KiteEntity;
import org.apache.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.persistence.Entity;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static io.cosmosoftware.kite.util.ReportUtils.getStackTrace;

/**
 * Entity implementation class for Entity: Grid.
 */
@Entity(name = Email.TABLE_NAME)
public class Email extends KiteEntity {


  private final Logger logger = Logger.getLogger(this.getClass().getName());

  /**
   * The Constant TABLE_NAME.
   */
  final static String TABLE_NAME = "emails";

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;


  /* Required */
  private String id= "";

  private String smtpServer;
  private Integer port;
  private String username;
  private String password;
  private String from;
  private List<String> to = new ArrayList<>();
  
  /* Optional */
  private String name;
  private String subject;
  private String text;
  private Boolean onlyOnFailure = false;
  private Boolean sendJsonResults = false;
  
  /**
   * Instantiates a new grid.
   */
  public Email() {
    super();
  }


  /**
   * Constructs a new App with the given remote address and JsonObject.
   *
   * @param jsonObject JsonObject
   */
  public Email(JsonObject jsonObject) {

    String missingKey = "";
    try {
      this.name = jsonObject.getString("name", "Default");
      missingKey = "smtpServer";
      this.smtpServer = jsonObject.getString(missingKey);
      missingKey = "port";
      this.port = jsonObject.getInt(missingKey);
      missingKey = "username";
      this.username = jsonObject.getString(missingKey);
      missingKey = "password";
      this.password = jsonObject.getString(missingKey);
      missingKey = "from";
      this.from = jsonObject.getString(missingKey);
      missingKey = "to";
      JsonArray jsonArray = jsonObject.getJsonArray(missingKey);
      for (int i = 0; i < jsonArray.size(); i++) {
        this.to.add(jsonArray.getString(i));
      }
      this.subject = jsonObject.getString("subject", "Email from KITE");
      this.text = jsonObject.getString(
          "text", "\r\nThis is an automatically generated email from KITE.\r\n\r\n");
      this.onlyOnFailure = jsonObject.getBoolean("onlyOnFailure", onlyOnFailure);
      this.sendJsonResults = jsonObject.getBoolean("sendJsonResults", sendJsonResults);
      
    } catch (NullPointerException e) {
      logger.error("Error in EmailSender json config, the key " + missingKey + " is missing.\r\n" + getStackTrace(e));
      throw e;
    }
  }
  
  
  /**
   * Gets the id.
   *
   * @return the id
   */
  @Id
  // @GeneratedValue(generator = "uuid")
  // @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = Email.TABLE_NAME)
  @GenericGenerator(name = Email.TABLE_NAME, strategy = "io.cosmosoftware.kite.dao.KiteIdGenerator", parameters = {
    @Parameter(name = "prefix", value = "EMAI")
  })
  public String getId() {
    return this.id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }


  /**
   * Gets the smptServer
   *
   * @return the smptServer
   */
  public String getSmtpServer() {
    return smtpServer;
  }

  /**
   * Sets the smtpServer
   * 
   * @param smtpServer the smptServer
   */
  public void setSmtpServer(String smtpServer) {
    this.smtpServer = smtpServer;
  }

  /**
   * Gets the port
   * 
   * @return the port
   */
  public Integer getPort() {
    return port;
  }

  /**
   * Sets the port
   * 
   * @param port the port
   */
  public void setPort(Integer port) {
    this.port = port;
  }

  /**
   * Gets the username
   * 
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username
   * 
   * @param username the username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets the password
   * 
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password
   * 
   * @param password the password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets the from
   * 
   * @return the from
   */
  public String getFrom() {
    return from;
  }

  /**
   * Sets the from
   * 
   * @param from the from
   */
  public void setFrom(String from) {
    this.from = from;
  }

  /**
   * Gets the to
   * 
   * @return the to
   */
  @Column
  @ElementCollection(fetch = FetchType.EAGER)
  public List<String> getTo() {
    return to;
  }

  /**
   * Sets the to
   * 
   * @param to the to
   */
  public void setTo(List<String> to) {
    this.to = to;
  }

  /**
   * Gets the name
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name
   * 
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the subject
   * 
   * @return the subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Sets the subject
   * 
   * @param subject the subject
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Gets the text
   * 
   * @return the text 
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text
   * 
   * @param text the text
   */
  public void setText(String text) {
    this.text = text;
  }


  public Boolean getOnlyOnFailure() {
    return onlyOnFailure != null ? onlyOnFailure : false;
  }

  public void setOnlyOnFailure(Boolean onlyOnFailure) {
    this.onlyOnFailure = onlyOnFailure;
  }

  public Boolean getSendJsonResults() {
    return sendJsonResults != null ? sendJsonResults : false;
  }

  public void setSendJsonResults(Boolean sendJsonResults) {
    this.sendJsonResults = sendJsonResults;
  }
  
  
  @Transient
  public String getToAsString() {
    String toStr = "";
    for (int i=0; i < to.size();i++) {
      toStr += to.get(i) + ( i < to.size() - 1 ? "," : "");
    }
    return toStr;
  }
  
  
  
  
}
