package org.teiid.datatools.connectivity.ui;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.datatools.sqltools.core.DatabaseIdentifier;
import org.eclipse.datatools.sqltools.core.profile.NoSuchProfileException;
import org.eclipse.datatools.sqltools.editor.core.connection.IConnectionTracker;
import org.eclipse.datatools.sqltools.routineeditor.result.CallableSQLResultRunnable;
import org.eclipse.debug.core.ILaunchConfiguration;

public class TeiidCallableSQLResultRunnable extends CallableSQLResultRunnable {

	public TeiidCallableSQLResultRunnable(Connection con,
			ILaunchConfiguration configuration, boolean closeCon,
			IConnectionTracker tracker, DatabaseIdentifier databaseIdentifier) throws CoreException, SQLException, NoSuchProfileException {
		super(con, configuration, closeCon, tracker, databaseIdentifier);
	}

}
