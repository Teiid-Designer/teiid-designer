/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.relationship.RelationshipPlugin;
import com.metamatrix.modeler.relationship.RelationshipTypeEditor;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * RelationshipWizardPage
 */
public class RelationshipTypeWizardPage extends WizardPage
                                     implements IStatusListener {


//    private Composite parent;
    private RelationshipType rtRelationshipTypeObject;
    private RelationshipTypePanel pnlRelationshipType;
    private IStatus stStatus;
    private Command cdCommand;
    private RelationshipTypeEditor reEditor;
    private boolean requiredStart = false;
    private IStructuredSelection selection;

    
    private static final String PAGE_NAME 
        = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypeWizardPage.pageName.text"); //$NON-NLS-1$

    private static final String TITLE 
        = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypeWizardPage.title.text"); //$NON-NLS-1$
        
    private static final String STATUS_OK_MESSAGE
        = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypeWizardPage.statusOkMessage.text"); //$NON-NLS-1$
        
    private static final String STARTUP_MESSAGE
        = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypeWizardPage.startupMessage.text"); //$NON-NLS-1$



    public RelationshipTypeWizardPage( Composite parent, RelationshipType rtRelationshipTypeObject, IStructuredSelection selection ) {
        super( PAGE_NAME,      
               TITLE,          
               null );

        this.rtRelationshipTypeObject = rtRelationshipTypeObject;
        this.selection = selection;
        setPageComplete( false );
        setDescription( STARTUP_MESSAGE );
    }


    @Override
    public Control getControl() {
        return pnlRelationshipType;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelObjectWizard#setCommand(org.eclipse.emf.common.command.Command)
     */
    public void setCommand( Command cdCommand ) {
//        System.out.println("[RelationshipTypeWizardPage.setCommand] cdCommandDescriptor: " + cdCommand.getClass().getName() );
        this.cdCommand = cdCommand;              
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {

        requiredStart = ModelerCore.startTxn( true, true, cdCommand.getLabel(), this ); //$
//        System.out.println("[RelationshipTypeWizardPage.createControl] requiredStart is:  " + requiredStart );  //$NON-NLS-1$        
        
        pnlRelationshipType = new RelationshipTypePanel( parent, createStarterRelationshipType(), this );
        pnlRelationshipType.setBusinessObject( reEditor.getRelationshipType() );
        setControl( pnlRelationshipType );       
    } 
    
    private RelationshipType createStarterRelationshipType() {
        
        if ( cdCommand != null && cdCommand.canExecute() ) {
            
            EObject parent = SelectionUtilities.getSelectedEObject( selection );
            try {
                ModelerCore.getModelEditor().executeCommand( parent, cdCommand );
            } catch (ModelerCoreException e) {
                final String msg = UiConstants.Util.getString("RelationshipTypeWizardPage.Error_creating_Relationship_Type_1"); //$NON-NLS-1$
                final Status status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, -1,  msg, e);
                setStatus(status);
                return null;
            }
            
            rtRelationshipTypeObject = (RelationshipType)cdCommand.getResult().iterator().next();
        } 
        
        reEditor = RelationshipPlugin.createEditor( rtRelationshipTypeObject );
               
        
        return reEditor.getRelationshipType();
    }
    
    public RelationshipTypeEditor getEditor() {
        return reEditor;
    }

    @Override
    public boolean isPageComplete() {
        return( stStatus != null && stStatus.isOK() );
    }
    
    
    public void setStatus( IStatus stStatus ) {
        
        this.stStatus = stStatus;
        
        if ( stStatus.isOK() ) {        

            setPageComplete( stStatus.isOK() );
            setMessage( STATUS_OK_MESSAGE );         
            
        } else {
            if(stStatus instanceof MultiStatus){
                final StringBuffer sbMessage = new StringBuffer();
                final IStatus[] msgs = ((MultiStatus)stStatus).getChildren();
                for (int i = 0; i < msgs.length; i++) {
                     
                    sbMessage.append( msgs[ i ].getMessage() + "\n" );  //$NON-NLS-1$ 
                }
       
                setMessage( sbMessage.toString(), IMessageProvider.ERROR );
            }else{
                setMessage( stStatus.getMessage(), IMessageProvider.ERROR );
            }                   
        }             
    }
    
    public RelationshipType getStarterRelationshipType() {
        return  rtRelationshipTypeObject;
    }

    public void endTxn( boolean bWizardFinished ) {
        
        if( requiredStart ){
            if ( bWizardFinished ) {
//                System.out.println("[RelationshipWizardTypePage.endTxn] bot, about to commit " );  //$NON-NLS-1$        
                ModelerCore.commitTxn( );
            } else {
//                System.out.println("[RelationshipWizardTypePage.endTxn] bot, about to rollback " );  //$NON-NLS-1$
                ModelerCore.rollbackTxn( );
            }
        } 
    }

    
    @Override
    public void dispose() {
        pnlRelationshipType.dispose();
        
        super.dispose();                
    }
}
