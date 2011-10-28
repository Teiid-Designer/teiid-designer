/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import java.beans.PropertyChangeEvent;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

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

    /**
     * @param body the parent UI control where all other controls should be added (never <code>null</code>)
     * @param toolkit the form toolkit to use when creating form controls (never <code>null</code>)
     */
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
        setResourceReadOnly(isReadonly());
    }

    /**
     * @return the MED being modified by the GUI (never <code>null</code>)
     */
    protected ModelExtensionDefinition getMed() {
        return getMedEditor().getMed();
    }

    /**
     * @return the model extension definition editor that this page belongs to (never <code>null</code>)
     */
    protected ModelExtensionDefinitionEditor getMedEditor() {
        return (ModelExtensionDefinitionEditor)getEditor();
    }

    /**
     * @return the model extension registry (never <code>null</code>)
     */
    protected ModelExtensionRegistry getRegistry() {
        return ExtensionPlugin.getInstance().getRegistry();
    }

    /**
     * @return the resource being edited (never <code>null</code>)
     */
    protected IFile getFile() {
        return getMedEditor().getFile();
    }

    protected Shell getShell() {
        return getSite().getShell();
    }

    /**
     * @param e the property change event being handled (never <code>null</code>)
     */
    protected abstract void handlePropertyChanged( PropertyChangeEvent e );

    /**
     * @return <code>true</code> if the editor is readonly
     */
    protected boolean isReadonly() {
        return getMedEditor().isReadOnly();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.FormPage#setFocus()
     */
    @Override
    public void setFocus() {
        super.setFocus();

        // check for null as this method gets called before controls are constructed
        if (getManagedForm() != null) {
            getManagedForm().refresh();
        }
    }

    /**
     * @param readOnly the new readonly state of the editor
     */
    protected abstract void setResourceReadOnly( boolean readOnly );

    protected abstract void updateAllMessages();

    /**
     * @param errorMessage the message being updated in the {@link IMessageManager message manager} (never <code>null</code>)
     */
    protected void updateMessage( ErrorMessage errorMessage ) {
        assert (errorMessage != null) : "errorMessage is null"; //$NON-NLS-1$
        IMessageManager msgMgr = ((ModelExtensionDefinitionEditor)getEditor()).getMessageManager();

        if (errorMessage.isOk()) {
            msgMgr.removeMessage(errorMessage.getKey(), errorMessage.getControl());
        } else {
            msgMgr.addMessage(errorMessage.getKey(), errorMessage.getMessage(), errorMessage.getData(),
                              errorMessage.getMessageType(), errorMessage.getControl());
        }
    }

}
