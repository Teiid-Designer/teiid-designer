/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.xml.factory;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.mapping.factory.MappingClassBuilderStrategy;

/**
 * IDocumentsFragmentsPopulator
 * 
 * Interface used by NewVirtualDocumentWizardPage to initially populate the documents
 * and fragments accumulators.  Also returns the item name.
 */
public interface IDocumentsAndFragmentsPopulator {

    /**
     * Get the item.
     * 
     * @return the Object that this instance is using;
     */
    Object getItem();

	/**
	 * Get a String for the item display name.
	 * 
	 * @return Item display name;
	 */
	String getItemName();
	
	/**
	 * Get initial population for available (lefthand) side of documents accumulator
	 * 
	 * @return  Items to be placed in available (lefthand) side of documents accumulator
	 */
	Collection /*<Object>*/ getInitialAvailableDocuments();
	
	/**
	 * Get initial population for selected (righthand) side of documents accumulator
	 * 
	 * @return  Items to be placed in selected (righthand) side of documents accumulator
	 */
	Collection /*<Object>*/ getSelectedDocuments();
	
	/**
	 * Get initial population for available (lefthand) side of fragments accumulator
	 * 
	 * @return  Items to be placed in available (lefthand) side of fragments accumulator
	 */
	Collection /*<Object>*/ getInitialAvailableFragments();
	
	/**
	 * Get initial population for selected (righthand) side of fragments accumulator
	 * 
	 * @return  Items to be placed in selected (righthand) side of fragments accumulator
	 */
	Collection /*<Object>*/ getSelectedFragments();
    
    Collection getUnhandledModelImports();
	
	/**
	 * Inform the implementor of the current selected (righthand) side items in the
	 * documents accumulator.  Class handling the accumulator should call this method
	 * whenever a change has been made to the selected side.  Passes in the entire
	 * list of selected items, not a delta.
	 * 
	 * @param  selectedItems  current entire set of selected items
	 */
	void setSelectedDocuments(Collection /*<Object>*/ selectedItems);
	
	/**
	 * Inform the implementor of the current selected (righthand) side items in the
	 * fragments accumulator.  Class handling the accumulator should call this method
	 * whenever a change has been made to the selected side.  Passes in the entire
	 * list of selected items, not a delta.
	 * 
	 * @param  selectedItems  current entire set of selected items
	 */
	void setSelectedFragments(Collection /*<Object>*/ selectedItems);

    /**
     * Request implementor to build the model.
     * 
     * @param modelResource   model resource
     * @param buildEntireDocuments    if set, build the entire documents 
     * @param monitor         progress monitor
     */
    XmlFragment[] buildModel(ModelResource modelResource, boolean buildEntireDocuments, boolean buildMappingClasses,
            MappingClassBuilderStrategy strategy, IProgressMonitor monitor);

    /** Generate mapping classes for the specified Fragment
      * @param treeNode the fragment to work with.
      */
    void buildMappingClasses(XmlFragment treeNode, MappingClassBuilderStrategy strategy); 
    
    /** Get the estimated count of nodes built from the last call to buildModel.
      * @return an int giving a reasonable estimate of the number of nodes generated.
      */
    int getLastEstimatedNodeCount();
}
