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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.modeler.webservice.IWebServiceModelBuilder;
import com.metamatrix.modeler.webservice.IWebServiceXsdResource;
import com.metamatrix.modeler.webservice.ui.WebServiceUiPlugin;
import com.metamatrix.modeler.webservice.ui.util.WebServiceUiUtil;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.GridPanel;

/**
 * SchemaLocationEditor
 */
public class SchemaLocationEditor extends GridPanel
    implements IInternalUiConstants, IInternalUiConstants.HelpContexts, IInternalUiConstants.Images {

    /*----- DESIGN NOTES --------------------------------------------------------------------------

     The editor was written such that it can handle multiple objects as input. This could allow
     in the future to set the folder of multiple xsd resources to the same folder.

    ---------------------------------------------------------------------------------------------*/

    /** Title prefix indicating editor is dirty. */
    private static final String DIRTY_INDICATOR = "*"; //$NON-NLS-1$

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(SchemaLocationEditor.class);

    /** Event type indicating only the folder was saved. */
    public static final int FOLDER_ONLY_SAVED = 0;

    /** Event type indicating only the model name was saved. */
    public static final int MODEL_ONLY_SAVED = 1;

    /** Event type indicating both the folder and model name was saved. */
    public static final int SAVED = 2;

    /** Event type indicating the editor has closed. */
    public static final int CLOSED = 3;

    /** Key for target folder MRU list. */
    private static final String TARGET_FOLDER_MRU = "targetFolderList"; //$NON-NLS-1$

    private String currentFolder;

    private boolean dirty;

    private boolean isValid;

    /** Collection of IWebServceXsdResources being edited. */
    private List editorInput;

    private IWebServiceModelBuilder builder;

    private String initialFolder;

    private String initialName;

    private ListenerList listeners;

    private IDialogSettings settings;

    private TargetFilter targetFilter;

    private Button btnApply;

    private Button btnFolder;

    private Combo cbxFolder;

    private Button chkFolder;

    private Button chkName;

    private CLabel lblEditorTitle;

    private CLabel lblStatusMsg;

    private Text txfName;

    private ViewForm viewForm;

    /**
     * @param theParent
     * @param theImporter
     * @since 4.1
     */
    public SchemaLocationEditor( Composite theParent,
                                 IWebServiceModelBuilder theBuilder ) {
        super(theParent);

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(this, SCHEMA_LOCATION_EDITOR);

        this.builder = theBuilder;
        this.listeners = new ListenerList(ListenerList.IDENTITY);
        this.targetFilter = new TargetFilter();

        constructUi(this);
    }

    /**
     * @param theListener
     * @since 4.1
     */
    public void addListener( Listener theListener ) {
        this.listeners.add(theListener);
    }

    /**
     * @since 4.1
     */
    public void clear() {
        setInput(null);
    }

    /**
     * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
     */
    @Override
    public Point computeSize( int theWidgthHint,
                              int theHeightHint,
                              boolean theChangedFlag ) {
        // since the super's impl doesn't factor in visibility this is needed.
        return (getVisible() ? super.computeSize(theWidgthHint, theHeightHint, theChangedFlag) : new Point(0, 0));
    }

    private void constructUi( Composite theParent ) {
        this.viewForm = WidgetFactory.createViewForm(theParent, SWT.BORDER, GridData.FILL_BOTH, 1);

        this.lblEditorTitle = WidgetFactory.createLabel(this.viewForm,
                                                        getTitle(),
                                                        WebServiceUiPlugin.getDefault().getImage(SCHEMA_EDITOR),
                                                        SWT.NONE);
        this.viewForm.setTopLeft(this.lblEditorTitle);

        // create toolbar and install actions
        createActions(WidgetFactory.createViewFormToolBar(this.viewForm));

        final int COLUMNS = 4;
        Composite pnl = WidgetFactory.createPanel(this.viewForm, 0, GridData.FILL_BOTH, 1, COLUMNS);
        this.viewForm.setContent(pnl);

        //
        // ROW 1
        //

        // folder checkbox
        this.chkFolder = WidgetFactory.createCheckBox(pnl);
        this.chkFolder.setEnabled(false);
        this.chkFolder.setToolTipText(UTIL.getString(PREFIX + "checkBox.folder.tip")); //$NON-NLS-1$
        this.chkFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleFolderCheckBoxSelected();
            }
        });

        // folder label
        CLabel folderLabel = WidgetFactory.createLabel(pnl, UTIL.getString(PREFIX + "label.folder")); //$NON-NLS-1$

        // folder combo
        this.cbxFolder = WidgetFactory.createCombo(pnl, SWT.READ_ONLY, GridData.FILL_HORIZONTAL);
        this.cbxFolder.setEnabled(false);
        this.cbxFolder.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleFolderChanged();
            }
        });

        // folder browse button
        this.btnFolder = WidgetFactory.createButton(pnl, UTIL.getString(PREFIX + "button.browse")); //$NON-NLS-1$
        this.btnFolder.setEnabled(false);
        this.btnFolder.setToolTipText(UTIL.getString(PREFIX + "button.browse.folder.tip")); //$NON-NLS-1$
        this.btnFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleBrowseFolderSelected();
            }
        });

        //
        // ROW 2
        //

        // model checkbox
        this.chkName = WidgetFactory.createCheckBox(pnl);
        this.chkName.setEnabled(false);
        this.chkName.setToolTipText(UTIL.getString(PREFIX + "checkBox.targetName.tip")); //$NON-NLS-1$
        this.chkName.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleNameCheckBoxSelected();
            }
        });

        // folder label
        WidgetFactory.createLabel(pnl, UTIL.getString(PREFIX + "label.targetName")); //$NON-NLS-1$

        // model combo
        this.txfName = WidgetFactory.createTextField(pnl, GridData.HORIZONTAL_ALIGN_FILL, 2);
        this.txfName.setEnabled(false);
        this.txfName.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleNameChanged();
            }
        });

        //
        // ROW 3
        //

        // label showing selected unit's status message
        this.lblStatusMsg = WidgetFactory.createLabel(pnl, GridData.HORIZONTAL_ALIGN_FILL, COLUMNS);

        //
        // ROW 4
        //

        final int NUM_BUTTONS = 1;
        int style = GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_END;
        Composite pnlButtons = WidgetFactory.createPanel(pnl, SWT.NONE, style, COLUMNS, NUM_BUTTONS);
        ((GridLayout)pnlButtons.getLayout()).marginWidth = 0;

        // apply button
        this.btnApply = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.apply")); //$NON-NLS-1$
        this.btnApply.setEnabled(false);
        this.btnApply.setToolTipText(UTIL.getString(PREFIX + "button.apply.tip")); //$NON-NLS-1$
        this.btnApply.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleApplySelected();
            }
        });

        // If hidden project product, don't show folder/location widgets
        if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            folderLabel.setVisible(false);
            btnFolder.setVisible(false);
            cbxFolder.setVisible(false);
            chkFolder.setVisible(false);
        }
    }

    private void createActions( IToolBarManager theToolBarMgr ) {
        Action action = new Action() {
            @Override
            public void run() {
                setVisible(false);
                fireEvent(CLOSED);
            }
        };

        action.setImageDescriptor(WebServiceUiPlugin.getDefault().getImageDescriptor(CLOSE_EDITOR));
        action.setToolTipText(UTIL.getString(PREFIX + "button.closeEditor.tip")); //$NON-NLS-1$
        theToolBarMgr.add(action);

        theToolBarMgr.update(true);
    }

    void fireEvent( int theType ) {
        Event event = new Event();
        event.widget = this;
        event.type = theType;

        // notify listeners
        Object[] stateListeners = this.listeners.getListeners();

        for (int i = 0; i < stateListeners.length; i++) {
            ((Listener)stateListeners[i]).handleEvent(event);
        }
    }

    private String getContainerText( IContainer theContainer ) {
        return (theContainer == null) ? "" //$NON-NLS-1$
        : theContainer.getFullPath().makeRelative().toString();
    }

    public List getInput() {
        return this.editorInput;
    }

    private IPath getNewPath( String theFolder,
                              String theName ) {
        IPath result = null;
        IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(theFolder);

        if ((resource != null) && (resource instanceof IContainer)) {
            IContainer folder = (IContainer)resource;
            result = folder.getFullPath().append(theName).makeRelative();
        }

        return result;
    }

    private String getTitle() {
        String result = null;

        if ((this.editorInput == null) || this.editorInput.isEmpty()) {
            result = UTIL.getString(PREFIX + "label.editorTitle.noInput"); //$NON-NLS-1$
        } else {
            IWebServiceXsdResource xsd = (IWebServiceXsdResource)this.editorInput.get(0);
            result = UTIL.getString(PREFIX + "label.editorTitle", new Object[] {xsd.getTargetNamespace()}); //$NON-NLS-1$
        }

        return result;
    }

    void handleApplySelected() {
        boolean folderChanged = this.chkFolder.getSelection();
        boolean modelChanged = this.chkName.getSelection();
        IPath newPath = null;

        if (folderChanged || modelChanged) {
            this.initialFolder = this.currentFolder;
            this.initialName = this.txfName.getText() + ModelerCore.XSD_FILE_EXTENSION;
            newPath = getNewPath(this.initialFolder, this.initialName);

            // save folder and model values if needed
            for (int size = this.editorInput.size(), i = 0; i < size; i++) {
                IWebServiceXsdResource xsd = (IWebServiceXsdResource)this.editorInput.get(i);
                this.builder.setDestinationPath(xsd, newPath);
            }
        }

        this.btnApply.setEnabled(false);

        this.chkFolder.setSelection(false);
        this.chkFolder.setEnabled(false);

        this.chkName.setSelection(false);
        this.chkName.setEnabled(false);

        updateState();

        // persist MRUs
        if (this.settings != null) {
            WidgetUtil.saveSettings(this.settings, TARGET_FOLDER_MRU, this.cbxFolder);
        }

        // determine event type
        int type = -1;

        if (folderChanged && modelChanged) {
            type = SAVED;
        } else if (modelChanged) {
            type = MODEL_ONLY_SAVED;
        } else if (folderChanged) {
            type = FOLDER_ONLY_SAVED;
        }

        fireEvent(type);
    }

    void handleBrowseFolderSelected() {
        Object[] resources = WidgetUtil.showWorkspaceObjectSelectionDialog(UTIL.getString(PREFIX
                                                                                          + "dialog.targetLocationChooser.title"), //$NON-NLS-1$
                                                                           UTIL.getString(PREFIX
                                                                                          + "dialog.targetLocationChooser.msg"), //$NON-NLS-1$
                                                                           false,
                                                                           this.currentFolder,
                                                                           new ModelingResourceFilter(this.targetFilter),
                                                                           null);

        if ((resources != null) && (resources.length > 0)) {
            setFolder(getContainerText((IContainer)resources[0]));
        }
    }

    void handleFolderChanged() {
        this.currentFolder = this.cbxFolder.getText();
        boolean check = (this.currentFolder.length() > 0);

        if (check) {
            check = (this.initialFolder == null) ? true : !this.initialFolder.equals(this.currentFolder);
        }

        this.chkFolder.setSelection(check);
        this.chkFolder.setEnabled(check);

        updateState();
    }

    void handleFolderCheckBoxSelected() {
        if (!this.chkFolder.getSelection()) {
            // set back to initial value
            setFolder(this.initialFolder);
        }

        updateState();
    }

    void handleNameChanged() {
        boolean check = false;

        if (this.initialName == null) {
            check = (this.txfName.getText().length() > 0);
        } else {
            check = !this.initialName.equals(this.txfName.getText());
        }

        this.chkName.setSelection(check);
        this.chkName.setEnabled(check);

        updateState();
    }

    void handleNameCheckBoxSelected() {
        if (!this.chkName.getSelection()) {
            // set back to initial value
            setSchemaName(this.initialName);
        }

        updateState();
    }

    /**
     * @return @since 4.1
     */
    public boolean isDirty() {
        return this.dirty;
    }

    /**
     * check whether the editor can be saved - is valid...
     */
    public boolean canSave() {
        return this.isValid;
    }

    /**
     * @param theListener
     * @since 4.1
     */
    public void removeListener( Listener theListener ) {
        this.listeners.remove(theListener);
    }

    public void save() {
        if (isDirty()) {
            handleApplySelected();
        }
    }

    /**
     * @param theSettings
     * @since 4.1
     */
    public void setDialogSettings( IDialogSettings theSettings ) {
        this.settings = theSettings;

        // target folder MRU
        String[] temp = this.settings.getArray(TARGET_FOLDER_MRU);
        setFolderMru((temp == null) ? new String[0] : temp);
    }

    /**
     * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
     */
    @Override
    public void setEnabled( boolean theEnableFlag ) {
        super.setEnabled(theEnableFlag);

        this.btnFolder.setEnabled(theEnableFlag);
        this.btnApply.setEnabled(theEnableFlag);

        this.cbxFolder.setEnabled(theEnableFlag);
        this.txfName.setEnabled(theEnableFlag);

        this.chkFolder.setEnabled(theEnableFlag);
        this.chkName.setEnabled(theEnableFlag);
    }

    /**
     * @param thePath
     * @since 4.1
     */
    private void setFolder( String thePath ) {
        if (thePath != null) {
            int index = this.cbxFolder.indexOf(thePath);

            if (index == -1) {
                this.cbxFolder.add(thePath);
                index = this.cbxFolder.indexOf(thePath);
            }

            this.cbxFolder.select(index);
        }
    }

    /**
     * @param theFolders
     * @since 4.1
     */
    private void setFolderMru( String[] theFolders ) {
        List folders = null;

        if ((theFolders == null) || (theFolders.length == 0)) {
            folders = new ArrayList(0);
        } else {
            folders = new ArrayList(theFolders.length);

            for (int i = 0; i < theFolders.length; i++) {
                // only add if folder exists
                if (ResourcesPlugin.getWorkspace().getRoot().findMember(theFolders[i]) != null) {
                    folders.add(theFolders[i]);
                }
            }
        }

        WidgetUtil.setComboItems(this.cbxFolder, folders, null, true);
    }

    /**
     * @param theSchemas
     * @since 4.1
     */
    public void setInput( List theSchemas ) {
        // this method is written to handle multiple schemas passed in.
        this.editorInput = theSchemas;

        this.initialFolder = null;
        this.initialName = ""; //$NON-NLS-1$

        // indicates a need to check the folder info
        boolean checkFolder = true;

        // indicates a need to check the name
        boolean checkName = true;

        int numSchemas = 0;

        // set editor fields
        if ((theSchemas == null) || theSchemas.isEmpty()) {
            setEnabled(false);
        } else {
            setEnabled(true);
            numSchemas = theSchemas.size();

            for (int i = 0; i < numSchemas; i++) {
                String tempFolder = null;
                String tempName = null;
                IWebServiceXsdResource xsd = (IWebServiceXsdResource)theSchemas.get(i);
                IPath path = xsd.getDestinationPath();

                if (checkFolder) {
                    tempFolder = path.removeLastSegments(1).toOSString();
                }

                if (checkName) {
                    tempName = path.lastSegment();

                    // remove file extension. that will automatically be added later
                    int index = tempName.indexOf(ModelerCore.XSD_FILE_EXTENSION);

                    if (index != -1) {
                        tempName = tempName.substring(0, index);
                    }
                }

                // set initial input values
                if (i == 0) {
                    this.initialFolder = tempFolder;
                    this.initialName = tempName;
                }

                // if found 2 folders that are different don't check other folders
                if (this.initialFolder == null) {
                    checkFolder = (tempFolder == null);
                } else {
                    checkFolder = (tempFolder == null) ? false : this.initialFolder.equals(tempFolder);
                }

                // if not checking folder clear the initial folder
                if (!checkFolder) {
                    this.initialFolder = null;
                }

                // if found 2 names that are different don't check other names
                if (!this.initialName.equals(tempName)) {
                    this.initialName = ""; //$NON-NLS-1$
                    checkName = false;
                }

                // break if no need to check anything
                if (!checkFolder && !checkName) {
                    break;
                }
            }
        }

        // set editor UI
        this.lblEditorTitle.setText(getTitle());
        setFolder(this.initialFolder);
        setSchemaName(this.initialName);

        // setup initial state
        this.btnApply.setEnabled(false);

        this.chkFolder.setEnabled(false);
        this.chkFolder.setSelection(false);

        this.chkName.setEnabled(false);
        this.chkName.setSelection(false);

        updateState();
    }

    private void setSchemaName( String theName ) {
        this.txfName.setText(theName);
    }

    private void setTitleDirty( boolean theDirtyFlag ) {
        String currentTitle = this.lblEditorTitle.getText();
        boolean update = false;

        if (theDirtyFlag && !currentTitle.startsWith(DIRTY_INDICATOR)) {
            this.lblEditorTitle.setText(DIRTY_INDICATOR + currentTitle);
            update = true;
        } else if (!theDirtyFlag && currentTitle.startsWith(DIRTY_INDICATOR)) {
            this.lblEditorTitle.setText(currentTitle.substring(1));
            update = true;
        }

        if (update) {
            this.lblEditorTitle.update();
        }
    }

    /**
     * Update apply button state, title dirty marker, and status label
     * 
     * @since 4.1
     */
    private void updateState() {
        this.dirty = this.chkFolder.getSelection() || this.chkName.getSelection();
        setTitleDirty(this.dirty);

        // now update status label
        this.isValid = true;
        Image image = null;
        String text = ""; //$NON-NLS-1$

        if ((this.editorInput != null) && !this.editorInput.isEmpty() && isDirty()) {
            if (this.editorInput.size() == 1) {
                // Validate the entered name first
                String enteredName = this.txfName.getText();
                boolean nameValid = false;
                if (enteredName != null && enteredName.length() > 0) {
                    nameValid = ModelUtilities.validateModelName(enteredName, ModelerCore.XSD_FILE_EXTENSION) == null;
                }

                if (!nameValid) {
                    text = UTIL.getString(PREFIX + "editor.invalidName.msg"); //$NON-NLS-1$
                    image = WebServiceUiUtil.getSharedImage(ISharedImages.IMG_OBJS_ERROR_TSK);
                    this.isValid = false;
                } else {
                    IWebServiceXsdResource xsd = (IWebServiceXsdResource)this.editorInput.get(0);
                    IStatus status = xsd.isValid(getNewPath(this.currentFolder, this.txfName.getText()
                                                                                + ModelerCore.XSD_FILE_EXTENSION));
                    image = WebServiceUiUtil.getStatusImage(status);
                    text = status.getMessage();
                    this.isValid = (status.getSeverity() != IStatus.ERROR);
                }
            }
        }

        this.lblStatusMsg.setImage(image);
        this.lblStatusMsg.setText(text);

        // set apply button state
        this.btnApply.setEnabled(this.dirty && this.isValid);
    }

    /** Filter for showing just model projects and folders. */
    class TargetFilter extends ViewerFilter {
        /**
         * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 4.1
         */
        @Override
        public boolean select( Viewer theViewer,
                               Object theParent,
                               Object theElement ) {
            boolean result = false;

            if (theElement instanceof IContainer) {
                IProject project = ((IContainer)theElement).getProject();

                // check for closed project
                if (project.isOpen()) {
                    try {
                        if (project.getNature(ModelerCore.NATURE_ID) != null) {
                            result = true;
                        }
                    } catch (CoreException theException) {
                        UTIL.log(theException);
                    }
                }
            }

            return result;
        }
    }
}
