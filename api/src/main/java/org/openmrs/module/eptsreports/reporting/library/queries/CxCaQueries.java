/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

/** @author Stélio Moiane */
public interface CxCaQueries {

  int negative = 664;

  int positive = 703;

  int suspectedCancer = 2093;

  String findPatientsWithCervicalCencerScreen =
      "SELECT p.patient_id FROM patient p\n"
          + "    INNER JOIN person pe ON pe.person_id = p.patient_id\n"
          + "	INNER JOIN\n"
          + "	(\n"
          + "		SELECT e.patient_id, MAX(e.encounter_datetime) last_ccs_date FROM encounter e\n"
          + "			INNER JOIN obs o ON o.encounter_id = e.encounter_id\n"
          + "				WHERE e.voided = 0 AND o.voided = 0\n"
          + "					AND o.concept_id = 2094 AND o.value_coded = %s\n"
          + "					AND e.encounter_type = 28 AND e.location_id = :location \n"
          + "					AND e.encounter_datetime BETWEEN :startDate AND :endDate\n"
          + "					GROUP BY e.patient_id\n"
          + "	)max_css ON max_css.patient_id = p.patient_id\n"
          + "	WHERE p.voided = 0 AND (YEAR( :endDate) - YEAR(pe.birthdate)) >= 15\n"
          + "	GROUP BY p.patient_id";

  String findPatientsWithCryothherapyCervicalCencerScreen =
      "SELECT p.patient_id FROM patient p\n"
          + "    INNER JOIN person pe ON pe.person_id = p.patient_id\n"
          + "	INNER JOIN\n"
          + "	(\n"
          + "		SELECT patient_id, MAX(last_ccs_cryotherapy_date) last_ccs_cryotherapy_date FROM\n"
          + "		(\n"
          + "			SELECT e.patient_id, MAX(e.encounter_datetime) last_ccs_cryotherapy_date FROM encounter e\n"
          + "				INNER JOIN obs o ON o.encounter_id = e.encounter_id\n"
          + "					WHERE e.voided = 0 AND o.voided = 0\n"
          + "						AND o.concept_id = 2117 AND o.value_coded = 1065\n"
          + "						AND e.encounter_type = 28 AND e.location_id = :location \n"
          + "						AND e.encounter_datetime BETWEEN :startDate AND :endDate\n"
          + "							GROUP BY e.patient_id			\n"
          + "			UNION\n"
          + "	\n"
          + "			SELECT e.patient_id, MAX(e.encounter_datetime) last_ccs_cryotherapy_date FROM encounter e\n"
          + "				INNER JOIN obs o ON o.encounter_id = e.encounter_id\n"
          + "					WHERE e.voided = 0 AND o.voided = 0\n"
          + "						AND o.concept_id = 23967 AND e.encounter_type = 28 AND e.location_id = :location \n"
          + "						AND e.encounter_datetime BETWEEN :startDate AND :endDate\n"
          + "							GROUP BY e.patient_id\n"
          + "		)max_ccs_cryotherapy GROUP BY max_ccs_cryotherapy.patient_id	\n"
          + "	)ccs_cryotherapy ON ccs_cryotherapy.patient_id = p.patient_id\n"
          + "	WHERE p.voided = 0 AND (YEAR( :endDate) - YEAR(pe.birthdate)) >= 15\n"
          + "		GROUP BY p.patient_id";

  String findPatientsWithCervicalCencerScreenList =
      "SELECT p.patient_id, pi.identifier, CONCAT(pn.given_name, ' ', COALESCE(pn.middle_name, ''), ' ', pn.family_name)name,\n"
          + "		CASE css_value WHEN 703 THEN 'POSITIVO' \n"
          + "			WHEN 664 THEN 'NEGATIVO' \n"
          + "			WHEN 2093 THEN 'SUSPEITA'\n"
          + "			WHEN 5622 THEN 'OUTRO'\n"
          + "			END AS result, ccs_date_value FROM patient p\n"
          + "	INNER JOIN patient_identifier pi ON p.patient_id = pi.patient_id\n"
          + "	INNER JOIN person_name pn ON pn.person_id = p.patient_id\n"
          + "	INNER JOIN person pe ON pe.person_id = p.patient_id\n"
          + "	INNER JOIN\n"
          + "	(\n"
          + "		SELECT last_css.patient_id, css_value FROM\n"
          + "		(\n"
          + "			SELECT e.patient_id, MAX(e.encounter_datetime) last_ccs_date FROM encounter e\n"
          + "				INNER JOIN obs o ON o.encounter_id = e.encounter_id\n"
          + "					WHERE e.voided = 0 AND o.voided = 0\n"
          + "						AND o.concept_id = 2094 AND e.encounter_type = 28 AND e.location_id = :location \n"
          + "						AND e.encounter_datetime BETWEEN :startDate AND :endDate\n"
          + "						GROUP BY e.patient_id\n"
          + "		)last_css INNER JOIN\n"
          + "		(\n"
          + "			SELECT o.person_id patient_id, o.obs_datetime, o.value_coded css_value FROM obs o\n"
          + "				WHERE o.concept_id = 2094 AND o.value_coded IN (703,664,2093,5622)\n"
          + "		)last_css_value ON last_css_value.patient_id = last_css.patient_id AND last_css.last_ccs_date = obs_datetime\n"
          + "	)css ON css.patient_id = p.patient_id\n"
          + "\n"
          + "	LEFT JOIN\n"
          + "	(\n"
          + "		SELECT e.patient_id, MAX(e.encounter_datetime) ccs_date_value FROM encounter e\n"
          + "				INNER JOIN obs o ON o.encounter_id = e.encounter_id\n"
          + "					WHERE e.voided = 0 AND o.voided = 0\n"
          + "						AND e.encounter_type = 28 AND e.location_id = :location \n"
          + "						AND e.encounter_datetime BETWEEN :startDate AND :endDate\n"
          + "						GROUP BY e.patient_id\n"
          + "	)css_date ON css_date.patient_id = p.patient_id\n"
          + "	WHERE p.voided = 0 AND (YEAR( :endDate) - YEAR(pe.birthdate)) >= 15\n"
          + "	GROUP BY p.patient_id";
}
