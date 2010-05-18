package org.teiid.datatools.connectivity.ui.plan;

import org.eclipse.datatools.sqltools.plan.IPlanParser;
import org.eclipse.datatools.sqltools.plan.PlanRequest;
import org.eclipse.datatools.sqltools.plan.PlanService;
import org.eclipse.datatools.sqltools.plan.PlanSupportRunnable;

public class TeiidPlanService extends PlanService {
	
	private static IPlanParser teiidPlanParser = new TeiidPlanParser();
	
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
