/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.table;

import java.util.Iterator;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserConfiguration;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserQuickSearchWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserUniversalListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.ui.views.browser.ShowBookmarksAction;
import org.apache.directory.studio.ldapbrowser.ui.views.browser.ShowSearchesAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiPlugin;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizard;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapPageUtils;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.impl.ConnectionNode;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WizardUtil;

/**
 * Wizard page for selecting LDAP entries to use
 * as the model tables
 */
public class LdapTablesPage extends WizardPage
    implements IChangeListener, ModelGeneratorLdapUiConstants, ModelGeneratorLdapUiConstants.Images,
    ModelGeneratorLdapUiConstants.HelpContexts, StringConstants {

    private static final int[] SPLITTER_WEIGHTS = new int[] {50, 50};

    private final LdapImportWizardManager importManager;

    private SashForm splitter;

    private Text tableNameText;

    private Text tableSourceNameText;

    private Text tableSourceSuffixText;

    private Button validateButton;

    // flag to denote when the validate button should be clicked before allowing Next page
    private boolean dirty;

	private BrowserWidget widget;

	private BrowserUniversalListener universalListener;

	private ShowSearchesAction showSearchesAction;

	private ShowBookmarksAction showBookmarksAction;

	private TreeViewer entryViewer;

	private LdapConnectionContentProvider entryContentProvider;

	private LdapConnectionLabelProvider entryLabelProvider;

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
    }

    private static String getString(String key, Object... properties) {
        return ModelGeneratorLdapUiConstants.UTIL.getString(LdapTablesPage.class.getSimpleName() + "_" + key, properties); //$NON-NLS-1$
    }

    private boolean isDirty() {
        return dirty;
    }

    private void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public void dispose() {
        super.dispose();

        universalListener.dispose();
        widget.dispose();

        entryViewer.getTree().dispose();
        entryContentProvider.dispose();
        entryLabelProvider.dispose();
    }

    private void nodeSelected( final ILdapEntryNode node ) {
        if (node.isRoot()) {
            tableNameText.setText(EMPTY_STRING);
            tableNameText.setEditable(false);
            tableSourceNameText.setText(EMPTY_STRING);
            tableSourceSuffixText.setText(EMPTY_STRING);
            tableSourceSuffixText.setEditable(false);
        } else {
            tableNameText.setText(node.getLabel());
            tableNameText.setEditable(true);
            tableSourceNameText.setText(node.getSourceName());
            tableSourceSuffixText.setText(node.getSourceNameSuffix());
            tableSourceSuffixText.setEditable(true);
        }
    }

    private void addNode(ILdapEntryNode entryNode) {
        if (entryNode == null || entryNode.isRoot())
            return;

        importManager.setSynchronising(true);

        boolean entryAdded = importManager.addEntry(entryNode);
        if (! entryAdded)
            return;

        entryViewer.refresh();
        entryViewer.setExpandedElements(new Object[] {importManager.getConnectionNode()});

        setDirty(true);

        importManager.setSynchronising(false);

        setPageStatus();
    }

    private void removeNode(ILdapEntryNode entryNode) {
        if (entryNode == null || entryNode.isRoot())
            return;

        importManager.setSynchronising(true);

        boolean entryRemoved = importManager.removeEntry(entryNode);
        if (! entryRemoved)
            return;

        entryViewer.refresh();
        ConnectionNode connectionNode = importManager.getConnectionNode();
		entryViewer.setExpandedElements(new Object[] {connectionNode});
        entryViewer.setSelection(new StructuredSelection(connectionNode));
        setDirty(true);

        importManager.setSynchronising(false);

        setPageStatus();
    }

    private void createEntriesView(Composite parent) {

        ToolBar bar = new ToolBar(parent, SWT.FLAT);
        ToolBarManager toolbarManager = new ToolBarManager(bar);

        final Action deleteAction = new Action() {
            @Override
            public String getToolTipText() {
                return getString("deleteTooltip"); //$NON-NLS-1$
            }

            @Override
            public ImageDescriptor getImageDescriptor() {
                return ModelGeneratorLdapUiPlugin.getDefault().getImageDescriptor(ModelGeneratorLdapUiConstants.Images.LDAP_DELETE_ICON);
            }

            @Override
            public void run() {
                ISelection selection = entryViewer.getSelection();
                if (selection.isEmpty() || (!(selection instanceof IStructuredSelection)))
                    return;

                IStructuredSelection sselection = (IStructuredSelection)selection;
                Iterator<Object> iter = sselection.iterator();
                while (iter.hasNext()) {
                    Object next = iter.next();
                    if (!(next instanceof ILdapEntryNode))
                        continue;

                    ILdapEntryNode entryNode = (ILdapEntryNode)next;
                    if (entryNode.isRoot())
                        continue;

                    removeNode(entryNode);
                }
            }
        };
        toolbarManager.add(deleteAction);
        toolbarManager.update(true);

        entryContentProvider = new LdapConnectionContentProvider(importManager);
        entryLabelProvider = new LdapConnectionLabelProvider(importManager);

        // Add contents to view form
        this.entryViewer = new TreeViewer(parent, SWT.SINGLE | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(entryViewer.getTree());
        this.entryViewer.setUseHashlookup(true);
        this.entryViewer.setContentProvider(entryContentProvider);
        this.entryViewer.setLabelProvider(entryLabelProvider);

        this.entryViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = event.getSelection();
                if (selection.isEmpty())
                    return;

                if (!(selection instanceof IStructuredSelection))
                    return;

                IStructuredSelection sselection = (IStructuredSelection)selection;
                importManager.setSynchronising(true);

                try {
                    Iterator iterator = sselection.iterator();
                    while (iterator.hasNext()) {
                        Object object = iterator.next();
                        if (!(object instanceof ILdapEntryNode))
                            continue;

                        nodeSelected((ILdapEntryNode)object);
                    }
                } finally {
                    // Turns off synchronising and calls state changed
                    importManager.setSynchronising(false);
                }
            }
        });
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

        Group widgetComposite = new Group(splitter, SWT.BORDER);
        widgetComposite.setText(getString("directoryTitle")); //$NON-NLS-1$
        widgetComposite.setFont(JFaceResources.getBannerFont());
        GridLayoutFactory.fillDefaults().margins(10, 5).applyTo(widgetComposite);

        BrowserConfiguration configuration = new BrowserConfiguration();
        widget = new BrowserWidget(configuration, null);
        widget.createWidget(widgetComposite);

        BrowserQuickSearchWidget quickSearchWidget = widget.getQuickSearchWidget();
        quickSearchWidget.setActive(true);

        //
        // Controls the refreshing of the widget's viewer on connection opening
        //
        universalListener = new BrowserUniversalListener(widget);

        //
        // Add refresh button to widget's tool bar
        //
        final Action refreshAction = new Action() {
            @Override
            public String getToolTipText() {
                return getString("refreshTooltip"); //$NON-NLS-1$
            }

            @Override
            public ImageDescriptor getImageDescriptor() {
                return ModelGeneratorLdapUiPlugin.getDefault().getImageDescriptor(ModelGeneratorLdapUiConstants.Images.LDAP_REFRESH_ICON);
            }

            @Override
            public void run() {
                if (widget == null || widget.getViewer() == null)
                    return;

                widget.getViewer().refresh();
            }
        };

        final Action addAction = new Action() {
            @Override
            public String getToolTipText() {
                return getString("addTooltip"); //$NON-NLS-1$
            }

            @Override
            public ImageDescriptor getImageDescriptor() {
                return ModelGeneratorLdapUiPlugin.getDefault().getImageDescriptor(ModelGeneratorLdapUiConstants.Images.LDAP_ADD_ICON);
            }

            @Override
            public void run() {
                ISelection selection = widget.getViewer().getSelection();
                if (selection.isEmpty() || (!(selection instanceof IStructuredSelection)))
                    return;

                IStructuredSelection sselection = (IStructuredSelection)selection;
                Iterator<Object> iter = sselection.iterator();
                while (iter.hasNext()) {
                    Object next = iter.next();
                    if (!(next instanceof IEntry))
                        continue;

                    IEntry entry = (IEntry)next;
                    ConnectionNode connectionNode = importManager.getConnectionNode();
                    ILdapEntryNode node = importManager.newEntry(connectionNode, entry);
                    addNode(node);
                }
            }
        };

        widget.getToolBarManager().add(refreshAction);
        widget.getToolBarManager().add(addAction);
        widget.getToolBarManager().update(true);

        widget.getViewer().addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				addAction.run();
			}
        });

        //
        // Don't need the infoText control
        //
        widget.getInfoText().setVisible(false);

        showSearchesAction = new ShowSearchesAction();
        showBookmarksAction = new ShowBookmarksAction();
		widget.getMenuManager().add(showSearchesAction);
		widget.getMenuManager().add(showBookmarksAction);
		widget.getMenuManager().update(true);

		//
		// The chosen-entries side of the splitter
		//
        Group selectedComposite = new Group(splitter, SWT.BORDER);
        selectedComposite.setText(getString("selectedEntryTitle")); //$NON-NLS-1$
        selectedComposite.setFont(JFaceResources.getBannerFont());
        GridLayoutFactory.fillDefaults().margins(10, 5).applyTo(selectedComposite);

        createEntriesView(selectedComposite);

        Group detailsGroup = WidgetFactory.createGroup(selectedComposite, getString("tableAttributesTitle"), SWT.NONE, 2); //$NON-NLS-1$
        LdapPageUtils.setBackground(detailsGroup, selectedComposite);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(detailsGroup);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(detailsGroup);

        Label tableNameLabel = new Label(detailsGroup, SWT.NONE);
        tableNameLabel.setText(getString("detailTableNameLabel")); //$NON-NLS-1$

        tableNameText = new Text(detailsGroup, SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(tableNameText);
        tableNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setDirty(true);

                IStructuredSelection selection = (IStructuredSelection) entryViewer.getSelection();
                if (selection.isEmpty())
                    return;

                ILdapEntryNode node = (ILdapEntryNode) selection.getFirstElement();
                String tblNameText = tableNameText.getText();
                if (! tblNameText.equals(node.getLabel())) {
                    node.setLabel(tblNameText);
                }
            }
        });

        Label tableSourceNameLabel = new Label(detailsGroup, SWT.NONE);
        tableSourceNameLabel.setText(getString("detailTableSourceNameLabel")); //$NON-NLS-1$

        tableSourceNameText = new Text(detailsGroup, SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(tableSourceNameText);
        LdapPageUtils.blueForeground(tableSourceNameText);
        LdapPageUtils.setBackground(tableSourceNameText, detailsGroup);
        tableSourceNameText.setEditable(false);

        Label tableSourceSuffixLabel = new Label(detailsGroup, SWT.NONE);
        tableSourceSuffixLabel.setText(getString("detailTableSourceSuffixLabel")); //$NON-NLS-1$
        tableSourceSuffixLabel.setToolTipText(getString("detailTableSourceSuffixToolTip")); //$NON-NLS-1$

        tableSourceSuffixText = new Text(detailsGroup, SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(tableSourceSuffixText);
        tableSourceSuffixText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setDirty(true);

                IStructuredSelection selection = (IStructuredSelection) entryViewer.getSelection();
                if (selection.isEmpty())
                    return;

                ILdapEntryNode node = (ILdapEntryNode) selection.getFirstElement();
                String tableSuffixText = tableSourceSuffixText.getText();
                if (! tableSuffixText.equals(node.getLabel())) {
                    node.setSourceNameSuffix(tableSuffixText);
                }
            }
        });

        validateButton = new Button(detailsGroup, SWT.PUSH);
        validateButton.setText(getString("validateButtonLabel")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().span(2, 1).align(GridData.END, GridData.CENTER).applyTo(validateButton);
        validateButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                setDirty(false);
                notifyChanged();
            }
        });
        
        this.splitter.setWeights(SPLITTER_WEIGHTS);
        this.splitter.setSashWidth(20);
    }

    /**
     * Performs validation and sets the page status.
     */
    private void setPageStatus() {
        if (getControl() != null && !getControl().isVisible())
            return;

        if (isDirty()) {
            WizardUtil.setPageComplete(this, getString("needsValidating"), IMessageProvider.ERROR); //$NON-NLS-1$
            return;
        }

        if (this.importManager.getError() != null) {
            ModelGeneratorLdapUiConstants.UTIL.log(this.importManager.getError());
            WizardUtil.setPageComplete(this, this.importManager.getError().getLocalizedMessage(), IMessageProvider.ERROR);
            return;
        }

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

        this.entryViewer.setInput(importManager);

        initBrowserWidget();

        setPageStatus();
    }

	private void initBrowserWidget() {
		if (widget == null)
			return;

		if (widget.getViewer() == null)
			return;

		IBrowserConnection browserConnection = importManager.getBrowserConnection();
		if (browserConnection == null)
			return;

		if (browserConnection.equals(widget.getViewer().getInput()))
			return;

		widget.setInput(browserConnection);

		BrowserQuickSearchWidget quickSearchWidget = widget.getQuickSearchWidget();
		quickSearchWidget.setInput(browserConnection);

		//
		// Turn off bookmarks and searches by default
		//
		showBookmarksAction.setChecked(false);
		showBookmarksAction.run();
		showSearchesAction.setChecked(false);
		showSearchesAction.run();
	}

    @Override
    public void stateChanged(IChangeNotifier theSource) {
        // Required if the user flicks back a page, makes a change
        // then comes forward to this page again
        initBrowserWidget();

        setPageStatus();
    }

    private void notifyChanged() {
        this.importManager.notifyChanged();
    }
}
