/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.queries.data.quality;

import java.util.List;

/** This class will contain the queries for the summary indicator page That aggregate them all */
public class SummaryQueries {

  /** GRAVIDAS INSCRITAS NO SERVIÇO TARV */
  public static String getPregnantPatients(
      int pregnantConcept,
      int gestationConcept,
      int weeksPregnantConcept,
      int eddConcept,
      int adultInitailEncounter,
      int adultSegEncounter,
      int etvProgram) {

    String query =
        "SELECT  p.patient_id"
            + " FROM patient p"
            + " INNER JOIN person pe ON p.patient_id=pe.person_id"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=%d"
            + " AND value_coded=%d"
            + " AND e.encounter_type IN (%d, %d)"
            + " AND e.location_id IN(:location)"
            + " AND pe.gender ='M'"
            + " UNION"
            + " SELECT p.patient_id"
            + " FROM patient p"
            + " INNER JOIN person pe ON p.patient_id=pe.person_id"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=%d"
            + " AND"
            + " e.encounter_type IN (%d, %d)"
            + " AND e.location_id IN(:location) "
            + " AND pe.gender ='M'"
            + " UNION"
            + " SELECT p.patient_id"
            + " FROM patient p"
            + " INNER JOIN person pe ON p.patient_id=pe.person_id"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=%d"
            + " AND"
            + " e.encounter_type in (%d, %d) "
            + " AND e.location_id IN(:location) "
            + " AND pe.gender ='M'"
            + " UNION"
            + " SELECT pp.patient_id FROM patient_program pp"
            + " INNER JOIN person pe ON pp.patient_id=pe.person_id"
            + " WHERE pp.program_id=%d"
            + " AND pp.voided=0 AND pp.location_id IN(:location) "
            + " AND pe.gender ='M'";
    return String.format(
        query,
        pregnantConcept,
        gestationConcept,
        adultInitailEncounter,
        adultSegEncounter,
        weeksPregnantConcept,
        adultInitailEncounter,
        adultSegEncounter,
        eddConcept,
        adultInitailEncounter,
        adultSegEncounter,
        etvProgram);
  }

  /**
   * Get the raw sql query for breastfeeding male patients
   *
   * @return String
   */
  public static String getBreastfeedingMalePatients(
      int deliveryDateConcept,
      int arvInitiationConcept,
      int lactationConcept,
      int registeredBreastfeedingConcept,
      int yesConcept,
      int ptvProgram,
      int gaveBirthState,
      int adultInitialEncounter,
      int adultSegEncounter) {

    String query =
        " SELECT p.patient_id"
            + " FROM patient p"
            + " INNER JOIN person pe ON p.patient_id=pe.person_id"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=%d"
            + " AND"
            + " e.encounter_type in (%d, %d)"
            + " AND e.location_id IN(:location) "
            + " AND pe.gender ='M'"
            + " UNION "
            + "SELECT  p.patient_id"
            + " FROM patient p"
            + " INNER JOIN person pe ON p.patient_id=pe.person_id"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=%d"
            + " AND value_coded=%d"
            + " AND e.encounter_type IN (%d, %d)"
            + " AND e.location_id IN(:location)"
            + " AND pe.gender ='M'"
            + " UNION "
            + " SELECT p.patient_id"
            + " FROM patient p"
            + " INNER JOIN person pe ON p.patient_id=pe.person_id"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=%d"
            + " AND value_coded=%d"
            + " AND"
            + " e.encounter_type IN (%d) "
            + " AND e.location_id IN(:location) "
            + " AND pe.gender ='M'"
            + "UNION "
            + "SELECT 	pg.patient_id"
            + " FROM patient p"
            + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
            + " INNER JOIN person pe ON pg.patient_id=pe.person_id"
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id"
            + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND"
            + " pg.program_id=%d"
            + " AND ps.state=%d"
            + " AND ps.end_date IS NULL AND"
            + " location_id IN(:location)"
            + " AND pe.gender ='M'";
    return String.format(
        query,
        deliveryDateConcept,
        adultInitialEncounter,
        adultSegEncounter,
        arvInitiationConcept,
        lactationConcept,
        adultInitialEncounter,
        adultSegEncounter,
        registeredBreastfeedingConcept,
        yesConcept,
        adultSegEncounter,
        ptvProgram,
        gaveBirthState);
  }

  /**
   * Get patients whose year of birth is before 1920
   *
   * @return String
   */
  public static String getPatientsWhoseYearOfBirthIsBeforeYear(int year) {
    String query =
        "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL AND YEAR(pe.birthdate) < %d ";
    return String.format(query, year);
  }

  /**
   * Get patients whose date of birth, estimated date of birth or age is negative
   *
   * @return String
   */
  public static String getPatientsWithNegativeBirthDates() {
    return "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NULL"
        + " UNION "
        + "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL AND pe.birthdate > pe.date_created";
  }

  /**
   * The patients birth, estimated date of birth or age indicates they are > 100 years of age
   *
   * @return String
   */
  public static String getPatientsWithMoreThanXyears(int years) {
    String query =
        "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL AND TIMESTAMPDIFF(YEAR, pe.birthdate, :endDate) > %d";
    return String.format(query, years);
  }

  public static String getCountPatientsEC14() {
    String query =
        " SELECT "
            + " pe.person_id As patient_id "
            + " FROM "
            + " person pe "
            + " inner join "
            + " (	select pn1.* "
            + " from person_name pn1 "
            + " inner join "
            + " ( "
            + " select person_id, min(person_name_id) id "
            + " from person_name "
            + " where voided = 0 "
            + " group by person_id "
            + " ) pn2 "
            + " where pn1.person_id = pn2.person_id and pn1.person_name_id = pn2.id "
            + " ) pn on pn.person_id = pe.person_id "
            + " inner join "
            + " (   select pid1.* "
            + " from patient_identifier pid1 "
            + " inner join "
            + " ( "
            + " select patient_id, min(patient_identifier_id) id "
            + " from patient_identifier "
            + " where voided = 0 "
            + " group by patient_id "
            + " ) pid2 "
            + " where pid1.patient_id = pid2.patient_id and pid1.patient_identifier_id = pid2.id "
            + " ) pid on pid.patient_id = pe.person_id "
            + " inner join "
            + " ( "
            + " SELECT p.patient_id AS patient_id, o.obs_datetime AS obs_datetime, e.encounter_datetime, l.name  location_name, "
            + " o.value_datetime as concept_value, cn.name, 'Ficha Resumo' AS FormType "
            + " FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + " INNER JOIN location l on l.location_id = e.location_id "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " INNER JOIN concept_name cn on cn.concept_id = o.concept_id AND cn.voided = 0 "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + " AND cn.locale = 'pt' "
            + " AND e.encounter_type = 53 "
            + " AND e.location_id IN (:location) "
            + " AND o.value_datetime is not null "
            + " AND o.value_datetime < '1985-01-01 00:00:00' "
            + " AND e.encounter_datetime between :startDate AND :endDate "
            + " UNION "
            + " SELECT p.patient_id AS patient_id, o.obs_datetime AS obs_datetime, e.encounter_datetime, l.name  location_name, "
            + " o.value_datetime as concept_value, cn.name, 'Ficha Clinica' AS FormType "
            + " FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + " INNER JOIN location l on l.location_id = e.location_id "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " INNER JOIN concept_name cn on cn.concept_id = o.concept_id AND cn.voided = 0 "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + " AND cn.locale = 'pt' "
            + " AND e.encounter_type = 6 "
            + " AND e.location_id IN (:location) "
            + " AND o.value_datetime is not null "
            + " AND o.value_datetime < '1985-01-01 00:00:00' "
            + " AND e.encounter_datetime between :startDate AND :endDate "
            + " UNION "
            + " Select p.patient_id AS patient_id, o.obs_datetime AS obs_datetime, e.encounter_datetime, l.name  location_name, "
            + " o.value_datetime as concept_value, cn.name, 'Fila' AS FormType "
            + " from patient p "
            + " INNER JOIN encounter e on p.patient_id = e.patient_id "
            + " INNER JOIN location l on l.location_id = e.location_id "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " INNER JOIN concept_name cn on cn.concept_id = o.concept_id AND cn.voided = 0 "
            + " where p.voided = 0 and e.voided = 0 and e.encounter_type = 18 and e.location_id IN (:location) "
            + " AND cn.locale = 'pt' "
            + " AND o.value_datetime is not null "
            + " AND o.value_datetime < '1985-01-01 00:00:00' "
            + " AND e.encounter_datetime between :startDate AND :endDate "
            + " UNION "
            + " SELECT p.patient_id AS patient_id, o.obs_datetime AS obs_datetime, e.encounter_datetime, l.name location_name, "
            + " o.value_datetime as concept_value, cn.name, 'Recepcao' AS FormType "
            + " FROM patient p "
            + " INNER JOIN encounter e on p.patient_id = e.patient_id "
            + " INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + " INNER JOIN concept_name cn on cn.concept_id = o.concept_id AND cn.voided = 0 "
            + " INNER JOIN location l on l.location_id = e.location_id "
            + " INNER JOIN  patient_program pg ON p.patient_id = pg.patient_id and pg.program_id = 2 and pg.location_id IN (:location) "
            + " INNER JOIN  patient_state ps ON pg.patient_program_id = ps.patient_program_id and ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " where p.voided = 0 and e.voided = 0 and o.voided = 0 and e.encounter_type = 52 "
            + " AND e.location_id IN (:location) "
            + " AND cn.locale = 'pt' "
            + " AND o.value_datetime is not null "
            + " AND o.value_datetime < '1985-01-01 00:00:00' "
            + " AND e.encounter_datetime between :startDate AND :endDate "
            + " ) obsvaluedatetime on pe.person_id = obsvaluedatetime.patient_id "
            + " LEFT JOIN (SELECT pg.patient_id, pg.date_enrolled, ps.state, max(ps.start_date) AS start_date FROM patient_program pg "
            + " INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id "
            + " AND ps.start_date IS NOT NULL "
            + " AND ps.end_date IS NULL "
            + " AND pg.program_id = 2 "
            + " AND pg.location_id IN (:location) "
            + " GROUP BY pg.patient_id "
            + " ) AS programState ON pe.person_id = programState.patient_id "
            + " where pe.voided = 0 and obsvaluedatetime.patient_id is not null ";
    return String.format(query);
  }

  /**
   * The patient’s date of birth is after any drug pick up date
   *
   * @return String
   */
  public static String getPatientsWhoseBirthdateIsAfterDrugPickup(List<Integer> encounterList) {
    String str1 = String.valueOf(encounterList).replaceAll("\\[", "");
    String str2 = str1.replaceAll("]", "");
    String query =
        " SELECT pa.patient_id FROM patient pa "
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + " WHERE pe.birthdate IS NOT NULL AND e.encounter_type IN(%s) "
            + " AND e.location_id IN(:location) AND pa.voided = 0 and e.voided=0 "
            + " AND pe.birthdate > e.encounter_datetime ";
    return String.format(query, str2);
  }

  /**
   * Get patients who have a given state before an encounter
   *
   * @return String
   */
  public static String getPatientsWithStateThatIsBeforeAnEncounter(
      int programId, int stateId, List<Integer> encounterList) {
    String str1 = String.valueOf(encounterList).replaceAll("\\[", "");
    String str2 = str1.replaceAll("]", "");
    String query =
        " SELECT pg.patient_id AS patient_id "
            + " FROM patient p "
            + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id  "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " WHERE p.voided=0 AND pg.program_id=%d"
            + " AND ps.state=%d "
            + " AND pg.voided=0 "
            + " AND ps.voided=0 "
            + " AND e.encounter_type IN(%s) "
            + " AND pg.location_id IN(:location) "
            + " AND e.location_id IN(:location) AND e.voided=0 "
            + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " AND e.encounter_datetime >= ps.start_date "
            + " GROUP BY pg.patient_id ";
    return String.format(query, programId, stateId, str2);
  }

  public static String getPatientsWithStateThatIsBeforeAnEncounterEC10() {
    String query =
        " SELECT pe.person_id As patient_id "
            + " FROM "
            + " person pe "
            + " inner join "
            + " ( "
            + " SELECT pg.patient_id AS patient_id, ps.start_date As abandoned_date "
            + " FROM patient p "
            + " INNER JOIN patient_program pg ON p.patient_id = pg.patient_id "
            + " INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id "
            + " WHERE p.voided = 0 AND pg.program_id = 2 "
            + " AND pg.voided = 0 "
            + " AND ps.voided = 0 "
            + " AND ps.state = 9 "
            + " AND pg.location_id IN (:location) "
            + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " AND ps.start_date <= :endDate "
            + " ) abandonedPrograma on pe.person_id = abandonedPrograma.patient_id "
            + " inner join "
            + " (	select pn1.* "
            + " from person_name pn1 "
            + " inner join "
            + " ( "
            + " select person_id, min(person_name_id) id "
            + " from person_name "
            + " where voided = 0 "
            + " group by person_id "
            + " ) pn2 "
            + " where pn1.person_id = pn2.person_id and pn1.person_name_id = pn2.id "
            + " ) pn on pn.person_id = pe.person_id "
            + " inner join "
            + " (   select pid1.* "
            + " from patient_identifier pid1 "
            + " inner join "
            + " ( "
            + " select patient_id, min(patient_identifier_id) id "
            + " from patient_identifier "
            + " where voided = 0 "
            + " group by patient_id "
            + " ) pid2 "
            + " where pid1.patient_id = pid2.patient_id and pid1.patient_identifier_id = pid2.id "
            + " ) pid on pid.patient_id = pe.person_id "
            + " inner join "
            + " ( "
            + " Select 	p.patient_id, e.encounter_datetime, l.name  location_name, e.date_created "
            + " from 	patient p "
            + " inner join encounter e on p.patient_id = e.patient_id "
            + " inner join location l on l.location_id = e.location_id "
            + " where 	p.voided = 0 and e.voided = 0 and e.encounter_type IN (6,9) and e.location_id IN (:location) "
            + " AND e.encounter_datetime between :startDate AND :endDate "
            + " order by e.encounter_datetime desc"
            + " ) consultation 	on consultation.patient_id = pe.person_id "
            + " inner join patient_program pg ON pe.person_id = pg.patient_id and pg.program_id = 2 and pg.location_id IN (:location) "
            + " inner join patient_state ps ON pg.patient_program_id = ps.patient_program_id and ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " where "
            + " pe.voided = 0 "
            + " and abandonedPrograma.patient_id is not null "
            + " and consultation.encounter_datetime >= abandonedPrograma.abandoned_date "
            + " GROUP BY pe.person_id";
    return String.format(query);
  }

  /**
   * Get patients who are marked as deceased and have a consultation after deceased date
   *
   * @return String
   */
  public static String getPatientsMarkedAsDeceasedAndHaveAnEncounter(List<Integer> encounterList) {
    String str1 = String.valueOf(encounterList).replaceAll("\\[", "");
    String encounters = str1.replaceAll("]", "");
    String query =
        " SELECT p.patient_id AS patientId "
            + " FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN person pe ON p.patient_id=pe.person_id "
            + " WHERE p.voided = 0 "
            + " AND e.encounter_type IN(%s) "
            + " AND e.location_id IN(:location) AND e.voided=0 "
            + " AND pe.voided=0 "
            + " AND pe.death_date IS NOT NULL "
            + " AND e.encounter_datetime >= pe.death_date "
            + " GROUP BY p.patient_id ";
    return String.format(query, encounters);
  }

  public static String getPatientsMarkedAsDeceasedAndHaveAnEncounterEC3() {
    String query =
        " SELECT pe.person_id As patient_id "
            + " FROM "
            + " person pe "
            + " left join person peObito on pe.person_id = peObito.person_id and peObito.voided = 0 and peObito.death_date IS NOT NULL "
            + " left join "
            + " ( "
            + " SELECT pg.patient_id AS patient_id, ps.start_date As death_date "
            + " FROM patient p "
            + " INNER JOIN patient_program pg ON p.patient_id = pg.patient_id "
            + " INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id "
            + " WHERE p.voided = 0 AND pg.program_id = 2 "
            // + programId // 2
            + " AND pg.voided = 0 "
            + " AND ps.voided = 0 "
            + " AND ps.state = 10 "
            + " AND pg.location_id IN (:location) "
            + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " AND ps.start_date <= :endDate "
            + " ) deadPrograma on pe.person_id = deadPrograma.patient_id "
            + " left join "
            + " ( "
            + " SELECT p.patient_id AS patient_id, o.obs_datetime AS death_date "
            + " FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o.concept_id = 6272 "
            // + stateOfStayPriorArtPatient // 6272
            + " AND o.value_coded = 1366"
            // + patientHasDiedConceptUuid // 1366
            + " AND e.encounter_type = 53 "
            // + masterCardEncounterType // 53
            + " AND e.location_id IN (:location) "
            + " AND o.obs_datetime <= :endDate "
            + " ) deadFichaResumo on pe.person_id = deadFichaResumo.patient_id "
            + " left join "
            + " ( "
            + " SELECT p.patient_id AS patient_id, o.obs_datetime AS death_date "
            + " FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o.concept_id = 6273 "
            // + stateOfStayArtPatient // 6273
            + " AND value_coded = 1366 "
            // + patientHasDiedConceptUuid // 1366
            + " AND e.encounter_type = 6 "
            // + sTarvAdultoSeguimentoEncounterTypeUuid // 6
            + " AND e.location_id IN (:location) "
            + " AND o.obs_datetime <= :endDate"
            + " ) deadFichaClinica on pe.person_id = deadFichaClinica.patient_id "
            + " left join "
            + " ( select pn1.* "
            + " from person_name pn1 "
            + " inner join "
            + " ( "
            + " select person_id, min(person_name_id) id "
            + " from person_name "
            + " where voided = 0 "
            + " group by person_id "
            + " ) pn2 "
            + " where pn1.person_id = pn2.person_id and pn1.person_name_id = pn2.id "
            + " ) pn on pn.person_id = pe.person_id "
            + " left join "
            + " ( select pid1.* "
            + " from patient_identifier pid1 "
            + " inner join "
            + " ( "
            + " select patient_id, min(patient_identifier_id) id "
            + " from patient_identifier "
            + " where voided = 0 "
            + " group by patient_id "
            + " ) pid2 "
            + " where pid1.patient_id = pid2.patient_id and pid1.patient_identifier_id = pid2.id "
            + " ) pid on pid.patient_id = pe.person_id "
            + " left join "
            + " ( "
            + " Select p.patient_id, e.encounter_datetime, l.name  location_name, e.date_created "
            + " from patient p "
            + " inner join encounter e on p.patient_id = e.patient_id "
            + " inner join location l on l.location_id = e.location_id "
            + " where 	p.voided = 0 and e.voided = 0 and e.encounter_type = 18 "
            // + encounterType // 18
            + " and e.location_id IN (:location) "
            + " AND e.encounter_datetime between :startDate AND :endDate"
            + " ) fila 	on fila.patient_id = pe.person_id "
            + " left join "
            + " ( "
            + " SELECT 	p.patient_id, o.value_datetime encounter_datetime, l.name location_name, e.date_created "
            + " FROM 	patient p "
            + " INNER JOIN encounter e on p.patient_id = e.patient_id "
            + " inner join obs o on e.encounter_id = o.encounter_id "
            + " inner join location l on l.location_id = e.location_id "
            + " where 	p.voided = 0 and e.voided = 0 and o.voided = 0 and e.encounter_type = 52 "
            // + masterCardDrugPickupEncounterTypeUuid // 52
            + " AND o.concept_id = 23866 "
            // + preArtPickupDate // 23866
            + " and o.value_datetime is not null and e.location_id IN (:location) "
            + " AND o.value_datetime between :startDate AND :endDate"
            + " ) recepcao	on recepcao.patient_id = pe.person_id "
            + " left join  patient_program pg ON pe.person_id = pg.patient_id and pg.program_id = 2 "
            // + programId // 2
            + " and pg.location_id IN (:location) "
            + " left join  patient_state ps ON pg.patient_program_id = ps.patient_program_id and ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " where 	pe.voided = 0 and "
            + " ( "
            + " peObito.person_id is not null or "
            + " deadPrograma.patient_id is not null or "
            + " deadFichaResumo.patient_id is not null or "
            + " deadFichaClinica.patient_id is not null "
            + " ) and "
            + " ( "
            + " ( "
            + " fila.encounter_datetime>peObito.death_date or "
            + " fila.encounter_datetime>deadPrograma.death_date or "
            + " fila.encounter_datetime>deadFichaResumo.death_date or "
            + " fila.encounter_datetime>deadFichaClinica.death_date "
            + " ) "
            + " OR "
            + " ( "
            + " recepcao.encounter_datetime>peObito.death_date or "
            + " recepcao.encounter_datetime>deadPrograma.death_date or "
            + " recepcao.encounter_datetime>deadFichaResumo.death_date or "
            + " recepcao.encounter_datetime>deadFichaClinica.death_date "
            + " ) "
            + " ) "
            + " GROUP BY pg.patient_id ";
    return String.format(query);
  }

  /**
   * The patients whose date of Encounter is before 1985
   *
   * @return String
   */
  public static String getPatientsWhoseEncounterIsBefore1985(List<Integer> encounterList) {
    String str1 = String.valueOf(encounterList).replaceAll("\\[", "");
    String str2 = str1.replaceAll("]", "");
    String query =
        " SELECT pa.patient_id FROM patient pa "
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + " WHERE pe.birthdate IS NOT NULL AND e.encounter_type IN(%s) "
            + " AND e.location_id IN(:location) AND pa.voided = 0 and e.voided=0 "
            + " AND YEAR(e.encounter_datetime) < 1985 ";
    return String.format(query, str2);
  }

  public static String getPatientsWithGivenEncounterList(List<Integer> encounterList) {
    String str1 = String.valueOf(encounterList).replaceAll("\\[", "");
    String str2 = str1.replaceAll("]", "");
    String query =
        " SELECT pa.patient_id FROM patient pa "
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + " WHERE pe.birthdate IS NOT NULL AND e.encounter_type IN(%s) "
            + " AND e.location_id IN(:location) AND pa.voided = 0 and e.voided=0 ";
    return String.format(query, str2);
  }

  public static String getPatientsEnrolledOnTARV(int programId) {
    String query =
        " SELECT pa.patient_id FROM patient pa "
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id"
            + " WHERE pe.birthdate IS NOT NULL  AND pg.program_id=%d"
            + " AND e.location_id IN(:location) AND pa.voided = 0 and e.voided=0 ";
    return String.format(query, programId);
  }
}
