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
import org.teiid.designer.core.extension.deprecated.DeprecatedModelExtensionAssistant;
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
 * 
 */
public abstract class RestWarPropertiesAction extends SortableSelectionAction {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(RestWarPropertiesAction.class);

    // assistant responsible for new extension properties
    private final ModelExtensionAssistant assistant;

    // assistant responsible for old 7.4 extension properties
    private final DeprecatedModelExtensionAssistant deprecatedAssistant;

    private ModelResource modelResource;

    private Procedure procedure;

    public RestWarPropertiesAction() {
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(EXTENSION_PROPS_ICON));

        ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
        this.assistant = registry.getModelExtensionAssistant(NAMESPACE_PREFIX);

        // should not happen
        if (this.assistant == null) {
            UTIL.log(IStatus.ERROR, UTIL.getString(PREFIX + "missingRestModelExtensionAssistant")); //$NON-NLS-1$
        }

        // get the assistant that converts 7.4 extension properties
        this.deprecatedAssistant = (DeprecatedModelExtensionAssistant)registry.getModelExtensionAssistant(DeprecatedModelExtensionAssistant.NAMESPACE_PREFIX);

        // should not happen
        if (this.deprecatedAssistant == null) {
            UTIL.log(IStatus.ERROR, UTIL.getString(PREFIX + "missingDeprecatedModelExtensionAssistant")); //$NON-NLS-1$
        }
    }

    protected abstract String getErrorMessage();

    protected DeprecatedModelExtensionAssistant getOldAssistant() {
        return this.deprecatedAssistant;
    }

    protected ModelExtensionAssistant getNewAssistant() {
        return this.assistant;
    }

    protected abstract String getSuccessfulMessage();

    protected abstract String getTransactionName();

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    final public boolean isApplicable( final ISelection selection ) {
        return isValidSelection(selection);
    }

    protected abstract boolean isValidSelection( Procedure procedure );

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    final public boolean isValidSelection( ISelection selection ) {
        boolean enable = false;
        EObject eObject = SelectionUtilities.getSelectedEObject(selection);

        if (eObject != null) {
            this.modelResource = ModelUtilities.getModelResource(eObject);

            if ((this.modelResource != null) && ModelIdentifier.isVirtualModelType(this.modelResource)
                    && (eObject instanceof Procedure)) {
                this.procedure = (Procedure)eObject;
                enable = isValidSelection(this.procedure);
            }
        }

        // not a valid selection
        if (!enable) {
            this.procedure = null;
            this.modelResource = null;
        }

        return enable;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    final public void run() {
        boolean requiredStart = ModelerCore.startTxn(true, true, getTransactionName(), this);
        boolean succeeded = false;

        try {
            if (ModelEditorManager.autoOpen(null, this.procedure, true)) {
                // store REST MED in model
                ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
                ModelExtensionDefinition definition = registry.getDefinition(NAMESPACE_PREFIX);

                if (definition == null) {
                    // should not happen
                    UTIL.log(IStatus.ERROR, UTIL.getString(PREFIX + "missingRestModelExtensionDefinition")); //$NON-NLS-1$
                } else {
                    runImpl(this.procedure, definition);
                    succeeded = true;
                    MessageDialog.openInformation(null, null, getSuccessfulMessage());
                }
            }
        } catch (Exception e) {
            UTIL.log(e);
            MessageDialog.openInformation(null, null, getErrorMessage());
        } finally {
            // if necessary, commit transaction
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    protected abstract void runImpl( Procedure procedure,
                                        ModelExtensionDefinition definition ) throws Exception;

}
