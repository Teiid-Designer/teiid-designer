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
package com.metamatrix.modeler.internal.dqp.ui.workspace.udf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import com.metamatrix.core.modeler.util.FileUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @since 5.0
 */
public class AddExistingUdfJarsDialog extends TitleAreaDialog implements DqpUiConstants {
    private static final String PREFIX = I18nUtil.getPropertyPrefix(AddExistingUdfJarsDialog.class);

    private static final String TITLE = UTIL.getString(PREFIX + "title"); //$NON-NLS-1$

    private TreeViewer viewer;

    private DqpExtensionFileContentProvider dqpExtensiontreeProvider;
    private DqpExtensionFileLabelProvider dqpExtensionLabelProvider;

    private ISelection lastSelection;

    /**
     * Construct an instance of ModelStatisticsDialog.
     */
    public AddExistingUdfJarsDialog( Shell shell ) {
        super(shell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     * @since 5.5.3
     */
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(TITLE);
    }

    /**
     * @see org.eclipse.jface.window.Window#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        setTitleImage(DqpUiPlugin.getDefault().getAnImage(Images.IMPORT_CONNECTORS_WIZBAN));
        setTitle(UTIL.getString(PREFIX + "header")); //$NON-NLS-1$

        Composite composite = (Composite)super.createDialogArea(parent);
        getShell().setMinimumSize(300, 500);

        GridData topCompositeGridData = new GridData(GridData.FILL_BOTH);
        topCompositeGridData.grabExcessHorizontalSpace = true;
        composite.setLayoutData(topCompositeGridData);

        viewer = new TreeViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

        GridData viewerGridData = new GridData(GridData.FILL_BOTH);
        viewerGridData.grabExcessHorizontalSpace = true;
        viewer.getControl().setLayoutData(viewerGridData);

        dqpExtensiontreeProvider = new DqpExtensionFileContentProvider();
        dqpExtensionLabelProvider = new DqpExtensionFileLabelProvider();
        viewer.setContentProvider(dqpExtensiontreeProvider);
        viewer.setLabelProvider(dqpExtensionLabelProvider);
        viewer.addFilter(new ViewerFilter() {

            @Override
            public boolean select( Viewer viewer,
                                   Object parentElement,
                                   Object element ) {
                if (element instanceof File) {
                    String name = ((File)element).getName();
                    return (name.endsWith(FileUtil.Extensions.JAR_LOWER) || name.endsWith(FileUtil.Extensions.JAR_UPPER));
                }

                return false;
            }
        });

        IPath extPath = DqpPlugin.getInstance().getExtensionsHandler().getDqpExtensionsFolderPath();
        File vdbExDirectory = new File(extPath.toOSString());
        viewer.setInput(vdbExDirectory);
        viewer.expandToLevel(2);

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                handleSelectionChanged(event);
            }
        });

        setMessage(UTIL.getString(PREFIX + "noJarsSelectedMsg")); //$NON-NLS-1$
        return composite;
    }

    void handleSelectionChanged( SelectionChangedEvent event ) {
        boolean okToFinish = true;

        setErrorMessage(null);
        setMessage(UTIL.getString(PREFIX + "selectOkToFinishMsg")); //$NON-NLS-1$
        lastSelection = event.getSelection();

        List<File> selectedObjs = SelectionUtilities.getSelectedObjects(lastSelection);
        boolean existingUDFs = false;
        int nExistingUdfs = 0;
        String firstExistingJar = null;
        if (selectedObjs != null && !selectedObjs.isEmpty()) {
            for (File theFile : selectedObjs) {
                if (DqpPlugin.getInstance().getExtensionsHandler().isUdfJar(theFile)) {
                    existingUDFs = true;
                    firstExistingJar = theFile.getName();
                    nExistingUdfs++;
                }
            }
        } else {
            okToFinish = false;
            setErrorMessage(UTIL.getString(PREFIX + "noJarsSelectedMsg")); //$NON-NLS-1$
        }

        if (existingUDFs) {
            okToFinish = false;
            if (nExistingUdfs == 1) {
                setErrorMessage(UTIL.getString(PREFIX + "jarAlreadyInUdfMsg", firstExistingJar)); //$NON-NLS-1$
            } else {
                setErrorMessage(UTIL.getString(PREFIX + "multipleExistingJarsSelectedMsg")); //$NON-NLS-1$
            }
            lastSelection = null;
        }

        getButton(IDialogConstants.OK_ID).setEnabled(okToFinish);

    }

    public ISelection getSelection() {
        return this.lastSelection;
    }

    /**
     * @see org.eclipse.jface.window.Window#create()
     */
    @Override
    public void create() {
        setShellStyle(getShellStyle() | SWT.RESIZE);
        super.create();
        super.getShell().setText(TITLE);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
    }

    class DqpExtensionFileLabelProvider extends LabelProvider {
        private final Image IMG_FOLDER = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
        private final Image IMG_FILE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
        private final Image JAR_FILE = DqpUiPlugin.getDefault().getImage(DqpUiConstants.Images.CONNECTOR_JAR_ICON);
        private final Image ZIP_FILE = DqpUiPlugin.getDefault().getImage(DqpUiConstants.Images.CONNECTOR_ZIP_ICON);

        @Override
        public Image getImage( Object element ) {
            if (element instanceof File) {
                File curr = (File)element;
                if (curr.isDirectory()) {
                    return IMG_FOLDER;
                } else if (curr.getName().indexOf(".jar") > -1) { //$NON-NLS-1$
                    return JAR_FILE;
                } else if (curr.getName().indexOf(".zip") > -1) { //$NON-NLS-1$
                    return ZIP_FILE;
                }

                return IMG_FILE;
            }
            return null;
        }

        @Override
        public String getText( Object element ) {
            if (element instanceof File) {
                return ((File)element).getName();
            }
            return super.getText(element);
        }
    }

    class DqpExtensionFileContentProvider implements ITreeContentProvider {

        private final Object[] EMPTY = new Object[0];

        public Object[] getChildren( Object parentElement ) {

            if (parentElement instanceof File) {
                File[] children = ((File)parentElement).listFiles();
                if (children != null) {
                    List<File> customConnectorJars = new ArrayList<File>();
                    for (File child : children) {
                        customConnectorJars.add(child);
                    }

                    if (!customConnectorJars.isEmpty()) {
                        return customConnectorJars.toArray();
                    }
                }
            }
            return EMPTY;
        }

        public Object getParent( Object element ) {
            if (element instanceof File) {
                return ((File)element).getParentFile();
            }
            return null;
        }

        public boolean hasChildren( Object element ) {
            return getChildren(element).length > 0;
        }

        public Object[] getElements( Object element ) {
            return getChildren(element);
        }

        public void dispose() {
        }

        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
        }
    }

}
