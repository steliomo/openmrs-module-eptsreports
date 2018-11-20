/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.metadata;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants;
import org.springframework.stereotype.Component;

@Component("commonMetadata")
public class CommonMetadata extends Metadata {
	
	public Concept getYesConcept() {
		String uuid = Context.getAdministrationService().getGlobalProperty(EptsReportConstants.GLOBAL_PROPERTY_YES_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
	public Concept getstartDrugsConcept() {
		String uuid = Context.getAdministrationService()
		        .getGlobalProperty(EptsReportConstants.GLOBAL_PROPERTY_START_DRUGS_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
	public Concept gethistoricalDrugStartDateConcept() {
		String uuid = Context.getAdministrationService()
		        .getGlobalProperty(EptsReportConstants.GLOBAL_PROPERTY_HISTORICAL_START_DATE_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
}
