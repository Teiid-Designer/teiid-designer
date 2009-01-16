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

package com.metamatrix.modeler.internal.ui.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import org.eclipse.emf.common.ui.celleditor.ExtendedDialogCellEditor;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;

import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.internal.ui.refactor.NamespaceUriRenameDialog;
import com.metamatrix.modeler.ui.properties.IPropertyEditorFactory;


/** 
 * @since 4.3
 */
public final class CoreModelPropertyEditorFactory implements IPropertyEditorFactory {

    /** 
     * @see com.metamatrix.modeler.ui.properties.IPropertyEditorFactory#supportsStructuralFeature(org.eclipse.emf.ecore.EStructuralFeature)
     * @since 4.3
     */
    public boolean supportsStructuralFeature(EStructuralFeature theFeature) {
        boolean result = false;
        
        if (ModelAnnotation.class == theFeature.getContainerClass()) {
            switch (theFeature.getFeatureID()) {
                case CorePackage.MODEL_ANNOTATION__NAMESPACE_URI: {
                    result = true;
                    break;
                } default: {
                    result = false;
                    break;
                }
            }
        }
        
        return result;
    }

    /** 
     * @see com.metamatrix.modeler.ui.properties.IPropertyEditorFactory#createPropertyEditor(org.eclipse.swt.widgets.Composite, org.eclipse.emf.edit.provider.IItemPropertyDescriptor, org.eclipse.ui.views.properties.IPropertyDescriptor, java.lang.Object)
     * @since 4.3
     */
    public CellEditor createPropertyEditor(Composite theComposite,
                                           IItemPropertyDescriptor theItemPropertyDescriptor,
                                           IPropertyDescriptor thePropertyDescriptor,
                                           Object theObject) {
        CellEditor result = null;
        
        if (theObject instanceof ModelAnnotation) {
            if (!theItemPropertyDescriptor.canSetProperty(theObject)) {
                return null;
            }
            EStructuralFeature feature = (EStructuralFeature)theItemPropertyDescriptor.getFeature(theObject);
            
            if (feature.getFeatureID() == CorePackage.MODEL_ANNOTATION__NAMESPACE_URI) {
                result = new ExtendedDialogCellEditor(theComposite, thePropertyDescriptor.getLabelProvider()) {
	                @Override
                    protected Object openDialogBox(Control theCellEditorWindow) {
	                    NamespaceUriRenameDialog dialog = new NamespaceUriRenameDialog(theCellEditorWindow.getShell(),
	                                                                                   (String)getValue());
                        setValidator(dialog.getValidator());

	                    // construct/show dialog
	                    dialog.create();
	                    dialog.open();

	                    // get the appropriate value
	                    return dialog.getUri();
	                }
                };
            }
        }
        
        return result;
    }

}
