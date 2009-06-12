/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;

import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.ui.internal.widget.InheritanceCheckboxTreeViewer;

/**
 * IStructuralCopyTreePopulator
 */
public interface IStructuralCopyTreePopulator {
	/**
	 * Populate the tree based on some means which must be contained internally.
	 * 
	 * @param viewer    InheritanceCheckboxTreeViewer for the tree.  NOTE-- it is the 
	 * 					implementor's responsibility to initially set the checkboxes as desired
	 * @param theModel	ModelResource for the model
	 * @param targetIsVirtual	true if target is a virtual model (affects filtering)
	 */
	void populateModelFeaturesTree(TreeViewer viewer,
			ModelResource theModel, boolean targetIsVirtual);
	
	/**
	 * Copy selected features of an internally contained model to a new model file.
	 * 
     * @param sourceModelResource modelResource containing the old information
     * @param targetModelResource modelResource to contain the new model
	 * @param viewer		InheritanceCheckboxTreeViewer for the model-- contains method to determine which nodes are selected for copy
     * @param extraProperties       optional properties to tweak creation of objects.
     * @param copyAllDescriptions   option to copy or supress coying all descriptions
	 * @param monitor      	progress monitor to display during operation
	 * @throws ModelerCoreException       could possibly occur in doing the copy
	 * @throws StructuralCopyException    could possibly occur in doing the copy
	 */
	void copyModel(ModelResource sourceModelResource, ModelResource targetModelResource, InheritanceCheckboxTreeViewer viewer, 
			Map extraProperties, boolean copyAllDescriptions, IProgressMonitor monitor) 
			throws ModelerCoreException, StructuralCopyException;
	
	/**
	 * Copy selected features of an internally contained model to a new model file.
	 * 
     * @param sourceModelResource modelResource containing the old information
     * @param targetModelResource modelResource to contain the new model
     * @param extraProperties       optional properties to tweak creation of objects.
     * @param copyAllDescriptions   option to copy or supress coying all descriptions
	 * @param monitor      	progress monitor to display during operation
	 * @throws ModelerCoreException       could possibly occur in doing the copy
	 * @throws StructuralCopyException    could possibly occur in doing the copy
	 */
	void copyModel(ModelResource sourceModelResource, ModelResource targetModelResource,
			Map extraProperties, boolean copyAllDescriptions, IProgressMonitor monitor) 
			throws ModelerCoreException, StructuralCopyException;
}
