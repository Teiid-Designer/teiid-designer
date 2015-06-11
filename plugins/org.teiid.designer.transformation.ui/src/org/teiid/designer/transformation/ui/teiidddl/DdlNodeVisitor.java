///*
// * JBoss, Home of Professional Open Source.
// * See the COPYRIGHT.txt file distributed with this work for information
// * regarding copyright ownership.  Some portions may be licensed
// * to Red Hat, Inc. under one or more contributor license agreements.
// *
// * This library is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Lesser General Public
// * License as published by the Free Software Foundation; either
// * version 2.1 of the License, or (at your option) any later version.
// *
// * This library is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this library; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
// * 02110-1301 USA.
// */
//package org.teiid.designer.transformation.ui.teiidddl;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import javax.jcr.Node;
//import javax.jcr.Property;
//import javax.jcr.RepositoryException;
//import javax.jcr.Value;
//import javax.jcr.ValueFormatException;
//import javax.jcr.nodetype.NodeType;
//import org.komodo.modeshape.AbstractNodeVisitor;
//import org.komodo.modeshape.teiid.TeiidSqlNodeVisitor;
//import org.komodo.modeshape.teiid.parser.TeiidSQLConstants;
//import org.komodo.modeshape.teiid.parser.TeiidSQLConstants.NonReserved;
//import org.komodo.modeshape.teiid.parser.TeiidSQLConstants.Reserved;
//import org.komodo.spi.ddl.TeiidDDLConstants;
//import org.komodo.spi.metadata.MetadataNamespaces;
//import org.komodo.spi.runtime.version.TeiidVersion;
//import org.komodo.spi.type.DataTypeManager.DataTypeName;
//import org.komodo.spi.utils.KeyInValueHashMap;
//import org.komodo.utils.StringUtils;
//import org.modeshape.jcr.JcrLexicon;
//import org.modeshape.sequencer.ddl.StandardDdlLexicon;
//import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlLexicon;
//
///**
// * Visitor that will walk a ddl node tree and convert it to
// * the a string.
// */
//public class DdlNodeVisitor extends AbstractNodeVisitor
//    implements Reserved, NonReserved, MetadataNamespaces {
//
//    private static final String UNDEFINED = "undefined"; //$NON-NLS-1$
//
//    /**
//     * Exclusions for what the visitor should avoid visiting in the given node.
//     * By default, the visitor will visit all tables, procedures and functions.
//     */
//    public enum VisitorExclusions {
//        /**
//         * Exclude Tables
//         */
//        EXCLUDE_TABLES,
//
//        /**
//         * Exclude Procedures
//         */
//        EXCLUDE_PROCEDURES,
//
//        /**
//         * Exclude Functions
//         */
//        EXCLUDE_FUNCTIONS
//    }
//
//    private StringBuilder ddlBuffer = new StringBuilder();
//
//    private boolean includeTables = true;
//
//    private boolean includeProcedures = true;
//
//    private boolean includeFunctions = true;
//
//    private Set<DataTypeName> lengthDataTypes;
//
//    private Set<DataTypeName> precisionDataTypes;
//
//    private KeyInValueHashMap<String, URI> namespaceMap = new KeyInValueHashMap<String, URI>(new URIMapAdapter());
//
//    private static Map<String, MixinTypeName> mixinTypeIndex = new HashMap<String, MixinTypeName>();
//
//    private enum MixinTypeName {
//
//        CREATE_TABLE(TeiidDdlLexicon.CreateTable.TABLE_STATEMENT),
//
//        CREATE_VIEW(TeiidDdlLexicon.CreateTable.VIEW_STATEMENT),
//
//        OPTION_NAMESPACE(TeiidDdlLexicon.OptionNamespace.STATEMENT),
//
//        CREATE_PROCEDURE(TeiidDdlLexicon.CreateProcedure.PROCEDURE_STATEMENT),
//
//        CREATE_FUNCTION(TeiidDdlLexicon.CreateProcedure.FUNCTION_STATEMENT),
//
//        UNKNOWN(UNDEFINED);
//
//        private String nodeTypeId;
//
//        private MixinTypeName(String nodeTypeId) {
//            this.nodeTypeId = nodeTypeId;
//            mixinTypeIndex.put(this.nodeTypeId, this);
//        }
//
//        /**
//         * Find the enum value related to the given node type
//         *
//         * @param nodeType node type to attempt to find
//         *
//         * @return related mixin type name to the node type
//         */
//        public static MixinTypeName findName(NodeType nodeType) {
//            if(nodeType == null)
//                return UNKNOWN;
//
//            MixinTypeName mtName = mixinTypeIndex.get(nodeType.getName());
//            if (mtName != null)
//                return mtName;
//
//            return UNKNOWN;
//        }
//
//    }
//
//    private enum TableType {
//        TABLE,
//        VIEW,
//        GLOBAL_TEMP_TABLE;
//    }
//
//    private class CreateObjectContext {
//
//        private TableType tableType = TableType.TABLE;
//
//        private boolean virtual = false;
//
//        public boolean isPhysical() {
//            return !virtual;
//        }
//
//        public void setPhysical(boolean physical) {
//            this.virtual = !physical;
//        }
//
//        public boolean isVirtual() {
//            return virtual;
//        }
//
//        public void setVirtual(boolean virtual) {
//            this.virtual = virtual;
//        }
//
//        public TableType getTableType() {
//            return tableType;
//        }
//
//        public void setTableType(TableType tableType) {
//            this.tableType = tableType;
//        }
//    }
//
//    private class ColumnContext {
//
//        private boolean autoIncremented;
//
//        private String nullType;
//
//        private String dataTypeId;
//
//        /**
//         * @return auto incremented
//         */
//        public boolean isAutoIncremented() {
//            return autoIncremented;
//        }
//
//        /**
//         * @param autoIncremented auto incremented
//         */
//        public void setAutoIncremented(boolean autoIncremented) {
//            this.autoIncremented = autoIncremented;
//        }
//
//        /**
//         * @return is not null
//         */
//        public boolean isNotNull() {
//            return TeiidDDLConstants.NOT_NULL.equals(nullType);
//        }
//
//        /**
//         * @param nullType null type
//         */
//        public void setNullType(String nullType) {
//            this.nullType = nullType;
//        }
//
//        /**
//         * @return data type name
//         */
//        public DataTypeName getDataTypeName() {
//            return getDataTypeManager().getDataTypeName(dataTypeId);
//        }
//
//        /**
//         * @return the dataType
//         */
//        public String getDataType() {
//            return dataTypeId;
//        }
//
//        /**
//         * @param dataType data type
//         */
//        public void setDataType(String dataType) {
//            this.dataTypeId = dataType.toLowerCase();
//        }
//    }
//
//    /**
//     * @param version teiid version
//     * @param startOnNewLine prepend new line to start of ddl string
//     * @param exclusions any items that should be excluded from visiting
//     */
//    public DdlNodeVisitor(TeiidVersion version, boolean startOnNewLine, VisitorExclusions... exclusions) {
//        super(version);
//
//        if (exclusions != null) {
//            for (VisitorExclusions exclusion : exclusions) {
//                switch (exclusion) {
//                    case EXCLUDE_TABLES:
//                        this.includeTables = false;
//                        break;
//                    case EXCLUDE_PROCEDURES:
//                        this.includeProcedures = false;
//                        break;
//                    case EXCLUDE_FUNCTIONS:
//                        this.includeFunctions = false;
//                        break;
//                }
//            }
//        }
//        if( startOnNewLine ) {
//        	ddlBuffer.append(NEW_LINE);
//        }
//    }
//
//    /**
//     * @return the complete visited ddl string
//     */
//    public String getDdl() {
//        return ddlBuffer.toString();
//    }
//
//    @Override
//    protected String undefined() {
//        return UNDEFINED;
//    }
//
//    private DdlNodeVisitor append(Object o) {
//        if (NEW_LINE.equals(o) && ddlBuffer.length() == 0) {
//            // Ignore new line calls at the start of the whole text
//            return this;
//        }
//
//        ddlBuffer.append(o);
//        return this;
//    }
//
//    private Set<DataTypeName> getLengthDataTypes() {
//        if (lengthDataTypes == null) {
//            lengthDataTypes = new HashSet<DataTypeName>();
//            lengthDataTypes.add(DataTypeName.CHAR);
//            lengthDataTypes.add(DataTypeName.CLOB);
//            lengthDataTypes.add(DataTypeName.BLOB);
//            lengthDataTypes.add(DataTypeName.OBJECT);
//            lengthDataTypes.add(DataTypeName.XML);
//            lengthDataTypes.add(DataTypeName.STRING);
//            lengthDataTypes.add(DataTypeName.VARBINARY);
//            lengthDataTypes.add(DataTypeName.BIG_INTEGER);
//        }
//
//        return lengthDataTypes;
//    }
//
//    private Set<DataTypeName> getPrecsionDataTypes() {
//        if (precisionDataTypes == null) {
//            precisionDataTypes = new HashSet<DataTypeName>();
//            precisionDataTypes.add(DataTypeName.BIG_DECIMAL);
//        }
//
//        return precisionDataTypes;
//    }
//
//    private ColumnContext createColumnContext(Node columnNode) throws RepositoryException {
//        Property autoIncProp = property(columnNode, TeiidDdlLexicon.CreateTable.AUTO_INCREMENT);
//        Property nullProp = property(columnNode, StandardDdlLexicon.NULLABLE);
//        Property dataTypeProp = property(columnNode, StandardDdlLexicon.DATATYPE_NAME);
//
//        boolean autoIncremented = autoIncProp != null ? autoIncProp.getBoolean() : false;
//        String nullType = toString(nullProp);
//        String dataType = toString(dataTypeProp);
//
//        ColumnContext columnContext = new ColumnContext();
//        columnContext.setAutoIncremented(autoIncremented);
//        columnContext.setNullType(nullType);
//        columnContext.setDataType(dataType);
//
//        return columnContext;
//    }
//
//    private String escapeStringValue(String str, String tick) {
//        return StringUtils.replaceAll(str, tick, tick + tick);
//    }
//
//    private String escapeSinglePart(String token) {
//        if (TeiidSQLConstants.isReservedWord(getVersion(), token)) {
//            return TeiidSQLConstants.Tokens.ID_ESCAPE_CHAR + token + TeiidSQLConstants.Tokens.ID_ESCAPE_CHAR;
//        }
//        boolean escape = true;
//        char start = token.charAt(0);
//        if (HASH.equals(Character.toString(start)) || AMPERSAND.equals(Character.toString(start)) || StringUtils.isLetter(start)) {
//            escape = false;
//            for (int i = 1; !escape && i < token.length(); i++) {
//                char c = token.charAt(i);
//                escape = !StringUtils.isLetterOrDigit(c) && c != '_';
//            }
//        }
//        if (escape) {
//            return TeiidSQLConstants.Tokens.ID_ESCAPE_CHAR + escapeStringValue(token, SPEECH_MARK) + TeiidSQLConstants.Tokens.ID_ESCAPE_CHAR;
//        }
//        return token;
//    }
//
//    private void optionNamespace(Node namespace) throws Exception {
//        if (!hasMixinType(namespace, TeiidDdlLexicon.OptionNamespace.STATEMENT))
//            return;
//
//        String prefix = namespace.getName();
//        String uriValue = undefined();
//        Property uriProp = property(namespace, TeiidDdlLexicon.OptionNamespace.URI);
//        if (uriProp != null)
//            uriValue = toString(uriProp);
//
//        if (namespaceMap.isEmpty()) {
//            for (URI builtInUri : MetadataNamespaces.URI.map().values())
//                namespaceMap.add(builtInUri);
//        }
//
//        URI uri = namespaceMap.get(prefix);
//        if (uri == null) {
//            uri = new URI(prefix, uriValue);
//            namespaceMap.add(uri);
//        }
//
//        append(SET).append(SPACE).append(NAMESPACE).
//        append(SPACE).append(QUOTE_MARK).
//        append(StringUtils.replaceAll(uri.getUnbracedURI(), QUOTE_MARK, QUOTE_MARK + QUOTE_MARK)).
//        append(QUOTE_MARK).
//        append(SPACE).append(AS).append(SPACE).
//        append(escapeSinglePart(uri.getPrefix())).
//        append(SEMI_COLON);
//    }
//
//    private void columnDefault(Node column, ColumnContext context) throws RepositoryException {
//        Property defaultProp = property(column, StandardDdlLexicon.DEFAULT_VALUE);
//        if (defaultProp == null)
//            return;
//
//        String defaultValue = toString(defaultProp);
//        append(SPACE).append(DEFAULT).append(SPACE).
//        append(QUOTE_MARK).append(StringUtils.replaceAll(defaultValue, QUOTE_MARK, QUOTE_MARK + QUOTE_MARK)).
//        append(QUOTE_MARK);
//    }
//
//    private void column(Node column, ColumnContext context, boolean includeName, boolean includeType) throws RepositoryException {
//        if (includeName) {
//            append(escapeSinglePart(column.getName()));
//        }
//
//        if (includeType) {
//            if (includeName) {
//                append(SPACE);
//            }
//
//            append(context.getDataType());
//
//            Property colLengthProp = property(column, StandardDdlLexicon.DATATYPE_LENGTH);
//            Property colPrecisionProp = property(column, StandardDdlLexicon.DATATYPE_PRECISION);
//            Property colScaleProp = property(column, StandardDdlLexicon.DATATYPE_SCALE);
//            Property colArrDimsProp = property(column, StandardDdlLexicon.DATATYPE_ARRAY_DIMENSIONS);
//
//            long colLength = colLengthProp != null ? colLengthProp.getLong() : -1;
//            long colPrecision = colPrecisionProp != null ? colPrecisionProp.getLong() : -1;
//            long colScale = colScaleProp != null ? colScaleProp.getLong() : -1;
//            long colArrDims = colArrDimsProp != null ? colArrDimsProp.getLong() : -1;
//
//            if (getLengthDataTypes().contains(context.getDataTypeName())) {
//
//                if (colLength > -1) {
//                    append(OPEN_BRACKET).append(colLength).append(CLOSE_BRACKET);
//                }
//
//            } else if (getPrecsionDataTypes().contains(context.getDataTypeName()) &&
//                            (colPrecision > -1 || colScale > -1)) {
//
//                append(OPEN_BRACKET).append(colPrecision);
//                if (colScale > -1) {
//                    append(COMMA).append(colScale);
//                }
//                append(CLOSE_BRACKET);
//
//            }
//
//            for (long dims = colArrDims; dims > 0; dims--) {
//                append(OPEN_SQUARE_BRACKET).append(CLOSE_SQUARE_BRACKET);
//            }
//
//            if (context.isNotNull()) {
//                append(SPACE).append(TeiidDDLConstants.NOT_NULL);
//            }
//        }
//    }
//
//    private URI findNamespace(String nsURI) {
//        for (URI uri : namespaceMap.values()) {
//            if (uri.getUri().equals(nsURI))
//                return uri;
//
//            if (uri.getUnbracedURI().equals(nsURI))
//                return uri;
//        }
//
//        return null;
//    }
//
//    private void statementOption(Node stmtOption) throws RepositoryException {
//        if (!hasMixinType(stmtOption, StandardDdlLexicon.TYPE_STATEMENT_OPTION))
//            return;
//
//        String key = stmtOption.getName();
//        String value = undefined();
//
//        Property property = property(stmtOption, StandardDdlLexicon.VALUE);
//        if (property != null)
//            value = toString(property);
//
//        if (undefined().equals(value))
//            value = NULL;
//
//        String[] keyComponents = key.split(COLON);
//        if (keyComponents.length > 1) {
//            //
//            // This key has a namespace preceding it
//            //
//            String prefix = keyComponents[0];
//
//            //
//            // The prefix represents a modeshape prefix so need the original uri
//            // from the modeshape namespace registry
//            //
//            String mURI = stmtOption.getSession().getNamespaceURI(prefix);
//            URI uri = null;
//            if (mURI != null) {
//                //
//                // Need to find the ddl prefix for this namespace URI
//                // Either a built-in namespace or custom namespace created with a SET NAMESPACE call
//                //
//                uri = findNamespace(mURI);
//            } else {
//                uri = namespaceMap.get(prefix);
//            }
//
//            if (uri != null)
//                key = uri.getPrefix() + COLON + keyComponents[1];
//
//            //
//            // If uri == null then the namespace has not been set so
//            // the colon is simply part of the name so leave the key intact
//            //
//        }
//
//        append(escapeSinglePart(key)).append(SPACE);
//        if (FALSE.equals(value) || TRUE.equals(value)) {
//            append(value);
//            return;
//        }
//
//        // Default to a string value which should be placed in quotes
//        append(QUOTE_MARK + value + QUOTE_MARK);
//    }
//
//    private void statementOptions(Node node, String prefix) throws RepositoryException {
//        Collection<Node> options = getChildren(node, StandardDdlLexicon.TYPE_STATEMENT_OPTION);
//        Iterator<Node> iterator = options.iterator();
//
//        boolean hasOptions = iterator.hasNext();
//        if (! hasOptions)
//            return;
//
//        append(prefix).append(OPTIONS).append(SPACE).append(OPEN_BRACKET);
//
//        while(iterator.hasNext()) {
//            Node option = iterator.next();
//            statementOption(option);
//
//            if (iterator.hasNext())
//                append(COMMA).append(SPACE);
//        }
//
//        append(CLOSE_BRACKET);
//    }
//
//    private void tableElement(Node tableElement, CreateObjectContext context) throws RepositoryException {
//        if (!hasMixinType(tableElement, TeiidDdlLexicon.CreateTable.TABLE_ELEMENT))
//            return;
//
//        append(NEW_LINE).append(TAB);
//
//        ColumnContext columnContext = createColumnContext(tableElement);
//
//        if (TableType.GLOBAL_TEMP_TABLE == context.getTableType() && columnContext.isAutoIncremented() &&
//            columnContext.isNotNull() &&
//            DataTypeName.INTEGER.equals(columnContext.getDataTypeName())) {
//            append(escapeSinglePart(tableElement.getName()));
//            append(SPACE);
//            append(SERIAL);
//        } else {
//            column(tableElement, columnContext, true, true);
//
//            if (columnContext.isAutoIncremented()) {
//                append(SPACE).append(AUTO_INCREMENT);
//            }
//        }
//
//        columnDefault(tableElement, columnContext);
//
//        // options
//        statementOptions(tableElement, SPACE);
//    }
//
//    private Node referenceByUuid(Collection<Node> nodes, Value uuidValue) throws ValueFormatException, RepositoryException {
//        String reference  = uuidValue.getString();
//        Node referenceNode = null;
//
//        for (Node node : nodes) {
//            Property uuidProp = property(node, JcrLexicon.UUID.getString());
//            if (uuidProp == null)
//                continue;
//
//            String uuid = toString(uuidProp);
//            if (! reference.equals(uuid))
//                continue;
//
//            referenceNode = node;
//            break;
//        }
//
//        return referenceNode;
//    }
//
//    private void constraint(Node constraint, String expectedType) throws RepositoryException {
//        if (!hasMixinType(constraint, expectedType))
//            return;
//
//        append(COMMA).append(NEW_LINE).append(TAB);
//
//        Node parentTabulation = constraint.getParent();
//        Collection<Node> siblingColumnNodes = getChildren(parentTabulation, TeiidDdlLexicon.CreateTable.TABLE_ELEMENT);
//
//        Node parentSchema = parentTabulation.getParent();
//        Collection<Node> tabulations = getChildren(parentSchema);
//
//        Property typeProp = property(constraint, TeiidDdlLexicon.Constraint.TYPE);
//        append(toString(typeProp));
//
//        Property refProp = property(constraint, TeiidDdlLexicon.Constraint.REFERENCES);
//        if (refProp != null) {
//            List<Value> values = multiPropertyValues(refProp);
//
//            append(OPEN_BRACKET);
//
//            Iterator<Value> valIter = values.iterator();
//            while(valIter.hasNext()) {
//                Value refValue = valIter.next();
//
//                Node keyColumn = referenceByUuid(siblingColumnNodes, refValue);
//                append(escapeSinglePart(keyColumn.getName()));
//
//                if (valIter.hasNext())
//                    append(COMMA).append(SPACE);
//            }
//
//            append(CLOSE_BRACKET);
//        }
//
//        if (TeiidDdlLexicon.Constraint.FOREIGN_KEY_CONSTRAINT.equals(expectedType)) {
//
//            append(SPACE).append(REFERENCES);
//
//            Property tableRefProp = property(constraint, TeiidDdlLexicon.Constraint.TABLE_REFERENCE);
//            Node tableReference = null;
//            Collection<Node> tableRefChildren = Collections.emptyList();
//
//            if (tableRefProp != null) {
//                tableReference = referenceByUuid(tabulations, tableRefProp.getValue());
//                tableRefChildren = getChildren(tableReference, TeiidDdlLexicon.CreateTable.TABLE_ELEMENT);
//                append(SPACE).append(tableReference == null ? undefined() : tableReference.getName());
//            }
//
//            Property tableRefRefsProp = property(constraint, TeiidDdlLexicon.Constraint.TABLE_REFERENCE_REFERENCES);
//            if (tableRefRefsProp != null) {
//                append(SPACE);
//
//                List<Value> tableRefs = multiPropertyValues(tableRefRefsProp);
//                append(OPEN_BRACKET);
//
//                Iterator<Value> valIter = tableRefs.iterator();
//                while(valIter.hasNext()) {
//                    Value refValue = valIter.next();
//                    Node refColumn = referenceByUuid(tableRefChildren, refValue);
//                    append(refColumn == null ? undefined() : escapeSinglePart(refColumn.getName()));
//
//                    if (valIter.hasNext())
//                        append(COMMA).append(SPACE);
//                }
//
//                append(CLOSE_BRACKET);
//            }
//        }
//
//        // options
//        statementOptions(constraint, SPACE);
//    }
//
//    private void constraints(Node node) throws RepositoryException {
//        Collection<Node> teConstraints = getChildren(node, TeiidDdlLexicon.Constraint.TABLE_ELEMENT);
//        for (Node teConstraint : teConstraints) {
//            constraint(teConstraint, TeiidDdlLexicon.Constraint.TABLE_ELEMENT);
//        }
//
//        Collection<Node> indexConstraints = getChildren(node, TeiidDdlLexicon.Constraint.INDEX_CONSTRAINT);
//        for (Node indexConstraint : indexConstraints) {
//            constraint(indexConstraint, TeiidDdlLexicon.Constraint.INDEX_CONSTRAINT);
//        }
//
//        Collection<Node> fkConstraints = getChildren(node, TeiidDdlLexicon.Constraint.FOREIGN_KEY_CONSTRAINT);
//        for (Node fkConstraint : fkConstraints) {
//            constraint(fkConstraint, TeiidDdlLexicon.Constraint.FOREIGN_KEY_CONSTRAINT);
//        }
//    }
//
//    private void addTableBody(Node node, CreateObjectContext context) throws RepositoryException {
//        String name = escapeSinglePart(node.getName());
//        append(name);
//
//        Collection<Node> tableElements = getChildren(node, TeiidDdlLexicon.CreateTable.TABLE_ELEMENT);
//        if (! tableElements.isEmpty()) {
//            append(SPACE);
//            append(OPEN_BRACKET);
//
//            Iterator<Node> iterator = tableElements.iterator();
//            while(iterator.hasNext()) {
//                Node tableElement = iterator.next();
//                tableElement(tableElement, context);
//
//                if (iterator.hasNext())
//                    append(COMMA);
//            }
//
//            constraints(node);
//            append(NEW_LINE);
//            append(CLOSE_BRACKET);
//        }
//
//        // options
//        statementOptions(node, SPACE);
//    }
//
//    private String schemaElementType(Node node) throws Exception {
//        Property property = property(node, TeiidDdlLexicon.SchemaElement.TYPE);
//        if (property == null)
//            return null;
//
//        return toString(property);
//    }
//
//    private void tabulation(Node tabulation, CreateObjectContext context) throws Exception {
//        append(SPACE);
//
//        addTableBody(tabulation, context);
//
//        if (TableType.GLOBAL_TEMP_TABLE != context.getTableType()) {
//            if (context.isVirtual()) {
//                TeiidSqlNodeVisitor visitor = new TeiidSqlNodeVisitor(getVersion());
//                String teiidSql = visitor.getTeiidSql(tabulation);
//                append(NEW_LINE).append(AS).append(NEW_LINE).append(teiidSql);
//            }
//            append(SEMI_COLON);
//        }
//    }
//
//    private void table(Node table) throws Exception {
//        if (! includeTables)
//            return;
//
//        if (!hasMixinType(table, TeiidDdlLexicon.CreateTable.TABLE_STATEMENT))
//            return;
//
//        append(NEW_LINE);
//
//        CreateObjectContext context = new CreateObjectContext();
//        context.setPhysical(FOREIGN.equals(schemaElementType(table)));
//
//        append(CREATE).append(SPACE);
//
//        if (context.isPhysical()) {
//            context.setTableType(TableType.TABLE);
//            append(FOREIGN).append(SPACE).append(TABLE);
//        }
//        else {
//            context.setTableType(TableType.GLOBAL_TEMP_TABLE);
//            append(GLOBAL).append(SPACE).append(TEMPORARY).append(SPACE).append(TABLE);
//        }
//
//        tabulation(table, context);
//    }
//
//    private void view(Node view) throws Exception {
//        if (! includeTables)
//            return;
//
//        if (!hasMixinType(view, TeiidDdlLexicon.CreateTable.VIEW_STATEMENT))
//            return;
//
//        append(NEW_LINE);
//
//        CreateObjectContext context = new CreateObjectContext();
//        context.setVirtual(true);
//        context.setTableType(TableType.VIEW);
//
//        append(CREATE).append(SPACE).append(VIEW);
//
//        tabulation(view, context);
//    }
//
//    private void procedureParameter(Node parameter) throws RepositoryException {
//        if (!hasMixinType(parameter, TeiidDdlLexicon.CreateProcedure.PARAMETER))
//            return;
//
//        Property typeProp = property(parameter, TeiidDdlLexicon.CreateProcedure.PARAMETER_TYPE);
//        String paramType = toString(typeProp);
//        append(paramType).append(SPACE);
//
//        ColumnContext columnContext = createColumnContext(parameter);
//        column(parameter, columnContext, true, true);
//
//        Property returnProp = property(parameter, TeiidDdlLexicon.CreateProcedure.PARAMETER_RESULT_FLAG);
//        boolean returnFlag = returnProp == null ? false : returnProp.getBoolean();
//        if (returnFlag) {
//            append(SPACE).append(NonReserved.RESULT);
//        }
//
//        columnDefault(parameter, columnContext);
//
//        // Options
//        statementOptions(parameter, SPACE);
//    }
//
//    private void procedureParameters(Node procedure) throws RepositoryException {
//        Collection<Node> parameters = getChildren(procedure, TeiidDdlLexicon.CreateProcedure.PARAMETER);
//        Iterator<Node> paramIter = parameters.iterator();
//        while(paramIter.hasNext()) {
//            Node parameter = paramIter.next();
//
//            procedureParameter(parameter);
//
//            if (paramIter.hasNext())
//                append(COMMA).append(SPACE);
//        }
//    }
//
//    private void procedure(Node procedure) throws Exception {
//        if (! includeProcedures)
//            return;
//
//        if (!hasMixinType(procedure, TeiidDdlLexicon.CreateProcedure.PROCEDURE_STATEMENT))
//            return;
//
//        append(NEW_LINE);
//
//        CreateObjectContext context = new CreateObjectContext();
//        context.setPhysical(FOREIGN.equals(schemaElementType(procedure)));
//
//        append(CREATE).append(SPACE);
//
//        if (context.isVirtual())
//            append(VIRTUAL);
//        else
//            append(FOREIGN);
//
//        append(SPACE).append(PROCEDURE).append(SPACE).append(escapeSinglePart(procedure.getName()));
//        append(OPEN_BRACKET);
//        procedureParameters(procedure);
//        append(CLOSE_BRACKET);
//
//        Node resultSet = procedure.getNode(TeiidDdlLexicon.CreateProcedure.RESULT_SET);
//        if (resultSet != null) {
//            append(SPACE).append(RETURNS).append(SPACE).append(TABLE).append(SPACE);
//
//            append(OPEN_BRACKET);
//            Collection<Node> resultColumns = getChildren(resultSet, TeiidDdlLexicon.CreateProcedure.RESULT_COLUMN);
//            Iterator<Node> iterator = resultColumns.iterator();
//            while (iterator.hasNext()) {
//                Node resultColumn = iterator.next();
//                ColumnContext columnContext = createColumnContext(resultColumn);
//                column(resultColumn, columnContext, true, true);
//
//                if (iterator.hasNext())
//                    append(COMMA).append(SPACE);
//            }
//            append(CLOSE_BRACKET);
//        }
//
//        //options
//        statementOptions(procedure, NEW_LINE);
//
//        //block
//        if (context.isVirtual()) {
//            append(NEW_LINE).append(AS).append(NEW_LINE);
//            TeiidSqlNodeVisitor visitor = new TeiidSqlNodeVisitor(getVersion());
//            String teiidSql = visitor.getTeiidSql(procedure);
//            append(teiidSql);
//            append(SEMI_COLON);
//        }
//    }
//
//    private void functionParameter(Node functionParameter) throws Exception {
//        Property typeProp = property(functionParameter, TeiidDdlLexicon.CreateProcedure.PARAMETER_TYPE);
//        String paramType = toString(typeProp);
//
//        if (VARIADIC.equals(paramType))
//            append(VARIADIC).append(SPACE);
//
//        ColumnContext columnContext = createColumnContext(functionParameter);
//        column(functionParameter, columnContext, true, true);
//    }
//
//    private void functionParameters(Node function) throws Exception {
//        Collection<Node> parameters = getChildren(function, TeiidDdlLexicon.CreateProcedure.PARAMETER);
//        Iterator<Node> paramIter = parameters.iterator();
//        while(paramIter.hasNext()) {
//            Node parameter = paramIter.next();
//
//            functionParameter(parameter);
//
//            if (paramIter.hasNext())
//                append(COMMA).append(SPACE);
//        }
//    }
//
//    private void function(Node function) throws Exception {
//        if (! includeFunctions)
//            return;
//
//        if (!hasMixinType(function, TeiidDdlLexicon.CreateProcedure.FUNCTION_STATEMENT))
//            return;
//
//        append(CREATE).append(SPACE);
//
//        CreateObjectContext context = new CreateObjectContext();
//        context.setPhysical(FOREIGN.equals(schemaElementType(function)));
//
//        if (context.isPhysical())
//            append(FOREIGN);
//        else
//            append(VIRTUAL);
//
//        append(SPACE).append(FUNCTION).append(SPACE).append(escapeSinglePart(function.getName()));
//
//        append(OPEN_BRACKET);
//        functionParameters(function);
//        append(CLOSE_BRACKET);
//
//        Node resultSet = function.getNode(TeiidDdlLexicon.CreateProcedure.RESULT_SET);
//        if (resultSet != null) {
//            ColumnContext columnContext = createColumnContext(resultSet);
//            append(SPACE).append(RETURNS).append(SPACE).
//            append(columnContext.getDataType());
//        }
//
//        //options
//        statementOptions(function, NEW_LINE);
//        append(SEMI_COLON);
//    }
//
//    @Override
//    public void visit(Node node) throws RepositoryException {
//        if (node == null)
//            return;
//
//        //
//        // Teiid DDL Nodes
//        //
//        NodeType tddlMixinType = findMixinTypeByNamespace(node, TeiidDdlLexicon.Namespace.PREFIX);
//        MixinTypeName typeName = MixinTypeName.findName(tddlMixinType);
//        try {
//            switch (typeName) {
//                case CREATE_TABLE:
//                    table(node);
//                    append(NEW_LINE);
//                    break;
//                case CREATE_VIEW:
//                    view(node);
//                    append(NEW_LINE);
//                    break;
//                case OPTION_NAMESPACE:
//                    optionNamespace(node);
//                    append(NEW_LINE);
//                    break;
//                case CREATE_PROCEDURE:
//                    procedure(node);
//                    append(NEW_LINE);
//                    break;
//                case CREATE_FUNCTION:
//                    function(node);
//                    append(NEW_LINE);
//                    break;
//                case UNKNOWN:
//                default:
//                    // Not a node we are interested in but may contain such nodes
//                    visitChildren(node);
//            }
//        } catch (Exception ex) {
//            throw new RepositoryException(ex);
//        }
//    }
//
//    @Override
//    public void visit(Property property) {
//        // Not used
//    }
//}
