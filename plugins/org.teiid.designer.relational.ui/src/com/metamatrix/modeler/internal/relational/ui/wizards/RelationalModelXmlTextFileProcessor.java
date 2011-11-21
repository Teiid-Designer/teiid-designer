/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relational.ui.wizards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.RelationalPlugin;
import org.teiid.designer.relational.model.RelationalAccessPattern;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalForeignKey;
import org.teiid.designer.relational.model.RelationalIndex;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalModelFactory;
import org.teiid.designer.relational.model.RelationalParameter;
import org.teiid.designer.relational.model.RelationalPrimaryKey;
import org.teiid.designer.relational.model.RelationalProcedure;
import org.teiid.designer.relational.model.RelationalProcedureResultSet;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.model.RelationalUniqueConstraint;
import org.teiid.designer.relational.model.RelationalView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.relational.ui.textimport.RelationalRowFactory;
import com.metamatrix.modeler.relational.ui.UiConstants;
import com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractObjectProcessor;
import com.metamatrix.modeler.tools.textimport.ui.wizards.IRowObject;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

/**
 * Creates relational model contents based on an input <code>RelationalModel</code> and a target <code>ModelResource</code>
 * 
 * 
 */
public class RelationalModelXmlTextFileProcessor extends AbstractObjectProcessor implements RelationalConstants {
    //============================================================================================================================
    // Static Constants
    
    static final String TAG_RELATIONAL_MODEL  = "relational-model";  //$NON-NLS-1$
    static final String TAG_TABLE  = "table";  //$NON-NLS-1$
    static final String TAG_COLUMN  = "column";  //$NON-NLS-1$
    static final String TAG_VIEW  = "view";  //$NON-NLS-1$
    static final String TAG_PRIMARY_KEY  = "primary-key";  //$NON-NLS-1$
    static final String TAG_UNIQUE_CONSTRAINT  = "unique-constraint";  //$NON-NLS-1$
    static final String TAG_FOREIGN_KEY  = "foreign-key";  //$NON-NLS-1$
    static final String TAG_ACCESS_PATTERN  = "access-pattern";  //$NON-NLS-1$
    static final String TAG_PROCEDURE  = "procedure";  //$NON-NLS-1$
    static final String TAG_PARAMETER  = "parameter";  //$NON-NLS-1$
    static final String TAG_RESULT_SET  = "result-set";  //$NON-NLS-1$
    static final String TAG_INDEX  = "index";  //$NON-NLS-1$
    static final String TAG_COLUMN_REFERENCE  = "column-reference";  //$NON-NLS-1$
    static final String TAG_COLUMN_TABLE_REFERENCE  = "column-table-reference";  //$NON-NLS-1$
    
    
    private static final String KEY_NAME  = "name";  //$NON-NLS-1$
    private static final String KEY_TABLE_NAME  = "tableName";  //$NON-NLS-1$
    
    public StatusInfo statusInfo;

    //============================================================================================================================
    // Static Methods
    
    @SuppressWarnings( "unused" )
    private IProgressMonitor monitor;
    
    private RelationalModel relationalModel;
    
    /** 
     * 
     * @since 4.2
     */
    public RelationalModelXmlTextFileProcessor() {
        super();
        statusInfo = new StatusInfo(RelationalPlugin.PLUGIN_ID);
    }
    
    /**
     * 
     * @param targetResource
     */
    public void buildModel(ModelResource targetResource, IProgressMonitor progressMonitor) {
        RelationalModelFactory factory = new RelationalModelFactory();
        
        factory.build(targetResource, this.relationalModel, progressMonitor);
    }
    
    public RelationalModel getRelationalModel() {
        return this.relationalModel;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractObjectProcessor#loadLinesFromFile(java.lang.String)
     */
    @Override
    public Collection loadLinesFromFile( String fileStr ) {
        Collection lines = super.loadLinesFromFile(fileStr);
        
        relationalModel = parseFile(fileStr);
        
        return lines;
    }
    
    private RelationalModel parseFile(String xmlFileUrl) {
        IPath filePath = new Path(xmlFileUrl);
        String fileName = filePath.lastSegment();
            
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        statusInfo = new StatusInfo(RelationalPlugin.PLUGIN_ID);
        RelationalModel relModel = new RelationalModel("bogus"); //$NON-NLS-1$
        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = factory.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            Document document = db.parse(xmlFileUrl);
            
            //DOMConfiguration config = document.getDomConfig();
            //config.setParameter("error-handler",new MyErrorHandler());
//            config.setParameter("schema-type", "http://www.w3.org/2001/XMLSchema");
//            config.setParameter("validate", Boolean.TRUE);
//            document.normalizeDocument();
            
            //get the root element
            Element root = document.getDocumentElement();
            
            if( getTagWithoutPrefix(root.getTagName()).equalsIgnoreCase(TAG_RELATIONAL_MODEL)) {

                //get a nodelist of elements
                NodeList children = root.getChildNodes();

                Collection<Node> indexNodes = new ArrayList<Node>();
                
                
                if(children != null && children.getLength() > 0) {
                    for(int i = 0 ; i < children.getLength();i++) {
    
                        Node child = children.item(i);
                        String name = getTagWithoutPrefix(child.getNodeName());
                        
                        int type = getType(name);
                        switch(type) {
                            case TYPES.TABLE: {
                                Properties props = getProperties(child.getAttributes());
                                RelationalTable table = new RelationalTable();
                                table.setProperties(props);
                                relModel.addChild(table);
                                
                                Node primaryKeyElement = null;
                                Node uniqueConstraintElement = null;
                                Collection<Node> accessPatternNodes = new ArrayList<Node>();
                                Collection<Node>  foreignKeyNodes = new ArrayList<Node>();
                                
                                // Create Table Children. Do not process KEYS until done with children
                                // DO NOT PROCESS FK's until ALL tables are complete
                                NodeList tableChildren = child.getChildNodes();
                                if(tableChildren != null && tableChildren.getLength() > 0) {
                                    for(int j = 0 ; j < tableChildren.getLength();j++) {
                                      //get the employee element
                                        Node tableChild = tableChildren.item(j);
                                        String tableChildName = getTagWithoutPrefix(tableChild.getNodeName());
                                        
                                        int tableChildType = getType(tableChildName);
                                        switch(tableChildType) {
                                            case TYPES.COLUMN: {
                                                Properties columnProps = getProperties(tableChild.getAttributes());
                                                RelationalColumn column = new RelationalColumn();
                                                column.setProperties(columnProps);
                                                table.addColumn(column);
                                            } break;
                                            case TYPES.PK: {
                                                primaryKeyElement = tableChild;
                                            } break;
                                            case TYPES.UC: {
                                                uniqueConstraintElement = tableChild;
                                            } break;
                                            case TYPES.AP: {
                                                accessPatternNodes.add(tableChild);
                                            } break;
                                            case TYPES.FK: {
                                                foreignKeyNodes.add(tableChild);
                                            } break;
                                        }
                                    }
                                }
                                
                                processPrimaryKey(primaryKeyElement, table); 
                                
                                processUniqueConstraint(uniqueConstraintElement, table);
                                
                                for( Node apNode : accessPatternNodes) {
                                    processAccessPattern( apNode, table);
                                }
                                
                                for( Node fkNode : foreignKeyNodes) {
                                    processForeignKey(fkNode, table);
                                }
                                
                            } break;
                            case TYPES.VIEW: {
                                Properties props = getProperties(child.getAttributes());
                                RelationalView view = new RelationalView();
                                view.setProperties(props);
                                relModel.addChild(view);
                                
                                Collection<Node> accessPatternNodes = new ArrayList<Node>();
                                
                                // Create Table Children. Do not process KEYS until done with children
                                // DO NOT PROCESS FK's until ALL tables are complete
                                NodeList tableChildren = child.getChildNodes();
                                if(tableChildren != null && tableChildren.getLength() > 0) {
                                    for(int j = 0 ; j < tableChildren.getLength();j++) {
                                      //get the employee element
                                        Node tableChild = tableChildren.item(j);
                                        String tableChildName = getTagWithoutPrefix(tableChild.getNodeName());
                                        
                                        int tableChildType = getType(tableChildName);
                                        switch(tableChildType) {
                                            case TYPES.COLUMN: {
                                                Properties columnProps = getProperties(tableChild.getAttributes());
                                                RelationalColumn column = new RelationalColumn();
                                                column.setProperties(columnProps);
                                                view.addColumn(column);
                                            } break;
                                            case TYPES.AP: {
                                                accessPatternNodes.add(tableChild);
                                            } break;
                                        }
                                    }
                                }

                                for( Node apNode : accessPatternNodes) {
                                    processAccessPattern( apNode, view);
                                }
                                
                            } break;
                            case TYPES.PROCEDURE: {
                                Properties props = getProperties(child.getAttributes());
                                RelationalProcedure proc = new RelationalProcedure();
                                proc.setProperties(props);
                                relModel.addChild(proc);
                                
                                NodeList procChildren = child.getChildNodes();
                                if(procChildren != null && procChildren.getLength() > 0) {
                                    for(int j = 0 ; j < procChildren.getLength();j++) {
                                      //get the employee element
                                        Node procChild = procChildren.item(j);
                                        String tableChildName = getTagWithoutPrefix(procChild.getNodeName());
                                        
                                        int tableChildType = getType(tableChildName);
                                        switch(tableChildType) {
                                            case TYPES.PARAMETER: {
                                                Properties paramProps = getProperties(procChild.getAttributes());
                                                RelationalParameter param = new RelationalParameter();
                                                param.setProperties(paramProps);
                                                proc.addParameter(param);
                                            } break;
                                            case TYPES.RESULT_SET: {
                                                Properties rsProps = getProperties(procChild.getAttributes());
                                                RelationalProcedureResultSet resultSet = new RelationalProcedureResultSet();
                                                resultSet.setProperties(rsProps);
                                                proc.setResultSet(resultSet);
                                                
                                                NodeList rsChildren = procChild.getChildNodes();
                                                if(rsChildren != null && rsChildren.getLength() > 0) {
                                                    for(int k = 0 ; k < rsChildren.getLength();k++) {
                                                      //get the employee element
                                                        Node rsChild = rsChildren.item(k);
                                                        String rsChildName = getTagWithoutPrefix(rsChild.getNodeName());
                                                        
                                                        int rsChildType = getType(rsChildName);
                                                        switch(rsChildType) {
                                                            case TYPES.COLUMN: {
                                                                Properties columnProps = getProperties(rsChild.getAttributes());
                                                                RelationalColumn column = new RelationalColumn();
                                                                column.setProperties(columnProps);
                                                                resultSet.addColumn(column);
                                                            } break;
                                                            
                                                        }
                                                    }
                                                }
                                                
                                            } break;
                                        }
                                    }
                                }
                                
                            } break;
                            case TYPES.INDEX: {
                                indexNodes.add(child);
                            } break;
                            
                            default: {
                                if( !name.equalsIgnoreCase("#text")) { //$NON-NLS-1$
                                    String message = UiConstants.Util.getString("RelationalModelXmlTextFileProcessor.importRelationalModelHasBugsElement", name); //$NON-NLS-1$
                                    statusInfo.setWarning(message);
                                }
                            }
                        }
                        //get the Employee object
    
                    }
                }
                
                
                for( Node indexNode : indexNodes ) {
                    Properties props = getProperties(indexNode.getAttributes());
                    RelationalIndex index = new RelationalIndex();
                    index.setProperties(props);
                    relModel.addChild(index);
                    
                    NodeList indexChildren = indexNode.getChildNodes();
                    if(indexChildren != null && indexChildren.getLength() > 0) {
                        for(int j = 0 ; j < indexChildren.getLength();j++) {
                            Node indexChild = indexChildren.item(j);
                            String indexChildName = getTagWithoutPrefix(indexChild.getNodeName());
                            if( indexChildName.equalsIgnoreCase(TAG_COLUMN_TABLE_REFERENCE) ) {
                                String tableName = indexChild.getAttributes().getNamedItem(KEY_TABLE_NAME).getNodeValue();
                                String colName = indexChild.getAttributes().getNamedItem(KEY_NAME).getNodeValue();
                                
                                for( RelationalReference child : relModel.getChildren() ) {
                                    if( child.getType() == TYPES.TABLE && child.getName().equalsIgnoreCase( tableName ) ) {
                                        for( RelationalColumn column : ((RelationalTable)child).getColumns()) {
                                            if( column.getName().equalsIgnoreCase(colName) ) {
                                                index.addColumn(column);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


        }catch(ParserConfigurationException pce) {
            String title = UiConstants.Util.getString("RelationalModelXmlTextFileProcessor.importRelationalModelXmlParsingError.title"); //$NON-NLS-1$
            String message = UiConstants.Util.getString("RelationalModelXmlTextFileProcessor.importRelationalModelXmlParsingError", fileName) + pce.getMessage(); //$NON-NLS-1$
            MessageDialog.openError(Display.getCurrent().getActiveShell(), title, message);
            statusInfo.setError(message);
            relModel = null;
        }catch(SAXException se) {
            String title = UiConstants.Util.getString("RelationalModelXmlTextFileProcessor.importRelationalModelXmlParsingError.title"); //$NON-NLS-1$
            String message = UiConstants.Util.getString("RelationalModelXmlTextFileProcessor.importRelationalModelXmlParsingError", fileName) + se.getMessage(); //$NON-NLS-1$
            MessageDialog.openError(Display.getCurrent().getActiveShell(), title, message);
            statusInfo.setError(message);
            relModel = null;
        }catch(IOException ioe) {
            String title = UiConstants.Util.getString("RelationalModelXmlTextFileProcessor.importRelationalModelXmlParsingError.title"); //$NON-NLS-1$
            String message = UiConstants.Util.getString("RelationalModelXmlTextFileProcessor.importRelationalModelXmlParsingError", fileName) + ioe.getMessage(); //$NON-NLS-1$
            MessageDialog.openError(Display.getCurrent().getActiveShell(), title, message);
            statusInfo.setError(message);
            relModel = null;
        }
        
        return relModel;
    }
    
    private void processPrimaryKey(Node primaryKeyNode, RelationalTable table) {
        if( primaryKeyNode == null ) {
            return;
        }
        
        Properties pkProps = getProperties(primaryKeyNode.getAttributes());
        RelationalPrimaryKey pk = new RelationalPrimaryKey();
        pk.setProperties(pkProps);
        table.setPrimaryKey(pk);
        NodeList pkChildren = primaryKeyNode.getChildNodes();
        if(pkChildren != null && pkChildren.getLength() > 0) {
            for(int j = 0 ; j < pkChildren.getLength();j++) {
                Node pkChild = pkChildren.item(j);
                String pkChildName = getTagWithoutPrefix(pkChild.getNodeName());
                if( pkChildName.equalsIgnoreCase(TAG_COLUMN_REFERENCE) ) {
                    String colName = pkChild.getAttributes().getNamedItem(KEY_NAME).getNodeValue();
                    for( RelationalColumn column : table.getColumns()) {
                        if( column.getName().equalsIgnoreCase(colName) ) {
                            pk.addColumn(column);
                        }
                    }
                }
            }
        }
    }
    
    private void processForeignKey(Node foreignKeyNode, RelationalTable table) {
        if( foreignKeyNode == null ) {
            return;
        }
        
        Properties fkProps = getProperties(foreignKeyNode.getAttributes());
        RelationalForeignKey fk = new RelationalForeignKey();
        fk.setProperties(fkProps);
        table.addForeignKey(fk);
        NodeList fkChildren = foreignKeyNode.getChildNodes();
        if(fkChildren != null && fkChildren.getLength() > 0) {
            for(int j = 0 ; j < fkChildren.getLength();j++) {
                Node fkChild = fkChildren.item(j);
                String pkChildName = getTagWithoutPrefix(fkChild.getNodeName());
                if( pkChildName.equalsIgnoreCase(TAG_COLUMN_REFERENCE) ) {
                    String colName = fkChild.getAttributes().getNamedItem(KEY_NAME).getNodeValue();
                    for( RelationalColumn column : table.getColumns()) {
                        if( column.getName().equalsIgnoreCase(colName) ) {
                            fk.addColumn(column);
                        }
                    }
                }
            }
        }
    }
    
    private void processAccessPattern(Node accessPatternNode, RelationalTable table) {
        if( accessPatternNode == null ) {
            return;
        }
        
        Properties acProps = getProperties(accessPatternNode.getAttributes());
        RelationalAccessPattern ac = new RelationalAccessPattern();
        ac.setProperties(acProps);
        table.addAccessPattern(ac);
        NodeList acChildren = accessPatternNode.getChildNodes();
        if(acChildren != null && acChildren.getLength() > 0) {
            for(int j = 0 ; j < acChildren.getLength();j++) {
                Node acChild = acChildren.item(j);
                String acChildName = getTagWithoutPrefix(acChild.getNodeName());
                if( acChildName.equalsIgnoreCase(TAG_COLUMN_REFERENCE) ) {
                    String colName = acChild.getAttributes().getNamedItem(KEY_NAME).getNodeValue();
                    for( RelationalColumn column : table.getColumns()) {
                        if( column.getName().equalsIgnoreCase(colName) ) {
                            ac.addColumn(column);
                        }
                    }
                }
            }
        }
    }
    
    private void processUniqueConstraint(Node uniqueConstraintNode, RelationalTable table) {
        if( uniqueConstraintNode == null ) {
            return;
        }
        
        Properties ucProps = getProperties(uniqueConstraintNode.getAttributes());
        RelationalUniqueConstraint uc = new RelationalUniqueConstraint();
        uc.setProperties(ucProps);
        table.setUniqueConstraint(uc);
        NodeList ucChildren = uniqueConstraintNode.getChildNodes();
        if(ucChildren != null && ucChildren.getLength() > 0) {
            for(int j = 0 ; j < ucChildren.getLength();j++) {
                Node ucChild = ucChildren.item(j);
                String ucChildName = getTagWithoutPrefix(ucChild.getNodeName());
                if( ucChildName.equalsIgnoreCase(TAG_COLUMN_REFERENCE) ) {
                    String colName = ucChild.getAttributes().getNamedItem(KEY_NAME).getNodeValue();
                    for( RelationalColumn column : table.getColumns()) {
                        if( column.getName().equalsIgnoreCase(colName) ) {
                            uc.addColumn(column);
                        }
                    }
                }
            }
        }
    }
    
    private Properties getProperties(NamedNodeMap attributeMap) {
        Properties props = new Properties();
        if( attributeMap != null && attributeMap.getLength() > 0 ) {
            for( int i=0; i< attributeMap.getLength(); i++ ) {
                Node item = attributeMap.item(i);
                props.put(item.getNodeName(), item.getNodeValue());
            }
        }
        return props;
    }


    public Collection createRowObjsFromStrings(Collection rowStrings) {
        Iterator iter = rowStrings.iterator();
        String nextStr = null;
        
        RelationalRowFactory factory = new RelationalRowFactory();
        
        Collection stringRows = new ArrayList();
        IRowObject nextRow = null;
        while( iter.hasNext() ) {
            nextStr = (String)iter.next();
            nextRow = factory.createRowObject(nextStr);
            if( nextRow != null && nextRow.isValid() )
                stringRows.add(nextRow);
            else {
                logParsingError(nextStr);
            }
        }
        return stringRows;
    }
    
    
    private int getType(String objType) {
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.ACCESSPATTERN) ) {
            return TYPES.AP;
        }
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.CATELOG) ) {
            return TYPES.CATELOG;
        }
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.COLUMN) ) {
            return TYPES.COLUMN;
        }
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.FOREIGNKEY) ) {
            return TYPES.FK;
        }
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.INDEX) ) {
            return TYPES.INDEX;
        }
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.MODEL) ) {
            return TYPES.MODEL;
        }
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.PARAMETER) ) {
            return TYPES.PARAMETER;
        }
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.PRIMARYKEY) ) {
            return TYPES.PK;
        }
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.PROCEDURE) ) {
            return TYPES.PROCEDURE;
        }
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.RESULTSET) ) {
            return TYPES.RESULT_SET;
        }
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.SCHEMA) ) {
            return TYPES.SCHEMA;
        }
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.TABLE) ) {
            return TYPES.TABLE;
        }
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.UNIQUECONSTRAINT) ) {
            return TYPES.UC;
        }
        if( objType.trim().equalsIgnoreCase(TYPES_LITERAL.VIEW) ) {
            return TYPES.VIEW;
        }
        
        return TYPES.UNDEFINED;
    }
    
    private String getTagWithoutPrefix(String tagStr) {
        int indexOfColon = tagStr.indexOf(':');
        if( indexOfColon < 0 ) {
            return tagStr;
        }
        
        return tagStr.substring(indexOfColon+1);
    }

    public void setProgressMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }
    
    public StatusInfo getStatusInfo() {
        return this.statusInfo;
    }
}