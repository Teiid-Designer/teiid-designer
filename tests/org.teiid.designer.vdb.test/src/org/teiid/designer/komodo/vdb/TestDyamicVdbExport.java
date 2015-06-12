/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

import static org.mockito.Mockito.mock;
import java.io.File;
import java.io.FileWriter;
import org.eclipse.core.resources.IFile;
import org.junit.Test;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.util.SmartTestDesignerSuite;
import org.teiid.designer.vdb.VdbSource;
import org.teiid.designer.vdb.dynamic.DynamicVdb;

@SuppressWarnings( "javadoc" )
public class TestDyamicVdbExport implements StringConstants {

    private static final String DYNAMIC_VDBS = "dynamic_vdbs";

	private File portfolioVdb = SmartTestDesignerSuite.getTestDataFile(getClass(), DYNAMIC_VDBS + File.separator + "portfolio-vdb.xml");

	private DynamicVdb setupPortfolioExample() throws Exception {
	    IFile vdbFile = mock(IFile.class);
        DynamicVdb vdb = new DynamicVdb(vdbFile);
        vdb.setName("Portfolio");
        vdb.setVersion(1);
        vdb.setDescription("The Portfolio Dynamic VDB");

        vdb.setProperty("UseConnectorMetadata", "true");

        DynamicModel marketData = new DynamicModel("MarketData");
        marketData.addSource(new VdbSource(vdb, "text-connector", "java:/marketdata-file" , "file"));
        vdb.addDynamicModel(marketData);

        DynamicModel accounts = new DynamicModel("Accounts");
        accounts.setProperty("importer.useFullSchemaName", "false");
        accounts.addSource(new VdbSource(vdb, "h2-connector", "java:/accounts-ds" , "h2"));

        DynamicModel valuations = new DynamicModel("PersonalValuations");
        valuations.setProperty("importer.headerRowNumber", "1");
        valuations.setProperty("importer.ExcelFileName", "otherholdings.xls");
        valuations.addSource(new VdbSource(vdb, "excelconnector", "java:/excel-file" , "excel"));
        String metadataText = EMPTY_STRING +
        "SET NAMESPACE 'http://www.teiid.org/translator/excel/2014' AS teiid_excel;" + NEW_LINE +
        NEW_LINE +
        "CREATE FOREIGN TABLE Sheet1 (" + NEW_LINE +
        "ROW_ID integer OPTIONS (SEARCHABLE 'All_Except_Like', \"teiid_excel:CELL_NUMBER\" 'ROW_ID')," + NEW_LINE +
        "ACCOUNT_ID integer OPTIONS (SEARCHABLE 'Unsearchable', \"teiid_excel:CELL_NUMBER\" '1')," + NEW_LINE +
        "PRODUCT_TYPE string OPTIONS (SEARCHABLE 'Unsearchable', \"teiid_excel:CELL_NUMBER\" '2')," + NEW_LINE +
        "PRODUCT_VALUE string OPTIONS (SEARCHABLE 'Unsearchable', \"teiid_excel:CELL_NUMBER\" '3')," + NEW_LINE +
        "CONSTRAINT PK0 PRIMARY KEY(ROW_ID)" + NEW_LINE +
        ") OPTIONS (\"teiid_excel:FILE\" 'otherholdings.xls', \"teiid_excel:FIRST_DATA_ROW_NUMBER\" '2');";
        valuations.setMetadata(new Metadata(metadataText, Metadata.Type.DDL.toString()));

        DynamicModel stocks = new DynamicModel("Stocks");
        stocks.setModelType(DynamicModel.Type.VIRTUAL);
        metadataText = EMPTY_STRING +
        "CREATE VIEW StockPrices (" + NEW_LINE +
        "symbol string," + NEW_LINE +
        "price bigdecimal" + NEW_LINE +
        ")" + NEW_LINE +
        "AS" + NEW_LINE +
        "SELECT SP.symbol, SP.price" + NEW_LINE +
        "FROM (EXEC MarketData.getTextFiles('*.txt')) AS f," + NEW_LINE +
        "TEXTTABLE(f.file COLUMNS symbol string, price bigdecimal HEADER) AS SP;" + NEW_LINE +
        NEW_LINE +
        NEW_LINE +
        "CREATE VIEW Stock (" + NEW_LINE +
        "product_id integer," + NEW_LINE +
        "symbol string," + NEW_LINE +
        "price bigdecimal," + NEW_LINE +
        "company_name   varchar(256)" + NEW_LINE +
        ")" + NEW_LINE +
        "AS" + NEW_LINE +
        "SELECT  A.ID, S.symbol, S.price, A.COMPANY_NAME" + NEW_LINE +
        "FROM StockPrices AS S, Accounts.PRODUCT AS A" + NEW_LINE +
        "WHERE S.symbol = A.SYMBOL;";
        stocks.setMetadata(new Metadata(metadataText, Metadata.Type.DDL.toString()));

        DynamicModel stocksMatModel = new DynamicModel("StocksMatModel");
        stocksMatModel.setModelType(DynamicModel.Type.VIRTUAL);
        metadataText = EMPTY_STRING +
        "CREATE view stockPricesMatView" + NEW_LINE +
        "(" + NEW_LINE +
        "product_id integer," + NEW_LINE +
        "symbol string," + NEW_LINE +
        "price bigdecimal," + NEW_LINE +
        "company_name   varchar(256)" + NEW_LINE +
        ") OPTIONS (MATERIALIZED 'TRUE', UPDATABLE 'TRUE'," + NEW_LINE +
        "MATERIALIZED_TABLE 'Accounts.h2_stock_mat'," + NEW_LINE +
        "\"teiid_rel:MATVIEW_TTL\" 120000," + NEW_LINE +
        "\"teiid_rel:MATVIEW_BEFORE_LOAD_SCRIPT\" 'execute accounts.native(''truncate table h2_stock_mat'');'," + NEW_LINE +
        "\"teiid_rel:MATVIEW_AFTER_LOAD_SCRIPT\"  'execute accounts.native('''')'," + NEW_LINE +
        "\"teiid_rel:ON_VDB_DROP_SCRIPT\" 'DELETE FROM Accounts.status WHERE Name=''stock'' AND schemaname = ''Stocks'''," + NEW_LINE +
        "\"teiid_rel:MATERIALIZED_STAGE_TABLE\" 'Accounts.h2_stock_mat'," + NEW_LINE +
        "\"teiid_rel:ALLOW_MATVIEW_MANAGEMENT\" 'true'," + NEW_LINE +
        "\"teiid_rel:MATVIEW_STATUS_TABLE\" 'status'," + NEW_LINE +
        "\"teiid_rel:MATVIEW_SHARE_SCOPE\" 'NONE'," + NEW_LINE +
        "\"teiid_rel:MATVIEW_ONERROR_ACTION\" 'THROW_EXCEPTION')" + NEW_LINE +
        "AS SELECT  A.ID, S.symbol, S.price, A.COMPANY_NAME" + NEW_LINE +
        "FROM Stocks.StockPrices AS S, Accounts.PRODUCT AS A" + NEW_LINE +
        "WHERE S.symbol = A.SYMBOL;";
        stocksMatModel.setMetadata(new Metadata(metadataText, Metadata.Type.DDL.toString()));

        return vdb;
    }

	@Test
	public void testExportDynamicVdb() throws Exception {
		DynamicVdb vdb = setupPortfolioExample();
		File destination = File.createTempFile("portfolio-vdb", DOT + XML);
		vdb.export(new FileWriter(destination));
		
	}

}
