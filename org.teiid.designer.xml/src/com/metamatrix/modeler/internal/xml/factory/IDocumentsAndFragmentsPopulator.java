/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
