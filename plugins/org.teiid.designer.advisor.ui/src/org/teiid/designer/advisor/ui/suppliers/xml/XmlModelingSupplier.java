/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.suppliers.xml;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.teiid.designer.advisor.ui.core.AdvisorSupplierFactory;
import org.teiid.designer.advisor.ui.core.DefaultCheatSheetProvider;
import org.teiid.designer.advisor.ui.core.IAdvisorSupplier;
import org.teiid.designer.advisor.ui.core.ICheatSheetProvider;
import org.teiid.designer.advisor.ui.core.status.IStatusContentProvider;
import org.teiid.designer.advisor.ui.core.status.IStatusManager;
import org.teiid.designer.advisor.ui.scope.WebServicesModelingNature;
import org.teiid.designer.advisor.ui.scope.XmlModelingNature;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;

public class XmlModelingSupplier implements IAdvisorSupplier {
	private IStatusContentProvider statusProvider;
	private IStatusManager statusManager;
	private ICheatSheetProvider cheatSheetProvider;
	
	public XmlModelingSupplier() {
		super();
		
		this.statusProvider = new XmlModelingStatusProvider();
		this.statusManager = new XmlModelingStatusManager();
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
		return this.statusProvider;
	}

	@Override
	public boolean isApplicable(Object target) {
		if( target instanceof IResource ) {
			IResource res = (IResource)target;
			if (res instanceof IProject && ModelerCore.hasModelNature((IProject) res)) {
				boolean isXml = 
						AdvisorSupplierFactory.hasPrimaryProjectScopeNature( (IProject)res, XmlModelingNature.NATURE_ID) &&
						!AdvisorSupplierFactory.hasPrimaryProjectScopeNature( (IProject)res, WebServicesModelingNature.NATURE_ID);
				return isXml;
			} else if (ModelIdentifier.isRelationalSourceModel(res) || ModelIdentifier.isRelationalViewModel(res)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void shutdown() {
		this.statusProvider.shutdown();
		this.statusManager.shutdown();
		this.cheatSheetProvider.shutdown();
	}

	@Override
	public void startup() {
		this.statusProvider.startup();
		this.statusManager.startup();
		this.cheatSheetProvider.startup();
	}

}