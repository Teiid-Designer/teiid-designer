package org.teiid.datatools.results.view;

import java.util.Iterator;

import org.eclipse.datatools.sqltools.result.IResultSetObject;
import org.eclipse.datatools.sqltools.result.IResultSetRow;

public class TeiidResultSetObject implements IResultSetObject {

	private IResultSetObject result;

	public TeiidResultSetObject(IResultSetObject result) {
		this.result = result;
	}

	@Override
	public int getColumnCount() {
		return 0;
	}

	@Override
	public String[] getColumnNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnName(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getColumnDisplaySizes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColumnDisplaySize(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] getColumnSQLTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColumnSQLType(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IResultSetRow getRowData(int row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator getAllRecords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator getDisplayRecords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAllResultLoaded() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
