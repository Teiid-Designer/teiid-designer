package net.sourceforge.sqlexplorer.dialogs;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.URLUtil;
import net.sourceforge.sqlexplorer.ext.PluginInfo;
import net.sourceforge.sqlexplorer.ext.PluginManager;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class AboutDlg extends Dialog {
    PluginManager pluginManager;

    public AboutDlg( Shell parentShell,
                     PluginManager pluginManager ) {
        super(parentShell);
        this.pluginManager = pluginManager;
    }

    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(Messages.getString("About_1")); //$NON-NLS-1$
    }

    @Override
    protected Control createDialogArea( Composite parent ) {
        // top level composite
        Composite parentComposite = (Composite)super.createDialogArea(parent);

        parentComposite.setLayout(new FillLayout());

        TabFolder tabFolder = new TabFolder(parentComposite, SWT.NULL);

        TabItem tabItem1 = new TabItem(tabFolder, SWT.NULL);
        tabItem1.setText(Messages.getString("About_2")); //$NON-NLS-1$
        tabItem1.setToolTipText(Messages.getString("About_3")); //$NON-NLS-1$

        TabItem tabItem2 = new TabItem(tabFolder, SWT.NULL);
        tabItem2.setText(Messages.getString("AboutDlg.License_1")); //$NON-NLS-1$
        tabItem2.setToolTipText(Messages.getString("AboutDlg.Show_License_2")); //$NON-NLS-1$

        TabItem tabItem3 = new TabItem(tabFolder, SWT.NULL);
        tabItem3.setText(Messages.getString("Credits_4")); //$NON-NLS-1$
        tabItem3.setToolTipText(Messages.getString("Show_Credits_5")); //$NON-NLS-1$

        TabItem tabItem4 = new TabItem(tabFolder, SWT.NULL);
        tabItem4.setText(Messages.getString("System_6")); //$NON-NLS-1$
        tabItem4.setToolTipText(Messages.getString("Show_System_Properties_7")); //$NON-NLS-1$

        TabItem tabItem5 = new TabItem(tabFolder, SWT.NULL);
        tabItem5.setText(Messages.getString("AboutDlg.Plugins_3")); //$NON-NLS-1$
        tabItem5.setToolTipText(Messages.getString("AboutDlg.Show_Plugins_Info_4")); //$NON-NLS-1$

        // AboutItem ai=
        new AboutItem(tabItem1, tabFolder);
        // LicenseItem li=
        new LicenseItem(tabItem2, tabFolder);
        // CreditsItem ci=
        new CreditsItem(tabItem3, tabFolder);
        // SystemProperties sp=
        new SystemProperties(tabItem4, tabFolder);
        // PluginsProperties pp=
        new PluginsProperties(tabItem5, tabFolder, pluginManager);
        return parentComposite;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 400);
    }

    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        // Button button =
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected void setShellStyle( int newShellStyle ) {
        super.setShellStyle(newShellStyle | SWT.RESIZE);// Make the about dialog resizable
    }
}

class AboutItem {
    Image logoImage;

    AboutItem( TabItem item,
               Composite parent ) {

        try {
            logoImage = new Image(parent.getDisplay(), SqlexplorerImages.getLogo().openStream());
        } catch (java.io.IOException e) {

            SQLExplorerPlugin.error("Error Getting the logo image ", e); //$NON-NLS-1$
        }

        parent.addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent event ) {
                if (logoImage != null) {
                    logoImage.dispose();
                }
            }
        });
        Composite cmp = new Composite(parent, SWT.NULL);
        item.setControl(cmp);
        GridLayout lay = new GridLayout();
        lay.numColumns = 1;
        lay.marginWidth = 50;
        lay.marginHeight = 50;
        cmp.setLayout(lay);
        Label lb1 = new Label(cmp, SWT.NULL);
        lb1.setText("SQLExplorer v 2.2.2");//$NON-NLS-1$

        Label lb11 = new Label(cmp, SWT.NULL);
        lb11.setText("https://sqlexplorer.dev.java.net/");//$NON-NLS-1$

        Label lb = new Label(cmp, SWT.NULL);
        lb.setText("(c) Copyright Andrea Mazzolini, 2002-2004");//$NON-NLS-1$

        GridData data = new GridData(GridData.VERTICAL_ALIGN_CENTER | GridData.CENTER);
        lb.setLayoutData(data);
        data = new GridData(GridData.VERTICAL_ALIGN_CENTER | GridData.CENTER);
        lb1.setLayoutData(data);
        data = new GridData(GridData.VERTICAL_ALIGN_CENTER | GridData.CENTER);
        lb11.setLayoutData(data);

        ImageData imgData = logoImage.getImageData();
        int width = imgData.width;
        int height = imgData.height;

        FontRegistry fr = JFaceResources.getFontRegistry();
        FontData[] fData = parent.getFont().getFontData();
        fData[0].setStyle(SWT.BOLD);
        fr.put("MyBoldFont", fData);//$NON-NLS-1$
        lb.setFont(fr.get("MyBoldFont"));//$NON-NLS-1$
        lb1.setFont(fr.get("MyBoldFont"));//$NON-NLS-1$
        lb11.setFont(fr.get("MyBoldFont"));//$NON-NLS-1$

        Label lb2 = new Label(cmp, SWT.NULL);
        lb2.setText("andreamazzolini@users.sourceforge.net");//$NON-NLS-1$
        data = new GridData(GridData.VERTICAL_ALIGN_CENTER | GridData.CENTER);
        lb2.setLayoutData(data);

        final Composite imgComposite = new Composite(cmp, SWT.BORDER);
        data = new GridData(GridData.VERTICAL_ALIGN_CENTER | GridData.CENTER);
        imgComposite.setLayoutData(data);
        data.heightHint = height;
        data.widthHint = width;
        imgComposite.addPaintListener(new PaintListener() {
            public void paintControl( PaintEvent event ) {
                // Rectangle area = imgComposite.getClientArea();
                GC gc = event.gc;
                gc.drawImage(logoImage, 0, 0);
            }
        });

    }
}

class LicenseItem {
    LicenseItem( TabItem item,
                 Composite parent ) {
        Composite cmp = new Composite(parent, SWT.NULL);
        item.setControl(cmp);
        GridLayout lay = new GridLayout();
        lay.numColumns = 1;
        cmp.setLayout(lay);

        StyledText st = new StyledText(cmp, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        st.setEditable(false);
        String separator = System.getProperty("line.separator"); //$NON-NLS-1$

        BufferedReader bbr = null;
        try {

            InputStream is = URLUtil.getResourceURL("license.txt").openStream(); //$NON-NLS-1$
            bbr = new BufferedReader(new InputStreamReader(is));

            String str;
            StringBuffer all = new StringBuffer();
            while ((str = bbr.readLine()) != null) {
                all.append(str);
                all.append(separator);
            }
            st.setText(all.toString());
            is.close();

        } catch (Exception e) {
            st.setText(Messages.getString("AboutDlg.This_software_is_licensed_under_LGPL._2")); //$NON-NLS-1$
            SQLExplorerPlugin.error("Error loading license document", e); //$NON-NLS-1$
        } finally {
            try {
                if (bbr != null) bbr.close();
            } catch (java.io.IOException e) {
            }

        }

        GridData data = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_CENTER | GridData.CENTER);
        st.setLayoutData(data);
    }
}

class CreditsItem {
    CreditsItem( TabItem item,
                 Composite parent ) {
        Composite cmp = new Composite(parent, SWT.NULL);
        item.setControl(cmp);
        GridLayout lay = new GridLayout();
        lay.numColumns = 1;
        cmp.setLayout(lay);

        StyledText st = new StyledText(cmp, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        st.setEditable(false);
        String separator = System.getProperty("line.separator"); //$NON-NLS-1$
        final String credits = "The GUI is based on the Eclipse Project libraries (http://www.eclipse.org)." + separator + //$NON-NLS-1$
                               "The SQL stuff is based on SquirreL SQL (http://squirrel-sql.sourceforge.net)."
                               + separator
                               + separator
                               + 
                               "SQLExplorer uses the following libraries too:"
                               + separator
                               + 
                               "- NanoXML (http://NanoXML.sourceforge.net/) Java XML API"
                               + separator
                               + 
                               "- log4j (http://jakarta.apache.org/log4j) Logging API"
                               + separator
                               + separator
                               + 
                               "Johan Compagner (joco01@users.sourceforge.net), Jouneau Luc (ljouneau@yahoo.com) and Stephen Schaub (sschaub@bju.edu) have given precious helps. "
                               + separator
                               + 
                               "Chris Potter (cjp0tter@users.sourceforge.net) has written the Sybase plugin. " + separator
                               + 
                               "Chris Potter (cjp0tter@users.sourceforge.net) and Joao Reis Belo (joaoreisbelo@yahoo.com) have written the Sql Server plugin. "
                               + separator + 
                               "Thanks to Michele Puopolo (mpuopol@tin.it) who designed the logo."; //$NON-NLS-1$
        st.setText(credits);
        GridData data = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_CENTER | GridData.CENTER);
        st.setLayoutData(data);
    }
}

class SystemProperties {

    class LProvider extends LabelProvider implements ITableLabelProvider {
        public Image getColumnImage( Object arg0,
                                     int arg1 ) {
            return null;
        }

        public String getColumnText( Object element,
                                     int columnIndex ) {
            java.util.Map.Entry cp = (java.util.Map.Entry)element;
            if (columnIndex == 0) return cp.getKey().toString();
            return cp.getValue().toString();
        }
    }

    java.util.Properties props;

    SystemProperties( TabItem itemTab,
                      Composite parent ) {
        props = System.getProperties();

        TableViewer tv = new TableViewer(parent, SWT.NULL);
        tv.setSorter(new MyViewerSorter());
        Table table = tv.getTable();
        TableColumn c1 = new TableColumn(table, SWT.NULL);
        c1.setText(Messages.getString("Property_9")); //$NON-NLS-1$
        TableColumn c2 = new TableColumn(table, SWT.NULL);
        c2.setText(Messages.getString("Value_10")); //$NON-NLS-1$
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        TableLayout tableLayout = new TableLayout();
        for (int i = 0; i < 2; i++)
            tableLayout.addColumnData(new ColumnWeightData(1, 50, true));
        table.setLayout(tableLayout);

        itemTab.setControl(tv.getControl());
        tv.setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements( Object input ) {
                return props.entrySet().toArray();
            }

            public void dispose() {
            }

            public void inputChanged( Viewer viewer,
                                      Object arg1,
                                      Object arg2 ) {
            }
        });
        tv.setLabelProvider(new LProvider());
        tv.setInput(this);
    }

}

class MyViewerSorter extends ViewerSorter {

    public MyViewerSorter() {
        super();
    }

    public MyViewerSorter( Collator collator ) {
        super(collator);
    }

    @Override
    public boolean isSorterProperty( Object element,
                                     String propertyId ) {
        return true;
    }
}

class PluginRow {
    String[] objArr = new String[5];

    PluginRow( PluginInfo pInfo ) {
        objArr[0] = pInfo.getDescriptiveName();
        objArr[1] = pInfo.getInternalName();
        objArr[2] = pInfo.getAuthor();
        objArr[3] = pInfo.getWebSite();
        objArr[4] = pInfo.getVersion();
    }

    public Object getValue( int i ) {
        return objArr[i];
    }

}

class PluginLabelProvider extends LabelProvider implements ITableLabelProvider {

    PluginTableModel cdtm;

    public PluginLabelProvider( PluginTableModel cdtm ) {
        this.cdtm = cdtm;
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(Object, int)
     */
    public Image getColumnImage( Object arg0,
                                 int arg1 ) {
        return null;
    }

    public String getColumnText( Object element,
                                 int columnIndex ) {
        Object obj = cdtm.getValue(element, columnIndex);
        if (obj != null) return obj.toString();
        return ""; //$NON-NLS-1$
    }

}

class PluginTableModel {
    private ArrayList list = new ArrayList();

    public PluginTableModel( PluginInfo[] rs ) {

        if (rs == null) return;
        try {
            for (int i = 0; i < rs.length; i++) {
                list.add(new PluginRow(rs[i]));
            }
        } catch (java.lang.Exception e) {
            SQLExplorerPlugin.error("Error creating plugin table model", e); //$NON-NLS-1$
        }
    }

    public Object[] getElements() {
        return list.toArray();
    }

    public Object getValue( Object element,
                            int i ) {
        PluginRow e = (PluginRow)element;
        return e.getValue(i);
    }

}

class PluginsContentProvider implements IStructuredContentProvider {

    TableViewer m_viewer;

    public Object[] getElements( Object input ) {
        return ((PluginTableModel)input).getElements();
    }

    public void dispose() {
    }

    public void inputChanged( Viewer viewer,
                              Object arg1,
                              Object arg2 ) {
        m_viewer = (TableViewer)viewer;
    }

}

class PluginsProperties {
    PluginManager pluginManager;

    PluginsProperties( TabItem itemTab,
                       Composite parent,
                       PluginManager pluginManager ) {
        this.pluginManager = pluginManager;
        PluginInfo[] pInfo = pluginManager.getPluginInformation();

        TableViewer viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
        itemTab.setControl(viewer.getControl());
        Table table = viewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        TableColumn tc = new TableColumn(table, SWT.NULL);
        tc.setText(Messages.getString("AboutDlg.Name_9")); //$NON-NLS-1$
        tc = new TableColumn(table, SWT.NULL);
        tc.setText(Messages.getString("AboutDlg.Internal_Name_10")); //$NON-NLS-1$
        tc = new TableColumn(table, SWT.NULL);
        tc.setText(Messages.getString("AboutDlg.Author_11")); //$NON-NLS-1$
        tc = new TableColumn(table, SWT.NULL);
        tc.setText(Messages.getString("AboutDlg.Web_Site_12")); //$NON-NLS-1$
        tc = new TableColumn(table, SWT.NULL);
        tc.setText(Messages.getString("AboutDlg.Version_13")); //$NON-NLS-1$

        TableLayout tableLayout = new TableLayout();
        for (int i = 0; i < 5; i++)
            tableLayout.addColumnData(new ColumnWeightData(1, 50, true));
        table.setLayout(tableLayout);
        viewer.setContentProvider(new PluginsContentProvider());
        PluginTableModel cdtm = new PluginTableModel(pInfo);
        PluginLabelProvider cdlp = new PluginLabelProvider(cdtm);
        viewer.setLabelProvider(cdlp);
        viewer.setInput(cdtm);

        // pInfo[0].
    }
}
