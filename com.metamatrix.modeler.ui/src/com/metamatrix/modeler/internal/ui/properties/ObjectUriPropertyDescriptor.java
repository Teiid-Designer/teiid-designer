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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.properties.ITransientPropertyDescriptor;


class ObjectUriPropertyDescriptor implements ITransientPropertyDescriptor,
                                             UiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ObjectUriPropertyDescriptor.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private EObject obj;
    private ILabelProvider labelProvider;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     * @since 4.3
     */
    public CellEditor createPropertyEditor(Composite theParent) {
        return null;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getCategory()
     * @since 4.3
     */
    public String getCategory() {
        return Util.getStringOrKey(PREFIX + "category"); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getDescription()
     * @since 4.3
     */
    public String getDescription() {
        return Util.getStringOrKey(PREFIX + "description"); //$NON-NLS-1$
    }

    public String getDisplayName() {
        return Util.getStringOrKey(PREFIX + "displayName"); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getFilterFlags()
     * @since 4.3
     */
    public String[] getFilterFlags() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.ui.properties.ITransientPropertyDescriptor#getPropertyValue()
     * @since 4.3
     */
    public Object getPropertyValue() {
        Object result = null;

        if (this.obj == null) {
            throw new IllegalStateException(Util.getStringOrKey(PREFIX + "errorMsg.objectNotSet")); //$NON-NLS-1$
        }

        // make sure we can get a model resource
        ModelResource model = ModelUtilities.getModelResourceForModelObject(this.obj);

        // global/external resources won't have a ModelResource
        if (model != null) {
            try {
                String namespaceUri = model.getModelAnnotation().getNamespaceUri();

                if (!StringUtil.isEmpty(namespaceUri)) {
                    result = new StringBuffer().append(namespaceUri)
                                               .append(DatatypeConstants.URI_REFERENCE_DELIMITER)
                                               .append(ModelerCore.getObjectIdString(obj))
                                               .toString();
                }
            } catch (ModelWorkspaceException theException) {
                throw new IllegalStateException(Util.getString(PREFIX + "errorMsg.modelAnnotationProblem", this.obj)); //$NON-NLS-1$
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getHelpContextIds()
     * @since 4.3
     */
    public Object getHelpContextIds() {
        return null;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getId()
     * @since 4.3
     */
    public Object getId() {
        return Util.getStringOrKey(PREFIX + "id"); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getLabelProvider()
     * @since 4.3
     */
    public ILabelProvider getLabelProvider() {
        if (labelProvider == null) {
            labelProvider = new LabelProvider();
        } // endif

        return labelProvider;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#isCompatibleWith(org.eclipse.ui.views.properties.IPropertyDescriptor)
     * @since 4.3
     */
    public boolean isCompatibleWith(IPropertyDescriptor theAnotherProperty) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.properties.ITransientPropertyDescriptor#setObject(java.lang.Object)
     * @since 4.3
     */
    public void setObject(Object theObject) {
        if ((theObject != null) && supports(theObject)) {
            this.obj = (EObject)theObject;
        } else {
            throw new IllegalArgumentException(Util.getString(PREFIX + "errorMsg.objectNotSupported", this.obj)); //$NON-NLS-1$
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.properties.ITransientPropertyDescriptor#supports(java.lang.Object)
     * @since 4.3
     */
    public boolean supports(Object theObject) {
        return (theObject instanceof EObject);
    }
}
