/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.views.ConnectorBindingsTreeProvider;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 4.2
 */
public class ExistingConnectorBindingPanel extends BaseNewConnectorBindingPanel {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ExistingConnectorBindingPanel.class);

    TreeViewer treeViewer;
    ConnectorBindingsTreeProvider treeProvider;
    private ConnectorType currentType;
    private Connector currentBinding;
    private boolean hasInitialized = false;
    private Action deleteAction;

    private IChangeListener configListener;

    /**
     * @param parent
     * @since 4.2
     */
    public ExistingConnectorBindingPanel( Composite parent,
                                          ConnectorType type,
                                          Connector connector ) {
        super(parent);
        this.currentType = type;
        this.currentBinding = connector;

        buildControls();
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.dialogs.BaseNewConnectorBindingPanel#getConnector()
     * @since 4.3
     */
    @Override
    public Connector getConnector() {
        Connector result = null;
        Object selection = SelectionUtilities.getSelectedObject(treeViewer.getSelection());

        if ((selection != null) && (selection instanceof Connector)) {
            result = (Connector)selection;
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.dialogs.BaseNewConnectorBindingPanel#getI18nPrefix()
     * @since 4.3
     */
    @Override
    protected String getI18nPrefix() {
        return PREFIX;
    }

    private void buildControls() {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        this.setLayout(layout);
        this.setLayoutData(new GridData(GridData.FILL_BOTH));

        treeViewer = new TreeViewer(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER);
        treeProvider = new ConnectorBindingsTreeProvider();
        treeViewer.setContentProvider(treeProvider);
        treeViewer.setLabelProvider(treeProvider);

        treeViewer.setInput(DqpPlugin.getInstance().getAdmin());
        treeViewer.expandToLevel(2);

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        treeViewer.getControl().setLayoutData(gd);
        treeViewer.setSorter(new ViewerSorter() {});
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleSelectionChanged();
            }
        });
        treeViewer.addFilter(new ViewerFilter() {
            @Override
            public boolean select( Viewer theViewer,
                                   Object theParentElement,
                                   Object theElement ) {
                // only show connector types if they have bindings
                return ((theElement instanceof Connector) || treeProvider.hasChildren(theElement));
            }
        });

        if (this.currentBinding != null) {
            this.treeViewer.setSelection(new StructuredSelection(this.currentBinding));
        }

        // register to receive configuration changes
        this.configListener = new IChangeListener() {
            public void stateChanged( IChangeNotifier theSource ) {
                handleConfigChanged();
            }
        };
        DqpPlugin.getInstance().getAdmin().addChangeListener(configListener);

        // add dispose listener because when I overrode dispose() it never got called
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent theEvent ) {
                handleDispose();
            }
        });

        MenuManager popupMenuManager = new MenuManager();
        IMenuListener listener = new IMenuListener() {
            public void menuAboutToShow( IMenuManager mng ) {
                fillContextMenu(mng);
            }
        };
        popupMenuManager.setRemoveAllWhenShown(true);
        popupMenuManager.addMenuListener(listener);
        Menu menu = popupMenuManager.createContextMenu(treeViewer.getTree());
        treeViewer.getTree().setMenu(menu);

        deleteAction = new Action() {
            @Override
            public void run() {
                // Delete
                delete(treeViewer.getSelection());
            }
        };
        deleteAction.setText(getString("delete")); //$NON-NLS-1$
        deleteAction.setToolTipText(getString("deleteTooltip")); //$NON-NLS-1$
        deleteAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.DELETE_ICON));
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.dialogs.BaseNewConnectorBindingPanel#getStatus()
     * @since 4.3
     */
    @Override
    protected IStatus getStatus() {
        int severity = IStatus.ERROR;
        String msg = "Message has not been set"; //$NON-NLS-1$

        if (getConnector() == null) {
            if (this.treeProvider.containsBindings()) {
                msg = getString("errorMsg"); //$NON-NLS-1$
            } else {
                msg = getString("noExistingBindingsMsg"); //$NON-NLS-1$
            }
        } else {
            severity = IStatus.OK;
            msg = UTIL.getString(PREFIX + "okMsg", getConnector()); //$NON-NLS-1$
        }

        return BaseNewConnectorBindingPanel.createStatus(severity, msg);
    }

    /**
     * Indicates if the configuration has any loaded connector bindings.
     * 
     * @return <code>true</code> if configuration has loaded bindings; <code>false</code> otherwise.
     * @since 5.5
     */
    public boolean hasLoadedBindings() {
        return this.treeProvider.containsBindings();
    }

    /**
     * Alert listeners of a state change.
     * 
     * @since 4.3
     */
    void handleSelectionChanged() {
        fireChangeEvent();
    }

    void handleConfigChanged() {
        if (treeViewer != null && !treeViewer.getTree().isDisposed()) {
            UiUtil.runInSwtThread(new Runnable() {
                public void run() {
                    treeViewer.refresh(true);
                }
            }, false);
        }
    }

    /**
     * Cleanup for when disposed.
     * 
     * @since 5.0
     */
    void handleDispose() {
        DqpPlugin.getInstance().getAdmin().removeChangeListener(this.configListener);
    }

    @Override
    public boolean setFocus() {
        if (!hasInitialized) {
            getDisplay().asyncExec(new Runnable() {
                public void run() {
                    initDisplay();
                }
            });
        }
        return treeViewer.getControl().setFocus();
    }

    void initDisplay() {
        hasInitialized = true;
        if (currentBinding != null) {
            // make sure the type gets revealed.
            treeViewer.reveal(currentBinding.getComponentTypeID());
            treeViewer.setSelection(new StructuredSelection(currentBinding), true);
        } else if (currentType != null) {
            treeViewer.setSelection(new StructuredSelection(currentType), true);
        }
    }

    void fillContextMenu( IMenuManager menuManager ) {
        menuManager.add(new Separator());
        menuManager.add(deleteAction);
        menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    void delete( ISelection selection ) {
        if (selection instanceof StructuredSelection) {
            if (((StructuredSelection)selection).size() >= 1) {
                StructuredSelection sel = (StructuredSelection)selection;
                Object[] selList = sel.toArray();
                for (int i = 0; i < selList.length; i++) {
                    try {
                        if (selList[i] instanceof Connector) {
                            DqpPlugin.getInstance().getAdmin().removeBinding((Connector)selList[i]);
                        }
                    } catch (Exception error) {
                        DqpUiPlugin.showErrorDialog(getShell(), error);
                    }
                }
            }
        }
    }
}
