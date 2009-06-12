package net.sourceforge.sqlexplorer.dialogs;

/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.sqlexplorer.AliasModel;
import net.sourceforge.sqlexplorer.DriverModel;
import net.sourceforge.sqlexplorer.IdentifierFactory;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.actions.OpenPasswordConnectDialogAction;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;

public class AliasContainerGroup extends Composite {
    Action changeAlias;
    Action copyAlias;
    Action newAlias;
    Action deleteAlias;
    TableViewer tableViewer;

    // public int setFocus() {
    // return containerGroup.setInitialFocus();
    // }
    void selectFirst() {
        if (model.getElements().length > 0) {
            Object obj = (model.getElements())[0];
            StructuredSelection sel = new StructuredSelection(obj);
            tableViewer.setSelection(sel);
        }
    }

    AliasModel model;
    DriverModel driverModel;

    private void createActions() {
        newAlias = new Action() {
            ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getAliasWizard());

            @Override
            public String getToolTipText() {
                return Messages.getString("CreateAlias"); //$NON-NLS-1$
            }

            @Override
            public String getText() {
                return Messages.getString("CreateAlias"); //$NON-NLS-1$
            }

            @Override
            public ImageDescriptor getHoverImageDescriptor() {
                return img;
            }

            @Override
            public ImageDescriptor getImageDescriptor() {
                return img;
            }

            @Override
            public void run() {
                IdentifierFactory factory = IdentifierFactory.getInstance();
                ISQLAlias alias = model.createAlias(factory.createIdentifier());
                CreateAliasDlg dlg = new CreateAliasDlg(AliasContainerGroup.this.getShell(), driverModel, 1, alias, model);
                dlg.open();
                tableViewer.refresh();
                selectFirst();
            }
        };
        changeAlias = new Action() {
            ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getEditAlias());

            @Override
            public String getText() {
                return Messages.getString("ChangeAlias"); //$NON-NLS-1$
            }

            @Override
            public String getToolTipText() {
                return Messages.getString("ChangeAlias"); //$NON-NLS-1$
            }

            @Override
            public ImageDescriptor getHoverImageDescriptor() {
                return img;
            }

            @Override
            public ImageDescriptor getImageDescriptor() {
                return img;
            }

            @Override
            public void run() {
                StructuredSelection sel = (StructuredSelection)tableViewer.getSelection();
                ISQLAlias al = (ISQLAlias)sel.getFirstElement();
                if (al != null) {
                    CreateAliasDlg dlg = new CreateAliasDlg(AliasContainerGroup.this.getShell(), driverModel, 2, al, model);
                    dlg.open();
                    tableViewer.refresh();
                    selectFirst();
                }
            }

        };
        copyAlias = new Action() {
            ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getCopyAlias());

            @Override
            public String getToolTipText() {
                return Messages.getString("CopyAlias"); //$NON-NLS-1$
            }

            @Override
            public String getText() {
                return Messages.getString("CopyAlias"); //$NON-NLS-1$
            }

            @Override
            public ImageDescriptor getHoverImageDescriptor() {
                return img;
            }

            @Override
            public ImageDescriptor getImageDescriptor() {
                return img;
            }

            @Override
            public void run() {
                StructuredSelection sel = (StructuredSelection)tableViewer.getSelection();
                ISQLAlias al = (ISQLAlias)sel.getFirstElement();
                IdentifierFactory factory = IdentifierFactory.getInstance();
                ISQLAlias alias = model.createAlias(factory.createIdentifier());
                if (al != null) {
                    try {
                        alias.assignFrom(al);
                    } catch (ValidationException e) {
                    }
                    CreateAliasDlg dlg = new CreateAliasDlg(AliasContainerGroup.this.getShell(), driverModel, 3, alias, model);
                    dlg.open();
                    for (int i = 0; i < model.getElements().length; i++) {
                        // System.out.println(model.getElements()[i]);
                    }
                    tableViewer.refresh();
                    selectFirst();
                }
            }
        };
        deleteAlias = new Action() {
            ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getDeleteAlias());

            @Override
            public String getToolTipText() {
                return Messages.getString("DeleteAlias"); //$NON-NLS-1$
            }

            @Override
            public ImageDescriptor getHoverImageDescriptor() {
                return img;
            }

            @Override
            public ImageDescriptor getImageDescriptor() {
                return img;
            }

            @Override
            public void run() {
                StructuredSelection sel = (StructuredSelection)tableViewer.getSelection();
                ISQLAlias al = (ISQLAlias)sel.getFirstElement();
                if (al != null) {
                    model.removeAlias(al);
                    tableViewer.refresh();
                    selectFirst();
                }
            }
        };

    }

    public AliasContainerGroup( Composite parent,
                                AliasModel m,
                                DriverModel dm ) {
        super(parent, SWT.NULL);
        this.model = m;
        this.driverModel = dm;
        setLayout(new FillLayout());
        Composite myParent = new Composite(this, SWT.NULL);
        GridLayout layout;

        // Define layout.
        layout = new GridLayout();
        layout.marginHeight = layout.marginWidth = layout.horizontalSpacing = layout.verticalSpacing = 0;
        myParent.setLayout(layout);

        layout.marginWidth = 0;
        layout.marginHeight = 0;

        GridData gid;

        // Create a toolbar.
        ToolBarManager toolBarMgr = new ToolBarManager(SWT.FLAT);
        createActions();
        toolBarMgr.add(this.newAlias);
        toolBarMgr.add(this.changeAlias);
        toolBarMgr.add(this.copyAlias);
        toolBarMgr.add(this.deleteAlias);

        toolBarMgr.createControl(myParent);
        gid = new GridData();
        gid.horizontalAlignment = GridData.FILL;
        gid.verticalAlignment = GridData.BEGINNING;
        toolBarMgr.getControl().setLayoutData(gid);
        gid = new GridData();
        gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
        gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;

        tableViewer = new TableViewer(myParent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        tableViewer.getControl().setLayoutData(gid);
        tableViewer.setContentProvider(new AliasContentProvider());
        tableViewer.setLabelProvider(new AliasLabelProvider());
        tableViewer.setInput(model);
        /*tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
        	public void selectionChanged(SelectionChangedEvent event) {
        		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        		Event changeEvent = new Event();
        		changeEvent.type = SWT.Selection;
        		changeEvent.widget = tableViewer.getTable();
        		changeEvent.data = selection.getFirstElement();
        		AliasContainerGroup.this.handleEvent(changeEvent);
        	}});*/
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( DoubleClickEvent event ) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                if (selection != null) {
                    ISQLAlias al = (ISQLAlias)selection.getFirstElement();
                    OpenPasswordConnectDialogAction openDlgAction = new OpenPasswordConnectDialogAction(
                                                                                                        tableViewer.getTable().getShell(),
                                                                                                        al,
                                                                                                        driverModel,
                                                                                                        SQLExplorerPlugin.getDefault().getPreferenceStore(),
                                                                                                        SQLExplorerPlugin.getDefault().getSQLDriverManager());
                    openDlgAction.run();

                }
            }
        });
        selectFirst();
        final Table table = tableViewer.getTable();
        MenuManager menuMgr = new MenuManager("#AliasMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(false);
        menuMgr.add(new Action() {
            @Override
            public String getText() {
                return "Open...";//$NON-NLS-1$
            }

            @Override
            public void run() {

                ISQLAlias al = (ISQLAlias)((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
                OpenPasswordConnectDialogAction openDlgAction = new OpenPasswordConnectDialogAction(
                                                                                                    table.getShell(),
                                                                                                    al,
                                                                                                    driverModel,
                                                                                                    SQLExplorerPlugin.getDefault().getPreferenceStore(),
                                                                                                    SQLExplorerPlugin.getDefault().getSQLDriverManager());
                openDlgAction.run();
            }
        });
        menuMgr.add(newAlias);
        menuMgr.add(changeAlias);
        menuMgr.add(copyAlias);
        Menu aliasContextMenu = menuMgr.createContextMenu(table);
        tableViewer.getTable().setMenu(aliasContextMenu);

        myParent.layout();
        parent.layout();

    }

    public ISQLAlias getSelection() {
        ISQLAlias al = (ISQLAlias)((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
        return al;
    }
}

class AliasContentProvider implements IStructuredContentProvider {

    AliasModel iResource;

    public Object[] getElements( Object input ) {
        return ((AliasModel)input).getElements();
    }

    public void dispose() {
    }

    public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {
    }
}

class AliasLabelProvider implements ILabelProvider {
    AliasLabelProvider() {
    }

    public Image getImage( Object elementx ) {
        return null;
    }

    public String getText( Object element ) {
        ISQLAlias al = (ISQLAlias)element;
        return al.getName();
    }

    public boolean isLabelProperty( Object element,
                                    String property ) {
        return true;
    }

    public void dispose() {
    }

    public void removeListener( ILabelProviderListener listener ) {
    }

    public void addListener( ILabelProviderListener listener ) {
    }

}
