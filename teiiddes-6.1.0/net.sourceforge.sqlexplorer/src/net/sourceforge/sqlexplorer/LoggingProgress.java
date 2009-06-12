package net.sourceforge.sqlexplorer;

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

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class LoggingProgress implements IRunnableWithProgress {

    /**
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(IProgressMonitor)
     */
    SQLDriverManager _driverMgr;
    ISQLDriver driver;
    ISQLAlias alias;
    String user;
    String pswd;
    String error;
    SQLConnection conn;
    Throwable th;

    public LoggingProgress( SQLDriverManager dm,
                            ISQLDriver dv,
                            ISQLAlias al,
                            String user,
                            String pswd ) {

        _driverMgr = dm;
        driver = dv;
        alias = al;
        this.user = user;
        this.pswd = pswd;
    }

    public void run( IProgressMonitor monitor ) {
        monitor.setTaskName(Messages.getString("Logging_to_database..._1")); //$NON-NLS-1$
        monitor.beginTask(Messages.getString("Logging_to_database..._1"), IProgressMonitor.UNKNOWN);//$NON-NLS-1$
        try {
            conn = _driverMgr.getConnection(driver, alias, user, pswd);
            monitor.done();
        } catch (Throwable e) {
            th = e;
            error = e.getMessage();
            SQLExplorerPlugin.error(error, e); 
        }
    }

    public SQLConnection getConn() {
        return conn;
    }

    public String getError() {
        return error;
    }

    public boolean isOk() {
        return ((th == null) ? true : false);
    }

    public Throwable getException() {
        return th;
    }

}
