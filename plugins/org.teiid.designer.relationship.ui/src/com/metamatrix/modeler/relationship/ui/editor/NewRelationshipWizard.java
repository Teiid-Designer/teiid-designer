/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.editor;

import org.eclipse.emf.common.command.Command;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import com.metamatrix.metamodels.relationship.provider.RelationshipAssociationDescriptor;
import com.metamatrix.modeler.core.association.AssociationDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.ui.wizards.INewAssociationWizard;
import com.metamatrix.modeler.ui.wizards.INewModelObjectWizard;
import com.metamatrix.ui.internal.wizard.AbstractWizard;



/**<p>
 * </p>
 * @since 4.0
 */
public final class NewRelationshipWizard extends AbstractWizard 
                                      implements INewModelObjectWizard, INewAssociationWizard {
    //============================================================================================================================
    // Constants
    private static final String PAGE_NAME 
        = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipWizardPage.pageName.text"); //$NON-NLS-1$
    

    //============================================================================================================================
    // Static Methods

    
    //============================================================================================================================
    // Variables   
    private RelationshipWizardPage rwpRelationshipPage;
    private RelationshipAssociationDescriptor radAssociationDescriptor;
    private Command cdCommand;
    private Object parentTarget = null;
    
    private boolean bWizardFinished = false;

    //============================================================================================================================
    // Constructors
    
    /**<p>
     * </p>
     * @since 4.0
     */
    public NewRelationshipWizard() {
        super( UiPlugin.getDefault(), PAGE_NAME, null ); 
    
    }
    
    //============================================================================================================================
    // Implemented Methods

    @Override
    public boolean finish() {

        bWizardFinished = true;
        if ( radAssociationDescriptor != null ) {
            radAssociationDescriptor.setCreationComplete(true);
        } // endif
        return true;
    }
    
    public boolean cancel() {
        bWizardFinished = false;

        return true;                
    }

    public void setAssociationDescriptor( AssociationDescriptor adAssociationDescriptor ) {
//        System.out.println("[NewRelationshipWizard.setAssociationDescriptor] adAssociationDescriptor: " + adAssociationDescriptor.getClass().getName() );
        if ( adAssociationDescriptor instanceof RelationshipAssociationDescriptor ) {        
            this.radAssociationDescriptor = (RelationshipAssociationDescriptor)adAssociationDescriptor;
        }
    }

    public void setCommand( Command cdCommand ) {
//        System.out.println("[NewRelationshipWizard.setCommand] cdCommand: " + cdCommand.getClass().getName() );
        this.cdCommand = cdCommand;
    }


    public void init( final IWorkbench workbench ) {
        // Create and add pages (selection may be null or not; the constructor will handle it)
        
        /*
         * Overall strategy for a RelationshipAssociation
         *      1. do RelAssoc.create() here to create the Relationship
         *      2. pass it to the panel
         *      3. the panel will see it has a RelAssoc and put up the location control
         *          (label + textfield + browse button)
         *      4. The browse dialog will allow instanceof RelatonshipContainer
         *           and RelationshipModel
         *      5. the panel will have a getLocation method so that the wizpage
         *         can retrieve the location to apply it on finish
         *      6. To apply the location to the Relationship
         *          a) If loc is a RelationshipContainer...
         *                  use Relationship.setRelationshipContainer( loc )
         *          b) If loc is a RelationshipModel...
         *                  use model.getModelResource().getAllRootEObjects().add( loc )
         *     
         *              
         */

        rwpRelationshipPage 
            = new RelationshipWizardPage( workbench.getWorkbenchWindows()[0].getShell(), parentTarget);

        if ( cdCommand != null ) {                    
            rwpRelationshipPage.setCommand( cdCommand );
//            requiredStart = ModelerCore.startTxn( true, true, cdCommand.getLabel(), this ); //$NON-NLS-1$$
        } 
        else
        if ( radAssociationDescriptor != null ) {
//            System.out.println("[NewRelationshipWizard.init 2] about to call setAssociation "  );
            rwpRelationshipPage.setAssociationDescriptor( radAssociationDescriptor );            
//            requiredStart = ModelerCore.startTxn( true, true, radAssociationDescriptor.getText(), this ); //$NON-NLS-1$$
        }                            
        
        addPage(rwpRelationshipPage);
    }
    
    @Override
    public void dispose() {
        if ( rwpRelationshipPage != null ) {        
            rwpRelationshipPage.dispose();
        }
 
        if ( cdCommand != null ) {        
            cdCommand.dispose();
        }
    }
     
    
    public void setModel( ModelResource model ) {
    
    }

    public boolean completedOperation() {
        
        rwpRelationshipPage.endTxn( bWizardFinished );              
        
        // return the result of the wizard:  true if 'finished'; false if 'cancelled' 
        return bWizardFinished;            
    }
    
    public void init( IWorkbench wb, IStructuredSelection selection ) {
       if (selection != null) {
           this.parentTarget = selection.getFirstElement();
       }
       
       init( wb );
    }



    /** 
     * @return Returns the parentTarget.
     * @since 4.2
     */
    public Object getParentTarget() {
        return this.parentTarget;
    }
}
