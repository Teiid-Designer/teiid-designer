/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

import com.metamatrix.core.util.CoreStringUtil;

/**
 * The <code>MedEditorPage</code> is a base class for {@link ModelExtensionDefinitionEditor} pages that require the model extension
 * definition that is being edited.
 */
public abstract class MedEditorPage extends FormPage {

    protected MedEditorPage( ModelExtensionDefinitionEditor medEditor,
                             String id,
                             String title ) {
        super(medEditor, id, title);
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

    /**
     * @return the MED being modified by the GUI (never <code>null</code>)
     */
    protected ModelExtensionDefinition getMed() {
        return getMedEditor().getMed();
    }

    protected ModelExtensionDefinitionEditor getMedEditor() {
        return (ModelExtensionDefinitionEditor)getEditor();
    }

    protected ModelExtensionRegistry getRegistry() {
        return ExtensionPlugin.getInstance().getRegistry();
    }

    protected Shell getShell() {
        return getSite().getShell();
    }

    /**
     * @param e the property change event being handled (never <code>null</code>)
     */
    protected abstract void handlePropertyChanged( PropertyChangeEvent e );

    protected abstract void updateAllMessages();

    protected void updateMessage( ErrorMessage errorMessage ) {
        IMessageManager msgMgr = ((ModelExtensionDefinitionEditor)getEditor()).getMessageManager();

        if (CoreStringUtil.isEmpty(errorMessage.getMessage())) {
            msgMgr.removeMessage(errorMessage.getKey(), errorMessage.getControl());
        } else {
            msgMgr.addMessage(errorMessage.getKey(), errorMessage.getMessage(), errorMessage.getData(), IMessageProvider.ERROR,
                              errorMessage.getControl());
        }
    }

}
