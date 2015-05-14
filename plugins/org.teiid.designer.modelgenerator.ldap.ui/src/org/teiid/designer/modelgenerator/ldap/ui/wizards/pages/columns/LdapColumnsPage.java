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
import java.util.Iterator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapAttributeNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizard;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapPageUtils;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WizardUtil;

/**
 *
 */
public class LdapColumnsPage extends WizardPage
    implements IChangeListener, ModelGeneratorLdapUiConstants, ModelGeneratorLdapUiConstants.Images,
    ModelGeneratorLdapUiConstants.HelpContexts {

    private static final String NULL_STRING = ""; //$NON-NLS-1$

    private static final int[] SPLITTER_WEIGHTS = new int[] {40, 60};

    private final LdapImportWizardManager importManager;

    private IContentProvider contentProvider;
    private ILabelProvider labelProvider;

    private SashForm splitter;

    private CheckboxTreeViewer treeViewer;

    private Text columnNameText;

    private Text columnSourceNameText;

    private Text columnDVCountText;

    private Text columnNVCountText;

    private Text columnMaxValueText;

    private Button validateButton;

    private boolean dirty;

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

    private boolean isDirty() {
        return dirty;
    }

    private void setDirty(boolean dirty) {
        this.dirty = dirty;
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

    private void nodeChecked(ILdapAttributeNode attrNode, boolean selected) {
        if (attrNode == null)
            return;

        importManager.setSynchronising(true);

        if (selected) {
            importManager.addAttribute(attrNode);
        } else {
            importManager.removeAttribute(attrNode);
        }

        importManager.setSynchronising(false);
    }

    private void deselectAllButtonSelected() {
        Collection<ILdapAttributeNode> oldSelection = new ArrayList<ILdapAttributeNode>(); 
        oldSelection.addAll(importManager.getSelectedAttributes());

        for (ILdapAttributeNode node : oldSelection) {
            treeViewer.setChecked(node, false);
            nodeChecked(node, false);
        }

        setPageStatus();
    }

    private void setNonEditable(Text control) {
        if (control == null)
            return;

        LdapPageUtils.blueForeground(control);
        LdapPageUtils.greyBackground(control);
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

        Group treeComposite = new Group(splitter, SWT.BORDER);
        treeComposite.setText(getString("attrTreeTitle")); //$NON-NLS-1$
        treeComposite.setFont(JFaceResources.getBannerFont());
        GridLayoutFactory.fillDefaults().margins(10, 5).applyTo(treeComposite);

        // Add contents to view form
        this.treeViewer = new CheckboxTreeViewer(treeComposite, SWT.SINGLE | SWT.BORDER);
        this.treeViewer.setUseHashlookup(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.treeViewer.getTree());

        this.treeViewer.setContentProvider(contentProvider);
        this.treeViewer.setLabelProvider(labelProvider);
        this.treeViewer.setCheckStateProvider(new ICheckStateProvider() {

            @Override
            public boolean isGrayed(Object element) {
                return element instanceof ILdapEntryNode;
            }

            @Override
            public boolean isChecked(Object element) {
                return element instanceof ILdapEntryNode;
            }
        });

        this.treeViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                Object element = event.getElement();

                if (!(element instanceof ILdapAttributeNode)) {
                    event.getCheckable().setChecked(element, true);
                    return;
                }

                ILdapAttributeNode node = (ILdapAttributeNode) element;
                nodeChecked(node, event.getChecked());
            }
          });

        this.treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = event.getSelection();
                if (selection.isEmpty())
                    return;

                if (! (selection instanceof IStructuredSelection))
                    return;

                IStructuredSelection sselection = (IStructuredSelection) selection;
                importManager.setSynchronising(true);

                try {
                    Iterator iterator = sselection.iterator();
                    while(iterator.hasNext()) {
                        Object object = iterator.next();
                        if (! (object instanceof ILdapAttributeNode))
                            continue;

                        nodeSelected(object);
                    }
                } finally {
                    // Turns off synchronising and calls state changed
                    importManager.setSynchronising(false);
                }
            }
        });

        this.treeViewer.getTree().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                if (selection.isEmpty())
                    return;

                Object node = selection.getFirstElement();
                treeViewer.setExpandedState(node, ! treeViewer.getExpandedState(node));
            }
        });

        ViewForm detailsView = new ViewForm(this.splitter, SWT.BORDER);
        Group detailsGroup = WidgetFactory.createGroup(detailsView, getString("columnAttributesTitle"), SWT.NONE, 2); //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(detailsGroup);
        LdapPageUtils.setBackground(detailsGroup, this.splitter);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(detailsGroup);

        detailsView.setContent(detailsGroup);

        Label columnNameLabel = new Label(detailsGroup, SWT.NONE);
        columnNameLabel.setText(getString("detailColumnNameLabel")); //$NON-NLS-1$

        columnNameText = new Text(detailsGroup, SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(columnNameText);
        columnNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setDirty(true);

                IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                if (selection.isEmpty())
                    return;

                if (selection.getFirstElement() instanceof ILdapAttributeNode) {
                    ILdapAttributeNode node = (ILdapAttributeNode) selection.getFirstElement();

                    String colNameText = columnNameText.getText();
                    if (! colNameText.equals(node.getLabel())) {
                        node.setLabel(colNameText);
                    }
                }

                setPageStatus();
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

        this.treeViewer.setInput(importManager);
        this.treeViewer.expandToLevel(2);

        setPageStatus();
    }

    @Override
    public void stateChanged(IChangeNotifier theSource) {
        if (treeViewer != null && treeViewer.getInput() != null) {
            // Required if the user flicks back a page, makes a change
            // then comes forward to this page again

            Object[] checkedElements = treeViewer.getCheckedElements();
            treeViewer.refresh(true);
            treeViewer.setCheckedElements(checkedElements);
        }

        setPageStatus();
    }

    private void notifyChanged() {
        this.importManager.notifyChanged();
    }
}
