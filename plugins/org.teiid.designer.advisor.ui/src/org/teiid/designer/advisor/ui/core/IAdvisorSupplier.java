package org.teiid.designer.advisor.ui.core;

import org.teiid.designer.advisor.ui.core.status.IStatusContentProvider;
import org.teiid.designer.advisor.ui.core.status.IStatusManager;

public interface IAdvisorSupplier {
	
	boolean isApplicable(Object target);

	IStatusContentProvider getStatusContentProvider();
	
	IStatusManager getStatusManager();
	
	ICheatSheetProvider getCheatSheetProvider();
	
	void changeContent(Object content);
	
	void shutdown();
	
	void startup();
}
