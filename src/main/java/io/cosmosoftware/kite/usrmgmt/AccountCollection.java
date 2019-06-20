/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.usrmgmt;

import io.cosmosoftware.kite.report.KiteLogger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Rc account collection.
 */
public class AccountCollection extends Entity {
  
  
  private final Map<AccountType, List<Account>> accountMap = new HashMap<>();
  private final KiteLogger logger = KiteLogger.getLogger(this.getClass().getName());
  private final int timeout;
  
  /**
   * Instantiates a new Rc account collection.
   *
   * @param jsonObject the json object
   */
  public AccountCollection(JsonObject jsonObject) {
    this.timeout = jsonObject.getInt("timeout");
    List<Account> accountList;
    
    JsonArray jsonArray;
    for (AccountType accountType : AccountType.values()) {
      jsonArray = jsonObject.getJsonArray(accountType.name());
      if (jsonArray != null) {
        accountList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
          accountList.add(new Account(jsonArray.getJsonObject(i)));
        }
        accountMap.put(accountType, accountList);
      }
    }
  }
  
  /**
   * Gets account list.
   *
   * @param type the type
   *
   * @return the account list
   */
  public List<Account> getAccountList(AccountType type) {
    return this.accountMap.get(type);
  }
  
  /**
   * Gets account map.
   *
   * @return the account map
   */
  public Map<AccountType, List<Account>> getAccountMap() {
    return accountMap;
  }
  
  /**
   * Gets timeout.
   *
   * @return the timeout
   */
  public int getTimeout() {
    return timeout;
  }
  
  /**
   * Sets account list.
   *
   * @param type        the type
   * @param accountList the account list
   */
  public void setAccountList(AccountType type, List<Account> accountList) {
    this.accountMap.put(type, accountList);
  }
  
}
