/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.viewsupport.ModelUtilities;



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
    @Override
	public CellEditor createPropertyEditor(Composite theParent) {
        return null;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getCategory()
     * @since 4.3
     */
    @Override
	public String getCategory() {
        return Util.getStringOrKey(PREFIX + "category"); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getDescription()
     * @since 4.3
     */
    @Override
	public String getDescription() {
        return Util.getStringOrKey(PREFIX + "description"); //$NON-NLS-1$
    }

    @Override
	public String getDisplayName() {
        return Util.getStringOrKey(PREFIX + "displayName"); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getFilterFlags()
     * @since 4.3
     */
    @Override
	public String[] getFilterFlags() {
        return null;
    }

    /**
     * @see org.teiid.designer.ui.properties.ITransientPropertyDescriptor#getPropertyValue()
     * @since 4.3
     */
    @Override
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

                if (!CoreStringUtil.isEmpty(namespaceUri)) {
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
    @Override
	public Object getHelpContextIds() {
        return null;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getId()
     * @since 4.3
     */
    @Override
	public Object getId() {
        return Util.getStringOrKey(PREFIX + "id"); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getLabelProvider()
     * @since 4.3
     */
    @Override
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
    @Override
	public boolean isCompatibleWith(IPropertyDescriptor theAnotherProperty) {
        return false;
    }

    /**
     * @see org.teiid.designer.ui.properties.ITransientPropertyDescriptor#setObject(java.lang.Object)
     * @since 4.3
     */
    @Override
	public void setObject(Object theObject) {
        if ((theObject != null) && supports(theObject)) {
            this.obj = (EObject)theObject;
        } else {
            throw new IllegalArgumentException(Util.getString(PREFIX + "errorMsg.objectNotSupported", this.obj)); //$NON-NLS-1$
        }
    }

    /**
     * @see org.teiid.designer.ui.properties.ITransientPropertyDescriptor#supports(java.lang.Object)
     * @since 4.3
     */
    @Override
	public boolean supports(Object theObject) {
        return (theObject instanceof EObject);
    }
}
