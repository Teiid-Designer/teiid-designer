/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import java.beans.PropertyChangeListener;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistantAdapter;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * 
 */
public abstract class MedEditorPage extends FormPage implements PropertyChangeListener {

    private final ModelExtensionDefinition med;
    private ModelExtensionDefinition medBeingEdited;

    protected MedEditorPage( FormEditor medEditor,
                             String id,
                             String title,
                             ModelExtensionDefinition medBeingEdited ) {
        super(medEditor, id, title);

        CoreArgCheck.isNotNull(medBeingEdited, "medBeingEdited is null"); //$NON-NLS-1$
        this.medBeingEdited = medBeingEdited;

        // copy over data to MED that will be changed by editor
        this.med = new ModelExtensionDefinition(new ModelExtensionAssistantAdapter());
        this.med.setDescription(this.medBeingEdited.getDescription());
        this.med.setMetamodelUri(this.medBeingEdited.getMetamodelUri());
        this.med.setNamespacePrefix(this.medBeingEdited.getNamespacePrefix());
        this.med.setNamespaceUri(this.medBeingEdited.getNamespaceUri());
        this.med.setVersion(this.medBeingEdited.getVersion());

        // properties
        for (String metaclassName : medBeingEdited.getExtendedMetaclasses()) {
            this.med.addMetaclass(metaclassName);

            for (ModelExtensionPropertyDefinition propDefn : medBeingEdited.getPropertyDefinitions(metaclassName)) {
                this.med.addPropertyDefinition(metaclassName, propDefn);
            }
        }
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

    /**
     * @return the MED being modified by the GUI (never <code>null</code>)
     */
    protected ModelExtensionDefinition getMed() {
        return this.med;
    }

    protected Shell getShell() {
        return getSite().getShell();
    }

    protected boolean isChanged() {
        return !this.med.equals(this.medBeingEdited);
    }

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
