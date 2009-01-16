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
package com.metamatrix.modeler.relationship.ui.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
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
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.properties.PropertySheetPage;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySheetPage;
import com.metamatrix.modeler.internal.ui.viewsupport.IContentFilter;
import com.metamatrix.modeler.internal.ui.viewsupport.MetamodelTreeViewer;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.PropertiesDialog;
import com.metamatrix.modeler.relationship.RelationshipPlugin;
import com.metamatrix.modeler.relationship.RelationshipTypeEditor;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.properties.RelationshipPropertyEditorFactory;
import com.metamatrix.ui.graphics.GlobalUiFontManager;
import com.metamatrix.ui.internal.eventsupport.SelectionProvider;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.IntegerSpinner;
import com.metamatrix.ui.table.TableSizeAdapter;

/**
 * RelationshipTypePanel
 */
public class RelationshipTypePanel extends Composite
    implements ISelectionChangedListener, SelectionListener, INotifyChangedListener, UiConstants, PluginConstants {

    private Composite pnlHeader; // numCols = 4

    private Composite pnlName;
    // row 1:
    Text txtRelationshipTypeName;

    private Group pnlRelationExpressions;
    private CLabel lblRelationshipExpressionText;

    // row 2:
    CLabel lblRoleANameAsSubject;
    Text txtAtoBExpression;
    CLabel lblRoleBNameAsObject;

    // row 3:
    CLabel lblRoleBNameAsSubject;
    Text txtBtoAExpression;
    CLabel lblRoleANameAsObject;

    private Composite pnlFlags;

    Button cbxDirected;
    Button cbxExclusive;
    Button cbxCrossModel;
    Button cbxAbstract;

    private Composite pnlTabOuterPanel;
    private TabFolder tabFolder;

    private TabItem tiRoleATab;
    private Composite pnlRoleAOuterPanel;

    private Composite pnlRoleAMainPanel;
    private Composite pnlRoleAHeader;
    Text txtRoleAName;
    Button btnBrowsePropertiesForRoleA;
    IntegerSpinner ispinLowerBoundA;
    IntegerSpinner ispinUpperBoundA;
    Button cbxOrderedA;
    Button cbxUniqueA;
    Button cbxNavigableA;

    private SashForm pnlRoleASplitter;
    // left half of sashform
    RolePanel rpIncludeTypesForRoleA;

    // right half of sashform
    RolePanel rpExcludeTypesForRoleA;

    private TabItem tiRoleBTab;
    private Composite pnlRoleBOuterPanel;

    private Composite pnlRoleBMainPanel;
    private Composite pnlRoleBHeader;
    Text txtRoleBName;
    Button btnBrowsePropertiesForRoleB;
    IntegerSpinner ispinLowerBoundB;
    IntegerSpinner ispinUpperBoundB;
    Button cbxOrderedB;
    Button cbxUniqueB;
    Button cbxNavigableB;

    private SashForm pnlRoleBSplitter;
    // left half of sashform
    RolePanel rpIncludeTypesForRoleB;

    // right half of sashform
    RolePanel rpExcludeTypesForRoleB;

    private TabItem tiPropertiesTab;
    private Composite pnlPropertiesOuterPanel;
    PropertySheetPage pspgProperties;

    private TabItem tiInheritanceTab;
    private Composite pnlInheritanceOuterPanel;
    private Composite pnlInheritanceMainPanel;

    private Composite pnlInheritanceHeaderPanel;
    Text txtSuperType2;
    Button btnBrowseForSuperType2;

    private Composite pnlInheritanceTablePanel;
    InheritanceTablePanel itpInheritanceTable;

    public static final int ROLE_A = 1;
    public static final int ROLE_B = 2;
    public static final int INCLUDE_TYPES = 11;
    public static final int EXCLUDE_TYPES = 12;

    public static final int BUTTON_GRID_STYLE = GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_CENTER;

    private static final String NAME = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.name.text"); //$NON-NLS-1$
    private static final String SUPERTYPE_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.superType.text"); //$NON-NLS-1$
    private static final String BROWSE_BUTTON_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.browseButton.text"); //$NON-NLS-1$

    private static final String RELATION_EXPRESSIONS_PANEL_TITLE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.relationExpressionsPanelTitle.text"); //$NON-NLS-1$        
    private static final String RELATION_EXPRESSIONS_PANEL_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.relationExpressionsPanelExplanation.text"); //$NON-NLS-1$       
    private static final String DEFAULT_ROLE_A_NAME = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.defaultRoleAName.text"); //$NON-NLS-1$
    private static final String DEFAULT_ROLE_B_NAME = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.defaultRoleBName.text"); //$NON-NLS-1$

    private static final String DIRECTED_CHECKBOX_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.directedCheckbox.text"); //$NON-NLS-1$
    private static final String EXCLUSIVE_CHECKBOX_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.exclusiveCheckbox.text"); //$NON-NLS-1$
    private static final String CROSS_MODEL_CHECKBOX_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.crossModelCheckbox.text"); //$NON-NLS-1$
    private static final String ABSTRACT_CHECKBOX_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.abstractCheckbox.text"); //$NON-NLS-1$
    private static final String ROLE_A_TITLE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.roleA.title"); //$NON-NLS-1$
    private static final String ROLE_B_TITLE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.roleB.title"); //$NON-NLS-1$
    private static final String PROPERTIES_TITLE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.properties.title"); //$NON-NLS-1$
    private static final String INHERITANCE_TITLE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.inheritance.title"); //$NON-NLS-1$

    final String INCLUDE_TYPES_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.includeTypes.text"); //$NON-NLS-1$
    final String EXCLUDE_TYPES_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.excludeTypes.text"); //$NON-NLS-1$

    private final String PROPERTIES_BUTTON_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.propertiesButton.text"); //$NON-NLS-1$
    private final String ROLE_A_TYPE_NAME_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.roleATypeName.text"); //$NON-NLS-1$
    private final String ROLE_B_TYPE_NAME_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.roleBTypeName.text"); //$NON-NLS-1$

    private final String ORDERED_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.ordered.text"); //$NON-NLS-1$          
    private final String UNIQUE_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.unique.text"); //$NON-NLS-1$
    private final String NAVIGABLE_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.navigable.text"); //$NON-NLS-1$

    private final String LOWER_BOUND_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.lowerBound.text"); //$NON-NLS-1$
    private final String UPPER_BOUND_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.upperBound.text"); //$NON-NLS-1$

    private String EMPTY_STRING = ""; //$NON-NLS-1$

    // this string has 10 spaces
    private String BLANKS_STRING = "          "; //$NON-NLS-1$

    private static final String EXPLORER_VIEW = com.metamatrix.modeler.ui.UiConstants.Extensions.Explorer.VIEW;

    RelationshipType rtRelationshipTypeObject;
    private Composite parent;

    private RelationshipTypeEditor reEditor;
    private IStatusListener islStatusListener;
    private IStatus isCurrentStatus;

    private int BOUND_MIN = -1;
    private int BOUND_MAX = 999;

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public RelationshipTypePanel( Composite parent,
                                  RelationshipType rel,
                                  IStatusListener islStatusListener ) {
        super(parent, SWT.NONE);
        this.parent = parent;
        this.rtRelationshipTypeObject = rel;
        this.islStatusListener = islStatusListener;

        init();
    }

    /**
     * Initialize the panel.
     */
    private void init() {
        // create the editor for the business object
        getEditor();

        // Create the Controls (Top) Panel
        createControl(this.parent);

        // Initialize the Button states
        setEnabledStates();

    }

    private RelationshipType createDefaultRelationshipType() {
        return RelationshipFactory.eINSTANCE.createRelationshipType();
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

    RelationshipTypeEditor getEditor() {
        if (reEditor == null) {
            if (rtRelationshipTypeObject == null) {
                rtRelationshipTypeObject = createDefaultRelationshipType();

                reEditor = RelationshipPlugin.createEditor(rtRelationshipTypeObject);

            } else {
                reEditor = RelationshipPlugin.createEditor(rtRelationshipTypeObject);
            }
        }

        return reEditor;
    }

    public void refreshFromBusinessObject() {

        // ==============================================
        // 1. main panel header
        // ==============================================

        // row 1:
        if (getEditor().getName() != null && !txtRelationshipTypeName.isDisposed()) {
            txtRelationshipTypeName.setText(getEditor().getName());
        } else {
            txtRelationshipTypeName.setText(EMPTY_STRING);
        }

        // row 2:
        if (getEditor().getSourceRole().getName() != null && !lblRoleANameAsSubject.isDisposed()) {
            lblRoleANameAsSubject.setText(getEditor().getSourceRole().getName());
        } else {
            lblRoleANameAsSubject.setText(DEFAULT_ROLE_A_NAME);
        }

        if (getEditor().getLabel() != null && !txtAtoBExpression.isDisposed()) {
            txtAtoBExpression.setText(getEditor().getLabel());
        } else {
            txtAtoBExpression.setText(EMPTY_STRING);
        }

        if (getEditor().getTargetRole().getName() != null && !lblRoleBNameAsObject.isDisposed()) {
            lblRoleBNameAsObject.setText(getEditor().getTargetRole().getName());
        } else {
            lblRoleBNameAsObject.setText(DEFAULT_ROLE_B_NAME);
        }

        // row 3:
        if (getEditor().getTargetRole().getName() != null && !lblRoleBNameAsSubject.isDisposed()) {
            lblRoleBNameAsSubject.setText(getEditor().getTargetRole().getName());
        } else {
            lblRoleBNameAsSubject.setText(DEFAULT_ROLE_B_NAME);
        }

        if (getEditor().getOppositeLabel() != null && !txtBtoAExpression.isDisposed()) {
            txtBtoAExpression.setText(getEditor().getOppositeLabel());
        } else {
            txtBtoAExpression.setText(EMPTY_STRING);
        }

        if (getEditor().getSourceRole().getName() != null && !lblRoleANameAsObject.isDisposed()) {
            lblRoleANameAsObject.setText(getEditor().getSourceRole().getName());
        } else {
            lblRoleANameAsObject.setText(DEFAULT_ROLE_A_NAME);
        }

        // private Composite pnlFlags;

        cbxDirected.setSelection(getEditor().isDirected());
        cbxExclusive.setSelection(getEditor().isExclusive());
        cbxCrossModel.setSelection(getEditor().isCrossModel());
        cbxAbstract.setSelection(getEditor().isAbstract());

        // ==============================================
        // 2. CTabItem tiRoleATab
        // ==============================================
        RelationshipRole rrRoleA = getEditor().getSourceRole();

        if (getEditor().getSourceRoleName() != null && !txtRoleAName.isDisposed()) {
            txtRoleAName.setText(getEditor().getSourceRoleName());
        } else {
            txtRoleAName.setText(EMPTY_STRING);
        }

        ispinLowerBoundA.setValue(getEditor().getLowerBound(rrRoleA));
        ispinUpperBoundA.setValue(getEditor().getUpperBound(rrRoleA));

        cbxOrderedA.setSelection(getEditor().isOrdered(rrRoleA));
        cbxUniqueA.setSelection(getEditor().isUnique(rrRoleA));
        cbxNavigableA.setSelection(getEditor().isNavigable(rrRoleA));

        // private Composite pnlRoleATables;

        rpIncludeTypesForRoleA.setInput(rtRelationshipTypeObject, ROLE_A, INCLUDE_TYPES);
        rpExcludeTypesForRoleA.setInput(rtRelationshipTypeObject, ROLE_A, EXCLUDE_TYPES);

        // ==============================================
        // 3. CTabItem tiRoleBTab
        // ==============================================
        RelationshipRole rrRoleB = getEditor().getTargetRole();

        if (getEditor().getTargetRoleName() != null) {
            txtRoleBName.setText(getEditor().getTargetRoleName());
        } else {
            txtRoleBName.setText(EMPTY_STRING);
        }

        ispinLowerBoundB.setValue(getEditor().getLowerBound(rrRoleB));
        ispinUpperBoundB.setValue(getEditor().getUpperBound(rrRoleB));
        cbxOrderedB.setSelection(getEditor().isOrdered(rrRoleB));
        cbxUniqueB.setSelection(getEditor().isUnique(rrRoleB));
        cbxNavigableB.setSelection(getEditor().isNavigable(rrRoleB));

        // private Composite pnlRoleBTables;
        rpIncludeTypesForRoleB.setInput(rtRelationshipTypeObject, ROLE_B, INCLUDE_TYPES);
        rpExcludeTypesForRoleB.setInput(rtRelationshipTypeObject, ROLE_B, EXCLUDE_TYPES);

        // ==============================================
        // 4. CTabItem tiFeaturesTab (not yet defined)
        // ==============================================

        // ==============================================
        // 5. CTabItem tiPropertiesTab
        // ==============================================
        pspgProperties.selectionChanged(null, new StructuredSelection(rtRelationshipTypeObject));

        // ==============================================
        // 6. CTabItem tiConstraintTab (not yet defined)
        // ==============================================

        // ==============================================
        // 8. CTabItem tiInheritanceTab
        // ==============================================

        // private CTabItem tiInheritanceTab;
        if (getEditor().getSupertype() != null && getEditor().getSupertype().getName() != null) {
            txtSuperType2.setText(getEditor().getSupertype().getName());
        } else {
            txtSuperType2.setText(EMPTY_STRING);
        }

        // pnlInheritanceTablePanel;
        itpInheritanceTable.setInput(rtRelationshipTypeObject);

        // 5. reset button states
        setEnabledStates();

        // 7. redo the layout
        forceRelayout();

    }

    void forceRelayout() {
        pnlTabOuterPanel.setRedraw(true);

        int iCount = 0;
        Composite pnlParent = parent;
        Composite pnlSecondLastParent = null;

        // try to find a SashForm ancestor
        while (true) {

            // quit when you run out of parents or do too many loops
            if (pnlParent == null || iCount > 100) {
                break;
            }

            // quit when you find the SashForm
            if (pnlParent instanceof SashForm || pnlParent instanceof Shell) {
                break;
            }
            pnlSecondLastParent = pnlParent;

            // get the next parent
            pnlParent = pnlParent.getParent();

            iCount++;
        }

        // if you found a SashForm ancestor, do a pack:
        if (pnlParent != null) {

            if (pnlParent instanceof SashForm) {

                if (pnlSecondLastParent != null && pnlSecondLastParent instanceof ViewForm) {
                    try {
                        pnlSecondLastParent.pack();
                    } catch (Exception err) {
                        ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
                    }
                } else {
                    pnlParent.pack();
                }

                wiggle((SashForm)pnlParent);
            } else {

                if (pnlSecondLastParent != null) {
                    pnlSecondLastParent.pack();
                }
                pnlParent.pack();
            }
        }

    }

    private void wiggle( SashForm sf ) {
        int iWiggleFactor = 1;

        int[] weights = sf.getWeights();
        int iCurrentFirstWeight = weights[0];
        weights[0] = iCurrentFirstWeight + iWiggleFactor;
        sf.setWeights(weights);

        weights[0] = iCurrentFirstWeight - iWiggleFactor;
        sf.setWeights(weights);
    }

    void validate() {
        // validate
        isCurrentStatus = getEditor().validate();

        islStatusListener.setStatus(isCurrentStatus);

    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {

        //         
        // 1. layout the SashForm
        //          
        GridLayout gridLayout = new GridLayout();
        this.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

        this.setLayoutData(gridData);

        // 2. create the header
        createHeaderPanel(this);

        // 3. create the tabs panel
        createTabsPanel(this);

        // 4. establish listening
        registerListeners();
    }

    private void createHeaderPanel( Composite parent ) {

        // create the panel
        pnlHeader = new Composite(parent, SWT.NONE);
        // pnlHeader.setBackground( ColorConstants.yellow );
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.marginWidth = 15;
        pnlHeader.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        pnlHeader.setLayoutData(gridData);
        // pnlHeader.setBackground( ColorConstants.red );

        // ==================
        // Name panel
        // ==================

        pnlName = new Composite(pnlHeader, SWT.NONE);
        GridLayout gridLayout331a = new GridLayout();
        gridLayout331a.numColumns = 5;
        gridLayout331a.marginWidth = 45;
        gridLayout331a.marginHeight = 5;
        gridLayout331a.horizontalSpacing = 5;

        pnlName.setLayout(gridLayout331a);

        GridData gridData331a = new GridData(GridData.FILL_HORIZONTAL);
        gridData331a.grabExcessHorizontalSpace = true;
        pnlName.setLayoutData(gridData331a);
        // pnlName.setBackground( ColorConstants.blue );

        // ================
        // Row 1:
        // ================

        // 'Name' label
        // lblRelationshipTypeName =
        WidgetFactory.createLabel(pnlName, NAME);

        // 'Name' textfield
        txtRelationshipTypeName = WidgetFactory.createTextField(pnlName, GridData.FILL_HORIZONTAL, this.EMPTY_STRING);
        GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
        gridData1.horizontalSpan = 3;
        txtRelationshipTypeName.setLayoutData(gridData1);

        // 'filler' label
        // lblFiller1 =
        WidgetFactory.createLabel(pnlName, BLANKS_STRING);

        // ==============================
        // Relation Expressions panel
        // ==============================
        // 
        pnlRelationExpressions = new Group(pnlHeader, SWT.NONE);
        pnlRelationExpressions.setText(RELATION_EXPRESSIONS_PANEL_TITLE);
        GridLayout gridLayout33 = new GridLayout();
        gridLayout33.numColumns = 5;
        gridLayout33.marginWidth = 25;
        gridLayout33.marginHeight = 5;
        pnlRelationExpressions.setLayout(gridLayout33);

        GridData gridData33 = new GridData(GridData.FILL_HORIZONTAL);
        pnlRelationExpressions.setLayoutData(gridData33);
        // pnlRelationExpressions.setBackground( ColorConstants.yellow );

        // ================
        // Row 2:
        // ================

        lblRelationshipExpressionText = WidgetFactory.createLabel(pnlRelationExpressions, RELATION_EXPRESSIONS_PANEL_TEXT);
        GridData gridData122bb = new GridData(GridData.FILL_HORIZONTAL);
        gridData122bb.horizontalSpan = 5;
        lblRelationshipExpressionText.setLayoutData(gridData122bb);

        // 'A as subject' label
        lblRoleANameAsSubject = WidgetFactory.createLabel(pnlRelationExpressions, DEFAULT_ROLE_A_NAME);
        lblRoleANameAsSubject.setFont(getBoldFont(lblRoleANameAsSubject.getFont()));

        // I could not get 'right justify' to work after a change to a lebel's text. might try
        // again later...
        GridData gridData12 = new GridData(GridData.HORIZONTAL_ALIGN_END);
        lblRoleANameAsSubject.setLayoutData(gridData12);

        txtAtoBExpression = WidgetFactory.createTextField(pnlRelationExpressions, GridData.FILL_HORIZONTAL, this.EMPTY_STRING);

        GridData gridData122 = new GridData(GridData.FILL_HORIZONTAL);
        gridData122.horizontalSpan = 3;
        txtAtoBExpression.setLayoutData(gridData122);

        // 'B as object' label
        lblRoleBNameAsObject = WidgetFactory.createLabel(pnlRelationExpressions, DEFAULT_ROLE_B_NAME);
        lblRoleBNameAsObject.setFont(getBoldFont(lblRoleBNameAsObject.getFont()));

        // ================
        // Row 3:
        // ================
        // 'name' label
        // 'A as subject' label
        lblRoleBNameAsSubject = WidgetFactory.createLabel(pnlRelationExpressions, DEFAULT_ROLE_B_NAME);
        lblRoleBNameAsSubject.setFont(getBoldFont(lblRoleBNameAsSubject.getFont()));
        // I could not get 'right justify' to work after a change to a lebel's text. might try
        // again later...
        GridData gridData123 = new GridData(GridData.HORIZONTAL_ALIGN_END);
        lblRoleBNameAsSubject.setLayoutData(gridData123);

        txtBtoAExpression = WidgetFactory.createTextField(pnlRelationExpressions, GridData.FILL_HORIZONTAL, this.EMPTY_STRING);

        GridData gridData22 = new GridData(GridData.FILL_HORIZONTAL);
        gridData22.horizontalSpan = 3;
        txtBtoAExpression.setLayoutData(gridData22);

        // 'B as object' label
        lblRoleANameAsObject = WidgetFactory.createLabel(pnlRelationExpressions, DEFAULT_ROLE_A_NAME);
        lblRoleANameAsObject.setFont(getBoldFont(lblRoleANameAsObject.getFont()));

        // ==================
        // Flags panel
        // ==================
        pnlFlags = new Composite(pnlHeader, SWT.NONE);
        GridLayout gridLayout331 = new GridLayout();
        gridLayout331.numColumns = 4;
        gridLayout331.marginWidth = 45;
        gridLayout331.marginHeight = 0;
        gridLayout331.horizontalSpacing = 15;
        // gridLayout331.makeColumnsEqualWidth = true;

        pnlFlags.setLayout(gridLayout331);

        GridData gridData331 = new GridData(GridData.FILL_HORIZONTAL);
        gridData331.horizontalAlignment = GridData.CENTER;
        pnlFlags.setLayoutData(gridData331);

        // 'directed' checkbox
        cbxDirected = WidgetFactory.createCheckBox(pnlFlags, DIRECTED_CHECKBOX_TEXT);
        // lblFiller1 = WidgetFactory.createLabel( pnlFlags, FOUR_BLANKS_STRING );

        // 'exclusive' checkbox
        cbxExclusive = WidgetFactory.createCheckBox(pnlFlags, EXCLUSIVE_CHECKBOX_TEXT);

        // lblFiller2 = WidgetFactory.createLabel( pnlFlags, FOUR_BLANKS_STRING );

        // 'crossmodel' checkbox
        cbxCrossModel = WidgetFactory.createCheckBox(pnlFlags, CROSS_MODEL_CHECKBOX_TEXT);

        // lblFiller3 = WidgetFactory.createLabel( pnlFlags, FOUR_BLANKS_STRING );

        // 'exclusive' checkbox
        cbxAbstract = WidgetFactory.createCheckBox(pnlFlags, ABSTRACT_CHECKBOX_TEXT);

    }

    private void createTabsPanel( Composite parent ) {
        pnlTabOuterPanel = new Composite(parent, SWT.NONE);
        // pnlTabOuterPanel.setBackground( ColorConstants.green );
        GridLayout gridLayout = new GridLayout();
        pnlTabOuterPanel.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        pnlTabOuterPanel.setLayoutData(gridData);

        // create Tab Folder
        tabFolder = new TabFolder(pnlTabOuterPanel, SWT.TOP);

        GridData gridData3 = new GridData(GridData.FILL_BOTH);
        tabFolder.setLayoutData(gridData3);

        // Role A Tab
        createRoleATab(tabFolder);
        pnlRoleAOuterPanel = new Composite(tabFolder, SWT.NONE);

        GridLayout gridLayout2 = new GridLayout();
        pnlRoleAOuterPanel.setLayout(gridLayout2);
        gridLayout2.numColumns = 1;

        createRoleAPanel(pnlRoleAOuterPanel);

        tiRoleATab.setControl(pnlRoleAOuterPanel);

        // Role B Tab
        createRoleBTab(tabFolder);
        pnlRoleBOuterPanel = new Composite(tabFolder, SWT.NONE);
        createRoleBPanel(pnlRoleBOuterPanel);
        GridLayout gridLayout23 = new GridLayout();
        pnlRoleBOuterPanel.setLayout(gridLayout23);
        gridLayout23.numColumns = 1;
        tiRoleBTab.setControl(pnlRoleBOuterPanel);

        // inheritance tab
        createInheritanceTab(tabFolder);
        pnlInheritanceOuterPanel = new Composite(tabFolder, SWT.NONE);
        GridLayout gridLayout4 = new GridLayout();
        pnlInheritanceOuterPanel.setLayout(gridLayout4);
        gridLayout4.numColumns = 1;

        createInheritancePanel(pnlInheritanceOuterPanel);
        tiInheritanceTab.setControl(pnlInheritanceOuterPanel);

        // Properties Tab
        createPropertiesTab(tabFolder);
        pnlPropertiesOuterPanel = new Composite(tabFolder, SWT.NONE);
        createPropertiesPanel(pnlPropertiesOuterPanel);
        GridLayout gridLayout214 = new GridLayout();
        pnlPropertiesOuterPanel.setLayout(gridLayout214);
        gridLayout214.numColumns = 1;
        tiPropertiesTab.setControl(pnlPropertiesOuterPanel);

        // default to the Roles Tab
        tabFolder.setSelection(0);
        tabFolder.setVisible(true);
    }

    private void createRoleATab( TabFolder parent ) {
        tiRoleATab = new TabItem(parent, SWT.NONE);
        tiRoleATab.setText(ROLE_A_TITLE);
        tiRoleATab.setToolTipText(ROLE_A_TITLE);
    }

    private void createRoleBTab( TabFolder parent ) {
        tiRoleBTab = new TabItem(parent, SWT.NONE);
        tiRoleBTab.setText(ROLE_B_TITLE);
        tiRoleBTab.setToolTipText(ROLE_B_TITLE);
    }

    private void createPropertiesTab( TabFolder parent ) {
        tiPropertiesTab = new TabItem(parent, SWT.NONE);
        tiPropertiesTab.setText(PROPERTIES_TITLE);
        tiPropertiesTab.setToolTipText(PROPERTIES_TITLE);
    }

    private void createInheritanceTab( TabFolder parent ) {
        tiInheritanceTab = new TabItem(parent, SWT.NONE);
        tiInheritanceTab.setText(INHERITANCE_TITLE);
        tiInheritanceTab.setToolTipText(INHERITANCE_TITLE);
    }

    private void createRoleAPanel( Composite parent ) {
        pnlRoleAMainPanel = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();

        GridData gridData = new GridData(GridData.FILL_BOTH);
        pnlRoleAMainPanel.setLayoutData(gridData);

        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.numColumns = 1;
        // gridLayout.marginWidth = 15;
        pnlRoleAMainPanel.setLayout(gridLayout);

        // pnlRoleAMainPanel.setBackground( ColorConstants.red );

        createRoleAHeaderPanel(pnlRoleAMainPanel);
        createRoleATablesPanel(pnlRoleAMainPanel);
    }

    private void createRoleAHeaderPanel( Composite parent ) {
        // create the panel
        pnlRoleAHeader = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 25;
        pnlRoleAHeader.setLayout(gridLayout);
        // pnlRoleAHeader.setBackground( ColorConstants.yellow );
        pnlRoleAHeader.setVisible(true);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        pnlRoleAHeader.setLayoutData(gridData);

        Composite pnlRoleAHeaderRow1 = new Composite(pnlRoleAHeader, SWT.NONE);
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 9;
        pnlRoleAHeaderRow1.setLayout(gridLayout2);
        GridData gridData32 = new GridData(GridData.FILL_HORIZONTAL);
        pnlRoleAHeaderRow1.setLayoutData(gridData32);

        Composite pnlRoleAHeaderRow2 = new Composite(pnlRoleAHeader, SWT.NONE);
        GridLayout gridLayout3 = new GridLayout();
        gridLayout3.numColumns = 4;
        gridLayout3.horizontalSpacing = 15;
        pnlRoleAHeaderRow2.setLayout(gridLayout3);
        GridData gridData33 = new GridData(GridData.FILL_HORIZONTAL);
        gridData33.horizontalAlignment = GridData.CENTER;
        pnlRoleAHeaderRow2.setLayoutData(gridData33);

        // 'name' label
        WidgetFactory.createLabel(pnlRoleAHeaderRow1, ROLE_A_TYPE_NAME_TEXT);

        // 'name' textfield
        txtRoleAName = WidgetFactory.createTextField(pnlRoleAHeaderRow1, GridData.FILL_HORIZONTAL, 4);

        // 'lower bound' label
        WidgetFactory.createLabel(pnlRoleAHeaderRow1, LOWER_BOUND_TEXT);

        // 'lower bound' textfield
        ispinLowerBoundA = new IntegerSpinner(pnlRoleAHeaderRow1, BOUND_MIN, BOUND_MAX);
        ispinLowerBoundA.setWrap(false);

        // 'upper bound' label
        WidgetFactory.createLabel(pnlRoleAHeaderRow1, UPPER_BOUND_TEXT);

        // 'upper bound' textfield
        ispinUpperBoundA = new IntegerSpinner(pnlRoleAHeaderRow1, BOUND_MIN, BOUND_MAX);
        ispinUpperBoundA.setWrap(false);

        // 'ordered' checkbox
        cbxOrderedA = WidgetFactory.createCheckBox(pnlRoleAHeaderRow2, ORDERED_TEXT);

        // 'unique' checkbox
        cbxUniqueA = WidgetFactory.createCheckBox(pnlRoleAHeaderRow2, UNIQUE_TEXT);

        // 'navigable' checkbox
        cbxNavigableA = WidgetFactory.createCheckBox(pnlRoleAHeaderRow2, NAVIGABLE_TEXT);

        // 'browse properties' button
        btnBrowsePropertiesForRoleA = WidgetFactory.createButton(pnlRoleAHeaderRow2, PROPERTIES_BUTTON_TEXT, BUTTON_GRID_STYLE);

    }

    private void createRoleATablesPanel( Composite parent ) {
        /*
         * rethink this: the dual table 'roles' panel will contain the 
         * include table and the exclude table for ONE role.
         */

        // create the panel
        pnlRoleASplitter = new SashForm(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        pnlRoleASplitter.setLayout(gridLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        pnlRoleASplitter.setLayoutData(gridData);

        // get role
        rpIncludeTypesForRoleA = new RolePanel(pnlRoleASplitter, rtRelationshipTypeObject, ROLE_A, INCLUDE_TYPES);

        rpIncludeTypesForRoleA.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {

                // test for selection first; no action on deselect?
                if (rpIncludeTypesForRoleA.hasSelection()) {
                    handleLeftRoleTableSelection();
                }
            }
        });

        rpExcludeTypesForRoleA = new RolePanel(pnlRoleASplitter, rtRelationshipTypeObject, ROLE_A, EXCLUDE_TYPES);

        rpExcludeTypesForRoleA.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {

                // test for selection first; no action on deselect?
                if (rpExcludeTypesForRoleA.hasSelection()) {
                    handleRightRoleTableSelection();
                }
            }
        });

        rpIncludeTypesForRoleA.setOppositePanel(rpExcludeTypesForRoleA);
        rpExcludeTypesForRoleA.setOppositePanel(rpIncludeTypesForRoleA);
    }

    private void createRoleBPanel( Composite parent ) {
        pnlRoleBMainPanel = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();

        GridData gridData = new GridData(GridData.FILL_BOTH);
        pnlRoleBMainPanel.setLayoutData(gridData);

        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.numColumns = 1;
        pnlRoleBMainPanel.setLayout(gridLayout);

        createRoleBHeaderPanel(pnlRoleBMainPanel);
        createRoleBTablesPanel(pnlRoleBMainPanel);
    }

    private void createRoleBHeaderPanel( Composite parent ) {

        // create the panel
        pnlRoleBHeader = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 25;
        // gridLayout.horizontalSpacing = 10;
        pnlRoleBHeader.setLayout(gridLayout);
        // pnlRoleBHeader.setBackground( ColorConstants.yellow );
        pnlRoleBHeader.setVisible(true);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        pnlRoleBHeader.setLayoutData(gridData);

        Composite pnlRoleBHeaderRow1 = new Composite(pnlRoleBHeader, SWT.NONE);
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 9;
        // gridLayout2.marginHeight = 5;
        // gridLayout2.marginWidth = 25;
        pnlRoleBHeaderRow1.setLayout(gridLayout2);
        GridData gridData32 = new GridData(GridData.FILL_HORIZONTAL);
        // gridData32.horizontalAlignment = GridData.CENTER;
        pnlRoleBHeaderRow1.setLayoutData(gridData32);

        Composite pnlRoleBHeaderRow2 = new Composite(pnlRoleBHeader, SWT.NONE);
        GridLayout gridLayout3 = new GridLayout();
        gridLayout3.numColumns = 4;
        gridLayout3.horizontalSpacing = 15;
        // gridLayout3.marginHeight = 5;
        // gridLayout3.marginWidth = 25;
        pnlRoleBHeaderRow2.setLayout(gridLayout3);
        GridData gridData33 = new GridData(GridData.FILL_HORIZONTAL);

        gridData33.horizontalAlignment = GridData.CENTER;
        pnlRoleBHeaderRow2.setLayoutData(gridData33);

        // 'name' label
        // lblRoleBName =
        WidgetFactory.createLabel(pnlRoleBHeaderRow1, ROLE_B_TYPE_NAME_TEXT);

        // 'name' textfield
        txtRoleBName = WidgetFactory.createTextField(pnlRoleBHeaderRow1, GridData.FILL_HORIZONTAL, 4);

        // 'lower bound' label
        WidgetFactory.createLabel(pnlRoleBHeaderRow1, LOWER_BOUND_TEXT);

        // 'lower bound' textfield
        ispinLowerBoundB = new IntegerSpinner(pnlRoleBHeaderRow1, -1, 999);
        ispinLowerBoundB.setWrap(false);

        // 'upper bound' label
        WidgetFactory.createLabel(pnlRoleBHeaderRow1, UPPER_BOUND_TEXT);

        // 'upper bound' textfield
        ispinUpperBoundB = new IntegerSpinner(pnlRoleBHeaderRow1, -1, 999);
        ispinUpperBoundB.setWrap(false);

        // 'ordered' checkbox
        cbxOrderedB = WidgetFactory.createCheckBox(pnlRoleBHeaderRow2, ORDERED_TEXT);

        // 'unique' checkbox
        cbxUniqueB = WidgetFactory.createCheckBox(pnlRoleBHeaderRow2, UNIQUE_TEXT);

        // 'navigable' checkbox
        cbxNavigableB = WidgetFactory.createCheckBox(pnlRoleBHeaderRow2, NAVIGABLE_TEXT);

        // 'browse properties' button
        btnBrowsePropertiesForRoleB = WidgetFactory.createButton(pnlRoleBHeaderRow2, PROPERTIES_BUTTON_TEXT, BUTTON_GRID_STYLE);

    }

    private void createRoleBTablesPanel( Composite parent ) {
        /*
         * rethink this: the dual table 'roles' panel will contain the 
         * include table and the exclude table for ONE role.  'A' is source; 'B' is target
         */

        // create the panel
        pnlRoleBSplitter = new SashForm(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        pnlRoleBSplitter.setLayout(gridLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        pnlRoleBSplitter.setLayoutData(gridData);

        // get role
        rpIncludeTypesForRoleB = new RolePanel(pnlRoleBSplitter, rtRelationshipTypeObject, ROLE_B, INCLUDE_TYPES);

        rpIncludeTypesForRoleB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {

                // test for selection first; no action on deselect?
                if (rpIncludeTypesForRoleB.hasSelection()) {
                    handleLeftRoleTableSelection();
                }
            }
        });

        rpExcludeTypesForRoleB = new RolePanel(pnlRoleBSplitter, rtRelationshipTypeObject, ROLE_B, EXCLUDE_TYPES);

        rpExcludeTypesForRoleB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {

                // test for selection first; no action on deselect?
                if (rpExcludeTypesForRoleB.hasSelection()) {
                    handleRightRoleTableSelection();
                }
            }
        });

        rpIncludeTypesForRoleB.setOppositePanel(rpExcludeTypesForRoleB);
        rpExcludeTypesForRoleB.setOppositePanel(rpIncludeTypesForRoleB);
    }

    private void createPropertiesPanel( Composite parent ) {

        pspgProperties = new ModelObjectPropertySheetPage();
        pspgProperties.setPropertySourceProvider(ModelUtilities.getPropertySourceProvider());
        pspgProperties.createControl(parent);
        Control result = pspgProperties.getControl();
        GridData gd = new GridData(GridData.FILL_BOTH);
        result.setLayoutData(gd);

    }

    private void createInheritancePanel( Composite parent ) {

        pnlInheritanceMainPanel = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();

        GridData gridData = new GridData(GridData.FILL_BOTH);
        pnlInheritanceMainPanel.setLayoutData(gridData);

        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.numColumns = 1;
        pnlInheritanceMainPanel.setLayout(gridLayout);

        createInheritanceHeaderPanel(pnlInheritanceMainPanel);
        createInheritanceTablePanel(pnlInheritanceMainPanel);
    }

    private void createInheritanceHeaderPanel( Composite parent ) {
        // create the panel

        pnlInheritanceHeaderPanel = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.marginHeight = 10;
        gridLayout.marginWidth = 5;
        pnlInheritanceHeaderPanel.setLayout(gridLayout);
        // pnlRoleAHeader.setBackground( ColorConstants.yellow );
        pnlInheritanceHeaderPanel.setVisible(true);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        pnlInheritanceHeaderPanel.setLayoutData(gridData);

        WidgetFactory.createLabel(pnlInheritanceHeaderPanel, SUPERTYPE_TEXT);

        txtSuperType2 = WidgetFactory.createTextField(pnlInheritanceHeaderPanel, GridData.FILL_HORIZONTAL);
        txtSuperType2.setEditable(false);

        btnBrowseForSuperType2 = WidgetFactory.createButton(pnlInheritanceHeaderPanel, BROWSE_BUTTON_TEXT, BUTTON_GRID_STYLE);
        GridData gridData3b = new GridData();
        gridData3b.horizontalSpan = 1;
        btnBrowseForSuperType2.setLayoutData(gridData3b);

    }

    private void createInheritanceTablePanel( Composite parent ) {

        /*
         * rethink this: the dual table 'roles' panel will contain the 
         * include table and the exclude table for ONE role.
         */

        // create the panel
        pnlInheritanceTablePanel = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        pnlInheritanceTablePanel.setLayout(gridLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        pnlInheritanceTablePanel.setLayoutData(gridData);

        // get role
        itpInheritanceTable = new InheritanceTablePanel(pnlInheritanceTablePanel, rtRelationshipTypeObject);

        itpInheritanceTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {

                // test for selection first; no action on deselect?
                if (rpIncludeTypesForRoleA.hasSelection()) {
                    handleLeftRoleTableSelection();
                }
            }
        });

    }

    private Font getBoldFont( Font f ) {

        FontData data = f.getFontData()[0];
        data.setStyle(SWT.BOLD);
        Font fNewFont = GlobalUiFontManager.getFont(data);

        return fNewFont;
    }

    private void registerListeners() {

        // ==============================================
        // 1. main panel header
        // ==============================================

        // header:
        txtRelationshipTypeName.addFocusListener(new FocusListener() {
            public void focusGained( final FocusEvent event ) {
                // no action
            }

            public void focusLost( final FocusEvent event ) {

                // update the editor when user leaves.
                getEditor().setName(txtRelationshipTypeName.getText());
                validate();
            }
        });

        txtAtoBExpression.addFocusListener(new FocusListener() {
            public void focusGained( final FocusEvent event ) {
                // no action
            }

            public void focusLost( final FocusEvent event ) {

                // update the editor when user leaves.
                getEditor().setLabel(txtAtoBExpression.getText());
                validate();
            }
        });

        txtBtoAExpression.addFocusListener(new FocusListener() {
            public void focusGained( final FocusEvent event ) {
                // no action
            }

            public void focusLost( final FocusEvent event ) {

                // update the editor when user leaves.
                getEditor().setOppositeLabel(txtBtoAExpression.getText());
                validate();
            }
        });

        cbxDirected.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setDirected(cbxDirected.getSelection());
                validate();
            }
        });

        cbxExclusive.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setExclusive(cbxExclusive.getSelection());
                validate();
            }
        });

        cbxCrossModel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setCrossModel(cbxCrossModel.getSelection());
                validate();
            }
        });

        cbxAbstract.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setAbstract(cbxAbstract.getSelection());
                validate();
            }
        });

        // ==============================================
        // 2. CTabItem tiRoleATab
        // ==============================================

        // when the Role A Name changes, also update these:
        // lblRoleANameAsSubject
        // lblRoleANameAsObject
        txtRoleAName.addFocusListener(new FocusListener() {
            public void focusGained( final FocusEvent event ) {
                // no action
            }

            public void focusLost( final FocusEvent event ) {

                // update the editor when user leaves.
                getEditor().setRoleName(getEditor().getSourceRole(), txtRoleAName.getText());

                // I could not get 'right justify' to work after a change to a lebel's text. might try
                // again later...

                lblRoleANameAsObject.setText(getRoleAName());
                lblRoleANameAsSubject.setText(getRoleAName());
                forceRelayout();

                validate();
            }
        });

        txtRoleAName.addKeyListener(new KeyListener() {

            public void keyPressed( final KeyEvent event ) {
            }

            public void keyReleased( final KeyEvent event ) {

                // update the other fields immediately, but only update the editor on focuslost
                lblRoleANameAsObject.setText(getRoleAName());
                lblRoleANameAsSubject.setText(getRoleAName());
            }
        });

        ispinLowerBoundA.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setLowerBound(getEditor().getSourceRole(), ispinLowerBoundA.getIntegerValue());
                validate();
            }
        });

        ispinUpperBoundA.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setUpperBound(getEditor().getSourceRole(), ispinUpperBoundA.getIntegerValue());
                validate();
            }
        });

        cbxOrderedA.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setOrdered(getEditor().getSourceRole(), cbxOrderedA.getSelection());
                validate();
            }
        });

        cbxUniqueA.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setUnique(getEditor().getSourceRole(), cbxUniqueA.getSelection());
                validate();
            }
        });

        cbxNavigableA.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setNavigable(getEditor().getSourceRole(), cbxNavigableA.getSelection());
                validate();
            }
        });

        btnBrowsePropertiesForRoleA.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                PropertiesDialog dlg = new PropertiesDialog(getEditor().getSourceRole(), null);
                dlg.open();
            }
        });

        // ==============================================
        // 3. CTabItem tiRoleBTab
        // ==============================================
        // when the Role A Name changes, also update these:
        // lblRoleANameAsSubject
        // lblRoleANameAsObject

        txtRoleBName.addFocusListener(new FocusListener() {
            public void focusGained( final FocusEvent event ) {
                // no action
            }

            public void focusLost( final FocusEvent event ) {

                // update the editor when user leaves.
                getEditor().setRoleName(getEditor().getTargetRole(), txtRoleBName.getText());
                // I could not get 'right justify' to work after a change to a lebel's text. might try
                // again later...
                lblRoleBNameAsObject.setText(getRoleBName());

                lblRoleBNameAsSubject.setText(getRoleBName());

                forceRelayout();

                validate();
            }
        });

        txtRoleBName.addKeyListener(new KeyListener() {

            public void keyPressed( final KeyEvent event ) {
            }

            public void keyReleased( final KeyEvent event ) {

                // update the other fields immediately, but only update the editor on focuslost
                // I could not get 'right justify' to work after a change to a lebel's text. might try
                // again later...
                lblRoleBNameAsObject.setText(getRoleBName());
                lblRoleBNameAsSubject.setText(getRoleBName());
            }
        });

        ispinLowerBoundB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setLowerBound(getEditor().getTargetRole(), ispinLowerBoundB.getIntegerValue());
                validate();
            }
        });

        ispinUpperBoundB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setUpperBound(getEditor().getTargetRole(), ispinUpperBoundB.getIntegerValue());
                validate();
            }
        });

        cbxOrderedB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setOrdered(getEditor().getTargetRole(), cbxOrderedB.getSelection());
                validate();
            }
        });

        cbxUniqueB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setUnique(getEditor().getTargetRole(), cbxUniqueB.getSelection());
                validate();
            }
        });

        cbxNavigableB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                getEditor().setNavigable(getEditor().getTargetRole(), cbxNavigableB.getSelection());
                validate();
            }
        });

        btnBrowsePropertiesForRoleB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                PropertiesDialog dlg = new PropertiesDialog(getEditor().getTargetRole(), null);
                dlg.open();
            }
        });

        // ==============================================
        // 8. CTabItem tiInheritanceTab
        // ==============================================

        // txtSuperType2 (not editable)
        // note: supertype has its own code for updating, based on the browse button.

        btnBrowseForSuperType2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {

                handleBrowseTypeButtonPressed();
            }
        });

    }

    String getRoleAName() {

        String sName = txtRoleAName.getText();

        if (sName.trim().equals(EMPTY_STRING)) {
            sName = DEFAULT_ROLE_A_NAME;
        }

        return sName;
    }

    String getRoleBName() {

        String sName = txtRoleBName.getText();

        if (sName.trim().equals(EMPTY_STRING)) {
            sName = DEFAULT_ROLE_B_NAME;
        }

        return sName;
    }

    void handleLeftRoleTableSelection() {

        rpExcludeTypesForRoleB.clearSelection();
        setEnabledStates();
    }

    void handleRightRoleTableSelection() {
        rpIncludeTypesForRoleA.clearSelection();
        setEnabledStates();
    }

    void handleBrowseTypeButtonPressed() {

        // ==================================
        // launch Relationship Type chooser
        // ==================================

        // jhTODO: discuss with Randall how to write a custom ITreeContentProvider that will include
        // the Builtin Relationship Types as well as the rest of the workspace tree,
        // then rework this to use the dialog constructor that takes a content provider.

        SelectionDialog sdDialog = RelationshipPropertyEditorFactory.createRelationshipTypeSupertypeSelector(UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                                                                                                             this.rtRelationshipTypeObject.getSuperType(),
                                                                                                             this.rtRelationshipTypeObject);
        sdDialog.open();

        if (sdDialog.getReturnCode() == Window.OK) {
            Object[] oSelectedObjects = sdDialog.getResult();

            // add the selected RelationshipType to this Relationship
            if (oSelectedObjects.length > 0) {
                RelationshipType rt = (RelationshipType)oSelectedObjects[0];
                // update the Relationship
                getEditor().setSupertype(rt);
                txtSuperType2.setText(getEditor().getSupertype().getName());
                validate();
            }
        }

    }

    /**
     * Set the enabled/disabled states of the Buttons.
     */
    private void setEnabledStates() {

        if (txtRelationshipTypeName != null && !txtRelationshipTypeName.isDisposed()) {

            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    boolean bEnabledState = (!ModelObjectUtilities.isReadOnly(rtRelationshipTypeObject));

                    txtRelationshipTypeName.setEnabled(bEnabledState);
                    txtAtoBExpression.setEnabled(bEnabledState);
                    txtBtoAExpression.setEnabled(bEnabledState);
                    cbxDirected.setEnabled(bEnabledState);
                    cbxExclusive.setEnabled(bEnabledState);
                    cbxCrossModel.setEnabled(bEnabledState);
                    cbxAbstract.setEnabled(bEnabledState);

                    txtRoleAName.setEnabled(bEnabledState);
                    btnBrowsePropertiesForRoleA.setEnabled(bEnabledState);
                    ispinLowerBoundA.setEnabled(bEnabledState);
                    ispinUpperBoundA.setEnabled(bEnabledState);
                    cbxOrderedA.setEnabled(bEnabledState);
                    cbxUniqueA.setEnabled(bEnabledState);
                    cbxNavigableA.setEnabled(bEnabledState);

                    rpIncludeTypesForRoleA.setButtonStates(bEnabledState);
                    rpExcludeTypesForRoleA.setButtonStates(bEnabledState);

                    txtRoleBName.setEnabled(bEnabledState);
                    btnBrowsePropertiesForRoleB.setEnabled(bEnabledState);
                    ispinLowerBoundB.setEnabled(bEnabledState);
                    ispinUpperBoundB.setEnabled(bEnabledState);
                    cbxOrderedB.setEnabled(bEnabledState);
                    cbxUniqueB.setEnabled(bEnabledState);
                    cbxNavigableB.setEnabled(bEnabledState);

                    rpIncludeTypesForRoleB.setButtonStates(bEnabledState);
                    rpExcludeTypesForRoleB.setButtonStates(bEnabledState);

                    pspgProperties.getControl().setEnabled(bEnabledState);
                    txtSuperType2.setEnabled(bEnabledState);
                    btnBrowseForSuperType2.setEnabled(bEnabledState);

                    itpInheritanceTable.setButtonStates(bEnabledState);

                }
            });
        }
    }

    public void selectionChanged( SelectionChangedEvent event ) {
        setEnabledStates();
    }

    public void setBusinessObject( RelationshipType rtRelationshipTypeObject ) {
        this.rtRelationshipTypeObject = rtRelationshipTypeObject;

        // recreate the editor
        reEditor = RelationshipPlugin.createEditor(rtRelationshipTypeObject);

        // listen to EObject changes
        ModelUtilities.addNotifyChangedListener(this);

        // when the business object changes, refresh everything...
        refreshFromBusinessObject();
    }

    public void notifyChanged( Notification notification ) {

        setEnabledStates();

        Object obj = notification.getNotifier();
        boolean bHandle = false;

        // Defect 18557 NPE - Need to check if notifier is NULL
        if (obj != null) {
            if (obj.equals(rtRelationshipTypeObject)) {
                bHandle = true;
            } else if (obj instanceof RelationshipRole) {

                if (getEditor().getTargetRole() != null) {
                    if (obj.equals(getEditor().getTargetRole())) {
                        bHandle = true;
                    }
                } else if (getEditor().getSourceRole() != null) {
                    if (obj.equals(getEditor().getSourceRole())) {
                        bHandle = true;
                    }
                }
            }
        }

        if (bHandle) {
            // System.out.println("[RelationshipPanel.notifyChanged] about to call refreshFromBusinessObject()");
            refreshFromBusinessObject();

        }
    }

    public void widgetSelected( SelectionEvent e ) {
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
        widgetSelected(e);
    }

    class RolePanel extends Composite implements IContentFilter, SelectionListener {

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

        private int iRoleType;
        private int iMetaclassesType;

        // derived from constructor args
        private RelationshipRole rrRelationshipRoleObject;

        private static final int BUTTON_GRID_STYLE = GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_CENTER;
        private final String ADD_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipPanel.add.text"); //$NON-NLS-1$
        private final String REMOVE_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipPanel.remove.text"); //$NON-NLS-1$
        private final String OBJECT_NAME_TITLE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.metaclassName.title"); //$NON-NLS-1$
        private final String OBJECT_PATH_TITLE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.metaclassPath.title"); //$NON-NLS-1$

        private String sDefaultMetaclassName = "MetaclassName"; //$NON-NLS-1$

        private RolePanel oppPanel;

        private Composite pnlTableStuff;
        private CLabel lblMetaclassType; // serves as heading

        private Table tblRoleTable;
        private String[] columnNames = new String[] {
        /*
         * Extremely weird:  When I put them in BACKWARDS, they appear in the desired order...
         */
        OBJECT_PATH_TITLE, OBJECT_NAME_TITLE};

        private TableViewer tvRoleTableViewer;
        private TableContentProvider cpContentProvider;
        private TableLabelProvider lpLabelProvider;

        private Composite pnlAddRemoveButtons;
        private Button btnAdd;
        private Button btnRemove;

        public RolePanel( Composite parent,
                          RelationshipType rRelationshipTypeObject,
                          int iRoleType,
                          int iMetaclassesType ) {

            super(parent, SWT.BORDER);
            // this.parent = parent;
            // this.rRelationshipTypeObject = rRelationshipTypeObject;
            this.iRoleType = iRoleType;
            this.iMetaclassesType = iMetaclassesType;

            init();
        }

        /**
         * Initialize the panel.
         */
        private void init() {

            // ----------------------------------
            // Create the Controls (Top) Panel
            // ----------------------------------
            createControl(this);

            if (iRoleType == ROLE_A) {
                rrRelationshipRoleObject = getEditor().getSourceRole();
            } else {
                rrRelationshipRoleObject = getEditor().getTargetRole();
            }

            // Initialize the Button states
            setButtonStates();

            tblRoleTable.addSelectionListener(this);

        }

        void setOppositePanel( final RolePanel panel ) {
            this.oppPanel = panel;
        }

        private TableContentProvider getContentProvider() {
            return this.cpContentProvider;
        }

        public void setInput( RelationshipType rRelationshipTypeObject,
                              int iRoleType,
                              int iMetaclassesType ) {

            // this.rRelationshipTypeObject = rRelationshipTypeObject;
            this.iRoleType = iRoleType;
            this.iMetaclassesType = iMetaclassesType;

            if (iRoleType == ROLE_A) {
                rrRelationshipRoleObject = getEditor().getSourceRole();
            } else {
                rrRelationshipRoleObject = getEditor().getTargetRole();
            }

            // to get the wizard ("new") case to work properly, refresh the contentprovider
            cpContentProvider = new TableContentProvider(getRelationshipRole(), iMetaclassesType);
            tvRoleTableViewer.setContentProvider(cpContentProvider);

            tvRoleTableViewer.setInput(getRelationshipRole());
        }

        public RelationshipRole getRelationshipRole() {
            return rrRelationshipRoleObject;
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

            List lstMetaClasses = new ArrayList();

            if (iMetaclassesType == INCLUDE_TYPES) {
                lstMetaClasses = getEditor().getIncludedMetaclasses(getRelationshipRole());
            } else {
                lstMetaClasses = getEditor().getExcludedMetaclasses(getRelationshipRole());
            }

            for (int i = 0; i < iSelectedIndices.length; i++) {

                lstSelectedObjects.add(lstMetaClasses.get(iSelectedIndices[i]));
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
            createMetaclassNamePanel(parent);

            // 2. Create the table
            createTableStuffPanel(parent);

            // 3. establish listening
            registerListeners();
        }

        private void createMetaclassNamePanel( Composite parent ) {

            String sMetaclassName = sDefaultMetaclassName;

            if (iMetaclassesType == INCLUDE_TYPES) {
                sMetaclassName = INCLUDE_TYPES_TEXT;
            } else {
                sMetaclassName = EXCLUDE_TYPES_TEXT;
            }

            lblMetaclassType = WidgetFactory.createLabel(parent, sMetaclassName);
            lblMetaclassType.setFont(getBoldFont(lblMetaclassType.getFont()));
        }

        private Font getBoldFont( Font f ) {

            FontData data = f.getFontData()[0];
            data.setStyle(SWT.BOLD);
            Font fNewFont = new Font(null, data);

            return fNewFont;
        }

        private void createTableStuffPanel( Composite parent ) {

            pnlTableStuff = new Composite(parent, SWT.NONE);

            GridLayout gridLayout = new GridLayout();
            gridLayout.marginWidth = gridLayout.marginHeight = 0;
            pnlTableStuff.setLayout(gridLayout);
            GridData gridData = new GridData(GridData.FILL_BOTH);
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
            cpContentProvider = new TableContentProvider(getRelationshipRole(), iMetaclassesType);
            tvRoleTableViewer.setContentProvider(cpContentProvider);

            lpLabelProvider = new TableLabelProvider();
            tvRoleTableViewer.setLabelProvider(lpLabelProvider);

            if (getRelationshipRole() != null) {
                tvRoleTableViewer.setInput(getRelationshipRole());
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
            // btnAdd.setToolTipText( ADD_TEXT );
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
             * launch the 'Add Objects' dialog and put the result in our business object, 
             * then refresh the table,
             *             
             */
            SelectionDialog sd = MetamodelTreeViewer.createSelectionDialog(null, true, this);
            sd.open();

            if (sd.getReturnCode() == Window.OK) {

                Object oSelectedObject = sd.getResult()[0];

                // Randall's 'null arg' defect: at line 2279; probably the selection is null when
                // we are doing this for the first time in a wizard...

                try {
                    if (iMetaclassesType == INCLUDE_TYPES) {
                        if (getEditor().canAddIncludedMetaclass(getRelationshipRole(), (EClass)oSelectedObject)) {

                            getEditor().addIncludedMetaclass(getRelationshipRole(), (EClass)oSelectedObject);
                        }
                    } else {

                        if (getEditor().canAddExcludedMetaclass(getRelationshipRole(), (EClass)oSelectedObject)) {

                            getEditor().addExcludedMetaclass(getRelationshipRole(), (EClass)oSelectedObject);
                        }
                    }

                } catch (ModelerCoreException mce) {
                    ModelerCore.Util.log(IStatus.ERROR, mce, mce.getMessage());
                }

                // new objects may have changed our state
                setButtonStates();
                validate();
                refreshTable();

            }

            tvRoleTableViewer.refresh();
        }

        /**
         * @see com.metamatrix.modeler.internal.ui.viewsupport.IContentFilter#filter(java.lang.Object[])
         * @since 4.2
         */
        public Object[] filter( final Object[] elements ) {
            final Object[] elems = getContentProvider().getElements(null);
            final Object[] oppElems = this.oppPanel.getContentProvider().getElements(null);
            final List filteredElems = new ArrayList(elements.length);
            for (int ndx = 0, len = elements.length; ndx < len; ++ndx) {
                final Object elem = elements[ndx];
                boolean exists = false;
                for (int ndx2 = elems.length; --ndx2 >= 0;) {
                    if (((TableRow)elems[ndx2]).oObject.equals(elem)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    for (int ndx2 = oppElems.length; --ndx2 >= 0;) {
                        if (((TableRow)oppElems[ndx2]).oObject.equals(elem)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        filteredElems.add(elem);
                    }
                }
            }
            return filteredElems.toArray();
        }

        void removeButtonPressed() {
            /*
             * remove the object selected in the table from our business object, then re init.
             */

            if (hasSelection()) {
                List lstElementsToRemove = getSelectedObjects();

                Iterator it = lstElementsToRemove.iterator();

                try {

                    while (it.hasNext()) {
                        EClass ecTemp = (EClass)it.next();

                        if (iMetaclassesType == INCLUDE_TYPES) {
                            getEditor().removeIncludedMetaclass(getRelationshipRole(), ecTemp);
                        } else {
                            getEditor().removeExcludedMetaclass(getRelationshipRole(), ecTemp);
                        }

                    }

                } catch (ModelerCoreException mce) {
                    ModelerCore.Util.log(IStatus.ERROR, mce, mce.getMessage());
                }
            }

            tvRoleTableViewer.refresh();
            setButtonStates();
        }

        /**
         * Set the enabled/disabled states of the Buttons.
         */
        public void setButtonStates( boolean bEnabledState ) {
            /*
             *  1. Always enable Add...
             *  2. if a row in the table is selected, enable Remove             
             */
            btnAdd.setEnabled(bEnabledState);

            btnRemove.setEnabled(bEnabledState && tblRoleTable.getSelectionCount() > 0);

        }

        /**
         * Set the enabled/disabled states of the Buttons.
         */
        public void setButtonStates() {
            /*
             *  1. Always enable Add...
             *  2. if a row in the table is selected, enable Remove             
             */
            btnAdd.setEnabled(true);

            btnRemove.setEnabled(tblRoleTable.getSelectionCount() > 0);

        }

        /*
         * this method picks up selection insided the table in once instance of RolePanel;
         * we also need the RelationshipPanel code to listen to RolePanel so that they
         * can respond when selection changes in one of the 2 tables.
         */
        public void widgetSelected( SelectionEvent e ) {
            setButtonStates();

        }

        public void widgetDefaultSelected( SelectionEvent e ) {
            widgetSelected(e);
        }

    }

    class TableContentProvider implements IStructuredContentProvider {

        private RelationshipRole rrRelationshipRoleObject2;
        // private int iRoleType;
        private int iMetaclassesType;

        public TableContentProvider( RelationshipRole rRelationshipRoleObject,
                                     int iMetaclassesType ) {

            // need to know what role and what metaclass type;
            // the rest we work out calling editor methods (getEditor() is globally visible).

            this.rrRelationshipRoleObject2 = rRelationshipRoleObject;
            this.iMetaclassesType = iMetaclassesType;

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

            // bail out if primary object is null
            if (rrRelationshipRoleObject2 == null) {
                return new Object[0];
            }

            Object[] result = null;

            List lstObjects = new ArrayList();

            if (this.iMetaclassesType == INCLUDE_TYPES) {
                lstObjects = getEditor().getIncludedMetaclasses(rrRelationshipRoleObject2);
            } else {
                lstObjects = getEditor().getExcludedMetaclasses(rrRelationshipRoleObject2);
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
            if (theNewInput != null) {
                //                    System.out.println("[RolePanel#TableContentProvider.inputChanged] About to replace rrRelationshipRoleObject2, new value: " + rrRelationshipRoleObject2 ); //$NON-NLS-1$                    
                rrRelationshipRoleObject2 = (RelationshipRole)theNewInput;
                theViewer.refresh();
            }
        }
    }

    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

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

                switch (theIndex) {
                    case 0:
                        sResult = ModelUtilities.getEMFLabelProvider().getText(eoObject);

                        break;

                    case 1:
                        sResult = eoObject.eResource().getURI().lastSegment(); // yielded: SimpleDataTypes

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
            //                String result = "unknown"; //$NON-NLS-1$
            Object oResult = null;

            return oResult;
        }
    }

    class InheritanceTablePanel extends Composite implements SelectionListener {

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

        private RelationshipType rtRelationshipTypeObject;

        private static final int BUTTON_GRID_STYLE = GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_CENTER;
        private final String CLASS_NAME_TITLE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.className.title"); //$NON-NLS-1$
        private final String CLASS_PATH_TITLE = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.classPath.title"); //$NON-NLS-1$

        private final String SUBTYPES_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.subTypes.text"); //$NON-NLS-1$    
        private final String FIND_BUTTON_TEXT = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypePanel.findButton.text"); //$NON-NLS-1$

        private Composite pnlTableStuff;

        private Table tblInheritanceTable;
        private String[] columnNames = new String[] {
        /*
         * Extremely weird:  When I put them in BACKWARDS, they appear in the desired order...
         */
        CLASS_PATH_TITLE, CLASS_NAME_TITLE};

        private TableViewer tvRoleTableViewer;
        private InheritanceTableContentProvider cpInheritanceContentProvider;
        private InheritanceTableLabelProvider lpInheritanceLabelProvider;

        private Button btnFind;

        public InheritanceTablePanel( Composite parent,
                                      RelationshipType rtRelationshipTypeObject ) {

            super(parent, SWT.NONE);
            this.rtRelationshipTypeObject = rtRelationshipTypeObject;

            init();
        }

        /**
         * Initialize the panel.
         */
        private void init() {

            // ----------------------------------
            // Create the Controls (Top) Panel
            // ----------------------------------
            createControl(this);

            // Initialize the Button states
            setButtonStates();

            tblInheritanceTable.addSelectionListener(this);

            tblInheritanceTable.addMouseListener(new MouseListener() {
                public void mouseDown( final MouseEvent event ) {
                    handleMouseEvent(event);
                }

                public void mouseUp( final MouseEvent event ) {

                }

                public void mouseDoubleClick( final MouseEvent event ) {

                }
            });

        }

        public void setInput( RelationshipType rtRelationshipTypeObject ) {

            this.rtRelationshipTypeObject = rtRelationshipTypeObject;

            tvRoleTableViewer.setInput(rtRelationshipTypeObject);
        }

        public void clearSelection() {
            tblInheritanceTable.deselectAll();
        }

        public boolean hasSelection() {
            return (tblInheritanceTable.getSelectionCount() > 0);
        }

        public void addSelectionListener( SelectionListener listener ) {
            tblInheritanceTable.addSelectionListener(listener);
        }

        public void removeSelectionListener( SelectionListener listener ) {
            tblInheritanceTable.removeSelectionListener(listener);
        }

        /**
         * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#createControl(org.eclipse.swt.widgets.Composite)
         */
        public void createControl( Composite parent ) {

            // 0. Set layout for the SashForm
            GridLayout gridLayout = new GridLayout();
            this.setLayout(gridLayout);
            gridLayout.numColumns = 3;
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridLayout.marginWidth = gridLayout.marginHeight = 0;

            this.setLayoutData(gridData);

            // 1. 'subtypes' label
            // lblSubtypes =
            WidgetFactory.createLabel(parent, SUBTYPES_TEXT);

            // 2. Create the table
            createTableStuffPanel(parent);

            // 3. 'edit' button
            btnFind = WidgetFactory.createButton(parent, FIND_BUTTON_TEXT, BUTTON_GRID_STYLE);
            btnFind.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    navigateToSelectedSubtype();
                }
            });

            // 4. establish listening
            registerListeners();
        }

        void navigateToSelectedSubtype() {

            ISelectionProvider selectionProvider;

            if (tblInheritanceTable.getSelectionIndex() < 0) {
                return;
            }

            try {

                // use table selection index to pull object from the source data.
                TableItem tbliSelection = tblInheritanceTable.getSelection()[tblInheritanceTable.getSelectionIndex()];
                InheritanceTableRow itr = (InheritanceTableRow)tbliSelection.getData();
                EObject eoSubtype = (EObject)itr.getObject();

                // send the selected EObject as a new selection in the workbench
                final ISelection selection = new StructuredSelection(eoSubtype);
                selectionProvider = new SelectionProvider();
                selectionProvider.setSelection(selection);

                // activate the Model Explorer view (must do this last)
                Display.getCurrent().asyncExec(new Runnable() {
                    public void run() {
                        try {

                            ModelExplorerResourceNavigator view = (ModelExplorerResourceNavigator)UiUtil.getWorkbenchPage().showView(EXPLORER_VIEW);
                            view.getTreeViewer().setSelection(selection);
                        } catch (PartInitException err) {
                            UiConstants.Util.log(err);
                            WidgetUtil.showError(err.getLocalizedMessage());
                        }
                    }
                });
            } catch (Exception err) {
                UiConstants.Util.log(err);
                WidgetUtil.showError(err.getLocalizedMessage());
            }

        }

        private void createTableStuffPanel( Composite parent ) {

            pnlTableStuff = new Composite(parent, SWT.NONE);

            GridLayout gridLayout = new GridLayout();
            gridLayout.marginWidth = gridLayout.marginHeight = 0;
            pnlTableStuff.setLayout(gridLayout);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            pnlTableStuff.setLayoutData(gridData);

            // 1. Create the table
            createTableViewerPanel(pnlTableStuff);

            // 2. Create the 'add/remove' button panel
            // createAddRemoveButtonPanel( pnlTableStuff );
        }

        /*
         * Create the TableViewerPanel 
         */
        private void createTableViewerPanel( Composite parent ) {
            // Create the table
            createTable(parent);

            // Create and setup the TableViewer
            createTableViewer();
            cpInheritanceContentProvider = new InheritanceTableContentProvider(this.rtRelationshipTypeObject);
            lpInheritanceLabelProvider = new InheritanceTableLabelProvider();

            tvRoleTableViewer.setContentProvider(cpInheritanceContentProvider);
            tvRoleTableViewer.setLabelProvider(lpInheritanceLabelProvider);

            if (rtRelationshipTypeObject != null) {
                //            System.out.println("[RelationshipTypePanel.createTableViewerPanel] rtRelationshipTypeObject is: " + rtRelationshipTypeObject ); //$NON-NLS-1$
                tvRoleTableViewer.setInput(rtRelationshipTypeObject);
            } else {
                //            System.out.println("[RelationshipTypePanel.createTableViewerPanel] rtRelationshipTypeObject is NULL"); //$NON-NLS-1$
            }

        }

        /**
         * Create the Table
         */
        private void createTable( Composite parent ) {
            int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;

            tblInheritanceTable = new Table(parent, style);
            TableLayout layout = new TableLayout();
            tblInheritanceTable.setLayout(layout);

            GridData gridData = new GridData(GridData.FILL_BOTH);
            tblInheritanceTable.setLayoutData(gridData);

            tblInheritanceTable.setLinesVisible(true);
            tblInheritanceTable.setHeaderVisible(true);

            // 1st column
            TableColumn column1 = new TableColumn(tblInheritanceTable, SWT.LEFT, 0);
            column1.setText(columnNames[0]);
            ColumnWeightData weight = new ColumnWeightData(1);
            layout.addColumnData(weight);

            // 2nd column
            TableColumn column2 = new TableColumn(tblInheritanceTable, SWT.LEFT, 0);
            column2.setText(columnNames[1]);
            ColumnWeightData weight2 = new ColumnWeightData(1);
            layout.addColumnData(weight2);

        }

        /**
         * Create the TableViewer
         */
        private void createTableViewer() {

            tvRoleTableViewer = new TableViewer(tblInheritanceTable);
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

        private void registerListeners() {

        }

        /**
         * Set the enabled/disabled states of the Buttons.
         */
        private void setButtonStates() {
            /*               
             *  1. if a row in the table is selected, enable Edit             
             */

            btnFind.setEnabled(tblInheritanceTable.getSelectionCount() == 1);

        }

        /**
         * Set the enabled/disabled states of the Buttons.
         */
        void setButtonStates( boolean bEnabledState ) {
            /*               
             *  1. if a row in the table is selected, enable Edit             
             */

            btnFind.setEnabled(bEnabledState && tblInheritanceTable.getSelectionCount() == 1);

        }

        /*
         * this method picks up selection insided the table in once instance of RolePanel;
         * we also need the RelationshipTypePanel code to listen to RolePanel so that they
         * can respond when selection changes in one of the 2 tables.
         */

        public void widgetSelected( SelectionEvent e ) {
            setButtonStates();

        }

        public void widgetDefaultSelected( SelectionEvent e ) {
            widgetSelected(e);
        }

        void handleMouseEvent( MouseEvent theEvent ) {

            // 0. bail out if no content yet
            if (rtRelationshipTypeObject == null) {
                return;
            }

        }

    }

    class InheritanceTableContentProvider implements IStructuredContentProvider {

        public InheritanceTableContentProvider( RelationshipType rtRelationshipTypeObject ) {
            // need to know what role and what metaclass type;
            // the rest we work out calling editor methods (getEditor() is globally visible).

            this.rtRelationshipTypeObject = rtRelationshipTypeObject;

        }

        RelationshipType rtRelationshipTypeObject;

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object theInputElement ) {
            Object[] result = null;

            List lstObjects = new ArrayList();

            if (rtRelationshipTypeObject != null) {
                lstObjects = getEditor().getSubtypes();
            }

            if ((lstObjects != null) && !lstObjects.isEmpty()) {
                int numRows = lstObjects.size();
                result = new Object[numRows];

                for (int i = 0; i < numRows; i++) {
                    Object oObject = lstObjects.get(i);

                    result[i] = new InheritanceTableRow(oObject);
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
            rtRelationshipTypeObject = (RelationshipType)theNewInput;
            if (theNewInput != null) {
                theViewer.refresh();
            }
        }

    }

    class InheritanceTableLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage( Object theElement,
                                     int theIndex ) {
            final WorkbenchLabelProvider workbenchProvider = new WorkbenchLabelProvider();
            Object oRealObject = ((InheritanceTableRow)theElement).getObject();

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
                    // no image
                    break;
            }
            return imgResult;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object theElement,
                                     int theColumnIndex ) {
            InheritanceTableRow row = (InheritanceTableRow)theElement;
            return row.getColumnText(theColumnIndex);
        }

    }

    class InheritanceTableRow {

        private Object oObject;

        public InheritanceTableRow( Object oObject ) {

            this.oObject = oObject;
        }

        public Object getObject() {
            return oObject;
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
