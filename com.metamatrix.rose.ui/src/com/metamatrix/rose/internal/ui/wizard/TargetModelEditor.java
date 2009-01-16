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
package com.metamatrix.rose.internal.ui.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.rose.internal.IUnit;
import com.metamatrix.rose.internal.RoseImporter;
import com.metamatrix.rose.internal.ui.IRoseUiConstants;
import com.metamatrix.rose.internal.ui.RoseUiPlugin;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.GridPanel;

/**
 * TargetModelEditor
 */
public class TargetModelEditor extends GridPanel implements IRoseUiConstants, IRoseUiConstants.Images {

    /** Title prefix indicating editor is dirty. */
    private static final String DIRTY_INDICATOR = "*"; //$NON-NLS-1$

    /** Properties key prefix. */
    static final String PREFIX = I18nUtil.getPropertyPrefix(TargetModelEditor.class);

    /** Event type indicating only the folder was saved. */
    public static final int FOLDER_ONLY_SAVED = 0;

    /** Event type indicating only the model name was saved. */
    public static final int MODEL_ONLY_SAVED = 1;

    /** Event type indicating both the folder and model name was saved. */
    public static final int FOLDER_AND_MODEL_SAVED = 2;

    /** Event type indicating the editor has closed. */
    public static final int CLOSED = 3;

    /** Event type indicating the editor has a message to be displayed in the page header. */
    public static final int MESSAGE = 4;

    /** Key for target folder MRU list. */
    private static final String TARGET_FOLDER_MRU = "targetFolderList"; //$NON-NLS-1$

    /** Key for target model name MRU list. */
    private static final String TARGET_MODEL_MRU = "targetModelList"; //$NON-NLS-1$

    private static final String INVALID_MODEL_NAME_MSG = UTIL.getString(PREFIX + "invalidModelName"); //$NON-NLS-1$

    private String currentFolder;

    private boolean dirty;

    /** Collection of Rose units being edited. */
    private List editorInput;

    private RoseImporter importer;

    private String initialFolder;

    private String initialModelName;

    private ListenerList listeners;

    private ModelSelectionValidator selectionValidator;

    private IDialogSettings settings;

    private TargetFilter targetFilter;

    private StringNameValidator nameValidator;

    private String msg;

    private Button btnApply;

    private Button btnFolder;

    private Button btnModel;

    private Combo cbxFolder;

    private Combo cbxModel;

    private Button chkFolder;

    private Button chkModel;

    private CLabel lblEditorTitle;

    private ViewForm viewForm;

    /**
     * @param theParent
     * @param theImporter
     * @since 4.1
     */
    public TargetModelEditor( Composite theParent,
                              RoseImporter theImporter ) {
        super(theParent);

        this.importer = theImporter;
        this.listeners = new ListenerList(ListenerList.IDENTITY);
        this.selectionValidator = new ModelSelectionValidator();
        this.targetFilter = new TargetFilter();
        this.nameValidator = new StringNameValidator();

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
                                                        getTitle(0),
                                                        RoseUiPlugin.getDefault().getImage(TARGET_MODEL_EDITOR),
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
        WidgetFactory.createLabel(pnl, UTIL.getString(PREFIX + "label.folder")); //$NON-NLS-1$

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
        this.chkModel = WidgetFactory.createCheckBox(pnl);
        this.chkModel.setEnabled(false);
        this.chkModel.setToolTipText(UTIL.getString(PREFIX + "checkBox.umlModel.tip")); //$NON-NLS-1$
        this.chkModel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleModelCheckBoxSelected();
            }
        });

        // folder label
        WidgetFactory.createLabel(pnl, UTIL.getString(PREFIX + "label.umlModel")); //$NON-NLS-1$

        // model combo
        this.cbxModel = WidgetFactory.createCombo(pnl, SWT.NONE, GridData.FILL_HORIZONTAL);
        this.cbxModel.setEnabled(false);
        this.cbxModel.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleModelChanged();
            }
        });

        // model browse button
        this.btnModel = WidgetFactory.createButton(pnl, UTIL.getString(PREFIX + "button.browse")); //$NON-NLS-1$
        this.btnModel.setEnabled(false);

        this.btnModel.setToolTipText(UTIL.getString(PREFIX + "button.browse.umlModel.tip")); //$NON-NLS-1$
        this.btnModel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleBrowseModelSelected();
            }
        });

        //
        // ROW 3
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
    }

    private void createActions( IToolBarManager theToolBarMgr ) {
        Action action = new Action() {
            @Override
            public void run() {
                setVisible(false);
                fireEvent(CLOSED);
            }
        };

        action.setImageDescriptor(RoseUiPlugin.getDefault().getImageDescriptor(CLOSE_EDITOR));
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

    private IContainer getContainer( String thePath ) {
        IContainer result = null;
        IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(thePath);

        if ((resource != null) && (resource instanceof IContainer)) {
            result = (IContainer)resource;
        }

        return result;
    }

    private String getContainerText( IContainer theContainer ) {
        return (theContainer == null) ? "" //$NON-NLS-1$
        : theContainer.getFullPath().makeRelative().toString();
    }

    public List getInput() {
        return this.editorInput;
    }

    String getMessage() {
        return this.msg;
    }

    private String getTitle( int theUnitNumber ) {
        String result = null;

        if (theUnitNumber == 1) {
            result = UTIL.getString(PREFIX + "label.editorTitle.single"); //$NON-NLS-1$
        } else {
            result = UTIL.getString(PREFIX + "label.editorTitle.multiple", //$NON-NLS-1$
                                    new Object[] {new Integer(theUnitNumber)});
        }

        return result;
    }

    void handleApplySelected() {
        boolean folderChanged = this.chkFolder.getSelection();
        boolean modelChanged = this.chkModel.getSelection();

        // save folder and model values if needed
        for (int size = this.editorInput.size(), i = 0; i < size; i++) {
            IUnit unit = (IUnit)this.editorInput.get(i);

            if (folderChanged) {
                this.importer.setUnitModelFolder(unit, getContainer(this.currentFolder));
                this.initialFolder = this.currentFolder;
            }

            if (modelChanged) {
                this.initialModelName = this.cbxModel.getText();
                this.importer.setUnitModelName(unit, this.initialModelName);
            }
        }

        this.btnApply.setEnabled(false);

        this.chkFolder.setSelection(false);
        this.chkFolder.setEnabled(false);

        this.chkModel.setSelection(false);
        this.chkModel.setEnabled(false);

        updateState();

        // persist MRUs
        if (this.settings != null) {
            WidgetUtil.saveSettings(this.settings, TARGET_FOLDER_MRU, this.cbxFolder);
            WidgetUtil.saveSettings(this.settings, TARGET_MODEL_MRU, this.cbxModel);
        }

        // determine event type
        int type = -1;

        if (folderChanged && modelChanged) {
            type = FOLDER_AND_MODEL_SAVED;
        } else if (modelChanged) {
            type = MODEL_ONLY_SAVED;
        } else if (folderChanged) {
            type = FOLDER_ONLY_SAVED;
        }

        fireEvent(type);
    }

    void handleBrowseFolderSelected() {
        this.targetFilter.showModels = false;
        Object[] resources = WidgetUtil.showWorkspaceObjectSelectionDialog(UTIL.getString(PREFIX
                                                                                          + "dialog.targetLocationChooser.title"), //$NON-NLS-1$
                                                                           UTIL.getString(PREFIX
                                                                                          + "dialog.targetLocationChooser.msg"), //$NON-NLS-1$
                                                                           false,
                                                                           this.currentFolder,
                                                                           this.targetFilter,
                                                                           null);

        if ((resources != null) && (resources.length > 0)) {
            setFolder(getContainerText((IContainer)resources[0]));
        }
    }

    void handleBrowseModelSelected() {
        this.targetFilter.showModels = true;
        Object[] resources = WidgetUtil.showWorkspaceObjectSelectionDialog(UTIL.getString(PREFIX
                                                                                          + "dialog.targetModelChooser.title"), //$NON-NLS-1$
                                                                           UTIL.getString(PREFIX
                                                                                          + "dialog.targetModelChooser.msg"), //$NON-NLS-1$
                                                                           false,
                                                                           null,
                                                                           this.targetFilter,
                                                                           this.selectionValidator);

        if ((resources != null) && (resources.length > 0)) {
            IFile model = (IFile)resources[0];
            this.currentFolder = getContainerText(model.getParent());
            setFolder(this.currentFolder);
            setModelName(model.getName());
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

    void handleModelChanged() {
        boolean check = false;

        if (this.initialModelName == null) {
            check = (this.cbxModel.getText().length() > 0);
        } else {
            check = !this.initialModelName.equals(this.cbxModel.getText());
        }

        this.chkModel.setSelection(check);
        this.chkModel.setEnabled(check);

        updateState();
    }

    void handleModelCheckBoxSelected() {
        if (!this.chkModel.getSelection()) {
            // set back to initial value
            setModelName(this.initialModelName);
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

        // target model MRU
        temp = this.settings.getArray(TARGET_MODEL_MRU);
        setModelMru((temp == null) ? new String[0] : temp);
    }

    /**
     * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
     */
    @Override
    public void setEnabled( boolean theEnableFlag ) {
        super.setEnabled(theEnableFlag);

        this.btnFolder.setEnabled(theEnableFlag);
        this.btnModel.setEnabled(theEnableFlag);
        this.btnApply.setEnabled(theEnableFlag);

        this.cbxFolder.setEnabled(theEnableFlag);
        this.cbxModel.setEnabled(theEnableFlag);

        this.chkFolder.setEnabled(theEnableFlag);
        this.chkModel.setEnabled(theEnableFlag);
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
                if (ResourcesPlugin.getWorkspace().getRoot().findMember(theFolders[i]) != null) {
                    folders.add(theFolders[i]);
                }
            }
        }

        WidgetUtil.setComboItems(this.cbxFolder, folders, null, true);
    }

    /**
     * @param theModels
     * @since 4.1
     */
    private void setModelMru( String[] theModels ) {
        String[] items = theModels;

        if (items == null) {
            items = new String[0];
        }

        WidgetUtil.setComboItems(this.cbxModel, Arrays.asList(items), null, true);
    }

    /**
     * @param theRoseUnits
     * @since 4.1
     */
    public void setInput( List theRoseUnits ) {
        this.editorInput = theRoseUnits;

        this.initialFolder = null;
        this.initialModelName = ""; //$NON-NLS-1$

        boolean checkFolder = true;
        boolean checkModel = true;

        int numUnits = 0;

        // set editor fields
        if ((theRoseUnits == null) || theRoseUnits.isEmpty()) {
            setEnabled(false);
        } else {
            setEnabled(true);
            numUnits = theRoseUnits.size();

            for (int i = 0; i < numUnits; i++) {
                String tempFolder = null;
                String tempModel = null;
                IUnit unit = (IUnit)theRoseUnits.get(i);

                if (checkFolder) {
                    tempFolder = getContainerText(unit.getModelFolder());
                }

                if (checkModel) {
                    tempModel = (unit.getModelName() == null) ? "" //$NON-NLS-1$
                    : unit.getModelName();
                }

                // set initial values
                if (i == 0) {
                    this.initialFolder = tempFolder;
                    this.initialModelName = tempModel;
                }

                if (this.initialFolder == null) {
                    checkFolder = (tempFolder == null);
                } else {
                    checkFolder = (tempFolder == null) ? false : this.initialFolder.equals(tempFolder);
                }

                if (!checkFolder) {
                    this.initialFolder = null;
                }

                if (!this.initialModelName.equals(tempModel)) {
                    this.initialModelName = ""; //$NON-NLS-1$
                    checkModel = false;
                }

                if (!checkFolder && !checkModel) {
                    break;
                }
            }
        }

        // set editor UI
        this.lblEditorTitle.setText(getTitle(numUnits));
        setFolder(this.initialFolder);
        setModelName(this.initialModelName);

        // setup initial state
        this.btnApply.setEnabled(false);

        this.chkFolder.setEnabled(false);
        this.chkFolder.setSelection(false);

        this.chkModel.setEnabled(false);
        this.chkModel.setSelection(false);

        updateState();
    }

    private void setModelName( String theName ) {
        int index = this.cbxModel.indexOf(theName);

        if (index == -1) {
            this.cbxModel.setText(theName);
        } else {
            this.cbxModel.select(index);
        }
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
     * Update apply button state and title.
     */
    private void updateState() {
        final boolean modelNameChanged = this.chkModel.getSelection();
        this.dirty = this.chkFolder.getSelection() || modelNameChanged;
        this.msg = modelNameChanged ? this.nameValidator.checkValidName(this.cbxModel.getText()) : null;
        if (this.msg != null) {
            this.msg = INVALID_MODEL_NAME_MSG + ' ' + this.msg;
            this.btnApply.setEnabled(false);
        } else {
            this.btnApply.setEnabled(this.dirty);
        }
        setTitleDirty(this.dirty);
        fireEvent(MESSAGE);
    }

    /** Filter for showing just model projects, folders, and models. */
    class TargetFilter extends ViewerFilter {

        /**
         * @since 4.1
         */
        public boolean showModels = false;

        private ViewerFilter resourceFilter = UiUtil.getResourceFilter("com.metamatrix.modeler.ui.explorer.view"); //$NON-NLS-1$

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
                            result = resourceFilter.select(theViewer, theParent, theElement);
                        }
                    } catch (CoreException theException) {
                        UTIL.log(theException);
                    }
                }
            } else if (theElement instanceof IFile) {
                // make sure model file
                result = this.showModels && ModelUtil.isModelFile((IFile)theElement);
            }

            return result;
        }
    }

    class ModelSelectionValidator implements ISelectionStatusValidator {

        /**
         * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
         */
        public IStatus validate( Object[] theSelection ) {
            IStatus result = null;

            // only looks at first selection since this is being used in a single select chooser
            if ((theSelection != null) && (theSelection.length > 0) && (theSelection[0] instanceof IFile)
                && ModelUtil.isModelFile((IFile)theSelection[0])) {
                result = new StatusInfo(PLUGIN_ID);
            } else {
                result = new StatusInfo(PLUGIN_ID, IStatus.ERROR, UTIL.getString(PREFIX + "msg.selectionIsNotModel")); //$NON-NLS-1$
            }

            return result;
        }
    }
}
