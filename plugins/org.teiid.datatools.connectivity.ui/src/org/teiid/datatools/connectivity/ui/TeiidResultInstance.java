package org.teiid.datatools.connectivity.ui;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.datatools.sqltools.result.IResultSetObject;
import org.eclipse.datatools.sqltools.result.OperationCommand;
import org.eclipse.datatools.sqltools.result.ResultConfiguration;
import org.eclipse.datatools.sqltools.result.ResultsViewPlugin;
import org.eclipse.datatools.sqltools.result.internal.core.IResultManager;
import org.eclipse.datatools.sqltools.result.internal.model.ResultInstance;
import org.eclipse.datatools.sqltools.result.model.IResultInstance;
import org.eclipse.datatools.sqltools.result.model.ResultItem;
import org.teiid.datatools.results.view.TeiidResultSetObject;

public class TeiidResultInstance extends ResultInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3939052827604344319L;

	public TeiidResultInstance(IResultManager resultmanager,
			OperationCommand command, Runnable terminateHandler,
			IResultInstance parentResult) {
		super(resultmanager, command, terminateHandler, parentResult);
		// TODO Auto-generated constructor stub
	}

	public void moreResultSet(ResultSet resultset) throws SQLException {
		IResultSetObject r = null;
		try {
			r = createTeiidResultSetObject(resultset);
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			ResultsViewPlugin.getLogger(null).error("ResultInstance_error_moreResultSet", e); //$NON-NLS-1$
			return;
		}
		moreResultItem(new ResultItem(r));
	}

	private IResultSetObject createTeiidResultSetObject(ResultSet resultset)
			throws SQLException {
		return new TeiidResultSetObject(resultset, ResultConfiguration
				.getInstance().getMaxRowCount(), ResultConfiguration
				.getInstance().getMaxDisplayRowCount(), ResultConfiguration
				.getInstance().isShowLabel());
	}
}
