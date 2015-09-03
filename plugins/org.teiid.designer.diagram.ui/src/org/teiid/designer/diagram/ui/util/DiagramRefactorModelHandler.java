/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.util;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.refactor.AbstractRefactorModelHandler;
import org.teiid.designer.core.workspace.ModelDiagrams;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.metamodels.diagram.Diagram;


/**
 * This class provides the mapping plugin a mechanism to affect changes internal to any model during
 * Model refactoring operations. (i.e. Move & Rename)
 * 
 * In particular, a Rename of model could result in mis-named hrefs in diagram entity references that could become
 * stale.
 * 
 *
 *
 * @since 8.0
 */
public class DiagramRefactorModelHandler extends AbstractRefactorModelHandler {
	
	@Override
	public boolean preProcess(RefactorType refactorType, IResource refactoredResource, IProgressMonitor monitor) {
		CoreArgCheck.isNotNull(refactoredResource, "modelResource"); //$NON-NLS-1$
		
		try {
			if(! ModelUtil.isXmiFile(refactoredResource))
			    return true;
			    
			ModelResource modelResource = ModelUtil.getModel(refactoredResource);
			if (modelResource == null)
			    return true; // If no resource then no diagrams to clean up!

			ModelDiagrams modelDiagrams = modelResource.getModelDiagrams();
			if (modelDiagrams != null) {
			    for( Object diagram : modelDiagrams.getDiagrams() ) {
			        DiagramEntityManager.cleanDiagramEntities((Diagram)diagram);

			        DiagramEntityManager.cleanUpDiagram((Diagram)diagram);
			    }
			}

		} catch (ModelWorkspaceException e) {
			DiagramUiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
			return false;
		}

		return true;
	}
}
