/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.product;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;

import com.metamatrix.ui.internal.product.WorkbenchState;




/** 
 * Interface which allows the product manager to relay the workbench/product characteristics
 * or behaviors to actions, widgets, dialogs and wizards.
 * @since 4.3
 */
public interface IProductCharacteristics {
    
    public static final int DESCRIPTION = 1; 
    public static final int IS_READONLY = 2; 
    
    

    /**
     * Method returns whether or not a specific product really exposes the workspace/project location
     * Some wizards, dialogs (i.e. Import, etc..) require the user to specify which project or project folder to import
     * into. These import wizards & dialogs can now assess whether or not to make these options visible and how to deal with this
     * situation.
     * @return true if workspace location and standard projects are exposed via some navigator/tree view, false if not
     * @since 4.3
     */
    boolean workspaceLocationExposed();
    
    /**
     * Obtains the product's primary navigation view id.
     * This is required by some actions so they can programmatically set selection or reveal specific objects... typically
     * in a tree view.  The default will be eclipse's resource navigator.
     * @return the ID
     * @since 4.3
     */
    String getPrimaryNavigationViewId();
    
    /**
     * Obtains the identifier of the product's default perspective if one exists. 
     * @return the ID or <code>null</code>
     * @since 5.0
     */
    String getDefaultPerspectiveId();
    
    /**
     * Method returns whether or not the specific product maintains a primary hidden project (ala Siperian/Dimension)
     * @return
     * @since 4.3
     */
    boolean isHiddenProjectCentric();
    
    /**
     * Obtains the hidden project. If the project does not exist, the user will be prompted to create one. 
     * @return the hidden project or <code>null</code> if one does not exist or if not a hidden project centric product
     * @since 4.3
     */
    IProject getHiddenProject();
    
    /**
     * Obtains the hidden project.
     * @param theCreateProjectFlag the flag indicating if the user should be prompted to create the hidden project
     * if it does not exist
     * @return the hidden project or <code>null</code> if one does not exist or if not a hidden project centric product
     * @since 4.4
     */
    IProject getHiddenProject(boolean theCreateProjectFlag);

    /**
     * Gets the IAction that corresponds to the specified ID.  Note: it is up to the
     * implementor as to what the ID means.  It may not be the same as Eclipse's IDs
     * as defined in plugin.xml, for example.
     * 
     * @param productActionID the ID to obtain.
     * @return the corresponding IAction
     */
    IAction getProductAction(String productActionID);
    
    /**
     * Gets the IAction that corresponds to the specified ID.  Note: it is up to the
     * implementor as to what the ID means.  It may not be the same as Eclipse's IDs
     * as defined in plugin.xml, for example.
     * 
     * @param retargetableActionID the ID to obtain.
     * @return the corresponding IAction
     */
    IAction getRetargetableAction(String retargetableActionID, IWorkbenchWindow theWindow);
    
    /**
     * Obtains an <code>IWizardPage</code> capable of creating the hidden project.
     * @return the wizard page if a hidden project needs to be created; otherwise <code>null</code>.
     * @since 4.4
     */
    IWizardPage getCreateHiddenProjectWizardPage();
    
    /**
     * Obtains a workbench state 
     * @return instanceof WorkbenchState
     * @since 5.0
     */
    WorkbenchState getWorkbenchState();
    
    /**
     * Obtains <code>ContributionItem</code> which provides an MRU list for the File Menu
     * @return
     * @since 5.0
     */
    ContributionItem getFileMruMenu();
    
    /**
     * Obtains an object representing this product's root context for models.   
     * @return an Object that is the root workspace for this product.  Known implementations
     * include the <code>WorkspaceRoot</code> and the current <code>VdbEditingContext</code>.
     * @since 5.0
     */
    Object getRootWorkspaceContext();
    
    /**
     * Method provides ability for products to operate on perspectives and views based on activation events of Parts.
     * @param eventID
     * @param ref
     * @since 5.0
     */
    void handlePartEvent(int eventID, IWorkbenchPartReference ref );
    
    /**
     * Method provides ability for products to operate on perspectives and views based on activation events of perspectives.
     * @param eventID
     * @param page
     * @param perspective
     * @since 5.0
     */
    void handlePerspectiveEvent(int eventID, IWorkbenchPage page, IPerspectiveDescriptor perspective );
    
    /**
     * Method used by NewModelWizard to determine if any customizer can pre-populate the NewModeWizard with model class, type 
     * or builder info based on selection. 
     * @param theSelection
     * @return
     * @since 5.0
     */
    public Object getNewModelInput(ISelection theSelection);
    
    
    /**
     * Method used by actions, wizards, etc... to pre-process using some input object.
     * implementors should check object type, process the object, then return true if processing should continue, or false if 
     * processing should stop. Implementors should handle all UI/Dialog within this method. 
     * @param someObject
     * @return
     * @since 5.0
     */
    public boolean preProcess(Object someObject, Shell shell);
    
    
    /**
     * Method used by actions, wizards, etc... to post-process using some input object.
     * implementors should check object type, process the object, then return true if processing should continue, or false if 
     * processing should stop. Implementors should handle all UI/Dialog within this method. 
     * @param someObject
     * @return
     * @since 5.0
     */
    public boolean postProcess(Object someObject, Shell shell);
    
    /**
     * Method used by actions, wizards, views, etc... to get the specified information about an object. 
     * implementors should check info type, process the object, then return the info.  
     * @param someObject object to be interpreted
     * @param infoType type of information desired
     * @return
     * @since 5.0
     */
    public Object getObjectInfo(int infoType, Object theSomeObject);
    
    
    /**
     * Method used by actions, wizards, views, etc... to set the specified information on an object. 
     * implementors should check info type, then appy the info to the object.  
     * @param someObject object to be interpreted
     * @param infoType type of information desired
     * @param theValue new value
     * @return
     * @since 5.0
     */
    public void setObjectInfo(int infoType, Object theSomeObject, Object theValue);
    
}
