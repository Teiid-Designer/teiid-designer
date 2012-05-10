/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.vdb.ui.translators;

import static com.metamatrix.modeler.vdb.ui.VdbUiConstants.Util;
import static com.metamatrix.modeler.vdb.ui.VdbUiConstants.Images.EDIT_TRANSLATOR;
import static com.metamatrix.modeler.vdb.ui.VdbUiConstants.Images.RESTORE_DEFAULT_VALUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISharedImages;
import org.teiid.core.properties.PropertyDefinition;
import org.teiid.designer.vdb.TranslatorOverride;
import org.teiid.designer.vdb.TranslatorOverrideProperty;
import org.teiid.designer.vdb.TranslatorPropertyDefinition;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.connections.SourceHandler;
import org.teiid.designer.vdb.connections.SourceHandlerExtensionManager;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.vdb.ui.VdbUiPlugin;
import com.metamatrix.ui.UiPlugin;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * 
 */
public final class TranslatorOverridesPanel extends Composite {

    static final String PREFIX = I18nUtil.getPropertyPrefix(TranslatorOverridesPanel.class);

    /**
     * Action to add a custom property.
     */
    private final IAction addPropertyAction;

    /**
     * Action to remove a custom property.
     */
    private final IAction deletePropertyAction;

    /**
     * Action to edit a translator override name.
     */
    private final IAction editTranslatorAction;

    /**
     * Action to remove a translator from those being overridden.
     */
    private final IAction deleteTranslatorAction;

    private final TableViewer propertiesViewer;

    /**
     * Action to restore the property to its default value.
     */
    private final IAction restorePropertyDefaultAction;

    private final TableViewer translatorsViewer;

    private final Text txtDescription;

    private final Vdb vdb;

    /**
     * @param parent
     * @param vdb
     */
    public TranslatorOverridesPanel( Composite parent,
                                     Vdb vdb ) {
        super(parent, SWT.NONE);
        setLayout(new GridLayout());
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.vdb = vdb;

        SashForm splitter = new SashForm(this, SWT.HORIZONTAL);
        splitter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        { // left-side is list of overridden translators
            Composite pnlTranslators = new Composite(splitter, SWT.BORDER);
            pnlTranslators.setLayout(new GridLayout());
            pnlTranslators.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            //
            // add the list for holding overridden translators
            //

            this.translatorsViewer = new TableViewer(pnlTranslators, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION));
            this.translatorsViewer.setContentProvider(new IStructuredContentProvider() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
                 */
                @Override
                public void dispose() {
                    // nothing to do
                }

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
                 */
                @Override
                public Object[] getElements( Object inputElement ) {
                    return getTranslatorOverrides().toArray();
                }

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
                 *      java.lang.Object)
                 */
                @Override
                public void inputChanged( Viewer viewer,
                                          Object oldInput,
                                          Object newInput ) {
                    // nothing to do
                }
            });

            // sort the table rows by translator name
            this.translatorsViewer.setComparator(new ViewerComparator() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
                 *      java.lang.Object)
                 */
                @Override
                public int compare( Viewer viewer,
                                    Object t1,
                                    Object t2 ) {
                    TranslatorOverride translator1 = (TranslatorOverride)t1;
                    TranslatorOverride translator2 = (TranslatorOverride)t2;

                    return super.compare(viewer, translator1.getName(), translator2.getName());
                }
            });

            // add selection listener
            this.translatorsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
                 */
                @Override
                public void selectionChanged( SelectionChangedEvent event ) {
                    handleTranslatorSelected(event);
                }
            });

            final Table table = this.translatorsViewer.getTable();
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setLayout(new TableLayout());
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            // create column
            final TableViewerColumn column = new TableViewerColumn(this.translatorsViewer, SWT.LEFT);
            column.getColumn().setText(Util.getString(PREFIX + "translatorColumn.text")); //$NON-NLS-1$
            column.setLabelProvider(new ColumnLabelProvider() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
                 */
                @Override
                public String getText( Object element ) {
                    TranslatorOverride translator = (TranslatorOverride)element;
                    return translator.getName();
                }
            });
            column.getColumn().pack();

            table.addControlListener(new ControlAdapter() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
                 */
                @Override
                public void controlResized( ControlEvent e ) {
                    column.getColumn().setWidth(table.getSize().x);
                }
            });

            //
            // add toolbar below the list of translators
            //

            ToolBar toolBar = new ToolBar(pnlTranslators, SWT.PUSH | SWT.BORDER);
            ToolBarManager toolBarMgr = new ToolBarManager(toolBar);

            //
            // add the add action to the toolbar
            //

            IAction addAction = new Action(Util.getString(PREFIX + "addTranslatorAction.text"), SWT.BORDER) { //$NON-NLS-1$
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.action.Action#run()
                 */
                @Override
                public void run() {
                    handleAddTranslatorOverride();
                }
            };

            addAction.setToolTipText(Util.getString(PREFIX + "addTranslatorAction.toolTip")); //$NON-NLS-1$
            toolBarMgr.add(addAction);
            toolBarMgr.add(new Separator());

            { // edit translator action
                this.editTranslatorAction = new Action(CoreStringUtil.Constants.EMPTY_STRING, SWT.BORDER) {
    
                    /**
                     * {@inheritDoc}
                     * 
                     * @see org.eclipse.jface.action.Action#run()
                     */
                    @Override
                    public void run() {
                        handleEditTranslatorOverride();
                    }
                };
    
                this.editTranslatorAction.setToolTipText(Util.getString(PREFIX + "editTranslatorAction.toolTip")); //$NON-NLS-1$
                this.editTranslatorAction.setEnabled(false);
                this.editTranslatorAction.setImageDescriptor(VdbUiPlugin.singleton.getImageDescriptor(EDIT_TRANSLATOR));
                toolBarMgr.add(this.editTranslatorAction);
                toolBarMgr.add(new Separator());
                
                this.translatorsViewer.addDoubleClickListener(new IDoubleClickListener() {
                    
                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
                     */
                    @Override
                    public void doubleClick( DoubleClickEvent event ) {
                        handleEditTranslatorOverride();
                    }
                });
            }

            //
            // add the delete action to the toolbar
            //

            this.deleteTranslatorAction = new Action(Util.getString(PREFIX + "removeTranslatorAction.text"), SWT.BORDER) { //$NON-NLS-1$
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.action.Action#run()
                 */
                @Override
                public void run() {
                    handleTranslatorRemoved();
                }
            };

            this.deleteTranslatorAction.setToolTipText(Util.getString(PREFIX + "removeTranslatorAction.toolTip")); //$NON-NLS-1$
            this.deleteTranslatorAction.setEnabled(false);
            toolBarMgr.add(this.deleteTranslatorAction);
            toolBarMgr.update(true);
        }

        { // right-side is an override description and table with the selected translator's properties
            Composite pnlOverrides = new Composite(splitter, SWT.BORDER);
            pnlOverrides.setLayout(new GridLayout(2, false));
            pnlOverrides.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Label lblDescription = new Label(pnlOverrides, SWT.NONE);
            lblDescription.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            lblDescription.setText(Util.getString(PREFIX + "lblTranslatorDescription")); //$NON-NLS-1$

            this.txtDescription = new Text(pnlOverrides, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
            this.txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            ((GridData)this.txtDescription.getLayoutData()).heightHint = 35;
            this.txtDescription.setToolTipText(Util.getString(PREFIX + "txtTranslatorDescription.toolTip")); //$NON-NLS-1$
            this.txtDescription.setEnabled(false);
            this.txtDescription.addModifyListener(new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleDescriptionChanged();
                }
            });

            this.propertiesViewer = new TableViewer(pnlOverrides, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION));
            ColumnViewerToolTipSupport.enableFor(this.propertiesViewer);
            this.propertiesViewer.setContentProvider(new IStructuredContentProvider() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
                 */
                @Override
                public void dispose() {
                    // nothing to do
                }

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
                 */
                @Override
                public Object[] getElements( Object inputElement ) {
                    TranslatorOverride translator = getSelectedTranslator();

                    if (translator == null) {
                        return new Object[0];
                    }

                    return translator.getProperties();
                }

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
                 *      java.lang.Object)
                 */
                @Override
                public void inputChanged( Viewer viewer,
                                          Object oldInput,
                                          Object newInput ) {
                    // nothing to do
                }
            });

            // sort the table rows by display name
            this.propertiesViewer.setComparator(new ViewerComparator() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
                 *      java.lang.Object)
                 */
                @Override
                public int compare( Viewer viewer,
                                    Object e1,
                                    Object e2 ) {
                    TranslatorOverrideProperty prop1 = (TranslatorOverrideProperty)e1;
                    TranslatorOverrideProperty prop2 = (TranslatorOverrideProperty)e2;

                    return super.compare(viewer, prop1.getDefinition().getDisplayName(), prop2.getDefinition().getDisplayName());
                }
            });

            Table table = this.propertiesViewer.getTable();
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setLayout(new TableLayout());
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            ((GridData)table.getLayoutData()).horizontalSpan = 2;

            // create columns
            TableViewerColumn column = new TableViewerColumn(this.propertiesViewer, SWT.LEFT);
            column.getColumn().setText(Util.getString(PREFIX + "propertyColumn.text")); //$NON-NLS-1$
            column.setLabelProvider(new PropertyLabelProvider(true));
            column.getColumn().pack();

            column = new TableViewerColumn(this.propertiesViewer, SWT.LEFT);
            column.getColumn().setText(Util.getString(PREFIX + "valueColumn.text")); //$NON-NLS-1$
            column.setLabelProvider(new PropertyLabelProvider(false));
            column.setEditingSupport(new TranslatorOverridePropertyEditingSupport(this.propertiesViewer, this.vdb.getFile()));
            column.getColumn().pack();

            this.propertiesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
                 */
                @Override
                public void selectionChanged( SelectionChangedEvent event ) {
                    handlePropertySelected(event);
                }
            });

            //
            // add toolbar below the table
            //

            ToolBar toolBar = new ToolBar(pnlOverrides, SWT.PUSH | SWT.BORDER);
            ToolBarManager toolBarMgr = new ToolBarManager(toolBar);

            //
            // add the add custom property action to the toolbar
            //

            this.addPropertyAction = new Action(Util.getString(PREFIX + "addPropertyAction.text"), SWT.BORDER) { //$NON-NLS-1$
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.action.Action#run()
                 */
                @Override
                public void run() {
                    handleAddProperty();
                }
            };

            this.addPropertyAction.setToolTipText(Util.getString(PREFIX + "addPropertyAction.toolTip")); //$NON-NLS-1$
            this.addPropertyAction.setEnabled(false);
            toolBarMgr.add(this.addPropertyAction);

            //
            // add the delete custom property action to the toolbar
            //

            this.deletePropertyAction = new Action(Util.getString(PREFIX + "removePropertyAction.text"), SWT.BORDER) { //$NON-NLS-1$
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.action.Action#run()
                 */
                @Override
                public void run() {
                    handlePropertyRemoved();
                }
            };

            this.deletePropertyAction.setToolTipText(Util.getString(PREFIX + "removePropertyAction.toolTip")); //$NON-NLS-1$
            this.deletePropertyAction.setEnabled(false);
            toolBarMgr.add(new Separator());
            toolBarMgr.add(this.deletePropertyAction);

            //
            // add the restore default value action to the toolbar
            //

            this.restorePropertyDefaultAction = new Action(Util.getString(PREFIX + "restorePropertyDefaultAction.text"), SWT.BORDER) { //$NON-NLS-1$
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.action.Action#run()
                 */
                @Override
                public void run() {
                    handleRestorePropertyDefaultValue();
                }
            };

            this.restorePropertyDefaultAction.setImageDescriptor(VdbUiPlugin.singleton.getImageDescriptor(RESTORE_DEFAULT_VALUE));
            this.restorePropertyDefaultAction.setToolTipText(Util.getString(PREFIX + "restorePropertyDefaultAction.toolTip")); //$NON-NLS-1$
            this.restorePropertyDefaultAction.setEnabled(false);
            toolBarMgr.add(new Separator());
            toolBarMgr.add(this.restorePropertyDefaultAction);

            // update toolbar to show all actions
            toolBarMgr.update(true);
        }

        splitter.setWeights(new int[] { 25, 75 });

        // populate with data from the VDB
        this.translatorsViewer.setInput(this);
        this.propertiesViewer.setInput(this);
    }

    private TranslatorOverrideProperty getSelectedProperty() {
        IStructuredSelection selection = (IStructuredSelection)this.propertiesViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (TranslatorOverrideProperty)selection.getFirstElement();
    }

    TranslatorOverride getSelectedTranslator() {
        ISelection selection = this.translatorsViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (TranslatorOverride)((IStructuredSelection)selection).getFirstElement();
    }

    Set<TranslatorOverride> getTranslatorOverrides() {
        return this.vdb.getTranslators();
    }

    private List<String> getTranslatorOverrideNames() {
        List<String> names = new ArrayList<String>(this.vdb.getTranslators().size());

        for (TranslatorOverride translator : this.vdb.getTranslators()) {
            names.add(translator.getName());
        }

        return names;
    }

    private String[] getTranslatorTypes() {
        SourceHandler handler = SourceHandlerExtensionManager.getVdbConnectionFinder();
        return handler.getTranslatorTypes();
    }

    void handleAddProperty() {
        assert (!this.translatorsViewer.getSelection().isEmpty());

        AddPropertyDialog dialog = new AddPropertyDialog(getShell(), getSelectedTranslator().getPropertyNames());

        if (dialog.open() == Window.OK) {
            // update model
            TranslatorOverride translator = getSelectedTranslator();
            TranslatorOverrideProperty property = dialog.getProperty();
            translator.addProperty(property);

            // update UI from model
            this.propertiesViewer.refresh();

            // select the new property
            TranslatorPropertyDefinition propDefn = property.getDefinition();

            for (TranslatorOverrideProperty prop : translator.getProperties()) {
                if (prop.getDefinition().equals(propDefn)) {
                    this.propertiesViewer.setSelection(new StructuredSelection(prop), true);
                    break;
                }
            }
        }
    }

    void handleAddTranslatorOverride() {
        TranslatorOverride translatorOverride = null;
        String[] translatorTypes = getTranslatorTypes();

        // if no default server or server not connected then there won't be any translators
        if (translatorTypes == null) {
            AddTranslatorOverrideDialog dialog = new AddTranslatorOverrideDialog(getShell(), getTranslatorOverrideNames());

            if (dialog.open() == Window.OK) {
                translatorOverride = new TranslatorOverride(this.vdb, dialog.getName(), dialog.getType(), null);
            }
        } else {
            EditTranslatorOverrideDialog dialog = new EditTranslatorOverrideDialog(getShell(),
                                                                                 translatorTypes,
                                                                                 this.vdb.getTranslators());

            if (dialog.open() == Window.OK) {
                translatorOverride = new TranslatorOverride(this.vdb, dialog.getName(), dialog.getType(), null);
            }
        }

        if (translatorOverride != null) {
            // update model
            this.vdb.addTranslator(translatorOverride, null);

            // update UI
            this.translatorsViewer.refresh(); // reload translators
            this.translatorsViewer.setSelection(new StructuredSelection(translatorOverride), true);
        }
    }

    void handleDescriptionChanged() {
        if (!this.translatorsViewer.getSelection().isEmpty()) {
            TranslatorOverride translator = getSelectedTranslator();
            translator.setDescription(this.txtDescription.getText());
        }
    }

    void handleEditTranslatorOverride() {
        assert (!this.translatorsViewer.getSelection().isEmpty());

        TranslatorOverride translatorOverride = getSelectedTranslator();
        EditTranslatorOverrideDialog dialog = new EditTranslatorOverrideDialog(getShell(),
                                                                               translatorOverride,
                                                                               this.vdb.getTranslators());

        if (dialog.open() == Window.OK) {
            this.translatorsViewer.refresh(translatorOverride);
        }
    }

    void handlePropertyRemoved() {
        TranslatorOverrideProperty selectedProperty = getSelectedProperty();
        assert (selectedProperty != null);

        // update model
        TranslatorOverride translator = getSelectedTranslator();
        translator.removeProperty(selectedProperty.getDefinition().getId());
        // TODO need to dirty VDB

        // update UI
        this.propertiesViewer.refresh();
    }

    void handlePropertySelected( SelectionChangedEvent event ) {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();

        if (selection.isEmpty()) {
            if (this.deletePropertyAction.isEnabled()) {
                this.deletePropertyAction.setEnabled(false);
            }

            if (this.restorePropertyDefaultAction.isEnabled()) {
                this.restorePropertyDefaultAction.setEnabled(false);
            }
        } else {
            TranslatorOverrideProperty prop = (TranslatorOverrideProperty)selection.getFirstElement();

            // only enable delete property if it is a custom property
            boolean enable = prop.isCustom();

            if (this.deletePropertyAction.isEnabled() != enable) {
                this.deletePropertyAction.setEnabled(enable);
            }

            // only enable restore default value if it has overridden value
            enable = prop.hasOverridenValue();

            if (this.restorePropertyDefaultAction.isEnabled() != enable) {
                this.restorePropertyDefaultAction.setEnabled(enable);
            }
        }
    }

    void handleRestorePropertyDefaultValue() {
        assert (!this.propertiesViewer.getSelection().isEmpty());

        TranslatorOverrideProperty prop = getSelectedProperty();
        prop.setValue(null);
        // TODO this needs to dirty VDB
        this.propertiesViewer.refresh(prop);
        this.restorePropertyDefaultAction.setEnabled(false);
    }

    void handleTranslatorRemoved() {
        assert (!this.translatorsViewer.getSelection().isEmpty());

        TranslatorOverride translatorOverride = getSelectedTranslator();

        if (this.vdb.removeTranslator(translatorOverride, null)) {
            this.translatorsViewer.remove(translatorOverride);
        }
    }

    void handleTranslatorSelected( SelectionChangedEvent event ) {
        ISelection selection = this.translatorsViewer.getSelection();

        if (selection.isEmpty()) {
            this.txtDescription.setText(StringUtilities.EMPTY_STRING);

            if (this.txtDescription.isEnabled()) {
                this.txtDescription.setEnabled(false);
            }

            if (this.editTranslatorAction.isEnabled()) {
                this.editTranslatorAction.setEnabled(false);
            }

            if (this.deleteTranslatorAction.isEnabled()) {
                this.deleteTranslatorAction.setEnabled(false);
            }

            if (this.addPropertyAction.isEnabled()) {
                this.addPropertyAction.setEnabled(false);
            }
        } else {
            if (!this.txtDescription.isEnabled()) {
                this.txtDescription.setEnabled(true);
            }

            if (!this.editTranslatorAction.isEnabled()) {
                this.editTranslatorAction.setEnabled(true);
            }

            if (!this.deleteTranslatorAction.isEnabled()) {
                this.deleteTranslatorAction.setEnabled(true);
            }

            if (!this.addPropertyAction.isEnabled()) {
                this.addPropertyAction.setEnabled(true);
            }

            TranslatorOverride translator = getSelectedTranslator();
            assert (translator != null);

            // get properties (server may have modified properties, server may be down, etc.)
            SourceHandler handler = SourceHandlerExtensionManager.getVdbConnectionFinder();
            PropertyDefinition[] propertyDefinitionsFromServer = handler.getTranslatorDefinitions(translator.getType());
            TranslatorOverrideProperty[] currentProps = translator.getProperties();
            List<TranslatorOverrideProperty> propsToRemove = new ArrayList<TranslatorOverrideProperty>();

            if (propertyDefinitionsFromServer != null) {
                List<PropertyDefinition> newServerProps = new ArrayList<PropertyDefinition>();

                // assume all server properties are new
                for (PropertyDefinition propDefn : propertyDefinitionsFromServer) {
                    newServerProps.add(propDefn);
                }

                if (currentProps.length != 0) {
                    // translator properties already exist, match with server props
                    for (TranslatorOverrideProperty property : currentProps) {
                        PropertyDefinition serverPropDefn = null;

                        // see if property definitions from server already exist in overridden translator
                        for (PropertyDefinition propDefn : propertyDefinitionsFromServer) {
                            // found a matching one
                            if (property.getDefinition().getId().equals(propDefn.getId())) {
                                serverPropDefn = propDefn;
                                newServerProps.remove(propDefn);
                                break;
                            }
                        }

                        if (serverPropDefn != null) {
                            // found existing property so update defn and use value from old defn
                            translator.updatePropertyDefinition(property.getDefinition().getId(), serverPropDefn, false);
                        } else if (property.hasOverridenValue()) {
                            // not found on server but has an overridden value
                            translator.markAsUserDefined(property);
                        } else {
                            // not found on server and has no overridden value so remove
                            propsToRemove.add(property);
                        }
                    }
                }

                // add in new properties from server
                if (!newServerProps.isEmpty()) {
                    for (PropertyDefinition delegate : newServerProps) {
                        TranslatorOverrideProperty newProp = new TranslatorOverrideProperty(new TranslatorPropertyDefinition(delegate),
                                                                                            null);
                        translator.addProperty(newProp, false);
                    }
                }
            } else {
                // no properties found for server so remove all without values (no default server, server is down)
                if (currentProps.length != 0) {
                    for (TranslatorOverrideProperty property : currentProps) {
                        if (!property.hasOverridenValue()) {
                            propsToRemove.add(property);
                        }
                    }
                }
            }

            // remove orphaned properties that don't have overridden values
            if (!propsToRemove.isEmpty()) {
                for (TranslatorOverrideProperty property : propsToRemove) {
                    translator.removeProperty(property.getDefinition().getId(), false);
                }
            }

            this.txtDescription.setText(translator.getDescription());
        }

        this.propertiesViewer.refresh();
        WidgetUtil.pack(this.propertiesViewer);
    }
    
    /**
     * Public access to refresh the contents of this panel based on external changes to the translator override
     * properties
     */
    public void refresh() {
        this.translatorsViewer.setInput(this);
        this.translatorsViewer.refresh();
        this.propertiesViewer.setInput(this);
        this.propertiesViewer.refresh();
    }

    class PropertyLabelProvider extends ColumnLabelProvider {

        private final boolean nameColumn;

        public PropertyLabelProvider( boolean nameColumn ) {
            this.nameColumn = nameColumn;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;
            String overridenValue = property.getOverriddenValue();
            Image image = null;

            if (this.nameColumn) {
                if (property.isCustom()) {
                    image = VdbUiPlugin.singleton.getImage(EDIT_TRANSLATOR);
                }
            } else {
                if (property.getDefinition().isValidValue(overridenValue) == null) {
                    if (property.hasOverridenValue()) {
                        if (!property.isCustom() || !property.getDefinition().getDefaultValue().equals(overridenValue)) {
                            image = VdbUiPlugin.singleton.getImage(EDIT_TRANSLATOR);
                        }
                    }
                } else {
                    image = UiPlugin.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
                }
            }

            return image;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;

            if (this.nameColumn) {
                return property.getDefinition().getDisplayName();
            }

            boolean masked = property.getDefinition().isMasked();
            final String maskedValue = "*****"; //$NON-NLS-1$

            // return override value if it exists
            if (property.hasOverridenValue()) {
                return (masked ? maskedValue : property.getOverriddenValue());
            }

            // return default value
            return (masked ? maskedValue : property.getDefinition().getDefaultValue());
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
         */
        @Override
        public String getToolTipText( Object element ) {
            TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;

            if (this.nameColumn) {
                return property.getDefinition().getDescription();
            }

            if (property.hasOverridenValue()) {
                if (!property.isCustom() || !property.getDefinition().getDefaultValue().equals(property.getOverriddenValue())) {
                    return property.getDefinition().isValidValue(property.getOverriddenValue());
                }
            }

            // default value is being used
            return Util.getString(TranslatorOverridesPanel.PREFIX + "usingPropertyDefaultValue"); //$NON-NLS-1$
        }
    }

}
