/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.ui.provider.PropertyDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectPathLabelProvider;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

/**
 * ModelObjectPropertyDescriptor Extension to PropertyDescriptor
 */
public class ModelObjectPropertyDescriptor extends PropertyDescriptor {

    private boolean showReadOnlyDialog = true;
    private boolean lazyLoadValues = false;

    public ModelObjectPropertyDescriptor( Object object,
                                          IItemPropertyDescriptor itemPropertyDescriptor ) {
        super(object, itemPropertyDescriptor);
    }

    public void setShowReadOnlyDialog( boolean enable ) {
        showReadOnlyDialog = enable;
    }

    public void setLazyLoadValues( boolean enable ) {
        lazyLoadValues = enable;
    }

    public Object getObject() {
        return this.object;
    }

    /**
     * @see org.eclipse.emf.edit.ui.provider.PropertyDescriptor#getLabelProvider()
     * @since 4.2
     */
    @Override
    public ILabelProvider getLabelProvider() {
        Object feature = itemPropertyDescriptor.getFeature(object);
        if (feature instanceof EReference) {
            int upperBound = ((EReference)feature).getUpperBound();
            if (upperBound > 1 || upperBound == ETypedElement.UNBOUNDED_MULTIPLICITY) {
                return (getLabelProvider(false));
            }
        }
        return getLabelProvider(true);
    }

    /**
     * Obtains a <code>ILabelProvider</code> whose text of an {@link EObject} includes a location.
     * 
     * @param theUseSuperFlag the flag indicating if the location label provider should be used
     * @return the label provider
     * @since 4.2
     */
    ILabelProvider getLabelProvider( boolean theUseLocationFlag ) {
        ILabelProvider result = null;

        if (theUseLocationFlag && (getObject() instanceof EObject)) {
            result = new ModelObjectPathLabelProvider();
        } else {
            result = super.getLabelProvider();
        }

        return result;
    }

    /**
     * The <code>ModelObjectLocationLabelProvider</code> provides location information for each <code>EObject</code>.
     * 
     * @since 4.2
     */
    class ModelObjectLocationLabelProvider extends LabelProvider {
        ILabelProvider delegate = new ModelObjectPathLabelProvider();

        @Override
        public String getText( Object theElement ) {
            return (theElement instanceof EObject) ? delegate.getText(theElement) : getLabelProvider(false).getText(theElement);
        }
    }

    /**
     * Return the cell editor provided by EMF
     * 
     * @param composite
     * @return
     * @since 4.2
     */
    public CellEditor createDelegatePropertyEditor( Composite composite ) {
        return super.createPropertyEditor(composite);
    }

    /**
     * Overridden from {@link PropertyDescriptor}. This returns the cell editor that will be used to edit the value of this
     * property. This default implementation determines the type of cell editor from the nature of the structural feature.
     */
    @Override
    public CellEditor createPropertyEditor( Composite composite ) {
        if (!itemPropertyDescriptor.canSetProperty(object)) {
            return null;
        }

        if (object instanceof EObject) {
            if (!lazyLoadValues) {
                if (!ModelEditorManager.autoOpen(UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(), (EObject)object,
                                                 this.showReadOnlyDialog)) {
                    return null;
                }
            }
        }

        return PropertyEditorFactory.createPropertyEditor(composite, itemPropertyDescriptor, this, object, lazyLoadValues);
    }

    public Object getFeature() {
        return itemPropertyDescriptor.getFeature(object);
    }
}
