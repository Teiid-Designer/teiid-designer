/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.table;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiPlugin;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizard;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.UiConstants;
import org.teiid.designer.ui.common.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;

/**
 * Wizard page for selecting LDAP entries to use
 * as the model tables
 */
public class LdapTablesPage extends WizardPage
    implements IChangeListener, ModelGeneratorLdapUiConstants, ModelGeneratorLdapUiConstants.Images,
    ModelGeneratorLdapUiConstants.HelpContexts{

    private static final int[] SPLITTER_WEIGHTS = new int[] {30, 70};

    private final LdapImportWizardManager importManager;

    private IContentProvider contentProvider;
    private ILabelProvider labelProvider;

    private SashForm splitter;
    private ViewForm objsView;
    private TreeViewer treeViewer;

    private Text tableNameText;

    private Text tableSourceNameText;

    private Text tableSourceSuffixText;

    private boolean synchronising;

    /**
     * Constructs the page with the provided import manager
     *
     * @param theImportManager
     *            the import manager object
     */
    public LdapTablesPage(LdapImportWizardManager theImportManager) {
        super(LdapTablesPage.class.getSimpleName(), getString("title"), null); //$NON-NLS-1$
        this.importManager = theImportManager;
        setDescription(getString("description")); //$NON-NLS-1$
        setImageDescriptor(LdapImportWizard.BANNER);
        this.importManager.addChangeListener(this);

        contentProvider = new LdapConnectionContentProvider(importManager);
        labelProvider = new LdapConnectionLabelProvider(importManager);
    }

    private static String getString(String key, Object... properties) {
        return ModelGeneratorLdapUiConstants.UTIL.getString(LdapTablesPage.class.getSimpleName() + "_" + key, properties); //$NON-NLS-1$
    }

    /**
     * @return the synchronising
     */
    private boolean isSynchronising() {
        return this.synchronising;
    }

    private void refresh() {
        importManager.clearEntries();
        treeViewer.refresh();
    }

    private void nodeSelected( final ILdapEntryNode node ) {
        if (node.isRoot()) {
            tableNameText.setText(""); //$NON-NLS-1$
            tableNameText.setEditable(false);
            tableSourceNameText.setText(""); //$NON-NLS-1$
            tableSourceSuffixText.setText(""); //$NON-NLS-1$
            tableSourceSuffixText.setEditable(false);
        } else {
            tableNameText.setText(node.getLabel());
            tableNameText.setEditable(true);
            tableSourceNameText.setText(node.getSourceName());
            tableSourceSuffixText.setText(node.getSourceNameSuffix());
            tableSourceSuffixText.setEditable(true);
        }
    }

    private void treeItemChecked(TreeItem item, boolean selected ) {
        // Update check boxes of item
        if (item == null) {
            return;
        }

        final ILdapEntryNode entryNode = (ILdapEntryNode) item.getData();
        if (entryNode.isRoot())
            return;

        if (selected) {
            importManager.addEntry(entryNode);
        } else {
            importManager.removeEntry(entryNode);
        }

        // Ensure the treeviewer has completed all events before updating the node
        treeViewer.getControl().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                treeViewer.update(entryNode, null);
            }
        });
    }

    private void deselectAllButtonSelected() {
        Collection<ILdapEntryNode> oldSelection = new ArrayList<ILdapEntryNode>(); 
        oldSelection.addAll(importManager.getSelectedEntries());

        for (ILdapEntryNode entryNode : oldSelection) {
            TreeItem treeItem = WidgetUtil.findTreeItem(entryNode, treeViewer);
            if (treeItem == null)
                continue;

            treeItemChecked(treeItem, false);
        }
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

        // Add refresh button to view form's title bar
        final ToolBar bar = new ToolBar(this.objsView, SWT.FLAT);
        final ToolBarManager mgr = new ToolBarManager(bar);
        final Action action = new Action() {
            @Override
            public String getToolTipText() {
                return getString("refreshTooltip"); //$NON-NLS-1$
            }

            @Override
            public ImageDescriptor getImageDescriptor() {
                return UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.REFRESH);
            }

            @Override
            public void run() {
                refresh();
            }
        };

        mgr.add(action);
        mgr.update(true);

        this.objsView.setTopRight(bar);
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

                    ILdapEntryNode entryNode = (ILdapEntryNode) treeItem.getData();
                    Rectangle imageRec = treeItem.getImageBounds(0);

                    if (imageRec.contains(e.x, e.y)) {
                        treeItemChecked(treeItem, ! importManager.entrySelected(entryNode));
                    }

                    nodeSelected(entryNode);
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

                ILdapEntryNode entryNode = (ILdapEntryNode) treeItem.getData();
                treeItemChecked(treeItem, importManager.entrySelected(entryNode));
            }
        });

        ViewForm detailsView = new ViewForm(this.splitter, SWT.BORDER);
        Group detailsGroup = WidgetFactory.createGroup(detailsView, getString("tableAttributesTitle"), SWT.NONE, 2); //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(detailsGroup);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(detailsGroup);

        detailsView.setContent(detailsGroup);

        Label tableNameLabel = new Label(detailsGroup, SWT.NONE);
        tableNameLabel.setText(getString("detailTableNameLabel")); //$NON-NLS-1$

        tableNameText = new Text(detailsGroup, SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(tableNameText);
        tableNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (isSynchronising())
                    return;

                IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                if (selection.isEmpty())
                    return;

                ILdapEntryNode node = (ILdapEntryNode) selection.getFirstElement();
                String tblNameText = tableNameText.getText();
                if (! tblNameText.equals(node.getLabel())) {
                    node.setLabel(tblNameText);
                    notifyChanged();
                }
            }
        });

        Label tableSourceNameLabel = new Label(detailsGroup, SWT.NONE);
        tableSourceNameLabel.setText(getString("detailTableSourceNameLabel")); //$NON-NLS-1$

        tableSourceNameText = new Text(detailsGroup, SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(tableSourceNameText);
        tableSourceNameText.setForeground(tableSourceNameText.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
        tableSourceNameText.setBackground(tableSourceNameText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        tableSourceNameText.setEditable(false);

        Label tableSourceSuffixLabel = new Label(detailsGroup, SWT.NONE);
        tableSourceSuffixLabel.setText(getString("detailTableSourceSuffixLabel")); //$NON-NLS-1$
        tableSourceSuffixLabel.setToolTipText(getString("detailTableSourceSuffixToolTip")); //$NON-NLS-1$

        tableSourceSuffixText = new Text(detailsGroup, SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(tableSourceSuffixText);
        tableSourceSuffixText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (isSynchronising())
                    return;

                IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                if (selection.isEmpty())
                    return;

                ILdapEntryNode node = (ILdapEntryNode) selection.getFirstElement();
                String tableSuffixText = tableSourceSuffixText.getText();
                if (! tableSuffixText.equals(node.getLabel())) {
                    node.setSourceNameSuffix(tableSuffixText);
                    notifyChanged();
                }
            }
        });

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
        if (this.importManager.hasSelectedEntries()) {
            WizardUtil.setPageComplete(this, getString("noSourceModelTables"), IMessageProvider.ERROR); //$NON-NLS-1$
            return;
        }

        for (ILdapEntryNode entry : importManager.getSelectedEntries()) {
            String suffix = entry.getSourceNameSuffix();
            if (suffix.length() == 0)
                continue;

            // Test to ensure that the suffix has at least 2 '?'s in it
            int qmarks = 0;
            for (int i = 0; i < suffix.length(); ++i) {
                char c = suffix.charAt(i);
                if (c == '?')
                    qmarks++;
            }

            if (qmarks != 2) {
                WizardUtil.setPageComplete(this, getString("invalidSourceNameSuffix"), IMessageProvider.ERROR); //$NON-NLS-1$
                return;
            }
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

        if (treeViewer.getInput() == null)
            this.treeViewer.setInput(importManager);

        setPageStatus();
    }

    @Override
    public void stateChanged(IChangeNotifier theSource) {
        if (treeViewer != null && treeViewer.getInput() != null) {
            // Required if the user flicks back a page, makes a change
            // then comes forward to this page again
            treeViewer.refresh();
        }

        setPageStatus();
    }

    private void notifyChanged() {
        this.importManager.notifyChanged();
    }
}
