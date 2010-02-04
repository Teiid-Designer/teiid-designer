/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.wizards;

import java.util.HashMap;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.modeler.core.association.AssociationDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * NewModelObjectWizardManager is a static class that manages the existance and
 * the running of New Model Object Wizard and New Association Wizard extensions.
 */
public abstract class NewModelObjectWizardManager implements UiConstants.ExtensionPoints {

    /** HashMap of key=new object id, value = wizard IExtension */
    private static HashMap<String, IConfigurationElement> objectWizardMap;

    /** HashMap of key=new object id, value = wizard IExtension */
    private static HashMap<String, IConfigurationElement> associationWizardMap;

   /**
    * [revised, 4/16/2004]
    * This method will only determine if there is a wizard, and report back to its caller
    * (for example, NewChildAction).  A second, new method will be responsible to
    * execute the wizard, and the caller should not care what the outcome of that is 
    * (user may cancel, for example).
    * 
    * Determine if the specified model object descriptor is valid by asking any available
    * NewModelObjectWizard extension to populate it.  If a wizard is available and successfully
    * completes the population of the descriptor, then this method returns true.  UNLESS the wizard
    * also performs the operation, in which case this method must return false.  If the wizard
    * is cancelled, this method returns false.  If no wizard is registered to operate on the
    * type of object in the descriptor, then this method returns true by default.  The wizard
    * will not execute the descriptor if this method returns true.
    * @param parent the Shell for running the wizard.
    * @param descriptor the CommandParameter descriptor for the desired new model object.
    * @param modelResource  the ModelResource for the model being created
    * @return true if there is no wizard or if the wizard completes successfully, false if
    * the user cancels the wizard.
    */
   public static boolean isObjectDescriptorValid(
       Shell parent,
       Command descriptor,
       ModelResource modelResource,
       ISelection currentSelection) {
       boolean result = true;

       if (objectWizardMap == null) {
           objectWizardMap =
               loadWizardExtensions(
                   NewModelObjectWizard.ID,
                   NewModelObjectWizard.CLASS_ELEMENT,
                   NewModelObjectWizard.DESCRIPTOR_ELEMENT,
                   NewModelObjectWizard.DESCRIPTOR_ID);
       }

       EObject target = (EObject)descriptor.getResult().iterator().next();
       if (target != null) {
           String key = target.eClass().getInstanceClassName();

           IConfigurationElement wizardElement = objectWizardMap.get(key);
           if (wizardElement == null) {
               // no wizard found  
               result = false;
           } else {
               result = true;
           }
       }

       return result;
   }

    /**
     * Process the specified object in the appropriate wizard.  A previous call to 
     * isObjectDescriptorValid() should have been done to ensure that a wizard exists
     * for this purpose.
     * 
     *
     * @param parent the Shell for running the wizard.
     * @param descriptor the CommandParameter descriptor for the desired new model object.
     * @param modelResource  the ModelResource for the model being created
     * @return true if the wizard completes successfully, false if
     * the user cancels the wizard.
     */
    public static boolean processObjectDescriptor(
        Shell parent,
        Command descriptor,
        ModelResource modelResource,
        ISelection currentSelection) {
        boolean result = true;

        if (objectWizardMap == null) {
            objectWizardMap =
                loadWizardExtensions(
                    NewModelObjectWizard.ID,
                    NewModelObjectWizard.CLASS_ELEMENT,
                    NewModelObjectWizard.DESCRIPTOR_ELEMENT,
                    NewModelObjectWizard.DESCRIPTOR_ID);
        }

        EObject target = (EObject)descriptor.getResult().iterator().next();
        if (target != null) {
            String key = target.eClass().getInstanceClassName();

            IConfigurationElement wizardElement = objectWizardMap.get(key);
            if (wizardElement == null) {
                // no wizard found - go ahead and create the object from the descriptor
                result = true;
            } else {
                result =
                    runWizard(
                        parent,
                        wizardElement,
                        descriptor,
                        modelResource,
                        currentSelection,
                        NewModelObjectWizard.CLASSNAME);
            }
        }
        return result;
    }

    /**
     * Determine if the specified association descriptor is valid by asking any available
     * NewAssociationWizard extension to populate it.  If a wizard is available and successfully
     * completes the population of the descriptor, then this method returns true.  UNLESS the wizard
     * also performs the operation, in which case the wizard must return false.  If the wizard
     * is cancelled, this method returns false.  If no wizard is registered to operate on the
     * type of association in the descriptor, then this method returns true by default.
     * The wizard will not execute the descriptor if this method returns true.
     * @param parent the Shell for running the wizard.
     * @param descriptor the CommandParameter descriptor for the desired new association.
     * @param modelResource ModelResource for the model
     * @return true if there is no wizard or if the wizard completes successfully, false if
     * the user cancels the wizard.
     */
    public static boolean isAssociationDescriptorValid(
        Shell parent,
        AssociationDescriptor descriptor,
        ModelResource modelResource,
        ISelection currentSelection) {
        boolean result = true;

        if (associationWizardMap == null) {
            associationWizardMap =
                loadWizardExtensions(
                    NewAssociationWizard.ID,
                    NewAssociationWizard.CLASS_ELEMENT,
                    NewAssociationWizard.DESCRIPTOR_ELEMENT,
                    NewAssociationWizard.DESCRIPTOR_ID);
        }

        String key = descriptor.getType();

        IConfigurationElement wizardElement = associationWizardMap.get(key);
        if (wizardElement == null) {
            // no wizard found - go ahead and create the association from the descriptor
            result = true;
        } else {
            result =
                runWizard(
                    parent,
                    wizardElement,
                    descriptor,
                    modelResource,
                    currentSelection,
                    NewAssociationWizard.CLASSNAME);
        }

        return result;
    }

    public static boolean isAssociationDescriptorValid(
        Shell parent,
        AssociationDescriptor descriptor,
        ISelection currentSelection) {
        return isAssociationDescriptorValid(parent, descriptor, null, currentSelection);
    }

    private static boolean runWizard(
        Shell parent,
        IConfigurationElement wizardConfig,
        Object descriptor,
        ModelResource modelResource,
        ISelection selection,
        String classElement) {
        boolean result = true;

        try {
            Object extension = wizardConfig.createExecutableExtension(classElement);
            if (extension instanceof INewModelObjectWizard) {
                INewObjectWizard wiz = (INewObjectWizard) extension;
                initializeWizard(wiz, selection, modelResource, descriptor);

                WizardDialog dialog = new WizardDialog(parent, wiz) {
                    @Override
                    public void create() {
                        setShellStyle(getShellStyle() | SWT.RESIZE);
                        super.create();
                    }
                };
                dialog.open();

                result = (wiz.completedOperation());
            }
        } catch (CoreException e) {
            // catch any Exception that occurred obtaining the configuration and log it
                String message = UiConstants.Util.getString("NewModelObjectWizardManager.runWizardErrorMessage", //$NON-NLS-1$
                        wizardConfig.getAttribute(NewModelObjectWizard.CLASSNAME));
            UiConstants.Util.log(IStatus.ERROR, e, message);
            String title = UiConstants.Util.getString("NewModelObjectWizardManager.runWizardErrorTitle"); //$NON-NLS-1$
            MessageDialog.openError(parent, title, message);
        }

        return result;
    }

    private static void initializeWizard(INewObjectWizard wizard, ISelection selection, ModelResource modelResource, Object descriptor) {
        wizard.setModel(modelResource);
        if ( descriptor instanceof AssociationDescriptor ) {
            ((INewAssociationWizard) wizard).setAssociationDescriptor((AssociationDescriptor) descriptor);
        } else if ( descriptor instanceof Command ) { 
            ((INewModelObjectWizard) wizard).setCommand((Command) descriptor);
        }
        if (selection instanceof IStructuredSelection) {
            wizard.init(
                UiPlugin.getDefault().getCurrentWorkbenchWindow().getWorkbench(),
                (IStructuredSelection)selection);
        } else {
            wizard.init(UiPlugin.getDefault().getCurrentWorkbenchWindow().getWorkbench(), null);
        }
    }

    private static HashMap<String, IConfigurationElement> loadWizardExtensions(
        String extensionPointId,
        String classElement,
        String descriptor,
        String desctiptorId) {

        HashMap<String, IConfigurationElement> result = new HashMap<String, IConfigurationElement>();

        // get the NewModelObjectWizard extension point from the plugin class
        IExtensionPoint extensionPoint =
            Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, extensionPointId);
        // get the all extensions to the NewModelObjectWizard extension point
        IExtension[] extensions = extensionPoint.getExtensions();

        // build a map of all types that these wizard extensions can handle
        for (int i = 0; i < extensions.length; ++i) {

            IConfigurationElement[] elements = extensions[i].getConfigurationElements();

            try {

                // first, find the wizard class name for the map
                IConfigurationElement wizardClassElement = null;
                for (int j = 0; j < elements.length; ++j) {
                    if (elements[j].getName().equals(classElement)) {
                        wizardClassElement = elements[j];
                        break;
                    }
                }

                // now map all descriptor types in this extension to the classname
                for (int j = 0; j < elements.length; ++j) {
                    if (elements[j].getName().equals(descriptor)) {
                        String id = elements[j].getAttribute(desctiptorId);
                        result.put(id, wizardClassElement);
                    }
                }

            } catch (Exception e) {
                // catch any Exception that occurred obtaining the configuration and log it
                    String message = UiConstants.Util.getString("NewModelObjectWizardManager.configurationErrorMessage", //$NON-NLS-1$
    extensions[i].getUniqueIdentifier());
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }
        return result;
    }

}
