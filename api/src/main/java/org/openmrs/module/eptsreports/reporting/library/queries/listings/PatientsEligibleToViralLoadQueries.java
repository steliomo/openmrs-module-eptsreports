/** */
package org.openmrs.module.eptsreports.reporting.library.queries.listings;

/** @author Stélio Moiane */
public interface PatientsEligibleToViralLoadQueries {

  class QUERY {
    public static final String findPatientsEligibleToViralLoad =
        "SELECT p.patient_id, pi.identifier, CONCAT(pn.given_name, ' ', COALESCE(pn.middle_name, ''), ' ', pn.family_name)name, pe.gender, (YEAR( :endDate) - YEAR(pe.birthdate)) age,\n"
            + "       art_start_date, art_line, last_viral_load_date, last_viral_load_value, last_follow_up_date, next_follow_up_date,\n"
            + "       last_pickup_date, next_pickup_date, restart_art_date, last_regimen_change_date, regimen_name FROM patient p\n"
            + "    INNER JOIN patient_identifier pi ON p.patient_id = pi.patient_id\n"
            + "	INNER JOIN person_name pn ON pn.person_id = p.patient_id\n"
            + "	INNER JOIN person pe ON pe.person_id = p.patient_id\n"
            + "    INNER JOIN\n"
            + "    (\n"
            + "        SELECT patient_id, MIN(art_start_date) art_start_date FROM\n"
            + "            (	\n"
            + "				SELECT p.patient_id,MIN(e.encounter_datetime) art_start_date FROM patient p \n"
            + "					INNER JOIN encounter e ON p.patient_id=e.patient_id	\n"
            + "					INNER JOIN obs o ON o.encounter_id=e.encounter_id \n"
            + "						WHERE e.voided=0 AND o.voided=0 AND p.voided=0 \n"
            + "						AND e.encounter_type IN (18,6,9) AND o.concept_id=1255 AND o.value_coded=1256 \n"
            + "						AND e.encounter_datetime <= :endDate AND e.location_id = :location\n"
            + "							GROUP BY p.patient_id\n"
            + "				UNION\n"
            + "		\n"
            + "				SELECT p.patient_id,MIN(value_datetime) art_start_date FROM patient p\n"
            + "					INNER JOIN encounter e ON p.patient_id=e.patient_id\n"
            + "					INNER JOIN obs o ON e.encounter_id=o.encounter_id\n"
            + "						WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type in (18,6,9,53) \n"
            + "						AND o.concept_id=1190 AND o.value_datetime is NOT NULL \n"
            + "						AND o.value_datetime <= :endDate AND e.location_id = :location\n"
            + "							GROUP BY p.patient_id\n"
            + "				\n"
            + "				UNION\n"
            + "\n"
            + "				SELECT pg.patient_id,MIN(date_enrolled) art_start_date FROM patient p \n"
            + "					INNER JOIN patient_program pg ON p.patient_id=pg.patient_id\n"
            + "						WHERE pg.voided=0 AND p.voided=0 AND program_id=2 AND date_enrolled <= :endDate AND pg.location_id = :location\n"
            + "							GROUP BY pg.patient_id\n"
            + "				\n"
            + "				UNION\n"
            + "				\n"
            + "				SELECT e.patient_id, MIN(e.encounter_datetime) AS art_start_date FROM patient p\n"
            + "					INNER JOIN encounter e ON p.patient_id=e.patient_id\n"
            + "				  		WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 AND e.encounter_datetime <= :endDate AND e.location_id = :location\n"
            + "				  			GROUP BY p.patient_id\n"
            + "			  \n"
            + "				UNION\n"
            + "				\n"
            + "				SELECT p.patient_id,MIN(value_datetime) art_start_date FROM patient p\n"
            + "					INNER JOIN encounter e ON p.patient_id=e.patient_id\n"
            + "					INNER JOIN obs o ON e.encounter_id=o.encounter_id\n"
            + "						WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=52 \n"
            + "						AND o.concept_id=23866 and o.value_datetime is NOT NULL \n"
            + "						AND o.value_datetime <= :endDate AND e.location_id = :location\n"
            + "							GROUP BY p.patient_id	\n"
            + "	\n"
            + "		)min_art_start_date GROUP BY min_art_start_date.patient_id\n"
            + "	)start_art ON start_art.patient_id = p.patient_id \n"
            + "    \n"
            + "    LEFT JOIN \n"
            + "    \n"
            + "    (    \n"
            + "        SELECT patient_id, MAX(state_date) state_date FROM \n"
            + "        (\n"
            + "            SELECT pg.patient_id, MAX(ps.start_date) state_date FROM patient p \n"
            + "                INNER JOIN patient_program pg ON p.patient_id=pg.patient_id \n"
            + "                INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id \n"
            + "                    WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id=2 \n"
            + "                    AND ps.state in (7,8,10) AND ps.end_date IS NULL \n"
            + "                    AND ps.start_date <= :endDate AND pg.location_id = :location \n"
            + "                        GROUP BY pg.patient_id           \n"
            + "            UNION \n"
            + "                        \n"
            + "            SELECT p.patient_id, MAX(o.obs_datetime) state_date FROM patient p \n"
            + "                INNER JOIN encounter e ON p.patient_id=e.patient_id \n"
            + "                INNER JOIN obs o ON e.encounter_id=o.encounter_id \n"
            + "                    WHERE e.voided=0 AND o.voided=0 AND p.voided=0 AND e.encounter_type IN (53,6) \n"
            + "                    AND o.concept_id IN (6272,6273) AND o.value_coded IN (1706,1366,1709) \n"
            + "                    AND o.obs_datetime <= :endDate AND e.location_id = :location \n"
            + "                        GROUP BY p.patient_id                   \n"
            + "            UNION\n"
            + "\n"
            + "            SELECT person_id as patient_id,death_date as state_date FROM person \n"
            + "                WHERE dead=1 AND death_date IS NOT NULL AND death_date <= :endDate \n"
            + "            \n"
            + "            UNION \n"
            + "            \n"
            + "            SELECT p.patient_id, MAX(o.obs_datetime) state_date FROM patient p \n"
            + "                INNER JOIN encounter e ON p.patient_id=e.patient_id \n"
            + "                INNER JOIN obs o ON e.encounter_id=o.encounter_id \n"
            + "                    WHERE e.voided=0 AND p.voided=0 AND o.voided=0 AND e.encounter_type=21 \n"
            + "                    AND e.encounter_datetime <= :endDate AND e.location_id = :location \n"
            + "                    AND o.concept_id IN (2031, 23944, 23945) AND o.value_coded=1366 \n"
            + "                        GROUP BY p.patient_id\n"
            + "        ) max_exits GROUP BY patient_id\n"
            + "    )exits ON exits.patient_id = p.patient_id\n"
            + "\n"
            + "    LEFT JOIN \n"
            + "    (\n"
            + "        SELECT max_expected.patient_id,MAX(last_encounter_date) last_encounter_date ,MAX(expected_date) expected_date FROM \n"
            + "        (\n"
            + "            SELECT last_encounter.patient_id, last_encounter_date, expected_follow_up.expected_date FROM  \n"
            + "	        (\n"
            + "                SELECT MAX(e.encounter_datetime) last_encounter_date, e.patient_id FROM encounter e\n"
            + "                    WHERE e.encounter_type IN(6,9)\n"
            + "                        AND e.voided = 0 AND e.location_id = :location AND e.encounter_datetime <= :endDate\n"
            + "                            GROUP BY e.patient_id\n"
            + "            ) last_encounter\n"
            + "            LEFT JOIN \n"
            + "            (\n"
            + "                SELECT o.person_id patient_id, o.value_datetime expected_date, o.obs_datetime FROM obs o\n"
            + "                        INNER JOIN encounter e ON e.encounter_id = o.encounter_id\n"
            + "                            WHERE o.concept_id = 1410 AND e.encounter_type IN(6,9) \n"
            + "                            AND o.voided = 0 AND e.voided = 0\n"
            + "            ) expected_follow_up ON expected_follow_up.patient_id = last_encounter.patient_id AND expected_follow_up.obs_datetime = last_encounter.last_encounter_date \n"
            + "\n"
            + "            UNION\n"
            + "\n"
            + "            SELECT last_pickup.patient_id, last_encounter_date, expected_pickup.expected_date FROM  \n"
            + "	        (\n"
            + "                SELECT MAX(e.encounter_datetime) last_encounter_date, e.patient_id FROM encounter e\n"
            + "                    WHERE e.encounter_type = 18\n"
            + "                        AND e.voided = 0 AND e.location_id = :location AND e.encounter_datetime <= :endDate\n"
            + "                            GROUP BY e.patient_id\n"
            + "            ) last_pickup\n"
            + "            LEFT JOIN \n"
            + "            (\n"
            + "                SELECT o.person_id patient_id, o.value_datetime expected_date, o.obs_datetime FROM obs o\n"
            + "                        INNER JOIN encounter e ON e.encounter_id = o.encounter_id\n"
            + "                            WHERE o.concept_id = 5096 AND e.encounter_type = 18\n"
            + "                            AND o.voided = 0 AND e.voided = 0\n"
            + "            ) expected_pickup ON expected_pickup.patient_id = last_pickup.patient_id AND expected_pickup.obs_datetime = last_pickup.last_encounter_date \n"
            + "\n"
            + "            UNION\n"
            + "\n"
            + "            SELECT o.person_id patient_id, MAX(o.value_datetime) as last_encounter_date, MAX((o.value_datetime + INTERVAL 30 DAY)) expected_date FROM obs o\n"
            + "                INNER JOIN encounter e ON e.encounter_id = o.encounter_id\n"
            + "                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 \n"
            + "                    AND o.voided = 0 AND e.voided = 0 AND o.location_id = :location AND o.value_datetime <= :endDate\n"
            + "                        GROUP BY o.person_id\n"
            + "        )max_expected GROUP BY max_expected.patient_id\n"
            + "    )expected ON expected.patient_id = p.patient_id\n"
            + "\n"
            + "    LEFT JOIN\n"
            + "    (\n"
            + "       SELECT o.person_id patient_id, MIN(o.obs_datetime) first_viral_load_date FROM obs o\n"
            + "	        WHERE o.voided = 0 AND o.concept_id=856\n"
            + "                    GROUP BY o.person_id\n"
            + "    )viral_load_result ON viral_load_result.patient_id = p.patient_id AND first_viral_load_date BETWEEN art_start_date AND :endDate\n"
            + "\n"
            + "    LEFT JOIN\n"
            + "    (\n"
            + "        SELECT last_viral_load_date.patient_id,last_viral_load_date, last_viral_load_value FROM\n"
            + "        (\n"
            + "            SELECT o.person_id patient_id, MAX(o.obs_datetime) last_viral_load_date FROM obs o\n"
            + "	            WHERE o.voided = 0 AND o.concept_id=856\n"
            + "                    GROUP BY o.person_id\n"
            + "        )last_viral_load_date \n"
            + "        LEFT JOIN\n"
            + "        (\n"
            + "            SELECT o.person_id patient_id, o.obs_datetime ,o.value_numeric last_viral_load_value FROM obs o\n"
            + "	            WHERE o.voided = 0 AND o.concept_id=856\n"
            + "        )last_viral_load_value ON last_viral_load_date.patient_id = last_viral_load_value.patient_id AND last_viral_load_value.obs_datetime = last_viral_load_date\n"
            + "    )last_viral_load_date_value ON last_viral_load_date_value.patient_id = p.patient_id AND last_viral_load_date BETWEEN art_start_date AND :endDate\n"
            + "    \n"
            + "    LEFT JOIN\n"
            + "    (\n"
            + "        SELECT o.person_id patient_id, MAX(o.obs_datetime) last_regimen_change_date FROM obs o\n"
            + "            WHERE o.voided = 0 AND o.concept_id = 23742 \n"
            + "                AND o.value_coded IN (1371, 23741)\n"
            + "                AND o.obs_datetime <= :endDate\n"
            + "                    GROUP BY o.person_id  \n"
            + "    )regimen_change ON regimen_change.patient_id = p.patient_id\n"
            + "    \n"
            + "    LEFT JOIN\n"
            + "    (\n"
            + "       SELECT o.person_id patient_id, MIN(o.obs_datetime) first_viral_load_date_regimen FROM obs o\n"
            + "	        WHERE o.voided = 0 AND o.concept_id=856\n"
            + "                    GROUP BY o.person_id\n"
            + "    )viral_load_regimen ON viral_load_regimen.patient_id = p.patient_id AND first_viral_load_date_regimen BETWEEN last_regimen_change_date AND :endDate\n"
            + "\n"
            + "    LEFT JOIN\n"
            + "    (\n"
            + "        SELECT last_viral_load_date_regimen.patient_id,last_viral_load_date_regimen, last_viral_load_value_regimen FROM\n"
            + "        (\n"
            + "            SELECT o.person_id patient_id, MAX(o.obs_datetime) last_viral_load_date_regimen FROM obs o\n"
            + "	            WHERE o.voided = 0 AND o.concept_id=856\n"
            + "                    GROUP BY o.person_id\n"
            + "        )last_viral_load_date_regimen \n"
            + "        LEFT JOIN\n"
            + "        (\n"
            + "            SELECT o.person_id patient_id, o.obs_datetime ,o.value_numeric last_viral_load_value_regimen FROM obs o\n"
            + "	            WHERE o.voided = 0 AND o.concept_id=856\n"
            + "        )last_viral_load_value_regimen ON last_viral_load_value_regimen.patient_id = last_viral_load_date_regimen.patient_id AND last_viral_load_value_regimen.obs_datetime = last_viral_load_date_regimen\n"
            + "    )last_viral_load_date_value_regimen ON last_viral_load_date_value_regimen.patient_id = p.patient_id AND last_viral_load_date_regimen BETWEEN last_regimen_change_date AND :endDate\n"
            + "    \n"
            + "    LEFT JOIN \n"
            + "    (\n"
            + "        SELECT o.person_id patient_id, MAX(o.obs_datetime) restart_art_date FROM obs o\n"
            + "	        WHERE o.voided = 0 AND o.concept_id = 6273 AND o.value_coded = 1705\n"
            + "		        GROUP BY o.person_id\n"
            + "    )restart_art ON restart_art.patient_id = p.patient_id AND restart_art_date BETWEEN art_start_date AND :endDate\n"
            + "    \n"
            + "    LEFT JOIN\n"
            + "    (\n"
            + "        SELECT o.person_id patient_id, MAX(o.obs_datetime) abandonment_date FROM obs o\n"
            + "	        WHERE o.voided = 0 AND o.concept_id = 6273 AND o.value_coded = 1705\n"
            + "		        GROUP BY o.person_id\n"
            + "    )abandonment ON abandonment.patient_id = p.patient_id AND abandonment_date BETWEEN art_start_date AND restart_art_date\n"
            + "    \n"
            + "    LEFT JOIN\n"
            + "    (\n"
            + "       SELECT o.person_id patient_id, MIN(o.obs_datetime) viral_load_restart_art FROM obs o\n"
            + "	        WHERE o.voided = 0 AND o.concept_id=856\n"
            + "                    GROUP BY o.person_id\n"
            + "    )viral_load_restart_art ON viral_load_restart_art.patient_id = p.patient_id AND viral_load_restart_art BETWEEN restart_art_date AND :endDate\n"
            + "\n"
            + "    LEFT JOIN\n"
            + "    (\n"
            + "        SELECT last_viral_load_date_restart_art.patient_id,last_viral_load_date_restart_art, last_viral_load_value_restart_art FROM\n"
            + "        (\n"
            + "            SELECT o.person_id patient_id, MAX(o.obs_datetime) last_viral_load_date_restart_art FROM obs o\n"
            + "	            WHERE o.voided = 0 AND o.concept_id=856\n"
            + "                    GROUP BY o.person_id\n"
            + "        )last_viral_load_date_restart_art \n"
            + "        LEFT JOIN\n"
            + "        (\n"
            + "            SELECT o.person_id patient_id, o.obs_datetime ,o.value_numeric last_viral_load_value_restart_art FROM obs o\n"
            + "	            WHERE o.voided = 0 AND o.concept_id=856\n"
            + "        )last_viral_load_value_restart_art ON last_viral_load_value_restart_art.patient_id = last_viral_load_date_restart_art.patient_id AND last_viral_load_value_restart_art.obs_datetime = last_viral_load_date_restart_art\n"
            + "    )last_viral_load_date_value_restart_art ON last_viral_load_date_value_restart_art.patient_id = p.patient_id AND last_viral_load_date_restart_art BETWEEN restart_art_date AND :endDate\n"
            + "    \n"
            + "    LEFT JOIN (\n"
            + "        SELECT max_art_line.patient_id, art_line, line_date FROM\n"
            + "        (\n"
            + "	        SELECT o.person_id patient_id, MAX(o.obs_datetime) line_date FROM obs o\n"
            + "                WHERE o.voided = 0 AND o.concept_id = 21151\n"
            + "                    AND o.value_coded IN (21150, 21148, 21149)\n"
            + "                        GROUP BY o.person_id\n"
            + "        ) max_art_line\n"
            + "        LEFT JOIN \n"
            + "        (\n"
            + "            SELECT o.person_id patient_id, CASE o.value_coded WHEN 21150 THEN 'PRIMEIRA LINHA' \n"
            + "										WHEN 21148 THEN 'SEGUNDA LINHA' \n"
            + "										WHEN 21149 THEN 'TERCEIRA LINHA'\n"
            + "										END AS art_line, o.obs_datetime FROM obs o\n"
            + "	            WHERE o.voided = 0 AND o.concept_id = 21151\n"
            + "	                AND o.value_coded IN (21150, 21148, 21149)\n"
            + "        ) art_line ON art_line.patient_id = max_art_line.patient_id AND art_line.obs_datetime = max_art_line.line_date\n"
            + "    )art_line ON art_line.patient_id = p.patient_id AND line_date <= :endDate\n"
            + "    LEFT JOIN (\n"
            + "        SELECT last_encounter.patient_id, last_follow_up_date, next_follow_up.next_follow_up_date FROM  \n"
            + "	        (\n"
            + "                SELECT MAX(e.encounter_datetime) last_follow_up_date, e.patient_id FROM encounter e\n"
            + "                    WHERE e.encounter_type IN(6,9)\n"
            + "                        AND e.voided = 0 AND e.location_id = :location AND e.encounter_datetime <= :endDate\n"
            + "                            GROUP BY e.patient_id\n"
            + "            ) last_encounter\n"
            + "            LEFT JOIN \n"
            + "            (\n"
            + "                SELECT o.person_id patient_id, o.value_datetime next_follow_up_date, o.obs_datetime FROM obs o\n"
            + "                        INNER JOIN encounter e ON e.encounter_id = o.encounter_id\n"
            + "                            WHERE o.concept_id = 1410 AND e.encounter_type IN(6,9) \n"
            + "                            AND o.voided = 0 AND e.voided = 0\n"
            + "            ) next_follow_up ON next_follow_up.patient_id = last_encounter.patient_id AND next_follow_up.obs_datetime = last_encounter.last_follow_up_date \n"
            + "    )last_encounter_and_follow_up ON last_encounter_and_follow_up.patient_id = p.patient_id\n"
            + "    LEFT JOIN \n"
            + "    (\n"
            + "        SELECT last_pickup.patient_id, MAX(last_pickup_date) last_pickup_date, MAX(next_pickup_date) next_pickup_date FROM  \n"
            + "	        (\n"
            + "                SELECT e.patient_id, MAX(e.encounter_datetime) last_pickup_date FROM encounter e\n"
            + "                    WHERE e.encounter_type = 18\n"
            + "                        AND e.voided = 0 AND e.location_id = :location AND e.encounter_datetime <= :endDate\n"
            + "                            GROUP BY e.patient_id\n"
            + "            ) last_pickup\n"
            + "            LEFT JOIN \n"
            + "            (\n"
            + "                SELECT o.person_id patient_id, o.value_datetime next_pickup_date, o.obs_datetime FROM obs o\n"
            + "                        INNER JOIN encounter e ON e.encounter_id = o.encounter_id\n"
            + "                            WHERE o.concept_id = 5096 AND e.encounter_type = 18\n"
            + "                            AND o.voided = 0 AND e.voided = 0\n"
            + "            ) next_pickup ON next_pickup.patient_id = last_pickup.patient_id AND next_pickup.obs_datetime = last_pickup.last_pickup_date \n"
            + "\n"
            + "            UNION\n"
            + "\n"
            + "            SELECT o.person_id patient_id, MAX(o.value_datetime) as last_pickup_date, MAX((o.value_datetime + INTERVAL 30 DAY)) next_pickup_date FROM obs o\n"
            + "                INNER JOIN encounter e ON e.encounter_id = o.encounter_id\n"
            + "                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 \n"
            + "                    AND o.voided = 0 AND e.voided = 0 AND o.location_id = :location AND o.value_datetime <= :endDate\n"
            + "                        GROUP BY o.person_id\n"
            + "    )last_and_next_pickup ON last_and_next_pickup.patient_id = p.patient_id\n"
            + "    LEFT JOIN (\n"
            + "        SELECT max_regimen_date.patient_id, last_regimen_date, regimen_name FROM \n"
            + "        (\n"
            + "            SELECT o.person_id patient_id, MAX(o.obs_datetime) last_regimen_date FROM obs o\n"
            + "                WHERE o.voided = 0 AND o.concept_id = 1088\n"
            + "                AND o.obs_datetime <= :endDate\n"
            + "                GROUP BY o.person_id\n"
            + "        )max_regimen_date\n"
            + "        LEFT JOIN \n"
            + "        (\n"
            + "            SELECT o.person_id patient_id, o.value_coded, cn.name regimen_name, o.obs_datetime FROM obs o\n"
            + "                INNER JOIN concept_name cn ON o.value_coded = cn.concept_id\n"
            + "                    WHERE o.voided = 0 AND o.concept_id = 1088 AND cn.voided = 0 AND cn.locale_preferred = 1 AND cn.locale = 'pt'\n"
            + "        )regimen_name ON regimen_name.patient_id = max_regimen_date.patient_id AND regimen_name.obs_datetime = max_regimen_date.last_regimen_date\n"
            + "    )regimen ON regimen.patient_id\n"
            + "WHERE (exits.state_date IS NULL OR (exits.state_date IS NOT NULL AND expected.last_encounter_date > exits.state_date)) \n"
            + "AND (next_pickup_date + INTERVAL 28 DAY) >= :endDate\n"
            + "AND ((DATEDIFF( :endDate, art_start_date) >= 180 AND last_regimen_change_date IS NULL AND restart_art_date IS NULL AND (first_viral_load_date IS NULL OR DATEDIFF( :endDate, last_viral_load_date) >= 365 AND last_viral_load_value < 1000 ))\n"
            + "    OR (DATEDIFF( :endDate, last_regimen_change_date) >= 180 AND (last_viral_load_date_regimen IS NUll OR (DATEDIFF( :endDate, last_viral_load_date_regimen) >= 365 AND last_viral_load_value_regimen < 1000)))\n"
            + "    OR (DATEDIFF( :endDate, restart_art_date) >= 180 AND (abandonment_date IS NOT NULL AND (viral_load_restart_art IS NULL OR DATEDIFF( :endDate, last_viral_load_date_restart_art) >= 365 AND last_viral_load_value_restart_art < 1000 ))))  \n"
            + "AND p.patient_id NOT IN\n"
            + "    (\n"
            + "        SELECT o.person_id patient_id FROM obs o\n"
            + "            WHERE o.voided = 0 AND o.concept_id = 1982 \n"
            + "            AND o.value_coded = 1065\n"
            + "                GROUP BY o.person_id\n"
            + "\n"
            + "        UNION\n"
            + "\n"
            + "        SELECT o.person_id patient_id FROM obs o\n"
            + "            WHERE o.voided = 0 AND o.concept_id = 6332 \n"
            + "            AND o.value_coded = 1065\n"
            + "                GROUP BY o.person_id\n"
            + "    )\n"
            + "    GROUP BY p.patient_id\n"
            + "    ORDER BY pi.identifier";
  }
}
