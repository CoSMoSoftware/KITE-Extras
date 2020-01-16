/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import io.cosmosoftware.kite.steps.StepPhase;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This class is a wrapper of the Allure's StepResult.
 */
public class AllureStepReport extends ReportEntity {

  private final List<CustomAttachment> attachments;
  private AllureStepReport parent;
  private String description = "N/C";
  private String clientId = "N/C";
  private boolean ignore = false;
  private ParamList parameters;
  private StepPhase phase;
  private boolean failedRegistered = false;
  protected Status status = Status.PASSED;
  protected StatusDetails details;
  protected final List<AllureStepReport> steps;

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
    this.details = defaultStatusDetail();
  }

  /**
   * Instantiates a new AllureStepReport report.
   *
   * @param description the description for this report
   */
  public AllureStepReport(String description) {
    super(description);
    this.clientId = null;
    this.description = description;
    this.attachments = Collections.synchronizedList(new ArrayList<>());
    this.steps = Collections.synchronizedList(new ArrayList<>());
    this.parameters = new ParamList();
    this.details = defaultStatusDetail();
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
    step.setParent(this);
    this.steps.add(step);
    // in theory, this is only tru after the steps that can be ignored happen
    // and will be fault if the successor step can't be ignore
    if (!step.canBeIgnore()) {
      if (this.status.equals(Status.PASSED) && !step.getStatus().equals(Status.SKIPPED)) {
        // prevent overwriting failed/broken status
        // step should not has status "skipped" if sub steps gets skipped on failure
        this.status = step.getStatus();
        if (step.getDetails() != null) {
          this.setDetails(step.getDetails());
        }
      } else {
        if (step.status.equals(Status.PASSED)) {
          this.status = step.getStatus();
        }
      }
    }
  }

  public void setParent(AllureStepReport parent) {
    this.parent = parent;
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

  public Status getActualStatus() {
    if (getFirstFailedSubStep() != null) {
      return getFirstFailedSubStep().getStatus();
    }
    return this.status;
  }

  public StatusDetails getActualDetail() {
    if (getFirstFailedSubStep() != null) {
      return getFirstFailedSubStep().getDetails();
    }
    return this.details;
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
    // prevent overwriting details
    if (this.details == null || this.details.getCode() == 0) {
      this.details = details;
      if (this.parent != null) {
        this.parent.setDetails(details);
      }
    }
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
      builder.add("statusDetails", getActualDetail().toJson());
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
    if (this.parent != null && !status.equals(Status.SKIPPED) && !this.canBeIgnore()) {
      if (! (this.parent instanceof AllureTestReport)) {
        this.parent.setStatus(status);
      }
    }
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

  protected StatusDetails defaultStatusDetail() {
    StatusDetails statusDetails = new StatusDetails();
    statusDetails.setMessage("The test has passed successfully!");
    statusDetails.setCode(0);
    return statusDetails;
  }

  protected AllureStepReport getFirstFailedSubStep() {
    synchronized (steps) {
      for (AllureStepReport stepReport : this.steps) {
        Status temp = stepReport.getStatus();
        if (temp.equals(Status.FAILED) || temp.equals(Status.BROKEN)) {
          return stepReport;
        }
      }
    }
    return null;
  }
}
