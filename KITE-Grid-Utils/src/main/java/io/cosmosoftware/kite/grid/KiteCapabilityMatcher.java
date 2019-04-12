package io.cosmosoftware.kite.grid;

import org.openqa.grid.internal.utils.DefaultCapabilityMatcher;

import java.util.Map;

public class KiteCapabilityMatcher extends DefaultCapabilityMatcher {
  private final String gateway = "gateway";
  boolean allCapabilitiesAvailability = false;

  @Override
  public boolean matches(
      Map<String, Object> nodeCapability, Map<String, Object> requestedCapability) {
    boolean defaultCapabilitiesAvailability = super.matches(nodeCapability, requestedCapability);
    if (!requestedCapability.containsKey(gateway) || !nodeCapability.containsKey(gateway)) {
      return defaultCapabilitiesAvailability;
    } else {
      boolean nodeOSAvailability =
          nodeCapability.get(gateway).equals(requestedCapability.get(gateway));
      allCapabilitiesAvailability = (defaultCapabilitiesAvailability && nodeOSAvailability);
      return allCapabilitiesAvailability;
    }
  }
}
