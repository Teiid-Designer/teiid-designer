/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.ui.provider.PropertyDescriptor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectPathLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

/**
 * ModelObjectPropertyDescriptor Extension to PropertyDescriptor to use a {@link AccumulatorDialog} rather than a
 * FeatureEditorDialog.
 */
public class ModelObjectPropertyDescriptor extends PropertyDescriptor {

    public static final String OPEN_EDITOR_TITLE = UiConstants.Util.getString("ModelObjectPropertyDescriptor.openModelEditorTitle"); //$NON-NLS-1$
    public static final String OPEN_EDITOR_MESSAGE = UiConstants.Util.getString("ModelObjectPropertyDescriptor.openModelEditorMessage"); //$NON-NLS-1$
    public static final String ALWAY_FORCE_OPEN_MESSAGE = UiConstants.Util.getString("ModelObjectPropertyDescriptor.alwaysForceOpenMessage"); //$NON-NLS-1$

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

        // check failure conditions: ModelResource is null, or read-only, or not open in an editor
        if (object instanceof EObject) {
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject((EObject)object);

            if (modelResource == null) {
                // if the modelResource is null, we can't edit the properties
                return null;
            }
            if (ModelUtil.isIResourceReadOnly(modelResource.getResource())) {
                if (showReadOnlyDialog) {
                    Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
                    MessageDialog.openError(shell,
                                            ReadOnlyPropertyDescriptor.READ_ONLY_TITLE,
                                            ReadOnlyPropertyDescriptor.READ_ONLY_MESSAGE);
                }
                return null;
            }

            if (!lazyLoadValues) {
                IFile file = (IFile)modelResource.getResource();
                if (file != null) {
                    if (!ModelEditorManager.isOpen(file)) {
                        // Let's get the preferenced value for auto-open-editor
                        String autoOpen = UiPlugin.getDefault().getPreferenceStore().getString(PluginConstants.Prefs.General.AUTO_OPEN_EDITOR_IF_NEEDED);
                        // if the preference is to auto-open, then set forceOpen so we don't prompt the user
                        boolean forceOpen = false;
                        if (autoOpen.equals(MessageDialogWithToggle.ALWAYS)) {
                            forceOpen = true;
                        } else if (autoOpen.equals(MessageDialogWithToggle.NEVER)) {
                            forceOpen = false;
                        }

                        if (!forceOpen) {
                            // can't modify a property value on an EObject if it's ModelEditor is not open.
                            Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
                            MessageDialogWithToggle tDialog = MessageDialogWithToggle.openYesNoCancelQuestion(shell,
                                                                                                              OPEN_EDITOR_TITLE,
                                                                                                              OPEN_EDITOR_MESSAGE,
                                                                                                              ALWAY_FORCE_OPEN_MESSAGE,
                                                                                                              false,
                                                                                                              UiPlugin.getDefault().getPreferenceStore(),
                                                                                                              PluginConstants.Prefs.General.AUTO_OPEN_EDITOR_IF_NEEDED);
                            int result = tDialog.getReturnCode();
                            switch (result) {
                                // yes, ok
                                case IDialogConstants.YES_ID:
                                case IDialogConstants.OK_ID:
                                    forceOpen = true;
                                    break;
                                // no
                                case IDialogConstants.NO_ID:
                                    forceOpen = false;
                                    break;
                            }
                        }

                        if (forceOpen) {
                            ModelEditorManager.open((EObject)object, true);
                        }

                        return null;
                    }
                }
            }
        }

        return PropertyEditorFactory.createPropertyEditor(composite, itemPropertyDescriptor, this, object, lazyLoadValues);
    }

    public Object getFeature() {
        return itemPropertyDescriptor.getFeature(object);
    }
}// end ModelObjectPropertyDescriptor

