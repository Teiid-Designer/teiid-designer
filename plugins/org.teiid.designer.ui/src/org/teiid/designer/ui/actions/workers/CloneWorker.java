/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions.workers;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.metamodels.core.ModelImport;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.DiagramHelperManager;
import org.teiid.designer.ui.viewsupport.ModelObjectEditHelperManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;



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
     * @see org.teiid.designer.ui.common.actions.IActionWorker#getEnableState()
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
     * @see org.teiid.designer.ui.common.actions.IActionWorker#execute()
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
