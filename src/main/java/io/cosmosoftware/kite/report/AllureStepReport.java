/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import io.cosmosoftware.kite.steps.StepPhase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;


/**
 * This class is a wrapper of the Allure's StepResult.
 */
public class AllureStepReport extends ReportEntity {

  private final List<CustomAttachment> attachments;
  private final List<AllureStepReport> steps;
  private String description = "N/C";
  private String clientId = "N/C";
  private StatusDetails details;
  private boolean ignore = false;
  private ParamList parameters;
  private Status status = Status.PASSED;
  private StepPhase phase;

  /**
   * Instantiates a new AllureStepReport report.
   *
   * @param clientId the id of the client's webdriver
   * @param description the description for this report
   */
  public AllureStepReport(String clientId, String description) {
    super((clientId == null ? "" : (clientId + ": ")) + description);
    this.clientId = clientId;
    this.description = description;
    this.attachments = Collections.synchronizedList(new ArrayList<>());
    this.steps = Collections.synchronizedList(new ArrayList<>());
    this.parameters = new ParamList();
  }

  /**
   * Instantiates a new AllureStepReport report.
   *
   * @param description the description for this report
   */
  public AllureStepReport(String description) {
    this(null, description);
  }

  public synchronized void addAttachment(CustomAttachment attachment) {
    this.attachments.add(attachment);
  }

  public String getClientId() {
    return clientId;
  }

  public void addParam(String name, String value) {
    this.parameters.put(name, value);
  }

  public synchronized void addStepReport(AllureStepReport step) {
    this.steps.add(step);
    // in theory, this is only tru after the steps that can be ignored happen
    // and will be fault if the successor step can't be ignore
    this.ignore = step.canBeIgnore();
    if (this.status.equals(Status.PASSED) && !step.getStatus().equals(Status.SKIPPED)) {
      // prevent overwriting failed/broken status
      // step should not has status "skipped" if sub steps gets skipped on failure
      this.status = step.getStatus();
    } else {
      if (step.status.equals(Status.PASSED)) {
        this.status = step.getStatus();
      }
    }
  }

  public boolean broken() {
    return this.status.equals(Status.BROKEN);
  }

  public boolean canBeIgnore() {
    return this.ignore;
  }

  public boolean failed() {
    return this.status.equals(Status.FAILED);
  }

  protected Status getActualStatus() {
    synchronized (steps) {
      for (AllureStepReport stepReport : this.steps) {
        Status temp = stepReport.getStatus();
        if (temp.equals(Status.FAILED) || temp.equals(Status.BROKEN)) {
          return temp;
        }
      }
    }
    return this.status;
  }

  public List<CustomAttachment> getAttachments() {
    return attachments;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public StatusDetails getDetails() {
    return details;
  }

  public void setDetails(StatusDetails details) {
    this.details = details;
    // reporter.textAttachment(this, "statusDetail", details.toJson().toString(), "json");
  }

  /**
   * Set the phase for this report
   * @param phase the phase that the step is in.
   */
  public void setPhase(StepPhase phase) {
    this.phase = phase;
  }

  public StepPhase getPhase() {
    return phase;
  }

  @Override
  public JsonObjectBuilder getJsonBuilder() {
    this.status = getActualStatus();
    JsonArrayBuilder stepsArray = Json.createArrayBuilder();
    if (steps.size() > 0) {
      synchronized (steps) {
        for (AllureStepReport stepReport : this.steps) {
          stepsArray.add(stepReport.toJson());
        }
      }
    }

    JsonArrayBuilder attArray = Json.createArrayBuilder();
    if (attachments.size() > 0) {
      synchronized (attachments) {
        for (CustomAttachment attachment : attachments) {
          attArray.add(attachment.toJson());
        }
      }
    }

    JsonObjectBuilder builder = super.getJsonBuilder()
        .add("description", this.description)
        .add("stage", this.stage)
        .add("status", this.status.toString())
        .add("parameters", parameters.toJson())
        .add("steps", stepsArray)
        .add("attachments", attArray);

    if (details != null) {
      builder.add("statusDetails", details.toJson());
    }

    return builder;
  }

  public String getName() {
    return name;
  }

  public long getStart() {
    return start;
  }

  public Status getStatus() {
    return status;
  }

  public synchronized void setStatus(Status status) {
    this.status = status;
    this.setStopTimestamp();
  }

  public List<AllureStepReport> getSteps() {
    return steps;
  }

  public long getStop() {
    return stop;
  }

  public synchronized void setIgnore(boolean ignore) {
    this.ignore = ignore;
  }

}
