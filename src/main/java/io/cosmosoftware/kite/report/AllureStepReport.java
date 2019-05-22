package io.cosmosoftware.kite.report;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is a wrapper of the Allure's StepResult.
 */
public class AllureStepReport extends Entity {
  
  private List<CustomAttachment> attachments;
  private String description = "N/C";
  private StatusDetails details;
  private boolean ignore = false;
  private ParamList parameters;
  private Status status = Status.PASSED;
  private List<AllureStepReport> steps;
  
  /**
   * Instantiates a new AllureStepReport report.
   *
   * @param name the name
   */
  public AllureStepReport(String name) {
    super(name);
    this.attachments = new ArrayList<>();
    this.steps = new ArrayList<>();
    this.parameters = new ParamList();
  }
  
  public synchronized void addAttachment(CustomAttachment attachment) {
    this.attachments.add(attachment);
  }
  
  public void addParam(String name, String value) {
    this.parameters.addLabel(name, value);
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
    for (AllureStepReport stepReport : this.steps) {
      Status temp = stepReport.getStatus();
      if (temp.equals(Status.FAILED) || temp.equals(Status.BROKEN)) {
        return temp;
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
    //this.parameters.addLabel("Description", description);
  }
  
  public StatusDetails getDetails() {
    return details;
  }
  
  public void setDetails(StatusDetails details) {
    this.details = details;
    // Reporter.getInstance().textAttachment(this, "statusDetail", details.toJson().toString(), "json");
  }
  
  @Override
  public JsonObjectBuilder getJsonBuilder() {
    this.status = getActualStatus();
    JsonArrayBuilder stepsArray = Json.createArrayBuilder();
    if (steps.size() > 0) {
      for (AllureStepReport stepReport : this.steps) {
        stepsArray.add(stepReport.toJson());
      }
    }
    
    JsonArrayBuilder attArray = Json.createArrayBuilder();
    if (attachments.size() > 0) {
      for (CustomAttachment attachment : attachments) {
        attArray.add(attachment.toJson());
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
