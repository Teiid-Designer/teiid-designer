/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.columns;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiPlugin;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapAttributeNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizard;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;

/**
 *
 */
public class LdapColumnsPage extends WizardPage
    implements IChangeListener, ModelGeneratorLdapUiConstants, ModelGeneratorLdapUiConstants.Images,
    ModelGeneratorLdapUiConstants.HelpContexts {

    private static final String NULL_STRING = ""; //$NON-NLS-1$

    private static final int[] SPLITTER_WEIGHTS = new int[] {30, 70};

    private final LdapImportWizardManager importManager;

    private IContentProvider contentProvider;
    private ILabelProvider labelProvider;

    private SashForm splitter;
    private ViewForm objsView;
    private TreeViewer treeViewer;

    private Text columnNameText;

    private Text columnSourceNameText;

    private Text columnDVCountText;

    private Text columnNVCountText;

    private Text columnMaxValueText;

    private boolean synchronising;

    /**
     * Constructs the page with the provided import manager
     *
     * @param theImportManager
     *            the import manager object
     */
    public LdapColumnsPage(LdapImportWizardManager theImportManager) {
        super(LdapColumnsPage.class.getSimpleName(), getString("title"), null); //$NON-NLS-1$
        this.importManager = theImportManager;
        setDescription(getString("description")); //$NON-NLS-1$
        setImageDescriptor(LdapImportWizard.BANNER);
        this.importManager.addChangeListener(this);

        contentProvider = new LdapEntryContentProvider(importManager);
        labelProvider = new LdapEntryLabelProvider(importManager);
    }

    private static String getString(String key, Object... properties) {
        return ModelGeneratorLdapUiConstants.UTIL.getString(LdapColumnsPage.class.getSimpleName() + "_" + key, properties); //$NON-NLS-1$
    }

    /**
     * @return the synchronising
     */
    private boolean isSynchronising() {
        return this.synchronising;
    }

    private void nodeSelected( final Object node ) {
        if (node instanceof ILdapAttributeNode) {
            ILdapAttributeNode attributeNode = (ILdapAttributeNode) node;
            columnNameText.setText(attributeNode.getLabel());
            columnNameText.setEditable(true);
            columnSourceNameText.setText(attributeNode.getId());
            columnDVCountText.setText(Integer.toString(attributeNode.getDistinctValueCount()));
            columnNVCountText.setText(Integer.toString(attributeNode.getNullValueCount()));
            columnMaxValueText.setText(Integer.toString(attributeNode.getMaximumValueLength()));
        } else {
            columnNameText.setText(NULL_STRING);
            columnNameText.setEditable(false);
            columnSourceNameText.setText(NULL_STRING);
            columnDVCountText.setText(NULL_STRING);
            columnNVCountText.setText(NULL_STRING);
            columnMaxValueText.setText(NULL_STRING);
        }
    }

    private void treeItemChecked(TreeItem item, boolean selected ) {
        // Update check boxes of item
        if (item == null) {
            return;
        }

        if (! (item.getData() instanceof ILdapAttributeNode))
            return;

        final ILdapAttributeNode attributeNode = (ILdapAttributeNode) item.getData();

        try {
            if (selected) {
                importManager.addAttribute(attributeNode);
            } else {
                importManager.removeAttribute(attributeNode);
            }

            // Ensure the treeviewer has completed all events before updating the node
            treeViewer.getControl().getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    treeViewer.update(attributeNode, null);
                }
            });
        } catch(Exception ex) {
            ModelGeneratorLdapUiConstants.UTIL.log(ex);
            WizardUtil.setPageComplete(this, ex.getLocalizedMessage(), IMessageProvider.ERROR);
        }
    }

    private void deselectAllButtonSelected() {
        Collection<ILdapAttributeNode> oldSelection = new ArrayList<ILdapAttributeNode>(); 
        oldSelection.addAll(importManager.getSelectedAttributes());

        for (ILdapAttributeNode attributeNode : oldSelection) {
            TreeItem treeItem = WidgetUtil.findTreeItem(attributeNode, treeViewer);
            if (treeItem == null)
                continue;

            treeItemChecked(treeItem, false);
        }
    }

    private void setNonEditable(Text control) {
        if (control == null)
            return;

        Display display = control.getDisplay();
        control.setForeground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
        control.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        control.setEditable(false);
    }

    @Override
    public void createControl(Composite parent) {
     // Create page
        final Composite pg = new Composite(parent, SWT.NONE) {

            @Override
            public Point computeSize( final int widthHint,
                                      final int heightHint,
                                      final boolean changed ) {
                final Point size = super.computeSize(widthHint, heightHint, changed);
                size.x = 800;
                return size;
            }
        };
        GridLayoutFactory.fillDefaults().applyTo(pg);
        setControl(pg);

        // Add widgets to page
        this.splitter = WidgetFactory.createSplitter(pg);

        this.objsView = new ViewForm(this.splitter, SWT.BORDER);

        CLabel ldapLabel = new CLabel(this.objsView, SWT.NONE);
        ldapLabel.setImage(ModelGeneratorLdapUiPlugin.getDefault().getImage(LDAP_OBJECTS_ICON));
        GridDataFactory.swtDefaults().applyTo(ldapLabel);

        // Add title label to view form's title bar
        this.objsView.setTopLeft(ldapLabel);

        // Add contents to view form
        this.treeViewer = new TreeViewer(this.objsView, SWT.SINGLE | SWT.BORDER);
        this.treeViewer.setUseHashlookup(true);
        final Tree tree = this.treeViewer.getTree();
        this.objsView.setContent(tree);

        this.treeViewer.setContentProvider(contentProvider);
        this.treeViewer.setLabelProvider(labelProvider);

        /*
         * Mouse down listener for simulating the checkbox selection.
         *
         * This is used instead of the SWT checkbox tree viewer which has really
         * awful performance on item selection.
         */
        this.treeViewer.getTree().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                if (selection.isEmpty())
                    return;

                Object node = selection.getFirstElement();
                treeViewer.setExpandedState(node, ! treeViewer.getExpandedState(node));
            }

            @Override
            public void mouseDown(MouseEvent e) {
                TreeItem[] selectedItems = treeViewer.getTree().getSelection();
                if (selectedItems.length == 0)
                    return;

                for (TreeItem treeItem : selectedItems) {
                    if(treeItem.getImage() == null)
                        continue;

                    if (treeItem.getData() instanceof ILdapAttributeNode) {
                        ILdapAttributeNode attributeNode = (ILdapAttributeNode) treeItem.getData();
                        Rectangle imageRec = treeItem.getImageBounds(0);

                        if (imageRec.contains(e.x, e.y)) {
                            treeItemChecked(treeItem, ! importManager.attributeSelected(attributeNode));
                        }
                    }

                    nodeSelected(treeItem.getData());
                }
            }
        });

        // Add listener to select node when expanded/collapsed
        this.treeViewer.addTreeListener(new ITreeViewerListener() {

            @Override
            public void treeCollapsed(TreeExpansionEvent e) {
                // Nothing to do
            }

            @Override
            public void treeExpanded(TreeExpansionEvent e) {
                Object element = e.getElement();
                TreeItem treeItem = WidgetUtil.findTreeItem(element, treeViewer);
                if (treeItem == null)
                    return;

                if (treeItem.getData() instanceof ILdapEntryNode)
                    return;

                ILdapAttributeNode attributeNode = (ILdapAttributeNode) treeItem.getData();
                treeItemChecked(treeItem, importManager.attributeSelected(attributeNode));
            }
        });

        ViewForm detailsView = new ViewForm(this.splitter, SWT.BORDER);
        Group detailsGroup = WidgetFactory.createGroup(detailsView, getString("columnAttributesTitle"), SWT.NONE, 2); //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(detailsGroup);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(detailsGroup);

        detailsView.setContent(detailsGroup);

        Label columnNameLabel = new Label(detailsGroup, SWT.NONE);
        columnNameLabel.setText(getString("detailColumnNameLabel")); //$NON-NLS-1$

        columnNameText = new Text(detailsGroup, SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(columnNameText);
        columnNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (isSynchronising())
                    return;

                IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                if (selection.isEmpty())
                    return;

                if (selection.getFirstElement() instanceof ILdapAttributeNode) {
                    ILdapAttributeNode node = (ILdapAttributeNode) selection.getFirstElement();

                    String colNameText = columnNameText.getText();
                    if (! colNameText.equals(node.getLabel())) {
                        node.setLabel(colNameText);
                        notifyChanged();
                    }
                }
            }
        });

        Label columnSourceNameLabel = new Label(detailsGroup, SWT.NONE);
        columnSourceNameLabel.setText(getString("detailColumnSourceNameLabel")); //$NON-NLS-1$

        columnSourceNameText = new Text(detailsGroup, SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(columnSourceNameText);
        setNonEditable(columnSourceNameText);

        Label columnDVCountLabel = new Label(detailsGroup, SWT.NONE);
        columnDVCountLabel.setText(getString("detailColumnDVCountLabel")); //$NON-NLS-1$

        columnDVCountText = new Text(detailsGroup, SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(columnDVCountText);
        setNonEditable(columnDVCountText);

        Label columnNVCountLabel = new Label(detailsGroup, SWT.NONE);
        columnNVCountLabel.setText(getString("detailColumnNVCountLabel")); //$NON-NLS-1$

        columnNVCountText = new Text(detailsGroup, SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(columnNVCountText);
        setNonEditable(columnNVCountText);

        Label maxValueLabel = new Label(detailsGroup, SWT.NONE);
        maxValueLabel.setText(getString("detailMaxValueLabel")); //$NON-NLS-1$

        columnMaxValueText = new Text(detailsGroup, SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(columnMaxValueText);
        setNonEditable(columnMaxValueText);

        this.splitter.setWeights(SPLITTER_WEIGHTS);

        final Button deselectAllButton = WidgetFactory.createButton(pg, InternalUiConstants.Widgets.DESELECT_ALL_BUTTON);
        deselectAllButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                deselectAllButtonSelected();
            }
        });
    }

    /**
     * Performs validation and sets the page status.
     */
    private void setPageStatus() {
        if (! this.importManager.hasAttributesForEachSelectedEntry()) {
            WizardUtil.setPageComplete(this, getString("sourceColumnsIncomplete"), IMessageProvider.ERROR); //$NON-NLS-1$
            return;
        }

        WizardUtil.setPageComplete(this);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (! visible)
            return;

        if (this.importManager.getConnectionProfile() == null)
            return;

        if (this.treeViewer.getInput() == null) {
            this.treeViewer.setInput(importManager);
            this.treeViewer.expandToLevel(2);
        }

        setPageStatus();
    }

    @Override
    public void stateChanged(IChangeNotifier theSource) {
        if (treeViewer != null && treeViewer.getInput() != null) {
            // Required if the user flicks back a page, makes a change
            // then comes forward to this page again
            treeViewer.refresh(true);
        }

        setPageStatus();
    }

    private void notifyChanged() {
        this.importManager.notifyChanged();
    }
}
