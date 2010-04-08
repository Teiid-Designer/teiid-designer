/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.ui.celleditor.ExtendedDialogCellEditor;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceSelections;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertyDescriptor;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * CustomPropertyOptionsWizardPanel. This panel contains options for applying custom properties.
 */
public class CustomPropertyOptionsWizardPanel extends Composite implements ModelGeneratorUiConstants, CoreStringUtil.Constants {

    /** Column headers for the custom property table. */
    static final String[] CUSTOM_PROPS_TBL_HDRS;

    /** Index of the name column in the custom property table. */
    private static final int SRC_UML_NAME_COLUMN;

    /** Index of the UML datatype column in the custom property table. */
    private static final int SRC_UML_DATATYPE_COLUMN;

    /** Index of the path column in the custom property table. */
    static final int TGT_RELATIONAL_COLUMN;

    /** Title for the panel group */
    private static final String GROUP_TITLE = Util.getString("CustomPropertyOptionsWizardPanel.title"); //$NON-NLS-1$

    static {
        // set column indexes
        SRC_UML_NAME_COLUMN = 0;
        SRC_UML_DATATYPE_COLUMN = 1;
        TGT_RELATIONAL_COLUMN = 2;

        // set column headers
        CUSTOM_PROPS_TBL_HDRS = new String[3];
        CUSTOM_PROPS_TBL_HDRS[SRC_UML_NAME_COLUMN] = Util.getString("CustomPropertyOptionsWizardPanel.table.col1.text"); //$NON-NLS-1$
        CUSTOM_PROPS_TBL_HDRS[SRC_UML_DATATYPE_COLUMN] = Util.getString("CustomPropertyOptionsWizardPanel.table.col2.text"); //$NON-NLS-1$
        CUSTOM_PROPS_TBL_HDRS[TGT_RELATIONAL_COLUMN] = Util.getString("CustomPropertyOptionsWizardPanel.table.col3.text"); //$NON-NLS-1$
    }

    private ModelResource sourceModelResource;
    private GeneratorManagerOptions generatorMgrOptions;

    private Button useCustomPropsCheckbox;

    /** Viewer for custom properties table. */
    TreeViewer customPropsViewer;
    private Map stereotypeUMLPropMap;
    private List stereotypeBindingList;
    private List propertyStereotypeList;
    private List classStereotypeList;

    public CustomPropertyOptionsWizardPanel( Composite parent,
                                             GeneratorManagerOptions generatorMgrOptions ) {
        super(parent, SWT.NULL);
        this.generatorMgrOptions = generatorMgrOptions;

        initialize();
    }

    /**
     * Initialize the Panel
     */
    private void initialize() {
        GridLayout layout = new GridLayout();
        this.setLayout(layout);
        createCustomPropertyOptionsPanel(this);
    }

    /**
     * Create the CustomProperty Options Content Panel
     * 
     * @param parent the parent composite
     * @return the content panel
     */
    private Composite createCustomPropertyOptionsPanel( Composite parent ) {
        Composite panel = new Composite(parent, SWT.NONE);
        // Set grid layout
        GridLayout gridLayout = new GridLayout(2, false);
        panel.setLayout(gridLayout);
        panel.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Group customPropertyGrp = WidgetFactory.createGroup(panel, GROUP_TITLE, GridData.FILL_BOTH);
        {
            // Set grp layout
            GridLayout grpLayout = new GridLayout();
            grpLayout.numColumns = 1;
            customPropertyGrp.setLayout(grpLayout);

            // Get the Source Models Selected
            ModelWorkspaceSelections umlSourceSelections = this.generatorMgrOptions.getModelWorkspaceUmlInputSelections();
            List sourceModelResources;
            try {
                sourceModelResources = umlSourceSelections.getSelectedOrPartiallySelectedModelResources();
            } catch (ModelWorkspaceException err) {
                sourceModelResources = Collections.EMPTY_LIST;
            }

            this.sourceModelResource = null;
            if (!sourceModelResources.isEmpty()) {
                this.sourceModelResource = (ModelResource)sourceModelResources.get(0);
            }

            // Get the custom Property Stereotypes
            List customStereotypes = getCustomPropertyStereotypes(sourceModelResource);

            // There is at least one custom stereotype
            if (!customStereotypes.isEmpty()) {
                String useCustomText = Util.getString("CustomPropertyOptionsWizardPanel.useCustomCheckbox.text"); //$NON-NLS-1$
                String useCustomTip = Util.getString("CustomPropertyOptionsWizardPanel.useCustomCheckbox.tip"); //$NON-NLS-1$
                useCustomPropsCheckbox = WidgetFactory.createCheckBox(customPropertyGrp, useCustomText, true);
                useCustomPropsCheckbox.setToolTipText(useCustomTip);
                createCustomPropsTableViewer(customPropertyGrp, customStereotypes);
                // There are no custom stereotype
            } else {
                String title = Util.getString("CustomPropertyOptionsWizardPanel.noProperties.text"); //$NON-NLS-1$
                WidgetFactory.createLabel(customPropertyGrp, title);
            }

        }
        return panel;
    }

    /**
     * Create the CustomProperty TableTreeViewer
     * 
     * @param theParent the parent composite
     * @param stereotypes the list of stereotypes
     */
    private void createCustomPropsTableViewer( Composite theParent,
                                               List stereotypes ) {
        // table
        int style = SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION;
        Tree tree = new Tree(theParent, style);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));

        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);

        this.customPropsViewer = new TreeViewer(tree);
        this.customPropsViewer.setContentProvider(new PropBindingContentProvider());
        this.customPropsViewer.setLabelProvider(new PropBindingLabelProvider());

        this.customPropsViewer.addTreeListener(new ITreeViewerListener() {
            public void treeCollapsed( TreeExpansionEvent theEvent ) {
            }

            public void treeExpanded( TreeExpansionEvent theEvent ) {
                handleTreeExpanded(theEvent.getElement());
            }
        });

        // create columns and editors
        CellEditor[] cellEditors = new CellEditor[CUSTOM_PROPS_TBL_HDRS.length];
        for (int i = 0; i < CUSTOM_PROPS_TBL_HDRS.length; i++) {
            TreeColumn col = new TreeColumn(tree, SWT.LEFT);
            col.setText(CUSTOM_PROPS_TBL_HDRS[i]);

            if (i == TGT_RELATIONAL_COLUMN) {
                cellEditors[i] = createRelationalPropCellEditor(tree);
            } else {
                cellEditors[i] = null;
            }
        }

        this.customPropsViewer.setColumnProperties(CUSTOM_PROPS_TBL_HDRS);
        this.customPropsViewer.setCellEditors(cellEditors);
        this.customPropsViewer.setCellModifier(new PropBindingCellModifier());

        // initialize map of steretype to its target relational property name
        setStereotypeUMLPropMap(stereotypes);

        // The input for the table viewer is the array of Stereotype bindings

        this.stereotypeBindingList = createBindingList(stereotypes);
        this.customPropsViewer.setInput(this);
        packTableColumns();
    }

    /**
     * Get the stereotype bindings list
     * 
     * @return the stereotype BindingList
     */
    List getStereotypeBindingList() {
        return this.stereotypeBindingList;
    }

    /**
     * Create the BindingsList from a list of stereotypes
     * 
     * @param stereotypes the list of stereotypes
     * @return the stereotype BindingList
     */
    private List createBindingList( List stereotypes ) {
        // result binding list
        List bindingList = new ArrayList(stereotypes.size());
        // Name list (used to eliminate duplicates)
        List propertyNames = new ArrayList();
        this.classStereotypeList = new ArrayList(stereotypes.size());
        this.propertyStereotypeList = new ArrayList(stereotypes.size());

        // Iterate the stereotypes, create initial bindings
        Iterator iter = stereotypes.iterator();
        while (iter.hasNext()) {
            Stereotype stereotype = (Stereotype)iter.next();
            // Create binding for the stereotype
            PropBinding stereoBinding = new PropBinding(stereotype);

            // Create bindings for the applicable stereotype properties
            List children = stereotype.eContents();
            List childList = new ArrayList();
            Iterator citer = children.iterator();
            while (citer.hasNext()) {
                Object child = citer.next();
                if (child instanceof Property) {
                    String propName = ((Property)child).getName();
                    if (!propName.startsWith("baseClass_")) { //$NON-NLS-1$
                        // If a binding for the Property name already created, dont duplicate
                        if (!propertyNames.contains(propName)) {
                            childList.add(new PropBinding(child));
                            propertyNames.add(propName);
                        }
                    } else {
                        if (propName.equals("baseClass_Property")) { //$NON-NLS-1$
                            this.propertyStereotypeList.add(stereotype);
                        } else if (propName.equals("baseClass_Classifier")) { //$NON-NLS-1$
                            this.classStereotypeList.add(stereotype);
                        }
                    }
                }

            }
            // Set the number of children on the stereotype binding
            stereoBinding.setTargetProp(childList);
            bindingList.add(stereoBinding);
        }
        return bindingList;
    }

    /**
     * Get the Custom Properties TableTreeViewer
     * 
     * @return the CustomProperties TableTreeViewer
     */
    private TreeViewer getCustomPropsViewer() {
        return this.customPropsViewer;
    }

    /**
     * Pack the Custom Properties TableTreeViewer
     */
    private void packTableColumns() {
        // units table
        Tree tree = getCustomPropsViewer().getTree();
        for (int i = 0; i < CUSTOM_PROPS_TBL_HDRS.length; tree.getColumn(i++).pack()) {
        }
    }

    /**
     * Set the Stereotype UML Custom Properties Map
     * 
     * @param stereotypes the list of stereotypes
     */
    private void setStereotypeUMLPropMap( List stereotypes ) {
        // Create the map
        this.stereotypeUMLPropMap = new HashMap();

        // Iterate the stereotypes
        Iterator iter = stereotypes.iterator();
        while (iter.hasNext()) {
            Stereotype stereotype = (Stereotype)iter.next();
            // Get the stereotype children
            List children = stereotype.eContents();
            Iterator citer = children.iterator();
            while (citer.hasNext()) {
                Object child = citer.next();
                // The stereotypes for custom properties start with 'baseClass'
                if (child instanceof Property) {
                    String propName = ((Property)child).getName();
                    if (propName.startsWith("baseClass_")) { //$NON-NLS-1$
                        String stereoDisplayName = getStereotypeDisplayName(stereotype);
                        String displayName = propName.substring(10, propName.length()) + " (" + stereoDisplayName + ")"; //$NON-NLS-1$  //$NON-NLS-2$
                        this.stereotypeUMLPropMap.put(stereotype, displayName);
                        break;
                    }
                }

            }
        }
    }

    private String getStereotypeDisplayName( Stereotype stereotype ) {
        String displayName = null;
        String stereoName = stereotype.getName();
        if (stereoName.startsWith("default__")) { //$NON-NLS-1$
            displayName = "Rose " + stereoName.substring(9, stereoName.length()); //$NON-NLS-1$
        }
        return displayName;
    }

    /**
     * Get the UML Property name for the provided stereotype. Does a lookup from the previously constructed Stereotype -
     * UMLPropName Map
     * 
     * @param stereotype the stereotype
     * @return the UML PropertyName for the supplied stereotype
     */
    String getStereotypeUMLPropName( Stereotype stereotype ) {
        if (this.stereotypeUMLPropMap != null) {
            return (String)this.stereotypeUMLPropMap.get(stereotype);
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Handler for tree expansion. (packs the table columns)
     * 
     * @param theObject the expanded object
     */
    void handleTreeExpanded( Object theObject ) {
        getCustomPropsViewer().getTree().getColumn(SRC_UML_NAME_COLUMN).pack();
    }

    /**
     * Get the CustomProperty Stereotypes for the supplied ModelResource.
     * 
     * @param modelResource the ModelResource
     * @return the List of custom property Stereotypes
     */
    private List getCustomPropertyStereotypes( ModelResource modelResource ) {
        // Get the stereotype Profile
        Profile stereotypeProfile = getStereotypeProfileForModel(modelResource);

        // Get the custom property stereotypes
        return getCustomPropertyStereotypes(stereotypeProfile);
    }

    /**
     * Get the Stereotype Profile for the supplied ModelResource. If it does not exist, null is returned
     * 
     * @param modelResource the ModelResource
     * @return the StereotypeProfile for the supplied ModelResource
     */
    private Profile getStereotypeProfileForModel( ModelResource modelResource ) {
        Profile stereotypeProfile = null;

        if (modelResource != null) {
            // Get the Model Root EObjects and find the Stereotype profile
            List modelRootEObjects;
            try {
                modelRootEObjects = modelResource.getAllRootEObjects();
            } catch (ModelWorkspaceException err1) {
                modelRootEObjects = Collections.EMPTY_LIST;
            }
            // Iterate the rootEObjects and find the Stereotypes Profile
            Iterator iter = modelRootEObjects.iterator();
            while (iter.hasNext()) {
                Object rootObj = iter.next();
                if (rootObj instanceof Profile) {
                    Profile profile = (Profile)rootObj;
                    if ("stereotypes".equals(profile.getName())) { //$NON-NLS-1$
                        stereotypeProfile = profile;
                        break;
                    }
                }

            }
        }

        return stereotypeProfile;
    }

    /**
     * Get the CustomProperty Stereotypes from a Stereotype Profile. This is strictly a name match - if the stereotype name starts
     * with 'default__', it is a custom property stereotype
     * 
     * @param stereotypeProfile the profile to look for custom property stereotypes.
     * @return the List of custom property Stereotypes
     */
    private List getCustomPropertyStereotypes( Profile stereotypeProfile ) {
        // result list
        List resultList = new ArrayList();

        if (stereotypeProfile != null) {
            // Iterate profile children and find custom property steretypes
            List children = stereotypeProfile.eContents();
            Iterator iter = children.iterator();
            while (iter.hasNext()) {
                Object obj = iter.next();
                if (obj instanceof Stereotype) {
                    Stereotype stereo = (Stereotype)obj;
                    String stereoName = stereo.getName();
                    if (stereoName != null && stereoName.startsWith("default__")) { //$NON-NLS-1$
                        // Iterate the stereotype children, keep UML Property and Class
                        List sChildren = stereo.eContents();
                        Iterator citer = sChildren.iterator();
                        while (citer.hasNext()) {
                            Object child = citer.next();
                            // The stereotypes for custom properties start with 'baseClass'
                            if (child instanceof Property) {
                                String propName = ((Property)child).getName();
                                if (propName.startsWith("baseClass_")) { //$NON-NLS-1$
                                    if (propName.equals("baseClass_Property") || //$NON-NLS-1$
                                        propName.equals("baseClass_Classifier")) { //$NON-NLS-1$
                                        resultList.add(stereo);
                                        break;
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }

        return resultList;
    }

    /**
     * Get the Custom Property Mappings to be used for generating relational Columns
     * 
     * @return the custom Properties Map
     */
    public Map getColumnCustomPropsMap() {
        // Result Map
        Map resultMap = new HashMap();

        // If the 'Use CustomProperties' checkbox is selected, build map
        // If not selected, the empty map is returned.
        if (useCustomPropsCheckbox.getSelection()) {
            // Iterate thru the stereotype bindings
            List stereoBindings = getStereotypeBindingList();
            Iterator stereoIter = stereoBindings.iterator();
            while (stereoIter.hasNext()) {
                // Stereotype binding
                PropBinding stereoBinding = (PropBinding)stereoIter.next();
                Stereotype stereotype = (Stereotype)stereoBinding.getSourceProp();
                if (this.propertyStereotypeList.contains(stereotype)) {
                    // Target is List of property bindings
                    List propBindingList = (List)stereoBinding.getTargetProp();
                    // Iterate thru property bindings
                    Iterator propIter = propBindingList.iterator();
                    while (propIter.hasNext()) {
                        PropBinding propBinding = (PropBinding)propIter.next();
                        // If binding has a target, add it to the map
                        Object targetProp = propBinding.getTargetProp();
                        if (targetProp != null) {
                            String targetStr = null;
                            if (targetProp instanceof ModelObjectPropertyDescriptor) {
                                Object feature = ((ModelObjectPropertyDescriptor)targetProp).getFeature();
                                if (feature instanceof EStructuralFeature) {
                                    targetStr = ((EStructuralFeature)feature).getName();
                                }
                            } else if (targetProp instanceof String) {
                                targetStr = (String)targetProp;
                            }
                            Property prop = (Property)propBinding.getSourceProp();
                            String propName = prop.getName();
                            Object obj = prop.getOwner().getValue(stereotype, propName);
                            // Object obj = prop.getValue(stereotype,prop.getName());
                            // Object obj = prop.getValue(stereotype, targetStr);
                            resultMap.put(targetStr, obj);
                        }
                    }
                }
            }
        }
        return resultMap;
    }

    /**
     * Get the Custom Property Mappings to be used for generating relational tables
     * 
     * @return the custom Properties Map
     */
    public Map getTableCustomPropsMap() {
        // Result Map
        Map resultMap = new HashMap();

        // If the 'Use CustomProperties' checkbox is selected, build map
        // If not selected, the empty map is returned.
        if (useCustomPropsCheckbox.getSelection()) {
            // Iterate thru the stereotype bindings
            List stereoBindings = getStereotypeBindingList();
            Iterator stereoIter = stereoBindings.iterator();
            while (stereoIter.hasNext()) {
                // Stereotype binding
                PropBinding stereoBinding = (PropBinding)stereoIter.next();
                Stereotype stereotype = (Stereotype)stereoBinding.getSourceProp();
                if (this.classStereotypeList.contains(stereotype)) {
                    // Target is List of property bindings
                    List propBindingList = (List)stereoBinding.getTargetProp();
                    // Iterate thru property bindings
                    Iterator propIter = propBindingList.iterator();
                    while (propIter.hasNext()) {
                        PropBinding propBinding = (PropBinding)propIter.next();
                        // If binding has a target, add it to the map
                        Object targetProp = propBinding.getTargetProp();
                        if (targetProp != null) {
                            String targetStr = null;
                            if (targetProp instanceof ModelObjectPropertyDescriptor) {
                                Object feature = ((ModelObjectPropertyDescriptor)targetProp).getFeature();
                                if (feature instanceof EStructuralFeature) {
                                    targetStr = ((EStructuralFeature)feature).getName();
                                }
                            } else if (targetProp instanceof String) {
                                targetStr = (String)targetProp;
                            }
                            Property prop = (Property)propBinding.getSourceProp();
                            String propName = prop.getName();
                            Object obj = prop.getOwner().getValue(stereotype, propName);
                            // Object obj = prop.getValue(stereotype,prop.getName());
                            // Object obj = prop.getValue(stereotype, targetStr);
                            resultMap.put(targetStr, obj);
                        }
                    }
                }
            }
        }
        return resultMap;
    }

    /**
     * Create cell editor for modification of the relational properties cell. The editor pops up a PropertySelectionDialog, where
     * the user can select from an existing relational property or enter a new user-defined property in a text field.
     * 
     * @param composite the parent composite
     * @return the relational property CellEditor
     */
    private CellEditor createRelationalPropCellEditor( final Composite composite ) {
        return new ExtendedDialogCellEditor(composite, new PropBindingLabelProvider()) {
            @Override
            protected Object openDialogBox( Control cellEditorWindow ) {
                PropertySelectionDialog dialog = new PropertySelectionDialog(composite.getShell(), getSelectedPropBinding());
                Object originalValue = getValue();
                Object[] selection = new Object[] {originalValue};
                // selection[0] = getValue();
                dialog.setInitialSelections(selection);

                int status = dialog.open();
                if (status == Window.OK) {
                    Object dialogResult = null;
                    // PropertySelectionDialog - existing Property selected
                    if (dialog.isSelectRelationalRadioSelected()) {
                        Object[] result = dialog.getResult();
                        if (result.length != 0) {
                            dialogResult = result[0];
                        }
                        // PropertySelectionDialog - user-entered Property selected
                    } else {
                        dialogResult = dialog.getUserDefinedPropertyText();
                    }
                    return dialogResult;
                }
                // return the original object
                return originalValue;
            }
        };
    }

    /**
     * get the selected PropBinding from the custom properties selection table
     * 
     * @return the selected PropBinding
     */
    PropBinding getSelectedPropBinding() {
        // result PropBinding
        PropBinding selectedBinding = null;
        // get the current table selection
        TreeItem[] selections = this.customPropsViewer.getTree().getSelection();
        // ensure something is selected
        if (selections != null && selections.length != 0) {
            TreeItem selection = selections[0];
            Object dataObject = selection.getData("TreeItemID"); //$NON-NLS-1$
            if (dataObject instanceof TreeItem) {
                // TableTreeItem
                TreeItem ttItem = (TreeItem)dataObject;
                // DataObject is the PropBinding
                Object data = ttItem.getData();
                if (data instanceof PropBinding) {
                    selectedBinding = (PropBinding)data;
                }
            }
        }
        return selectedBinding;
    }

    class PropBindingCellModifier implements ICellModifier {

        public PropBindingCellModifier() {
        }

        /**
         * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
         */
        public boolean canModify( Object element,
                                  String property ) {
            boolean result = false;
            if (CUSTOM_PROPS_TBL_HDRS[TGT_RELATIONAL_COLUMN].equals(property)) {
                PropBinding binding = (PropBinding)element;
                // Can edit unless the binding source is Stereotype
                if (!(binding.getSourceProp() instanceof Stereotype)) {
                    result = true;
                }
            }
            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
         */
        public Object getValue( Object element,
                                String property ) {
            Object result = null;
            if (CUSTOM_PROPS_TBL_HDRS[TGT_RELATIONAL_COLUMN].equals(property)) {
                PropBinding binding = (PropBinding)element;
                result = binding.getTargetProp();
            }
            return result;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
         */
        public void modify( Object element,
                            String property,
                            Object value ) {
            if (CUSTOM_PROPS_TBL_HDRS[TGT_RELATIONAL_COLUMN].equals(property)) {
                if (element instanceof PropBinding) {
                    PropBinding binding = (PropBinding)element;
                    binding.setTargetProp(value);
                    customPropsViewer.refresh(true);
                } else if (element instanceof TreeItem) {
                    // the PropBinding is inside the TableItem
                    Object input = ((TreeItem)element).getData();
                    modify(input, property, value);
                }
            }
        }

    }

    class PropBindingContentProvider implements IStructuredContentProvider, ITreeContentProvider {

        /** Contains the custom stereotypes. */
        private Object[] rootArray;

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         * @since 4.1
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         * @since 4.1
         */
        public Object[] getChildren( Object theParentElement ) {
            if (theParentElement instanceof PropBinding) {
                PropBinding binding = (PropBinding)theParentElement;
                Object sourceProp = binding.getSourceProp();
                // If the sourceUML property is Stereotype, then target is the list of children
                if (sourceProp instanceof Stereotype) {
                    List children = (List)binding.getTargetProp();
                    return children.toArray();
                }
            }
            return Collections.EMPTY_LIST.toArray();
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         * @since 4.1
         */
        public Object[] getElements( Object theInputElement ) {
            return this.rootArray;
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         * @since 4.1
         */
        public Object getParent( Object theElement ) {
            // return (getSourceUnit() == theElement) ? null : ((IUnit)theElement).getContainingUnit();
            return null;
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         * @since 4.1
         */
        public boolean hasChildren( Object theElement ) {
            return getChildren(theElement).length > 0;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 4.1
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {
            this.rootArray = getStereotypeBindingList().toArray();
        }

    }

    /**
     * PropBindingLabelProvider is a specialization of LabelProvider that allows us to display or hide additional features of a
     * model in the outline view.
     */
    public class PropBindingLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object element,
                                     int columnIndex ) {
            PropBinding binding = (PropBinding)element;
            String result = ""; //$NON-NLS-1$
            switch (columnIndex) {
                case 0: // Source UML Column
                    Object sourceProp = binding.getSourceProp();
                    if (sourceProp instanceof Stereotype) {
                        Stereotype stereotype = (Stereotype)sourceProp;
                        result = getStereotypeUMLPropName(stereotype);
                    } else if (sourceProp instanceof Property) {
                        Property prop = (Property)sourceProp;
                        result = prop.getName();
                    }
                    break;
                case 1: // Source UML Column datatype
                    Object srcProp = binding.getSourceProp();
                    if (srcProp instanceof Property) {
                        Property prop = (Property)srcProp;
                        DataType dtype = prop.getDatatype();
                        if (dtype != null) {
                            result = dtype.getName();
                        } else {
                            result = "unkown"; //$NON-NLS-1$
                        }
                    } else if (srcProp instanceof Stereotype) {
                        result = ""; //$NON-NLS-1$
                    } else {
                        result = "NA"; //$NON-NLS-1$
                    }
                    break;
                case 2: // Target Relational Column
                    Object targetProp = binding.getTargetProp();
                    if (targetProp instanceof IPropertyDescriptor) {
                        result = ((IPropertyDescriptor)targetProp).getDisplayName();
                    } else if (targetProp instanceof String) {
                        result = (String)targetProp;
                    }
                    break;
                default:
                    break;
            }
            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage( Object element,
                                     int columnIndex ) {
            return null;
        }

    }

}// end CustomPropertyOptionsWizardPanel
