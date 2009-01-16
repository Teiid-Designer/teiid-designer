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

import java.util.HashMap;
import java.util.Iterator;
import net.sourceforge.sqlexplorer.DriverModel;
import net.sourceforge.sqlexplorer.IdentifierFactory;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;

public class DriverContainerGroup extends Composite {
    TableViewer tableViewer;
    Action newDriver;
    Action changeDriver;
    Action copyDriver;
    Action deleteDriver;

    private void createActions() {
        newDriver = new Action() {
            ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getCreateDriverIcon());

            @Override
            public String getToolTipText() {
                return Messages.getString("Create_new_Driver_6"); //$NON-NLS-1$
            }

            @Override
            public String getText() {
                return Messages.getString("Create_new_Driver_6"); //$NON-NLS-1$
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
                final IdentifierFactory factory = IdentifierFactory.getInstance();
                final ISQLDriver driver = driverModel.createDriver(factory.createIdentifier());

                CreateDriverDlg dlg = new CreateDriverDlg(DriverContainerGroup.this.getShell(), driverModel, 1, driver);
                dlg.open();

                tableViewer.refresh();
                selectFirst();
            }
        };
        changeDriver = new Action() {
            ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getEditDriver());

            @Override
            public String getToolTipText() {
                return Messages.getString("Change_the_selected_Driver_8"); //$NON-NLS-1$
            }

            @Override
            public String getText() {
                return Messages.getString("Change_the_selected_Driver_8"); //$NON-NLS-1$
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
                ISQLDriver dv = (ISQLDriver)sel.getFirstElement();
                if (dv != null) {
                    CreateDriverDlg dlg = new CreateDriverDlg(DriverContainerGroup.this.getShell(), driverModel, 2, dv);
                    dlg.open();
                    tableViewer.refresh();
                    selectFirst();
                }
            }

        };
        copyDriver = new Action() {
            ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getCopyDriver());

            @Override
            public String getToolTipText() {
                return Messages.getString("Copy_the_selected_Driver_10"); //$NON-NLS-1$
            }

            @Override
            public String getText() {
                return Messages.getString("Copy_the_selected_Driver_10"); //$NON-NLS-1$
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
                ISQLDriver dv = (ISQLDriver)sel.getFirstElement();
                if (dv != null) {
                    CreateDriverDlg dlg = new CreateDriverDlg(DriverContainerGroup.this.getShell(), driverModel, 3, dv);
                    dlg.open();
                    tableViewer.refresh();
                }
            }
        };
        deleteDriver = new Action() {
            ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getDeleteDriver());

            @Override
            public String getToolTipText() {
                return Messages.getString("Delete_the_selected_Driver_12"); //$NON-NLS-1$
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
                ISQLDriver dv = (ISQLDriver)sel.getFirstElement();
                if (dv != null) {
                    driverModel.removeDriver(dv);
                    tableViewer.refresh();
                    selectFirst();
                }
            }
        };
    }

    DriverModel driverModel;

    public DriverContainerGroup( Composite parent,
                                 DriverModel dm ) {
        super(parent, SWT.NULL);
        driverModel = dm;

        setLayout(new FillLayout());
        Composite myParent = new Composite(this, SWT.NULL);
        GridLayout layout;

        // Define layout.
        layout = new GridLayout();
        layout.marginHeight = layout.marginWidth = layout.horizontalSpacing = layout.verticalSpacing = 0;
        myParent.setLayout(layout);

        layout.marginWidth = 0;
        layout.marginHeight = 0;

        ToolBarManager toolBarMgr = new ToolBarManager(SWT.FLAT);
        createActions();
        toolBarMgr.add(this.newDriver);
        toolBarMgr.add(this.changeDriver);
        toolBarMgr.add(this.copyDriver);
        toolBarMgr.add(this.deleteDriver);

        toolBarMgr.createControl(myParent);
        GridData gid = new GridData();
        gid.horizontalAlignment = GridData.FILL;
        gid.verticalAlignment = GridData.BEGINNING;
        toolBarMgr.getControl().setLayoutData(gid);
        gid = new GridData();
        gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
        gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;

        tableViewer = new TableViewer(myParent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        tableViewer.getControl().setLayoutData(gid);
        tableViewer.setContentProvider(new DriverContentProvider());
        final DriverLabelProvider dlp = new DriverLabelProvider();
        tableViewer.setLabelProvider(dlp);
        tableViewer.getTable().addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent e ) {
                dlp.dispose();

            }
        });
        tableViewer.setInput(driverModel);
        selectFirst();
        final Table table = tableViewer.getTable();
        MenuManager menuMgr = new MenuManager("#DriverMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(false);

        menuMgr.add(newDriver);
        menuMgr.add(changeDriver);
        menuMgr.add(copyDriver);
        Menu driverContextMenu = menuMgr.createContextMenu(table);
        tableViewer.getTable().setMenu(driverContextMenu);
        myParent.layout();
        parent.layout();
    }

    void selectFirst() {
        if (driverModel.getElements().length > 0) {
            Object obj = (driverModel.getElements())[0];
            StructuredSelection sel = new StructuredSelection(obj);
            tableViewer.setSelection(sel);
        }
    }

}

class DriverContentProvider implements IStructuredContentProvider {

    DriverModel iResource;

    public Object[] getElements( Object input ) {
        return ((DriverModel)input).getElements();
    }

    public void dispose() {
    }

    public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {
    }
}

class DriverLabelProvider extends LabelProvider implements ITableLabelProvider {
    private HashMap imageCache = new HashMap(11);

    DriverLabelProvider() {
    }

    ImageDescriptor okDescriptor = ImageDescriptor.createFromURL(SqlexplorerImages.getOkDriver());
    ImageDescriptor errDescriptor = ImageDescriptor.createFromURL(SqlexplorerImages.getErrorDriver());

    public Image getColumnImage( Object element,
                                 int i ) {
        ISQLDriver dv = (ISQLDriver)element;
        ImageDescriptor descriptor = null;
        if (dv.isJDBCDriverClassLoaded() == true) descriptor = okDescriptor;
        else descriptor = errDescriptor;
        Image image = (Image)imageCache.get(descriptor);
        if (image == null) {
            image = descriptor.createImage();
            imageCache.put(descriptor, image);
        }
        return image;
    }

    public String getColumnText( Object element,
                                 int i ) {
        ISQLDriver dv = (ISQLDriver)element;
        return dv.getName();
    }

    @Override
    public boolean isLabelProperty( Object element,
                                    String property ) {
        return true;
    }

    @Override
    public void dispose() {
        for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
            ((Image)i.next()).dispose();
        }
        imageCache.clear();
    }

    @Override
    public void removeListener( ILabelProviderListener listener ) {
    }

    @Override
    public void addListener( ILabelProviderListener listener ) {
    }

}
