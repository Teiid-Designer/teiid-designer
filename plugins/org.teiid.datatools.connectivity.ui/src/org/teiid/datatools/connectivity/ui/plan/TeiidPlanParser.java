package org.teiid.datatools.connectivity.ui.plan;

import org.eclipse.datatools.sqltools.plan.IExecutionPlanDocument;
import org.eclipse.datatools.sqltools.plan.IPlanParser;
import org.eclipse.datatools.sqltools.plan.treeplan.TreeExecutionPlanDocument;
import org.eclipse.datatools.sqltools.plan.treeplan.TreePlanNodeComponent;
import org.eclipse.datatools.sqltools.plan.treeplan.TreePlanNodeComposite;

public class TeiidPlanParser implements IPlanParser {

	@Override
	public IExecutionPlanDocument[] parsePlan(String rawPlan) {
		TreeExecutionPlanDocument result;
		
		//TreePlanNodeComposite
		//TreePlanNodeLeaf
		
		TreePlanNodeComponent rootNode = new TreePlanNodeComposite();
		
		String planName = "Teiid Plan"; //$NON-NLS-1$
		result = new TreeExecutionPlanDocument(rootNode, planName, rawPlan);
		return new TreeExecutionPlanDocument[]{result};
	}

}
