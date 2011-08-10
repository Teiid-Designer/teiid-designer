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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.ui.wizards.INewModelObjectWizard;
import com.metamatrix.modeler.ui.wizards.INewObjectWizard;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**<p>
 * </p>
 * @since 4.0
 */
public final class NewRelationshipTypeWizard extends AbstractWizard 
                                          implements INewModelObjectWizard,
                                                     INewObjectWizard {
    //============================================================================================================================
    // Constants    
    private static final String PAGE_NAME 
        = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypeWizardPage.pageName.text"); //$NON-NLS-1$


    //============================================================================================================================
    // Static Methods

    /**<p>
     * </p>
     * @since 4.0
     */

    
    //============================================================================================================================
    // Variables
    private boolean bWizardFinished = false;
//    private boolean bWizardCancelled = false;
    private Command cdCommand;
    private IStructuredSelection selection;

   
    private RelationshipTypeWizardPage rtwpRelationshipTypePage;

    //============================================================================================================================
    // Constructors
    
    /**<p>
     * </p>
     * @since 4.0
     */
    public NewRelationshipTypeWizard() {
        super( UiPlugin.getDefault(), PAGE_NAME, null );   
    }
    
    //============================================================================================================================
    // Implemented Methods


    public void init( final IWorkbench workbench ) {
//        System.out.println("[NewRelationshipWizard.init 2] selection: " + selection );
        // Create and add pages (selection may be null or not; the constructor will handle it)

        
        rtwpRelationshipTypePage 
            = new RelationshipTypeWizardPage( workbench.getWorkbenchWindows()[0].getShell(), 
                                              null, 
                                              selection );

        if ( cdCommand != null ) {                    
            rtwpRelationshipTypePage.setCommand( cdCommand );
        } 
        
        addPage( rtwpRelationshipTypePage );
    }
    
    /**
     * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageControls(Composite thePageContainer) {
        // don't allow restoring of size. there is a bug that makes the wizard size
        // increase each time it is executed. remove this method when the bug gets fixed.
        createPageControls(thePageContainer, false);
    }

    
    @Override
    public void dispose() {
        rtwpRelationshipTypePage.dispose();
        cdCommand.dispose();
    }
     
    @Override
    public boolean finish() {
        bWizardFinished = true;

        return true;  
    }
    
    public boolean cancel() {

        bWizardFinished = false;
       
        return true;                
    }


    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelObjectWizard#setCommand(org.eclipse.emf.common.command.Command)
     */
    public void setCommand( Command cdCommand ) {
        this.cdCommand = cdCommand;
    }

   public void setModel( ModelResource model ) {
    
    }

    public boolean completedOperation() {

        rtwpRelationshipTypePage.endTxn( bWizardFinished );              
        
        // return the result of the wizard:  true if 'finished'; false if 'cancelled'
        return bWizardFinished;            
    }
    
    public void init( IWorkbench wb, IStructuredSelection selection ) {
        this.selection = selection;
        init( wb );
     }



}
