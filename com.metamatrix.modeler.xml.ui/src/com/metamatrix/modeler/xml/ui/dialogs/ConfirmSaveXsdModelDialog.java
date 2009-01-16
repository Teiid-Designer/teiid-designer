/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.xml.ui.dialogs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;


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
