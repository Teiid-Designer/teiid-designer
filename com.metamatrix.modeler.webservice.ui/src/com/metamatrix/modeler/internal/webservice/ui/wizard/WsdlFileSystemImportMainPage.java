/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.webservice.ui.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.FileSystemElement;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.eclipse.ui.internal.wizards.datatransfer.MinimizedFileSystemElement;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetUtil;


/**
 * @since 5.0
 */
public class WsdlFileSystemImportMainPage extends WsdlFileSystemResourcePage {

    private static final String I18N_PREFIX         = "WsdlFileSystemImportMainPage"; //$NON-NLS-1$
    private static final String SEPARATOR           = "."; //$NON-NLS-1$

    // dialog store id constants
    private final static String STORE_SOURCE_NAMES_ID = getString("storeSourceNamesId"); //$NON-NLS-1$
    private final static String NO_RESOURCES_SELECTED_MESSAGE = getString("noResourcesSelectedMethod"); //$NON-NLS-1$
	//    private final static boolean allowEditLocation = ProductCustomizerMgr.getInstance().getProductCharacteristics().workspaceLocationExposed();

    //============================================================================================================================
    // Static Methods

    private static String getString(final String id) {
        return IInternalUiConstants.UTIL.getString(I18N_PREFIX + SEPARATOR + id);
    }

    private File topLevelSourceDirectory;

    /**
     * @param theName
     * @param theWorkbench
     * @param theSelection
     * @since 4.3
     */
    public WsdlFileSystemImportMainPage(String theName,
                                        IWorkbench theWorkbench,
                                        IStructuredSelection theSelection) {
        super(theName, theWorkbench, theSelection);
    }

    /**
     * @param theWorkbench
     * @param theSelection
     * @since 4.3
     */
    public WsdlFileSystemImportMainPage(IWorkbench theWorkbench,
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
                    return element.getFiles( WsdlFileSystemStructureProvider.INSTANCE).getChildren(element);
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

        return selectFiles(sourceDirectory, WsdlFileSystemStructureProvider.INSTANCE);
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

        if (fileSystemObjects.size() > 0) {

            return importResources(fileSystemObjects);
        }

        MessageDialog.openInformation(getContainer().getShell(),
                DataTransferMessages.DataTransfer_information,
                DataTransferMessages.FileImport_noneSelected);

        return false;
    }

    /**
     * Override method because the topLevelSourceDirectory needs to get passed into the ImportOperation instead of the
     * directory selected in the wizard dialog.  This insures that the final folder structure created for the dependent xsds
     * is correct.
     * @see org.eclipse.ui.internal.wizards.datatransfer.WizardFileSystemResourceImportPage1#importResources(java.util.List)
     * @since 4.3
     */
    @Override
    protected boolean importResources(List fileSystemObjects) {
        ImportOperation operation = new ImportOperation(getContainerFullPath(),
                topLevelSourceDirectory, FileSystemStructureProvider.INSTANCE,
                this, fileSystemObjects);

        operation.setContext(getShell());
        boolean importResult = executeImportOperation(operation);
        // We need to reset these files to "Not Modified"
        for( Iterator iter = fileSystemObjects.iterator(); iter.hasNext(); ) {
            Object nextObj = iter.next();
            if( nextObj instanceof File  && !((File)nextObj).isDirectory()) {
                String name = ((File)nextObj).getName();
                // do something here if .xsd
                if( name.indexOf(".xsd") > -1 ) { //$NON-NLS-1$
                    Resource[] resources = null;

                    try {
                        resources = ModelWorkspaceManager.getModelWorkspaceManager().getModelContainer().getResourceFinder().findByName(name, false, false);
                        if( resources != null && resources.length == 1) {
                            ModelResource mr = ModelUtilities.getModelResource(resources[0], true);
                            if( mr != null ) {
                                mr.getEmfResource().setModified(false);
                            }
                        }
                    } catch (CoreException theException) {
                    }


                }
            }
        }
        return importResult;
    }



    /**
     * @see org.eclipse.ui.internal.wizards.datatransfer.WizardFileSystemResourceImportPage1#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.3
     */
    @Override
    public void createControl(Composite theParent) {
        super.createControl(theParent);
        //if( !allowEditLocation )
        //    destinationGroup.setVisible(false);
    }

    /**
     *  Create the import options specification widgets.
     */
    @Override
    protected void createOptionsGroupButtons(Group optionsGroup) {
    }

    /**
     *  Answer a boolean indicating whether self's source specification
     *  widgets currently all contain valid values.
     */
    @Override
    protected boolean validateSourceGroup() {
        File sourceDirectory = getSourceDirectory();
        if (sourceDirectory == null) {
            setMessage(SOURCE_EMPTY_MESSAGE);
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
                sourceNameField.add(sourceNames[i]);
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
