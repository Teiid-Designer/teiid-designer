/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.editor;

import org.eclipse.emf.common.command.Command;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.relationship.ui.UiConstants;
import org.teiid.designer.relationship.ui.UiPlugin;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.wizards.INewModelObjectWizard;

/**<p>
 * </p>
 * @since 4.0
 */
public final class NewRelationshipTypeWizard extends AbstractWizard 
                                          implements INewModelObjectWizard {
    //============================================================================================================================
    // Constants    
    private static final String PAGE_NAME 
        = UiConstants.Util.getString("org.teiid.designer.relationship.ui.editor.RelationshipTypeWizardPage.pageName.text"); //$NON-NLS-1$


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
     * @See org.teiid.designer.ui.wizards.INewModelObjectWizard#setCommand(org.eclipse.emf.common.command.Command)
     */
    @Override
	public void setCommand( Command cdCommand ) {
        this.cdCommand = cdCommand;
    }

   @Override
public void setModel( ModelResource model ) {
    
    }

    @Override
	public boolean completedOperation() {

        rtwpRelationshipTypePage.endTxn( bWizardFinished );              
        
        // return the result of the wizard:  true if 'finished'; false if 'cancelled'
        return bWizardFinished;            
    }
    
    @Override
	public void init( IWorkbench wb, IStructuredSelection selection ) {
        this.selection = selection;
        init( wb );
     }



}
