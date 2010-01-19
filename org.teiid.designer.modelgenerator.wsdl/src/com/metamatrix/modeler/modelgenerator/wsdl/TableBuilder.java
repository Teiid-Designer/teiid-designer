/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.xsd.XSDSchema;
import org.jdom.Namespace;
import com.metamatrix.core.log.Logger;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Message;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Part;
import com.metamatrix.modeler.modelgenerator.wsdl.schema.extensions.SOAPSchemaProcessor;
import com.metamatrix.modeler.modelgenerator.wsdl.util.NameUtil;
import com.metamatrix.modeler.modelgenerator.xml.modelextension.impl.XMLRequestResponseExtensionManagerImpl;
import com.metamatrix.modeler.schema.tools.model.jdbc.Column;
import com.metamatrix.modeler.schema.tools.model.jdbc.DataType;
import com.metamatrix.modeler.schema.tools.model.jdbc.Table;
import com.metamatrix.modeler.schema.tools.model.jdbc.internal.ColumnImpl;
import com.metamatrix.modeler.schema.tools.model.jdbc.internal.DataTypeImpl;
import com.metamatrix.modeler.schema.tools.model.jdbc.internal.TableImpl;
import com.metamatrix.modeler.schema.tools.model.schema.Relationship;
import com.metamatrix.modeler.schema.tools.model.schema.RootElement;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.processing.RelationshipProcessor;
import com.metamatrix.modeler.schema.tools.processing.RelationshipProcessorFactory;
import com.metamatrix.modeler.schema.tools.processing.SchemaProcessingException;
import com.metamatrix.modeler.schema.tools.processing.SchemaProcessor;

public class TableBuilder {

    public static final String RESPONSE_IN = "ResponseIn"; //$NON-NLS-1$
    public static final String RESPONSE_OUT = "ResponseOut"; //$NON-NLS-1$
    public static final String XML_SCHEMA_URI = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$
    public static final String MMID = "mmid"; //$NON-NLS-1$

    XSDSchema[] m_schemas;
    SchemaModel m_schemaModel;
    private Model m_model;

    public Map getNamespaces() {
        return m_model.getNamespaces();
    }

    public Collection createTables( Operation[] opers,
                                    Logger logger ) throws ModelBuildingException {

        // This method has the potential of being VERY slow since we do a LOT
        // of looping over collections (operations, messages, parts, elements,
        // tables, columns).
        // If performance becomes an issue when building the model we may
        // need to revisit this or even worse
        // add a progress monitor into the mix.

        HashMap tables = new HashMap(opers.length * 2);
        // walk up to get the schemas and namespaces
        m_model = opers[0].getBinding().getPort().getService().getModel();
        m_schemas = m_model.getSchemas();

        try {
            // process the schemas and get the schema model
            SchemaProcessor processor = new SOAPSchemaProcessor(logger, null);
            processor.representTypes(true);
            processor.setNamespaces(this.m_model.getNamespaces());
            processor.processSchemas(m_schemas);

            m_schemaModel = processor.getSchemaModel();
            // create the relationship processor that builds the tables
            final int maxOccursMax = 4;
            final int maxParents = Integer.MAX_VALUE;
            final int maxFields = Integer.MAX_VALUE;

            // get the list of messages from the operations
            ArrayList messages = getMessages(opers);

            for (Iterator messageIter = messages.iterator(); messageIter.hasNext();) {
                Message message = (Message)messageIter.next();

                // create the base table for the message
                Table baseTable = createBaseTable(message);
                String soapAction = null;
                if (message.isRequest()) {
                    soapAction = message.getOperation().getSOAPAction();
                }
                SoapBindingInfo info = null;
                if (message.getOperation() != null) {
                    info = message.getOperation().getSoapBindingInfo();
                }
                SOAPTable baseTableWrapper = new SOAPTableImpl(baseTable, message.isRequest(), soapAction, info);
                // add it to the return list
                String tableName = baseTable.getCatalog() + "." + baseTable.getName(); //$NON-NLS-1$
                tables.put(tableName, baseTableWrapper);

                // loop over the message parts
                Part[] parts = message.getParts();

                for (int i = 0; i < parts.length; i++) {
                    Part part = parts[i];
                    String elementNamespace;
                    String elementName;
                    if (part.isElement()) {
                        elementName = part.getElementName();
                        elementNamespace = part.getElementNamespace();
                    } else {
                        elementName = part.getTypeName(); // get the name here also
                        elementNamespace = part.getTypeNamespace();
                    }

                    if (elementNamespace.equalsIgnoreCase(XML_SCHEMA_URI)) {
                        Column add = createColumn(part);
                        baseTable.addColumn(add);
                    } else {
                        // since the relationship processor changes the model we need to
                        // clone the schema model each time
                        SchemaModel clone = m_schemaModel.copy();
                        String topName = null;
                        if (part.isElement()) {
                            // now find the element you want
                            RootElement theElement = null;
                            List roots = m_schemaModel.getPotentialRootElements();
                            for (Iterator rootIter = roots.iterator(); rootIter.hasNext();) {
                                RootElement root = (RootElement)rootIter.next();

                                if ((root == null && elementNamespace.trim().length() == 0)
                                    || (root != null && root.getNamespace().equalsIgnoreCase(elementNamespace))) {
                                    if (root.getName().equalsIgnoreCase(elementName)) {
                                        theElement = root;
                                        break;
                                    }
                                }
                            }

                            // prepare to use the RelationshipProcessor

                            // create a hash containing my element
                            HashSet eSet = new HashSet(1);
                            eSet.add(theElement);

                            // now set that element as the only root element
                            clone.setSelectedRootElements(eSet);
                            topName = theElement.getName();
                        } else {
                            clone.setTypeAsRoot(elementName, elementNamespace);
                            topName = elementName;
                        }
                        // create the tables
                        RelationshipProcessor relProc;
                        if (message.isRequest()) {
                            relProc = RelationshipProcessorFactory.getRequestProcessor();

                        } else {
                            relProc = RelationshipProcessorFactory.getQueryOptimizingProcessor(maxOccursMax,
                                                                                               maxParents,
                                                                                               maxFields);
                        }

                        relProc.calculateRelationshipTypes(clone);

                        // get them back as a list
                        List tabList = clone.getTables();

                        // get the root table
                        SOAPTable top = new SOAPTableImpl(getRootTable(tabList, topName));

                        top.setCatalog(baseTable.getCatalog());
                        top.setSchema(baseTable.getSchema());
                        top.setName(NameUtil.normalizeNameForRelationalTable(top.getName()));
                        Column[] columns = top.getColumns();
                        for (int col = 0; col < columns.length; col++) {
                            Column column = columns[col];
                            column.setName(NameUtil.normalizeNameForRelationalTable(column.getName()));
                        }

                        // If the top table is an instance of a SOAP Array, it needs special handling.
                        // I think the test below should only be true for SOAP Arrays but I could be wrong.
                        if (!message.isRequest() && (top.getMaxOccurs() == -1 || top.getMaxOccurs() > 1)) {
                            // use as a child to the response and create a keyed relation.
                            createKeyInChild(top, baseTable.getName());
                            addMMIdColumn(baseTable, ""); //$NON-NLS-1$
                            top.setName(part.getName());
                            StringBuffer combinedXPath = new StringBuffer(baseTable.getOutputXPath());
                            if (!baseTable.getOutputXPath().endsWith("/")) { //$NON-NLS-1$
                                combinedXPath.append("/"); //$NON-NLS-1$
                            }
                            combinedXPath.append(part.getName());
                            combinedXPath.append("/*"); //$NON-NLS-1$
                            String newXPathPrefix = combinedXPath.toString();
                            top.setOutputXPath(newXPathPrefix);
                            Table[] table = {top};
                            processChildren(table, message.isRequest(), tables, baseTable, newXPathPrefix);
                        } else {
                            // fold top into baseTable
                            if (!part.isElement()) {
                                String inputXPath = top.getInputXPath();
                                if (inputXPath.indexOf('/') != 0) {
                                    inputXPath = "/" + inputXPath; //$NON-NLS-1$
                                }
                                top.setInputXPath("/" + part.getName() + inputXPath); //$NON-NLS-1$
                            }
                            mergeTables(baseTable, top, message.isRequest());
                            processChildren(top.getChildTables(), message.isRequest(), tables, baseTable, null);
                        }
                    }
                }
            }
        } catch (SchemaProcessingException se) {
            throw new ModelBuildingException(se);
        }
        return tables.values();
    }

    private void createKeyInChild( Table table,
                                   String parentName ) {
        String childColumnName = parentName + "_" + MMID; //$NON-NLS-1$
        Column keyColumn = new ColumnImpl();
        keyColumn.setName(childColumnName);
        keyColumn.setDataType(new DataTypeImpl("string", XML_SCHEMA_URI)); //$NON-NLS-1$
        keyColumn.setRole(XMLRequestResponseExtensionManagerImpl.DATA_ROLE);
        keyColumn.setOutputXPath("../../@com.metamatrix.xml.xpathpart"); //$NON-NLS-1$
        keyColumn.setIsForeignKey(true);
        table.addColumn(keyColumn);
    }

    private void processChildren( Table[] tabs,
                                  boolean isRequest,
                                  HashMap tables,
                                  Table baseTable,
                                  String xPathPrefix ) {
        for (int i = 0; i < tabs.length; i++) {
            Table table = tabs[i];
            String tableName = baseTable.getCatalog() + "." + baseTable.getSchema() + "." + table.getName(); //$NON-NLS-1$ //$NON-NLS-2$
            tableName = NameUtil.normalizeNameForRelationalTable(tableName);
            // did we already process this table?
            if (tables.get(tableName) != null) continue;
            table.setCatalog(baseTable.getCatalog());
            table.setSchema(baseTable.getSchema());
            table.setName(NameUtil.normalizeNameForRelationalTable(table.getName()));
            checkForForeignKeys(baseTable, table, tables);
            mergeNamespacePrefixes(table, baseTable);
            Column[] columns = table.getColumns();
            for (int col = 0; col < columns.length; col++) {
                Column column = columns[col];
                column.setName(NameUtil.normalizeNameForRelationalTable(column.getName()));
                if (isRequest) {
                    if (!column.getName().equals(MMID)) {
                        setInputColumnValues(column.getOutputXPath(), column);
                    }
                }
            }
            if (!isRequest) {
                addResponseInColumn(table);
            }
            tables.put(tableName, new SOAPTableImpl(table, isRequest, null, null));
            processChildren(table, tables, isRequest, baseTable, xPathPrefix);
        }
    }

    private void processChildren( Table table,
                                  HashMap tables,
                                  boolean isRequest,
                                  Table baseTable,
                                  String xPathPrefix ) {
        Table[] tabs = table.getChildTables();
        for (int i = 0; i < tabs.length; i++) {
            String tableName = table.getCatalog() + "." + table.getSchema() + "." + tabs[i].getName(); //$NON-NLS-1$ //$NON-NLS-2$
            tableName = NameUtil.normalizeNameForRelationalTable(tableName);
            checkForForeignKeys(table, tabs[i], tables);
            // did we already process the table?
            if (tables.get(tableName) != null) continue;
            if (null != xPathPrefix) {
                String currentXPath = tabs[i].getOutputXPath();
                if (currentXPath.charAt(0) != '/') {
                    tabs[i].setOutputXPath(xPathPrefix + "/" + currentXPath); //$NON-NLS-1$
                } else {
                    tabs[i].setOutputXPath(xPathPrefix + currentXPath);
                }
            }
            tabs[i].setName(NameUtil.normalizeNameForRelationalTable(tabs[i].getName()));
            tabs[i].setCatalog(table.getCatalog());
            tabs[i].setSchema(table.getSchema());
            mergeNamespacePrefixes(table, baseTable);
            tables.put(tableName, new SOAPTableImpl(tabs[i], isRequest, null, null));
            Column[] columns = tabs[i].getColumns();
            for (int col = 0; col < columns.length; col++) {
                Column column = columns[col];
                column.setName(NameUtil.normalizeNameForRelationalTable(column.getName()));
            }
            if (!isRequest) {
                addResponseInColumn(tabs[i]);
            }
            processChildren(tabs[i], tables, isRequest, table, xPathPrefix);
        }
    }

    private Table getRootTable( List tabList,
                                String name ) {
        Table tab = null;
        for (Iterator iter = tabList.iterator(); iter.hasNext();) {
            Table temp = (Table)iter.next();
            String tempName = temp.getName();
            if (tempName.indexOf('(') != -1) {
                tempName = tempName.substring(0, tempName.indexOf('('));
            }
            if (tempName.equalsIgnoreCase(name)) {
                tab = temp;
                break;
            }
        }
        return tab;
    }

    private void mergeTables( Table baseTable,
                              Table toMerge,
                              boolean isRequest ) {

        mergeNamespacePrefixes(baseTable, toMerge);

        // columns
        String inputXpathPrepend = toMerge.getInputXPath();
        String outputXpathPrepend = toMerge.getOutputXPath();
        Column[] cols = toMerge.getColumns();

        // is the only column mmid?
        if (isRequest && cols.length == 1) {
            // must be an empty element - create a column that allows for empty
            Column empty = new ColumnImpl();
            empty.setName(NameUtil.normalizeNameForRelationalTable(toMerge.getName()));
            empty.setIsInputParameter(true);
            empty.setIsRequiredValue(false);
            empty.setInputXPath(toMerge.getOutputXPath());
            empty.setDataType(new DataTypeImpl("string", XML_SCHEMA_URI)); //$NON-NLS-1$
            baseTable.addColumn(empty);
        }

        for (int i = 0; i < cols.length; i++) {
            Column source = cols[i];
            if (!source.getName().equals(MMID)) {
                Column candidate = copyColumn(inputXpathPrepend, outputXpathPrepend, source, isRequest);
                baseTable.addColumn(candidate);
            } else if (!isRequest) {
                this.addMMIdColumn(baseTable, outputXpathPrepend);
            }
        }
        // This object is added so that in later processing we can generate the
        // XPath to the parent's key. Without it we have lost the connection: more hackery for defect 23639
        baseTable.setElement(toMerge.getElement());
        baseTable.setBase(true);
    }

    public static void mergeNamespacePrefixes( Table toTable,
                                               Table fromTable ) {
        Namespace[] ns = extractNamespaces(fromTable.getNamespaceDeclaration());

        Namespace[] existing = extractNamespaces(toTable.getNamespaceDeclaration());

        for (int i = 0; i < ns.length; i++) {

            // ensure its not already there
            // TODO: if the same prefix is declared for different uri's were boned.
            String nsUri = ns[i].getURI();
            String nsPrefix = ns[i].getPrefix();
            boolean already = false;
            for (int j = 0; j < existing.length; j++) {
                if (existing[j].getURI().equals(nsUri) && existing[j].getPrefix().equals(nsPrefix)) {
                    already = true;
                    break;
                }
            }
            if (!already) toTable.addNamespace(ns[i]);
        }
    }

    private Column copyColumn( String inputXpathPrepend,
                               String outputXpathPrepend,
                               Column source,
                               boolean isRequest ) {
        Column candidate = new ColumnImpl();
        candidate.setName(NameUtil.normalizeNameForRelationalTable(source.getName()));
        candidate.setDataType(source.getDataType());
        candidate.setMultipleValues(source.getMultipleValues());
        // extension properties
        // these need to be factored out later
        if (isRequest) {
            // have to use output xpath since input xpath is not populated.
            if (inputXpathPrepend.lastIndexOf('/') == inputXpathPrepend.length() - 1) {
                setInputColumnValues(inputXpathPrepend + source.getOutputXPath(), candidate);
            } else {
                setInputColumnValues(inputXpathPrepend + "/" + source.getOutputXPath(), candidate); //$NON-NLS-1$
            }
        } else {
            candidate.setOutputXPath(outputXpathPrepend + "/" + source.getOutputXPath()); //$NON-NLS-1$
        }
        candidate.setRole(source.getRole());
        return candidate;
    }

    private void setInputColumnValues( String baseXpath,
                                       Column candidate ) {
        // TODO: currently only output xpaths are created so until that is fixed we have to
        // derive the output values

        if (baseXpath.startsWith("/")) baseXpath = baseXpath.substring(1); //$NON-NLS-1$

        // one thing we can be sure of
        candidate.setIsInputParameter(true);

        // we have to figure these out from the output xpath

        // look for @ symbol which indicates that the column is an attribute
        if (baseXpath.indexOf('@') >= 0) {
            // its an attribute
            candidate.setIsAttributeOfParent(true);
            int atIndex = baseXpath.indexOf('@');
            String attrName = baseXpath.substring(atIndex + 1);
            String parentXpath = "/"; //$NON-NLS-1$
            if (atIndex > 0) {
                // substring up to 2 spaces before the @ (removes /@)
                parentXpath = baseXpath.substring(0, baseXpath.indexOf('@') - 1);
                if (parentXpath.endsWith("/")) parentXpath = parentXpath.substring(0, parentXpath.length() - 1); //$NON-NLS-1$
            }
            candidate.setDataAttributeName(attrName);
            candidate.setInputXPath(parentXpath);
        } else {
            final String data = "/data()"; //$NON-NLS-1$
            final String text = "/text()"; //$NON-NLS-1$
            if (baseXpath.endsWith(data)) baseXpath = baseXpath.substring(0, baseXpath.length() - data.length());
            if (baseXpath.endsWith(text)) baseXpath = baseXpath.substring(0, baseXpath.length() - text.length());
            candidate.setInputXPath(baseXpath);
        }
        // should always be true for web services
        candidate.setIsRequiredValue(true);
    }

    private static Namespace[] extractNamespaces( String namespaceDeclaration ) {
        String temp = namespaceDeclaration;
        String[] decls = temp.split("xmlns"); //$NON-NLS-1$
        Namespace[] retVal = new Namespace[decls.length - 1];
        for (int i = 1; i < decls.length; i++) {
            if (decls[i].startsWith("=")) { //$NON-NLS-1$
                // TODO: how to handle default namespaces?
                String suffix = decls[i].substring(2, decls[i].length() - 1);
                retVal[i - 1] = Namespace.getNamespace(null, suffix);
            } else {
                // strip out the : before splitting
                String[] nsParts = decls[i].substring(1).split("="); //$NON-NLS-1$
                String prefix = nsParts[0].trim();
                String suffix = nsParts[1].trim().substring(1, nsParts[1].trim().length() - 1);
                retVal[i - 1] = Namespace.getNamespace(prefix, suffix);
            }
        }
        return retVal;
    }

    private ArrayList getMessages( Operation[] opers ) {
        ArrayList messages = new ArrayList(opers.length);
        for (int i = 0; i < opers.length; i++) {
            messages.add(opers[i].getInputMessage());
            messages.add(opers[i].getOutputMessage());

            // TODO: add this back in when the connector supports fault tables
            /*
            Fault[] faults = opers[i].getFaults();
            
            for (int j = 0; j < faults.length; j++) {				
            	messages.add(faults[j].getMessage());
            }
            */
        }
        return messages;
    }

    private Table createBaseTable( Message msg ) {
        Table table = new SOAPTableImpl();
        if (msg.isRequest() || msg.isResponse()) {
            table.setName(NameUtil.normalizeNameForRelationalTable(msg.getName()));
            table.setSchema(msg.getOperation().getName());
            table.setCatalog(msg.getOperation().getBinding().getPort().getService().getName());
        } else {
            table.setName(NameUtil.normalizeNameForRelationalTable(msg.getFault().getName()));
            table.setSchema(msg.getFault().getOperation().getName());
            table.setCatalog(msg.getFault().getOperation().getBinding().getPort().getService().getName());
        }
        String style = (msg.getOperation() == null) ? msg.getFault().getOperation().getBinding().getStyle() : msg.getOperation().getBinding().getStyle();
        if (style.equalsIgnoreCase("RPC")) { //$NON-NLS-1$
            String ns = ""; //$NON-NLS-1$
            if (msg.getNamespaceURI() != null && !msg.getNamespaceURI().trim().equals("")) { //$NON-NLS-1$
                // search for the prefix
                ns = findNamespacePrefix(msg.getNamespaceURI().trim()) + ":"; //$NON-NLS-1$
            }
            if (msg.isRequest()) {
                table.setInputXPath("/" + ns + table.getSchema()); //$NON-NLS-1$
                table.setOutputXPath("/"); //$NON-NLS-1$
            } else {
                table.setOutputXPath("/" + ns + table.getSchema() + "Response"); //$NON-NLS-1$  //$NON-NLS-2$
            }

        } else {
            table.setInputXPath("/"); //$NON-NLS-1$
            table.setOutputXPath("/"); //$NON-NLS-1$			
        }
        for (Iterator iter = m_model.getNamespaces().keySet().iterator(); iter.hasNext();) {
            String prefix = (String)iter.next();
            String uri = (String)m_model.getNamespaces().get(prefix);
            Namespace ns = Namespace.getNamespace(prefix, uri);
            table.addNamespace(ns);
        }
        if (msg.isResponse() || msg.isFault()) {
            addResponseInColumn(table);
        } else {
            addResponseOutColumn(table);
        }
        return table;

    }

    private void addMMIdColumn( Table table,
                                String prefix ) {
        Column col = new ColumnImpl();
        DataType type = new DataTypeImpl("string", XML_SCHEMA_URI); //$NON-NLS-1$
        col.setDataType(type);
        col.setName("mmid"); //$NON-NLS-1$
        col.setOutputXPath(prefix + "/@com.metamatrix.xml.xpathpart"); //$NON-NLS-1$
        col.setRole(XMLRequestResponseExtensionManagerImpl.DATA_ROLE);
        table.addColumn(col);
    }

    private void addResponseOutColumn( Table table ) {
        Column col = new ColumnImpl();
        DataType type = new DataTypeImpl("string", XML_SCHEMA_URI); //$NON-NLS-1$
        col.setDataType(type);
        col.setName(RESPONSE_OUT);
        col.setRole(XMLRequestResponseExtensionManagerImpl.RESPONSE_OUT_ROLE);
        table.addColumn(col);
    }

    private void addResponseInColumn( Table table ) {
        Column col = new ColumnImpl();
        DataType type = new DataTypeImpl("string", XML_SCHEMA_URI); //$NON-NLS-1$
        col.setDataType(type);
        col.setName(RESPONSE_IN);
        col.setRole(XMLRequestResponseExtensionManagerImpl.RESPONSE_IN_ROLE);
        col.setIsInputParameter(true);
        table.addColumn(col);
    }

    private Column createColumn( Part part ) {

        String name = (part.getElementName() == null) ? part.getName() : part.getElementName();

        Column col = new ColumnImpl();
        col.setName(NameUtil.normalizeNameForRelationalTable(name));
        if (part.getElementNamespace() != null) {
            m_model.addNamespaceToMap(part.getElementNamespace());
        }
        if (part.getMessage().isRequest()) {
            col.setIsInputParameter(true);
            col.setInputXPath(name);
        } else {
            col.setOutputXPath(name + "/text()"); //$NON-NLS-1$	
        }

        // The part defined by an element or a type?
        if (part.getTypeName() != null) {
            col.setDataType(new DataTypeImpl(part.getTypeName(), part.getTypeNamespace()));
        } else {
            col.setDataType(new DataTypeImpl(part.getElementName(), part.getElementNamespace()));
        }
        return col;
    }

    public String findNamespacePrefix( String namespaceURI ) {
        String prefix = null;
        for (Iterator iter = m_model.getNamespaces().keySet().iterator(); iter.hasNext();) {
            String key = (String)iter.next();
            if (m_model.getNamespaces().get(key).equals(namespaceURI)) {
                prefix = key;
            }
        }
        if (prefix == null) {
            m_model.addNamespaceToMap(namespaceURI);
            prefix = findNamespacePrefix(namespaceURI);
        }
        return prefix;
    }

    private void checkForForeignKeys( Table parent,
                                      Table child,
                                      HashMap tables ) {
        switch (child.getRelationToParent()) {
            case Relationship.KEY_IN_CHILD:
                String childColumnName = parent.getName() + "_" + MMID; //$NON-NLS-1$
                // TODO: how do we calculate the parent's mmid xpath?
                Column keyColumn = new ColumnImpl();
                keyColumn.setName(childColumnName);
                keyColumn.setDataType(new DataTypeImpl("string", XML_SCHEMA_URI)); //$NON-NLS-1$
                keyColumn.setRole(XMLRequestResponseExtensionManagerImpl.DATA_ROLE);
                keyColumn.setOutputXPath(getChildKeyColumnXPath(parent, child));
                keyColumn.setIsForeignKey(true);
                child.addColumn(keyColumn);
                break;
            case Relationship.KEY_IN_PARENT_SINGLE:
                // TODO: how do we calculate the child's mmid xpath?
                String parentColumnName = child.getName() + "_" + MMID; //$NON-NLS-1$
                String parentOutputXPath = ""; //$NON-NLS-1$
                Column parentKeyColumn = new ColumnImpl();
                parentKeyColumn.setName(parentColumnName);
                parentKeyColumn.setDataType(new DataTypeImpl("string", XML_SCHEMA_URI)); //$NON-NLS-1$
                parentKeyColumn.setRole(XMLRequestResponseExtensionManagerImpl.DATA_ROLE);
                parentKeyColumn.setOutputXPath(parentOutputXPath);
                parentKeyColumn.setIsForeignKey(true);
                parent.addColumn(parentKeyColumn);
                break;
            case Relationship.RELATIONSHIP_TABLE:
                if (tables.containsKey(parent.getName() + "_" + child.getName())) { //$NON-NLS-1$
                    break; // don't create more than one relationship table between the same tables.
                }
                Table relTable = new SOAPTableImpl();
                relTable.setCatalog(parent.getCatalog());
                relTable.setSchema(parent.getSchema());
                relTable.setName(parent.getName() + "_" + child.getName()); //$NON-NLS-1$
                relTable.setOutputXPath("/"); //$NON-NLS-1$

                String relParentColumnName = parent.getName() + "_" + MMID; //$NON-NLS-1$
                String relParentOutputXPath = parent.getOutputXPath() + "/@com.metamatrix.xml.xpathpart"; //$NON-NLS-1$
                Column relParentKeyColumn = new ColumnImpl();
                relParentKeyColumn.setName(relParentColumnName);
                relParentKeyColumn.setDataType(new DataTypeImpl("string", XML_SCHEMA_URI)); //$NON-NLS-1$
                relParentKeyColumn.setRole(XMLRequestResponseExtensionManagerImpl.DATA_ROLE);
                relParentKeyColumn.setOutputXPath(relParentOutputXPath);
                relParentKeyColumn.setIsForeignKey(true);
                relTable.addColumn(relParentKeyColumn);

                String relChildColumnName = child.getName() + "_" + MMID; //$NON-NLS-1$
                String relChildOutputXPath = child.getOutputXPath() + "/@com.metamatrix.xml.xpathpart"; //$NON-NLS-1$
                Column relChildKeyColumn = new ColumnImpl();
                relChildKeyColumn.setName(relChildColumnName);
                relChildKeyColumn.setDataType(new DataTypeImpl("string", XML_SCHEMA_URI)); //$NON-NLS-1$
                relChildKeyColumn.setRole(XMLRequestResponseExtensionManagerImpl.DATA_ROLE);
                relChildKeyColumn.setOutputXPath(relChildOutputXPath);
                relChildKeyColumn.setIsForeignKey(true);
                relTable.addColumn(relChildKeyColumn);
                tables.put(relTable.getName(), new SOAPTableImpl(relTable, false, null, null));

                break;
            case Relationship.KEY_IN_PARENT_MULTIPLE:
            case Relationship.MERGE_IN_PARENT_MULTIPLE:
            case Relationship.MERGE_IN_PARENT_SINGLE:
            case Relationship.UNBOUNDED:
            default:
                // should probably throw something here
                // since these relationships would indicate an
                // error in the schema processing
        }
    }

    private String getChildKeyColumnXPath( Table parent,
                                           Table child ) {
        Relationship relationship = ((TableImpl)child).getRelationObjectToParent(parent);
        StringBuffer result = new StringBuffer();
        result.append(relationship.getParentRelativeXpath());
        result.append("/@com.metamatrix.xml.xpathpart"); //$NON-NLS-1$
        return result.toString();
    }
}
