package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxMlCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.TxMLDimensions;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TxMlCommunityDataset extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TxMlCohortQueries txMlCohortQueries;

  @Autowired private TxMLDimensions txMLDimensions;

  public DataSetDefinition constructtxMlDataset() {
    final CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("Tx_Ml Data Set");
    dsd.addParameters(this.getParameters());
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition patientsWhoMissedNextApointment =
        this.txMlCohortQueries.getCommunityPatientsWhoMissedNextApointment();

    final CohortDefinition ltfuLessThan3Months =
        this.txMlCohortQueries.getCommunityPatientsWhoAreLTFULessThan3Months();

    final CohortDefinition ltfuLessGreatherThan3Months =
        this.txMlCohortQueries.getCommunityPatientsWhoAreLTFUGreaterThan3Months();

    final CohortIndicator patientsWhoMissedNextApointmentIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "findPatientsWhoMissedNextApointment",
            EptsReportUtils.map(patientsWhoMissedNextApointment, mappings));
    final CohortIndicator ltfuLessThan3MonthsIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "findPatientsWhoAreLTFULessThan3Months",
            EptsReportUtils.map(ltfuLessThan3Months, mappings));
    final CohortIndicator ltfuLessGreatherThan3MonthsIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "findPatientsWhoAreLTFUGreaterThan3Months",
            EptsReportUtils.map(ltfuLessGreatherThan3Months, mappings));

    dsd.addDimension("gender", EptsReportUtils.map(this.eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            this.eptsCommonDimension.age(this.ageDimensionCohort), "effectiveDate=${endDate}"));
    dsd.addDimension(
        "dead", EptsReportUtils.map(this.txMLDimensions.findPatientsWhoAreAsDead(), mappings));
    dsd.addDimension(
        "transferedout",
        EptsReportUtils.map(this.txMLDimensions.findPatientsWhoAreTransferedOut(), mappings));
    dsd.addDimension(
        "refusedorstoppedtreatment",
        EptsReportUtils.map(
            this.txMLDimensions.findPatientsWhoRefusedOrStoppedTreatment(), mappings));

    dsd.addColumn(
        "M1",
        "Total missed appointments",
        EptsReportUtils.map(patientsWhoMissedNextApointmentIndicator, mappings),
        "");
    super.addRow(
        dsd,
        "M2",
        "Age and Gender",
        EptsReportUtils.map(patientsWhoMissedNextApointmentIndicator, mappings),
        this.getColumnsForAgeAndGender());

    dsd.addColumn(
        "M2-TotalMale",
        " Age and Gender (Totals male) ",
        EptsReportUtils.map(patientsWhoMissedNextApointmentIndicator, mappings),
        "gender=M");
    dsd.addColumn(
        "M2-TotalFemale",
        "Age and Gender (Totals female) ",
        EptsReportUtils.map(patientsWhoMissedNextApointmentIndicator, mappings),
        "gender=F");

    this.setDeadDimension(
        dsd, EptsReportUtils.map(patientsWhoMissedNextApointmentIndicator, mappings), mappings);
    super.addRow(
        dsd,
        "M4",
        "LTFU < 90 days",
        EptsReportUtils.map(ltfuLessThan3MonthsIndicator, mappings),
        this.getColumnsForAgeAndGender());
    dsd.addColumn(
        "M4-TotalMale",
        "LTFU < 90 days (Totals male) ",
        EptsReportUtils.map(ltfuLessThan3MonthsIndicator, mappings),
        "gender=M");
    dsd.addColumn(
        "M4-TotalFemale",
        "LTFU < 90 days (Totals female) ",
        EptsReportUtils.map(ltfuLessThan3MonthsIndicator, mappings),
        "gender=F");

    super.addRow(
        dsd,
        "M5",
        "LTFU >= 90 days",
        EptsReportUtils.map(ltfuLessGreatherThan3MonthsIndicator, mappings),
        this.getColumnsForAgeAndGender());
    dsd.addColumn(
        "M5-TotalMale",
        "LTFU >= 90 days (Totals male) ",
        EptsReportUtils.map(ltfuLessGreatherThan3MonthsIndicator, mappings),
        "gender=M");
    dsd.addColumn(
        "M5-TotalFemale",
        "LTFU >= 90 days (Totals female) ",
        EptsReportUtils.map(ltfuLessGreatherThan3MonthsIndicator, mappings),
        "gender=F");

    this.setTransferedDimension(
        dsd, EptsReportUtils.map(patientsWhoMissedNextApointmentIndicator, mappings), mappings);
    this.setRefusedOrStoppedTreatmentDimension(
        dsd, EptsReportUtils.map(patientsWhoMissedNextApointmentIndicator, mappings), mappings);

    return dsd;
  }

  private void setDeadDimension(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      final Mapped<? extends CohortIndicator> indicator,
      final String mappings) {
    final String dimension = "dead=dead";
    for (final ColumnParameters column : this.getColumnsForAgeAndGender()) {
      final String name = "M3" + "-" + column.getColumn();
      final String label = "Dead" + " (" + column.getLabel() + ")";
      final String dimensionIter =
          column.getDimensions().length() > 2
              ? column.getDimensions() + "|" + dimension
              : dimension;
      dataSetDefinition.addColumn(name, label, indicator, dimensionIter);
    }
    dataSetDefinition.addColumn(
        "M3-TotalMale", "Dead (Totals male) ", indicator, "gender=M|dead=dead");
    dataSetDefinition.addColumn(
        "M3-TotalFemale", "Dead (Totals female) ", indicator, "gender=F|dead=dead");
  }

  private void setTransferedDimension(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      final Mapped<? extends CohortIndicator> indicator,
      final String mappings) {
    final String dimension = "transferedout=transferedout";
    for (final ColumnParameters column : this.getColumnsForAgeAndGender()) {
      final String name = "M6" + "-" + column.getColumn();
      final String label = "Transfered Out" + " (" + column.getLabel() + ")";
      final String dimensionIter =
          column.getDimensions().length() > 2
              ? column.getDimensions() + "|" + dimension
              : dimension;
      dataSetDefinition.addColumn(name, label, indicator, dimensionIter);
    }
    dataSetDefinition.addColumn(
        "M6-TotalMale",
        "Transfered Out (Totals male) ",
        indicator,
        "gender=M|transferedout=transferedout");
    dataSetDefinition.addColumn(
        "M6-TotalFemale",
        "Transfered Out (Totals female) ",
        indicator,
        "gender=F|transferedout=transferedout");
  }

  private void setRefusedOrStoppedTreatmentDimension(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      final Mapped<? extends CohortIndicator> indicator,
      final String mappings) {

    final String dimension = "refusedorstoppedtreatment=refusedorstoppedtreatment";
    for (final ColumnParameters column : this.getColumnsForAgeAndGender()) {
      final String name = "M7" + "-" + column.getColumn();
      final String label = "Stopped/Refused Treatment" + " (" + column.getLabel() + ")";
      final String dimensionIter =
          column.getDimensions().length() > 2
              ? column.getDimensions() + "|" + dimension
              : dimension;
      dataSetDefinition.addColumn(name, label, indicator, dimensionIter);
    }
    dataSetDefinition.addColumn(
        "M7-TotalMale",
        "Stopped/Refused Treatment (Totals male) ",
        indicator,
        "gender=M|refusedorstoppedtreatment=refusedorstoppedtreatment");
    dataSetDefinition.addColumn(
        "M7-TotalFemale",
        "Stopped/Refused Treatment (Totals female) ",
        indicator,
        "gender=F|refusedorstoppedtreatment=refusedorstoppedtreatment");
  }

  private List<ColumnParameters> getColumnsForAgeAndGender() {
    final ColumnParameters under1M =
        new ColumnParameters("under1M", "under 1 year male", "gender=M|age=<1", "01");
    final ColumnParameters oneTo4M =
        new ColumnParameters("oneTo4M", "1 - 4 years male", "gender=M|age=1-4", "02");
    final ColumnParameters fiveTo9M =
        new ColumnParameters("fiveTo9M", "5 - 9 years male", "gender=M|age=5-9", "03");
    final ColumnParameters tenTo14M =
        new ColumnParameters("tenTo14M", "10 - 14 male", "gender=M|age=10-14", "04");
    final ColumnParameters fifteenTo19M =
        new ColumnParameters("fifteenTo19M", "15 - 19 male", "gender=M|age=15-19", "05");
    final ColumnParameters twentyTo24M =
        new ColumnParameters("twentyTo24M", "20 - 24 male", "gender=M|age=20-24", "06");
    final ColumnParameters twenty5To29M =
        new ColumnParameters("twenty4To29M", "25 - 29 male", "gender=M|age=25-29", "07");
    final ColumnParameters thirtyTo34M =
        new ColumnParameters("thirtyTo34M", "30 - 34 male", "gender=M|age=30-34", "08");
    final ColumnParameters thirty5To39M =
        new ColumnParameters("thirty5To39M", "35 - 39 male", "gender=M|age=35-39", "09");
    final ColumnParameters foutyTo44M =
        new ColumnParameters("foutyTo44M", "40 - 44 male", "gender=M|age=40-44", "10");
    final ColumnParameters fouty5To49M =
        new ColumnParameters("fouty5To49M", "45 - 49 male", "gender=M|age=45-49", "11");
    final ColumnParameters above50M =
        new ColumnParameters("above50M", "50+ male", "gender=M|age=50+", "12");
    final ColumnParameters unknownM =
        new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "13");

    final ColumnParameters under1F =
        new ColumnParameters("under1F", "under 1 year female", "gender=F|age=<1", "14");
    final ColumnParameters oneTo4F =
        new ColumnParameters("oneTo4F", "1 - 4 years female", "gender=F|age=1-4", "15");
    final ColumnParameters fiveTo9F =
        new ColumnParameters("fiveTo9F", "5 - 9 years female", "gender=F|age=5-9", "16");
    final ColumnParameters tenTo14F =
        new ColumnParameters("tenTo14F", "10 - 14 female", "gender=F|age=10-14", "17");
    final ColumnParameters fifteenTo19F =
        new ColumnParameters("fifteenTo19F", "15 - 19 female", "gender=F|age=15-19", "18");
    final ColumnParameters twentyTo24F =
        new ColumnParameters("twentyTo24F", "20 - 24 female", "gender=F|age=20-24", "19");
    final ColumnParameters twenty5To29F =
        new ColumnParameters("twenty4To29F", "25 - 29 female", "gender=F|age=25-29", "20");
    final ColumnParameters thirtyTo34F =
        new ColumnParameters("thirtyTo34F", "30 - 34 female", "gender=F|age=30-34", "21");
    final ColumnParameters thirty5To39F =
        new ColumnParameters("thirty5To39F", "35 - 39 female", "gender=F|age=35-39", "22");
    final ColumnParameters foutyTo44F =
        new ColumnParameters("foutyTo44F", "40 - 44 female", "gender=F|age=40-44", "23");
    final ColumnParameters fouty5To49F =
        new ColumnParameters("fouty5To49F", "45 - 49 female", "gender=F|age=45-49", "24");
    final ColumnParameters above50F =
        new ColumnParameters("above50F", "50+ female", "gender=F|age=50+", "25");
    final ColumnParameters unknownF =
        new ColumnParameters("unknownF", "Unknown age female", "gender=F|age=UK", "26");
    final ColumnParameters total = new ColumnParameters("totals", "Totals", "", "27");

    return Arrays.asList(
        under1M,
        oneTo4M,
        fiveTo9M,
        tenTo14M,
        fifteenTo19M,
        twentyTo24M,
        twenty5To29M,
        thirtyTo34M,
        thirty5To39M,
        foutyTo44M,
        fouty5To49M,
        above50M,
        unknownM,
        under1F,
        oneTo4F,
        fiveTo9F,
        tenTo14F,
        fifteenTo19F,
        twentyTo24F,
        twenty5To29F,
        thirtyTo34F,
        thirty5To39F,
        foutyTo44F,
        fouty5To49F,
        above50F,
        unknownF,
        total);
  }
}