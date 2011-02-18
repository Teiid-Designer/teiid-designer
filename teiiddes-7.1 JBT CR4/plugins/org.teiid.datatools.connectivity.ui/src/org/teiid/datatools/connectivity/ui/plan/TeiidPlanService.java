/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.ui.plan;

import org.eclipse.datatools.sqltools.plan.IPlanParser;
import org.eclipse.datatools.sqltools.plan.PlanRequest;
import org.eclipse.datatools.sqltools.plan.PlanService;
import org.eclipse.datatools.sqltools.plan.PlanSupportRunnable;

public class TeiidPlanService extends PlanService {
	
	private static IPlanParser teiidPlanParser = new TeiidPlanParser();
	
	@Override
    public PlanSupportRunnable createPlanSupportRunnable(
			final PlanRequest request, final String profileName,
			final String dbName) {
		return new TeiidPlanSupportRunnable(request, profileName, dbName);
	}

	@Override
	public IPlanParser getPlanParser() {
		return teiidPlanParser;
	}


}
