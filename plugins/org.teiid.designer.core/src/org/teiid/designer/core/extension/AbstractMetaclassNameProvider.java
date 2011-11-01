/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.extension;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * 
 */
public abstract class AbstractMetaclassNameProvider implements ExtendableMetaclassNameProvider {

    private static final String RELATIONAL_URI = "http://www.metamatrix.com/metamodels/Relational"; //$NON-NLS-1$
    private static final String SOURCE_FUNCTION_URI = "http://www.metamatrix.com/metamodels/MetaMatrixFunction"; //$NON-NLS-1$
    private static final String WEB_SERVICE_URI = "http://www.metamatrix.com/metamodels/WebService"; //$NON-NLS-1$
    private static final String XML_DOCUMENT_URI = "http://www.metamatrix.com/metamodels/XmlDocument"; //$NON-NLS-1$

    private static final String RELATIONAL_COLUMN = "com.metamatrix.metamodels.relational.impl.ColumnImpl"; //$NON-NLS-1$
    private static final String RELATIONAL_PRIMARY_KEY = "com.metamatrix.metamodels.relational.impl.PrimaryKeyImpl"; //$NON-NLS-1$
    private static final String RELATIONAL_FOREIGN_KEY = "com.metamatrix.metamodels.relational.impl.ForeignKeyImpl"; //$NON-NLS-1$
    private static final String RELATIONAL_VIEW = "com.metamatrix.metamodels.relational.impl.ViewImpl"; //$NON-NLS-1$
    private static final String RELATIONAL_PROCEDURE = "com.metamatrix.metamodels.relational.impl.ProcedureImpl"; //$NON-NLS-1$
    private static final String RELATIONAL_INDEX = "com.metamatrix.metamodels.relational.impl.IndexImpl"; //$NON-NLS-1$
    private static final String RELATIONAL_PROCEDURE_PARAMETER = "com.metamatrix.metamodels.relational.impl.ProcedureParameterImpl"; //$NON-NLS-1$
    private static final String RELATIONAL_UNIQUE_CONSTRAINT = "com.metamatrix.metamodels.relational.impl.UniqueConstraintImpl"; //$NON-NLS-1$
    private static final String RELATIONAL_ACCESS_PATTERN = "com.metamatrix.metamodels.relational.impl.AccessPatternImpl"; //$NON-NLS-1$
    private static final String RELATIONAL_BASE_TABLE = "com.metamatrix.metamodels.relational.impl.BaseTableImpl"; //$NON-NLS-1$
    private static final String RELATIONAL_PROCEDURE_RESULT = "com.metamatrix.metamodels.relational.impl.ProcedureResultImpl"; //$NON-NLS-1$

    private static final String FUNCTION_SCALAR_FUNCTION = "com.metamatrix.metamodels.function.impl.ScalarFunctionImpl"; //$NON-NLS-1$
    private static final String FUNCTION_FUNCTION_PARAMETER = "com.metamatrix.metamodels.function.impl.FunctionParameterImpl"; //$NON-NLS-1$
    private static final String FUNCTION_RETURN_PARAMETER = "com.metamatrix.metamodels.function.impl.ReturnParameterImpl"; //$NON-NLS-1$

    private static final String WEB_SERVICE_OPERATION = "com.metamatrix.metamodels.webservice.impl.OperationImpl"; //$NON-NLS-1$
    private static final String WEB_SERVICE_INPUT = "com.metamatrix.metamodels.webservice.impl.InputImpl"; //$NON-NLS-1$
    private static final String WEB_SERVICE_OUTPUT = "com.metamatrix.metamodels.webservice.impl.OutputImpl"; //$NON-NLS-1$
    private static final String WEB_SERVICE_INTERFACE = "com.metamatrix.metamodels.webservice.impl.InterfaceImpl"; //$NON-NLS-1$
    private static final String WEB_SERVICE_SAMPLE_MESSAGES = "com.metamatrix.metamodels.webservice.impl.SampleMessagesImpl"; //$NON-NLS-1$
    private static final String WEB_SERVICE_SAMPLE_FILE = "com.metamatrix.metamodels.webservice.impl.SampleFileImpl"; //$NON-NLS-1$
    private static final String WEB_SERVICE_SAMPLE_XSD = "com.metamatrix.metamodels.webservice.impl.SampleFromXsdImpl"; //$NON-NLS-1$

    private static final String XML_XMLDOCUMENT = "com.metamatrix.metamodels.xml.impl.XmlDocumentImpl"; //$NON-NLS-1$
    private static final String XML_XMLELEMENT = "com.metamatrix.metamodels.xml.impl.XmlElementImpl"; //$NON-NLS-1$
    private static final String XML_XMLATTRIBUTE = "com.metamatrix.metamodels.xml.impl.XmlAttributeImpl"; //$NON-NLS-1$
    private static final String XML_XMLROOT = "com.metamatrix.metamodels.xml.impl.XmlRootImpl"; //$NON-NLS-1$
    private static final String XML_XMLCOMMENT = "com.metamatrix.metamodels.xml.impl.XmlCommentImpl"; //$NON-NLS-1$
    private static final String XML_XMLNAMESPACE = "com.metamatrix.metamodels.xml.impl.XmlNamespaceImpl"; //$NON-NLS-1$
    private static final String XML_XMLSEQUENCE = "com.metamatrix.metamodels.xml.impl.XmlSequenceImpl"; //$NON-NLS-1$
    private static final String XML_XMLALL = "com.metamatrix.metamodels.xml.impl.XmlAllImpl"; //$NON-NLS-1$
    private static final String XML_XMLCHOICE = "com.metamatrix.metamodels.xml.impl.XmlChoiceImpl"; //$NON-NLS-1$
    private static final String XML_XMLPROCESSING_INSTRUCTION = "com.metamatrix.metamodels.xml.impl.ProcessingInstructionImpl"; //$NON-NLS-1$

    private static final String MC_PREFIX = ".impl."; //$NON-NLS-1$
    private static final String MC_SUFFIX = "Impl"; //$NON-NLS-1$

    private Map<String, List<String>> parentChildMap;

    private String metamodelUri;

    public AbstractMetaclassNameProvider( final String metamodelUri ) {
        this.metamodelUri = metamodelUri;
        populateParentChildMap(this.metamodelUri);
    }

    private void populateParentChildMap( String metamodelUri ) {
        this.parentChildMap = new HashMap<String, List<String>>();
        if (RELATIONAL_URI.equals(this.metamodelUri)) {
            // BaseTable
            List<String> children = new ArrayList<String>();
            children.add(RELATIONAL_COLUMN);
            children.add(RELATIONAL_PRIMARY_KEY);
            children.add(RELATIONAL_FOREIGN_KEY);
            children.add(RELATIONAL_ACCESS_PATTERN);
            children.add(RELATIONAL_UNIQUE_CONSTRAINT);
            this.parentChildMap.put(RELATIONAL_BASE_TABLE, children);
            // View
            children = new ArrayList<String>();
            children.add(RELATIONAL_COLUMN);
            children.add(RELATIONAL_ACCESS_PATTERN);
            this.parentChildMap.put(RELATIONAL_VIEW, children);
            // Procedure
            children = new ArrayList<String>();
            children.add(RELATIONAL_PROCEDURE_RESULT);
            children.add(RELATIONAL_PROCEDURE_PARAMETER);
            this.parentChildMap.put(RELATIONAL_PROCEDURE, children);
            // Procedure
            children = new ArrayList<String>();
            children.add(RELATIONAL_COLUMN);
            this.parentChildMap.put(RELATIONAL_PROCEDURE_RESULT, children);
        } else if (SOURCE_FUNCTION_URI.equals(this.metamodelUri)) {
            // Function
            List<String> children = new ArrayList<String>();
            children.add(FUNCTION_FUNCTION_PARAMETER);
            children.add(FUNCTION_RETURN_PARAMETER);
            this.parentChildMap.put(FUNCTION_SCALAR_FUNCTION, children);
        } else if (WEB_SERVICE_URI.equals(this.metamodelUri)) {
            // Interface
            List<String> children = new ArrayList<String>();
            children.add(WEB_SERVICE_OPERATION);
            this.parentChildMap.put(WEB_SERVICE_INTERFACE, children);
            // Operation
            children = new ArrayList<String>();
            children.add(WEB_SERVICE_INPUT);
            children.add(WEB_SERVICE_OUTPUT);
            this.parentChildMap.put(WEB_SERVICE_OPERATION, children);
            // Input and Output
            children = new ArrayList<String>();
            children.add(WEB_SERVICE_SAMPLE_MESSAGES);
            this.parentChildMap.put(WEB_SERVICE_INPUT, children);
            this.parentChildMap.put(WEB_SERVICE_OUTPUT, children);
            // Message Samples
            children = new ArrayList<String>();
            children.add(WEB_SERVICE_SAMPLE_FILE);
            children.add(WEB_SERVICE_SAMPLE_XSD);
            this.parentChildMap.put(WEB_SERVICE_SAMPLE_MESSAGES, children);
        } else if (XML_DOCUMENT_URI.equals(this.metamodelUri)) {
            // XML Doc
            List<String> children = new ArrayList<String>();
            children.add(XML_XMLROOT);
            children.add(XML_XMLCOMMENT);
            this.parentChildMap.put(XML_XMLDOCUMENT, children);
            // XML Root and Element
            children = new ArrayList<String>();
            children.add(XML_XMLELEMENT);
            children.add(XML_XMLATTRIBUTE);
            children.add(XML_XMLCOMMENT);
            children.add(XML_XMLNAMESPACE);
            children.add(XML_XMLPROCESSING_INSTRUCTION);
            children.add(XML_XMLALL);
            children.add(XML_XMLCHOICE);
            children.add(XML_XMLSEQUENCE);
            this.parentChildMap.put(XML_XMLROOT, children);
        }
    }

    public String[] getExtendableMetaclassRoots() {
        String[] resultArray = new String[0];
        if (RELATIONAL_URI.equals(this.metamodelUri)) {
            resultArray = new String[4];
            resultArray[0] = RELATIONAL_BASE_TABLE;
            resultArray[1] = RELATIONAL_VIEW;
            resultArray[2] = RELATIONAL_PROCEDURE;
            resultArray[3] = RELATIONAL_INDEX;
        } else if (SOURCE_FUNCTION_URI.equals(this.metamodelUri)) {
            resultArray = new String[1];
            resultArray[0] = FUNCTION_SCALAR_FUNCTION;
        } else if (WEB_SERVICE_URI.equals(this.metamodelUri)) {
            resultArray = new String[1];
            resultArray[0] = WEB_SERVICE_INTERFACE;
        } else if (XML_DOCUMENT_URI.equals(this.metamodelUri)) {
            resultArray = new String[1];
            resultArray[0] = XML_XMLDOCUMENT;
        }
        return resultArray;
    }

    public String[] getExtendableMetaclassChildren( String metaclassName ) {
        if (!this.parentChildMap.containsKey(metaclassName)) {
            return new String[0];
        }
        List<String> childList = this.parentChildMap.get(metaclassName);
        String[] childNames = new String[childList.size()];
        for (int i = 0; i < childList.size(); i++) {
            childNames[i] = childList.get(i);
        }
        return childNames;
    }

    public boolean hasChildren( String metaclassName ) {
        return getExtendableMetaclassChildren(metaclassName).length > 0;
    }

    public String getParent( String metaclassName ) {
        return null;
    }

    public Image getImage( String metaclassName ) {
        return null;
    }

    public String getLabelText( String metaclassName ) {
        // This extracts the name between ".impl." and "Impl" from the metaclass name
        if (!CoreStringUtil.isEmpty(metaclassName)) {
            int indx1 = metaclassName.indexOf(MC_PREFIX);
            int indx2 = metaclassName.indexOf(MC_SUFFIX);
            return metaclassName.substring(indx1 + MC_PREFIX.length(), indx2);
        }
        return null;
    }

    public String getMetamodelUri() {
        return this.metamodelUri;
    }

}
