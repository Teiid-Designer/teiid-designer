/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.DisabledCommand;
import com.metamatrix.modeler.core.util.NewModelObjectHelperManager;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.wizards.NewModelObjectWizardManager;

/**
 * The <code>NewSiblingAction</code> class creates a new sibling object.
 * @since 4.0
 */
public class NewSiblingAction extends ModelObjectAction {

    //============================================================================================================================
    // Constants
    
    private static final String PROBLEM = "NewSiblingAction.problem"; //$NON-NLS-1$
    private static final String NONE_ALLOWED = "NewSiblingAction.noneAllowed"; //$NON-NLS-1$

    //============================================================================================================================
    // Static Variables
    
    private static final ModelExplorerLabelProvider diagramProvider = new ModelExplorerLabelProvider();
    private static final ModelObjectLabelProvider provider = (ModelObjectLabelProvider) ModelUtilities.getEMFLabelProvider();

    //============================================================================================================================
    // Fields
    
    /** The sibling type descriptor. */
    private Command descriptor;
    private EObject sibling;

    //============================================================================================================================
    // Constructors
    
    /**
     * Constructs a <code>NewSiblingAction</code> where no siblings are allowed. This action is not
     * enabled.
     */
    public NewSiblingAction() {
        super(UiPlugin.getDefault());
        configureNoneAllowedState();
    }
    
    /**
     * Constructs a <code>NewSiblingAction</code> where a new sibling is created.
     * @param theDescriptor the descriptor that determines the sibling type created
     */
    public NewSiblingAction(EObject sibling, Command theDescriptor) {
        super(UiPlugin.getDefault());
        this.sibling = sibling;
        setCommand(theDescriptor);
    }

    //============================================================================================================================
    // Methods
    
    /**
     * Configures the action by setting text, image, and enabled state.
     */
    private void configureAllowedState() {

        Object result = descriptor.getResult().iterator().next();
        
        // If the descriptor represents a disabled command then extract the
        // underlying command to use when getting labels
        Command cmd = descriptor;
        if (descriptor instanceof DisabledCommand) {
            cmd = ((DisabledCommand)descriptor).getDisabledCommand();
        }

        // Use the CreateChildCommand label for the menu text
        String label = cmd.getLabel();
        
        // If the label does not exist of if we are creating a new root entity
        // in a model (i.e. AddCommand) then use the label provider's text since
        // AddCommand labels do not provide meaningful text for a "new" child 
        if ( label == null || label.length() == 0 || cmd instanceof AddCommand) {
            label = provider.getText(result);
        }
        
        // Remove the "New" prefix present in labels from CreateChildCommand instances
        if ( label != null && label.startsWith("New ") ) { //$NON-NLS-1$
            label = label.substring(4);
        }

        setText(label);
        if ( result instanceof Diagram ) {
            setImage(diagramProvider.getImage(result));
        } else if ( result instanceof EObject ) {
            setImage(provider.getImage((EObject) result, ModelObjectUtilities.isVirtual(sibling)));
        } else {
            setImage(provider.getImage(result));
        }
        setToolTipText(cmd.getDescription());
        setEnabled(true);
    }

    /**
     * Configures the action by setting text and disabling it.
     */
    private void configureNoneAllowedState() {
        setText(getPluginUtils().getString(NONE_ALLOWED)); 
        setEnabled(false);
    }


    @Override
    protected void doRun() {
       if (descriptor != null) {

           Shell shell = super.getPlugin().getWorkbench().getActiveWorkbenchWindow().getShell();
           ModelResource modelResource = null;
           boolean continuing = true;
           try {
               modelResource = ModelerCore.getModelEditor().findModelResource( sibling );
           } catch (Exception ex) {
               String msg = getPluginUtils().getString(PROBLEM, new Object[] {descriptor});
               getPluginUtils().log(IStatus.ERROR, ex, msg);
               continuing = false;
           }
           
           // BML 9/16/04  ----------------------------
           // ModelObjectAction caches a selection, however, this instance does not.
           // Need to create a temporary selection to pass into the wizards, either an EObject
           // or a model resource.
           ISelection tempSelection = null;
           if( sibling.eContainer() != null ) {
               tempSelection = new StructuredSelection(sibling.eContainer());
           } else {
               ModelResource mr = ModelUtilities.getModelResourceForModelObject(sibling);
               if( mr != null )
                   tempSelection = new StructuredSelection(mr); 
           }
           // --------------------------------------------
           
           if (continuing) {
               // We need to insure that all containers (especially Transformation & MappingClassSet containers) are loaded/created
               // prior to new child transaction.  This insures undo works.
               ModelUtilities.initializeModelContainers(modelResource, "New Sibling Added", this); //$NON-NLS-1$
               
               if ( NewModelObjectWizardManager.isObjectDescriptorValid( shell, 
                                                                         descriptor,
                                                                         modelResource, 
                                                                         tempSelection ) ) {
                                                                              
//                   System.out.println("[NewSiblingAction.doRun] will run the wizard");//$NON-NLS-1$
                   
                   // Defect 18433 - The XMLDocumentWizard was changed to insure that all work was done one a single thread
                   // and that wizard managed a single transaction to insure UNDO capability 
                   // (i.e. started, committed, canceled/rolledBack)           
                   // if there is a wizard, use it:                                                                              
                   NewModelObjectWizardManager.processObjectDescriptor( shell, 
                                                                        descriptor,
                                                                        modelResource, 
                                                                        tempSelection );
                                                                         
               } else {

                   // if no wizard, create the new objects here                                          
//                   System.out.println("[NewSiblingAction.doRun] will do create txn"); //$NON-NLS-1$
                   boolean started = ModelerCore.startTxn(true, UiConstants.Util.getString("NewSiblingAction.undoLabel", descriptor.getLabel()), this); //$NON-NLS-1$
                   boolean succeeded = false;
                   EObject newObj = null;
                   try {
                       boolean undoable = true;
                       
                       newObj = ModelerCore.getModelEditor().createNewSiblingFromCommand( sibling, descriptor );
                       
                       // Defect 18433 - BML 8/31/05 - Added this manager and INewModelObjectHelper interface to give arbitary
                       // plugins the change to contribute more work following the creation of a new object
                       // In the case of a Virtual Group, we needed to create the transformation for that table so it didn't 
                       // get lazily created as another "Undo" event. (i.e. TransformationNotificationListener, EditAction,, etc.)
                       // If Helper exists, ask for helpCreate(newObj)
                       // The helper also has the opportunity to change/override the "Undo" state of the transaction

                       undoable = NewModelObjectHelperManager.helpCreate(newObj, null);
                       if( !undoable )
                           ModelerCore.getCurrentUoW().setUndoable(false);
                       
                       succeeded = true;
                   } catch (ModelerCoreException theException) {
                       String msg = getPluginUtils().getString(PROBLEM, new Object[] {descriptor});
                       getPluginUtils().log(IStatus.ERROR, theException, msg);
                       setEnabled(false);
                   } finally {
                       if ( started ) {
                           if ( succeeded ) {
                               String newObjName = ModelerCore.getModelEditor().getName(newObj);
                               if( newObjName != null ) {
                                   ModelerCore.getCurrentUoW().setDescription(UiConstants.Util.getString("NewSiblingAction.undoLabel", newObjName)); //$NON-NLS-1$
                               }
                               ModelerCore.commitTxn();
                           } else {
                               ModelerCore.rollbackTxn();
                           }
                       }
                   }
               }                     
           }
                
       }
   }



    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling
     * the run at the last minute.
     * This overrides the AbstractAction preRun() method.
     */
    @Override
    protected boolean preRun() {
        if( requiresEditorForRun() && sibling != null ) {
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(sibling);
            if( mr != null ) {
                ModelEditorManager.activate(mr, true, true);
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
        // does not care about selection since the selected object is passed in
    }
    
    /**
     * Sets the sibling type descriptor used to create the sibling.
     * @param theDescriptor the sibling type descriptor or <code>null</code> if no sibling can be created
     */
    public void setCommand(Command theDescriptor) {
        descriptor = theDescriptor;

        if (descriptor == null || descriptor.getResult().isEmpty() ) {
            configureNoneAllowedState();
        } else {
            configureAllowedState();
            if ( descriptor instanceof DisabledCommand ) {
                super.setEnabled(false);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }
}
