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
package io.cosmosoftware.kite.steps;

import static io.cosmosoftware.kite.action.JSActionScript.getIceConnectionStateScript;
import static io.cosmosoftware.kite.util.TestUtils.executeJsScript;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.pages.BasePage;
import io.cosmosoftware.kite.report.Status;
import javax.json.JsonObject;
import net.bytebuddy.implementation.bytecode.Throw;

public class PeerConnectionCheck extends TestCheck {
  private final JsonObject getStatsConfig;
  private final Runner runner;
  private BasePage page;

  public PeerConnectionCheck(Runner runner, JsonObject getStatsConfig, BasePage page) {
    super(runner);
    this.runner = runner;
    this.page = page;
    this.getStatsConfig = getStatsConfig;
  }
  
  @Override
  public String stepDescription() {
    return "Verify that the ICE connection state is 'connected'";
  }
  
  @Override
  protected void step() throws KiteTestException {
    if (!getStatsConfig.getJsonArray("peerConnections").isEmpty()) {
      String state = page.getIceConnectionState(
              getStatsConfig.getJsonArray("peerConnections").getString(0));
      if (!state.equalsIgnoreCase("connected") && !state.equalsIgnoreCase("completed")) {
        throw new KiteTestException("The ICE connection's state has changed to failed",
            Status.FAILED);
      }
    }
  }
}
