/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.editor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.metamatrix.metamodels.transformation.InputBinding;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;

/**
 * InputSet Business Object that the InputSet Panel works with
 */
public class InputSetAdapter {

    // ============================================================
    // Instance variables
    // ============================================================
    private MappingClass mappingClass; // the MappingClass for this InputSet
    private BindingList bindingList;

    // jhTODO:
    private Collection colParentResultSets;

    // ============================================================
    // Constructors
    // ============================================================
    /**
     * Constructor.
     * 
     * @param mappingClass the MappingClass that contains the InputSet.
     */
    public InputSetAdapter( MappingClass mappingClass ) {
        this.mappingClass = mappingClass;
        init(this.mappingClass);
    }

    // ============================================================
    // Instance methods
    // ============================================================

    public InputSet getInputSet() {
        return this.mappingClass.getInputSet();
    }

    /**
     * Initialize the object.
     * 
     * @param mappingRoot the TransformationMappingRoot object
     */
    private void init( MappingClass mappingClass ) {
        // Get the target Columns and find any Bindings

        if (mappingClass != null) {
            List inputs = mappingClass.getInputSet().getInputParameters();

            List bindings = mappingClass.getMappingClassSet().getInputBinding();
            HashMap bindingMap = new HashMap();

            Iterator iter = bindings.iterator();
            while (iter.hasNext()) {
                InputBinding inputBinding = (InputBinding)iter.next();
                InputParameter testInput = inputBinding.getInputParameter();

                if (testInput == null) {
                    // this binding has been orphaned - delete it
                    mappingClass.getMappingClassSet().getInputBinding().remove(inputBinding);
                } else if (inputs.contains(testInput)) {
                    BindingAdapter binding = new BindingAdapter(inputBinding);
                    bindingMap.put(inputBinding.getInputParameter(), binding);
                }
            }

            iter = inputs.iterator();
            while (iter.hasNext()) {
                InputParameter input = (InputParameter)iter.next();
                BindingAdapter binding = (BindingAdapter)bindingMap.get(input);
                if (binding == null) {
                    binding = new BindingAdapter(input);
                }
                getBindingList().add(binding);
            }

        }

    }

    /**
     * jhTODO: REWRITE ... all this can do now is present a count of items; saves scrolling? Get the status string for the current
     * state of the object
     * 
     * @return the current reconciled status
     */
    public String getStatus() {
        String message = ""; //$NON-NLS-1$

        return message;
    }

    /**
     * Get BindingAdapter List
     * 
     * @return the list of Bindings for this object
     */
    public BindingList getBindingList() {
        if (bindingList == null) {
            bindingList = new BindingList();
        }

        return bindingList;
    }

    /**
     * jhTODO: How will this work? What is data, what is model? Get TreeModel
     * 
     * @return the list of SQL symbols for this object
     */
    public Collection getResultSetsTreeModel() {
        return colParentResultSets;

    }

    /**
     * Refresh lists
     */
    public void refresh() {
        getBindingList().refresh(true);
    }

    public void refreshFromMetadata() {
        // jhTODO: write this one. it should use the business object to rebuild all content
        /*  
         *  1. retrieve 'parentResultSets' and use the to create data for the Tree
         *  
         *  2. remove any current bindings that are not supported by the 
         *     new ResultSets
         *  
         * 
         */
    }

    public void updateMetadata() {
        // jhTODO: write this one. it should use the business object to rebuild all content
        /* FROM the BusinessObject...
         *  1. retrieve 'parentResultSets' and use the to create data for the Tree
         *  
         *  2. remove any current bindings that are not supported by the 
         *     new ResultSets
         *  
         * 
         */
    }

    /**
     * Add a new binding to the end of the bindings list
     * 
     * @param binding the binding to add
     */
    public void addBinding( BindingAdapter binding ) {
        getBindingList().add(binding);
    }

}
