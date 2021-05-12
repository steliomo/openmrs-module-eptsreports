/** */
package org.openmrs.module.eptsreports.reporting.library.queries.listings;

/** @author Stélio Moiane */
public interface PatientsOnARTwithViralLoadsQueries {

  String findPatientsOnARTwithViralLoads =
      "select 	concat(pid.identifier,' ') as NID,\n"
          + "		concat(ifnull(pn.given_name,''),' ',ifnull(pn.middle_name,''),' ',ifnull(pn.family_name,'')) as 'NomeCompleto',\n"
          + "		inicio_real.data_inicio,\n"
          + "                pat.value as Telefone,\n"
          + "		seguimento.data_seguimento,\n"
          + "		carga1.data_primeiro_carga,\n"
          + "		carga1.valor_primeira_carga,\n"
          + "		if(carga1.data_primeiro_carga<>carga2.data_ultima_carga,carga2.data_ultima_carga,'') as data_ultima_carga,\n"
          + "		if(carga1.data_primeiro_carga<>carga2.data_ultima_carga,carga2.valor_ultima_carga,'') as valor_ultima_carga,		\n"
          + "		regime.data_regime,\n"
          + "		regime.ultimo_regime,\n"
          + "		pe.gender,\n"
          + "		round(datediff( :endDate,pe.birthdate)/365) idade_actual\n"
          + "from						\n"
          + "		\n"
          + "		(	Select ultimavisita.patient_id,ultimavisita.encounter_datetime,o.value_datetime,e.location_id\n"
          + "			from\n"
          + "				(	select 	p.patient_id,max(encounter_datetime) as encounter_datetime\n"
          + "					from 	encounter e \n"
          + "							inner join patient p on p.patient_id=e.patient_id 		\n"
          + "					where 	e.voided=0 and p.voided=0 and e.encounter_type in (18,9,6) and \n"
          + "							e.location_id= :location and e.encounter_datetime<= :endDate\n"
          + "					group by p.patient_id\n"
          + "				) ultimavisita\n"
          + "			inner join encounter e on e.patient_id=ultimavisita.patient_id\n"
          + "			left join obs o on o.encounter_id=e.encounter_id and (o.concept_id=5096 OR o.concept_id=1410) and \n"
          + "			e.encounter_datetime=ultimavisita.encounter_datetime			\n"
          + "			where  o.voided=0 and e.encounter_type in (18,9,6) and e.location_id= :location and datediff( :endDate,o.value_datetime) < 60 \n"
          + "		) visita \n"
          + "		\n"
          + "		inner join\n"
          + "		(select patient_id,min(data_primeiro_carga) data_primeiro_carga,max(value_numeric) valor_primeira_carga\n"
          + "			from	\n"
          + "				(	select 	e.patient_id,\n"
          + "							min(o.obs_datetime) data_primeiro_carga\n"
          + "					from 	encounter e\n"
          + "							inner join obs o on e.encounter_id=o.encounter_id\n"
          + "					where 	e.encounter_type in (6,9) and e.form_id = 163 and e.voided=0 and\n"
          + "							o.voided=0 and o.concept_id=856 and e.encounter_datetime between date_add( :endDate, interval -3 month) and :endDate \n"
          + "					 and e.location_id= :location\n"
          + "					group by e.patient_id\n"
          + "				) primeiro_carga\n"
          + "				inner join obs o on o.person_id=primeiro_carga.patient_id and o.obs_datetime=primeiro_carga.data_primeiro_carga\n"
          + "			where o.concept_id=856 and o.voided=0\n"
          + "			group by patient_id\n"
          + "		) carga1 on carga1.patient_id = visita.patient_id\n"
          + "\n"
          + "		left join\n"
          + "		(	select patient_id,max(data_ultima_carga) data_ultima_carga,max(value_numeric) valor_ultima_carga\n"
          + "			from	\n"
          + "				(	select 	e.patient_id,\n"
          + "							max(o.obs_datetime) data_ultima_carga\n"
          + "					from 	encounter e\n"
          + "							inner join obs o on e.encounter_id=o.encounter_id\n"
          + "					where 	e.encounter_type in (6,9) and e.form_id = 163 and e.voided=0 and\n"
          + "						o.voided=0 and o.concept_id=856 and e.encounter_datetime between date_add( :endDate, interval -3 month) and :endDate \n"
          + "					and e.location_id= :location\n"
          + "					group by e.patient_id\n"
          + "				) ultima_carga\n"
          + "				inner join obs o on o.person_id=ultima_carga.patient_id and o.obs_datetime=ultima_carga.data_ultima_carga\n"
          + "			where o.concept_id=856 and o.voided=0\n"
          + "			group by patient_id\n"
          + "		) carga2 on carga2.patient_id= visita.patient_id\n"
          + "		\n"
          + "		left join 		\n"
          + "		(	Select patient_id,min(data_inicio) data_inicio\n"
          + "			from\n"
          + "				(	Select p.patient_id,min(e.encounter_datetime) data_inicio\n"
          + "					from 	patient p \n"
          + "							inner join encounter e on p.patient_id=e.patient_id	\n"
          + "							inner join obs o on o.encounter_id=e.encounter_id\n"
          + "					where 	e.voided=0 and o.voided=0 and p.voided=0 and \n"
          + "							e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and \n"
          + "							e.encounter_datetime<= :endDate and e.location_id= :location\n"
          + "					group by p.patient_id\n"
          + "				\n"
          + "					union\n"
          + "				\n"
          + "					Select p.patient_id,min(value_datetime) data_inicio\n"
          + "					from 	patient p\n"
          + "							inner join encounter e on p.patient_id=e.patient_id\n"
          + "							inner join obs o on e.encounter_id=o.encounter_id\n"
          + "					where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9) and \n"
          + "							o.concept_id=1190 and o.value_datetime is not null and \n"
          + "							o.value_datetime<= :endDate and e.location_id= :location\n"
          + "					group by p.patient_id\n"
          + "					\n"
          + "					union\n"
          + "					\n"
          + "					select 	pg.patient_id,pg.date_enrolled data_inicio\n"
          + "					from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id\n"
          + "					where 	pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<= :endDate and location_id= :location\n"
          + "					\n"
          + "					\n"
          + "				) inicio\n"
          + "			group by patient_id\n"
          + "		) inicio_real on inicio_real.patient_id = visita.patient_id\n"
          + "\n"
          + "		left join \n"
          + "		(	select pad1.*\n"
          + "			from person_address pad1\n"
          + "			inner join \n"
          + "			(\n"
          + "				select person_id,min(person_address_id) id \n"
          + "				from person_address\n"
          + "				where voided=0\n"
          + "				group by person_id\n"
          + "			) pad2\n"
          + "			where pad1.person_id=pad2.person_id and pad1.person_address_id=pad2.id\n"
          + "		) pad3 on pad3.person_id=visita.patient_id				\n"
          + "		left join 			\n"
          + "		(	select pn1.*\n"
          + "			from person_name pn1\n"
          + "			inner join \n"
          + "			(\n"
          + "				select person_id,min(person_name_id) id \n"
          + "				from person_name\n"
          + "				where voided=0\n"
          + "				group by person_id\n"
          + "			) pn2\n"
          + "			where pn1.person_id=pn2.person_id and pn1.person_name_id=pn2.id\n"
          + "		) pn on pn.person_id=visita.patient_id			\n"
          + "		left join\n"
          + "		(       select pid1.*\n"
          + "				from patient_identifier pid1\n"
          + "				inner join\n"
          + "				(\n"
          + "					select patient_id,min(patient_identifier_id) id\n"
          + "					from patient_identifier\n"
          + "					where voided=0\n"
          + "					group by patient_id\n"
          + "				) pid2\n"
          + "				where pid1.patient_id=pid2.patient_id and pid1.patient_identifier_id=pid2.id\n"
          + "		) pid on pid.patient_id=visita.patient_id\n"
          + "\n"
          + "                inner join person pe on pe.person_id=visita.patient_id\n"
          + "\n"
          + "		left join \n"
          + "		(\n"
          + "			select 	ultimo_lev.patient_id,\n"
          + "					case o.value_coded						\n"
          + "						when 6103 then 'D4T+3TC+LPV/r'\n"
          + "							when 792 then 'D4T+3TC+NVP'\n"
          + "							when 1827 then 'D4T+3TC+EFV'\n"
          + "							when 6102 then 'D4T+3TC+ABC'\n"
          + "							when 6116 then 'AZT+3TC+ABC'\n"
          + "							when 6330 then 'AZT+3TC+RAL+DRV/r (3ª Linha)'\n"
          + "							when 6105 then 'ABC+3TC+NVP'\n"
          + "							when 6325 then 'D4T+3TC+ABC+LPV/r (2ª Linha)'\n"
          + "							when 6326 then 'AZT+3TC+ABC+LPV/r (2ª Linha)'\n"
          + "							when 6327 then 'D4T+3TC+ABC+EFV (2ª Linha)'\n"
          + "							when 6328 then 'AZT+3TC+ABC+EFV (2ª Linha)'\n"
          + "							when 6109 then 'AZT+DDI+LPV/r (2ª Linha)'\n"
          + "							when 6106 then 'ABC+3TC+LPV/r'\n"
          + "							when 1313 then 'ABC+3TC+EFV(2ª Linha)'\n"
          + "							when 1311 then 'ABC+3TC+LPV/r(2ª Linha)'\n"
          + "							when 23799 then 'TDF+3TC+DTG(2ª Linha)'\n"
          + "							when 23800 then 'ABC+3TC+DTG(2ª Linha)'\n"
          + "							when 21163 then 'AZT+3TC+LPV/r(2ª Linha)'\n"
          + "							when 23801 then 'AZT+3TC+RAL(2ª Linha)'\n"
          + "							when 23802 then 'AZT+3TC+DRV/r(2ª Linha)'\n"
          + "							when 23815 then 'AZT+3TC+DTG(2ª Linha)'\n"
          + "							when 6329 then 'TDF+3TC+RAL+DRV/r(3ª Linha)'\n"
          + "							when 23803 then 'AZT+3TC+RAL+DRV/r(3ª Linha)'							\n"
          + "							when 1703 then 'AZT+3TC+EFV'\n"
          + "							when 6100 then 'AZT+3TC+LPV/r'\n"
          + "							when 1651 then 'AZT+3TC+NVP'\n"
          + "							when 6324 then 'TDF+3TC+EFV'\n"
          + "							when 6243 then 'TDF+3TC+NVP'\n"
          + "							when 6104 then 'ABC+3TC+EFV'\n"
          + "							when 23784 then 'TDF+3TC+DTG'\n"
          + "							when 23786 then 'ABC+3TC+DTG'\n"
          + "							when 23785 then 'TDF+3TC+DTG2'\n"
          + "							when 1311 then 'ABC+3TC+LPV/r(2ª Linha)'\n"
          + "							when 6108 then 'TDF+3TC+LPV/r(2ª Linha)'\n"
          + "							when 1314 then 'AZT+3TC+LPV/r(2ª Linha)'\n"
          + "							when 23790 then 'TDF+3TC+LPV/r+RTV(2ª Linha)'\n"
          + "							when 23791 then 'TDF+3TC+ATV/r(2ª Linha)'\n"
          + "							when 23792 then 'ABC+3TC+ATV/r(2ª Linha)'\n"
          + "							when 23793 then 'AZT+3TC+ATV/r(2ª Linha)'\n"
          + "							when 23795 then 'ABC+3TC+ATV/r+RAL(2ª Linha)'\n"
          + "							when 23796 then 'TDF+3TC+ATV/r+RAL(2ª Linha)'\n"
          + "							when 6329 then 'TDF+3TC+RAL+DRV/r(3ª Linha)'\n"
          + "							when 23797 then 'ABC+3TC++RAL+DRV/r(3ª Linha)'\n"
          + "							when 23798 then '3TC+RAL+DRV/r(3ª Linha)'					\n"
          + "					else 'OUTRO' end as ultimo_regime,\n"
          + "					ultimo_lev.encounter_datetime data_regime\n"
          + "			from 	obs o,				\n"
          + "					(	select p.patient_id,max(encounter_datetime) as encounter_datetime\n"
          + "						from 	patient p\n"
          + "								inner join encounter e on p.patient_id=e.patient_id	\n"
          + "								inner join obs o on o.encounter_id=e.encounter_id\n"
          + "						where 	encounter_type=18 and e.voided=0 and o.concept_id=1088 and \n"
          + "								encounter_datetime <= :endDate and e.location_id= :location and p.voided=0\n"
          + "						group by patient_id\n"
          + "					) ultimo_lev\n"
          + "			where 	o.person_id=ultimo_lev.patient_id and o.obs_datetime=ultimo_lev.encounter_datetime and o.voided=0 and \n"
          + "					o.concept_id=1088 and o.location_id= :location\n"
          + "		) regime on regime.patient_id=visita.patient_id\n"
          + "\n"
          + "		left join \n"
          + "		(	select e.patient_id,max(e.encounter_datetime) data_seguimento\n"
          + "			from encounter e\n"
          + "			inner join obs o on e.encounter_id=o.encounter_id\n"
          + "			where o.concept_id=856 and o.voided=0 and e.voided=0 and e.encounter_type in (6,9) and e.form_id = 163 and e.encounter_datetime <= :endDate\n"
          + "			group by e.patient_id\n"
          + "		) seguimento on seguimento.patient_id=carga1.patient_id\n"
          + "\n"
          + "		left join person_attribute pat on pat.person_id=inicio_real.patient_id and pat.person_attribute_type_id=9 and pat.value is not null and pat.value<>'' and pat.voided=0\n"
          + "group by carga1.patient_id";
}