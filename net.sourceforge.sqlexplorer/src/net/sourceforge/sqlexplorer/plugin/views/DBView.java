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
package net.sourceforge.sqlexplorer.plugin.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.sourceforge.sqlexplorer.dbviewer.DatabaseContentProvider;
import net.sourceforge.sqlexplorer.dbviewer.DatabaseLabelProvider;
import net.sourceforge.sqlexplorer.dbviewer.DetailManager;
import net.sourceforge.sqlexplorer.dbviewer.actions.DatabaseActionGroup;
import net.sourceforge.sqlexplorer.dbviewer.model.IDbModel;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.model.ISessionTreeClosedListener;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Andrea Mazzolini To change the template for this generated type comment go to Window>Preferences>Java>Code
 *         Generation>Code and Comments
 */
public class DBView extends ViewPart {
    SessionTreeNode node_;
    // TreeViewer tv;
    TabFolder tabFolder;
    HashMap nodeTabItemsMap = new HashMap();

    private Couple createItem( SessionTreeNode sessionTreeNode,
                               int theTabIndex ) {
        TabItem ti = (theTabIndex == -1) ? new TabItem(tabFolder, SWT.NULL) : new TabItem(tabFolder, SWT.NULL, theTabIndex);
        ti.setText(sessionTreeNode.toString());
        SashForm sash = new SashForm(tabFolder, SWT.VERTICAL);
        ti.setControl(sash);
        sash.setLayout(new FillLayout());
        final TreeViewer tv = new TreeViewer(sash, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        tv.setUseHashlookup(true);
        Transfer[] transfers = new Transfer[] {TableNodeTransfer.getInstance()};
        tv.addDragSupport(DND.DROP_COPY, transfers, new DragSourceListener() {

            public void dragStart( DragSourceEvent event ) {

                event.doit = !tv.getSelection().isEmpty();
                if (event.doit) {
                    Object sel = ((IStructuredSelection)tv.getSelection()).getFirstElement();
                    if (!(sel instanceof TableNode)) {
                        event.doit = false;
                    } else {
                        TableNode tn = (TableNode)sel;
                        TableNodeTransfer.getInstance().setSelection(tn);
                        if (!tn.isTable()) event.doit = false;
                    }
                }
            }

            public void dragSetData( DragSourceEvent event ) {
                Object sel = ((IStructuredSelection)tv.getSelection()).getFirstElement();
                event.data = sel;
            }

            public void dragFinished( DragSourceEvent event ) {
                TableNodeTransfer.getInstance().setSelection(null);
            }
        });
        Composite c = new Composite(sash, SWT.BORDER);
        c.setLayout(new FillLayout());
        final DetailManager dm = new DetailManager(tv, c, SQLExplorerPlugin.getDefault().getPreferenceStore(), sessionTreeNode);
        tv.setContentProvider(new DatabaseContentProvider());
        final DatabaseLabelProvider tlp = new DatabaseLabelProvider(SQLExplorerPlugin.getDefault().pluginManager);// Va rimesso il
        // plugin
        // manager
        tv.setLabelProvider(tlp);
        tv.setSorter(new ViewerSorter());

        sash.setWeights(new int[] {3, 1});

        tv.getControl().addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent e ) {
                tlp.dispose();

            }
        });
        tv.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent ev ) {
                IStructuredSelection sel = (IStructuredSelection)ev.getSelection();
                if (!sel.isEmpty()) {
                    try {
                        dm.activate((IDbModel)sel.getFirstElement());
                    } catch (Throwable e) {
                        SQLExplorerPlugin.error("Error managing selection changed ", e); //$NON-NLS-1$

                        // To catch bad-written plugins!
                    }
                }
            }
        });

        final DatabaseActionGroup actGroup = new DatabaseActionGroup(this, tv);
        MenuManager menuMgr = new MenuManager("#DbPopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        Menu fDbContextMenu = menuMgr.createContextMenu(tv.getTree());
        tv.getTree().setMenu(fDbContextMenu);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow( IMenuManager manager ) {

                actGroup.fillContextMenu(manager);
            }
        });
        return new Couple(ti, tv);
    }

    @Override
    public void createPartControl( Composite parent ) {
        tabFolder = new TabFolder(parent, SWT.NULL);
        tabFolder.addSelectionListener(new SelectionListener() {
            public void widgetSelected( SelectionEvent e ) {
                node_ = getSessionTreeNode();
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });
        parent.setLayout(new FillLayout());
    }

    /**
     * Remove any
     * 
     * @since 4.3
     */
    void handleConnectionClosed() {
        final Map connectionTabMap = this.nodeTabItemsMap;

        if ((this.tabFolder != null) && !this.tabFolder.isDisposed()) {
            this.tabFolder.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    // make sure a single connection is represented by a single tab
                    Iterator itr = connectionTabMap.entrySet().iterator();

                    while (itr.hasNext()) {
                        Map.Entry entry = (Map.Entry)itr.next();
                        SessionTreeNode session = (SessionTreeNode)entry.getKey();
                        Couple couple = (Couple)entry.getValue();
                        SQLConnection conn = session.getConnection();

                        if ((conn.getConnection() == null) || (conn.getTimeClosed() != null)) {
                            // Defect 21949 requires null checks and isDiposed() checks to prevent errors during exit/shutdown
                            if (!couple.ti.isDisposed()) {
                                Control control = couple.ti.getControl();
                                couple.ti.setControl(null);
                                couple.ti.dispose();
                                control.dispose();
                            }
                            itr.remove();
                        }
                    }
                }
            });
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
    }

    /**
     * @param node
     */
    public void setInput( SessionTreeNode node ) {
        setInput(node, false);
    }

    public void setInput( SessionTreeNode node,
                          boolean bForceReload ) {
        // jh Defect 22232: added bForceReload boolean so refresh can use this method

        if (node == node_ && !bForceReload) return;

        if (node != null) {
            Couple cp = (Couple)nodeTabItemsMap.get(node);
            int index = -1;

            // jh Defect 22232: reinitialize to force a reload
            if (cp != null) {
                nodeTabItemsMap.remove(node);

                // find tab index for tab item being removed so that we can put the new
                // tab item in the right place later
                TabItem[] items = this.tabFolder.getItems();

                for (int i = 0; i < items.length; ++i) {
                    if (items[i] == cp.ti) {
                        index = i;
                        break;
                    }
                }

                // dispose the tab item being removed
                Control control = cp.ti.getControl();
                cp.ti.setControl(null);
                cp.ti.dispose();
                control.dispose();
                cp = null;
            }

            cp = this.createItem(node, index);
            nodeTabItemsMap.put(node, cp);
            node.addListener(new ISessionTreeClosedListener() {

                public void sessionTreeClosed() {
                    handleConnectionClosed();
                }
            });
            cp.tv.setInput(node.dbModel);
            cp.tv.expandToLevel(3);
            tabFolder.setSelection(new TabItem[] {cp.ti});
            node_ = node;
        }
    }

    public SessionTreeNode getSessionTreeNode() {
        TabItem[] items = tabFolder.getSelection();
        if (items.length == 0) return null;
        TabItem ti = items[0];
        Iterator it = nodeTabItemsMap.keySet().iterator();
        while (it.hasNext()) {
            SessionTreeNode nd = (SessionTreeNode)it.next();
            Couple cp = (Couple)nodeTabItemsMap.get(nd);
            if (ti == cp.ti) return nd;
        }
        return null;
    }

    public void refresh() {
        // jh Defect 22232: make refresh recreate tree just like original load
        setInput(node_, true);
    }

    class Couple {
        TabItem ti;
        TreeViewer tv;

        public Couple( TabItem ti,
                       TreeViewer tv ) {
            this.ti = ti;
            this.tv = tv;
        }
    }

    /**
     * @param activeTableNode
     */
    public void tryToSelect( SessionTreeNode sessionNode,
                             IDbModel activeTableNode ) {
        if (activeTableNode instanceof TableNode) {
            Couple cp = (Couple)nodeTabItemsMap.get(sessionNode);
            cp.tv.reveal(((IDbModel)activeTableNode.getParent()).getParent());
            cp.tv.expandToLevel(activeTableNode.getParent(), 2);
            cp.tv.reveal(activeTableNode);
            cp.tv.setSelection(new StructuredSelection(new Object[] {activeTableNode}), true);

        }
    }
}
