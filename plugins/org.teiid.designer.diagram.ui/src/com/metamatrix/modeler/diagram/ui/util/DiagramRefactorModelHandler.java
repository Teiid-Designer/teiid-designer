/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.refactor.IRefactorModelHandler;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;

/**
 * This class provides the mapping plugin a mechanism to affect changes internal to any model during
 * Model refactoring operations. (i.e. Move & Rename)
 * 
 * In particular, a Rename of model could result in mis-named hrefs in diagram entity references that could become
 * stale.
 * 
 *
 */
public class DiagramRefactorModelHandler implements IRefactorModelHandler {

		public DiagramRefactorModelHandler() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void helpUpdateDependentModelContents(int type, ModelResource modelResource,
				Map refactoredPaths, IProgressMonitor monitor) {
			CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
			CoreArgCheck.isNotEmpty(refactoredPaths.values(), "refactoredPaths"); //$NON-NLS-1$
			
			
			try {
				for( Object diagram : modelResource.getModelDiagrams().getDiagrams() ) {
					DiagramEntityManager.cleanDiagramEntities((Diagram)diagram);
					
					DiagramEntityManager.cleanUpDiagram((Diagram)diagram);
				}
			} catch (ModelWorkspaceException e) {
				DiagramUiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
			}

		}
		
		@Override
		public void helpUpdateModelContents(int type, ModelResource modelResource,
				Map refactoredPaths, IProgressMonitor monitor) {
			CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
			CoreArgCheck.isNotEmpty(refactoredPaths.values(), "refactoredPaths"); //$NON-NLS-1$
			
			try {
				for( Object diagram : modelResource.getModelDiagrams().getDiagrams() ) {
					DiagramEntityManager.cleanDiagramEntities((Diagram)diagram);
					
					DiagramEntityManager.cleanUpDiagram((Diagram)diagram);
				}
			} catch (ModelWorkspaceException e) {
				DiagramUiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
			}

		}
}
