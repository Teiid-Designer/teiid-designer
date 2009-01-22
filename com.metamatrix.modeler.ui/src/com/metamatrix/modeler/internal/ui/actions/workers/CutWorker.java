/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions.workers;


import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.DiagramHelperManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectEditHelperManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.TransactionSettings;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;



/** 
 * @since 4.2
 */
public class CutWorker extends ModelObjectWorker {
    //============================================================================================================================
    // Constants
    
    private static final String PROBLEM = "CutWorker.problem"; //$NON-NLS-1$
    private static final String UNDO_TEXT = "CutAction.undoText"; //$NON-NLS-1$
    private static final String PLURAL_UNDO_TEXT = "CutAction.pluralUndoText"; //$NON-NLS-1$
    
    //============================================================================================================================
    // Fields
    
    /** The child type descriptor. */
    private ModelResource modelResource;
    private boolean editorIsOpening = false;
    private EObject focusedObject;
    private ISelection tempSelection;
    
    /** 
     * 
     * @since 4.2
     */
    public CutWorker(boolean enableAfterExecute) {
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
                        enable = DiagramHelperManager.canCut((Diagram) o);
                    } else {
                        enable = ( o != null && ModelObjectEditHelperManager.canCut(o) ); 
                    }
                    if( enable ) {
                        focusedObject = (EObject)o;
                        modelResource = ModelUtilities.getModelResourceForModelObject((EObject)o);
                    }
                } else if (SelectionUtilities.isMultiSelection(iSelection)) {
                    List sourceEObjects = SelectionUtilities.getSelectedEObjects(iSelection);
                    enable = !sourceEObjects.isEmpty();
                    for ( Iterator iter = sourceEObjects.iterator() ; iter.hasNext() && enable ; ) {
                        Object o = iter.next();
                        if ( o instanceof Diagram ) {
                            enable = DiagramHelperManager.canCut((Diagram) o);
                        } else {
                            enable = ModelObjectEditHelperManager.canCut(o);
                        }
                        if( enable && tempSelection == null) {
                            focusedObject = (EObject)o;
                            modelResource = ModelUtilities.getModelResourceForModelObject((EObject)o);
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
            
            // create the transaction            
            TransactionSettings ts = getTransactionSettings();
            ts.setSource( this );
            boolean started = ModelerCore.startTxn( ts.isSignificant(),
                                                     ts.isUndoable(),
                                                     ts.getDescription(), 
                                                     ts.getSource() );
            
            boolean succeeded = false;
            try {
                ModelerCore.getModelEditor().cutAllToClipboard(lstObjs); 
                succeeded = true;
            } catch (ModelerCoreException theException) {
                String msg = UiConstants.Util.getString(PROBLEM,  objectString); 
                UiConstants.Util.log(IStatus.ERROR, theException, msg);
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
            this.setTransactionSettings( null );
        }
        return successful;
        
    }
    
    
    public TransactionSettings initTransactionSettings() {
        
        TransactionSettings ts = getTransactionSettings();
        
        ts = processCanUndoCut( ts );
        ts = processDescription( ts );
        
        return ts;            
    }
    
    private TransactionSettings processDescription( TransactionSettings ts ) {
        
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
            
            ts.setDescription( description );            
        }
        
        return ts;            
    }
    
    private TransactionSettings processCanUndoCut( TransactionSettings ts ) {
        
        boolean bCanUndoCut = false;
        Object selection = getSelection();
        
        if( selection instanceof ISelection ) {
            ISelection iSelection = (ISelection)selection;
            if( !iSelection.isEmpty() && !isReadOnly() && canLegallyEditResource() ) {
                if (SelectionUtilities.isSingleSelection(iSelection)) {
                    Object o = SelectionUtilities.getSelectedEObject(iSelection);

                    bCanUndoCut = ( o != null && ModelObjectEditHelperManager.canUndoCut(o) ); 

                } else if (SelectionUtilities.isMultiSelection(iSelection)) {
                    List sourceEObjects = SelectionUtilities.getSelectedEObjects(iSelection);
                    bCanUndoCut = true;
                    
                    if ( sourceEObjects.size() > 0 ) {
                        bCanUndoCut = ModelObjectEditHelperManager.canUndoCut( sourceEObjects );
                    }                    
                }
                                
                ts.setIsUndoable( bCanUndoCut );
            }
        }
        
        return ts;            
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
