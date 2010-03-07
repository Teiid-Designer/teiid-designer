/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;
import org.teiid.designer.runtime.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.IExecutionConfigurationListener;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ServerManager;
import org.teiid.designer.runtime.ExecutionConfigurationEvent.EventType;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.workspace.ConnectorsViewTreeProvider;

/**
 * The <code>ServerView</code> shows all defined servers and their repositories.
 */
public final class ServerView extends ViewPart implements IExecutionConfigurationListener {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * Collapses all tree nodes.
     */
    private IAction collapseAllAction;

    /**
     * Deletes a server.
     */
    private BaseSelectionListenerAction deleteAction;

    /**
     * Edits a server's properties.
     */
    private BaseSelectionListenerAction editAction;

    /**
     * Creates a new server.
     */
    private Action newAction;

    /**
     * The viewer's content and label provider.
     */
    private ConnectorsViewTreeProvider provider;

    /**
     * Refreshes the server connections.
     */
    private ReconnectToServerAction reconnectAction;

    private TreeViewer viewer;

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    private void constructActions() {
        // the collapse all action is always enabled
        this.collapseAllAction = new Action() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                getViewer().collapseAll();
            }
        };

        this.collapseAllAction.setToolTipText(UTIL.getString("collapseActionToolTip.text()"));
        this.collapseAllAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.COLLAPSE_ALL_ICON));

        // the reconnect action tries to ping a selected server
        this.reconnectAction = new ReconnectToServerAction(this.viewer);
        this.viewer.addSelectionChangedListener(this.reconnectAction);

        // the shell used for dialogs that the actions display
        Shell shell = this.getSite().getShell();

        // the delete action will delete one or more servers
        this.deleteAction = new DeleteServerAction(shell, getServerManager());
        this.viewer.addSelectionChangedListener(this.deleteAction);

        // the edit action is only enabled when one server is selected
        this.editAction = new EditServerAction(shell, getServerManager());
        this.viewer.addSelectionChangedListener(this.editAction);

        // the new server action is always enabled
        this.newAction = new NewServerAction(shell, getServerManager());
    }

    private void constructContextMenu() {
        MenuManager menuMgr = new MenuManager();
        menuMgr.add(this.newAction);
        menuMgr.add(this.editAction);
        menuMgr.add(this.deleteAction);
        menuMgr.add(this.reconnectAction);

        Menu menu = menuMgr.createContextMenu(this.viewer.getTree());
        this.viewer.getTree().setMenu(menu);
        getSite().registerContextMenu(menuMgr, this.viewer);
    }

    private void constructToolBar() {
        IToolBarManager toolBar = getViewSite().getActionBars().getToolBarManager();
        toolBar.add(this.newAction);
        toolBar.add(this.editAction);
        toolBar.add(this.deleteAction);
        toolBar.add(this.reconnectAction);
        toolBar.add(this.collapseAllAction);
    }

    /**
     * @param parent the viewer's parent
     */
    private void constructTreeViewer( Composite parent ) {
        this.provider = new ConnectorsViewTreeProvider(true);
        this.viewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);

        this.viewer.setContentProvider(this.provider);
        ILabelDecorator decorator = DqpUiPlugin.getDefault().getWorkbench().getDecoratorManager().getLabelDecorator();
        this.viewer.setLabelProvider(new DecoratingLabelProvider(this.provider, decorator));
        ColumnViewerToolTipSupport.enableFor(this.viewer);

        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handleSelectionChanged(event);
            }
        });
        this.viewer.addDoubleClickListener(new IDoubleClickListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
             */
            @Override
            public void doubleClick( DoubleClickEvent arg0 ) {
                handleDoubleClick();
            }
        });

        // need to call this (doesn't matter what the param is) to bootstrap the provider.
        this.viewer.setInput(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent ) {
        constructTreeViewer(parent);
        constructActions();
        constructToolBar();
        constructContextMenu();
        hookGlobalActions();

        setTitleToolTip(UTIL.getString("serverViewToolTip.text()"));

        // register to receive changes to the server registry
        getServerManager().addListener(this);
        // getServerManager().addListener(this.provider);

        // register with the help system
        IWorkbenchHelpSystem helpSystem = DqpUiPlugin.getDefault().getWorkbench().getHelpSystem();
        helpSystem.setHelp(parent, "SERVER_VIEW_HELP_CONTEXT");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        getServerManager().removeListener(this);

        if (this.provider != null) {
            // getServerManager().removeListener(this.provider);
        }

        super.dispose();
    }

    IAction getDeleteAction() {
        return this.deleteAction;
    }

    /**
     * @return the server manager being used by this view
     */
    private ServerManager getServerManager() {
        return DqpPlugin.getInstance().getServerRegistry();
    }

    /**
     * @return the tree viewer
     */
    TreeViewer getViewer() {
        return this.viewer;
    }

    /**
     * Opens a dialog to edit server properties.
     */
    void handleDoubleClick() {
        this.editAction.run();
    }

    /**
     * @param event the event being processed
     */
    void handleSelectionChanged( SelectionChangedEvent event ) {
        updateStatusLine((IStructuredSelection)event.getSelection());
    }

    /**
     * Sets global action handlers.
     */
    private void hookGlobalActions() {
        IActionBars bars = getViewSite().getActionBars();

        // hook delete server action up
        bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), this.deleteAction);
        this.viewer.getControl().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed( KeyEvent event ) {
                if ((event.character == SWT.DEL) && (event.stateMask == 0) && getDeleteAction().isEnabled()) {
                    getDeleteAction().run();
                }
            }
        });

        // don't want cut, copy, or paste actions so hook them up with a disabled action
        class NoOpAction extends Action {
            NoOpAction() {
                setEnabled(false);
            }
        }

        IAction noop = new NoOpAction();
        bars.setGlobalActionHandler(ActionFactory.CUT.getId(), noop);
        bars.setGlobalActionHandler(ActionFactory.COPY.getId(), noop);
        bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), noop);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.IExecutionConfigurationListener#configurationChanged(org.teiid.designer.runtime.ExecutionConfigurationEvent)
     */
    @Override
    public void configurationChanged( ExecutionConfigurationEvent event ) {
        if (event.getEventType() == EventType.ADD || event.getEventType() == EventType.UPDATE) {
            this.viewer.refresh();
        } else if (event.getEventType() == EventType.REMOVE) {
            this.viewer.remove(event.getServer());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        if (!this.viewer.getControl().isDisposed()) {
            this.viewer.getControl().setFocus();
        }
    }

    /**
     * @param selection the current viewer selection (never <code>null</code>)
     */
    private void updateStatusLine( IStructuredSelection selection ) {
        assert (selection.size() < 2);

        String msg = (selection.isEmpty() ? "" : ((Server)selection.getFirstElement()).toString()); //$NON-NLS-1$
        getViewSite().getActionBars().getStatusLineManager().setMessage(msg);
    }

}
