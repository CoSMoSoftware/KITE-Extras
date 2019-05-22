/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cosmosoftware.kite.exception;

import io.cosmosoftware.kite.manager.SSHManager;

/**
 * The type Ssh manager exception.
 */
public class SSHManagerException extends Exception {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The command runner.
   */
  private SSHManager sshManager;

  /**
   * The exception.
   */
  private Exception exception;
  
  /**
   * Instantiates a new Ssh manager exception.
   *
   * @param sshManager the command runner
   * @param exception  the exception
   */
  public SSHManagerException(SSHManager sshManager, Exception exception) {
    this.sshManager = sshManager;
    this.exception = exception;
  }
  
  /**
   * Gets command runner.
   *
   * @return the command runner
   */
  public SSHManager getSSHManager() {
    return sshManager;
  }
  
  /**
   * Gets exception.
   *
   * @return the exception
   */
  public Exception getException() {
    return exception;
  }

}
