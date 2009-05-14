/*
 * Copyright � 2000-2005 MetaMatrix, Inc.
 * All rights reserved.
 */
package net.sourceforge.sqlexplorer.dbviewer.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.sqlexplorer.dbviewer.DetailManager;
import net.sourceforge.sqlexplorer.dbviewer.details.ProcedureDetail;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @since 4.3
 */
public class ProcedureNode implements IDbModel {

    private IDbModel parent;
    private String txt;
    protected SQLConnection conn;
    IProcedureInfo iProcInfo;
    private ArrayList list = new ArrayList(1);
    Composite detailPanel;
    private List argumentNameList;

    /**
     * @since 4.3
     */
    public ProcedureNode( IDbModel s,
                          String name,
                          SQLConnection conn,
                          IProcedureInfo iProcInfo ) {
        this.parent = s;
        this.txt = name;
        this.conn = conn;
        this.iProcInfo = iProcInfo;
    }

    /**
     * @see net.sourceforge.sqlexplorer.dbviewer.model.IDbModel#getChildren()
     * @since 4.3
     */
    public Object[] getChildren() {
        return list.toArray();
    }

    /**
     * @see net.sourceforge.sqlexplorer.dbviewer.model.IDbModel#getParent()
     * @since 4.3
     */
    public Object getParent() {
        return parent;
    }

    /**
     * @see net.sourceforge.sqlexplorer.dbviewer.model.IDbModel#getComposite(net.sourceforge.sqlexplorer.dbviewer.DetailManager)
     * @since 4.3
     */
    public Composite getComposite( final DetailManager dm ) {

        this.detailPanel = null;

        final Display display = dm.getComposite().getDisplay();
        BusyIndicator.showWhile(display, new Runnable() {

            public void run() {
                if (display.isDisposed()) {
                    return;
                }

                new StringBuffer();
                ResultSetReader reader = null;
                try {
                    if (conn.getTimeClosed() == null) {
                        ResultSet rs = conn.getSQLMetaData().getProcedureColumns(iProcInfo);
                        reader = new ResultSetReader(rs);
                    }
                } catch (SQLException e) {
                    SQLExplorerPlugin.error("Error Retrieving schema children in plugin ", e); //$NON-NLS-1$
                }

                if (reader != null) {

                    detailPanel = new Composite(dm.getComposite(), SWT.NONE);
                    GridLayout layout = new GridLayout();
                    detailPanel.setLayout(layout);
                    GridData gd = new GridData(GridData.FILL_BOTH);
                    detailPanel.setLayoutData(gd);

                    new ProcedureDetail(detailPanel, reader, getThis());

                }
            }
        });

        return detailPanel;

    }

    public void setArgumentNameList( List names ) {
        this.argumentNameList = names;
    }

    public List getArgumentNameList() {
        return this.argumentNameList;
    }

    public IProcedureInfo getProcedureInfo() {
        return this.iProcInfo;
    }

    @Override
    public String toString() {
        return txt;
    }

    ProcedureNode getThis() {
        return this;
    }
}
