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

package com.metamatrix.modeler.internal.ui.views;

import java.util.EventObject;
import java.util.Stack;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.provider.XSDSemanticItemProviderAdapterFactory;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.event.EventSourceException;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.DatatypeHierarchyTreeViewer;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.ui.internal.eventsupport.CompositeNotifyChangeListener;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.widget.AbstractTableLabelProvider;
import com.metamatrix.ui.internal.widget.DefaultContentProvider;

/**
 * DatatypeHierarchyView is the ViewPart to display Datatypes in the Modeler.
 * 
 * @since 4.0
 */
public class DatatypeHierarchyView extends ModelerView {

    private static final int NAME_COLUMN = 0;
    private static final int VALUE_COLUMN = 1;
    private static final int TYPE_COLUMN = 2;
    private static final String LABEL_DETAILS = UiConstants.Util.getString("DatatypeHierarchyView.details"); //$NON-NLS-1$
    private static final String LABEL_NAME = UiConstants.Util.getString("DatatypeHierarchyView.name"); //$NON-NLS-1$
    private static final String LABEL_VALUE = UiConstants.Util.getString("DatatypeHierarchyView.value"); //$NON-NLS-1$
    private static final String LABEL_TYPE = UiConstants.Util.getString("DatatypeHierarchyView.type"); //$NON-NLS-1$

    TreeViewer typeTree;
    TableViewer detailsTable;
    private CompositeNotifyChangeListener notifyListener;
    private EventObjectListener modelResourceListener;

    /**
     * Construct a DatatypeHierarchyView for the Modeler
     * 
     * @since 4.0
     */
    public DatatypeHierarchyView() {
        super();
    }

    public void revealType( XSDTypeDefinition type ) {
        if (typeTree != null) {
            // gather a list of all parents. Use a stack so we can process in reverse:
            Stack s = new Stack();
            try {
                Object root = ModelerCore.getBuiltInTypesManager().getAnyType();
                XSDTypeDefinition parent = type.getBaseType();
                while (parent != root) {
                    s.push(parent);
                    parent = parent.getBaseType();
                } // endwhile
            } catch (ModelerCoreException err) {
                UiConstants.Util.log(err);
            } // endtry

            // make sure all parents are opened:
            while (!s.isEmpty()) {
                Object element = s.pop();
                typeTree.setExpandedState(element, true);
            } // endwhile

            typeTree.setSelection(new StructuredSelection(type));
            typeTree.reveal(type);
        } // endif
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    @Override
    public void createPartControl( final Composite parent ) {
        super.createPartControl(parent);

        // create components:
        SashForm sf = new SashForm(parent, SWT.VERTICAL);

        // upper tree:
        typeTree = new DatatypeHierarchyTreeViewer(sf);

        // lower portion:
        Group lowerComp = new Group(sf, SWT.NONE);
        lowerComp.setText(LABEL_DETAILS);
        lowerComp.setLayout(new FillLayout());

        // create details table:
        detailsTable = new TableViewer(lowerComp);
        detailsTable.setContentProvider(new DefaultContentProvider() {
            @Override
            public Object[] getElements( Object inputElement ) {
                return ((XSDSimpleTypeDefinition)inputElement).getFacets().toArray();
            }
        }); // endanon
        detailsTable.setLabelProvider(new DetailsTableLabelProvider());

        // set up table component:
        Table table = detailsTable.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(false);

        // set up columns:
        TableColumn col = new TableColumn(table, SWT.RIGHT);
        col.setText(LABEL_NAME);
        col = new TableColumn(table, SWT.LEFT);
        col.setText(LABEL_VALUE);
        col = new TableColumn(table, SWT.LEFT);
        col.setText(LABEL_TYPE);
        // pack the columns to get them to show up:
        packColumns();

        sf.setWeights(new int[] {3, 1});

        // set up view part:
        setPartName(UiConstants.Util.getString("DatatypeHierarchyView.title")); //$NON-NLS-1$
        // hook up this view's selection provider to this site
        getViewSite().setSelectionProvider(typeTree);

        // set up listeners
        notifyListener = new CompositeNotifyChangeListener();
        ModelUtilities.addNotifyChangedListener(notifyListener);
        notifyListener.addNotifyChangeListener(new DatatypeHierarchyNotificationHandler());

        // hook up our status bar manager for EObject selection inside this view
        typeTree.addSelectionChangedListener(new ISelectionChangedListener() {
            private Object lastSelected;

            public void selectionChanged( SelectionChangedEvent event ) {
                ISelection sel = event.getSelection();
                if (!sel.isEmpty()) {
                    Object selectedObject = SelectionUtilities.getSelectedObject(sel);
                    if (lastSelected != selectedObject) {
                        // selection is different:
                        lastSelected = selectedObject;
                        detailsTable.setInput(selectedObject);
                        packColumns();
                    } // endif
                } // endif
            }
        });
        typeTree.addSelectionChangedListener(getStatusBarListener());

        // Hook up this view to listen for resource change events. Basically need to refresh the tree whenever an XSD file is
        // being opened or closed?
        modelResourceListener = new EventObjectListener() {
            public void processEvent( EventObject obj ) {
                ModelResourceEvent event = (ModelResourceEvent)obj;
                final IResource file = event.getResource();

                // defect 16898 - since we now get notifications for projects,
                // the extension may be null.
                String fileExtension = file.getFileExtension();
                if (fileExtension != null && fileExtension.equalsIgnoreCase("xsd")) { //$NON-NLS-1$
                    int type = event.getType();
                    if (type == ModelResourceEvent.CLOSED || type == ModelResourceEvent.RELOADED
                        || type == ModelResourceEvent.ADDED || type == ModelResourceEvent.REMOVED
                        || type == ModelResourceEvent.CHANGED) {
                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                if (!typeTree.getTree().isDisposed()) {
                                    typeTree.refresh();
                                }
                            }
                        });
                    }
                }
            }
        };
        try {
            UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, modelResourceListener);
        } catch (EventSourceException e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        typeTree.expandToLevel(2);
    }

    void packColumns() {
        Table table = detailsTable.getTable();
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn col = table.getColumn(i);
            col.pack();
            if (col.getWidth() > 200) {
                col.setWidth(200);
            } // endif
        } // endfor
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     * @since 4.0
     */
    @Override
    public void setFocus() {
        if (typeTree != null && !typeTree.getTree().isDisposed()) {
            typeTree.getTree().setFocus();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        if (notifyListener != null) {
            ModelUtilities.removeNotifyChangedListener(notifyListener);
        }
        if (modelResourceListener != null) {
            try {
                UiPlugin.getDefault().getEventBroker().removeListener(modelResourceListener);
            } catch (EventSourceException e) {
                UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
    }

    /**
     * DatatypeHierarchyNotificationHandler is the notification handler for the Datatype viewer.
     */
    class DatatypeHierarchyNotificationHandler implements INotifyChangedListener {

        /* (non-Javadoc)
         * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        public void notifyChanged( Notification notification ) {
            if (typeTree != null && !typeTree.getTree().isDisposed()) {
                EObject obj = NotificationUtilities.getEObject(notification);
                if (obj instanceof XSDSimpleTypeDefinition) {
                    XSDSimpleTypeDefinition std = (XSDSimpleTypeDefinition)obj;
                    switch (notification.getEventType()) {
                        case Notification.ADD:
                        case Notification.ADD_MANY:
                            typeTree.refresh(std.getBaseTypeDefinition());
                            break;

                        case Notification.REMOVE:
                        case Notification.REMOVE_MANY:
                            typeTree.remove(std);
                            break;

                        case Notification.SET:
                            // A change; we need to update tree and details:
                            int feature = NotificationUtilities.getFeatureChanged(notification);
                            switch (feature) {
                                case XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__BASE_TYPE:
                                case XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__BASE_TYPE_DEFINITION:
                                    // need to refresh old and new parents and their kids:
                                    Object oldParent = notification.getOldValue();
                                    Object newParent = notification.getNewValue();

                                    typeTree.refresh(oldParent);
                                    typeTree.refresh(newParent);
                                    break;

                                case XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__NAME:
                                    // need to refresh this tree node, not kids:
                                    typeTree.update(std, null);
                                    break;

                                default:
                                    // need to refresh details info:
                                    if (std == SelectionUtilities.getSelectedObject(typeTree.getSelection())) {
                                        detailsTable.setInput(std);
                                        packColumns();
                                    } // endif
                            } // endswitch -- feature id changed
                            break;

                        default:
                            // we should not care about any other kinds of notifications...
                            break;
                    } // endswitch -- notification type
                } // endif -- on a type
            } // endif -- tree available
        }
    } // endclass DatatypeHierarchyNotificationHandler

    class DetailsTableLabelProvider extends AbstractTableLabelProvider {
        private AdapterFactoryLabelProvider labelProv = new AdapterFactoryLabelProvider(
                                                                                        new XSDSemanticItemProviderAdapterFactory());

        public String getColumnText( Object element,
                                     int columnIndex ) {
            if (element instanceof XSDFacet) {
                XSDFacet f = (XSDFacet)element;
                switch (columnIndex) {
                    case NAME_COLUMN:
                        return f.getFacetName();

                    case VALUE_COLUMN:
                        return f.getLexicalValue();

                    case TYPE_COLUMN:
                        XSDSimpleTypeDefinition std = f.getSimpleTypeDefinition();
                        if (detailsTable.getInput() == std) {
                            return ""; //$NON-NLS-1$
                        } // endif

                        return labelProv.getText(std);

                    default: // ignore
                } // endswitch
            } // endif

            return null;
        }
    }
}
