/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.actions;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.mapping.ui.UiConstants;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.MappingClassColumn;
import org.teiid.designer.metamodels.transformation.StagingTable;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.actions.TransactionSettings;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelObjectEditHelperManager;


/**
 * DeleteAction
 *
 * @since 8.0
 */
public class DeleteAction extends MappingAction implements UiConstants {
    //============================================================================================================================
    // Constants
    private static final String PROBLEM = "org.teiid.designer.transformation.ui.actions.DeleteAction.problem"; //$NON-NLS-1$
    private static final String ACTION_DESCRIPTION = "Delete From Mapping"; //$NON-NLS-1$
    private static final String CANNOT_UNDO_TITLE = "DeleteAction.cannotUndoTitle"; //$NON-NLS-1$
    private static final String CANNOT_UNDO_MSG = "DeleteAction.cannotUndoMsg"; //$NON-NLS-1$

    //============================================================================================================================
    // Constructors

    /**
     * Construct an instance of DeleteAction.
     * 
     */
    public DeleteAction(EObject transformationEObject) {
        super(transformationEObject);
        final ISharedImages imgs = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        setDisabledImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
    }

    //============================================================================================================================
    // ISelectionListener Methods

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    @Override
    public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
        // sample code:
        super.selectionChanged(part, selection);
        
        determineEnablement();
    }

    //============================================================================================================================
    // Action Methods

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {
        //        System.out.println("[transformation.ui.actions.DeleteAction.doRun] TOP"); //$NON-NLS-1$
        
        List selectedEObjects = SelectionUtilities.getSelectedEObjects(getSelection());
        
        if (selectedEObjects != null) {

            // first determine if this action is undoable
            TransactionSettings ts = determineCanUndoStatus();

            if ( !ts.isUndoable() ) {
                String sTitle = UiConstants.Util.getString( CANNOT_UNDO_TITLE );
                String sMsg = UiConstants.Util.getString( CANNOT_UNDO_MSG );
                
                
                // if not undoable, warn the user
                boolean bDoAnyway 
                    = MessageDialog.openQuestion( getShell(), sTitle, sMsg );
                
                // if they do not wish to continue, bail out now
                if ( !bDoAnyway ) {
                    return;
                }
            }
                    
            
            //start txn (using the 'isUndoable' info from TransactionSettings)
            boolean  requiredStart = ModelerCore.startTxn(true, ts.isUndoable(), ACTION_DESCRIPTION, this);
            
            boolean succeeded = false;
            try {
                delete(selectedEObjects);
                succeeded = true;
            } catch (ModelerCoreException theException) {
                final String msg = Util.getString(PROBLEM); 
                getPluginUtils().log(IStatus.ERROR, theException, msg);
                setEnabled(false);
            } finally {
                if (requiredStart) {
                    if ( succeeded ) {
                        ModelerCore.commitTxn( );
                    } else {
                        ModelerCore.rollbackTxn( );
                    }
                }
            }
        }
    }
    
    private TransactionSettings determineCanUndoStatus() {
        
        TransactionSettings ts = new TransactionSettings();            
        boolean bCanUndoDelete = false;
        Object selection = getSelection();
        
        if( selection instanceof ISelection ) {
            ISelection iSelection = (ISelection)selection;
            if( !iSelection.isEmpty() && !isReadOnly() && canLegallyEditResource() ) {
                if (SelectionUtilities.isSingleSelection(iSelection)) {
                    Object o = SelectionUtilities.getSelectedEObject(iSelection);

                    bCanUndoDelete = ( o != null && ModelObjectEditHelperManager.canUndoDelete(o) ); 

                } else if (SelectionUtilities.isMultiSelection(iSelection)) {
                    List sourceEObjects = SelectionUtilities.getSelectedEObjects(iSelection);
                    bCanUndoDelete = true;
                    
                    if ( sourceEObjects.size() > 0 ) {
                        bCanUndoDelete = ModelObjectEditHelperManager.canUndoDelete( sourceEObjects );
                    }                    
                }                                
            }
        }
        ts.setIsUndoable( bCanUndoDelete );
        
        return ts;            
    }
    
    private void delete(List deleteList) throws ModelerCoreException {
        // Walk through all objects.  Treat Staging tables and mapping classes differently
        EObject eObj = null;
        boolean hasMCF = (getMappingClassFactory() != null);
        /*
         * jh Defect 21277: This action was always creates a fresh MappingClassFactory, which itself
         *         creates a fresh TreeMappingAdapter.  We should be using the centralized, common
         *         TreeMappingAdapter so that the delete done by this action will change the 
         *         internal state of the common TMA, which is then use by the XmlDocumentModelObjectLabelProvider,
         *         as well as other classes.
         *         I have modified this super class' (MappingAction) getMappingClassFactory() to
         *         always get the current mcf.
         *         Removed code from this method that used to create a new MappingClassFactory.
         */
        
        Iterator iter = deleteList.iterator();
        while( iter.hasNext() ) {
            eObj = (EObject)iter.next();

            if ( isStagingTable(eObj) ) {
                if( hasMCF )
                    getMappingClassFactory().deleteStagingTable((StagingTable)eObj);
            } else if( isMappingClass(eObj) ) {
                if( hasMCF ) {
                    getMappingClassFactory().deleteMappingClass((MappingClass)eObj);
                }
            } else if( isMappingClassColumn(eObj) ) {
                if( hasMCF )
                    getMappingClassFactory().deleteMappingClassColumn((MappingClassColumn)eObj);
            } else {
                ModelerCore.getModelEditor().delete(eObj);
            }
        }
    }

    private void determineEnablement() {
        boolean enable = false;
        if( !isReadOnly() ) {
            enable = true;
            
            // if Coarse Mode, the we need to ask one set of questions:
            // Can Delete MappingClasses, StagingTables, 
            List selectedEObjects = SelectionUtilities.getSelectedEObjects(getSelection());
            EObject eObj = null;
            
            if( isDetailed() ) {
                enable = MappingGlobalActionsManager.canDelete(selectedEObjects);
            } else {
                Iterator iter = selectedEObjects.iterator();
                while( iter.hasNext() && enable == true ) {
                    eObj = (EObject)iter.next();
        
                    if( !isMappingClassColumn(eObj) &&
                        !isMappingClass(eObj) && 
                        !isStagingTable(eObj) ) {
                            enable = false;
                    }
                }
            }
        }

        setEnabled(enable);
    }
    
    
    protected Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }
    
}
