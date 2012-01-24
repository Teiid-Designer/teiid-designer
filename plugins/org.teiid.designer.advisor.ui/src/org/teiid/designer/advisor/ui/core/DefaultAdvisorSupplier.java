package org.teiid.designer.advisor.ui.core;

import org.teiid.designer.advisor.ui.core.status.DefaultStatusManager;
import org.teiid.designer.advisor.ui.core.status.DefaultStatusProvider;
import org.teiid.designer.advisor.ui.core.status.IStatusContentProvider;
import org.teiid.designer.advisor.ui.core.status.IStatusManager;


public class DefaultAdvisorSupplier implements IAdvisorSupplier {

	private IStatusContentProvider statusContentProvider;
	private IStatusManager statusManager;
	private ICheatSheetProvider cheatSheetProvider;
	
	public DefaultAdvisorSupplier() {
		super();
		
		this.statusContentProvider = new DefaultStatusProvider();
		this.statusManager = new DefaultStatusManager();
		this.cheatSheetProvider = new DefaultCheatSheetProvider();
	}

	@Override
	public void changeContent(Object content) {
		// TODO Auto-generated method stub
	}

	@Override
	public ICheatSheetProvider getCheatSheetProvider() {
		return this.cheatSheetProvider;
	}

	@Override
	public IStatusManager getStatusManager() {
		return this.statusManager;
	}

	@Override
	public IStatusContentProvider getStatusContentProvider() {
		return this.statusContentProvider;
	}

	@Override
	public boolean isApplicable(Object target) {
		return true;
	}

	@Override
	public void shutdown() {
		this.statusContentProvider.shutdown();
		this.statusManager.shutdown();
		this.cheatSheetProvider.shutdown();
	}

	@Override
	public void startup() {
		this.statusContentProvider.startup();
		this.statusManager.startup();
		this.cheatSheetProvider.startup();
	}
}
