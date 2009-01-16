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

package com.metamatrix.modeler.mapping.ui.editor;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.transformation.InputBinding;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * BindingAdapter Business Object A BindingAdapter has the following properties: (1) Item - Item that can be bound (2) Mapping -
 * object that is bound to the item
 * 
 * @author Jerry Helbling
 */
public class BindingAdapter {

    private InputBinding inputBinding;
    private Object oItem;
    private Object oMapping;

    /**
     * Create a BindingAdapter given only the Item
     * 
     * @param attribute the target attribute
     */
    public BindingAdapter( InputBinding inputBinding ) {
        this.inputBinding = inputBinding;
        setMapping(inputBinding.getMappingClassColumn());
        setItem(inputBinding.getInputParameter());
    }

    /**
     * Create a BindingAdapter given only the Item
     * 
     * @param attribute the target attribute
     */
    public BindingAdapter( Object inputObject ) {
        this.inputBinding = null;
        setItem(inputObject);
    }

    /**
     * Create a BindingAdapter given only the Item
     * 
     * @param attribute the target attribute
     */
    public BindingAdapter( Object inputObject,
                           Object mapping ) {
        this.inputBinding = null;
        setItem(inputObject);
        setMapping(mapping);
    }

    /**
     * @return true if bound, false otherwise
     */
    public boolean isBound() {
        return (oMapping != null) ? true : false;
    }

    /**
     * @return item
     */
    public Object getItem() {
        return oItem;
    }

    /**
     * @return mapping
     */
    public Object getMapping() {
        return oMapping;
    }

    /**
     * Set the Item
     * 
     * @param oItem
     */
    public void setItem( Object oItem ) {
        if (inputBinding != null) {
            inputBinding.setInputParameter((InputParameter)oItem);
        }
        // don't lazily create a binding on the setItem, only on the setMapping.
        this.oItem = oItem;
    }

    /**
     * Set the mapping. This method may cause the InputBinding to be either created, if one did not already exist, or deleted, if
     * the mapping is set to null.
     * 
     * @param the mapping object. If this BindingAdapter's mapping was previously null, then setting this value to non-null will
     *        cause an InputBinding to be created. If an InputBinding already exists and this value is set to null, the
     *        InputBinding will be deleted.
     */
    public void setMapping( Object oMapping ) {
        // WRAP IN TRANSACTION (SIGNIFICANT)
        boolean requiredStart = ModelerCore.startTxn(true, true, "Set Mapping", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            if (this.inputBinding != null && oMapping == null) {
                // // delete the binding
                // ModelerCore.getModelEditor().delete(inputBinding);
                // 12/30/03 (LLP) Do nothing... binding is still valid without a mapping
                // changed to correct defect 10807
            } else if (this.inputBinding == null && oMapping instanceof MappingClassColumn) {
                // create the binding
                inputBinding = TransformationFactory.eINSTANCE.createInputBinding();
                inputBinding.setMappingClassColumn((MappingClassColumn)oMapping);
                inputBinding.setInputParameter((InputParameter)this.oItem);
                inputBinding.setMappingClassSet(((MappingClassColumn)oMapping).getMappingClass().getMappingClassSet());

                // set the type on the input parameter
                ((InputParameter)this.oItem).setType(((MappingClassColumn)oMapping).getType());
            }

            if (inputBinding != null) {
                // set the mapping onto the binding
                inputBinding.setMappingClassColumn((MappingClassColumn)oMapping);
            }
            succeeded = true;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        // store the mapping state in this object
        this.oMapping = oMapping;
    }

    public void delete() throws ModelerCoreException {
        if (this.inputBinding != null) {
            ModelerCore.getModelEditor().delete(inputBinding);
        }
        if (oItem instanceof EObject) {
            ModelerCore.getModelEditor().delete((EObject)this.getItem());
        }
    }

    public void deleteBinding() throws ModelerCoreException {
        if (this.inputBinding != null) {
            ModelerCore.getModelEditor().delete(inputBinding);
            this.inputBinding = null;
        }
    }

}
