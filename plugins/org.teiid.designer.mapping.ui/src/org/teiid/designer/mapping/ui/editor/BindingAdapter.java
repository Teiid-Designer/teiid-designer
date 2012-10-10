/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.editor;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.metamodels.transformation.InputBinding;
import org.teiid.designer.metamodels.transformation.InputParameter;
import org.teiid.designer.metamodels.transformation.MappingClassColumn;
import org.teiid.designer.metamodels.transformation.TransformationFactory;


/**
 * BindingAdapter Business Object A BindingAdapter has the following properties: (1) Item - Item that can be bound (2) Mapping -
 * object that is bound to the item
 * 
 * @author Jerry Helbling
 *
 * @since 8.0
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
