/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.ui.dialogs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.teiid.core.util.I18nUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.xml.ui.ModelerXmlUiConstants;



/**
 * This dialog asks the user if they want to save the XSD model. If the user wants to save
 * a save is done. Do not use this dialog is the XSD model is not dirty.
 * @since 5.0.2
 */
public class ConfirmSaveXsdModelDialog extends MessageDialog
                                       implements ModelerXmlUiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private IProgressMonitor monitor;
    
    private boolean schemaSaved;
    
    private ModelResource schemaModel;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public ConfirmSaveXsdModelDialog(Shell theShell,
                                     ModelResource theSchemaModel) {
        super(theShell,
              Util.getString(I18nUtil.getPropertyPrefix(ConfirmSaveXsdModelDialog.class) + "title"), //$NON-NLS-1$
              null,
              Util.getString(I18nUtil.getPropertyPrefix(ConfirmSaveXsdModelDialog.class) + "msg"), //$NON-NLS-1$
              QUESTION,
              new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL},
              0);
        this.schemaModel = theSchemaModel;
    }
    
    public ConfirmSaveXsdModelDialog(Shell theShell,
                                     ModelResource theSchemaModel,
                                     IProgressMonitor theMonitor) {
        this(theShell, theSchemaModel);
        this.monitor = theMonitor;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** 
     * @see org.eclipse.jface.dialogs.Dialog#close()
     * @since 5.0.2
     */
    @Override
    public boolean close() {
        if (getReturnCode() == OK) {
            try {
                if (this.schemaModel.getEmfResource().isModified()) {
                    IFile file = (IFile)this.schemaModel.getUnderlyingResource();
                    IEditorReference editorRef = ModelEditorManager.getEditorReferenceForFile(file);
                    
                    if (editorRef != null) {
                        editorRef.getEditor(false).doSave(this.monitor);
                    } else {
                        // shouldn't get here if there is no editor open but just in case save the model
                        this.schemaModel.save(this.monitor, false);
                    }
                }
                
                this.schemaSaved = true;
            } catch (ModelWorkspaceException theException) {
                this.schemaSaved = false;
                Util.log(theException);
            }
        } else {
            this.schemaSaved = false;
        }
        
        return super.close();
    }

    /**
     * Indicates if the schema associated with this dialog has been saved.
     * @return <code>true</code> if saved; <code>false</code> otherwise.
     */
    public boolean isSchemaSaved() {
        return this.schemaSaved;
    }
    
}
