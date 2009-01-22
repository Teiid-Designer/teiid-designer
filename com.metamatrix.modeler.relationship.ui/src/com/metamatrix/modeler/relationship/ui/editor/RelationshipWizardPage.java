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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.provider.RelationshipAssociationDescriptor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.relationship.RelationshipEditor;
import com.metamatrix.modeler.relationship.RelationshipPlugin;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * RelationshipWizardPage
 */
public class RelationshipWizardPage extends WizardPage implements IStatusListener {

    private RelationshipAssociationDescriptor radAssociationDescriptor;
    private Command cdCommand;
    private Object parentTarget;

    private Relationship rRelationship;
    private RelationshipEditor reEditor;

    private RelationshipPanel pnlRelationship;
    private IStatus stStatus;
    private boolean requiredStart = false;

    private static final String PAGE_NAME = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipWizardPage.pageName.text"); //$NON-NLS-1$
    private static final String TITLE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipWizardPage.title.text"); //$NON-NLS-1$
    private static final String STATUS_OK_MESSAGE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipWizardPage.statusOkMessage.text"); //$NON-NLS-1$
    private static final String STARTUP_MESSAGE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipWizardPage.startupMessage.text"); //$NON-NLS-1$

    /*
     * Overall strategy for a RelationshipAssociation
     *      1a. if Assoc, do RelAssoc.create() here to create the Relationship
     *      1b. if cmd, do cmd.create, cmd.getResult to get the relationship 
     *      2. if assoc, pass it to the panel
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

    public RelationshipWizardPage( Composite parent,
                                   Object target ) {
        super(PAGE_NAME, TITLE, null);

        setPageComplete(false);
        setDescription(STARTUP_MESSAGE);
        this.parentTarget = target;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewAssociationWizard#setAssociationDescriptor(com.metamatrix.modeler.core.association.AssociationDescriptor)
     */
    public void setAssociationDescriptor( RelationshipAssociationDescriptor radAssociationDescriptor ) {
        this.radAssociationDescriptor = radAssociationDescriptor;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelObjectWizard#setCommand(org.eclipse.emf.common.command.Command)
     */
    public void setCommand( Command cdCommand ) {
        this.cdCommand = cdCommand;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {

        // start the transaction (make it NOT undoable, since we will add our own undoable edit on commit)

        if (radAssociationDescriptor != null) {
            requiredStart = ModelerCore.startTxn(true, true, radAssociationDescriptor.getText(), this); // $
            //            System.out.println("[RelationshipWizardPage.createControl] requiredStart is:  " + requiredStart );  //$NON-NLS-1$        
            pnlRelationship = new RelationshipPanel(parent, createStarterRelationship(), this, radAssociationDescriptor);

        } else {
            requiredStart = ModelerCore.startTxn(true, true, cdCommand.getLabel(), this); // $
            //            System.out.println("[RelationshipWizardPage.createControl] requiredStart is:  " + requiredStart );  //$NON-NLS-1$        
            pnlRelationship = new RelationshipPanel(parent, createStarterRelationship(), this);
        }

        setControl(pnlRelationship);
    }

    private Relationship createStarterRelationship() {

        if (cdCommand != null && cdCommand.canExecute()) {
            try {
                EObject owner = null;
                if (parentTarget != null && parentTarget instanceof EObject) owner = (EObject)parentTarget;
                ModelerCore.getModelEditor().executeCommand(owner, cdCommand);
            } catch (ModelerCoreException e) {
                final String msg = UiConstants.Util.getString("RelationshipWizardPage.Error_creating_Relationship_1"); //$NON-NLS-1$
                final Status status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, -1, msg, e);
                setStatus(status);
                return null;
            }

            rRelationship = (Relationship)cdCommand.getResult().iterator().next();
        } else if (radAssociationDescriptor != null && radAssociationDescriptor.canCreate()) {
            rRelationship = (Relationship)radAssociationDescriptor.create();
        }

        reEditor = RelationshipPlugin.createEditor(rRelationship);

        return reEditor.getRelationship();
    }

    public Relationship getStarterRelationship() {
        return rRelationship;
    }

    @Override
    public Control getControl() {
        return pnlRelationship;
    }

    @Override
    public boolean isPageComplete() {
        return (stStatus != null && isStatusOk() && pnlRelationship.passesLocalValidation());
    }

    private boolean isStatusOk() {
        return (stStatus.isOK() || stStatus.getSeverity() == IStatus.INFO || stStatus.getSeverity() == IStatus.WARNING);
    }

    public void setStatus( IStatus stStatus ) {

        this.stStatus = stStatus;

        if (stStatus.isOK()) {
            setMessage(STATUS_OK_MESSAGE);

        } else {
            if (stStatus instanceof MultiStatus) {
                final StringBuffer sbMessage = new StringBuffer();
                final IStatus[] msgs = ((MultiStatus)stStatus).getChildren();
                int highestErrorLevel = 0;
                for (int i = 0; i < msgs.length; i++) {

                    sbMessage.append(msgs[i].getMessage() + "\n"); //$NON-NLS-1$
                    if (msgs[i].getSeverity() > highestErrorLevel) highestErrorLevel = msgs[i].getSeverity();

                    /*
                     * Convert from IStatus to IMessageProvider value. 
                     */
                    highestErrorLevel = UiUtil.getDialogMessageType(msgs[i]);
                }

                setMessage(sbMessage.toString(), highestErrorLevel);
            } else {
                /*
                 * Convert from IStatus to IMessageProvider value. 
                 */
                int errorLevel = UiUtil.getDialogMessageType(stStatus);
                setMessage(stStatus.getMessage(), errorLevel);
            }
        }

        /*
         * This call will verify there are no messages of type ERROR before enabling the Finish button 
         */
        setPageComplete(true);
    }

    public void endTxn( boolean bWizardFinished ) {

        if (requiredStart) {
            if (bWizardFinished) {
                //                System.out.println("[RelationshipWizardPage.endTxn] bot, about to commit " );  //$NON-NLS-1$        
                ModelerCore.commitTxn();
            } else {
                //                System.out.println("[RelationshipWizardPage.endTxn] bot, about to rollback " );  //$NON-NLS-1$
                ModelerCore.rollbackTxn();
            }
        }
    }

    @Override
    public void dispose() {

        pnlRelationship.dispose();

        super.dispose();
    }
}
