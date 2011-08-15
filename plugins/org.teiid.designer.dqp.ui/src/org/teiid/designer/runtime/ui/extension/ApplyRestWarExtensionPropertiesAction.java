/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.extension;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.Images.EXTENSION_PROPS_ICON;
import static org.teiid.designer.runtime.extension.rest.RestModelExtensionConstants.NAMESPACE_PREFIX;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * Action to apply REST WAR generation extension properties to virtual procedures.
 */
public class ApplyRestWarExtensionPropertiesAction extends SortableSelectionAction {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ApplyRestWarExtensionPropertiesAction.class);

    private ModelResource modelResource;

    private Procedure procedure;

    public ApplyRestWarExtensionPropertiesAction() {
        super();
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(EXTENSION_PROPS_ICON));
    }

    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public boolean isApplicable( final ISelection selection ) {
        return isValidSelection(selection);
    }

    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        EObject eObject = SelectionUtilities.getSelectedEObject(selection);

        if (eObject != null) {
            this.modelResource = ModelUtilities.getModelResource(eObject);

            if ((modelResource != null) && ModelIdentifier.isVirtualModelType(modelResource) && (eObject instanceof Procedure)) {
                this.procedure = (Procedure)eObject;
                return true;
            }
        }

        // not a valid selection
        this.procedure = null;
        this.modelResource = null;
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        boolean requiredStart = ModelerCore.startTxn(true, true, "Set Model Resource Extended Properties", this); //$NON-NLS-1$
        boolean succeeded = false;

        try {
            if (ModelEditorManager.autoOpen(null, this.procedure, true)) {
                // store REST MED in model
                ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
                ModelExtensionAssistant assistant = registry.getModelExtensionAssistant(NAMESPACE_PREFIX);

                if (assistant == null) {
                    // should not happen
                    UTIL.log(IStatus.ERROR, UTIL.getString(PREFIX + "missingRestModelExtensionAssistant")); //$NON-NLS-1$
                } else {
                    ModelExtensionDefinition definition = registry.getDefinition(NAMESPACE_PREFIX);

                    if (definition == null) {
                        // should not happen
                        UTIL.log(IStatus.ERROR, UTIL.getString(PREFIX + "missingRestModelExtensionDefinition")); //$NON-NLS-1$
                    } else {
                        assistant.saveModelExtensionDefinition(this.procedure, definition);

                        // need to wiggle the selection so that the properties view gets the new properties
                        IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();

                        if (part != null) {
                            ISelectionProvider selectionProvider = part.getSite().getSelectionProvider();

                            if (selectionProvider != null) {
                                selectionProvider.setSelection(StructuredSelection.EMPTY);
                                selectionProvider.setSelection(new StructuredSelection(this.procedure));
                            }
                        }

                        succeeded = true;
                        MessageDialog.openInformation(null, null, UTIL.getString(PREFIX + "restExtensionPropertiesSaved")); //$NON-NLS-1$
                    }
                }
            }
        } catch (Exception e) {
            UTIL.log(e);
            MessageDialog.openInformation(null, null, UTIL.getString(PREFIX + "errorApplyingRestExtensionProperties")); //$NON-NLS-1$
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
    }

}
