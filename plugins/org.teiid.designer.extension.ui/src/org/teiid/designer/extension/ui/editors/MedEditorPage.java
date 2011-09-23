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
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * 
 */
public abstract class MedEditorPage extends FormPage {

    private final ModelExtensionDefinition med;

    protected MedEditorPage( FormEditor medEditor,
                             String id,
                             String title,
                             ModelExtensionDefinition med ) {
        super(medEditor, id, title);
        CoreArgCheck.isNotNull(med, "MED is null"); //$NON-NLS-1$
        this.med = med;
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

    protected IMessageManager getMessageManager() {
        return ((ModelExtensionDefinitionEditor)getEditor()).getMessageManager();
    }

    protected ModelExtensionDefinition getModelExtensionDefinition() {
        return this.med;
    }

    protected ModelExtensionRegistry getRegistry() {
        return ExtensionPlugin.getInstance().getRegistry();
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
