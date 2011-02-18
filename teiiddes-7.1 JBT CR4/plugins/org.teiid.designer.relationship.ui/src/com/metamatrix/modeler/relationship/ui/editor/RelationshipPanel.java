/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.editor;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.properties.PropertySheetPage;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipContainer;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.relationship.provider.RelationshipAssociationDescriptor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySheetPage;
import com.metamatrix.modeler.internal.ui.search.ModelObjectFinderDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceDialog;
import com.metamatrix.modeler.relationship.RelationshipEditor;
import com.metamatrix.modeler.relationship.RelationshipPlugin;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.properties.RelationshipPropertyEditorFactory;
import com.metamatrix.modeler.relationship.ui.properties.RelationshipRoleSelectionValidator;
import com.metamatrix.ui.graphics.GlobalUiFontManager;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.ClosedProjectFilter;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.table.TableSizeAdapter;

public class RelationshipPanel extends Composite
    implements ISelectionChangedListener, SelectionListener, INotifyChangedListener, UiConstants, PluginConstants {

    public static final int SOURCE = 1;
    public static final int TARGET = 2;

    private static final int[] ROLE_WEIGHTS = {4, 6};

    // Style Constants
    public static final int BUTTON_GRID_STYLE = GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_CENTER;

    private static final String RELATIONSHIP_NAME_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipPanel.name.text"); //$NON-NLS-1$
    private static final String RELATIONSHIP_TYPE_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipPanel.type.text"); //$NON-NLS-1$
    private static final String LOCATION_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipPanel.location.text"); //$NON-NLS-1$
    private static final String BROWSE_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipPanel.browse.text"); //$NON-NLS-1$
    private static final String ROLES_TAB_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipPanel.rolesTab.title"); //$NON-NLS-1$
    private static final String PROPERTIES_TITLE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.properties.title"); //$NON-NLS-1$
    private static final String NEED_LOCATION_ERROR_MSG = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipPanel.needLocation.text"); //$NON-NLS-1$
    private static final String SELECT_TYPE_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipPanel.selectType.text"); //$NON-NLS-1$

    static final String DEFAULT_ROLE_A_NAME = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.defaultRoleAName.text"); //$NON-NLS-1$
    static final String DEFAULT_ROLE_B_NAME = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.defaultRoleBName.text"); //$NON-NLS-1$
    private static/*final*/String SWAP_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.swap.text"); //$NON-NLS-1$

    private static final String UP_BUTTON_TEXT = UiConstants.Util.getString("RelationshipPanel.leftButton.text"); //$NON-NLS-1$
    private static final String DOWN_BUTTON_TEXT = UiConstants.Util.getString("RelationshipPanel.rightButton.text"); //$NON-NLS-1$   

    private Composite pnlHeader; // numCols = 3
    // row 1:
    Text txtRelationshipName;

    // row 2:
    private CLabel lblLocation;
    private Text txtLocation;
    private Button btnBrowseLocation;

    // row 3:
    private Text txtRelationshipType;
    private Button btnBrowseType;

    // row 3:

    private Composite pnlTabOuterPanel;
    private TabFolder tabFolder;

    private TabItem tiRolesTab;
    private SashForm pnlRoleSplitter;

    // left half of sashform
    RolePanel rpLeftRole;

    // right half of sashform
    private Composite pnlRight;

    private Composite pnlArrowButtons;
    private Button btnLeft;
    private Button btnRight;
    private Button btnSwap;

    RolePanel rpRightRole;

    private TabItem tiPropertiesTab;
    private PropertySheetPage pspgProperties;

    private Relationship rRelationshipObject;
    private RelationshipEditor reEditor;
    private IStatusListener islStatusListener;
    private RelationshipAssociationDescriptor radAssociationDescriptor;
    private Object oLocation;

    private IStatus isCurrentStatus;

    private int iColumns = 10;

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public RelationshipPanel( Composite parent,
                              Relationship rel ) {
        super(parent, SWT.NONE);
        this.rRelationshipObject = rel;

        init();
    }

    public RelationshipPanel( Composite parent,
                              Relationship rel,
                              IStatusListener islStatusListener ) {
        super(parent, SWT.NONE);
        this.rRelationshipObject = rel;
        this.islStatusListener = islStatusListener;

        init();
    }

    public RelationshipPanel( Composite parent,
                              Relationship rel,
                              IStatusListener islStatusListener,
                              RelationshipAssociationDescriptor radAssociationDescriptor ) {
        super(parent, SWT.NONE);
        this.rRelationshipObject = rel;
        this.islStatusListener = islStatusListener;
        this.radAssociationDescriptor = radAssociationDescriptor;

        init();
    }

    /**
     * Initialize the panel.
     */
    private void init() {

        // temporarily create an empty RelObject here
        if (rRelationshipObject == null) {
            rRelationshipObject = createDefaultRelationship();
        }

        // create the editor for the business object
        getEditor();

        // create the controls
        createControl(this);

        // Initialize the Button states
        setButtonStates();

        // Register to listen for Change Notifications
        ModelUtilities.addNotifyChangedListener(this);
    }

    private Relationship createDefaultRelationship() {
        return RelationshipFactory.eINSTANCE.createRelationship();
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose() {
        // remove us
        ModelUtilities.removeNotifyChangedListener(this);

        super.dispose();

    }

    RelationshipEditor getEditor() {
        if (reEditor == null) {
            if (rRelationshipObject == null) {
                rRelationshipObject = createDefaultRelationship();
                reEditor = RelationshipPlugin.createEditor(rRelationshipObject);

            } else {
                reEditor = RelationshipPlugin.createEditor(rRelationshipObject);
            }
        }

        return reEditor;
    }

    public void setEditor( RelationshipEditor reEditor ) {
        this.reEditor = reEditor;
    }

    public void setAssociationDescriptor( RelationshipAssociationDescriptor radAssociationDescriptor ) {
        // System.out.println("[RelationshipPanel.setAssociationDescriptor] radAssociationDescriptor: " +
        // radAssociationDescriptor.getClass().getName() );
        this.radAssociationDescriptor = radAssociationDescriptor;
        /*
         * strategy for a RelationshipAssociation
         *      1. do RelAssoc.create() here to create the Relationship
         *      2. pass it to the panel
         *      3. the panel will see it has a RelAssoc and put up the location control
         *          (label + textfield + browse button)
         *      4. The browse dialog will allow instanceof RelatonshipContainer
         *           and RelationshipModel
         *      5. the panel will have a getLocation method so that the wizpage
         *         can retrieve the location to apply it on finish
         *      6. To apply the location to the Relationship
         *          a) If loc is a RelationshipContainer...
         *                  use Relationship.setRelationshipContainer( loc )
         *          b) If loc is a RelationshipModel...
         *                  use model.getModelResource().getAllRootEObjects().add( loc )
         *     
         *              
         */
    }

    private RelationshipAssociationDescriptor getRelationshipAssociationDescriptor() {
        return radAssociationDescriptor;
    }

    private Object getRelationshipLocation() {
        return oLocation;
    }

    private void setRelationshipLocation( Object oLocation ) {
        this.oLocation = oLocation;
    }

    private void setLocationText() {
        IPath path = ModelerCore.getModelEditor().getFullPathToParent(getRelationship());

        if (!path.equals(Path.ROOT)) {
            txtLocation.setText(path.toString());
        }
    }

    private Relationship getRelationship() {
        return this.rRelationshipObject;
    }

    private void applyLocationToRelationship() {
        if (radAssociationDescriptor != null) {
            Object oLocation = getRelationshipLocation();
            if (oLocation instanceof RelationshipContainer) {
                getRelationship().setRelationshipContainer((RelationshipContainer)oLocation);
            } else {
                try {

                    ModelResource mr = null;
                    if (oLocation instanceof ModelResource) {
                        mr = (ModelResource)oLocation;
                    } else {
                        IResource resource = (IResource)oLocation;
                        mr = ModelUtil.getModelResource((IFile)resource, true);
                    }

                    ModelerCore.getModelEditor().addValue(mr.getEmfResource(),
                                                          getRelationship(),
                                                          mr.getEmfResource().getContents());

                    setLocationText();
                } catch (ModelWorkspaceException mwe) {
                    ModelerCore.Util.log(IStatus.ERROR, mwe, mwe.getMessage());
                } catch (ModelerCoreException mce) {
                    ModelerCore.Util.log(IStatus.ERROR, mce, mce.getMessage());
                }
            }

            // Let's set the association object on the descriptor (assume complete here?)
            radAssociationDescriptor.setAssociation(getRelationship());
        }
    }

    public void refreshFromBusinessObject() {

        // 1. header
        String sName = getEditor().getName();

        // jh added null test to fix NPE (def. 12855)
        if (txtRelationshipName != null && sName != null) {
            txtRelationshipName.setText(sName);
            txtRelationshipName.setSelection(sName.length());
        }

        if (getEditor().getRelationshipType() != null) {
            txtRelationshipType.setText(getEditor().getRelationshipType().getName());
        }

        if (txtLocation != null) {
            setLocationText();
        }

        // 2. Role Tab: Role A
        rpLeftRole.setInput(rRelationshipObject, SOURCE);

        // 3. Role Tab: Role B ;
        rpRightRole.setInput(rRelationshipObject, TARGET);

        // 4. Properties Tab
        //        System.out.println("[RelationshipPanel.refreshFromBusinessObject] About to load propertysheet, rRelationshipObject is: " + rRelationshipObject );  //$NON-NLS-1$
        pspgProperties.selectionChanged(null, new StructuredSelection(rRelationshipObject));

        // 5. reset button states
        setButtonStates();

        // 6. validate
        validate();

    }

    void validate() {
        // validate
        isCurrentStatus = getEditor().validate();

        // now do the local test, and if we fail construct our own IStatus to carry that error
        if (isCurrentStatus.isOK()) {

            if (!isLocationOk()) {
                isCurrentStatus = new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, NEED_LOCATION_ERROR_MSG);
            }
        }

        islStatusListener.setStatus(isCurrentStatus);

    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {

        iColumns = 10;

        GridLayout gridLayout = new GridLayout();
        this.setLayout(gridLayout);
        gridLayout.numColumns = iColumns;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint = 400;
        gridData.widthHint = 600;

        this.setLayoutData(gridData);

        // 2. create the header
        createHeaderPanel(parent);

        // 3. create the tabs panel
        createTabsPanel(parent);

        // 4. establish listening
        registerListeners();
        refreshFromBusinessObject();

    }

    private void createHeaderPanel( Composite parent ) {
        final int COLUMNS = 3;

        this.pnlHeader = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_HORIZONTAL, iColumns, COLUMNS);

        // ================
        // Row 1: HORIZONTAL_ALIGN_FILL
        // ================

        // 'name' label
        WidgetFactory.createLabel(this.pnlHeader, RELATIONSHIP_NAME_TEXT);

        // 'name' textfield
        String sText = "Name"; //$NON-NLS-1$

        if (rRelationshipObject != null) {
            sText = this.rRelationshipObject.getName();
        }

        this.txtRelationshipName = WidgetFactory.createTextField(this.pnlHeader,
                                                                 GridData.HORIZONTAL_ALIGN_FILL,
                                                                 (COLUMNS - 1),
                                                                 sText);

        // ================
        // Row 2:
        // ================

        // 'type' label
        WidgetFactory.createLabel(this.pnlHeader, RELATIONSHIP_TYPE_TEXT);

        // 'type' textfield
        String sText2 = ""; //$NON-NLS-1$
        if (this.rRelationshipObject != null) {
            if (this.rRelationshipObject.getType() != null) {
                sText2 = this.rRelationshipObject.getType().getName();
            }
        }

        this.txtRelationshipType = WidgetFactory.createTextField(pnlHeader, GridData.FILL_HORIZONTAL, sText2);
        this.txtRelationshipType.setText(SELECT_TYPE_TEXT);
        this.txtRelationshipType.setEditable(false);

        // 'browse' button
        this.btnBrowseType = WidgetFactory.createButton(this.pnlHeader, BROWSE_TEXT);

        // ================
        // Row 3:
        // ================

        // 'location' label
        this.lblLocation = WidgetFactory.createLabel(this.pnlHeader, LOCATION_TEXT);

        // 'location' textfield
        this.txtLocation = WidgetFactory.createTextField(this.pnlHeader, GridData.FILL_HORIZONTAL, ""); //$NON-NLS-1$
        this.txtLocation.setEditable(false);

        // 'location browse' button
        this.btnBrowseLocation = WidgetFactory.createButton(this.pnlHeader, BROWSE_TEXT);

    }

    private void createTabsPanel( Composite parent ) {

        pnlTabOuterPanel = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        pnlTabOuterPanel.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = iColumns;
        pnlTabOuterPanel.setLayoutData(gridData);

        // create Tab Folder
        tabFolder = new TabFolder(pnlTabOuterPanel, SWT.TOP);
        GridData gridData3 = new GridData(GridData.FILL_BOTH);
        tabFolder.setLayoutData(gridData3);

        // Roles Tab
        createRolesTab(tabFolder);
        Composite pnlRolesOuterPanel = new Composite(tabFolder, SWT.NONE);
        createRolesPanel(pnlRolesOuterPanel);
        GridLayout gridLayout2 = new GridLayout();
        pnlRolesOuterPanel.setLayout(gridLayout2);
        gridLayout2.numColumns = 1;
        tiRolesTab.setControl(pnlRolesOuterPanel);

        // Properties Tab
        createPropertiesTab(tabFolder);
        Composite pnlPropertiesOuterPanel = new Composite(tabFolder, SWT.NONE);

        createPropertiesPanel(pnlPropertiesOuterPanel);
        GridLayout gridLayout3 = new GridLayout();
        pnlPropertiesOuterPanel.setLayout(gridLayout3);
        gridLayout3.numColumns = 1;
        tiPropertiesTab.setControl(pnlPropertiesOuterPanel);

        // default to the Roles Tab
        tabFolder.setSelection(0);
        tabFolder.setVisible(true);
    }

    private void createRolesTab( TabFolder parent ) {
        tiRolesTab = new TabItem(parent, SWT.NONE);
        tiRolesTab.setText(ROLES_TAB_TEXT);
        tiRolesTab.setToolTipText(ROLES_TAB_TEXT);
    }

    private void createPropertiesTab( TabFolder parent ) {
        tiPropertiesTab = new TabItem(parent, SWT.NONE);
        tiPropertiesTab.setText(PROPERTIES_TITLE);
        tiPropertiesTab.setToolTipText(PROPERTIES_TITLE);
    }

    private void createRolesPanel( Composite parent ) {

        // create the panel
        pnlRoleSplitter = new SashForm(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        // gridLayout.numColumns = 3;
        pnlRoleSplitter.setLayout(gridLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        pnlRoleSplitter.setLayoutData(gridData);

        // left role panel
        rpLeftRole = new RolePanel(pnlRoleSplitter, rRelationshipObject, SOURCE);

        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = GridData.FILL;
        gridData1.verticalAlignment = GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = true;
        rpLeftRole.setLayoutData(gridData1);

        // ???? does the main class really need to listen to the role panels? I don't think so...
        rpLeftRole.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {

                // test for selection first; no action on deselect?
                if (rpLeftRole.hasSelection()) {
                    handleLeftRoleTableSelection();
                }
            }
        });

        pnlRight = new Composite(pnlRoleSplitter, SWT.NONE);
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 2;
        gridLayout2.marginHeight = 0;
        pnlRight.setLayout(gridLayout2);

        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.verticalAlignment = GridData.FILL;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.grabExcessVerticalSpace = true;
        pnlRight.setLayoutData(gridData2);

        // arrow button panel
        createArrowButtonPanel(pnlRight);

        // right role panel
        rpRightRole = new RolePanel(pnlRight, rRelationshipObject, TARGET);

        rpRightRole.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {

                // test for selection first; no action on deselect?
                if (rpRightRole.hasSelection()) {
                    handleRightRoleTableSelection();
                }
            }
        });

        // set weights on the splitter (must do after children created)
        pnlRoleSplitter.setWeights(ROLE_WEIGHTS);

    }

    private void createPropertiesPanel( Composite parent ) {
        // create the panel
        pspgProperties = new ModelObjectPropertySheetPage();
        pspgProperties.setPropertySourceProvider(ModelUtilities.getPropertySourceProvider());
        pspgProperties.createControl(parent);
        Control result = pspgProperties.getControl();
        GridData gd = new GridData(GridData.FILL_BOTH);
        result.setLayoutData(gd);

    }

    /**
     * Create the 'add/remove' button panel
     */
    private void createArrowButtonPanel( Composite parent ) {

        pnlArrowButtons = new Composite(parent, SWT.NONE);
        // ////// pnlArrowButtons.setBackground( ColorConstants.green );

        GridLayout gridLayout = new GridLayout();
        pnlArrowButtons.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = false;

        pnlArrowButtons.setLayoutData(gridData);

        // Right Arrow button
        btnRight = WidgetFactory.createButton(pnlArrowButtons, DOWN_BUTTON_TEXT, BUTTON_GRID_STYLE);
        btnRight.setImage(UiPlugin.getDefault().getImage(PluginConstants.Images.RIGHT_ARROW_ICON));

        btnRight.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Left Arrow button
        btnLeft = WidgetFactory.createButton(pnlArrowButtons, UP_BUTTON_TEXT, BUTTON_GRID_STYLE);
        btnLeft.setImage(UiPlugin.getDefault().getImage(PluginConstants.Images.LEFT_ARROW_ICON));

        // btnAdd.setToolTipText( UP_BUTTON_TOOLTIP );
        btnLeft.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Swap button
        btnSwap = WidgetFactory.createButton(pnlArrowButtons, SWAP_TEXT, BUTTON_GRID_STYLE);
        // btnSwap.setImage( UiPlugin.getDefault().getImage( PluginConstants.Images.RIGHT_ARROW_ICON ) );

        btnSwap.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // btnRemove.setToolTipText( DOWN_BUTTON_TOOLTIP );

    }

    private void registerListeners() {

        // txtRelationshipName - listener for typing changes
        txtRelationshipName.addModifyListener(new ModifyListener() {
            public void modifyText( final ModifyEvent event ) {
                getEditor().setName(txtRelationshipName.getText());
                validate();
            }
        });

        // txtRelationshipType - readonly, no listening

        // btnBrowseType
        btnBrowseType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                handleBrowseTypeButtonPressed();
                validate();

            }
        });

        // btnBrowseLocation
        if (btnBrowseLocation != null) {

            btnBrowseLocation.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    handleBrowseLocationButtonPressed();
                    validate();

                }
            });
        }

        // Left Arrow Button
        btnLeft.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                leftButtonPressed();
                validate();

            }
        });

        // Right Arrow Button
        btnRight.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                rightButtonPressed();
                validate();

            }
        });

        // Right Arrow Button
        btnSwap.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                swapButtonPressed();
                validate();

            }
        });

        // note: RolePanel controls are handled inside that (inner) class.

    }

    private void setEnabledNonTypeControls( boolean bEnabled ) {

        // txtRelationshipName
        btnLeft.setEnabled(bEnabled);
        btnRight.setEnabled(bEnabled);
        rpLeftRole.setControlsEnabled(bEnabled);
        rpRightRole.setControlsEnabled(bEnabled);

        if (btnBrowseLocation != null) {
            boolean bNewAssociationCase = isNewAssociationCase();
            //          System.out.println("[RelationshipPanel.setEnabledNonTypeControls] about to apply bNewAssociationCase: " + bNewAssociationCase );  //$NON-NLS-1$
            btnBrowseLocation.setEnabled(bNewAssociationCase);
            btnBrowseLocation.setVisible(bNewAssociationCase);
        }
    }

    void handleBrowseTypeButtonPressed() {

        // ==================================
        // launch Relationship Type chooser
        // ==================================

        // generate the dialog
        SelectionDialog sdDialog = RelationshipPropertyEditorFactory.createRelationshipTypeSelector(UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                                                                                                    rRelationshipObject);

        // add filters
        ((ModelWorkspaceDialog)sdDialog).addFilter(new ClosedProjectFilter());
        ((ModelWorkspaceDialog)sdDialog).addFilter(new AbstractRelationshipTypeFilter());

        // present it
        sdDialog.open();

        if (sdDialog.getReturnCode() == Window.OK) {
            Object[] oSelectedObjects = sdDialog.getResult();

            // add the selected RelationshipType to this Relationship
            if (oSelectedObjects.length > 0) {
                RelationshipType rt = (RelationshipType)oSelectedObjects[0];
                // update the Relationship
                getEditor().setRelationshipType(rt);
                txtRelationshipType.setText(getEditor().getRelationshipType().getName());
                this.setEnabledNonTypeControls(true);
                rpLeftRole.updateRoleName();
                rpRightRole.updateRoleName();

                rpLeftRole.setRoleButtonStates();
                rpRightRole.setRoleButtonStates();

                // update buttons for main panel
                setButtonStates();

                validate();
            }
        }

    }

    void handleBrowseLocationButtonPressed() {

        // ==================================
        // launch Location chooser
        // ==================================

        RelationshipModelSelectorDialog mwdDialog = new RelationshipModelSelectorDialog(
                                                                                        UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell());
        mwdDialog.setValidator(new RelationshipLocationSelectionValidator());
        mwdDialog.setAllowMultiple(false);
        mwdDialog.open();

        if (mwdDialog.getReturnCode() == Window.OK) {
            Object[] oSelectedObjects = mwdDialog.getResult();

            // add the selected location to this Relationship
            if (oSelectedObjects.length > 0) {
                setRelationshipLocation(oSelectedObjects[0]);
                applyLocationToRelationship();

                validate();
            }
        }

    }

    void handleLeftRoleTableSelection() {
        rpRightRole.clearSelection();

        // update buttons on both sides
        rpLeftRole.setRoleButtonStates();
        rpRightRole.setRoleButtonStates();

        // update buttons for main panel
        setButtonStates();

        validate();
    }

    void handleRightRoleTableSelection() {
        rpLeftRole.clearSelection();

        // update buttons on both sides
        rpLeftRole.setRoleButtonStates();
        rpRightRole.setRoleButtonStates();

        // update buttons for main panel
        setButtonStates();

        validate();
    }

    /**
     * Displays dialog informing user that an object of an invalid type was trying to be moved.
     * 
     * @param theStatus the status to use for the message
     * @since 4.2
     */
    private void showInvalidMoveDialog( IStatus theStatus ) {
        ErrorDialog.openError(getShell(), UiConstants.Util.getString("RelationshipPanel.dialog.invalidType.title"), //$NON-NLS-1$, 
                              UiConstants.Util.getString("RelationshipPanel.dialog.invalidType.msg"), //$NON-NLS-1$, 
                              theStatus);
    }

    void rightButtonPressed() {

        //        System.out.println("[RelationshipPanel.rightButtonPressed] " );  //$NON-NLS-1$
        // get the selected objects from the right panel
        List lstObjectsToMove = rpLeftRole.getSelectedObjects();

        // if allowed, move the objects to the left table
        if (getEditor().canMoveSourceParticipantToTargetParticipant(lstObjectsToMove)) {
            //            System.out.println("[RelationshipPanel.rightButtonPressed] about to call 'move source' on: " + lstObjectsToMove );  //$NON-NLS-1$
            getEditor().moveSourceParticipantToTargetParticipant(lstObjectsToMove);
        } else {
            // not allowed to move. use the RolePanel validator to get consistent status message.
            RelationshipRoleSelectionValidator roleValidator = new RelationshipRoleSelectionValidator(
                                                                                                      this.rRelationshipObject.getTargetRole());
            IStatus status = roleValidator.validate(lstObjectsToMove.toArray());

            if (!status.isOK()) {
                showInvalidMoveDialog(status);
            }
        }
        refreshTables();

        // update buttons on both sides
        rpLeftRole.setRoleButtonStates();
        rpRightRole.setRoleButtonStates();

        // update buttons for main panel
        setButtonStates();

        validate();
    }

    private void refreshTables() {
        rpRightRole.refreshTable();
        rpLeftRole.refreshTable();
    }

    void leftButtonPressed() {

        // get the selected objects from the right panel
        List lstObjectsToMove = rpRightRole.getSelectedObjects();

        // if allowed, move the objects to the left table
        if (getEditor().canMoveTargetParticipantToSourceParticipant(lstObjectsToMove)) {
            //            System.out.println("[RelationshipPanel.leftButtonPressed] about to call 'move target' on: " + lstObjectsToMove );  //$NON-NLS-1$
            getEditor().moveTargetParticipantToSourceParticipant(lstObjectsToMove);
        } else {
            // not allowed to move. use the RolePanel validator to get consistent status message.
            RelationshipRoleSelectionValidator roleValidator = new RelationshipRoleSelectionValidator(
                                                                                                      this.rRelationshipObject.getSourceRole());
            IStatus status = roleValidator.validate(lstObjectsToMove.toArray());

            if (!status.isOK()) {
                showInvalidMoveDialog(status);
            }
        }
        refreshTables();

        // update buttons on both sides
        rpLeftRole.setRoleButtonStates();
        rpRightRole.setRoleButtonStates();

        // update buttons for main panel
        setButtonStates();

        validate();
    }

    void swapButtonPressed() {

        if (getEditor().canSwapParticipants()) {

            try {
                getEditor().swapParticipants();

            } catch (ModelerCoreException mce) {
                ModelerCore.Util.log(IStatus.ERROR, mce, mce.getMessage());
            }
        }

        refreshTables();

        // update buttons on both sides
        rpLeftRole.setRoleButtonStates();
        rpRightRole.setRoleButtonStates();

        // update buttons for main panel
        setButtonStates();

        validate();

    }

    /**
     * Set the enabled/disabled states of the Buttons.
     */
    private void setButtonStates() {

        /*
         * 1. table selection code enforces that only 1 of the 2 tables will have
         *    selelction at any one time.
         * 2. Button rules:
         *     a) header 'browse for type' button is always enabled
         *     b) left arrow is enabled when the right table has a selection
         *     c) right arrow is enabled when the left table has a selection
         */

        if (!isNewAssociationCase() && ModelObjectUtilities.isReadOnly(rRelationshipObject)) {
            btnBrowseType.setEnabled(false);
            btnSwap.setEnabled(false);
            btnLeft.setEnabled(false);
            btnRight.setEnabled(false);
            setEnabledNonTypeControls(false);

            return;
        }

        btnBrowseType.setEnabled(true);

        // enable if either side has rows
        btnSwap.setEnabled(getEditor().getSourceParticipants().size() > 0 || getEditor().getTargetParticipants().size() > 0);

        // if no type selected, protect all fields except the 'browse for type' button
        if (getEditor().getRelationshipType() != null && getEditor().getRelationshipType().getName() != null) {

            setEnabledNonTypeControls(true);
            btnLeft.setEnabled(rpRightRole.hasSelection());

            btnRight.setEnabled(rpLeftRole.hasSelection());

        } else {
            setEnabledNonTypeControls(false);
        }

        boolean bNewAssociationCase = isNewAssociationCase();

        if (txtLocation != null) {
            txtLocation.setVisible(true); // bNewAssociationCase );
            lblLocation.setVisible(true);// bNewAssociationCase );
            btnBrowseLocation.setEnabled(bNewAssociationCase);
            btnBrowseLocation.setVisible(bNewAssociationCase);
        }

    }

    public void selectionChanged( SelectionChangedEvent event ) {
        setButtonStates();
    }

    private boolean isNewAssociationCase() {
        return (getRelationshipAssociationDescriptor() != null);
    }

    public void setBusinessObject( Relationship rRelationshipObject ) {
        this.rRelationshipObject = rRelationshipObject;

        // recreate the editor
        reEditor = RelationshipPlugin.createEditor(rRelationshipObject);

        // create the editor for the business object
        reEditor = RelationshipPlugin.createEditor(rRelationshipObject);

        // when the business object changes, refresh everything...
        refreshFromBusinessObject();
    }

    /*
     * do any non-metadata validations here
     */

    public boolean passesLocalValidation() {
        boolean bResult = !isNewAssociationCase() || isLocationOk();

        return bResult;
    }

    /*
     * Test location here because the RelationshipEditor does not support it.
     */

    public boolean isLocationOk() {
        boolean bResult = true;

        // default to true, then look for any exceptional bad state
        if (isNewAssociationCase() && txtLocation != null && txtLocation.getText().trim().equals("")) { //$NON-NLS-1$
            bResult = false;
        }
        return bResult;
    }

    public void notifyChanged( Notification notification ) {
        Object obj = notification.getNotifier();

        if (obj.equals(rRelationshipObject)) {

            if (!this.isDisposed() && obj instanceof Relationship || obj instanceof RelationshipRole) {
                // System.out.println("[RelationshipPanel.notifyChanged] about to call refreshFromBusinessObject()");
                refreshFromBusinessObject();
            }
        }
    }

    public void widgetSelected( SelectionEvent e ) {
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
        widgetSelected(e);
    }

    class RolePanel extends Composite implements SelectionListener {

        /*
         * This class will create a panel containing:
         *      1. A label on top to carry the role's name, centered
         *      2. A one-col table 
         *          a. the model will hold the objects belonging to the role
         *             (source or target);
         *          b. the label provider will concat the icon + the object's name + the object's fullname
         *          c. optionally, the one col could carry the heading 'Objects', or something like that
         *      3. A button panel containing 'add...' and 'remove' buttons will appear at the bottom
         */

        private Relationship rRelationshipObject;
        private int iRoleType;
        private int DEFAULT_ROLE_LABEL_WIDTH = 120;

        private static final int BUTTON_GRID_STYLE = GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_CENTER;
        private final String ADD_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipPanel.add.text"); //$NON-NLS-1$
        private final String REMOVE_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipPanel.remove.text"); //$NON-NLS-1$
        private final String OBJECT_NAME_TITLE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipPanel.objectName.title"); //$NON-NLS-1$
        private final String OBJECT_PATH_TITLE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipPanel.objectPath.title"); //$NON-NLS-1$

        private String sDefaultRoleName = "RoleName"; //$NON-NLS-1$
        private String sRoleName;

        private Composite pnlTableStuff;
        private CLabel lblRoleType; // serves as heading

        private Table tblRoleTable;
        private String[] columnNames = new String[] {
        /*
         * Extremely weird:  When I put them in BACKWARDS, they appear in the desired order...
         */
        OBJECT_PATH_TITLE, OBJECT_NAME_TITLE};

        private TableViewer tvRoleTableViewer;
        private TableContentProvider cpContentProvider;
        private TableLabelProvider lpChoiceLabelProvider;

        private Composite pnlAddRemoveButtons;
        private Button btnAdd;
        private Button btnRemove;

        public RolePanel( Composite parent,
                          Relationship rRelationshipObject,
                          int iRoleType ) {

            super(parent, SWT.NONE);
            // this.parent = parent;
            this.rRelationshipObject = rRelationshipObject;
            this.iRoleType = iRoleType;

            init();
        }

        /**
         * Initialize the panel.
         */
        private void init() {
            // =================================
            // Create the Controls (Top) Panel
            // =================================
            createControl(this);

            // Initialize the Button states
            setRoleButtonStates();

            tblRoleTable.addSelectionListener(this);

        }

        public void setInput( Relationship rRelationshipObject,
                              int iRoleType ) {

            this.rRelationshipObject = rRelationshipObject;
            this.iRoleType = iRoleType;

            tvRoleTableViewer.setInput(rRelationshipObject);
            updateRoleName();
        }

        public void updateRoleName() {
            // establish the role name from the input objects, if any
            if (rRelationshipObject != null) {
                if (iRoleType == SOURCE) {
                    if (getEditor().getSourceRoleName() != null) {
                        sRoleName = getEditor().getSourceRoleName();
                    }
                } else {
                    if (getEditor().getTargetRoleName() != null) {
                        sRoleName = getEditor().getTargetRoleName();
                    }
                }
            }

            // update the label
            setRoleTypeLabelText(sRoleName);
        }

        public void clearSelection() {
            tblRoleTable.deselectAll();
        }

        public void refreshTable() {
            tvRoleTableViewer.refresh();
        }

        public boolean hasSelection() {
            return (tblRoleTable.getSelectionCount() > 0);
        }

        public List getSelectedObjects() {
            List lstSelectedObjects = new ArrayList();

            int[] iSelectedIndices = tblRoleTable.getSelectionIndices();

            if (iRoleType == SOURCE) {
                for (int i = 0; i < iSelectedIndices.length; i++) {
                    lstSelectedObjects.add(getEditor().getSourceParticipants().get(iSelectedIndices[i]));
                }
            } else {
                for (int i = 0; i < iSelectedIndices.length; i++) {
                    lstSelectedObjects.add(getEditor().getTargetParticipants().get(iSelectedIndices[i]));
                }
            }

            return lstSelectedObjects;
        }

        public void addSelectionListener( SelectionListener listener ) {
            tblRoleTable.addSelectionListener(listener);
        }

        public void removeSelectionListener( SelectionListener listener ) {
            tblRoleTable.removeSelectionListener(listener);
        }

        /**
         * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#createControl(org.eclipse.swt.widgets.Composite)
         */
        public void createControl( Composite parent ) {

            // 0. Set layout for the SashForm
            GridLayout gridLayout = new GridLayout();
            this.setLayout(gridLayout);
            gridLayout.numColumns = 1;
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridLayout.marginWidth = gridLayout.marginHeight = 0;

            this.setLayoutData(gridData);

            // 1. 'RoleName' label
            createRoleNamePanel(parent);

            // 2. Create the table
            createTableStuffPanel(parent);

            // 3. establish listening
            registerListeners();
        }

        private void createRoleNamePanel( Composite parent ) {
            if (iRoleType == SOURCE) {
                sDefaultRoleName = DEFAULT_ROLE_A_NAME;
            } else {
                sDefaultRoleName = DEFAULT_ROLE_B_NAME;
            }

            lblRoleType = WidgetFactory.createLabel(parent, sDefaultRoleName);

            // establish a reasonably wide preferred size to handle longer role type names
            GridData gridData = (GridData)lblRoleType.getLayoutData();
            gridData.widthHint = DEFAULT_ROLE_LABEL_WIDTH;

            lblRoleType.setFont(getBoldFont(lblRoleType.getFont()));

            // default to default role name
            sRoleName = sDefaultRoleName;

            // update the label
            setRoleTypeLabelText(sRoleName);
        }

        private void setRoleTypeLabelText( String sText ) {
            lblRoleType.setText(sText);
        }

        private Font getBoldFont( Font f ) {

            FontData data = f.getFontData()[0];
            data.setStyle(SWT.BOLD);
            Font fNewFont = GlobalUiFontManager.getFont(data);

            return fNewFont;
        }

        private void createTableStuffPanel( Composite parent ) {

            pnlTableStuff = new Composite(parent, SWT.NONE);

            GridLayout gridLayout = new GridLayout();
            gridLayout.marginWidth = gridLayout.marginHeight = 0;
            pnlTableStuff.setLayout(gridLayout);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridData.widthHint = 120;
            gridData.heightHint = 150;
            pnlTableStuff.setLayoutData(gridData);

            // 1. Create the table
            createTableViewerPanel(pnlTableStuff);

            // 2. Create the 'add/remove' button panel
            createAddRemoveButtonPanel(pnlTableStuff);
        }

        /*
         * Create the TableViewerPanel 
         */
        private void createTableViewerPanel( Composite parent ) {
            // Create the table
            createTable(parent);

            // Create and setup the TableViewer
            createTableViewer();

            cpContentProvider = new TableContentProvider(iRoleType);
            tvRoleTableViewer.setContentProvider(cpContentProvider);

            lpChoiceLabelProvider = new TableLabelProvider();
            tvRoleTableViewer.setLabelProvider(lpChoiceLabelProvider);

            if (rRelationshipObject != null) {
                tvRoleTableViewer.setInput(rRelationshipObject);
            }

        }

        /**
         * Create the Table
         */
        private void createTable( Composite parent ) {
            int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;

            tblRoleTable = new Table(parent, style);
            TableLayout layout = new TableLayout();
            tblRoleTable.setLayout(layout);

            GridData gridData = new GridData(GridData.FILL_BOTH);
            tblRoleTable.setLayoutData(gridData);

            tblRoleTable.setLinesVisible(true);
            tblRoleTable.setHeaderVisible(true);

            // 1st column
            TableColumn column1 = new TableColumn(tblRoleTable, SWT.LEFT, 0);
            column1.setText(columnNames[0]);
            ColumnWeightData weight = new ColumnWeightData(1);
            layout.addColumnData(weight);

            // 2nd column
            TableColumn column2 = new TableColumn(tblRoleTable, SWT.LEFT, 0);
            column2.setText(columnNames[1]);
            ColumnWeightData weight2 = new ColumnWeightData(1);
            layout.addColumnData(weight2);

            // add a listener to keep the table sized to it's container
            new TableSizeAdapter(tblRoleTable, 10);

        }

        /**
         * Create the TableViewer
         */
        private void createTableViewer() {

            tvRoleTableViewer = new TableViewer(tblRoleTable);

            tvRoleTableViewer.setUseHashlookup(true);

            tvRoleTableViewer.setColumnProperties(columnNames);

            // Create the cell editors
            CellEditor[] editors = new CellEditor[columnNames.length];

            // Column 1 : Attribute not editable
            editors[0] = null;

            // Column 1 : Attribute not editable
            editors[1] = null;

            // Assign the cell editors to the viewer
            tvRoleTableViewer.setCellEditors(editors);
        }

        /**
         * Create the 'add/remove' button panel
         */
        private void createAddRemoveButtonPanel( Composite parent ) {
            pnlAddRemoveButtons = new Composite(parent, SWT.NONE);

            GridLayout gridLayout = new GridLayout();
            pnlAddRemoveButtons.setLayout(gridLayout);
            gridLayout.numColumns = 2;
            GridData gridData = new GridData(GridData.GRAB_VERTICAL);
            gridData.horizontalAlignment = GridData.CENTER;
            gridData.verticalAlignment = GridData.CENTER;
            gridData.grabExcessHorizontalSpace = false;
            gridData.grabExcessVerticalSpace = false;

            pnlAddRemoveButtons.setLayoutData(gridData);

            // Add... button
            btnAdd = WidgetFactory.createButton(pnlAddRemoveButtons, ADD_TEXT, BUTTON_GRID_STYLE);

            btnAdd.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    addButtonPressed();
                }
            });
            btnAdd.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            // Remove button
            btnRemove = WidgetFactory.createButton(pnlAddRemoveButtons, REMOVE_TEXT, BUTTON_GRID_STYLE);

            btnRemove.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    removeButtonPressed();
                }
            });

            btnRemove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }

        private void registerListeners() {
        }

        void addButtonPressed() {

            /*
             * launch the 'Add Objects' dialog and put the result in our business object, then re init.
             *             
             */

            ModelObjectFinderDialog mwdDialog = new ModelObjectFinderDialog(
                                                                            UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell());
            RelationshipRole role = (iRoleType == SOURCE) ? this.rRelationshipObject.getSourceRole() : this.rRelationshipObject.getTargetRole();
            mwdDialog.setValidator(new RelationshipRoleSelectionValidator(role));
            mwdDialog.open();

            if (mwdDialog.getReturnCode() == Window.OK) {
                Object[] oSelectedObjects = mwdDialog.getResult();

                // add the new object(s) to the Role object

                ArrayList aryl = new ArrayList();

                for (int i = 0; i < oSelectedObjects.length; i++) {
                    aryl.add(oSelectedObjects[i]);
                }
                try {
                    if (iRoleType == SOURCE) {
                        if (getEditor().canAddToSourceParticipants(aryl)) {
                            getEditor().addSourceParticipants(aryl);
                        }
                    } else {
                        if (getEditor().canAddToTargetParticipants(aryl)) {
                            getEditor().addTargetParticipants(aryl);
                        }
                    }
                } catch (ModelerCoreException mce) {
                    ModelerCore.Util.log(IStatus.ERROR, mce, mce.getMessage());
                }

                // new objects may have changed our state
                setRoleButtonStates();
                validate();
            }

            tvRoleTableViewer.refresh();

            validate();
        }

        void removeButtonPressed() {
            /*
             * remove the object selected in the table from our business object, then re init.
             */

            if (hasSelection()) {
                List lstElementsToRemove = getSelectedObjects();

                try {
                    if (iRoleType == SOURCE) {
                        getEditor().removeSourceParticipants(lstElementsToRemove);

                    } else {
                        getEditor().removeTargetParticipants(lstElementsToRemove);
                    }
                } catch (ModelerCoreException mce) {
                    ModelerCore.Util.log(IStatus.ERROR, mce, mce.getMessage());
                }
            }

            tvRoleTableViewer.refresh();
            setRoleButtonStates();

            validate();
        }

        /**
         * Set the enabled/disabled states of the Buttons.
         */
        void setRoleButtonStates() {
            /*
             *  1. Always enable Add...
             *  2. if a row in the table is selected, enable Remove             
             */

            btnAdd.setEnabled(true);

            btnRemove.setEnabled(tblRoleTable.getSelectionCount() > 0);

        }

        /**
         * Set the enabled/disabled states of the Buttons.
         */
        public void setControlsEnabled( boolean bEnabled ) {
            /*
             *  1. Always enable Add...
             *  2. if a row in the table is selected, enable Remove             
             */
            btnAdd.setEnabled(bEnabled);

            btnRemove.setEnabled(bEnabled);

        }

        /**
         * this method picks up selection insided the table in once instance of RolePanel; we also need the RelationshipPanel code
         * to listen to RolePanel so that they can respond when selection changes in one of the 2 tables.
         */
        public void widgetSelected( SelectionEvent e ) {
            setRoleButtonStates();
        }

        public void widgetDefaultSelected( SelectionEvent e ) {
            widgetSelected(e);
        }
    }

    class TableContentProvider implements IStructuredContentProvider {

        Relationship rRelationshipObject;
        private int iRoleType;

        public TableContentProvider( int iRoleType ) {

            this.iRoleType = iRoleType;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object theInputElement ) {

            rRelationshipObject = (Relationship)theInputElement;

            Object[] result = null;

            List lstObjects = new ArrayList();
            if (rRelationshipObject != null) {
                if (iRoleType == SOURCE) {
                    lstObjects = getEditor().getSourceParticipants();
                } else {
                    lstObjects = getEditor().getTargetParticipants();
                }
            }

            if ((lstObjects != null) && !lstObjects.isEmpty()) {
                int numRows = lstObjects.size();
                result = new Object[numRows];

                for (int i = 0; i < numRows; i++) {
                    Object oObject = lstObjects.get(i);

                    result[i] = new TableRow(oObject);
                }
            } else {
            }

            return ((lstObjects == null) || lstObjects.isEmpty()) ? new Object[0] : result;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {

            if (theOldInput != null) {
                // do any required cleanup
            }

            rRelationshipObject = (Relationship)theNewInput;
            if (theNewInput != null) {
                theViewer.refresh();
            }
        }
    }

    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        class TableContentProvider implements IStructuredContentProvider {

            Relationship rRelationshipObject;
            private int iRoleType;

            public TableContentProvider( int iRoleType ) {

                this.iRoleType = iRoleType;
            }

            /**
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             */
            public void dispose() {
            }

            /**
             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
             */
            public Object[] getElements( Object theInputElement ) {

                rRelationshipObject = (Relationship)theInputElement;

                Object[] result = null;

                List lstObjects = new ArrayList();
                if (rRelationshipObject != null) {
                    if (iRoleType == SOURCE) {
                        lstObjects = getEditor().getSourceParticipants();
                    } else {
                        lstObjects = getEditor().getTargetParticipants();
                    }
                }

                if ((lstObjects != null) && !lstObjects.isEmpty()) {

                    int numRows = lstObjects.size();
                    result = new Object[numRows];

                    for (int i = 0; i < numRows; i++) {
                        Object oObject = lstObjects.get(i);

                        result[i] = new TableRow(oObject);
                    }
                }

                return ((lstObjects == null) || lstObjects.isEmpty()) ? new Object[0] : result;
            }

            /**
             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            public void inputChanged( Viewer theViewer,
                                      Object theOldInput,
                                      Object theNewInput ) {

                if (theOldInput != null) {
                    // do any required cleanup
                }

                rRelationshipObject = (Relationship)theNewInput;
                if (theNewInput != null) {
                    theViewer.refresh();
                }
            }

        }

        public Image getColumnImage( Object theElement,
                                     int theIndex ) {
            final WorkbenchLabelProvider workbenchProvider = new WorkbenchLabelProvider();
            Object oRealObject = ((TableRow)theElement).oObject;
            Image imgResult = null;

            switch (theIndex) {
                case 0:
                    if (oRealObject instanceof EObject) {
                        imgResult = ModelUtilities.getEMFLabelProvider().getImage(oRealObject);
                    } else if (oRealObject instanceof ModelWorkspaceItem) {

                        imgResult = workbenchProvider.getImage(((ModelWorkspaceItem)oRealObject).getResource());
                    }

                    break;

                case 1:
                    // no imamge
                    break;
            }
            return imgResult;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object theElement,
                                     int theColumnIndex ) {
            TableRow row = (TableRow)theElement;
            return row.getColumnText(theColumnIndex);
        }

    }

    class TableRow {

        Object oObject;

        public TableRow( Object oObject ) {

            this.oObject = oObject;
        }

        public String getColumnText( int theIndex ) {
            String sResult = "<unknown>"; //$NON-NLS-1$
            EObject eoObject;

            if (oObject instanceof EObject) {
                eoObject = (EObject)oObject;
            }

            if (oObject instanceof EObject) {

                eoObject = (EObject)oObject;

                switch (theIndex) {
                    case 0:
                        sResult = ModelUtilities.getEMFLabelProvider().getText(eoObject);

                        break;

                    case 1:
                        IPath path = ModelerCore.getModelEditor().getFullPathToParent(eoObject);
                        sResult = path.toString();
                        break;
                }

            } else {
                switch (theIndex) {
                    case 0:
                        sResult = oObject.toString();
                        break;

                    case 1:
                        sResult = oObject.toString();
                        break;
                }
            }

            return sResult;
        }

        public Object getValue( int theIndex ) {
            Object oResult = null;

            return oResult;
        }
    }
}
