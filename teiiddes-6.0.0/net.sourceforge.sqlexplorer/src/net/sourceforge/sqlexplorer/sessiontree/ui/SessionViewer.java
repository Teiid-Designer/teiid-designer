/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.sqlexplorer.sessiontree.ui;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.views.ConnectionInfo;
import net.sourceforge.sqlexplorer.plugin.views.DBView;
import net.sourceforge.sqlexplorer.plugin.views.SqlexplorerViewConstants;
import net.sourceforge.sqlexplorer.sessiontree.actions.NewSQLEditor;
import net.sourceforge.sqlexplorer.sessiontree.model.RootSessionTreeNode;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeContentProvider;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeLabelProvider;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeModel;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeModelChangedListener;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;

public class SessionViewer extends TreeViewer {

    SessionTreeModel md;
    boolean disposed = false;

    /**
     * @param parent
     */
    public SessionViewer( final Composite parent,
                          int style,
                          SessionTreeModel md,
                          final IViewPart viewPart ) {
        super(parent, style);
        this.md = md;

        setContentProvider(new SessionTreeContentProvider());
        final SessionTreeLabelProvider stlp = new SessionTreeLabelProvider();
        this.getControl().addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent e ) {
                stlp.dispose();
                disposed = true;
            }
        });
        setLabelProvider(stlp);
        setUseHashlookup(true);
        setInput(md);
        md.addListener(new SessionTreeModelChangedListener() {
            public void modelChanged( final SessionTreeNode node_ ) {
                parent.getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        // Defect 21949 requires null checks and isDiposed() checks to prevent errors during exit/shutdown
                        if (!disposed) {
                            SessionViewer.this.refresh();
                            SessionViewer.this.expandAll();
                            if (node_ != null) {
                                SessionViewer.this.setSelection(new StructuredSelection(node_));

                                // open editor for new connection
                                new NewSQLEditor(node_).run();
                            }
                        }
                    }
                });

            }
        });
        addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent ev ) {
                // Defect 21949 requires null checks and isDiposed() checks to prevent errors during exit/shutdown
                // We don't want to process any "selection" events if the tree viewer is disposed
                if (disposed) {
                    return;
                }
                IStructuredSelection sel = (IStructuredSelection)ev.getSelection();
                if (!sel.isEmpty()) {
                    // actGroup.updateActionBars();
                    if (sel.getFirstElement() instanceof SessionTreeNode) {
                        SessionTreeNode node = (SessionTreeNode)sel.getFirstElement();

                        try {
                            IViewReference[] views = SQLExplorerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
                            for (int i = 0; i < views.length; ++i) {
                                if (views[i].getId().equals(SqlexplorerViewConstants.SQLEXPLORER_CONNINFO)) {
                                    ConnectionInfo connInfo = (ConnectionInfo)views[i].getView(false);
                                    if (connInfo != null) {
                                        connInfo.setInput(node);
                                    }
                                    break;
                                }
                            }
                        } catch (Throwable e) {
                            SQLExplorerPlugin.error("Error opening connection info view", e);
                        }
                        try {
                            DBView dbView = (DBView)SQLExplorerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("net.sourceforge.sqlexplorer.plugin.views.DBView");
                            if (dbView != null) dbView.setInput(node);

                        } catch (Throwable e) {
                            SQLExplorerPlugin.error("Error opening db view", e);
                        }

                    } else if (sel.getFirstElement() instanceof RootSessionTreeNode) {
                        try {
                            DBView dbView = (DBView)SQLExplorerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("net.sourceforge.sqlexplorer.plugin.views.DBView");
                            dbView.setInput(null);
                        } catch (Throwable e) {
                        }
                        try {
                            IViewReference[] views = SQLExplorerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
                            for (int i = 0; i < views.length; ++i) {
                                if (views[i].getId().equals(SqlexplorerViewConstants.SQLEXPLORER_CONNINFO)) {
                                    ConnectionInfo connInfo = (ConnectionInfo)views[i].getView(false);
                                    if (connInfo != null) {
                                        connInfo.setInput(null);
                                    }
                                    break;
                                }
                            }
                        } catch (Throwable e) {
                            SQLExplorerPlugin.error("Error opening connection info view", e);
                        }

                    }

                }
            }
        });

    }
}
