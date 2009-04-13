/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.model;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.modeler.modelgenerator.xml.wizards.StateManager;
import com.metamatrix.modeler.schema.tools.NameUtil;
import com.metamatrix.modeler.schema.tools.model.schema.Column;
import com.metamatrix.modeler.schema.tools.model.schema.QName;
import com.metamatrix.modeler.schema.tools.model.schema.Relationship;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.Type;
import com.metamatrix.modeler.schema.tools.model.schema.impl.SchemaModelImpl;
import com.metamatrix.modeler.schema.tools.processing.SchemaUtil;

public class DatabaseMetaDataImpl extends DatabaseMetaDataBase {
    private static final String ELEMENT_TABLETYPE = "ELEMENT"; //$NON-NLS-1$
    private static final String RELATIONSHIP_TABLETYPE = "RELATIONSHIP"; //$NON-NLS-1$

    private static final String[] tableTypes = new String[] {ELEMENT_TABLETYPE, RELATIONSHIP_TABLETYPE};
    private StateManager stateManager;
    private UserSettings userSettings;
    private List catalogsResultsetData;

    private List columnsResultsetData;
    private Map columnsByTableName;

    private List crossReferencesResultsetData;
    private Map crossReferencesByBothTableNames;
    private Map crossReferencesByPrimaryTableName;
    private Map crossReferencesByForeignTableName;

    private List primaryKeysResultsetData;
    private Map primaryKeysByTableName;

    private List tablesResultsetData;
    private Map tablesByTableName;

    private List tableTypesResultsetData;

    private Map tableNameInSourceMap;
    private Map columnNameInSourceMap;
    private Map primaryKeyNameInSourceMap;
    private Map foreignKeyNameInSourceMap;
    private Map tableSimpleNameMap;
    private Map columnSimpleNameMap;
    private Map primaryKeySimpleNameMap;
    private Map foreignKeySimpleNameMap;

    private Object syncObject;

    private SchemaModel schemaModel;
    private HashSet knownCatalogs;

    /**
     * 
     */
    public DatabaseMetaDataImpl( StateManager manager,
                                 UserSettings userSettings,
                                 Connection connection,
                                 Object syncObject ) {
        super(connection);
        this.stateManager = manager;
        this.syncObject = syncObject;
        this.userSettings = userSettings;
    }

    public void changed() {
        synchronized (syncObject) {
            initialize();
        }
    }

    private void ensureInitialized() {
    }

    private void initialize() {
        tableTypesResultsetData = new ArrayList();
        catalogsResultsetData = new ArrayList();
        columnsResultsetData = new ArrayList();
        columnsByTableName = new HashMap();
        crossReferencesResultsetData = new ArrayList();
        crossReferencesByBothTableNames = new HashMap();
        crossReferencesByPrimaryTableName = new HashMap();
        crossReferencesByForeignTableName = new HashMap();
        primaryKeysResultsetData = new ArrayList();
        primaryKeysByTableName = new HashMap();
        tablesResultsetData = new ArrayList();
        tablesByTableName = new HashMap();

        tableNameInSourceMap = new HashMap();
        columnNameInSourceMap = new HashMap();
        primaryKeyNameInSourceMap = new HashMap();
        foreignKeyNameInSourceMap = new HashMap();
        tableSimpleNameMap = new HashMap();
        columnSimpleNameMap = new HashMap();
        primaryKeySimpleNameMap = new HashMap();
        foreignKeySimpleNameMap = new HashMap();

        knownCatalogs = new HashSet();

        schemaModel = stateManager.getProcessedModel();
        createMetadataForTableTypes();
        createMetadataForCatalogs();
        createMetadataForRequestTable();
        createMetadataForTables();

        if (exportSQL) {
            exportMetaData();
        }
    }

    private void createMetadataForTables() {
        for (Iterator iter = schemaModel.getElements().iterator(); iter.hasNext();) {
            Object o = iter.next();
            SchemaObject table = (SchemaObject)o;
            createMetadataForTable(table);
        }
    }

    private void createMetadataForRequestTable() {
        if (userSettings.getSourceType() == StateManager.SOURCE_ACS
            || userSettings.getSourceType() == StateManager.SOURCE_HTTP_PARAMS
            || userSettings.getSourceType() == StateManager.SOURCE_HTTP_REQUEST_DOC) {
            QName qname = stateManager.getRequestResponseTable();
            addTable(qname.getNamespace(), qname.getLName(), ELEMENT_TABLETYPE);
            addTableNameToMap(qname.getNamespace(), userSettings.getRequestResponseTableXpath(), qname.getLName());
        }
    }

    protected void createMetadataForCatalogs() {
        catalogsResultsetData = new ArrayList();
        List catalogs = stateManager.getCatalogs();
        for (int i = 0; i < catalogs.size(); ++i) {
            String catalog = (String)catalogs.get(i);
            if (catalog == null || catalog.length() == 0) {
                continue;
            }
            createMetadataForCatalog(catalog);
        }
        createMetadataForCatalog(StateManager.globalNamespace);
    }

    public static boolean exportSQL = true;

    public static String[] getTableTypeNames() {
        return tableTypes;
    }

    private void createMetadataForTableTypes() {
        List row;
        String tableType;

        for (int i = 0; i < getTableTypeNames().length; i++) {
            tableType = getTableTypeNames()[i];
            row = new ArrayList();
            row.add(tableType);
            tableTypesResultsetData.add(row);
        }
    }

    private void createMetadataForCatalog( String catalog ) {
        if (!knownCatalogs.contains(catalog)) {
            knownCatalogs.add(catalog);
            List row = new ArrayList();
            row.add(catalog);
            catalogsResultsetData.add(row);
        }
    }

    private void createMetadataForTable( SchemaObject table ) {
        addTableForTable(table);
        int pkIndex;
        pkIndex = 1;
        for (Iterator colIter = table.getAttributes().iterator(); colIter.hasNext();) {
            Object oCol = colIter.next();
            Column column = (Column)oCol;
            addColumnForTable(table, column);
            if (column.isPrimaryKey()) {
                addPrimaryKeyForTable(table, column, pkIndex++);
            }
        }

        for (Iterator relIter = table.getChildren().iterator(); relIter.hasNext();) {
            Object oTableRelationship = relIter.next();
            Relationship tableRelationship = (Relationship)oTableRelationship;
            String key = tableRelationship.getChild().getSimpleName() + ':' + tableRelationship.getChild().getNamespace();
            int representation = ((SchemaModelImpl)schemaModel).getRelationToParent(key);
            switch (representation) {
                case Relationship.RELATIONSHIP_TABLE:
                    createMetadataForRelationshipTable(tableRelationship);
                    break;
                case Relationship.KEY_IN_CHILD:
                    createMetadataForKeyInChild(tableRelationship);
                    break;
                case Relationship.KEY_IN_PARENT_SINGLE:
                    createMetadataForKeyInParent(tableRelationship);
                    break;
                case Relationship.KEY_IN_PARENT_MULTIPLE:
                    createMetadataForKeyInParent(tableRelationship);
                    break;
                case Relationship.MERGE_IN_PARENT_SINGLE:
                    // The columns were alread yadded to the parent
                    break;
                case Relationship.MERGE_IN_PARENT_MULTIPLE:
                    // The columns were alread yadded to the parent
                    break;
                default:
                    break;
            }
        }
    }

    private void createMetadataForKeyInChild( Relationship tableRelationship ) {
        int pkIndex;
        pkIndex = 1;
        for (Iterator colIter = tableRelationship.getParent().getAttributes().iterator(); colIter.hasNext();) {
            Object oCol = colIter.next();
            Column column = (Column)oCol;
            if (column.isPrimaryKey()) {
                addColumnForKeyInChild(tableRelationship, column);
                addForeignKeyForKeyInChild(tableRelationship, column, pkIndex);
                ++pkIndex;
            }
        }
    }

    private void createMetadataForKeyInParent( Relationship tableRelationship ) {
        int pkIndex;
        int maxOccurs = tableRelationship.getMaxOccurs();
        for (int iOccurrence = 1; iOccurrence <= maxOccurs; iOccurrence++) {
            pkIndex = 1;
            int iOccurenceParam = maxOccurs > 1 ? iOccurrence : -1;
            for (Iterator colIter = tableRelationship.getChild().getAttributes().iterator(); colIter.hasNext();) {
                Object oCol = colIter.next();
                Column column = (Column)oCol;
                if (column.isPrimaryKey()) {
                    addColumnForKeyInParent(tableRelationship, column, iOccurenceParam);
                    addForeignKeyForKeyInParent(tableRelationship, column, pkIndex, iOccurenceParam);
                    ++pkIndex;
                }
            }
        }
    }

    private void createMetadataForRelationshipTable( Relationship tableRelationship ) {
        int pkIndex;
        addTableForParent(tableRelationship);
        pkIndex = 1;
        int fkIndex;
        SchemaObject child = tableRelationship.getChild();
        SchemaObject parent = tableRelationship.getParent();

        fkIndex = 1;
        for (Iterator colIter = parent.getAttributes().iterator(); colIter.hasNext();) {
            Object oCol = colIter.next();
            Column column = (Column)oCol;
            if (column.isPrimaryKey()) {
                addColumnForRelationshipTable(tableRelationship, column, false);
                addPrimaryKeyForRelationshipTable(tableRelationship, column, false, pkIndex++);
                addForeignKeyForRelationshipTable(tableRelationship, column, false, fkIndex++);
            }
        }

        fkIndex = 1;
        boolean selfReference = (parent == child);
        for (Iterator colIter = child.getAttributes().iterator(); colIter.hasNext();) {
            Object oCol = colIter.next();
            Column column = (Column)oCol;
            if (column.isPrimaryKey()) {
                addColumnForRelationshipTable(tableRelationship, column, selfReference);
                addPrimaryKeyForRelationshipTable(tableRelationship, column, selfReference, pkIndex++);
                addForeignKeyForRelationshipTable(tableRelationship, column, selfReference, fkIndex++);
            }
        }
    }

    private String getColumnSimpleName( final String name,
                                        String namespace,
                                        String tableName ) {
        String[] array = new String[] {namespace, tableName, name};
        List list = Arrays.asList(array);
        return (String)columnNameInSourceMap.get(list);
    }

    public String getPrimaryKeyNameInSource( final String name,
                                             String namespace ) {
        synchronized (syncObject) {
            String[] array = new String[] {namespace, name};
            List list = Arrays.asList(array);
            return (String)primaryKeySimpleNameMap.get(list);
        }
    }

    public String getForeignKeyNameInSource( final String name,
                                             String namespace ) {
        synchronized (syncObject) {
            String[] array = new String[] {namespace, name};
            List list = Arrays.asList(array);
            return (String)foreignKeySimpleNameMap.get(list);
        }
    }

    public String getTableNameInSource( final String name,
                                        String namespace ) {
        synchronized (syncObject) {
            String[] array = new String[] {namespace, name};
            List list = Arrays.asList(array);
            return (String)tableSimpleNameMap.get(list);
        }
    }

    public String getColumnNameInSource( final String name,
                                         String namespace,
                                         String tableName ) {
        synchronized (syncObject) {
            String[] array = new String[] {namespace, tableName, name};
            List list = Arrays.asList(array);
            return (String)columnSimpleNameMap.get(list);
        }
    }

    private void addTableNameToMap( String catalog,
                                    String name,
                                    String simpleName ) {
        String[] array;
        List list;
        array = new String[] {catalog, name};
        list = Arrays.asList(array);
        tableNameInSourceMap.put(list, simpleName);
        array = new String[] {catalog, simpleName};
        list = Arrays.asList(array);
        tableSimpleNameMap.put(list, name);
    }

    private void addColumnNameToMap( String catalog,
                                     String tableName,
                                     String columnName,
                                     String simpleName,
                                     String tableSimpleName ) {
        String[] array;
        List list;
        array = new String[] {catalog, tableName, columnName};
        list = Arrays.asList(array);
        columnNameInSourceMap.put(list, simpleName);
        array = new String[] {catalog, tableSimpleName, simpleName};
        list = Arrays.asList(array);
        columnSimpleNameMap.put(list, columnName);
    }

    private void addPrimaryKeyNameToMap( String catalog,
                                         String primaryKeyName,
                                         String simpleName ) {
        String[] array;
        List list;
        array = new String[] {catalog, primaryKeyName};
        list = Arrays.asList(array);
        primaryKeyNameInSourceMap.put(list, simpleName);
        array = new String[] {catalog, simpleName};
        list = Arrays.asList(array);
        primaryKeySimpleNameMap.put(list, primaryKeyName);
    }

    private void addForeignKeyNameToMap( String catalog,
                                         String foreignKeyName,
                                         String simpleName ) {
        String[] array;
        List list;
        array = new String[] {catalog, foreignKeyName};
        list = Arrays.asList(array);
        foreignKeyNameInSourceMap.put(list, simpleName);
        array = new String[] {catalog, simpleName};
        list = Arrays.asList(array);
        foreignKeySimpleNameMap.put(list, foreignKeyName);
    }

    private void addTableForTable( SchemaObject table ) {
        String catalog = stateManager.getCatalog(table);
        createMetadataForCatalog(catalog);
        String name = table.getInputXPath();

        String simpleName = NameUtil.normalizeNameForRelationalTable(table.getSimpleName());

        addTable(catalog, simpleName, ELEMENT_TABLETYPE);
        addTableNameToMap(catalog, name, simpleName);
    }

    private void addColumnForTable( SchemaObject table,
                                    Column column ) {
        String catalog = stateManager.getCatalog(table);
        String tableName = table.getInputXPath();
        String columnName = column.getXpath();

        String simpleName = NameUtil.normalizeNameForRelationalTable(column.getSimpleName());
        // Simple element types (xsd:String) that are referenced by several
        // elements are sometimes turned into schemaModel. In this event a column
        // with the name text is created to hold the data. We only want to
        // retain that name if it is never rolled up. If it is rolled up,
        // then it should assume the table name. Length indicates a rollup.
        // ElementImpl:Author Column:text becomes Column:Author.
        // TODO: We can still be burned by text matching, we need a legitimate indicator here.
        int sepLength = UserSettings.getMergedChildSep().length();
        if (simpleName.endsWith("text") && simpleName.length() > 4 + sepLength) { //$NON-NLS-1$
            // Strip the length of "text" and the length of the user defined separator.
            simpleName = simpleName.substring(0, simpleName.length() - 4 - sepLength);
        }
        String simpleTableName = NameUtil.normalizeNameForRelationalTable(table.getSimpleName());

        addColumn(catalog, simpleTableName, simpleName, column);
        addColumnNameToMap(catalog, tableName, columnName, simpleName, simpleTableName);
    }

    private void addPrimaryKeyForTable( SchemaObject table,
                                        Column column,
                                        int pkIndex ) {
        String catalog = stateManager.getCatalog(table);
        String tableName = table.getInputXPath();
        String simpleTableName = NameUtil.normalizeNameForRelationalTable(table.getSimpleName());
        String columnName = column.getXpath();
        String primaryKeyName = "pk_" + tableName; //$NON-NLS-1$
        String simpleName = "pk" + NameUtil.normalizeNameForRelationalTable(table.getSimpleName()); //$NON-NLS-1$

        addPrimaryKey(pkIndex, catalog, simpleTableName, columnName, simpleName);
        addPrimaryKeyNameToMap(catalog, primaryKeyName, simpleName);
    }

    private String getRelationshipCatalog( Relationship tableRelationship ) {
        return stateManager.getCatalog(tableRelationship.getChild());
    }

    private String getRelationshipTableName( Relationship tableRelationship ) {
        String parent = tableRelationship.getParent().getInputXPath();
        String childRel = tableRelationship.getChildRelativeXpath();
        return parent + "/" + childRel; //$NON-NLS-1$
    }

    private String getRelationshipTableSimpleName( Relationship tableRelationship ) {
        return NameUtil.normalizeNameForRelationalTable(tableRelationship.getChild().getSimpleName()) + "_rel_" //$NON-NLS-1$
               + NameUtil.normalizeNameForRelationalTable(tableRelationship.getParent().getSimpleName());
    }

    private String getRelationshipColumnName( Relationship tableRelationship,
                                              Column column,
                                              boolean selfReferenceChild ) {
        SchemaObject child = tableRelationship.getChild();
        SchemaObject parent = tableRelationship.getParent();
        SchemaObject table = column.getTable();
        String xpath;
        String columnXpath = column.getXpath();

        boolean useChild;
        if (parent == child) {
            useChild = selfReferenceChild;
        } else {
            useChild = (table == child);
        }

        if (useChild) {
            xpath = columnXpath;
        } else {
            String parentRelativeXpath = tableRelationship.getParentRelativeXpath();
            xpath = parentRelativeXpath + "/" + columnXpath; //$NON-NLS-1$
        }
        return xpath;
    }

    private String getRelationshipSimpleColumnName( Column column,
                                                    boolean selfReferenceChild ) {
        SchemaObject table = column.getTable();
        StringBuffer retval = new StringBuffer();
        if (selfReferenceChild) {
            retval.append("child_"); //$NON-NLS-1$
        }
        retval.append(NameUtil.normalizeNameForRelationalTable(table.getSimpleName()));
        retval.append('_');
        retval.append(NameUtil.normalizeNameForRelationalTable(column.getSimpleName()));
        return retval.toString();
    }

    private void addTableForParent( Relationship tableRelationship ) {
        String catalog = getRelationshipCatalog(tableRelationship);
        String name = getRelationshipTableName(tableRelationship);
        String simpleName = getRelationshipTableSimpleName(tableRelationship);

        addTable(catalog, simpleName, RELATIONSHIP_TABLETYPE);
        addTableNameToMap(catalog, name, simpleName);
    }

    private void addTable( String catalog,
                           String name,
                           String tableType ) {
        List row = new ArrayList();
        row = new ArrayList();
        row.add(catalog);
        row.add(null);
        row.add(name);
        row.add(tableType);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        tablesResultsetData.add(row);
        QName qname = SchemaUtil.getQName(catalog, name);
        addToMapOfLists(tablesByTableName, qname, row);
    }

    private void addColumnForRelationshipTable( Relationship tableRelationship,
                                                Column column,
                                                boolean selfReferenceChild ) {
        String catalog = getRelationshipCatalog(tableRelationship);
        String columnName = getRelationshipColumnName(tableRelationship, column, selfReferenceChild);
        String simpleName = getRelationshipSimpleColumnName(column, selfReferenceChild);
        String tableName = getRelationshipTableName(tableRelationship);
        String simpleTableName = getRelationshipTableSimpleName(tableRelationship);

        addColumn(catalog, simpleTableName, simpleName, column);
        addColumnNameToMap(catalog, tableName, columnName, simpleName, simpleTableName);
    }

    private void addColumnForKeyInChild( Relationship tableRelationship,
                                         Column column ) {
        String catalog = stateManager.getCatalog(tableRelationship.getChild());
        String columnName = tableRelationship.getParentRelativeXpath() + "/" + column.getXpath(); //$NON-NLS-1$
        String simpleName = NameUtil.normalizeNameForRelationalTable(tableRelationship.getParent().getSimpleName()) + "_" //$NON-NLS-1$
                            + NameUtil.normalizeNameForRelationalTable(column.getSimpleName());
        String tableName = tableRelationship.getParent().getInputXPath() + "/" + tableRelationship.getChildRelativeXpath(); //$NON-NLS-1$
        String simpleTableName = NameUtil.normalizeNameForRelationalTable(tableRelationship.getChild().getSimpleName());

        addColumn(catalog, simpleTableName, simpleName, column);
        addColumnNameToMap(catalog, tableName, columnName, simpleName, simpleTableName);
    }

    private void addForeignKeyForKeyInChild( Relationship tableRelationship,
                                             Column column,
                                             int fkIndex ) {
        // The importer contains the foreign key. The importer is the child table.
        // The exporter contains the primary key. The exporter is the parent table.

        String importerCatalog = stateManager.getCatalog(tableRelationship.getChild());
        String importerColumnName = tableRelationship.getParentRelativeXpath() + "/" + column.getXpath(); //$NON-NLS-1$
        String importerTableName = tableRelationship.getParent().getInputXPath() + "/" //$NON-NLS-1$
                                   + tableRelationship.getChildRelativeXpath();
        String importerTableSimpleName = NameUtil.normalizeNameForRelationalTable(tableRelationship.getChild().getSimpleName());

        String exporterCatalog = stateManager.getCatalog(tableRelationship.getParent());
        String exporterColumnName = column.getXpath();
        String exporterTableName = tableRelationship.getParent().getInputXPath();
        String exporterTableSimpleName = NameUtil.normalizeNameForRelationalTable(tableRelationship.getParent().getSimpleName());

        String primaryKeyName = "pk_" + exporterTableName; //$NON-NLS-1$
        String foreignKeyName = "fk_" + importerTableName + "_" + exporterTableName; //$NON-NLS-1$ //$NON-NLS-2$

        String simpleName = "fk_" + importerTableSimpleName + "_" + exporterTableSimpleName; //$NON-NLS-1$ //$NON-NLS-2$

        addCrossReference(fkIndex,
                          importerCatalog,
                          importerColumnName,
                          importerTableSimpleName,
                          exporterCatalog,
                          exporterColumnName,
                          exporterTableSimpleName,
                          primaryKeyName,
                          simpleName);
        addForeignKeyNameToMap(exporterCatalog, foreignKeyName, simpleName);
    }

    private void addCrossReference( int index,
                                    String importerCatalog,
                                    String importerColumnName,
                                    String importerTableName,
                                    String exporterCatalog,
                                    String exporterColumnName,
                                    String exporterTableName,
                                    String primaryKeyName,
                                    String name ) {
        List row = new ArrayList();
        row.add(exporterCatalog);
        row.add(null);
        row.add(exporterTableName);
        row.add(exporterColumnName);
        row.add(importerCatalog);
        row.add(null);
        row.add(importerTableName);
        row.add(importerColumnName);
        row.add(new Integer(index));
        row.add(new Integer(importedKeyNoAction));
        row.add(new Integer(importedKeyNoAction));
        row.add(name);
        row.add(primaryKeyName);
        row.add(new Integer(importedKeyInitiallyDeferred));

        crossReferencesResultsetData.add(row);
        QName primaryQname = SchemaUtil.getQName(exporterCatalog, exporterTableName);
        addToMapOfLists(crossReferencesByPrimaryTableName, primaryQname, row);
        QName foreignQname = SchemaUtil.getQName(importerCatalog, importerTableName);
        addToMapOfLists(crossReferencesByForeignTableName, foreignQname, row);
        Pair pair = new Pair(primaryQname, foreignQname);
        addToMapOfLists(crossReferencesByBothTableNames, pair, row);
    }

    private void addColumnForKeyInParent( Relationship tableRelationship,
                                          Column column,
                                          int repetition ) {
        String catalog = stateManager.getCatalog(tableRelationship.getParent());
        String columnName;
        String simpleName;
        String columnNameElementPart = tableRelationship.getChildRelativeXpath();
        String columnNameAttributePart = "/" + column.getXpath(); //$NON-NLS-1$
        String simpleNameElementPart = NameUtil.normalizeNameForRelationalTable(tableRelationship.getChild().getSimpleName());
        String simpleNameAttributePart = NameUtil.normalizeNameForRelationalTable(column.getSimpleName());
        if (repetition >= 0) {
            columnName = columnNameElementPart + "[" + repetition + "]" + columnNameAttributePart; //$NON-NLS-1$//$NON-NLS-2$
            simpleName = simpleNameElementPart + repetition + "_" + simpleNameAttributePart; //$NON-NLS-1$
        } else {
            columnName = columnNameElementPart + columnNameAttributePart;
            simpleName = simpleNameElementPart + "_" + simpleNameAttributePart; //$NON-NLS-1$
        }
        String tableName = tableRelationship.getParent().getInputXPath();
        String simpleTableName = NameUtil.normalizeNameForRelationalTable(tableRelationship.getParent().getSimpleName());

        addColumn(catalog, simpleTableName, simpleName, column);
        addColumnNameToMap(catalog, tableName, columnName, simpleName, simpleTableName);
    }

    private void addColumn( String catalog,
                            String tableName,
                            String columnName,
                            Type type ) {
        /*
         * Each column description has the following columns:

           1. TABLE_CAT String => table catalog (may be null)
           2. TABLE_SCHEM String => table schema (may be null)
           3. TABLE_NAME String => table name
           4. COLUMN_NAME String => column name
           5. DATA_TYPE int => SQL type from java.sql.Types
           6. TYPE_NAME String => Data source dependent type name, for a UDT the type name is fully qualified
           7. COLUMN_SIZE int => column size. For char or date types this is the maximum number of characters, for numeric or decimal types this is precision.
           8. BUFFER_LENGTH is not used.
           9. DECIMAL_DIGITS int => the number of fractional digits
          10. NUM_PREC_RADIX int => Radix (typically either 10 or 2)
          11. NULLABLE int => is NULL allowed.
                  * columnNoNulls - might not allow NULL values
                  * columnNullable - definitely allows NULL values
                  * columnNullableUnknown - nullability unknown 
          12. REMARKS String => comment describing column (may be null)
          13. COLUMN_DEF String => default value (may be null)
          14. SQL_DATA_TYPE int => unused
          15. SQL_DATETIME_SUB int => unused
          16. CHAR_OCTET_LENGTH int => for char types the maximum number of bytes in the column
          17. ORDINAL_POSITION int => index of column in table (starting at 1)
          18. IS_NULLABLE String => "NO" means column definitely does not allow NULL values; "YES" means the column might allow NULL values. An empty string means nobody knows.
          19. SCOPE_CATLOG String => catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
          20. SCOPE_SCHEMA String => schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
          21. SCOPE_TABLE String => table name that this the scope of a reference attribute (null if the DATA_TYPE isn't REF)
          22. SOURCE_DATA_TYPE short => source type of a distinct type or user-generated ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF) 
         */

        XSDSimpleTypeDefinition sourceType = type.getType();
        List validFacets = sourceType.getValidFacets();
        String typeName = null;
    	String baseTypeName = sourceType.getBaseTypeDefinition().getName();
        if (baseTypeName!=null && baseTypeName.equals("anySimpleType")) { //$NON-NLS-1$
            typeName = sourceType.getRootTypeDefinition().getName();
        } else {
            typeName = sourceType.getBaseTypeDefinition().getName();
        }
        int actualLength = 255;
        int actualFractionalDigits = 0;
        boolean lengthDone = false;
        if (validFacets.contains("length") && sourceType.getLengthFacet() != null) {//$NON-NLS-1$
            actualLength = sourceType.getLengthFacet().getValue();
            lengthDone = true;
        }
        if (!lengthDone && validFacets.contains("maxLength") && sourceType.getMaxLengthFacet() != null) {//$NON-NLS-1$
            actualLength = sourceType.getMaxLengthFacet().getValue();
            lengthDone = true;
        }
        if (!lengthDone && validFacets.contains("totalDigits") && sourceType.getTotalDigitsFacet() != null) {//$NON-NLS-1$
            actualLength = sourceType.getTotalDigitsFacet().getValue();
        } else if (!lengthDone && validFacets.contains("totalDigits")) { //$NON-NLS-1$
            actualLength = 20; // the type is decimal or one of its derivatives
            // but no facet specifying digits is found assign value.
            // TODO: pick more appropriate value based on derived type
        }
        if (validFacets.contains("fractionDigits") && sourceType.getFractionDigitsFacet() != null) {//$NON-NLS-1$
            actualFractionalDigits = sourceType.getFractionDigitsFacet().getValue();
        }

        List row = new ArrayList();
        row.add(catalog); // 1
        row.add(null); // 2
        row.add(tableName); // 3
        row.add(columnName); // 4
        if (this.userSettings.isUseSchemaTypes()) {
            row.add(new Integer(getSQLtype(typeName))); // 5
            row.add(typeName); // 6
        } else {
            row.add(new Integer(getSQLtype("string"))); // 5 //$NON-NLS-1$
            row.add(String.class.getName()); // 6
        }
        row.add(new Integer(actualLength)); // 7:
        row.add(""); // 8 //$NON-NLS-1$
        row.add(new Integer(actualFractionalDigits)); // 9
        row.add(new Integer(10)); // 10
        row.add(new Integer(columnNullableUnknown)); // 11
        row.add(""); // 12 //$NON-NLS-1$
        row.add(""); // 13 //$NON-NLS-1$
        row.add(""); // 14 //$NON-NLS-1$
        row.add(""); // 15 //$NON-NLS-1$
        String bType = type.getBaseType();      
        if (bType!=null && bType.equals("string")) { //$NON-NLS-1$
            row.add(new Integer(actualLength * 2)); // 16
        } else {
            row.add(new Integer(0)); // 16
        }
        row.add(""); // 17 //$NON-NLS-1$
        row.add(""); // 18 //$NON-NLS-1$
        row.add(""); // 19 //$NON-NLS-1$
        row.add(""); // 20 //$NON-NLS-1$
        row.add(""); // 21 //$NON-NLS-1$
        row.add(""); // 22 //$NON-NLS-1$
        columnsResultsetData.add(row);
        QName qname = SchemaUtil.getQName(catalog, tableName);
        addToMapOfLists(columnsByTableName, qname, row);
    }

    private int getSQLtype( String schemaTypeName ) {
        int retval = Types.VARCHAR;
		if(schemaTypeName==null) return retval;

		// trim off NS prefix if present
        String lschemaTypeName = null;
        int idx = schemaTypeName.indexOf(":"); //$NON-NLS-1$
        if (idx != -1) {
            lschemaTypeName = new String(schemaTypeName.substring(idx));
        } else {
            lschemaTypeName = schemaTypeName;
        }

        if (lschemaTypeName.equals("string")) { //$NON-NLS-1$
            retval = Types.VARCHAR;
        } else if (lschemaTypeName.equals("boolean")) { //$NON-NLS-1$
            retval = Types.BOOLEAN;
        } else if (lschemaTypeName.equals("decimal")) { //$NON-NLS-1$
            retval = Types.DECIMAL;
        } else if (lschemaTypeName.equals("float")) { //$NON-NLS-1$
            retval = Types.FLOAT;
        } else if (lschemaTypeName.equals("double")) { //$NON-NLS-1$
            retval = Types.DOUBLE;
        } else if (lschemaTypeName.equals("duration")) { //$NON-NLS-1$
            retval = Types.TIMESTAMP;
        } else if (lschemaTypeName.equals("dateTime")) { //$NON-NLS-1$
            retval = Types.TIMESTAMP;
        } else if (lschemaTypeName.equals("integer")) { //$NON-NLS-1$
            retval = Types.INTEGER;
        } else if (lschemaTypeName.equals("long") || lschemaTypeName.equals("nonPositiveInteger") //$NON-NLS-1$ //$NON-NLS-2$
                   || lschemaTypeName.equals("nonNegativeInteger")) { //$NON-NLS-1$
            retval = Types.INTEGER;
        } else if (lschemaTypeName.equals("int") || lschemaTypeName.equals("positiveInteger") //$NON-NLS-1$ //$NON-NLS-2$
                   || lschemaTypeName.equals("negativeInteger") || lschemaTypeName.equals("unsignedLong")) { //$NON-NLS-1$ //$NON-NLS-2$
            retval = Types.SMALLINT;
        } else if (lschemaTypeName.equals("short") || lschemaTypeName.equals("unsignedInt")) { //$NON-NLS-1$ //$NON-NLS-2$
            retval = Types.TINYINT;
        } else if (lschemaTypeName.equals("byte") || lschemaTypeName.equals("unsignedShort") //$NON-NLS-1$ //$NON-NLS-2$
                   || lschemaTypeName.equals("unsignedByte")) { //$NON-NLS-1$
            retval = Types.TINYINT;
        } else if (lschemaTypeName.equals("date")) { //$NON-NLS-1$
            retval = Types.TIMESTAMP;
        } else if (lschemaTypeName.equals("time")) { //$NON-NLS-1$
            retval = Types.TIMESTAMP;
        } else if (lschemaTypeName.equals("dateTime")) { //$NON-NLS-1$
            retval = Types.TIMESTAMP;
        } else if (lschemaTypeName.equals("hexBinary")) { //$NON-NLS-1$
            retval = Types.VARBINARY;
        } else if (lschemaTypeName.equals("base64Binary")) { //$NON-NLS-1$
            retval = Types.VARBINARY;
        } else if (lschemaTypeName.equals("anyURI")) { //$NON-NLS-1$
            retval = Types.VARCHAR;
        } else if (lschemaTypeName.equals("QNameImpl")) { //$NON-NLS-1$
            retval = Types.VARCHAR;
        } else if (lschemaTypeName.equals("NOTATION")) { //$NON-NLS-1$
            retval = Types.VARCHAR;
        }

        return retval;
    }

    private void addToMapOfLists( Map map,
                                  Object key,
                                  List row ) {
        Object olists = map.get(key);
        List lists = (List)olists;
        if (lists == null) {
            lists = new ArrayList();
            map.put(key, lists);
        }
        lists.add(row);
    }

    private void addForeignKeyForKeyInParent( Relationship tableRelationship,
                                              Column column,
                                              int fkIndex,
                                              int repetition ) {
        // The importer contains the foreign key. The importer is the parent table.
        // The exporter contains the primary key. The exporter is the child table.

        String importerCatalog = stateManager.getCatalog(tableRelationship.getParent());
        String importerColumnName;
        String columnNameElementPart = tableRelationship.getChildRelativeXpath();
        String columnNameAttributePart = "/" + column.getXpath(); //$NON-NLS-1$
        if (repetition >= 0) {
            importerColumnName = columnNameElementPart + "[" + repetition + "]" + columnNameAttributePart; //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            importerColumnName = columnNameElementPart + columnNameAttributePart;
        }
        String importerTableName = tableRelationship.getParent().getInputXPath();
        String importerTableSimpleName = NameUtil.normalizeNameForRelationalTable(tableRelationship.getParent().getSimpleName());

        String exporterCatalog = stateManager.getCatalog(tableRelationship.getChild());
        String exporterColumnName = column.getXpath();
        String exporterTableName = tableRelationship.getParent().getInputXPath() + "/" //$NON-NLS-1$
                                   + tableRelationship.getChildRelativeXpath();
        String exporterTableSimpleName = NameUtil.normalizeNameForRelationalTable(tableRelationship.getChild().getSimpleName());

        String primaryKeyName = "pk_" + exporterTableName; //$NON-NLS-1$
        String foreignKeyName = "fk_" + importerTableName + "_" + exporterTableName; //$NON-NLS-1$ //$NON-NLS-2$
        if (repetition > 0) {
            foreignKeyName += repetition;
        }

        String simpleName = "fk_" + importerTableSimpleName + "_" + exporterTableSimpleName; //$NON-NLS-1$ //$NON-NLS-2$
        if (repetition > 0) {
            simpleName += repetition;
        }

        addCrossReference(fkIndex,
                          importerCatalog,
                          importerColumnName,
                          importerTableSimpleName,
                          exporterCatalog,
                          exporterColumnName,
                          exporterTableSimpleName,
                          primaryKeyName,
                          simpleName);
        addForeignKeyNameToMap(exporterCatalog, foreignKeyName, simpleName);
    }

    private void addPrimaryKeyForRelationshipTable( Relationship tableRelationship,
                                                    Column column,
                                                    boolean selfReferenceChild,
                                                    int pkIndex ) {
        String catalog = getRelationshipCatalog(tableRelationship);
        String tableName = getRelationshipTableName(tableRelationship);
        String simpleTableName = getRelationshipTableSimpleName(tableRelationship);
        String columnName = getRelationshipColumnName(tableRelationship, column, selfReferenceChild);
        String primaryKeyName = "pk_" + tableName; //$NON-NLS-1$

        String simpleName = "pk_" + getRelationshipTableSimpleName(tableRelationship); //$NON-NLS-1$

        addPrimaryKey(pkIndex, catalog, simpleTableName, columnName, simpleName);
        addPrimaryKeyNameToMap(catalog, primaryKeyName, simpleName);
    }

    private void addPrimaryKey( int pkIndex,
                                String catalog,
                                String tableName,
                                String columnName,
                                String keyName ) {
        List row = new ArrayList();
        row.add(catalog);
        row.add(null);
        row.add(tableName);
        row.add(columnName);
        row.add(new Integer(pkIndex));
        row.add(keyName);
        primaryKeysResultsetData.add(row);
        QName qname = SchemaUtil.getQName(catalog, tableName);
        addToMapOfLists(primaryKeysByTableName, qname, row);
    }

    private void addForeignKeyForRelationshipTable( Relationship tableRelationship,
                                                    Column column,
                                                    boolean selfReferenceChild,
                                                    int fkIndex ) {
        // The importer contains the foreign key. The importer is the relationship table.
        // The exporter contains the primary key. The exporter can be the parent table or the child table (the column's table)

        String importerCatalog = getRelationshipCatalog(tableRelationship);
        String importerTableName = getRelationshipTableName(tableRelationship);
        String importerTableSimpleName = getRelationshipTableSimpleName(tableRelationship);
        String importerColumnName = getRelationshipColumnName(tableRelationship, column, selfReferenceChild);

        String exporterCatalog = stateManager.getCatalog(column.getTable());
        String exporterTableName = column.getTable().getInputXPath();
        String exporterTableSimpleName = NameUtil.normalizeNameForRelationalTable(column.getTable().getSimpleName());
        String exporterColumnName = column.getXpath();

        String primaryKeyName = "pk_" + exporterTableName; //$NON-NLS-1$

        String fkPrefix = selfReferenceChild ? "fk_child_" : "fk_"; //$NON-NLS-1$ //$NON-NLS-2$
        String foreignKeyName = fkPrefix + importerTableName + "_" + exporterTableName; //$NON-NLS-1$

        String simpleName = fkPrefix + getRelationshipTableSimpleName(tableRelationship) + "_" //$NON-NLS-1$
                            + NameUtil.normalizeNameForRelationalTable(column.getTable().getSimpleName());

        addCrossReference(fkIndex,
                          importerCatalog,
                          importerColumnName,
                          importerTableSimpleName,
                          exporterCatalog,
                          exporterColumnName,
                          exporterTableSimpleName,
                          primaryKeyName,
                          simpleName);
        addForeignKeyNameToMap(exporterCatalog, foreignKeyName, simpleName);
    }

    /**
     * @see java.sql.DatabaseMetaData#getCatalogs()
     */
    public ResultSet getCatalogs() {
        synchronized (syncObject) {
            ensureInitialized();
            ResultSet retval = new MapResultSet(catalogsResultsetData, catalogsResultsetMetadata);
            return retval;
        }
    }

    /**
     * @see java.sql.DatabaseMetaData#getTableTypes()
     */
    public ResultSet getTableTypes() {
        synchronized (syncObject) {
            return new MapResultSet(tableTypesResultsetData, tableTypesResultsetMetadata);
        }
    }

    /**
     * @see java.sql.DatabaseMetaData#getExportedKeys(java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getExportedKeys( String catalog,
                                      String schema,
                                      String table ) {
        synchronized (syncObject) {
            ensureInitialized();
            QName qname = makeLookup(catalog, schema, table);

            List data;
            if (qname != null) {
                Object o = crossReferencesByPrimaryTableName.get(qname);
                data = (List)o;
                if (data == null) {
                    data = new ArrayList();
                }
            } else {
                data = new ArrayList();
                for (Iterator iter = crossReferencesResultsetData.iterator(); iter.hasNext();) {
                    Object o = iter.next();
                    List row = (List)o;
                    Object rowCatalog = row.get(0);
                    Object rowTable = row.get(2);
                    if (catalog != null && !catalog.equals(rowCatalog)) continue;
                    if (table != null && !table.equals(rowTable)) continue;

                    data.add(row);
                }
            }
            return new MapResultSet(data, crossReferencesResultsetMetadata);
        }
    }

    /**
     * @see java.sql.DatabaseMetaData#getImportedKeys(java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getImportedKeys( String catalog,
                                      String schema,
                                      String table ) {
        synchronized (syncObject) {
            ensureInitialized();
            QName qname = makeLookup(catalog, schema, table);

            List data;
            if (qname != null) {
                Object o = crossReferencesByForeignTableName.get(qname);
                data = (List)o;
                if (data == null) {
                    data = new ArrayList();
                }
            } else {
                data = new ArrayList();
                for (Iterator iter = crossReferencesResultsetData.iterator(); iter.hasNext();) {
                    Object o = iter.next();
                    List row = (List)o;
                    Object rowCatalog = row.get(4);
                    Object rowTable = row.get(6);
                    if (catalog != null && !catalog.equals(rowCatalog)) continue;
                    if (table != null && !table.equals(rowTable)) continue;

                    data.add(row);
                }
            }
            return new MapResultSet(data, crossReferencesResultsetMetadata);
        }
    }

    private QName makeLookup( String catalog,
                              String schema,
                              String tableNamePattern ) {
        if (tableNamePattern == null || tableNamePattern.indexOf('%') > -1) {
            return null;
        }

        if (schema != null) {
            return null;
        }

        QName retval = SchemaUtil.getQName(catalog, tableNamePattern);
        return retval;

    }

    /**
     * @see java.sql.DatabaseMetaData#getPrimaryKeys(java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getPrimaryKeys( String catalog,
                                     String schema,
                                     String tableNamePattern ) {
        synchronized (syncObject) {
            ensureInitialized();

            List data;
            QName qname = makeLookup(catalog, schema, tableNamePattern);
            if (qname != null) {
                Object o = primaryKeysByTableName.get(qname);
                data = (List)o;
                if (data == null) {
                    data = new ArrayList();
                }
            } else {
                data = new ArrayList();
                for (Iterator iter = primaryKeysResultsetData.iterator(); iter.hasNext();) {
                    Object o = iter.next();
                    List row = (List)o;
                    Object rowCatalog = row.get(0);
                    Object oRowTable = row.get(2);
                    String rowTable = (String)oRowTable;
                    if (catalog != null && !catalog.equals(rowCatalog)) continue;
                    if (tableNamePattern != null && !patternMatch(tableNamePattern, rowTable)) continue;

                    data.add(row);
                }
            }
            return new MapResultSet(data, primaryKeysResultsetMetadata);
        }
    }

    /**
     * @see java.sql.DatabaseMetaData#getColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getColumns( String catalog,
                                 String schemaPattern,
                                 String tableNamePattern,
                                 String columnNamePattern ) {
        synchronized (syncObject) {
            ensureInitialized();

            List dataForTable;
            QName qname = makeLookup(catalog, schemaPattern, tableNamePattern);
            if (qname != null) {
                Object o = columnsByTableName.get(qname);
                dataForTable = (List)o;
            } else {
                dataForTable = new ArrayList();
                for (Iterator iter = columnsResultsetData.iterator(); iter.hasNext();) {
                    Object o = iter.next();
                    List row = (List)o;
                    Object rowCatalog = row.get(0);
                    Object oRowTable = row.get(2);
                    String rowTable = (String)oRowTable;
                    if (catalog != null && !catalog.equals(rowCatalog)) continue;
                    if (tableNamePattern != null && !patternMatch(tableNamePattern, rowTable)) continue;

                    dataForTable.add(row);
                }
            }

            List data;
            if (columnNamePattern == null || columnNamePattern.equals("%")) { //$NON-NLS-1$
                data = dataForTable;
            } else {
                data = new ArrayList();
                for (Iterator iter = dataForTable.iterator(); iter.hasNext();) {
                    Object o = iter.next();
                    List row = (List)o;
                    Object oRowColumn = row.get(3);
                    String rowColumn = (String)oRowColumn;
                    if (!patternMatch(columnNamePattern, rowColumn)) continue;

                    data.add(row);
                }
            }
            if (data == null) {
                data = new ArrayList();
            }

            return new MapResultSet(data, columnsResultsetMetadata);
        }
    }

    private boolean patternMatch( String pattern,
                                  String test ) {
        if (pattern.equals("%")) { //$NON-NLS-1$
            return true;
        }

        if (pattern.indexOf('%') == -1) {
            boolean retval = (pattern.equals(test));
            return retval;
        }

        StringBuffer regex = new StringBuffer();
        boolean insideQuote = false;
        for (int i = 0; i < pattern.length(); ++i) {
            char c = pattern.charAt(i);
            switch (c) {
                case '%':
                    if (insideQuote) {
                        regex.append("\\E"); //$NON-NLS-1$
                        insideQuote = false;
                    }
                    regex.append(".*"); //$NON-NLS-1$
                    break;
                case '_':
                    if (insideQuote) {
                        regex.append("\\E"); //$NON-NLS-1$
                        insideQuote = false;
                    }
                    regex.append('.');
                    break;
                default:
                    if (!insideQuote) {
                        regex.append("\\Q"); //$NON-NLS-1$
                        insideQuote = true;
                    }
                    regex.append(c);
                    break;
            }
        }
        if (insideQuote) {
            regex.append("\\E"); //$NON-NLS-1$
            insideQuote = false;
        }

        Pattern p = Pattern.compile(regex.toString());
        Matcher m = p.matcher(test);
        boolean b = m.matches();
        return b;
    }

    /**
     * @see java.sql.DatabaseMetaData#getTables(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
     */
    public ResultSet getTables( String catalog,
                                String schemaPattern,
                                String tableNamePattern,
                                String[] types ) {
        synchronized (syncObject) {
            ensureInitialized();

            List typeList = types == null ? null : Arrays.asList(types);

            List dataForTable;
            QName qname = makeLookup(catalog, schemaPattern, tableNamePattern);
            if (qname != null) {
                Object o = tablesByTableName.get(qname);
                dataForTable = (List)o;
                if (dataForTable == null) {
                    dataForTable = new ArrayList();
                }
            } else {
                dataForTable = new ArrayList();
                for (Iterator iter = tablesResultsetData.iterator(); iter.hasNext();) {
                    Object o = iter.next();
                    List row = (List)o;
                    Object rowCatalog = row.get(0);
                    Object oRowTable = row.get(2);
                    String rowTable = (String)oRowTable;
                    if (catalog != null && !catalog.equals(rowCatalog)) continue;
                    if (tableNamePattern != null && !patternMatch(tableNamePattern, rowTable)) continue;

                    dataForTable.add(row);
                }
            }

            List data;
            if (types == null) {
                data = dataForTable;
            } else {
                data = new ArrayList();
                for (Iterator iter = dataForTable.iterator(); iter.hasNext();) {
                    Object o = iter.next();
                    List row = (List)o;
                    Object oType = row.get(3);
                    String type = (String)oType;
                    if (!typeList.contains(type)) continue;

                    data.add(row);
                }
            }

            return new MapResultSet(data, tablesResultsetMetadata);
        }
    }

    private static class Pair {
        public Object o1;
        public Object o2;

        public Pair( Object o1,
                     Object o2 ) {
            this.o1 = o1;
            this.o2 = o2;

            int o1Hash = o1 == null ? 0 : o1.hashCode();
            int o2Hash = o2 == null ? 0 : o2.hashCode();

            hashCode = 17;
            hashCode = 37 * hashCode + o1Hash;
            hashCode = 37 * hashCode + o2Hash;
        }

        @Override
        public boolean equals( Object o ) {
            if (!(o instanceof Pair)) {
                return false;
            }

            Pair other = (Pair)o;
            if (o1 == null && other.o1 != null) {
                return false;
            }
            if (o1 != null && other.o1 == null) {
                return false;
            }
            if (o1 != null && !(o1.equals(other.o1))) {
                return false;
            }

            if (o2 == null && other.o2 != null) {
                return false;
            }
            if (o2 != null && other.o2 == null) {
                return false;
            }
            if (o2 != null && !(o2.equals(other.o2))) {
                return false;
            }

            return true;
        }

        int hashCode;

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    /**
     * @see java.sql.DatabaseMetaData#getCrossReference(java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public ResultSet getCrossReference( String primaryCatalog,
                                        String primarySchema,
                                        String primaryTable,
                                        String foreignCatalog,
                                        String foreignSchema,
                                        String foreignTable ) {
        synchronized (syncObject) {
            ensureInitialized();
            QName primaryQname = makeLookup(primaryCatalog, primarySchema, primaryTable);
            QName foreignQname = makeLookup(foreignCatalog, foreignSchema, foreignTable);
            Pair pair;
            if (primaryQname == null || foreignQname == null) {
                pair = null;
            } else {
                pair = new Pair(primaryQname, foreignQname);
            }

            List data;
            if (pair != null) {
                Object o = crossReferencesByBothTableNames.get(pair);
                data = (List)o;
                if (data == null) {
                    data = new ArrayList();
                }
            } else {
                data = new ArrayList();
                for (Iterator iter = crossReferencesResultsetData.iterator(); iter.hasNext();) {
                    Object o = iter.next();
                    List row = (List)o;
                    Object rowPkCatalog = row.get(0);
                    Object rowPkTable = row.get(2);
                    Object rowFkCatalog = row.get(4);
                    Object rowFkTable = row.get(6);
                    if (primaryCatalog != null && !primaryCatalog.equals(rowPkCatalog)) continue;
                    if (primaryTable != null && !primaryTable.equals(rowPkTable)) continue;
                    if (foreignCatalog != null && !foreignCatalog.equals(rowFkCatalog)) continue;
                    if (foreignTable != null && !foreignTable.equals(rowFkTable)) continue;

                    data.add(row);
                }
            }
            return new MapResultSet(data, crossReferencesResultsetMetadata);
        }
    }

    private void exportMetaData() {
        File file = null;
        Writer writer = null;
        try {
            file = File.createTempFile("xmlmetadata", ".sql"); //$NON-NLS-1$ //$NON-NLS-2$
            writer = new FileWriter(file);
            ResultSet catalogset = getCatalogs();
            while (catalogset.next()) {
                String catalog = catalogset.getString(1);
                ResultSet tableset = getTables(catalog, null, null, null);
                while (tableset.next()) {
                    String tableName = tableset.getString(3);
                    String tableNameInSource = this.getTableNameInSource(tableName, catalog);

                    // CREATE TABLE [dbo].[AgeTable] (
                    // [Name] [varchar] (50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
                    // [Age] [int] NULL
                    // ) ON [PRIMARY]
                    // GO
                    writer.write("CREATE TABLE [" + tableName + "] (\n"); //$NON-NLS-1$ //$NON-NLS-2$
                    ResultSet columnset = getColumns(catalog, null, tableName, null);
                    boolean first = true;
                    while (columnset.next()) {
                        if (first) {
                            first = false;
                        } else {
                            writer.write(",\n"); //$NON-NLS-1$
                        }
                        writer.write("\t[" + columnset.getString(4) + "] [varchar] NOT NULL"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    writer.write("\n) ON [PRIMARY]\nGO\n\n"); //$NON-NLS-1$

                    // ALTER TABLE [dbo].[AgeTable] ADD
                    // CONSTRAINT [PK_AgeTable] PRIMARY KEY CLUSTERED
                    // (
                    // [Name]
                    // ) ON [PRIMARY]
                    // GO
                    ResultSet pkset = getPrimaryKeys(catalog, null, tableName);
                    if (pkset.next()) {
                        String pkName = pkset.getString(6);
                        writer.write("ALTER TABLE [" + tableName + "] ADD \n"); //$NON-NLS-1$ //$NON-NLS-2$
                        writer.write("\tCONSTRAINT [" + pkName + "] PRIMARY KEY CLUSTERED (\n"); //$NON-NLS-1$ //$NON-NLS-2$
                        first = true;
                        while (first || pkset.next()) {
                            if (first) {
                                first = false;
                            } else {
                                writer.write(",\n"); //$NON-NLS-1$
                            }
                            String colNameInSource = pkset.getString(4);
                            String colName = getColumnSimpleName(colNameInSource, catalog, tableNameInSource);
                            writer.write("\t[" + colName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        writer.write("\n) ON [PRIMARY]\nGO\n\n"); //$NON-NLS-1$
                    }
                }
            }

            catalogset = getCatalogs();
            while (catalogset.next()) {
                String catalog = catalogset.getString(1);
                ResultSet tableset = getTables(catalog, null, null, null);
                while (tableset.next()) {
                    String tableName = tableset.getString(3);
                    String tableNameInSource = this.getTableNameInSource(tableName, catalog);

                    // ALTER TABLE [dbo].[AgeTable] ADD
                    // CONSTRAINT [FK_AgeTable_Person] FOREIGN KEY
                    // (
                    // [Name]
                    // ) REFERENCES [dbo].[Person] (
                    // [Name]
                    // )
                    // GO
                    ResultSet fkset = getImportedKeys(catalog, null, tableName);
                    if (fkset.next()) {
                        String fkName = fkset.getString(12);
                        String exporterTableName = fkset.getString(3);
                        writer.write("ALTER TABLE [" + tableName + "] ADD \n"); //$NON-NLS-1$ //$NON-NLS-2$
                        writer.write("\tCONSTRAINT [" + fkName + "] FOREIGN KEY (\n"); //$NON-NLS-1$ //$NON-NLS-2$
                        boolean first = true;
                        while (first || fkset.next()) {
                            if (first) {
                                first = false;
                            } else {
                                writer.write(",\n"); //$NON-NLS-1$
                            }
                            String importerColNameInSource = fkset.getString(8);
                            String importerColName = getColumnSimpleName(importerColNameInSource, catalog, tableNameInSource);
                            writer.write("\t[" + importerColName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                        }

                        writer.write("\n) REFERENCES " + exporterTableName + " (\n"); //$NON-NLS-1$ //$NON-NLS-2$
                        first = true;
                        fkset.beforeFirst();
                        while (fkset.next()) {
                            if (first) {
                                first = false;
                            } else {
                                writer.write(",\n"); //$NON-NLS-1$
                            }
                            String exporterColNameInSource = fkset.getString(4);
                            String exporterColName = getColumnSimpleName(exporterColNameInSource, catalog, tableNameInSource);
                            writer.write("\t[" + exporterColName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        writer.write("\n)\nGO\n\n"); //$NON-NLS-1$
                    }
                }
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter(0);
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return;
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    StringWriter sw = new StringWriter(0);
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    return;
                }
            }
        }
    }

    public Boolean isRequestOrResponseTable( QName qname ) {
        return stateManager.isRequestOrResponseTable(qname);
    }

    protected void catalogsChanged() {
        initialize();
    }

    public Map getNamespacePrefixes( String name ) {
        return schemaModel.getNamespacePrefixes();
    }
}
