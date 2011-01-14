/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.ui.internal.wizards.datatransfer.MinimizedFileSystemElement;
import org.eclipse.ui.internal.wizards.datatransfer.WizardFileSystemResourceImportPage1;
import org.eclipse.ui.model.WorkbenchContentProvider;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.WidgetUtil;

/** 
 * @since 4.3
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
    
    //============================================================================================================================
    // Static Methods
    
    private static String getString(final String id) {
        return ModelerXsdUiConstants.Util.getString(I18N_PREFIX + SEPARATOR + id);
    }
    
    /** 
     * @param theName
     * @param theWorkbench
     * @param theSelection
     * @since 4.3
     */
    public XsdFileSystemImportMainPage(String theName,
                                        IWorkbench theWorkbench,
                                        IStructuredSelection theSelection) {
        super(theName, theWorkbench, theSelection);
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

        return XsdFileSystemImportUtil.importXsds(fileSystemObjects, addDependentXsdsCheckbox.getSelection(), getContainerFullPath(), getContainer(), this, this.createTopLevelFolderCheckbox.getSelection(), this.overwriteExistingResourcesCheckbox.getSelection());
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
        
        this.createTopLevelFolderCheckbox.setEnabled(false);
//        this.createOnlySelectedButton.setEnabled(false);
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

            if( this.createTopLevelFolderCheckbox != null ) {
            	boolean createStructure = settings
                    .getBoolean(STORE_CREATE_CONTAINER_STRUCTURE_ID);
            		this.createTopLevelFolderCheckbox.setSelection(createStructure);
            		this.createTopLevelFolderCheckbox.setEnabled(true);
            }
//            this.createOnlySelectedButton.setSelection(!createStructure);
            
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

            settings.put(STORE_CREATE_CONTAINER_STRUCTURE_ID,
                    createTopLevelFolderCheckbox.getSelection());
            
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