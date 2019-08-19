/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.report.Status;
import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import static io.cosmosoftware.kite.util.ReportUtils.getStackTrace;
import static io.cosmosoftware.kite.util.ReportUtils.timestamp;
import static io.cosmosoftware.kite.util.TestUtils.executeJsScript;

public class StopGetStatsStep extends TestStep {

  public StopGetStatsStep(Runner runner) {
    super(runner);
  }

  @Override
  public String stepDescription() { return "End of stats recovery"; }

  @Override
  public void step() throws KiteTestException {
    try {
      List<Map<String, Object>> statsOvertime = (List) executeJsScript(webDriver, getStopGetStatsDuringTestScript());
      JSONArray jsonArray = new JSONArray(statsOvertime);

      // Writing stats to file and report
      String resDir = System.getProperty("user.dir");
      File res = new File(resDir + "/results/charts/");
      res.mkdirs();
      BufferedWriter writer = new BufferedWriter(new FileWriter(res.toString() + "/" + timestamp() + "_" + this.getClientID() + "_data.json"));
      writer.write(jsonArray.toString(2));
      writer.close();
      reporter.textAttachment(this.report, "stats overtime", jsonArray.toString(2), "json");
      logger.debug("Stopped");
    } catch (Exception e) {
      logger.error(getStackTrace(e));
      throw new KiteTestException("Failed to stop stats recovery", Status.FAILED, e);
    }
  }
  
  private String getStopGetStatsDuringTestScript() {
    return "window.Running = false;"
      + "return window.StatsOvertime;";
  }
  
}
