/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui.editor;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.type.Type;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.internal.core.xml.XPathHelper;
import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.xsd.XsdUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.ui.OverlayImageIcon;
import com.metamatrix.modeler.webservice.procedure.DocumentGenerator;
import com.metamatrix.modeler.webservice.procedure.XsdInstanceNode;
import com.metamatrix.modeler.webservice.ui.WebServiceUiPlugin;
import com.metamatrix.modeler.webservice.ui.util.WebServiceUiUtil;
import com.metamatrix.modeler.webservice.util.WebServiceUtil;
import com.metamatrix.query.internal.ui.sqleditor.component.AssignmentStatementDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.BlockDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.DeclareStatementDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.DisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.DisplayNodeFactory;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.proc.AssignmentStatement;
import com.metamatrix.query.sql.proc.DeclareStatement;
import com.metamatrix.query.sql.proc.Statement;
import com.metamatrix.ui.graphics.GlobalUiColorManager;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.DefaultTreeViewerController;
import com.metamatrix.ui.internal.widget.Dialog;
import com.metamatrix.ui.tree.AbstractTreeContentProvider;

/**
 * @since 4.3
 */
public class VariableEditorDialog extends Dialog
    implements IInternalUiConstants, IInternalUiConstants.Images, StringUtil.Constants {

    private static final String I18N_PFX = I18nUtil.getPropertyPrefix(VariableEditorDialog.class);

    private static final WebServiceUiPlugin PLUGIN = WebServiceUiPlugin.getDefault();

    private static final String NULL_ELEMENT_MSG = UTIL.getString(I18N_PFX + "nullElementMsg"); //$NON-NLS-1$
    private static final String NULL_INPUT_MSG = UTIL.getString(I18N_PFX + "nullInputMsg"); //$NON-NLS-1$

    private static final String REQUEST_DOC_DESC = UTIL.getString(I18N_PFX + "requestDocumentDescription"); //$NON-NLS-1$
    private static final String REQUEST_DOC_TITLE = UTIL.getString(I18N_PFX + "requestDocumentTitle"); //$NON-NLS-1$
    private static final String TITLE = UTIL.getString(I18N_PFX + "title"); //$NON-NLS-1$
    private static final String VAR_XPATH_TITLE = I18N_PFX + "variableXpathTitle"; //$NON-NLS-1$
    private static final String VARS_DESC = UTIL.getString(I18N_PFX + "variablesDescription"); //$NON-NLS-1$
    private static final String XPATH_TITLE = UTIL.getString(I18N_PFX + "xpathTitle"); //$NON-NLS-1$

    private static final String X_PREF = I18N_PFX + "x"; //$NON-NLS-1$
    private static final String Y_PREF = I18N_PFX + "y"; //$NON-NLS-1$
    private static final String WTH_PREF = I18N_PFX + "width"; //$NON-NLS-1$
    private static final String HGT_PREF = I18N_PFX + "height"; //$NON-NLS-1$

    private static final int DFLT_LEFT_H_WGT = 750;
    private static final int DFLT_RIGHT_H_WGT = 250;
    private static final int DFLT_TOP_V_WGT = 750;
    private static final int DFLT_BOTTOM_V_WGT = 250;

    private static final String LEFT_H_WGT_PREF = I18N_PFX + "leftHorizontalWeight"; //$NON-NLS-1$
    private static final String RIGHT_H_WGT_PREF = I18N_PFX + "rightHorizontalWeight"; //$NON-NLS-1$
    private static final String TOP_V_WGT_PREF = I18N_PFX + "leftVerticalWeight"; //$NON-NLS-1$
    private static final String BOTTOM_V_WGT_PREF = I18N_PFX + "rightVerticalWeight"; //$NON-NLS-1$

    private OperationObjectEditorPage editor;
    private Input input;
    private XsdInstanceNode rootNode;
    private Map nodesToDeclarations;
    private TreeViewer nodeViewer;
    private InputVariableSection varSection;
    private Section xpathSection;
    private StyledText xpathText;
    private Color normalBkgd, recursionBkgd;
    private Set highlightedRecursionItems = new HashSet(); // TreeItems
    private IStructuredSelection selection;
    private SashForm hSplitter, vSplitter;

    public VariableEditorDialog( Shell shell,
                                 IStructuredSelection selection,
                                 OperationObjectEditorPage editor ) {
        super(shell, TITLE);
        this.editor = editor;
        this.input = ((Operation)editor.getCurrentMappingRoot().getTarget()).getInput();
        if (this.input != null) {
            XSDElementDeclaration element = this.input.getContentElement();
            if (element != null) {
                this.rootNode = new XsdInstanceNode(element);
            }
        }
        this.nodesToDeclarations = new HashMap(editor.getDeclarationsToAssignments().size());
        this.selection = selection;
    }

    private void addDisplayNode( Statement statement ) {
        BlockDisplayNode block = this.editor.findBlock();
        DisplayNode newNode = DisplayNodeFactory.createDisplayNode(block, statement);
        if (newNode != null) {
            newNode.setVisible(false, true);
            block.getChildren().add(0, newNode);
            // Add new statement's display nodes to block and its ancestors
            List newNodes = newNode.getDisplayNodeList();
            for (DisplayNode ancestor = block; ancestor != null; ancestor = ancestor.getParent()) {
                List nodes = ancestor.getDisplayNodeList();
                for (int ndx = 0; ndx < nodes.size(); ++ndx) {
                    DisplayNode node = (DisplayNode)nodes.get(ndx);
                    if (node.getParent() == block && ReservedWords.BEGIN.equals(node.toString())) {
                        nodes.addAll(ndx + 2, newNodes);
                        break;
                    }
                } // for
            } // for
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
     * @since 5.0.1
     */
    @Override
    protected Control createContents( Composite parent ) {
        Control ctrl = super.createContents(parent);
        Button button = getButton(Window.CANCEL);
        ((GridLayout)button.getParent().getLayout()).numColumns--;
        button.dispose();
        // Restore the previously saved size or, if not present, set the default size
        IPreferenceStore store = WebServiceUiPlugin.getDefault().getPreferenceStore();
        int wth = store.getInt(WTH_PREF);
        int hgt = store.getInt(HGT_PREF);
        if (wth == 0) {
            setSizeRelativeToScreen(60, 50);
        } else {
            getShell().setSize(wth, hgt);
        }
        // Restore the previously saved location or, if not present, center it
        int x = store.getInt(X_PREF);
        int y = store.getInt(Y_PREF);
        if (x == 0) {
            setCenterOnDisplay(true);
        } else {
            getShell().setLocation(x, y);
        }
        // Create listener to save dialog bounds to preferences as it changes
        getShell().addControlListener(new ControlAdapter() {

            @Override
            public void controlMoved( ControlEvent event ) {
                updateLocationPreferences();
            }

            @Override
            public void controlResized( ControlEvent event ) {
                updateSizePreferences();
            }
        });
        return ctrl;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 5.0.1
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        parent = (Composite)super.createDialogArea(parent);
        if (this.input == null) {
            WidgetFactory.createStyledTextBox(parent, NULL_INPUT_MSG);
            return parent;
        }
        if (this.rootNode == null) {
            WidgetFactory.createStyledTextBox(parent, NULL_ELEMENT_MSG);
            return parent;
        }
        FillLayout fillLayout = new FillLayout();
        parent.setLayout(fillLayout);
        FormToolkit toolkit = WebServiceUiPlugin.getDefault().getFormToolkit(parent.getDisplay());
        Composite form = toolkit.createForm(parent).getBody();
        form.setLayout(fillLayout);
        this.vSplitter = new SashForm(form, SWT.VERTICAL);
        this.hSplitter = WidgetFactory.createSplitter(this.vSplitter);
        Section section = toolkit.createSection(this.hSplitter, ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
        section.setText(REQUEST_DOC_TITLE);
        section.setDescription(REQUEST_DOC_DESC);
        toolkit.paintBordersFor(section);
        this.nodeViewer = WidgetFactory.createTreeViewer2(section, SWT.CHECK, new DefaultTreeViewerController() {

            @Override
            public void checkedStateToggled( TreeItem item ) {
                nodeCheckedStateToggled(item);
            }

            @Override
            public boolean isItemCheckable( TreeItem item ) {
                return isNodeCheckable(item);
            }

            @Override
            public void itemExpanded( TreeExpansionEvent event ) {
                nodeExpanded(event);
            }

            @Override
            public void itemSelected( SelectionChangedEvent event ) {
                nodeSelected(event);
            }

            @Override
            public void update( TreeItem item,
                                boolean checked ) {
                nodeUpdated(item);
            }
        });
        section.setClient(this.nodeViewer.getTree());
        this.nodeViewer.setContentProvider(new AbstractTreeContentProvider() {

            @Override
            public Object[] getChildren( Object element ) {
                return getNodeChildren(element);
            }

            public Object getParent( Object element ) {
                return getNodeParent(element);
            }

            @Override
            public boolean hasChildren( Object element ) {
                return getNodeHasChildren(element);
            }
        });
        this.nodeViewer.setLabelProvider(new LabelProvider() {

            @Override
            public Image getImage( Object element ) {
                return getNodeImage(element);
            }

            @Override
            public String getText( Object element ) {
                return getNodeName(element);
            }
        });
        this.varSection = new InputVariableSection(hSplitter, VARS_DESC, editor) {

            @Override
            protected void variablesDeleted( List entries ) {
                updateDeletedNodes(entries);
            }

            @Override
            protected void variableRenamed( Entry entry ) {
                updateNodesToDeclarations();
            }
        };
        this.varSection.create();
        this.varSection.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged( SelectionChangedEvent event ) {
                variableSelected();
            }
        });
        this.xpathSection = toolkit.createSection(vSplitter, ExpandableComposite.TITLE_BAR);
        this.xpathSection.setText(XPATH_TITLE);
        toolkit.paintBordersFor(this.xpathSection);
        this.xpathText = new StyledText(this.xpathSection, SWT.WRAP);
        this.xpathText.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
        this.xpathSection.setClient(this.xpathText);
        this.xpathText.setEditable(false);
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
        // Create listener to save splitter weights to preferences before splitters are disposed
        this.nodeViewer.getTree().getParent().addControlListener(new ControlAdapter() {

            @Override
            public void controlResized( ControlEvent event ) {
                updateWeightPreferences();
            }
        });
        // Initialize viewers
        this.nodeViewer.setInput(this);
        // Expand the tree to the first level
        this.nodeViewer.expandToLevel(2);
        // Save "normal" background color
        this.normalBkgd = this.nodeViewer.getTree().getItem(0).getBackground();
        // Create checked background color
        this.recursionBkgd = GlobalUiColorManager.getColor(new RGB(204, 204, 255));
        // Select nodes that are targets of existing variables
        updateNodesToDeclarations();
        // Update root node checkboxes
        updateCheckBoxes(this.nodeViewer.getTree().getItems());
        // Make initial selection
        if (this.selection != null && !this.selection.isEmpty()) {
            this.varSection.select((Entry)this.selection.getFirstElement());
        }
        return parent;
    }

    private XsdInstanceNode getNode( NodeInfo node ) {
        if (node.getParent() instanceof DocumentInfo) {
            return this.rootNode;
        }
        XsdInstanceNode parent = getNode(node.getParent());
        if (parent == null) {
            return null;
        }
        return getNode(node.getLocalPart(), node.getURI(), parent);
    }

    private XsdInstanceNode getNode( String name,
                                     String namespace,
                                     XsdInstanceNode node ) {
        XsdInstanceNode[] children = node.getChildren();
        for (int ndx = 0; ndx < children.length; ++ndx) {
            XsdInstanceNode child = children[ndx];
            if (child.getResolvedXsdComponent() instanceof XSDModelGroup) {
                child = getNode(name, namespace, child);
                if (child != null) {
                    return child;
                }
            } else if (name.equals(child.getName()) && namespace.equals(child.getTargetNamespace())) {
                return child;
            }
        } // for
        return null;
    }

    private XsdInstanceNode getNode( DeclareStatement declaration ) {
        for (Iterator iter = this.nodesToDeclarations.entrySet().iterator(); iter.hasNext();) {
            Entry nodeEntry = (Entry)iter.next();
            if (nodeEntry.getValue().equals(declaration)) {
                return (XsdInstanceNode)nodeEntry.getKey();
            }
        } // for
        return null;
    }

    Object[] getNodeChildren( Object element ) {
        if (element == this) {
            return new Object[] {this.rootNode};
        }
        return ((XsdInstanceNode)element).getChildren();
    }

    boolean getNodeHasChildren( Object element ) {
        XsdInstanceNode node = (XsdInstanceNode)element;
        Object[] children = node.getChildren();
        // Children created lazily, so in case one of node's children dynamically determined to be recursive, update any recursive
        // ancestors.
        for (XsdInstanceNode ancestor = node.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor.isRecursive()) {
                this.nodeViewer.update(ancestor, null);
            }
        }
        return (children.length > 0);
    }

    Image getNodeImage( Object element ) {
        // There is an EMF bug that prevents maxOccurs values other than 1 from being stored correctly for particles with model
        // group definition content, so just show image of underlying model group
        XsdInstanceNode node = (XsdInstanceNode)element;
        XSDConcreteComponent comp = node.getXsdComponent();
        Image img;
        if (comp instanceof XSDParticle) {
            XSDParticle particle = (XSDParticle)comp;
            // Start with image of "real" component
            img = ModelUtilities.getEMFLabelProvider().getImage(particle.getTerm());
            // If non-default minOccurs/maxOccurs, overlay occurs image
            int min = XsdUtil.getMinOccurs(particle);
            int max = XsdUtil.getMaxOccurs(particle);
            if (min != 1 || max != 1) {
                Image overlay;
                if (min == 0) {
                    if (max == 0) {
                        overlay = PLUGIN.getImage(OCCURS_ZERO);
                    } else if (max == 1) {
                        overlay = PLUGIN.getImage(OCCURS_ZERO_TO_ONE);
                    } else if (max > 1) {
                        overlay = PLUGIN.getImage(OCCURS_ZERO_TO_N);
                    } else { // Must be unbounded
                        overlay = PLUGIN.getImage(OCCURS_ZERO_TO_UNBOUNDED);
                    }
                } else if (min == 1) {
                    if (max > 1) {
                        overlay = PLUGIN.getImage(OCCURS_ONE_TO_N);
                    } else { // Must be unbounded
                        overlay = PLUGIN.getImage(OCCURS_ONE_TO_UNBOUNDED);
                    }
                } else { // Min > 1
                    if (max == min) {
                        overlay = PLUGIN.getImage(OCCURS_N);
                    } else if (max > min) {
                        overlay = PLUGIN.getImage(OCCURS_N_TO_M);
                    } else { // Must be unbounded
                        overlay = PLUGIN.getImage(OCCURS_N_TO_UNBOUNDED);
                    }
                }
                img = new OverlayImageIcon(img, overlay, OverlayImageIcon.BOTTOM_LEFT).getImage();
            }
        } else {
            img = ModelUtilities.getEMFLabelProvider().getImage(comp);
        }
        // If recursive, overlay recursive image
        if (node.isRecursive()) {
            return new OverlayImageIcon(img, PLUGIN.getImage(RECURSIVE), OverlayImageIcon.TOP_RIGHT).getImage();
        }
        return img;
    }

    String getNodeName( Object element ) {
        XsdInstanceNode node = (XsdInstanceNode)element;
        XSDConcreteComponent comp = node.getXsdComponent();
        if (comp instanceof XSDParticle) {
            comp = ((XSDParticle)comp).getTerm();
        }
        return ModelUtilities.getEMFLabelProvider().getText(comp);
    }

    Object getNodeParent( Object element ) {
        return ((XsdInstanceNode)element).getParent();
    }

    private void highlightRecursionItems( TreeItem item,
                                          TreeItem selectedItem ) {
        XSDConcreteComponent comp = ((XsdInstanceNode)item.getData()).getXsdComponent();
        if (comp instanceof XSDParticle) {
            comp = ((XSDParticle)comp).getTerm();
        }
        highlightRecursionItems(item, selectedItem, comp);
    }

    private void highlightRecursionItems( TreeItem item,
                                          TreeItem selectedItem,
                                          XSDConcreteComponent rootComponent ) {
        TreeItem[] items = item.getItems();
        for (int ndx = items.length; --ndx >= 0;) {
            TreeItem childItem = items[ndx];
            if (childItem != selectedItem) {
                XsdInstanceNode childNode = (XsdInstanceNode)childItem.getData();
                if (childNode.isRecursive()) {
                    XSDConcreteComponent childComp = childNode.getXsdComponent();
                    if (childComp instanceof XSDParticle) {
                        childComp = ((XSDParticle)childComp).getTerm();
                    }
                    if (childComp == rootComponent) {
                        this.highlightedRecursionItems.add(childItem);
                        childItem.setBackground(this.recursionBkgd);
                    }
                }
            }
            if (childItem.getExpanded()) {
                highlightRecursionItems(childItem, selectedItem, rootComponent);
            }
        }
    }

    private boolean isDescendentOf( DisplayNode node,
                                    DisplayNode ancestor ) {
        DisplayNode parent = node.getParent();
        if (parent == ancestor) {
            return true;
        }
        if (parent == null) {
            return false;
        }
        return isDescendentOf(parent, ancestor);
    }

    boolean isNodeCheckable( TreeItem item ) {
        return ((XsdInstanceNode)item.getData()).isSelectable();
    }

    void nodeCheckedStateToggled( TreeItem item ) {
        XsdInstanceNode node = (XsdInstanceNode)item.getData();
        node.setSelected(item.getChecked());
        if (item.getChecked()) {
            DeclareStatement declaration = WebServiceUiUtil.createDeclareStatement(node, input);
            this.nodesToDeclarations.put(node, declaration);
            WebServiceUiUtil.ensureVariablesUnique(this.nodesToDeclarations);
            this.editor.getDeclarationsToAssignments().put(declaration, declaration);
            addDisplayNode(declaration);
        } else {
            removeVariableStatements((DeclareStatement)this.nodesToDeclarations.remove(node));
        }
        this.varSection.refresh();
        this.editor.refreshVariables();
        this.editor.getCurrentSqlEditor().setHasPendingChanges();
    }

    void nodeExpanded( TreeExpansionEvent event ) {
        // Update checkboxes of expanded item's children
        TreeItem item = WidgetUtil.findTreeItem(event.getElement(), this.nodeViewer);
        updateCheckBoxes(item.getItems());
    }

    void nodeSelected( SelectionChangedEvent event ) {
        // System.out.println("VariableEditorDialog.nodeSelected()");
        // Clear any previously highlighted recursion items
        for (Iterator iter = this.highlightedRecursionItems.iterator(); iter.hasNext();) {
            TreeItem item = (TreeItem)iter.next();
            item.setBackground(this.normalBkgd);
        } // for
        this.nodeViewer.update(this.highlightedRecursionItems.toArray(), null);
        this.highlightedRecursionItems.clear();
        // Handle new selection
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        if (!selection.isEmpty()) {
            XsdInstanceNode node = (XsdInstanceNode)selection.getFirstElement();
            // If recursive, highlight all recursion items for the recursion root
            if (node.isRecursive()) {
                XsdInstanceNode recursionRoot = node.findRecursionRoot();
                TreeItem item = WidgetUtil.findTreeItem(node, this.nodeViewer);
                if (recursionRoot == null) {
                    recursionRoot = node;
                    highlightRecursionItems(item, item);
                } else {
                    TreeItem recursionItem = WidgetUtil.findTreeItem(recursionRoot, this.nodeViewer);
                    this.highlightedRecursionItems.add(recursionItem);
                    recursionItem.setBackground(this.recursionBkgd);
                    highlightRecursionItems(recursionItem, item);
                }
                this.nodeViewer.update(this.highlightedRecursionItems.toArray(), null);
            }

            DeclareStatement declaration = (DeclareStatement)this.nodesToDeclarations.get(node);
            if (declaration != null) {
                for (Iterator iter = this.editor.getDeclarationsToAssignments().entrySet().iterator(); iter.hasNext();) {
                    Entry entry = (Entry)iter.next();
                    if (entry.getKey() == declaration) {
                        this.varSection.select(entry);
                        break;
                    }
                } // for
            } else {
                this.varSection.clearSelection();
            }
        }
    }

    void nodeUpdated( TreeItem item ) {
        if (item.getGrayed()) {
            item.setChecked(false);
        }
    }

    private void removeVariableStatements( DeclareStatement declaration ) {
        AssignmentStatement assignment = (AssignmentStatement)this.editor.getDeclarationsToAssignments().remove(declaration);
        BlockDisplayNode block = this.editor.findBlock();
        if (block != null) {
            for (Iterator childIter = block.getChildren().iterator(); childIter.hasNext();) {
                DisplayNode blockChild = (DisplayNode)childIter.next();
                if (blockChild instanceof DeclareStatementDisplayNode || blockChild instanceof AssignmentStatementDisplayNode) {
                    LanguageObject obj = blockChild.getLanguageObject();
                    if (obj == declaration || obj == assignment) {
                        childIter.remove();
                        for (DisplayNode displayNode = block; displayNode != null; displayNode = displayNode.getParent()) {
                            for (Iterator nodeIter = displayNode.getDisplayNodeList().iterator(); nodeIter.hasNext();) {
                                if (isDescendentOf((DisplayNode)nodeIter.next(), blockChild)) {
                                    nodeIter.remove();
                                }
                            } // for
                        } // for
                        if (obj == assignment) {
                            break;
                        }
                    }
                }
            } // for
        }
    }

    private void updateCheckBoxes( TreeItem[] items ) {
        for (int ndx = items.length; --ndx >= 0;) {
            TreeItem item = items[ndx];
            XsdInstanceNode node = (XsdInstanceNode)item.getData();
            if (!node.isSelectable()) {
                item.setGrayed(true);
            } else if (node.isSelected()) {
                item.setChecked(true);
            }
            if (item.getExpanded()) {
                updateCheckBoxes(item.getItems());
            }
        }
    }

    void updateDeletedNodes( List entries ) {
        for (Iterator entryIter = entries.iterator(); entryIter.hasNext();) {
            Entry varEntry = (Entry)entryIter.next();
            DeclareStatement declaration = (DeclareStatement)varEntry.getKey();
            for (Iterator nodeIter = this.nodesToDeclarations.entrySet().iterator(); nodeIter.hasNext();) {
                Entry nodeEntry = (Entry)nodeIter.next();
                if (nodeEntry.getValue() == declaration) {
                    nodeIter.remove();
                    XsdInstanceNode node = (XsdInstanceNode)nodeEntry.getKey();
                    node.setSelected(false);
                    WidgetUtil.findTreeItem(node, this.nodeViewer).setChecked(false);
                    removeVariableStatements(declaration);
                }
            } // for
        } // for
        this.editor.refreshVariables();
    }

    /**
     * @since 5.0.1
     */
    void updateLocationPreferences() {
        IPreferenceStore store = WebServiceUiPlugin.getDefault().getPreferenceStore();
        Point loc = getShell().getLocation();
        UiUtil.updateIntegerPreference(X_PREF, loc.x, 0, store);
        UiUtil.updateIntegerPreference(Y_PREF, loc.y, 0, store);
    }

    void updateNodesToDeclarations() {
        // Wire procedure section in object editor page to update selected nodes map
        String doc = DocumentGenerator.SHARED.generate(this.rootNode);
        this.nodesToDeclarations.clear();
        for (Iterator entryIter = this.editor.getDeclarationsToAssignments().entrySet().iterator(); entryIter.hasNext();) {
            Entry entry = (Entry)entryIter.next();
            try {
                Object obj = XPathHelper.getSingleMatch(new StringReader(doc),
                                                        WebServiceUtil.getXpath((AssignmentStatement)entry.getValue()));
                if (obj instanceof NodeInfo) {
                    NodeInfo saxonNode = (NodeInfo)obj;
                    if (saxonNode.getNodeKind() == Type.ELEMENT || saxonNode.getNodeKind() == Type.ATTRIBUTE) {
                        XsdInstanceNode node = getNode(saxonNode);
                        if (node != null && node.isSelectable()) {
                            node.setSelected(true);
                            this.nodesToDeclarations.put(node, entry.getKey());
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        } // for
        this.editor.refreshVariables();
    }

    /**
     * @since 5.0.1
     */
    void updateSizePreferences() {
        IPreferenceStore store = WebServiceUiPlugin.getDefault().getPreferenceStore();
        Point loc = getShell().getSize();
        UiUtil.updateIntegerPreference(WTH_PREF, loc.x, 0, store);
        UiUtil.updateIntegerPreference(HGT_PREF, loc.y, 0, store);
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

    void variableSelected() {
        // System.out.println("VariableEditorDialog.variableSelected()");
        IStructuredSelection selection = this.varSection.getSelection();
        if (selection.isEmpty() || selection.size() > 1) {
            this.xpathSection.setText(XPATH_TITLE);
            this.xpathText.setText(EMPTY_STRING);
        } else {
            Entry varEntry = (Entry)selection.getFirstElement();
            DeclareStatement declaration = (DeclareStatement)varEntry.getKey();
            this.xpathSection.setText(UTIL.getString(VAR_XPATH_TITLE, declaration.getVariable().getShortName()));
            this.xpathSection.layout(); // This is necessary to get the title label resized
            AssignmentStatement assignment = (AssignmentStatement)varEntry.getValue();
            this.xpathText.setText(WebServiceUtil.getXpath(assignment));
            // Select node in tree
            XsdInstanceNode node = getNode(declaration);
            if (node != null) {
                WidgetUtil.select(node, this.nodeViewer);
            }
        }
    }
}
