/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship.ui.search;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.search.SearchPageUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.RelationshipPlugin;
import com.metamatrix.modeler.relationship.RelationshipSearch;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.properties.RelationshipPropertyEditorFactory;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * The <code>RelationshipSearchPage</code> is the UI to perform workspace relationship searching.
 */
public final class RelationshipSearchPage extends DialogPage implements ISearchPage {

    // ===========================================================================================================================
    // Interfaces
    // ===========================================================================================================================

    /**
     * Constants used in the {@link IDialogSettings}.
     * 
     * @since 4.2
     */
    interface DialogSettingsConstants {
        String ANY_TYPE = "anyType"; //$NON-NLS-1$
        String NAMED_TYPE = "namedType"; //$NON-NLS-1$
        String INCLUDE_SUBTYPES = "includeSubTypes"; //$NON-NLS-1$
        String ANY_PARTICIPANT = "anyParticipant"; //$NON-NLS-1$
        String SELECTED_PARTICIPANTS = "selectedParticipants"; //$NON-NLS-1$
        String NAME_PATTERN = "namePattern"; //$NON-NLS-1$
        String LAST_USED_NAME_PATTERN = "lastUsedNamePattern"; //$NON-NLS-1$
        int NAME_PATTERN_LIMIT = 20;
        String CASE_SENSITIVE = "caseSensitive"; //$NON-NLS-1$
        String LAST_NAMED_TYPE = "lastNamedType"; //$NON-NLS-1$
    }

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(RelationshipSearchPage.class);

    static final PluginUtil UTIL = UiConstants.Util;

    // ===========================================================================================================================
    // Class Methods
    // ===========================================================================================================================

    private static RelationshipType getRelationshipType( URI u ) throws CoreException {
        RelationshipType rt = (RelationshipType)ModelerCore.getModelContainer().getEObject(u, true);
        if (!RelationshipMetamodelPlugin.getBuiltInRelationshipTypeManager().isBuiltInRelationshipType(rt)) {
            // not built-in, need to determine if this URI exists in an open project:
            IWorkspace ws = ResourcesPlugin.getWorkspace();
            IFile f = ws.getRoot().getFile(new Path(u.path()));
            if (!f.exists() || !f.isAccessible()) {
                // no, so we don't want to load this type:
                rt = null;
            } // endif -- file does not exist
        } // endif -- not a built-in type

        return rt;
    }

    private static String getEObjectURI( EObject eobj ) {
        String uri;
        ModelResource mdlRsrc = ModelUtilities.getModelResourceForModelObject(eobj);
        if (mdlRsrc != null) {
            // this is a custom type:
            IResource resource = mdlRsrc.getResource();
            uri = resource.getFullPath().toString() + '#' + ModelerCore.getObjectIdString(eobj);
        } else {
            // this is a built-in type:
            URI u = eobj.eResource().getURI().appendFragment(ModelerCore.getObjectIdString(eobj));
            uri = u.toString();
        } // endif -- is custom type

        return uri;
    }

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private ISearchPageContainer searchPageContainer;

    /** Persisted dialog settings. */
    private IDialogSettings settings;

    /** Filter for showing just projects and folders. */
    private ViewerFilter filter = new ViewerFilter() {
        @Override
        public boolean select( Viewer theViewer,
                               Object theParent,
                               Object theElement ) {
            boolean result = false;

            if (theElement instanceof IResource) {
                if (theElement instanceof IContainer) {
                    IProject project = ((IContainer)theElement).getProject();

                    // only open projects
                    if (project.isOpen()) {
                        try {
                            if (project.getNature(PluginConstants.MODEL_PROJECT_NATURE_ID) != null) {
                                result = true;
                            }
                        } catch (CoreException theException) {
                            UTIL.log(theException);
                        }
                    }
                } else if ((theElement instanceof IFile) && ModelUtilities.isModelFile((IResource)theElement)) {
                    result = true;
                }
            }

            return result;
        }
    };

    /** The named type to use in the search. */
    private RelationshipType namedType;

    /** Search business object that executes search. */
    private RelationshipSearch searchMgr;

    /** Collection of selected participant resources. */
    private List selectedParticipantResources;

    // ===========================================================================================================================
    // Controls
    // ===========================================================================================================================

    /** Button to indicate that all relationship types should be used in search. */
    private Button btnAnyType;

    /** Button indicating participants can be found in any resource. */
    private Button btnAnyParticipant;

    /** Button to browse and select resources. */
    private Button btnBrowseResources;

    /** Button to browse and select relationship type. */
    private Button btnBrowseType;

    /** Button to indicate that a named relationalship type should be used in search. */
    private Button btnNamedType;

    /** Button indicating participants can be found in only the selected resources. */
    private Button btnSelectedParticipants;

    /** MRU list of relationship name patterns. */
    private Combo cbxName;

    /** Indicates if the name pattern should be considered case sensitive. */
    private Button chkCaseSensitive;

    /** Indicates if the subtypes of the named type should be included in search. */
    private Button chkIncludeSubtypes;

    /** Indicates the number of selected participant resources. */
    private CLabel lblParticipantCount;

    /** Editor for relationship name pattern. */
    private Text txfNamedType;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    public RelationshipSearchPage() {
        this.searchMgr = RelationshipPlugin.createRelationshipSearch();
        this.selectedParticipantResources = Collections.EMPTY_LIST;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl( Composite parent ) {
        initializeDialogSettings();
        setControl(createControlImpl(parent));
        restoreState();
    }

    private Composite createControlImpl( Composite theParent ) {
        //
        // Create main container
        //

        final int MAIN_COLS = 1;
        final Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        pnlMain.setLayout(new GridLayout(MAIN_COLS, false));

        //
        // pnlMain contents
        //

        //
        // ROW 1 - type group
        //

        final int TYPE_COLS = 3;
        Group pnl = WidgetFactory.createGroup(pnlMain, UTIL.getString(PREFIX + "group.type"), //$NON-NLS-1$
                                              GridData.FILL_BOTH,
                                              MAIN_COLS,
                                              TYPE_COLS);
        pnl.setFont(JFaceResources.getDefaultFont()); // undo bold font WidgetFactory assigned
        createTypePanelContents(pnl, TYPE_COLS);

        //
        // ROW 2 - participant objects group
        //

        final int PARTICIPANT_COLS = 3;
        pnl = WidgetFactory.createGroup(pnlMain, UTIL.getString(PREFIX + "group.participants"), //$NON-NLS-1$
                                        GridData.FILL_BOTH,
                                        MAIN_COLS,
                                        PARTICIPANT_COLS);
        pnl.setFont(JFaceResources.getDefaultFont()); // undo bold font WidgetFactory assigned
        createParticipantPanelContents(pnl, PARTICIPANT_COLS);

        //
        // ROW 3 - name group
        //

        final int NAME_COLS = 2;
        pnl = WidgetFactory.createGroup(pnlMain, UTIL.getString(PREFIX + "group.name"), //$NON-NLS-1$
                                        GridData.FILL_BOTH,
                                        MAIN_COLS,
                                        NAME_COLS);
        pnl.setFont(JFaceResources.getDefaultFont()); // undo bold font WidgetFactory assigned
        createNamePanelContents(pnl, NAME_COLS);

        //
        // ROW 4 Scope is added by SearchPage framework
        //
//        pnlMain.addControlListener(new ControlAdapter() {
//
//            @Override
//            public void controlResized( ControlEvent e ) {
//                mainPanelResized(pnlMain, this);
//            }
//        });

        return pnlMain;
    }
//
//    void mainPanelResized( Composite panel,
//                           ControlListener listener ) {
//        setModelWorkspaceScope(SearchPageUtil.getModelWorkspaceScope(container));
//        updateSearchState();
//        panel.removeControlListener(listener);
//    }

    /**
     * Constructs the panel containing the controls for managing the UI for the relationship name.
     * 
     * @param theParent the panel's parent container
     * @param theColumnCount the parent's column count
     */
    private void createNamePanelContents( Composite theParent,
                                          int theColumnCount ) {
        // label for name combo
        CLabel lbl = WidgetFactory.createLabel(theParent, UTIL.getString(PREFIX + "label.name")); //$NON-NLS-1$
        ((GridData)lbl.getLayoutData()).horizontalSpan = theColumnCount;

        // MRU for name
        this.cbxName = WidgetFactory.createCombo(theParent, SWT.NONE, GridData.FILL_HORIZONTAL);
        this.cbxName.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleRelationshipNameModified();
            }
        });

        // browse button to choose import folder
        this.chkCaseSensitive = WidgetFactory.createCheckBox(theParent, UTIL.getString(PREFIX + "checkBox.caseSensitive"), //$NON-NLS-1$
                                                             GridData.HORIZONTAL_ALIGN_END);
        this.chkCaseSensitive.setToolTipText(UTIL.getString(PREFIX + "checkBox.caseSensitive.tip")); //$NON-NLS-1$
        this.chkCaseSensitive.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleCaseSensitiveChanged();
            }
        });

        // label for name wildcard characters
        lbl = WidgetFactory.createLabel(theParent, UTIL.getString(PREFIX + "label.nameWildcards")); //$NON-NLS-1$
        ((GridData)lbl.getLayoutData()).horizontalSpan = theColumnCount;

    }

    /**
     * Constructs the panel containing the controls for managing the UI for the relationship participants.
     * 
     * @param theParent the panel's parent container
     * @param theColumnCount the parent's column count
     */
    private void createParticipantPanelContents( Composite theParent,
                                                 int theColumnCount ) {
        //
        // ROW 1
        //

        // any resource radio button (set to default)
        btnAnyParticipant = WidgetFactory.createRadioButton(theParent, UTIL.getString(PREFIX + "radioButton.anyResource"), //$NON-NLS-1$
                                                            0,
                                                            theColumnCount,
                                                            false);
        btnAnyParticipant.setToolTipText(UTIL.getString(PREFIX + "radioButton.anyResource.tip")); //$NON-NLS-1$
        btnAnyParticipant.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleAnyResourceSelected(theEvent);
            }
        });

        //
        // ROW 2
        //

        // select resource radio button
        btnSelectedParticipants = WidgetFactory.createRadioButton(theParent, UTIL.getString(PREFIX
                                                                                            + "radioButton.selectedResources")); //$NON-NLS-1$
        btnSelectedParticipants.setToolTipText(UTIL.getString(PREFIX + "radioButton.selectedResources.tip")); //$NON-NLS-1$
        btnSelectedParticipants.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleParticipantResourcesSelected(theEvent);
            }
        });

        // browse participant objects button
        this.btnBrowseResources = WidgetFactory.createButton(theParent, UTIL.getString(PREFIX + "button.browseResources")); //$NON-NLS-1$
        this.btnBrowseResources.setToolTipText(UTIL.getString(PREFIX + "button.browseResources.tip")); //$NON-NLS-1$
        this.btnBrowseResources.setEnabled(false);
        this.btnBrowseResources.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleBrowseParticipantObjects();
            }
        });

        // label indicating the number of participant resources
        this.lblParticipantCount = WidgetFactory.createLabel(theParent);
        updateParticipantCount();
    }

    /**
     * Constructs the panel containing the controls for managing the UI for the relationship type.
     * 
     * @param theParent the panel's parent container
     * @param theColumnCount the parent's column count
     */
    private void createTypePanelContents( Composite theParent,
                                          int theColumnCount ) {
        //
        // ROW 1
        //

        // any type radio button (set to default)
        this.btnAnyType = WidgetFactory.createRadioButton(theParent, UTIL.getString(PREFIX + "radioButton.anyType"), //$NON-NLS-1$
                                                          0,
                                                          theColumnCount,
                                                          false);
        this.btnAnyType.setToolTipText(UTIL.getString(PREFIX + "radioButton.anyType.tip")); //$NON-NLS-1$
        this.btnAnyType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleAnyTypeSelected(theEvent);
            }
        });

        //
        // ROW 2
        //

        // type name radio button
        this.btnNamedType = WidgetFactory.createRadioButton(theParent, UTIL.getString(PREFIX + "radioButton.nameType")); //$NON-NLS-1$
        this.btnNamedType.setToolTipText(UTIL.getString(PREFIX + "radioButton.nameType.tip")); //$NON-NLS-1$
        this.btnNamedType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleNamedTypeSelected(theEvent);
            }
        });

        // textfield for named type
        this.txfNamedType = WidgetFactory.createTextField(theParent, GridData.FILL_HORIZONTAL/*GridData.HORIZONTAL_ALIGN_FILL*/);
        this.txfNamedType.setToolTipText(UTIL.getString(PREFIX + "text.typeName.tip")); //$NON-NLS-1$
        this.txfNamedType.setEditable(false);
        this.txfNamedType.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleNamedTypeChange();
            }
        });

        // browse type button
        this.btnBrowseType = WidgetFactory.createButton(theParent, UTIL.getString(PREFIX + "button.browseType")); //$NON-NLS-1$
        this.btnBrowseType.setToolTipText(UTIL.getString(PREFIX + "button.browseType.tip")); //$NON-NLS-1$
        this.btnBrowseType.setEnabled(false);
        this.btnBrowseType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleBrowseType();
            }
        });

        //
        // ROW 3
        //

        // include subtype checkbox
        this.chkIncludeSubtypes = WidgetFactory.createCheckBox(theParent, UTIL.getString(PREFIX + "checkBox.includeSubtypes"), //$NON-NLS-1$
                                                               SWT.LEFT,
                                                               theColumnCount);
        ((GridData)this.chkIncludeSubtypes.getLayoutData()).horizontalIndent = 20;
        this.chkIncludeSubtypes.setToolTipText(UTIL.getString(PREFIX + "checkBox.includeSubtypes.tip")); //$NON-NLS-1$
        this.chkIncludeSubtypes.setEnabled(false);
    }

    private ISearchPageContainer getContainer() {
        return this.searchPageContainer;
    }

    /**
     * Handler for when the any type button is selected/deselected.
     * 
     * @param theEvent the event being processed
     */
    void handleAnyTypeSelected( SelectionEvent theEvent ) {
        boolean selected = ((Button)theEvent.widget).getSelection();

        this.btnBrowseType.setEnabled(!selected);
        this.txfNamedType.setEnabled(!selected);
        this.chkIncludeSubtypes.setEnabled(!selected);

        if (selected) {
            searchMgr.setRelationshipTypeCriteria(RelationshipSearch.ANY_RELATIONSHIP_TYPE,
                                                  this.chkIncludeSubtypes.getSelection());
            updateSearchState();
        }
    }

    /**
     * Handler for when the any resource button is selected/deselected.
     * 
     * @param theEvent the event being processed
     */
    void handleAnyResourceSelected( SelectionEvent theEvent ) {
        boolean selected = ((Button)theEvent.widget).getSelection();

        // enable/disable browse button
        this.btnBrowseResources.setEnabled(!selected);
        this.lblParticipantCount.setEnabled(!selected);

        if (selected) {
            // set to null to signify that participants found anywhere in workspace can be used
            searchMgr.setParticipantsCriteria(null);
            updateSearchState();
        }
    }

    /**
     * Handler for when the browse for relationship participants button is selected.
     */
    void handleBrowseParticipantObjects() {
        Object[] resources = WidgetUtil.showWorkspaceObjectSelectionDialog(UTIL.getString(PREFIX
                                                                                          + "dialog.participantChooser.title"), //$NON-NLS-1$
                                                                           UTIL.getString(PREFIX
                                                                                          + "dialog.participantChooser.msg"), //$NON-NLS-1$
                                                                           true,
                                                                           null,
                                                                           new ModelingResourceFilter(this.filter),
                                                                           null); // no need for selection validator because of
        // filter

        List list = new LinkedList();

        for (int i = 0; i < resources.length; i++) {
            list.add(SearchPageUtil.getModelWorkspaceResource(resources[i]));
        }

        this.selectedParticipantResources = list;

        searchMgr.setParticipantsCriteria(this.selectedParticipantResources);

        updateParticipantCount();
        updateSearchState();
    }

    /**
     * Handler for when the browse for relationship type button is selected.
     */
    void handleBrowseType() {
        SelectionDialog dialog = RelationshipPropertyEditorFactory.createRelationshipTypeSelector(getShell(), this.namedType);
        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            Object[] selectedTypes = dialog.getResult();

            // should only get one selection
            this.namedType = (selectedTypes.length > 0) ? (RelationshipType)selectedTypes[0] : null;

            this.txfNamedType.setText(this.namedType.getName());
        }
    }

    /**
     * Handler for when the named relationship type changes. This will be called after the user chooses a type from the type
     * dialog.
     */
    void handleNamedTypeChange() {
        this.searchMgr.setRelationshipTypeCriteria(this.namedType, this.chkIncludeSubtypes.getSelection());
        if (namedType != null) {
            // update the tooltip to the full URI:
            txfNamedType.setToolTipText(getEObjectURI(namedType));
        } else {
            txfNamedType.setToolTipText(UTIL.getString(PREFIX + "text.typeName.tip")); //$NON-NLS-1$
        } // endif
        updateSearchState();
    }

    /**
     * Handler for when the named type button is selected/deselected.
     * 
     * @param theEvent the event being processed
     */
    void handleNamedTypeSelected( SelectionEvent theEvent ) {
        boolean selected = ((Button)theEvent.widget).getSelection();

        this.btnBrowseType.setEnabled(selected);
        this.chkIncludeSubtypes.setEnabled(selected);

        if (selected) {
            searchMgr.setRelationshipTypeCriteria(this.namedType, this.chkIncludeSubtypes.getSelection());
            updateSearchState();
        }
    }

    /**
     * Handler for when the selected participant resources button is selected/deselected.
     * 
     * @param theEvent the event being processed
     */
    void handleParticipantResourcesSelected( SelectionEvent theEvent ) {
        boolean selected = ((Button)theEvent.widget).getSelection();

        this.btnBrowseResources.setEnabled(selected);
        this.lblParticipantCount.setEnabled(selected);

        if (selected && selectedParticipantResources != null && !selectedParticipantResources.isEmpty()) {
            searchMgr.setParticipantsCriteria(this.selectedParticipantResources);
            updateSearchState();
        }
    }

    /**
     * Handler for when the relationship name pattern textfield is modified.
     * 
     * @see #handleBrowseType()
     */
    void handleRelationshipNameModified() {
        searchMgr.setNameCriteria(this.cbxName.getText(), this.chkCaseSensitive.getSelection());
        updateSearchState();
    }

    /**
     * Handler for when the relationship name pattern textfield is modified.
     * 
     * @see #handleBrowseType()
     */
    void handleCaseSensitiveChanged() {
        searchMgr.setNameCriteria(this.cbxName.getText(), this.chkCaseSensitive.getSelection());
        updateSearchState();
    }

    private void initializeDialogSettings() {
        // get general search dialog settings
        IDialogSettings tempSettings = UiPlugin.getDefault().getDialogSettings();

        // get this page's settings
        this.settings = tempSettings.getSection(getClass().getSimpleName());

        // if settings not found create them
        if (this.settings == null) {
            this.settings = tempSettings.addNewSection(getClass().getSimpleName());
        }
    }

    /**
     * Select specified button and fire selection event to it's listeners. Need to do this because events aren't generated at
     * construction.
     * 
     * @param theButton the button being selected at construction
     */
    private void initButtonSelected( final Button theButton ) {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                theButton.setSelection(true);
                Event event = new Event();
                event.widget = theButton;
                theButton.notifyListeners(SWT.Selection, event);
            }
        });
    }

    private boolean isSearchStateValid() {
        return this.searchMgr.canExecute().isOK();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.search.ui.ISearchPage#performAction()
     */
    @Override
    public boolean performAction() {
        saveState();
        NewSearchUI.runQueryInBackground(new RelationshipSearchQuery(this.searchMgr));

        return true;
    }

    private void restoreState() {
        //
        // restore type group
        //

        // any type, named type radio buttons
        if (this.settings.getBoolean(DialogSettingsConstants.ANY_TYPE)) {
            initButtonSelected(this.btnAnyType);
        } else if (this.settings.getBoolean(DialogSettingsConstants.NAMED_TYPE)) {
            initButtonSelected(this.btnNamedType);
        } else {
            // first time using search page
            initButtonSelected(this.btnAnyType);
        }

        // include subtypes checkbox
        if (this.settings.getBoolean(DialogSettingsConstants.INCLUDE_SUBTYPES)) {
            initButtonSelected(this.chkIncludeSubtypes);
        } else if (this.settings.get(DialogSettingsConstants.INCLUDE_SUBTYPES) == null) {
            // first time using search page
            if (RelationshipSearch.DEFAULT_INCLUDE_SUBTYPES) {
                initButtonSelected(this.chkIncludeSubtypes);
            }
        }

        String lastNamedType = this.settings.get(DialogSettingsConstants.LAST_NAMED_TYPE);
        if (lastNamedType != null) {
            // this really isn't enough, need to set the namedType information...
            // need to make sure this named type exists.
            try {
                URI u = URI.createURI(lastNamedType);
                // determine if built-in type:
                RelationshipType rt = getRelationshipType(u);
                if (rt != null) {
                    // set the field:
                    this.namedType = rt;
                    // set the display:
                    this.txfNamedType.setText(rt.getName());
                    // set the search info:
                    handleNamedTypeChange();
                } // endif
            } catch (Exception ex) {
                UTIL.log(ex);
            } // endtry
        } // endif

        //
        // restore participant group
        //

        // any participant, selected participants radio buttons
        if (this.settings.getBoolean(DialogSettingsConstants.ANY_PARTICIPANT)) {
            initButtonSelected(this.btnAnyParticipant);
        } else if (this.settings.getBoolean(DialogSettingsConstants.SELECTED_PARTICIPANTS)) {
            initButtonSelected(this.btnSelectedParticipants);
        } else {
            // first time using search page
            initButtonSelected(this.btnAnyParticipant);
        }

        //
        // restore relationship name group
        //

        // case sensitive checkbox
        // include property in search checkbox
        if (this.settings.getBoolean(DialogSettingsConstants.CASE_SENSITIVE)) {
            initButtonSelected(this.chkCaseSensitive);
        } else if (this.settings.get(DialogSettingsConstants.CASE_SENSITIVE) == null) {
            // first time using search page
            if (RelationshipSearch.DEFAULT_NAME_CASE_SENSITIVE) {
                initButtonSelected(this.chkCaseSensitive);
            }
        }

        // MRU for name patterns
        String[] patterns = this.settings.getArray(DialogSettingsConstants.NAME_PATTERN);

        if (patterns != null) {
            this.cbxName.setItems(patterns);
        }

        // restore last used text pattern:
        String lastName = this.settings.get(DialogSettingsConstants.LAST_USED_NAME_PATTERN);
        if (lastName != null) {
            this.cbxName.setText(lastName);
        } // endif

        // // case sensitive checkbox
        // // include property in search checkbox
        // if (settings.getBoolean(DialogSettingsConstants.CASE_SENSITIVE)) {
        // initButtonSelected(this.chkCaseSensitive);
        // } else if (settings.get(DialogSettingsConstants.CASE_SENSITIVE) == null) {
        // // first time using search page
        // if (RelationshipSearch.DEFAULT_NAME_CASE_SENSITIVE) {
        // initButtonSelected(this.chkCaseSensitive);
        // }
        // }

        this.updateSearchState();
    }

    private void saveState() {
        this.settings.put(DialogSettingsConstants.ANY_TYPE, this.btnAnyType.getSelection());
        this.settings.put(DialogSettingsConstants.NAMED_TYPE, this.btnNamedType.getSelection());
        if (this.namedType != null) {
            // save the object URI:
            this.settings.put(DialogSettingsConstants.LAST_NAMED_TYPE, getEObjectURI(namedType));
        } // endif -- named type specified

        this.settings.put(DialogSettingsConstants.INCLUDE_SUBTYPES, this.chkIncludeSubtypes.getSelection());
        this.settings.put(DialogSettingsConstants.ANY_PARTICIPANT, this.btnAnyParticipant.getSelection());
        this.settings.put(DialogSettingsConstants.SELECTED_PARTICIPANTS, this.btnSelectedParticipants.getSelection());
        this.settings.put(DialogSettingsConstants.CASE_SENSITIVE, this.chkCaseSensitive.getSelection());

        // utility to save combo items
        WidgetUtil.saveSettings(this.settings,
                                DialogSettingsConstants.NAME_PATTERN,
                                this.cbxName,
                                DialogSettingsConstants.NAME_PATTERN_LIMIT);
        // save last used text pattern
        this.settings.put(DialogSettingsConstants.LAST_USED_NAME_PATTERN, this.cbxName.getText());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.search.ui.ISearchPage#setContainer(org.eclipse.search.ui.ISearchPageContainer)
     */
    @Override
    public void setContainer( ISearchPageContainer container ) {
        this.searchPageContainer = container;
        setModelWorkspaceScope(SearchPageUtil.getModelWorkspaceScope(container));
    }

    private void setModelWorkspaceScope( List theScope ) {
        this.searchMgr.setRelationshipModelScope(theScope);
    }

    /**
     * Updates the label showing the count of the currently selected participant resources.
     */
    private void updateParticipantCount() {
        int count = this.selectedParticipantResources.size();
        this.lblParticipantCount.setText(UTIL.getString(PREFIX + "label.numberParticipants", //$NON-NLS-1$
                                                        new Object[] {Integer.toString(count)}));
    }

    private void updateSearchState() {
        getContainer().setPerformActionEnabled(isSearchStateValid());
    }
}
