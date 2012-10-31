/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xsd.ui.wizards;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.FileSystemElement;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.internal.wizards.datatransfer.MinimizedFileSystemElement;
import org.eclipse.ui.internal.wizards.datatransfer.WizardFileSystemResourceImportPage1;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.xsd.ui.ModelerXsdUiConstants;


/** 
 * @since 8.0
 */
public class XsdFileSystemImportMainPage extends WizardFileSystemResourceImportPage1 {
    protected Button addDependentXsdsCheckbox;
    
    private static final String I18N_PREFIX         = "XsdFileSystemImportMainPage"; //$NON-NLS-1$
    private static final String SEPARATOR           = "."; //$NON-NLS-1$
    
    // dialog store id constants
    private final static String STORE_SOURCE_NAMES_ID = getString("storeSourceNamesId"); //$NON-NLS-1$
    private final static String STORE_OVERWRITE_EXISTING_RESOURCES_ID = getString("storeOverwriteExistingResourcesId");//$NON-NLS-1$
    private final static String STORE_CREATE_CONTAINER_STRUCTURE_ID = getString("storeCreateContainerStructureId"); //$NON-NLS-1$
    private final static String ADD_DEPENDENT_XSD_FILES_ID = getString("addDependentXsdFilesId");  //$NON-NLS-1$
    private final static String NO_RESOURCES_SELECTED_MESSAGE = getString("noResourcesSelectedMethod"); //$NON-NLS-1$
    private final static boolean allowEditLocation = ProductCustomizerMgr.getInstance().getProductCharacteristics().workspaceLocationExposed();

    private boolean okToOverwriteTarget = false;
    
    /**
     * Value that indicates an Eclipse target platform prior to 3.6.2.
     */
    private static final int TP_PRE_362 = 0;

    /**
     * Value that indicates an Eclipse target platform of 3.6.2 or later.
     */
    private static final int TP_362 = 1;

    /**
     * Indicates which Eclipse target platform is being used. <strong>This variable should not be referenced directly.</strong>
     * @see #isPre362()
     */
    private static int targetPlatform = -1;
    
    /**
     * @return <code>true</code> if the Eclipse target platform is earlier than 3.6.2
     */
    private static boolean isPre362() {
        if (targetPlatform == -1) {
            try {
                // this field exists prior to 3.6.2 only
                Class superClass = XsdFileSystemImportMainPage.class.getSuperclass();
                superClass.getDeclaredField("createContainerStructureButton"); //$NON-NLS-1$
                targetPlatform = TP_PRE_362;
            } catch (Throwable t) {
                // must be 3.6.2 or later
                targetPlatform = TP_362;
            }
        }
        
        return (targetPlatform == TP_PRE_362);
    }

    //============================================================================================================================
    // Static Methods
    
    private static String getString(final String id) {
        return ModelerXsdUiConstants.Util.getString(I18N_PREFIX + SEPARATOR + id);
    }

    /** 
     * @param theWorkbench
     * @param theSelection
     * @since 4.3
     */
    public XsdFileSystemImportMainPage(IWorkbench theWorkbench,
                                        IStructuredSelection theSelection) {
        super(theWorkbench, theSelection);
    }
    
    private Field getField(String name) throws Throwable {
        Class superClass = XsdFileSystemImportMainPage.class.getSuperclass();
        return superClass.getDeclaredField(name);
    }
    
    private boolean isSelected( String buttonName ) {
        // assumes only called when the field exists
        try {
            Field buttonField = getField(buttonName);
            return (Boolean)buttonField.getType().getMethod("getSelection").invoke(buttonField.get(this)); //$NON-NLS-1$
        } catch (Throwable e) {
            // should never happen
        }

        return false;
    }

    private void setEnabled( String buttonName,
                             boolean enable ) {
        // assumes only called when the field exists
        try {
            Field buttonField = getField(buttonName);
            buttonField.getType().getMethod("setEnabled", Boolean.TYPE).invoke(buttonField.get(this), enable); //$NON-NLS-1$
        } catch (Throwable e) {
            // should never happen
        }
    }

    private void setSelection( String buttonName,
                               boolean selected ) {
        // assumes only called when the field exists
        try {
            Field buttonField = getField(buttonName);
            buttonField.getType().getMethod("setSelection", Boolean.TYPE).invoke(buttonField.get(this), selected); //$NON-NLS-1$
        } catch (Throwable e) {
            // should never happen
        }
    }
    
    /**
     * Returns a content provider for <code>FileSystemElement</code>s that returns 
     * only files as children.
     */
    @Override
    protected ITreeContentProvider getFileProvider() {
        return new WorkbenchContentProvider() {
            @Override
            public Object[] getChildren(Object o) {
                if (o instanceof MinimizedFileSystemElement) {
                    MinimizedFileSystemElement element = (MinimizedFileSystemElement) o;
                    return element.getFiles( XsdFileSystemStructureProvider.INSTANCE).getChildren(element);
                }
                return new Object[0];
            }
        };
    }

    /**
     *  Answer the root FileSystemElement that represents the contents of
     *  the currently-specified source.  If this FileSystemElement is not
     *  currently defined then create and return it.
     */
    @Override
    protected MinimizedFileSystemElement getFileSystemTree() {

        File sourceDirectory = getSourceDirectory();
        if (sourceDirectory == null)
            return null;

        return selectFiles(sourceDirectory, XsdFileSystemStructureProvider.INSTANCE);
    }
    
    /**
     *  The Finish button was pressed.  Try to do the required work now and answer
     *  a boolean indicating success.  If false is returned then the wizard will
     *  not close.
     *
     * @return boolean
     */
    @Override
    public boolean finish() {
        if (!ensureSourceIsValid())
            return false;

        saveWidgetValues();

        Iterator resourcesEnum = getSelectedResources().iterator();
        List fileSystemObjects = new ArrayList();
        while (resourcesEnum.hasNext()) {
            fileSystemObjects.add(((FileSystemElement) resourcesEnum.next())
                    .getFileSystemObject());
        }

        //List xsdFiles, boolean addDependentXsds, IPath destinationFullPath, Shell shell, IWizardContainer container, IOverwriteQuery overwriteQuery, boolean createContainerStructure, boolean overwriteExistingResources) {
        boolean createContainer = isPre362() ? isSelected("createContainerStructureButton") //$NON-NLS-1$
                                             : isSelected("createTopLevelFolderCheckbox"); //$NON-NLS-1$

        // Set OK to overwrite flag before doing the import
        resetOKToOverwriteTarget();
        
        // Do the import
        return XsdFileSystemImportUtil.importXsds(fileSystemObjects,
                                                  addDependentXsdsCheckbox.getSelection(),
                                                  getContainerFullPath(),
                                                  getContainer(),
                                                  this,
                                                  createContainer,
                                                  this.overwriteExistingResourcesCheckbox.getSelection());
    } 

    /*
     * Set the OK To overwrite status.  This does a check on the target container, to see if it's empty. 
     * If the target container is empty, the status is set to 'true'.  
     */
    private void resetOKToOverwriteTarget() {
        this.okToOverwriteTarget = false;
        
        // User has selected the Overwrite Checkbox...
        if(this.overwriteExistingResourcesCheckbox.getSelection()) {
            this.okToOverwriteTarget = true;
        // Check the target container.  If the container is empty, ok to overwrite...
        } else {
            IPath path = getContainerFullPath();
            IWorkspaceRoot root = ModelerCore.getWorkspace().getRoot();
            IResource theResource = root.findMember(path);
            if(theResource!=null && theResource instanceof IContainer) {
                IResource[] containedResources = null;
                try {
                    containedResources = ((IContainer)theResource).members();
                } catch (CoreException ex) {
                    // Error getting the resources - okToOverwrite will be false.
                }
                // If no resources in the container, ok to overwrite.
                if(containedResources!=null && containedResources.length==0) {
                    this.okToOverwriteTarget = true;
                }
            }
        }
    }
    
    @Override
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.dialogs.WizardDataTransferPage#queryOverwrite(java.lang.String)
     */
    public String queryOverwrite(String pathString) {
        // If container is empty, ok to skip the query dialog
        if(this.okToOverwriteTarget) return IOverwriteQuery.YES;
        
        return super.queryOverwrite(pathString);
    }
    
    /** 
     * @see org.eclipse.ui.internal.wizards.datatransfer.WizardFileSystemResourceImportPage1#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.3
     */
    @Override
    public void createControl(Composite theParent) {
        super.createControl(theParent);
        if( !allowEditLocation ) {
            //destinationGroup.setVisible(false);
            
            // container path must always point to the hidden project
            IProject proj = ProductCustomizerMgr.getInstance().getProductCharacteristics().getHiddenProject();
            String path = proj.getFullPath().toPortableString();
            setContainerFieldValue(path);
        }
        
        if (isPre362()) {
            setEnabled("createContainerStructureButton", false); //$NON-NLS-1$
            setEnabled("createOnlySelectedButton", false); //$NON-NLS-1$
        } else {
            setEnabled("createTopLevelFolderCheckbox", false); //$NON-NLS-1$
        }
    }

    /**
     *  Create the import options specification widgets.
     */
    @Override
    protected void createOptionsGroupButtons(Group optionsGroup) {
        super.createOptionsGroupButtons(optionsGroup);
        // add dependent xsd's
        addDependentXsdsCheckbox = new Button(optionsGroup, SWT.CHECK);
        addDependentXsdsCheckbox.setFont(optionsGroup.getFont());
        addDependentXsdsCheckbox.setText(getString("addDependentXsdFiles.text")); //$NON-NLS-1$
        addDependentXsdsCheckbox.setSelection(true);
    }
    
    /**
     *  Answer a boolean indicating whether self's source specification
     *  widgets currently all contain valid values.
     */
    @Override
    protected boolean validateSourceGroup() {
        File sourceDirectory = getSourceDirectory();
        if (sourceDirectory == null) {
            setMessage(WizardFileSystemResourceImportPage1.SOURCE_EMPTY_MESSAGE);
            enableButtonGroup(false);
            return false;
        }

        if (sourceConflictsWithDestination(new Path(sourceDirectory.getPath()))) {
            setErrorMessage(getSourceConflictMessage()); 
            enableButtonGroup(false);
            return false;
        }
        
        if( getSelectedResources().isEmpty() ) {
            setErrorMessage(NO_RESOURCES_SELECTED_MESSAGE);
            return false;
        }
        
        if( getContainerFullPath() == null || getResourcePath().segmentCount() == 0 ) {
        	setErrorMessage(getString("missingTargetLocation")); //$NON-NLS-1$
        }

        enableButtonGroup(true);
        return true;
    }
    
    /**
     *  Use the dialog store to restore widget values to the values that they held
     *  last time this wizard was used to completion
     */
    @Override
    protected void restoreWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            String[] sourceNames = settings.getArray(STORE_SOURCE_NAMES_ID);
            if (sourceNames == null)
                return; // ie.- no values stored, so stop

            // set filenames history
            for (int i = 0; i < sourceNames.length; i++)
            	this.sourceNameField.add(sourceNames[i]);

            // radio buttons and checkboxes 
            this.overwriteExistingResourcesCheckbox.setSelection(settings
                    .getBoolean(STORE_OVERWRITE_EXISTING_RESOURCES_ID));
            boolean createStructure = settings.getBoolean(STORE_CREATE_CONTAINER_STRUCTURE_ID);
            
            if (isPre362()) {
                setSelection("createContainerStructureButton", createStructure); //$NON-NLS-1$
                setSelection("createOnlySelectedButton", !createStructure); //$NON-NLS-1$
            } else {
                setSelection("createTopLevelFolderCheckbox", createStructure); //$NON-NLS-1$
                setEnabled("createTopLevelFolderCheckbox", true); //$NON-NLS-1$
            }
            
            boolean addDependencies = settings.getBoolean(ADD_DEPENDENT_XSD_FILES_ID);
            addDependentXsdsCheckbox.setSelection(addDependencies);
        }
    }

    /**
     *  Since Finish was pressed, write widget values to the dialog store so that they
     *  will persist into the next invocation of this wizard page
     */
    @Override
    protected void saveWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {

            // persist location MRU
            WidgetUtil.saveSettings(getDialogSettings(), STORE_SOURCE_NAMES_ID, this.sourceNameField, 10);

            // radio buttons and checkboxes 
            settings.put(STORE_OVERWRITE_EXISTING_RESOURCES_ID,
                    overwriteExistingResourcesCheckbox.getSelection());

            if (isPre362()) {
                settings.put(STORE_CREATE_CONTAINER_STRUCTURE_ID, isSelected("createContainerStructureButton")); //$NON-NLS-1$
            } else {
                settings.put(STORE_CREATE_CONTAINER_STRUCTURE_ID, isSelected("createTopLevelFolderCheckbox")); //$NON-NLS-1$
            }
            
            settings.put(ADD_DEPENDENT_XSD_FILES_ID,
                         addDependentXsdsCheckbox.getSelection());
        }
    }
    
    /**
     *  Answer the directory name specified as being the import source.
     *  Note that if it ends with a separator then the separator is first
     *  removed so that java treats it as a proper directory
     */
    protected String getSourceDirectoryName() {
        // DONT THINK THIS METHOD IS NEEDED ANYMORE!!! Defect 20998 - wizard now saves MRU xsd locations. checked by Dan.
        String sName = this.sourceNameField.getText();
        IPath result = new Path(sName);

        if (result.getDevice() != null && result.segmentCount() == 0) // something like "c:"
            result = result.addTrailingSeparator();
        else
            result = result.removeTrailingSeparator();

        return result.toOSString();
    }
    
}