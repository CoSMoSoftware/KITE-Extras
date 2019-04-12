/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.usrmgmt;

import io.cosmosoftware.kite.pool.BlockingPool;
import io.cosmosoftware.kite.pool.PoolFactory;
import io.cosmosoftware.kite.pool.TimeElapsedException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Singleton class for managing the accounts.
 */
public class AccountManager {
  
  private static AccountManager manager = new AccountManager();
  private AccountCollection accountCollection;
  private Map<AccountType, BlockingPool<Account>> accountPoolMap =
    new HashMap<AccountType, BlockingPool<Account>>();
  
  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static AccountManager getInstance() {
    return manager;
  }
  
  /**
   * Init rc account manager.
   *
   * @param accountCollection the account collection
   *
   * @return the rc account manager
   */
  public AccountManager init(AccountCollection accountCollection) {
    this.accountCollection = accountCollection;
    
    Iterator<Map.Entry<AccountType, List<Account>>> iterator =
      this.accountCollection.getAccountMap().entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<AccountType, List<Account>> mapEntry = iterator.next();
      List<Account> accountList = mapEntry.getValue();
      if (accountList.size() > 0) {
        this.accountPoolMap.put(mapEntry.getKey(), (BlockingPool<Account>) PoolFactory
          .newBoundedBlockingPool(accountList.size(), new AccountFactory(accountList),
            new AccountValidator()));
      }
    }
    
    return this;
  }
  
  /**
   * Release account.
   *
   * @param account the account
   */
  public synchronized void releaseAccount(Account account) {
    if (this.accountPoolMap.get(account.getAccountType()) != null) {
      this.accountPoolMap.get(account.getAccountType()).release(account);
    }
  }
  
  /**
   * Retain account rc account.
   *
   * @param type the type
   *
   * @return the rc account
   * @throws TimeElapsedException the time elapsed exception
   * @throws InterruptedException the interrupted exception
   */
  public synchronized Account retainAccount(AccountType type)
    throws TimeElapsedException, InterruptedException {
    return this.accountPoolMap.get(type).get(this.accountCollection.getTimeout(), TimeUnit.MINUTES);
  }
  
}
