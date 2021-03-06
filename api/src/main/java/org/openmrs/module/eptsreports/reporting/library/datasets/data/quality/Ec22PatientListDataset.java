package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.Ec22Queries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class Ec22PatientListDataset extends BaseDataSet {

  public DataSetDefinition ec22PatientListDataset(List<Parameter> parameterList) {
    SqlDataSetDefinition sqlDataSetDefinition = new SqlDataSetDefinition();
    sqlDataSetDefinition.setName("EC22");
    sqlDataSetDefinition.addParameters(parameterList);
    sqlDataSetDefinition.setSqlQuery(Ec22Queries.getEc22CombinedQuery());
    return sqlDataSetDefinition;
  }
}
