/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.ui.translators;

import static org.teiid.designer.vdb.ui.VdbUiConstants.Util;
import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.ADD;
import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.ADD_TRANSLATOR;
import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.EDIT;
import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.EDIT_TRANSLATOR;
import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.REMOVE;
import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.REMOVE_TRANSLATOR;
import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.RESTORE_DEFAULT_VALUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.teiid.core.designer.properties.PropertyDefinition;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.translators.TranslatorOverrideProperty;
import org.teiid.designer.core.translators.TranslatorPropertyDefinition;
import org.teiid.designer.ui.common.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.viewsupport.TranslatorOverridePropertyEditingSupport;
import org.teiid.designer.vdb.TranslatorOverride;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.connections.SourceHandler;
import org.teiid.designer.vdb.connections.SourceHandlerExtensionManager;
import org.teiid.designer.vdb.ui.VdbUiPlugin;
import org.teiid.designer.vdb.ui.editor.ConfirmationDialog;


/**
 * 
 *
 * @since 8.0
 */
public final class TranslatorOverridesPanel extends Composite {

    static final String PREFIX = I18nUtil.getPropertyPrefix(TranslatorOverridesPanel.class);
    
    static final String CONFIRM_REMOVE_MESSAGE = Util.getString(PREFIX + "confirmRemoveMessage"); //$NON-NLS-1$
    
    Button addPropertyButton;
    Button deletePropertyButton;
    Button editPropertyButton;
    
    Button addTranslatorButton;
    Button deleteTranslatorButton;
    Button editTranslatorButton;

    private final TableViewer propertiesViewer;

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

            this.translatorsViewer = new TableViewer(pnlTranslators, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));
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
            Composite toolbarPanel = WidgetFactory.createPanel(pnlTranslators, SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 3);
            
            this.addTranslatorButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
            this.addTranslatorButton.setImage(VdbUiPlugin.singleton.getImage(ADD_TRANSLATOR));
            this.addTranslatorButton.setToolTipText(Util.getString(PREFIX + "addTranslatorAction.toolTip")); //$NON-NLS-1$
            this.addTranslatorButton.addSelectionListener(new SelectionListener() {
    			
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handleAddTranslatorOverride();
    			}

    			@Override
    			public void widgetDefaultSelected(SelectionEvent e) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
            
            this.editTranslatorButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
            this.editTranslatorButton.setImage(VdbUiPlugin.singleton.getImage(EDIT_TRANSLATOR));
            this.editTranslatorButton.setToolTipText(Util.getString(PREFIX + "editTranslatorAction.toolTip")); //$NON-NLS-1$
            this.editTranslatorButton.addSelectionListener(new SelectionListener() {
    			
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handleEditTranslatorOverride();
    			}

    			@Override
    			public void widgetDefaultSelected(SelectionEvent e) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
            
            this.editTranslatorButton.setEnabled(false);
            
            this.deleteTranslatorButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
            this.deleteTranslatorButton.setImage(VdbUiPlugin.singleton.getImage(REMOVE_TRANSLATOR));
            this.deleteTranslatorButton.setToolTipText(Util.getString(PREFIX + "removeTranslatorAction.toolTip")); //$NON-NLS-1$
            this.deleteTranslatorButton.addSelectionListener(new SelectionListener() {
    			
    			@Override
    			public void widgetSelected(SelectionEvent e) {
                    if (ConfirmationDialog.confirm(CONFIRM_REMOVE_MESSAGE)) {
        				handleTranslatorRemoved();
                    }
    			}

    			@Override
    			public void widgetDefaultSelected(SelectionEvent e) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
            
            this.deleteTranslatorButton.setEnabled(false);
            
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
            ((GridData)this.txtDescription.getLayoutData()). horizontalSpan = 2;
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

            this.propertiesViewer = new TableViewer(pnlOverrides, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));
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
            column.getColumn().setText(Util.getString(PREFIX + "propertyColumn.text") + "        "); //$NON-NLS-1$ //$NON-NLS-2$
            column.setLabelProvider(new PropertyLabelProvider(true));


            column = new TableViewerColumn(this.propertiesViewer, SWT.LEFT);
            column.getColumn().setText(Util.getString(PREFIX + "valueColumn.text")); //$NON-NLS-1$
            column.setLabelProvider(new PropertyLabelProvider(false));
            column.setEditingSupport(new TranslatorOverridePropertyEditingSupport(this.propertiesViewer, this.vdb.getFile()));


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
            Composite toolbarPanel = WidgetFactory.createPanel(pnlOverrides, SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 4);
            
            this.addPropertyButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
            this.addPropertyButton.setImage(VdbUiPlugin.singleton.getImage(ADD));
            this.addPropertyButton.setToolTipText(Util.getString(PREFIX + "addPropertyAction.toolTip")); //$NON-NLS-1$
            this.addPropertyButton.addSelectionListener(new SelectionListener() {
    			
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handleAddProperty();
    			}

    			@Override
    			public void widgetDefaultSelected(SelectionEvent e) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
            this.addPropertyButton.setEnabled(false);
            
            this.editPropertyButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
            this.editPropertyButton.setEnabled(false);
            this.editPropertyButton.setImage(VdbUiPlugin.singleton.getImage(EDIT));
            this.editPropertyButton.setToolTipText(Util.getString(PREFIX + "editPropertyAction.toolTip")); //$NON-NLS-1$
            this.editPropertyButton.addSelectionListener(new SelectionListener() {
    			
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handleEditProperty();
    			}

    			@Override
    			public void widgetDefaultSelected(SelectionEvent e) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
            
            this.deletePropertyButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
            this.deletePropertyButton.setEnabled(false);
            this.deletePropertyButton.setImage(VdbUiPlugin.singleton.getImage(REMOVE));
            this.deletePropertyButton.setToolTipText(Util.getString(PREFIX + "removePropertyAction.toolTip")); //$NON-NLS-1$
            this.deletePropertyButton.addSelectionListener(new SelectionListener() {
    			
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handlePropertyRemoved();
    			}

    			@Override
    			public void widgetDefaultSelected(SelectionEvent e) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
            
        	org.teiid.designer.ui.common.widget.Label noteLabel = 
        			WidgetFactory.createLabel(toolbarPanel, Util.getString(PREFIX + "overridePropertiesNoteLabel")); //$NON-NLS-1$
        	noteLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        	GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(noteLabel);
        	noteLabel.setToolTipText(Util.getString(PREFIX + "overridePropertiesNoteTooltip"));  //$NON-NLS-1$

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
        
        AddPropertyDialog dialog = new AddPropertyDialog(getShell(), Util.getString("AddPropertyDialog.addTitle"), getSelectedTranslator().getPropertyNames(), null);  //$NON-NLS-1$

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
    
    void handleEditProperty() {
        assert (!this.propertiesViewer.getSelection().isEmpty());
        
        IStructuredSelection ssel = (IStructuredSelection)this.propertiesViewer.getSelection();
        if (ssel.isEmpty()) {
        }
        
        TranslatorOverrideProperty overrideProp = (TranslatorOverrideProperty)ssel.getFirstElement();
        
        AddPropertyDialog dialog = new AddPropertyDialog(getShell(), Util.getString("AddPropertyDialog.editTitle"), getSelectedTranslator().getPropertyNames(), overrideProp); //$NON-NLS-1$

        if (dialog.open() == Window.OK) {
            // update model
            TranslatorOverride translator = getSelectedTranslator();
            TranslatorOverrideProperty property = dialog.getProperty();
            translator.removeProperty(overrideProp.getDefinition().getId());
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

        // if no default teiid instance or server not connected then there won't be any translators
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
            this.vdb.addTranslator(translatorOverride);

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
        String[] translatorTypes = getTranslatorTypes();
        EditTranslatorOverrideDialog dialog = new EditTranslatorOverrideDialog(getShell(),
                                                                               translatorOverride,
                                                                               translatorTypes,
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
            if (this.deletePropertyButton.isEnabled()) {
                this.deletePropertyButton.setEnabled(false);
            }

            if (this.editPropertyButton.isEnabled()) {
                this.editPropertyButton.setEnabled(false);
            }
        } else {
            TranslatorOverrideProperty prop = (TranslatorOverrideProperty)selection.getFirstElement();

            // only enable delete property if it is a custom property
            boolean enable = prop.isCustom();

            if (this.deletePropertyButton.isEnabled() != enable) {
                this.deletePropertyButton.setEnabled(enable);
            }

            // only enable restore default value if it has overridden value
            enable = prop.hasOverridenValue();

            if (this.editPropertyButton.isEnabled() != enable) {
                this.editPropertyButton.setEnabled(enable);
            }
        }
    }

    void handleRestorePropertyDefaultValue() {
        assert (!this.propertiesViewer.getSelection().isEmpty());

        TranslatorOverrideProperty prop = getSelectedProperty();
        prop.setValue(null);
        // TODO this needs to dirty VDB
        this.propertiesViewer.refresh(prop);
        this.editPropertyButton.setEnabled(false);
    }

    void handleTranslatorRemoved() {
        assert (!this.translatorsViewer.getSelection().isEmpty());

        TranslatorOverride translatorOverride = getSelectedTranslator();

        if (this.vdb.removeTranslator(translatorOverride)) {
            this.translatorsViewer.remove(translatorOverride);
        }
    }

    void handleTranslatorSelected( SelectionChangedEvent event ) {
        ISelection selection = this.translatorsViewer.getSelection();

        if (selection.isEmpty()) {
            this.txtDescription.setText(StringConstants.EMPTY_STRING);

            if (this.txtDescription.isEnabled()) {
                this.txtDescription.setEnabled(false);
            }

            if (this.editTranslatorButton.isEnabled()) {
                this.editTranslatorButton.setEnabled(false);
            }

            if (this.deleteTranslatorButton.isEnabled()) {
                this.deleteTranslatorButton.setEnabled(false);
            }

            if (this.addPropertyButton.isEnabled()) {
                this.addPropertyButton.setEnabled(false);
            }
        } else {
            if (!this.txtDescription.isEnabled()) {
                this.txtDescription.setEnabled(true);
            }

            if (!this.editTranslatorButton.isEnabled()) {
                this.editTranslatorButton.setEnabled(true);
            }

            if (!this.deleteTranslatorButton.isEnabled()) {
                this.deleteTranslatorButton.setEnabled(true);
            }

            if (!this.addPropertyButton.isEnabled()) {
                this.addPropertyButton.setEnabled(true);
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
                // no properties found for server so remove all without values (no default teiid instance, server is down)
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

            if (!this.nameColumn) {
                if (property.getDefinition().isValidValue(overridenValue) == null) {
                    if (property.hasOverridenValue()) {
                        if (!property.isCustom() || !property.getDefinition().getDefaultValue().equals(overridenValue)) {
                            image = VdbUiPlugin.singleton.getImage(RESTORE_DEFAULT_VALUE);
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
