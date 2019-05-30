/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.usrmgmt;

import javax.json.JsonObject;
import java.util.Objects;

/**
 * The type Rc type role.
 */
public class TypeRole extends Entity {
  
  private AccountRole role = AccountRole.CALLEE;
  private AccountType type;
  
  /**
   * Instantiates a new Rc type role.
   */
  public TypeRole() {
  
  }
  
  
  /**
   * Instantiates a new Rc type role.
   *
   * @param jsonObject the json object
   */
  public TypeRole(JsonObject jsonObject) {
    this.type = AccountType.valueOf(jsonObject.getString("type"));
    this.role = AccountRole.valueOf(jsonObject.getString("role"));
  }
  
  /**
   * Instantiates a new Rc type role.
   *
   * @param typeRole the type role
   */
  public TypeRole(TypeRole typeRole) {
    this.type = typeRole.type;
    this.role = typeRole.role;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TypeRole that = (TypeRole) o;
    return type == that.type;
  }
  
  /**
   * Gets role.
   *
   * @return the role
   */
  public AccountRole getRole() {
    return role;
  }
  
  /**
   * Sets role.
   *
   * @param role the role
   */
  public void setRole(AccountRole role) {
    this.role = role;
  }
  
  /**
   * Gets short name.
   *
   * @return a shortname for this Typerole
   */
  public String getShortName() {
    String str = "";
    switch (type) {
      case FREE_ACCOUNT:
        str += "FREE";
        break;
      case RC_ACCOUNT_A:
        str += "RC_A";
        break;
      case RC_ACCOUNT_B:
        str += "RC_B";
        break;
      case RC_ACCOUNT_C:
        str += "RC_C";
        break;
    }
    return getRole() + "_" + str;
  }
  
  /**
   * Gets type.
   *
   * @return the type
   */
  public AccountType getType() {
    return type;
  }
  
  /**
   * Sets type.
   *
   * @param type the type
   */
  public void setType(AccountType type) {
    this.type = type;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(type);
  }
  
}
