package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import java.util.Date;
import org.junit.Before;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.test.BaseContextSensitiveTest;

public abstract class DefinitionsFGHLiveTest extends DefinitionsTest {
  /** @see BaseContextSensitiveTest#useInMemoryDatabase() */
  @Override
  public Boolean useInMemoryDatabase() {
    /*
     * ensure ~/.OpenMRS/openmrs-runtime.properties exists with your properties
     * such as; connection.username=openmrs
     * connection.url=jdbc:mysql://127.0.0.1:3316/openmrs
     * connection.password=wTV.Tpp0|Q&c
     */
    return false;
  }

  protected abstract String username();

  protected abstract String password();

  @Before
  public void initialize() throws ContextAuthenticationException {
    Context.authenticate(username(), password());
  }

  @Override
  protected Date getStartDate() {
    return DateUtil.getDateTime(2013, 2, 6);
  }

  @Override
  protected Date getEndDate() {
    return DateUtil.getDateTime(2019, 3, 6);
  }

  @Override
  protected Location getLocation() {
    return Context.getLocationService().getLocation(103);
  }
}
