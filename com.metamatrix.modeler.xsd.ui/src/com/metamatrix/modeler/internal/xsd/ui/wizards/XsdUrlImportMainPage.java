/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.wizards;

import java.io.File;
import java.net.Authenticator;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import com.metamatrix.common.protocol.URLHelper;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * @since 5.5
 */
public class XsdUrlImportMainPage extends AbstractWizardPage implements IOverwriteQuery {
    private static final String I18N_PREFIX = "XsdUrlImportMainPage"; //$NON-NLS-1$
    private static final String SEPARATOR = "."; //$NON-NLS-1$

    private TableViewer viewer;

    Map urlToUserInfo = new HashMap();

    private IAction addUrlAction;
    private IAction removeUrlAction;

    private Button addDependentXsdsCheckbox;
    private Button overwriteExistingResourcesCheckbox;
    private Text containerNameField;

    private IStructuredSelection defaultSelection;

    public XsdUrlImportMainPage( IWorkbench theWorkbench,
                                 IStructuredSelection theSelection ) {
        super(XsdUrlImportMainPage.class.getSimpleName(), getString("title"));//$NON-NLS-1$
        defaultSelection = theSelection;
    }

    private static String getString( final String id ) {
        return ModelerXsdUiConstants.Util.getString(I18N_PREFIX + SEPARATOR + id);
    }

    public void createControl( Composite theParent ) {
        final int COLUMNS = 1;
        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        pnlMain.setLayout(new GridLayout(COLUMNS, false));
        setControl(pnlMain);
        createUrlLocationsTable(pnlMain);
        createDestinationGroup(pnlMain);
        createOptionsPanel(pnlMain);
    }

    public boolean finish() {
        List xsdFiles = new ArrayList();
        URL url = null;
        Map fileToUserInfo = new HashMap();
        try {
            // create temp files from URLs
            Iterator iter = urlToUserInfo.keySet().iterator();
            while (iter.hasNext()) {
                url = (URL)iter.next();
                Object[] userInfo = (Object[])urlToUserInfo.get(url);
                String filePath = url.getPath();
                if (filePath.startsWith("/")) {//$NON-NLS-1$
                    filePath = filePath.substring(1);
                }
                File xsdFile = URLHelper.createFileFromUrl(url,
                                                           filePath,
                                                           (String)userInfo[0],
                                                           (String)userInfo[1],
                                                           ((Boolean)userInfo[2]).booleanValue());
                xsdFiles.add(xsdFile);
                fileToUserInfo.put(xsdFile, urlToUserInfo.get(url));
            }
        } catch (Exception e) {
            ModelerXsdUiConstants.Util.log(e);
            MessageDialog.openError(getShell(), getString("error1.title"),//$NON-NLS-1$
                                    ModelerXsdUiConstants.Util.getString(I18N_PREFIX + SEPARATOR + "error1.message", url));//$NON-NLS-1$
            return false;
        }

        // import XSDs
        XsdFileSystemImportUtil.importXsds(xsdFiles,
                                           addDependentXsdsCheckbox.getSelection(),
                                           getContainerFullPath(),
                                           getContainer(),
                                           this,
                                           false,
                                           overwriteExistingResourcesCheckbox.getSelection(),
                                           fileToUserInfo);

        Authenticator.setDefault(null);
        return true;
    }

    @Override
    public boolean isPageComplete() {
        if (urlToUserInfo.isEmpty()) {
            return false;
        }
        return this.validateDestinationFolder();
    }

    void handleModifyText() {
        getContainer().updateButtons();
    }

    private void createDestinationGroup( Composite parent ) {
        // container specification group
        Composite containerGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        containerGroup.setLayout(layout);
        containerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        containerGroup.setFont(parent.getFont());

        // container label
        Label resourcesLabel = new Label(containerGroup, SWT.NONE);
        resourcesLabel.setText(getString("folderLabel")); //$NON-NLS-1$
        resourcesLabel.setFont(parent.getFont());

        // container name entry field
        containerNameField = new Text(containerGroup, SWT.SINGLE | SWT.BORDER);
        containerNameField.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                handleModifyText();
            }
        });
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        containerNameField.setLayoutData(data);
        containerNameField.setFont(parent.getFont());

        // container browse button
        Button containerBrowseButton = new Button(containerGroup, SWT.PUSH);
        containerBrowseButton.setText(getString("browse")); //$NON-NLS-1$
        containerBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        containerBrowseButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected( SelectionEvent e ) {
                handleContainerBrowseButtonPressed();
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });
        containerBrowseButton.setFont(parent.getFont());
        setButtonLayoutData(containerBrowseButton);

        // set init value for the destination
        if (defaultSelection != null) {
            IResource currentResourceSelection = null;
            if (defaultSelection.size() == 1) {
                Object firstElement = defaultSelection.getFirstElement();
                if (firstElement instanceof IAdaptable) {
                    Object resource = ((IAdaptable)firstElement).getAdapter(IResource.class);
                    if (resource != null) currentResourceSelection = (IResource)resource;
                }
            }
            if (currentResourceSelection != null) {
                if (currentResourceSelection.getType() == IResource.FILE) currentResourceSelection = currentResourceSelection.getParent();

                if (!currentResourceSelection.isAccessible()) currentResourceSelection = null;
            }
            if (currentResourceSelection != null) {
                containerNameField.setText(currentResourceSelection.getFullPath().makeRelative().toString());
            }
        }
    }

    private final boolean validateDestinationFolder() {
        IPath containerPath = getContainerFullPath();
        if (containerPath == null) {
            setMessage(IDEWorkbenchMessages.WizardImportPage_specifyFolder);
            return false;
        }

        // If the container exist, validate it
        IContainer container = getSpecifiedContainer();
        if (container == null) {
            // If it exists but is not valid then abort
            if (IDEWorkbenchPlugin.getPluginWorkspace().getRoot().exists(getContainerFullPath())) return false;

            // if it is does not exist be sure the project does
            IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();
            IPath projectPath = containerPath.removeLastSegments(containerPath.segmentCount() - 1);

            if (workspace.getRoot().exists(projectPath)) return true;
            setErrorMessage(IDEWorkbenchMessages.WizardImportPage_projectNotExist);
            return false;
        }
        if (!container.isAccessible()) {
            setErrorMessage(IDEWorkbenchMessages.WizardImportPage_folderMustExist);
            return false;
        }
        if (container.getLocation() == null) {
            if (container.isLinked()) {
                setErrorMessage(IDEWorkbenchMessages.WizardImportPage_undefinedPathVariable);
            } else {
                setErrorMessage(IDEWorkbenchMessages.WizardImportPage_containerNotExist);
            }
            return false;
        }

        return true;

    }

    void handleContainerBrowseButtonPressed() {
        // see if the user wishes to modify this container selection
        IPath containerPath = queryForContainer(getSpecifiedContainer(),
                                                IDEWorkbenchMessages.WizardImportPage_selectFolderLabel,
                                                IDEWorkbenchMessages.WizardImportPage_selectFolderTitle);

        // if a container was selected then put its name in the container name field
        if (containerPath != null) { // null means user cancelled
            setErrorMessage(null);
            containerNameField.setText(containerPath.makeRelative().toString());
        }
    }

    private IContainer getSpecifiedContainer() {
        IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();
        IPath path = getContainerFullPath();
        if (workspace.getRoot().exists(path)) {
            IResource resource = workspace.getRoot().findMember(path);
            if (resource.getType() == IResource.FILE) return null;
            return (IContainer)resource;

        }

        return null;
    }

    private IPath getContainerFullPath() {
        IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();

        // make the path absolute to allow for optional leading slash
        IPath testPath = getPathFromText(containerNameField);

        if (testPath.equals(workspace.getRoot().getFullPath())) return testPath;

        IStatus result = workspace.validatePath(testPath.toString(), IResource.PROJECT | IResource.FOLDER | IResource.ROOT);
        if (result.isOK()) {
            return testPath;
        }

        return null;
    }

    protected IPath getPathFromText( Text textField ) {
        String text = textField.getText();
        if (text.length() == 0) return new Path(text);

        return (new Path(text)).makeAbsolute();
    }

    private IPath queryForContainer( IContainer initialSelection,
                                     String msg,
                                     String title ) {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(getControl().getShell(), initialSelection, true, msg);
        if (title != null) dialog.setTitle(title);
        dialog.showClosedProjects(false);
        dialog.open();
        Object[] result = dialog.getResult();
        if (result != null && result.length == 1) {
            return (IPath)result[0];
        }
        return null;
    }

    private void createUrlLocationsTable( Composite theParent ) {
        ViewForm viewForm = WidgetFactory.createViewForm(theParent, SWT.BORDER, GridData.FILL_BOTH, 1);
        viewForm.setTopLeft(WidgetFactory.createLabel(viewForm, getString("label.table"))); //$NON-NLS-1$

        Composite pnl = WidgetFactory.createPanel(viewForm, SWT.NONE, GridData.FILL_BOTH);
        viewForm.setContent(pnl);

        // table
        this.viewer = WidgetFactory.createTableViewer(pnl, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
        this.viewer.setContentProvider(new IStructuredContentProvider() {

            public void dispose() {
            }

            public Object[] getElements( Object theInputElement ) {
                return urlToUserInfo.keySet().toArray();
            }

            public void inputChanged( Viewer theViewer,
                                      Object theOldInput,
                                      Object theNewInput ) {
            }
        });
        LabelProvider labelProvider = new LabelProvider() {
            @Override
            public String getText( Object theElement ) {
                return theElement.toString();
            }
        };
        this.viewer.setLabelProvider(labelProvider);
        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleTableSelectionChanged();
            }
        });
        // this.viewer.addFilter(this.wsdlFilter);

        // create toolbar actions
        createTableActions(WidgetFactory.createViewFormToolBar(viewForm));

        // context menu for table
        createTableContextMenu();
    }

    void handleTableSelectionChanged() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        this.removeUrlAction.setEnabled(!selection.isEmpty());
        getContainer().updateButtons();
    }

    private void createTableActions( IToolBarManager theToolBarMgr ) {
        // Add action
        this.addUrlAction = new Action(getString("action.addUrl"), IAction.AS_PUSH_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                handleAddUrl();
            }
        };

        this.addUrlAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
        this.addUrlAction.setToolTipText(getString("action.addUrl.tip")); //$NON-NLS-1$
        this.addUrlAction.setEnabled(true);

        theToolBarMgr.add(this.addUrlAction);

        // remove action
        this.removeUrlAction = new Action(getString("action.removeUrl"), IAction.AS_PUSH_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                handleRemoveUrl();
            }
        };

        this.removeUrlAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        this.removeUrlAction.setToolTipText(getString("action.removeUrl.tip")); //$NON-NLS-1$
        this.removeUrlAction.setEnabled(false);

        theToolBarMgr.add(this.removeUrlAction);

        // update toolbar to pick up actions
        theToolBarMgr.update(true);
    }

    protected void createTableContextMenu() {
        MenuManager mgr = new MenuManager();
        mgr.add(this.addUrlAction);
        mgr.add(this.removeUrlAction);

        Control table = this.viewer.getControl();
        table.setMenu(mgr.createContextMenu(table));
    }

    void handleAddUrl() {
        XsdUrlDialog dialog = new XsdUrlDialog(getShell());
        dialog.create();
        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            URL newUrl = dialog.getUrlObject();
            urlToUserInfo.put(newUrl, new Object[] {dialog.getUserName(), dialog.getPassword(),
                new Boolean(dialog.verifyHostname())});
            viewer.add(newUrl);
            viewer.setSelection(new StructuredSelection(newUrl), true);
        }
        handleTableSelectionChanged();
    }

    void handleRemoveUrl() {
        Object[] selectedUrls = ((IStructuredSelection)this.viewer.getSelection()).toArray();

        for (int i = 0; i < selectedUrls.length; i++) {
            this.urlToUserInfo.remove(selectedUrls[i]);
        }

        // remove files from viewer
        this.viewer.remove(selectedUrls);

        // enable buttons
        handleTableSelectionChanged();
    }

    private void createOptionsPanel( Composite theParent ) {
        Group optionsGroup = new Group(theParent, SWT.NONE);
        GridLayout layout = new GridLayout();
        optionsGroup.setLayout(layout);
        optionsGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        optionsGroup.setText(IDEWorkbenchMessages.WizardExportPage_options);
        optionsGroup.setFont(theParent.getFont());
        // overwrite... checkbox
        overwriteExistingResourcesCheckbox = new Button(optionsGroup, SWT.CHECK);
        overwriteExistingResourcesCheckbox.setFont(optionsGroup.getFont());
        overwriteExistingResourcesCheckbox.setText(DataTransferMessages.FileImport_overwriteExisting);

        // add dependent xsd's
        addDependentXsdsCheckbox = new Button(optionsGroup, SWT.CHECK);
        addDependentXsdsCheckbox.setFont(optionsGroup.getFont());
        addDependentXsdsCheckbox.setText(getString("addDependentXsdFiles.text")); //$NON-NLS-1$
        addDependentXsdsCheckbox.setSelection(true);
    }

    public String queryOverwrite( String pathString ) {

        Path path = new Path(pathString);

        String messageString;
        // Break the message up if there is a file name and a directory
        // and there are at least 2 segments.
        if (path.getFileExtension() == null || path.segmentCount() < 2) messageString = NLS.bind(IDEWorkbenchMessages.WizardDataTransfer_existsQuestion,
                                                                                                 pathString);

        else messageString = NLS.bind(IDEWorkbenchMessages.WizardDataTransfer_overwriteNameAndPathQuestion,
                                      path.lastSegment(),
                                      path.removeLastSegments(1).toOSString());

        final MessageDialog dialog = new MessageDialog(getContainer().getShell(), IDEWorkbenchMessages.Question, null,
                                                       messageString, MessageDialog.QUESTION, new String[] {
                                                           IDialogConstants.YES_LABEL, IDialogConstants.YES_TO_ALL_LABEL,
                                                           IDialogConstants.NO_LABEL, IDialogConstants.NO_TO_ALL_LABEL,
                                                           IDialogConstants.CANCEL_LABEL}, 0);
        String[] response = new String[] {YES, ALL, NO, NO_ALL, CANCEL};
        // run in syncExec because callback is from an operation,
        // which is probably not running in the UI thread.
        getControl().getDisplay().syncExec(new Runnable() {
            public void run() {
                dialog.open();
            }
        });
        return dialog.getReturnCode() < 0 ? CANCEL : response[dialog.getReturnCode()];
    }
}
