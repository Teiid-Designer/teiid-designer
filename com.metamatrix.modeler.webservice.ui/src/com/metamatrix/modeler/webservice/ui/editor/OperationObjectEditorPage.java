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
package com.metamatrix.modeler.webservice.ui.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import com.metamatrix.core.util.ArrayUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.actions.TransformationSourceManager;
import com.metamatrix.modeler.transformation.ui.editors.SqlPanelDropTargetListener;
import com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.modeler.webservice.ui.WebServiceUiPlugin;
import com.metamatrix.modeler.webservice.ui.util.WebServiceUiUtil;
import com.metamatrix.modeler.webservice.util.WebServiceUtil;
import com.metamatrix.query.internal.ui.sqleditor.component.AssignmentStatementDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.BlockDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.DeclareStatementDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.DisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.QueryDisplayComponent;
import com.metamatrix.query.sql.proc.AssignmentStatement;
import com.metamatrix.query.sql.proc.DeclareStatement;
import com.metamatrix.query.ui.sqleditor.SqlEditorPanel;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;
import com.metamatrix.ui.tree.AbstractTreeContentProvider;

/**
 * OperationObjectEditorPage is the class for editing Transformation Objects.
 */
public class OperationObjectEditorPage extends TransformationObjectEditorPage
    implements ArrayUtil.Constants, IInternalUiConstants, IInternalUiConstants.Images, StringUtil.Constants {

    private static final String I18N_PFX = I18nUtil.getPropertyPrefix(OperationObjectEditorPage.class);

    public static final String NAME = UTIL.getString(I18N_PFX + "name"); //$NON-NLS-1$

    private static final String OPS_TITLE = UTIL.getString(I18N_PFX + "operationsTitle"); //$NON-NLS-1$
    private static final String PROC_DESC = UTIL.getString(I18N_PFX + "procedureDescription"); //$NON-NLS-1$
    private static final String PROC_TITLE = UTIL.getString(I18N_PFX + "procedureTitle"); //$NON-NLS-1$
    private static final String VARS_DESC = UTIL.getString(I18N_PFX + "variablesDescription"); //$NON-NLS-1$
    private static final String DROP_CONFIRM_TITLE = UTIL.getString(I18N_PFX + "dropConfirmTitle"); //$NON-NLS-1$
    private static final String DROP_CONFIRM_MSG = UTIL.getString(I18N_PFX + "dropConfirmMsg"); //$NON-NLS-1$

    private static final String EDIT = UTIL.getString(I18N_PFX + "edit"); //$NON-NLS-1$

    private static final String EDIT_LABEL = WidgetUtil.FORM_TEXT_START_TAG
                                             + UTIL.getString(I18N_PFX + "editLabel", //$NON-NLS-1$
                                                              WidgetUtil.A_START_TAG + EDIT + WidgetUtil.A_END_TAG)
                                             + WidgetUtil.FORM_TEXT_END_TAG;

    private static final int DFLT_LEFT_H_WGT = 250;
    private static final int DFLT_RIGHT_H_WGT = 750;
    private static final int DFLT_TOP_V_WGT = 250;
    private static final int DFLT_BOTTOM_V_WGT = 750;

    private static final String LEFT_H_WGT_PREF = I18N_PFX + "leftHorizontalWeight"; //$NON-NLS-1$
    private static final String RIGHT_H_WGT_PREF = I18N_PFX + "rightHorizontalWeight"; //$NON-NLS-1$
    private static final String TOP_V_WGT_PREF = I18N_PFX + "leftVerticalWeight"; //$NON-NLS-1$
    private static final String BOTTOM_V_WGT_PREF = I18N_PFX + "rightVerticalWeight"; //$NON-NLS-1$

    private TreeViewer opViewer;
    private InputVariableSection varSection;
    private Map declarationsToAssignments;
    Operation selectedOp;
    private FormText editLink;
    private Resource resrc;
    private OperationEditorPage opEditorPage;
    private SashForm hSplitter, vSplitter;
    private Section sqlPanelSection, treeSection;
    private SqlEditorPanel sqlEditorPanel;
    private boolean allowsExternalEdits = true;

    @Override
    protected boolean allowsMultipleTabs() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage#canClose()
     * @since 5.0.1
     */
    @Override
    public boolean canClose() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#canEdit(java.lang.Object, org.eclipse.ui.IEditorPart)
     * @since 5.0.1
     */
    @Override
    public boolean canEdit( Object object,
                            IEditorPart editor ) {
        // System.out.println("OperationObjectEditorPage.canEdit()"); //$NON-NLS-1$
        return (editor instanceof OperationEditorPage);
    }

    /**
     * @return <code>null</code>.
     * @see com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage#contributeReconcileAction()
     * @since 5.0.1
     */
    @Override
    protected IAction contributeReconcileAction() {
        return null;
    }

    @Override
    protected SqlPanelDropTargetListener createDropTargetListener( SqlEditorPanel sqlPanel,
                                                                   SqlTransformationMappingRoot transformation ) {
        return new MySqlPanelDropTargetListener(sqlPanel, transformation, this);
    }

    void createEditActionControl( Composite parent ) {
        // Create edit action
        FormToolkit toolkit = WebServiceUiPlugin.getDefault().getFormToolkit(parent.getDisplay());
        this.editLink = WidgetFactory.createFormText(parent, toolkit, EDIT_LABEL, new HyperlinkAdapter() {

            @Override
            public void linkActivated( HyperlinkEvent event ) {
                editVariables();
            }
        });
        WidgetUtil.disableFormText(this.editLink);
    }

    /**
     * @see com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage#createSelectEditorForNoUpdate(org.eclipse.swt.widgets.Composite)
     * @since 5.0.1
     */
    @Override
    protected SqlEditorPanel createSelectEditorForNoUpdate( Composite parent ) {
        // System.out.println("OperationObjectEditorPage.createSelectEditorForNoUpdate()"); //$NON-NLS-1$
        FillLayout fillLayout = new FillLayout();
        parent.setLayout(fillLayout);
        final Display display = parent.getDisplay();
        FormToolkit toolkit = WebServiceUiPlugin.getDefault().getFormToolkit(display);
        Composite mainBody = toolkit.createForm(parent).getBody();
        mainBody.setLayout(fillLayout);
        // Create list of existing operations
        this.hSplitter = new SashForm(mainBody, SWT.HORIZONTAL);
        treeSection = toolkit.createSection(this.hSplitter, ExpandableComposite.TITLE_BAR);
        treeSection.setText(OPS_TITLE);
        toolkit.paintBordersFor(treeSection);
        this.opViewer = new TreeViewer(treeSection, SWT.H_SCROLL | SWT.V_SCROLL);
        treeSection.setClient(this.opViewer.getTree());
        this.opViewer.setContentProvider(new AbstractTreeContentProvider() {

            @Override
            public Object[] getChildren( Object parent ) {
                return (parent instanceof Interface ? ((Interface)parent).getOperations().toArray() : EMPTY_ARRAY);
            }

            @Override
            public Object[] getElements( Object inputElement ) {
                List interfaces = new ArrayList();
                for (Iterator iter = ((List)inputElement).iterator(); iter.hasNext();) {
                    Object obj = iter.next();
                    if (obj instanceof Interface) {
                        interfaces.add(obj);
                    }
                } // for
                return interfaces.toArray();
            }

            public Object getParent( Object element ) {
                return (element instanceof Operation ? ((Operation)element).getInterface() : null);
            }
        });
        this.opViewer.setLabelProvider(ModelUtilities.getEMFLabelProvider());
        // Wire the editor's selection handler

        this.opEditorPage.getSelectionHandler().initialize(this.opViewer);
        super.setNoUpdatesAllowed(true);
        ((OperationEditorNotifyChangedListener)this.opEditorPage.getNotifyChangedListener()).initialize(this.opViewer,
                                                                                                        this.opEditorPage.getEditorResource());

        this.opViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged( final SelectionChangedEvent event ) {
                UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {

                    public void run() {
                        operationSelected(event);
                    }
                });
            }
        });
        // Create editor within splitter with declaration area at top
        this.vSplitter = new SashForm(this.hSplitter, SWT.VERTICAL);
        this.declarationsToAssignments = new TreeMap(new Comparator() {

            public int compare( Object object1,
                                Object object2 ) {
                if (object1 == null) {
                    return -1;
                }
                if (object2 == null) {
                    return 1;
                }
                return ((DeclareStatement)object1).getVariable().getShortName().compareTo(((DeclareStatement)object2).getVariable().getShortName());
            }
        });
        this.varSection = new InputVariableSection(this.vSplitter, VARS_DESC, this) {

            @Override
            protected void createActionControls( Composite parent ) {
                createEditActionControl(parent);
            }
        };
        this.varSection.create();

        sqlPanelSection = toolkit.createSection(this.vSplitter, ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
        sqlPanelSection.setText(PROC_TITLE);
        sqlPanelSection.setDescription(PROC_DESC);

        sqlEditorPanel = super.createSelectEditorForNoUpdate(sqlPanelSection);
        sqlEditorPanel.getTextViewer().getTextWidget().setEnabled(false);
        sqlPanelSection.setClient(sqlEditorPanel);
        sqlEditorPanel.getQueryDisplayComponent().addPropertyListener(new PropertyChangeListener() {

            public void propertyChange( PropertyChangeEvent event ) {
                displayNodeUpdated(((QueryDisplayComponent)event.getSource()).getDisplayNode());
            }
        });
        // Restore splitter weights from preferences (or set default values if not present)
        IPreferenceStore store = WebServiceUiPlugin.getDefault().getPreferenceStore();
        int leftHWgt = store.getInt(LEFT_H_WGT_PREF);
        int rightHWgt = store.getInt(RIGHT_H_WGT_PREF);
        this.hSplitter.setWeights(new int[] {(leftHWgt == 0 ? DFLT_LEFT_H_WGT : leftHWgt),
            (rightHWgt == 0 ? DFLT_RIGHT_H_WGT : rightHWgt)});
        int topVWgt = store.getInt(TOP_V_WGT_PREF);
        int bottomVWgt = store.getInt(BOTTOM_V_WGT_PREF);
        this.vSplitter.setWeights(new int[] {(topVWgt == 0 ? DFLT_TOP_V_WGT : topVWgt),
            (bottomVWgt == 0 ? DFLT_BOTTOM_V_WGT : bottomVWgt)});
        // Create listener to save splitter weights to preferences as they change
        sqlPanelSection.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized( ControlEvent event ) {
                updateWeightPreferences();
            }
        });
        return sqlEditorPanel;
    }

    /*
     * internal method which disables a section's content including dimming all text.
     */
    private void setSectionEnabled( Object section,
                                    boolean enable ) {
        if (section == varSection) {
            this.varSection.setEnabled(enable);
        } else if (section == sqlPanelSection) {
            if (enable) {
                Color fgdColor = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
                sqlPanelSection.setEnabled(true);
                sqlPanelSection.setForeground(fgdColor);
                getTextWidget().setEnabled(true);
                getTextWidget().setEditable(true);
                getTextWidget().setForeground(fgdColor);
                this.editLink.setEnabled(true);
                WidgetUtil.enableFormText(this.editLink);
                this.editLink.setForeground(fgdColor);
            } else {
                Color fgdColor = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
                sqlPanelSection.setEnabled(false);
                sqlPanelSection.setForeground(fgdColor);
                getTextWidget().setEnabled(false);
                getTextWidget().setEditable(false);
                getTextWidget().setForeground(fgdColor);
                this.editLink.setForeground(fgdColor);
                this.editLink.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                WidgetUtil.disableFormText(this.editLink);
                showMessageArea(false);
            }
        }
    }

    @Override
    protected List customizeActionList( List initialActionList ) {
        initialActionList.remove(SqlEditorPanel.ACTION_ID_EXPAND_SELECT);
        initialActionList.remove(SqlEditorPanel.ACTION_ID_TOGGLE_OPTIMIZER);
        // Defect 23719 - problems with Criteria builder & expression builder. Doesn't make sense to keep them in for SP2
        // Defect will be fixed in SP3, so we'll add these actions back in??
        initialActionList.remove(SqlEditorPanel.ACTION_ID_LAUNCH_CRITERIA_BUILDER);
        initialActionList.remove(SqlEditorPanel.ACTION_ID_LAUNCH_EXPRESSION_BUILDER);
        return initialActionList;
    }

    /**
     * @see com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage#deactivate()
     * @since 5.0.1
     */
    @Override
    public boolean deactivate() {
        UiUtil.savePreferences(WebServiceUiPlugin.getDefault().getPreferenceStore());
        return super.deactivate();
    }

    void displayNodeUpdated( DisplayNode root ) {
        // System.out.println("OperationObjectEditorPage.makeInputVariableStatementsInvisible()");
        root.setVisible(false, false);
        for (Iterator childIter = root.getChildren().iterator(); childIter.hasNext();) {
            DisplayNode node = (DisplayNode)childIter.next();
            if (node instanceof BlockDisplayNode) {
                // Update declaration-to-assignment mapping using new nodes and set visibility of all nodes as appropriate
                this.declarationsToAssignments.clear();
                node.setVisible(false, false);
                Map declarations = new HashMap();
                for (Iterator blockIter = node.getChildren().iterator(); blockIter.hasNext();) {
                    node = (DisplayNode)blockIter.next();
                    if (node instanceof DeclareStatementDisplayNode) {
                        DeclareStatement declaration = (DeclareStatement)node.getLanguageObject();
                        if (declaration.getVariable().getName().startsWith(WebServiceUtil.INPUT_VARIABLE_PREFIX)) {
                            declarations.put(declaration.getVariable(), declaration);
                            node.setVisible(false, true);
                        }
                    }

                    if (node instanceof AssignmentStatementDisplayNode || node instanceof DeclareStatementDisplayNode) {
                        AssignmentStatement assignment = (AssignmentStatement)node.getLanguageObject();
                        Object declaration = declarations.get(assignment.getVariable());
                        if (declaration != null) {
                            this.declarationsToAssignments.put(declaration, assignment);
                            node.setVisible(false, true);
                        }
                    }
                } // for
                this.varSection.refresh();
                break;
            }
        } // for
    }

    /**
     * @see com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage#edit(java.lang.Object)
     * @since 5.0.1
     */
    @Override
    public void edit( Object object ) {
        // System.out.println("OperationObjectEditorPage.edit(" + object + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        super.edit(object);
        if (object != null) {
            Resource resrc = ((EObject)object).eResource();
            if (resrc == null && object instanceof Diagram) {
                resrc = ((Diagram)object).getTarget().eResource();
            }
            if (this.resrc == resrc) {
                selectOperation(getCurrentMappingRoot());
            } else {
                this.resrc = resrc;
                updateOperations();
            }
        }
    }

    void editVariables() {
        if (this.editLink.getHyperlinkSettings().getHyperlinkUnderlineMode() == HyperlinkSettings.UNDERLINE_NEVER) {
            return;
        }
        new VariableEditorDialog(getControl().getShell(), this.varSection.getSelection(), this).open();
    }

    /**
     * @param node
     * @return
     * @since 5.0.1
     */
    public BlockDisplayNode findBlock() {
        return findBlock(getCurrentSqlEditor().getQueryDisplayComponent().getDisplayNode());
    }

    private BlockDisplayNode findBlock( DisplayNode node ) {
        if (node instanceof BlockDisplayNode) {
            return (BlockDisplayNode)node;
        }
        for (Iterator iter = node.getChildren().iterator(); iter.hasNext();) {
            node = findBlock((DisplayNode)iter.next());
            if (node != null) {
                return (BlockDisplayNode)node;
            }
        } // for
        return null;
    }

    /**
     * @return Returns the declarationsToAssignments.
     * @since 5.0.1
     */
    public Map getDeclarationsToAssignments() {
        return this.declarationsToAssignments;
    }

    /**
     * @see com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage#getEditableObject(java.lang.Object)
     * @since 5.0.2
     */
    @Override
    public Object getEditableObject( Object object ) {
        Object obj = super.getEditableObject(object);
        if (obj == null) {
            Operation theOp = WebServiceUiUtil.getOperation(object);
            if (theOp != null && theOp.eResource() != null) {
                obj = TransformationHelper.getTransformationMappingRoot(theOp);
            } else {
                obj = WebServiceUiUtil.getFirstInterface(object);
            }
        }
        return obj;
    }

    /**
     * Builds list of the Separator locations for the toolbar. The separator will be placed following the action count (eg,
     * separator at 2 will be placed after the second action)
     * 
     * @since 5.0.1
     */
    @Override
    protected List getSeparatorLocations() {
        List separatorLocs = new ArrayList(6);
        separatorLocs.add(new Integer(1));
        separatorLocs.add(new Integer(3));
        separatorLocs.add(new Integer(4));
        separatorLocs.add(new Integer(6));
        return separatorLocs;
    }

    /**
     * @see com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage#getTransformationName()
     * @since 5.0.1
     */
    @Override
    public String getTransformationName() {
        return NAME;
    }

    /**
     * @see com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage#initialize(com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor)
     * @since 5.0.1
     */
    @Override
    public void initialize( MultiPageModelEditor editor ) {
        // System.out.println("OperationObjectEditorPage.initialize(" + editor + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        // Set this editor as an override for all other editors
        for (Iterator iter = editor.getObjectEditors().iterator(); iter.hasNext();) {
            ModelObjectEditorPage objEditor = (ModelObjectEditorPage)iter.next();
            if (objEditor != this) {
                objEditor.setOverride(this);
            }
        } // for
        // Wire workspace selection to selection appropriate operation
        for (Iterator iter = editor.getAllEditors().iterator(); iter.hasNext();) {
            ModelEditorPage editorPg = (ModelEditorPage)iter.next();
            if (editorPg instanceof OperationEditorPage) {
                this.opEditorPage = (OperationEditorPage)editorPg;
                this.opEditorPage.addWorkspaceSelectionListener(new ISelectionListener() {

                    public void selectionChanged( IWorkbenchPart part,
                                                  ISelection selection ) {
                        workspaceSelectionChanged(selection);
                    }
                });
            }
        }
    }

    /**
     * Returns <code>true</code>.
     * 
     * @see com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage#isResourceValid()
     * @since 5.0.1
     */
    @Override
    public boolean isResourceValid() {
        return true;
    }

    void operationSelected( SelectionChangedEvent event ) {

        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        Object obj = selection.getFirstElement();
        if (obj instanceof Operation) {

            Operation op = (Operation)obj;
            if (op == this.selectedOp) {
                return;
            }
            ModelResource resrc = ModelUtilities.getModelResourceForModelObject(op);
            try {
                List transformations = resrc.getModelTransformations().getTransformations(op);
                if (!transformations.isEmpty()) {

                    // Enable sections
                    setSectionEnabled(varSection, true);
                    setSectionEnabled(sqlPanelSection, true);

                    // Initialize operation's procedure
                    WebServiceUiUtil.initializeProcedure(op, this, false);

                    if (this.selectedOp != null) {
                        // Save dirty state
                        boolean dirty = isDirty();
                        // Save/Validate changes
                        doSave(false);
                        // Edit operation's transformation
                        super.edit(transformations.get(0));
                        // Restore dirty state
                        if (dirty && !isDirty()) {
                            setDirty(dirty);
                        }
                    } else {
                        // Edit operation's transformation
                        super.edit(transformations.get(0));
                    }
                    this.selectedOp = op;

                    // Refresh/redraw the sections
                    varSection.refresh();
                    sqlPanelSection.redraw();

                    return;
                }
            } catch (ModelWorkspaceException err) {
                IInternalUiConstants.UTIL.log(err);
            }
        } else {
            // If operation was NOT selected, we need to save off the changes in the sql panel
            if (this.selectedOp != null) {
                // Save/Validate changes
                doSave(false);
            }

            super.edit(null);

            this.selectedOp = null;

            // Disable Sections
            setSectionEnabled(varSection, false);
            setSectionEnabled(sqlPanelSection, false);

            // Selection is either empty or an Interface
            this.declarationsToAssignments.clear();

            // Refresh/redraw the sections
            this.varSection.refresh();
            this.sqlPanelSection.redraw();
        }
    }

    /**
     * @since 5.0.1
     */
    public void refreshVariables() {
        this.varSection.refresh();
    }

    private void selectOperation( Object object ) {
        Operation op = WebServiceUiUtil.getOperation(object);
        if (op != null && op.eResource() == this.resrc) {
            this.opViewer.setSelection(new StructuredSelection(op), true);
        }
    }

    private void updateOperations() {
        // Ensure update occurs in UI thread
        UiUtil.runInSwtThread(new Runnable() {

            public void run() {
                updateOperationsInSwtThread();
            }
        }, true);
    }

    void updateOperationsInSwtThread() {
        if (!this.opViewer.getTree().isDisposed()) {
            this.opViewer.setInput(this.resrc.getContents());
            this.opViewer.expandAll();
            if (getCurrentMappingRoot() != null) {
                selectOperation(getCurrentMappingRoot());
            }
        }
    }

    void updateWeightPreferences() {
        IPreferenceStore store = WebServiceUiPlugin.getDefault().getPreferenceStore();
        int[] wgts = this.hSplitter.getWeights();
        UiUtil.updateIntegerPreference(LEFT_H_WGT_PREF, wgts[0], DFLT_LEFT_H_WGT, store);
        UiUtil.updateIntegerPreference(RIGHT_H_WGT_PREF, wgts[1], DFLT_RIGHT_H_WGT, store);
        wgts = this.vSplitter.getWeights();
        UiUtil.updateIntegerPreference(TOP_V_WGT_PREF, wgts[0], DFLT_TOP_V_WGT, store);
        UiUtil.updateIntegerPreference(BOTTOM_V_WGT_PREF, wgts[1], DFLT_BOTTOM_V_WGT, store);
    }

    void workspaceSelectionChanged( ISelection selection ) {
        // System.out.println("OperationObjectEditorPage.workspaceSelectionChanged(): " + selection); //$NON-NLS-1$
        if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
            selectOperation(((IStructuredSelection)selection).getFirstElement());
        }
    }

    public class MySqlPanelDropTargetListener extends SqlPanelDropTargetListener {

        SqlEditorPanel sqlPanel;

        /**
         * @param theSqlPanel
         * @param theTransformation
         * @param theTxnSource
         * @since 5.0
         */
        public MySqlPanelDropTargetListener( SqlEditorPanel theSqlPanel,
                                             SqlTransformationMappingRoot theTransformation,
                                             Object theTxnSource ) {
            super(theSqlPanel, theTransformation, theTxnSource);
            sqlPanel = theSqlPanel;
        }

        // Note that this is a HACK!!!. For some reason, the SqlEditorPanel's composite isn't nested correctly
        // so the Display.map() method can't determine the real offset. SO we have to manually walk up three parents
        // as well as start off with an initial offset. This'll break, I'm sure based on some UI resolution setting, so be
        // prepared.
        @Override
        protected Point getXYPanelOffset() {
            Point newPt = new Point(3, -8);
            newPt.y = newPt.y + sqlPanel.getLocation().y;
            return newPt;
        }

        /**
         * @see org.eclipse.jface.util.TransferDropTargetListener#isEnabled(org.eclipse.swt.dnd.DropTargetEvent)
         * @since 4.3
         */
        @Override
        public boolean isEnabled( DropTargetEvent event ) {
            if (dragSourceIsDocument(event)) {
                return true;
            }

            return false;
        }

        // Really just checks if "columns" case, that it's OK to insert
        // FROM case is more relaxed
        @Override
        protected boolean isInsertOK( DropTargetEvent event ) {
            if (dragSourceIsDocument(event)) {
                return true;
            }

            return false;
        }

        /**
         * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
         * @since 4.3
         */
        @Override
        public void drop( DropTargetEvent event ) {
            event.detail = DND.DROP_COPY;
            final DropTargetEvent dtEvent = event;
            if (dragSourceIsDocument(event)) {
                UiBusyIndicator.showWhile(null, new Runnable() {

                    public void run() {
                        executeDropInFrom(getEventEObjects(dtEvent));
                        edit(selectedOp);
                    }
                });
            }
        }

        private boolean dragSourceIsDocument( DropTargetEvent event ) {
            boolean result = false;
            List eObjList = getEventEObjects(event);
            if (!eObjList.isEmpty() && eObjList.size() == 1) {
                result = (eObjList.get(0) instanceof XmlDocument);
            }

            return result;
        }

        @Override
        public void executeDropInFrom( List dropList ) {
            if (dropList.size() != 1) {
                return;
            }
            SqlTransformationMappingRoot transformation = getTransformation();
            // We need to see if we can add the list of objects to the transformation
            boolean canAdd = false;

            if (transformation != null) canAdd = TransformationSourceManager.canAdd(transformation, dropList, this);

            if (canAdd) {
                XmlDocument document = null;

                if (!dropList.isEmpty() && dropList.size() == 1) {
                    if (dropList.get(0) instanceof XmlDocument) {
                        document = (XmlDocument)dropList.get(0);
                    }
                }
                if (document != null) {
                    if (sqlIsEmpty()) {
                        WebServiceUiUtil.addXmlDocumentAsSource(transformation, document, this);
                    } else {
                        // Confirm that the user wants to proceed.
                        boolean shouldResetSource = confirmSourceReset();
                        if (shouldResetSource) {
                            WebServiceUiUtil.setXmlDocumentAsSource(transformation, document, this);
                        }
                    }
                }

            } else {
                // Need to throw up a dialog stating that user can't add these objects to the
                TransformationSourceManager.warnUserAboutInvalidSources(dropList);
            }
        }

        @Override
        public boolean sqlIsEmpty() {
            DisplayNode firstVisibleNode = sqlPanel.getQueryDisplayComponent().getFirstVisibleNode();
            if (firstVisibleNode == null) {
                return true;
            }

            return false;
        }
    }

    /* 
     * Method to confirm that the user wants to reset the xml document source 
     * @return 'true' if doc source is to be reset, 'false' if not.
     */
    boolean confirmSourceReset() {
        boolean resetSource = false;
        // Check if callbacks are disabled
        IPreferenceStore prefStore = UiPlugin.getDefault().getPreferenceStore();
        boolean disableCallbacks = prefStore.getBoolean(PluginConstants.Prefs.Callbacks.DISABLE_CALLBACKS);
        if (!disableCallbacks) {
            // Prompt whether to reset the source document
            resetSource = MessageDialog.openQuestion(null, DROP_CONFIRM_TITLE, DROP_CONFIRM_MSG);
        }
        return resetSource;
    }

    public boolean allowsExternalEdits() {
        return this.allowsExternalEdits;
    }

    public void setAllowsExternalEdits( boolean theAllowExternalEdits ) {
        this.allowsExternalEdits = theAllowExternalEdits;
    }
}
