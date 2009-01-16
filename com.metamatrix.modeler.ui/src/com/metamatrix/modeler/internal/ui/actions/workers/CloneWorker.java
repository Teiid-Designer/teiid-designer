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

package com.metamatrix.modeler.internal.ui.actions.workers;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.DiagramHelperManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectEditHelperManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/** 
 * @since 4.2
 */
public class CloneWorker extends ModelObjectWorker {
    
    private static final String PROBLEM = "CloneWorker.problem"; //$NON-NLS-1$
    private static final String UNDO_TEXT = "CloneWorker.undoText"; //$NON-NLS-1$
    private static final String PLURAL_UNDO_TEXT = "CloneWorker.pluralUndoText"; //$NON-NLS-1$
    
    private ModelResource modelResource;
    private boolean editorIsOpening = false;
    private EObject focusedObject;
    private ISelection tempSelection;
    /** 
     * 
     * @since 4.2
     */
    public CloneWorker(boolean enableAfterExecute) {
        super(enableAfterExecute);
    }
    /** 
     * @see com.metamatrix.ui.actions.IActionWorker#getEnableState()
     * @since 4.2
     */
    @Override
    public boolean setEnabledState() {
        boolean enable = false;
        Object selection = getSelection();
        if( selection instanceof ISelection ) {
            ISelection iSelection = (ISelection)selection;
            if( !iSelection.isEmpty() && !isReadOnly() && canLegallyEditResource() ) {
                if (SelectionUtilities.isSingleSelection(iSelection)) {
                    Object o = SelectionUtilities.getSelectedEObject(iSelection);
                    if ( o instanceof Diagram ) {
                        enable = DiagramHelperManager.canClone((Diagram) o);
                    } else if ( o instanceof XmlDocument || o instanceof ModelImport ) {
                        //TODO: remove this elseif when defect 15022 is fixed.
                        enable = false;
                    } else {
                        enable = ( o != null && ModelObjectEditHelperManager.canClone(o)); 
                    }
                    if( enable ) {
                        focusedObject = (EObject)o;
                        modelResource = ModelUtilities.getModelResourceForModelObject((EObject)o);
                        if( modelResource != null ) {
                            EObject parent = focusedObject.eContainer();
                            if( parent != null) {
                                enable = ModelerCore.getModelEditor().isValidParent(parent, (EObject)o);
                            }
                        } else {
                            enable = false;
                        }
                        if( !enable )
                            modelResource = null;
                    }
                } else if (SelectionUtilities.isMultiSelection(iSelection)) {
                    List sourceEObjects = SelectionUtilities.getSelectedEObjects(iSelection);
                    enable = true;
                    for ( Iterator iter = sourceEObjects.iterator() ; iter.hasNext() && enable ; ) {
                        Object o = iter.next();
                        if ( o instanceof Diagram ) {
                            enable = DiagramHelperManager.canClone((Diagram) o);
                        } else if ( o instanceof XmlDocument || o instanceof ModelImport) {
                            //TODO: remove this elseif when defect 15022 is fixed.
                            enable = false;
                        } else {
                            enable =  ModelObjectEditHelperManager.canClone(o);
                        }
                        if( enable && tempSelection == null) {
                            focusedObject = (EObject)o;
                            modelResource = ModelUtilities.getModelResourceForModelObject((EObject)o);
                            if( modelResource != null ) {
                                EObject parent = focusedObject.eContainer();
                                if( parent != null) {
                                    enable = ModelerCore.getModelEditor().isValidParent(parent, (EObject)o);
                                }
                            } else {
                                enable = false;
                            }
                            if( !enable )
                                modelResource = null;
                        }
                    }
                }
            }
        }
        if( !enable ) {
            focusedObject = null;
            modelResource = null;
        }

        return enable;
    }
    

    /**
     * @see com.metamatrix.ui.actions.IActionWorker#execute()
     * @since 4.2
     */
    @Override
    public boolean execute() {
        boolean successful = false;
        Object selection = getSelection();
        if( selection instanceof ISelection && canLegallyEditResource() ) {
            ISelection iSelection = (ISelection)selection;
    
            String objectString = null;
            List lstObjs = SelectionUtilities.getSelectedEObjects(iSelection);
            String description = null;
            if ( lstObjs.size() == 1 ) {
                EObject obj = (EObject) lstObjs.get(0);            
                objectString = ModelerCore.getModelEditor().getModelRelativePath(obj).toString();
                description = UiConstants.Util.getString(UNDO_TEXT, objectString);
            } else {
                objectString = "" + lstObjs.size(); //$NON-NLS-1$
                description = UiConstants.Util.getString(PLURAL_UNDO_TEXT,objectString);
            }
            
            boolean started = ModelerCore.startTxn(description, this);
            boolean succeeded = false;
            try {
                for ( Iterator iter= lstObjs.iterator() ; iter.hasNext() ; ) {
                    EObject obj = (EObject) iter.next();
                    try {
                        ModelerCore.getModelEditor().clone(obj); 
                    } catch (ModelerCoreException theException) {
                        Object path = ModelerCore.getModelEditor().getModelRelativePathIncludingModel(obj);
                        String msg = UiConstants.Util.getString(PROBLEM,  path);
                        UiConstants.Util.log(IStatus.ERROR, theException, msg);
                    }
                }
                successful = true;
                succeeded = true;
            } finally {
                if ( started ) {
                    if ( succeeded ) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
    
            tempSelection = null;
            editorIsOpening = false;
            focusedObject = null;
            modelResource = null;

        }
        return successful;

    }
    // -------------------------------------------------------------------------
    // Getter & Setter methods specific to this CutWorker class
    // -------------------------------------------------------------------------
    
    public void setModelResource(ModelResource modelResource) {
        this.modelResource = modelResource;
    }
    
    public ISelection getTempSelection() {
        return this.tempSelection;
    }
    
    public void setTempSelection(ISelection tempSelection) {
        this.tempSelection = tempSelection;
    }
    
    public void setEditorIsOpening(boolean editorIsOpening) {
        this.editorIsOpening = editorIsOpening;
    }
    
    public boolean getEditorIsOpening() {
        return this.editorIsOpening;
    }
    
    public EObject getFocusedObject() {
        return this.focusedObject;
    }
    public ModelResource getModelResource() {
        return this.modelResource;
    }
}
