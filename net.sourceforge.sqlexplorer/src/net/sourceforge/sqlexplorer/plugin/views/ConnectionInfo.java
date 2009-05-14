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
package net.sourceforge.sqlexplorer.plugin.views;


import java.sql.DatabaseMetaData;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.model.ISessionTreeClosedListener;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Mazzolini
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ConnectionInfo extends ViewPart {

	Table table;
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
    public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		table=new Table(parent,SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL|SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		TableColumn tc=new TableColumn(table,SWT.NULL);
		tc.setText(Messages.getString("Property_2")); //$NON-NLS-1$
		tc=new TableColumn(table,SWT.NULL);
		tc.setText(Messages.getString("Value_3")); //$NON-NLS-1$
		TableLayout tableLayout=new TableLayout();
		for(int i=0;i<2;i++)
			tableLayout.addColumnData(new ColumnWeightData(1, 50, true));
		table.setLayout(tableLayout);
		table.layout();

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	@Override
    public void setFocus() {
	}
	public void setInput(SessionTreeNode node) {
		if(table==null|| table.isDisposed()){
			return;
		}
		table.removeAll();
		if(node==null){
			return;
		}
			
		SQLConnection conn=node.getSQLConnection();
		node.addListener(new ISessionTreeClosedListener(){

			public void sessionTreeClosed() {
				table.removeAll();
				//getTreeViewer().setInput(null);
			}
		});
		
		TableItem ti=new TableItem(table,SWT.NULL);
		ti.setText(0,Messages.getString("Database_Product_Name_4")); //$NON-NLS-1$
		SQLDatabaseMetaData metaData=null;
		DatabaseMetaData metaData_=null;
		try{
			metaData=conn.getSQLMetaData();
			metaData_=conn.getSQLMetaData().getJDBCMetaData();
		}
		catch(Throwable e){
			MessageDialog.openInformation(null,"No metadata","");
			SQLExplorerPlugin.error("Error getting database metadata ",e); //$NON-NLS-1$
			return;
		}
		try{
			ti.setText(1,metaData.getDatabaseProductName());
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error getting database product name ",e); //$NON-NLS-1$
		}

		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,Messages.getString("Database_Product_Version_5")); //$NON-NLS-1$
		try{
			ti.setText(1,metaData.getDatabaseProductVersion());
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error getting database product version ",e); //$NON-NLS-1$
		}


		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,Messages.getString("Driver_Major_Version_6")); //$NON-NLS-1$
		try{
			ti.setText(1,""+metaData_.getDriverMajorVersion()); //$NON-NLS-1$
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error getting driver major version ",e); //$NON-NLS-1$
		}

		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,Messages.getString("Driver_Minor_Version_8")); //$NON-NLS-1$
		try{
			ti.setText(1,""+metaData_.getDriverMinorVersion()); //$NON-NLS-1$
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error getting driver minor version ",e); //$NON-NLS-1$
		}

		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,Messages.getString("Driver_Name_10")); //$NON-NLS-1$
		try{
			ti.setText(1,""+metaData.getDriverName()); //$NON-NLS-1$
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error getting driver name ",e); //$NON-NLS-1$
		}

		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,Messages.getString("Driver_Version_12")); //$NON-NLS-1$
		try{
			ti.setText(1,""+metaData_.getDriverVersion()); //$NON-NLS-1$
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error getting driver version ",e); //$NON-NLS-1$
		}

		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,Messages.getString("User_Name_14")); //$NON-NLS-1$
		try{
			ti.setText(1,""+metaData.getUserName()); //$NON-NLS-1$
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error getting user name ",e); //$NON-NLS-1$
		}

		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,Messages.getString("URL_16")); //$NON-NLS-1$
		try{
			ti.setText(1,""+metaData_.getURL()); //$NON-NLS-1$
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error getting database URL ",e); //$NON-NLS-1$
		}

		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,Messages.getString("SessionTreeNode.AutoCommit_Mode_1"));  //$NON-NLS-1$
		try{
			ti.setText(1,""+metaData_.getConnection().getAutoCommit()); //$NON-NLS-1$
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error getting database autocommit mode ",e); //$NON-NLS-1$
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"All Procedures Are Callable" );
		try{
			ti.setText(1,""+metaData_.allProceduresAreCallable()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"All Tables Are Selectable" );
		try{
			ti.setText(1,""+metaData_.allTablesAreSelectable()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Nulls are sorted High" );
		try{
			ti.setText(1,""+metaData_.nullsAreSortedHigh()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Nulls are sorted Low" );
		try{
			ti.setText(1,""+metaData_.nullsAreSortedLow()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Nulls are sorted at Start" );
		try{
			ti.setText(1,""+metaData_.nullsAreSortedAtStart()); //$NON-NLS-1$
		}catch(Throwable e){
		}

		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Nulls are sorted at End" );
		try{
			ti.setText(1,""+metaData_.nullsAreSortedAtEnd()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Is Read Only" );
		try{
			ti.setText(1,""+metaData_.isReadOnly()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Result Set Holdability" );
		try{
			ti.setText(1,""+metaData_.getResultSetHoldability()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Uses Local Files" );
		try{
			ti.setText(1,""+metaData_.usesLocalFiles()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Uses Local File per Table" );
		try{
			ti.setText(1,""+metaData_.usesLocalFilePerTable()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Mixed Case Identifiers" );
		try{
			ti.setText(1,""+metaData_.supportsMixedCaseIdentifiers()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Stores Upper Case Identifiers" );
		try{
			ti.setText(1,""+metaData_.storesUpperCaseIdentifiers()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Stores Lower Case Identifiers" );
		try{
			ti.setText(1,""+metaData_.storesLowerCaseIdentifiers()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Stores Mixed Case Identifiers" );
		try{
			ti.setText(1,""+metaData_.storesMixedCaseIdentifiers()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Mixed Case Quoted Identifiers" );
		try{
			ti.setText(1,""+metaData_.supportsMixedCaseQuotedIdentifiers()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Stores Upper Case Quoted Identifiers" );
		try{
			ti.setText(1,""+metaData_.storesUpperCaseQuotedIdentifiers()); //$NON-NLS-1$
		}catch(Throwable e){
		}

		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Stores Lower Case Quoted Identifiers" );
		try{
			ti.setText(1,""+metaData_.storesLowerCaseQuotedIdentifiers()); //$NON-NLS-1$
		}catch(Throwable e){
		}

		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Stores Mixed Case Quoted Identifiers" );
		try{
			ti.setText(1,""+metaData_.storesMixedCaseQuotedIdentifiers()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Identifier Quote" );
		try{
			ti.setText(1,""+metaData_.getIdentifierQuoteString()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Search String Escape" );
		try{
			ti.setText(1,""+metaData_.getSearchStringEscape()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Extra Name Characters" );
		try{
			ti.setText(1,""+metaData_.getExtraNameCharacters()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Alter Table With Add Column" );
		try{
			ti.setText(1,""+metaData_.supportsAlterTableWithAddColumn()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Alter Table With Drop Column" );
		try{
			ti.setText(1,""+metaData_.supportsAlterTableWithDropColumn()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Column Aliasing" );
		try{
			ti.setText(1,""+metaData_.supportsColumnAliasing()); //$NON-NLS-1$
		}catch(Throwable e){
		}		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Null Plus Non Null Is Null" );
		try{
			ti.setText(1,""+metaData_.nullPlusNonNullIsNull()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Convert" );
		try{
			ti.setText(1,""+metaData_.supportsConvert()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Table Correlation Names" );
		try{
			ti.setText(1,""+metaData_.supportsTableCorrelationNames()); //$NON-NLS-1$
		}catch(Throwable e){
		}	
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Different Table Correlation Names" );
		try{
			ti.setText(1,""+metaData_.supportsDifferentTableCorrelationNames()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Expressions in Order By" );
		try{
			ti.setText(1,""+metaData_.supportsExpressionsInOrderBy()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Order By Unrelated" );
		try{
			ti.setText(1,""+metaData_.supportsOrderByUnrelated()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Group By" );
		try{
			ti.setText(1,""+metaData_.supportsGroupBy()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Group By Unrelated" );
		try{
			ti.setText(1,""+metaData_.supportsGroupByUnrelated()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Group By Beyond Select" );
		try{
			ti.setText(1,""+metaData_.supportsGroupByBeyondSelect()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Like Escape Clause" );
		try{
			ti.setText(1,""+metaData_.supportsLikeEscapeClause()); //$NON-NLS-1$
		}catch(Throwable e){
		}	
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Multiple Result Sets" );
		try{
			ti.setText(1,""+metaData_.supportsMultipleResultSets()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Multiple Open Results " );
		try{
			ti.setText(1,""+metaData_.supportsMultipleOpenResults()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Multiple Transactions" );
		try{
			ti.setText(1,""+metaData_.supportsMultipleTransactions()); //$NON-NLS-1$
		}catch(Throwable e){
		}	
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Non Nullable Columns" );
		try{
			ti.setText(1,""+metaData_.supportsNonNullableColumns()); //$NON-NLS-1$
		}catch(Throwable e){
		}	
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Minimum SQL Grammar" );
		try{
			ti.setText(1,""+metaData_.supportsMinimumSQLGrammar()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Core SQL Grammar" );
		try{
			ti.setText(1,""+metaData_.supportsCoreSQLGrammar()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Extended SQL Grammar" );
		try{
			ti.setText(1,""+metaData_.supportsExtendedSQLGrammar()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports ANSI92 Entry Level SQL" );
		try{
			ti.setText(1,""+metaData_.supportsANSI92EntryLevelSQL()); //$NON-NLS-1$
		}catch(Throwable e){
		}	
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports ANSI92 Intermediate SQL" );
		try{
			ti.setText(1,""+metaData_.supportsANSI92IntermediateSQL()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports ANSI92 Full SQL" );
		try{
			ti.setText(1,""+metaData_.supportsANSI92FullSQL()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Integrity Enhancement Facility" );
		try{
			ti.setText(1,""+metaData_.supportsIntegrityEnhancementFacility()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Outer Joins" );
		try{
			ti.setText(1,""+metaData_.supportsOuterJoins()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Full Outer Joins" );
		try{
			ti.setText(1,""+metaData_.supportsFullOuterJoins()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Limited Outer Joins" );
		try{
			ti.setText(1,""+metaData_.supportsLimitedOuterJoins()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Schema Term" );
		try{
			ti.setText(1,""+metaData_.getSchemaTerm()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Procedure Term" );
		try{
			ti.setText(1,""+metaData_.getProcedureTerm()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Catalog Term" );
		try{
			ti.setText(1,""+metaData_.getCatalogTerm()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Is Catalog at Start" );
		try{
			ti.setText(1,""+metaData_.isCatalogAtStart()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Catalog Separator" );
		try{
			ti.setText(1,""+metaData_.getCatalogSeparator()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Schemas In Data Manipulation" );
		try{
			ti.setText(1,""+metaData_.supportsSchemasInDataManipulation()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Schemas In Procedure Calls" );
		try{
			ti.setText(1,""+metaData_.supportsSchemasInProcedureCalls()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Schemas In Table Definitions" );
		try{
			ti.setText(1,""+metaData_.supportsSchemasInTableDefinitions()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Schemas In Index Definitions" );
		try{
			ti.setText(1,""+metaData_.supportsSchemasInIndexDefinitions()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Schemas In Privilege Definitions" );
		try{
			ti.setText(1,""+metaData_.supportsSchemasInPrivilegeDefinitions()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Catalogs In Data Manipulation" );
		try{
			ti.setText(1,""+metaData_.supportsCatalogsInDataManipulation()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Catalogs In Procedure Calls" );
		try{
			ti.setText(1,""+metaData_.supportsCatalogsInProcedureCalls()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Catalogs In Table Definitions" );
		try{
			ti.setText(1,""+metaData_.supportsCatalogsInTableDefinitions()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Catalogs In Index Definitions" );
		try{
			ti.setText(1,""+metaData_.supportsCatalogsInIndexDefinitions()); //$NON-NLS-1$
		}catch(Throwable e){
		}

		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Catalogs In Privilege Definitions" );
		try{
			ti.setText(1,""+metaData_.supportsCatalogsInPrivilegeDefinitions()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Positioned Delete" );
		try{
			ti.setText(1,""+metaData_.supportsPositionedDelete()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Positioned Update" );
		try{
			ti.setText(1,""+metaData_.supportsPositionedUpdate()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Stored Procedures" );
		try{
			ti.setText(1,""+metaData_.supportsStoredProcedures()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Subqueries In Comparisons" );
		try{
			ti.setText(1,""+metaData_.supportsSubqueriesInComparisons()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Subqueries In Exists" );
		try{
			ti.setText(1,""+metaData_.supportsSubqueriesInExists()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Subqueries in IN Statements" );
		try{
			ti.setText(1,""+metaData_.supportsSubqueriesInIns()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Subqueries in Quantified Expressions" );
		try{
			ti.setText(1,""+metaData_.supportsSubqueriesInQuantifieds()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Correlated Subqueries" );
		try{
			ti.setText(1,""+metaData_.supportsCorrelatedSubqueries()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Union" );
		try{
			ti.setText(1,""+metaData_.supportsUnion()); //$NON-NLS-1$
		}catch(Throwable e){
		}		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Union All" );
		try{
			ti.setText(1,""+metaData_.supportsUnionAll()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Open Cursors Across Commit" );
		try{
			ti.setText(1,""+metaData_.supportsOpenCursorsAcrossCommit()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Open Cursors Across Rollback" );
		try{
			ti.setText(1,""+metaData_.supportsOpenCursorsAcrossRollback()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Open Statements Across Commit" );
		try{
			ti.setText(1,""+metaData_.supportsOpenStatementsAcrossCommit()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Open Statements Across Rollback" );
		try{
			ti.setText(1,""+metaData_.supportsOpenStatementsAcrossRollback()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Binary Literal Length" );
		try{
			ti.setText(1,""+metaData_.getMaxBinaryLiteralLength()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Char Literal Length" );
		try{
			ti.setText(1,""+metaData_.getMaxCharLiteralLength()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Column Name Length" );
		try{
			ti.setText(1,""+metaData_.getMaxColumnNameLength()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Columns In Group By" );
		try{
			ti.setText(1,""+metaData_.getMaxColumnsInGroupBy()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Columns In Index" );
		try{
			ti.setText(1,""+metaData_.getMaxColumnsInIndex()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Columns In Order By" );
		try{
			ti.setText(1,""+metaData_.getMaxColumnsInOrderBy()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Columns In Select" );
		try{
			ti.setText(1,""+metaData_.getMaxColumnsInSelect()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Columns In Table" );
		try{
			ti.setText(1,""+metaData_.getMaxColumnsInTable()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Connections" );
		try{
			ti.setText(1,""+metaData_.getMaxConnections()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Cursor Name Length" );
		try{
			ti.setText(1,""+metaData_.getMaxCursorNameLength()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Index Length" );
		try{
			ti.setText(1,""+metaData_.getMaxIndexLength()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Schema Name Length" );
		try{
			ti.setText(1,""+metaData_.getMaxSchemaNameLength()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Procedure Name Length" );
		try{
			ti.setText(1,""+metaData_.getMaxProcedureNameLength()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Catalog Name Length" );
		try{
			ti.setText(1,""+metaData_.getMaxCatalogNameLength()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Row Size" );
		try{
			ti.setText(1,""+metaData_.getMaxRowSize()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Row Size Include Blobs" );
		try{
			ti.setText(1,""+metaData_.doesMaxRowSizeIncludeBlobs()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Statement Length" );
		try{
			ti.setText(1,""+metaData_.getMaxStatementLength()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Statements" );
		try{
			ti.setText(1,""+metaData_.getMaxStatements()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Table Name Length" );
		try{
			ti.setText(1,""+metaData_.getMaxTableNameLength()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max Tables In Select" );
		try{
			ti.setText(1,""+metaData_.getMaxTablesInSelect()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Max User Name Length" );
		try{
			ti.setText(1,""+metaData_.getMaxUserNameLength()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Default Transaction Isolation" );
		try{
			int isol=metaData_.getDefaultTransactionIsolation();
			String is=null;
			switch (isol){
				case java.sql.Connection.TRANSACTION_NONE :
					{
						is = "TRANSACTION_NONE";
						break;
					}
				case java.sql.Connection.TRANSACTION_READ_COMMITTED :
					{
						is = "TRANSACTION_READ_COMMITTED";
						break;
					}
				case java.sql.Connection.TRANSACTION_READ_UNCOMMITTED :
					{
						is = "TRANSACTION_READ_UNCOMMITTED";
						break;
					}
				case java.sql.Connection.TRANSACTION_REPEATABLE_READ :
					{
						is = "TRANSACTION_REPEATABLE_READ";
						break;
					}
				case java.sql.Connection.TRANSACTION_SERIALIZABLE :
					{
						is = "TRANSACTION_SERIALIZABLE";
						break;
					}
				default :
				{
					is = "";
					break;
				}
			}
			ti.setText(1,is); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Transactions" );
		try{
			ti.setText(1,""+metaData_.supportsTransactions()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Data Definition and Data Manipulation Transactions" );
		try{
			ti.setText(1,""+metaData_.supportsDataDefinitionAndDataManipulationTransactions()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Data Manipulation Transactions Only" );
		try{
			ti.setText(1,""+metaData_.supportsDataManipulationTransactionsOnly()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Data Definition Causes Transaction Commit" );
		try{
			ti.setText(1,""+metaData_.dataDefinitionCausesTransactionCommit()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Data Definition Ignored in Transactions" );
		try{
			ti.setText(1,""+metaData_.dataDefinitionIgnoredInTransactions()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Batch Updates" );
		try{
			ti.setText(1,""+metaData_.supportsBatchUpdates()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Savepoints" );
		try{
			ti.setText(1,""+metaData_.supportsSavepoints()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Named Parameters" );
		try{
			ti.setText(1,""+metaData_.supportsNamedParameters()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Get Generated Keys" );
		try{
			ti.setText(1,""+metaData_.supportsGetGeneratedKeys()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Database Major Version" );
		try{
			ti.setText(1,""+metaData_.getDatabaseMajorVersion()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Database Minor Version" );
		try{
			ti.setText(1,""+metaData_.getDatabaseMinorVersion()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"JDBC Minor Version" );
		try{
			ti.setText(1,""+metaData_.getJDBCMinorVersion()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"JDBC Major Version" );
		try{
			ti.setText(1,""+metaData_.getJDBCMajorVersion()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"SQL State Type" );
		try{
			ti.setText(1,""+metaData_.getSQLStateType()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Locators Update Copy" );
		try{
			ti.setText(1,""+metaData_.locatorsUpdateCopy()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		ti=new TableItem(table,SWT.NULL);
		ti.setText(0,"Supports Statement Pooling" );
		try{
			ti.setText(1,""+metaData_.supportsStatementPooling()); //$NON-NLS-1$
		}catch(Throwable e){
		}
		
		table.redraw();
		//PluginManager pm=JFaceDbcPlugin.getDefault().pluginManager;
		//DatabaseModel dbModel=node.dbModel;
		//tv.setInput(dbModel);
	
	}

}
