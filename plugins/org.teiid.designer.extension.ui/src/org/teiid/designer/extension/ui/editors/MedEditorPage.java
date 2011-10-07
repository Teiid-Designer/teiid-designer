/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionValidator;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * 
 */
public abstract class MedEditorPage extends FormPage {

    private final ModelExtensionDefinitionValidator medValidator;

    protected MedEditorPage( FormEditor medEditor,
                             String id,
                             String title,
                             ModelExtensionDefinitionValidator medValidator ) {
        super(medEditor, id, title);
        CoreArgCheck.isNotNull(medValidator, "medValidator is null"); //$NON-NLS-1$
        this.medValidator = medValidator;
    }

    protected abstract void createBody( Composite body,
                                        FormToolkit toolkit );

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    @Override
    protected final void createFormContent( IManagedForm managedForm ) {
        createBody(managedForm.getForm().getBody(), managedForm.getToolkit());
        updateAllMessages();
    }

    protected ModelExtensionRegistry getRegistry() {
        return ExtensionPlugin.getInstance().getRegistry();
    }

    protected Shell getShell() {
        return getSite().getShell();
    }

    protected ModelExtensionDefinitionValidator getValidator() {
        return this.medValidator;
    }

    protected boolean isEditMode() {
        return this.medValidator.isEditMode();
    }

    protected abstract void updateAllMessages();

    protected void updateMessage( ErrorMessage errorMessage ) {
        IMessageManager msgMgr = ((ModelExtensionDefinitionEditor)getEditor()).getMessageManager();

        if (CoreStringUtil.isEmpty(errorMessage.message)) {
            msgMgr.removeMessage(errorMessage.getKey(), errorMessage.getControl());
        } else {
            msgMgr.addMessage(errorMessage.getKey(), errorMessage.getMessage(), errorMessage.getData(), IMessageProvider.ERROR,
                              errorMessage.getControl());
        }
    }

}
