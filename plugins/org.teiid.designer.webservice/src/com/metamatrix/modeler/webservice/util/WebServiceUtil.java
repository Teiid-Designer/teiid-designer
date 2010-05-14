/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Message;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.webservice.Output;
import com.metamatrix.metamodels.webservice.WebServiceComponent;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlValueHolder;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.webservice.WebServicePlugin;
import com.metamatrix.modeler.webservice.procedure.DocumentGenerator;
import com.metamatrix.modeler.webservice.procedure.XsdInstanceNode;
import com.metamatrix.query.function.FunctionLibrary;
import com.metamatrix.query.sql.ProcedureReservedWords;
import com.metamatrix.query.sql.proc.AssignmentStatement;
import com.metamatrix.query.sql.symbol.Constant;
import com.metamatrix.query.sql.symbol.Expression;
import com.metamatrix.query.sql.symbol.Function;

/**
 * @since 4.2
 */
public class WebServiceUtil {

    // ===========================================================================================================================
    // Constants

    private static final String DEFAULT_INPUT_NAME = "NEWINPUT"; //$NON-NLS-1$
    private static final String PLACEHOLDER = WebServicePlugin.Util.getString("WebServiceUtil.chooseElementOrAttribute"); //$NON-NLS-1$

    public static final String INPUT_VARIABLE_UNQUALIFIED_PREFIX = "IN_"; //$NON-NLS-1$
    public static final String INPUT_VARIABLE_PREFIX = ProcedureReservedWords.VARIABLES + '.' + INPUT_VARIABLE_UNQUALIFIED_PREFIX;

    // ===========================================================================================================================
    // Static Methods

    public static String createXPath(XsdInstanceNode node) {
        StringBuffer xpath = new StringBuffer();
        for (XsdInstanceNode segNode = node; segNode != null;) {
            XsdInstanceNode parent = segNode.getParent();
            XSDConcreteComponent comp = segNode.getResolvedXsdComponent();
            if (!(comp instanceof XSDModelGroup)) {
                StringBuffer seg = new StringBuffer("/"); //$NON-NLS-1$
                if (comp instanceof XSDAttributeDeclaration) {
                    seg.append('@');
                }
                String name = segNode.getName();
                boolean conflict = false;
                if (parent != null) {
                    XsdInstanceNode[] siblings = parent.getChildren();
                    for (int ndx = siblings.length; --ndx >= 0;) {
                        XsdInstanceNode sibling = siblings[ndx];
                        if (sibling != segNode && sibling.getName().equals(name)) {
                            conflict = true;
                            break;
                        }
                    } // for
                }
                if (conflict) {
                    seg.append("*[local-name()=\""); //$NON-NLS-1$
                    seg.append(name);
                    seg.append("\" and namespace-uri()=\""); //$NON-NLS-1$
                    seg.append(segNode.getTargetNamespace());
                    seg.append("\"]"); //$NON-NLS-1$
                } else {
                    seg.append("*:"); //$NON-NLS-1$
                    seg.append(name);
                }
                xpath.insert(0, seg);
            }
            segNode = parent;
        } // for
        return xpath.toString();
    }
    
    /**
     * Generate a string that is a request XML document for the specified web service operation.
     * 
     * @param operation
     *            the operation whose request document will be generated
     * @param paramValues
     *            the ordered set of values to be inserted into the document or <code>null</code> if no values are inserted
     * @return the request document
     * @since 5.5.3
     */
    public static String generateRequestDocument(Operation operation,
                                                 List<String> paramValues) {
        XsdInstanceNode node = new XsdInstanceNode(operation.getInput().getContentElement());
        return DocumentGenerator.SHARED.generate(node, paramValues);
    }

    /**
     * Get the messages content element type definition.
     * 
     * @param message
     *            the input or ouput message
     * @return the ContentElement TypeDefn or <code>null</code>
     */
    public static XSDTypeDefinition getContentElementTypeDefn(Message message) {
        XSDTypeDefinition typeDefn = null;

        if (message != null) {
            XSDElementDeclaration elem = message.getContentElement();

            if (elem != null) {
                typeDefn = elem.getTypeDefinition();
            }
        }

        return typeDefn;
    }
    
    /**
     * This method will get the operation input, then get the input elements from it (if available) to use in the transformation
     * sql.
     * 
     * @param operation
     *            the webservice operation
     * @param includeFixedValueFeatures
     *            <code>true</code> if input elements with fixed values should be returned
     * @return the list of input elements (never <code>null</code>)
     */
    public static List<EObject> getInputElements(Operation operation,
                                                               boolean includeFixedValueFeatures) {
        List<EObject> elemList = new ArrayList<EObject>();

        if (operation != null) {
            addInputElements(operation.getInput(), elemList);
        }

        return elemList;
    }
    
    private static void addInputElements(Input input,
                                  List variables) {
        if (input != null) {
            XSDElementDeclaration element = input.getContentElement();
            if (element != null) {
                addInputElements(new XsdInstanceNode(element), variables);
            }
        }
    }

    private static void addInputElements(XsdInstanceNode node,
                                  List variables) {
        if (node.isSelectable()) {
            variables.add(node.getResolvedXsdComponent());
        }
        XsdInstanceNode[] children = node.getChildren();
        for (int ndx = 0; ndx<children.length; ndx++) {
            addInputElements(children[ndx], variables);
        } // for
    }
    
    public static String getSql(Operation operation,
                                List<String> paramValues) {
        StringBuffer sql = new StringBuffer("EXEC "); //$NON-NLS-1$
        String fullName = ModelerCore.getModelEditor().getModelRelativePathIncludingModel(operation).toString();
        fullName = fullName.replace('/', '.');
        sql.append(fullName);
        sql.append("('"); //$NON-NLS-1$
        sql.append(generateRequestDocument(operation, paramValues));
        sql.append("\n')"); //$NON-NLS-1$
        
        return sql.toString();
    }

    public static MetamodelDescriptor getWebServiceModelDescriptor() {
        return ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(WebServicePackage.eNS_URI);
    }

    public static String getXpath(AssignmentStatement statement) {
        Function function = getXpathFunction(statement);
        if (function != null && function.getArgs().length > 1) {
            return (String)((Constant)function.getArg(1)).getValue();
        }
        return CoreStringUtil.Constants.EMPTY_STRING;
    }

    public static Function getXpathFunction(AssignmentStatement statement) {
        if (statement.hasExpression()) {
            Expression expr = statement.getExpression();
            if (expr instanceof Function) {
                Function function = (Function)expr;
                if (FunctionLibrary.XPATHVALUE.equalsIgnoreCase(function.getName())) {
                    return function;
                }
            }
        }
        return null;
    }

    public static boolean isWebServiceComponent(final Object object) {
        boolean result = false;
        if (object instanceof WebServiceComponent) {
            result = true;
        }
        return result;
    }

    public static boolean isWebServiceInterface(final Object object) {
        boolean result = false;
        if (object instanceof Interface) {
            result = true;
        }
        return result;
    }

    public static boolean isWebServiceModelResource(final ModelResource modelResource) {
        boolean result = false;
        if (modelResource != null) {
            MetamodelDescriptor descriptor = null;

            try {
                descriptor = modelResource.getPrimaryMetamodelDescriptor();
            } catch (ModelWorkspaceException e) {
                e.printStackTrace();
            }

            if (descriptor != null && descriptor.getNamespaceURI().equals(WebServicePackage.eNS_URI)) {
                result = true;
            }
        }
        return result;
    }

    // ===========================================================================================================================
    // Constructors

    /**
     * @since 4.2
     */
    public WebServiceUtil() {
        super();
    }

    // ===========================================================================================================================
    // Methods

    public String generateTransformationSql(final Output output,
                                            final XmlDocument xmlDoc) {
        if (output == null || xmlDoc == null) {
            return null;
        }

        final List variables = new ArrayList();

        // Find the corresponding Input ...
        final Operation op = output.getOperation();
        String inputName = DEFAULT_INPUT_NAME;
        if (op != null) {
            final Input input = op.getInput();
            addInputVariables(input, variables);
            if (input.getName() != null && input.getName().trim().length() != 0) {
                inputName = getFullName(input);
            }
        }

        final StringBuffer sb = new StringBuffer();

        // Create the basic header ...
        sb.append("CREATE VIRTUAL PROCEDURE BEGIN "); //$NON-NLS-1$

        // Add the variables ...
        final Iterator iter = variables.iterator();
        final List criteriaStrings = new ArrayList();
        int i = 0;
        while (iter.hasNext()) {
            final InputVariable variable = (InputVariable)iter.next();
            ++i;
            String name = variable.getName();

            // Add the declaration ...
            if (name == null || name.trim().length() == 0) {
                name = "VARIABLE"; //$NON-NLS-1$
                variable.setName(name);
            }
            sb.append("DECLARE string "); //$NON-NLS-1$
            sb.append(WebServiceUtil.INPUT_VARIABLE_PREFIX);
            sb.append(variable.getName());
            sb.append("; "); //$NON-NLS-1$

            // Add the assignment ...
            sb.append(WebServiceUtil.INPUT_VARIABLE_PREFIX);
            sb.append(variable.getName());
            sb.append(" = xpathValue("); //$NON-NLS-1$
            sb.append(inputName);
            sb.append(",'"); //$NON-NLS-1$
            sb.append(variable.getXpath());
            sb.append("'); "); //$NON-NLS-1$

            // Add criteria for each value holder ...
            final List xmlValueHolders = variable.getXmlDocumentEntityForCriteria();
            if (!xmlValueHolders.isEmpty()) {
                final Iterator vhIter = xmlValueHolders.iterator();
                while (vhIter.hasNext()) {
                    final XmlValueHolder valueHolder = (XmlValueHolder)vhIter.next();
                    if (valueHolder == null) {
                        continue;
                    }
                    final String valueHolderPath = getFullName(valueHolder);
                    if (valueHolderPath == null || valueHolderPath.trim().length() == 0) {
                        continue;
                    }
                    final StringBuffer criteria = new StringBuffer();
                    criteria.append(valueHolderPath);
                    criteria.append(" = "); //$NON-NLS-1$
                    criteria.append(WebServiceUtil.INPUT_VARIABLE_PREFIX);
                    criteria.append(variable.getName());
                    criteriaStrings.add(criteria.toString());
                }
            } else {
                final String valueHolderPath = getFullName(xmlDoc);
                final StringBuffer criteria = new StringBuffer();
                criteria.append(valueHolderPath);
                criteria.append(PLACEHOLDER); 
                criteria.append(" = "); //$NON-NLS-1$
                criteria.append(WebServiceUtil.INPUT_VARIABLE_PREFIX);
                criteria.append(variable.getName());
                criteriaStrings.add(criteria.toString());
            }
        }

        // Add the variable assignments ...

        // Create the SELECT FROM <XMLDOC> ...
        final String docPath = getFullName(xmlDoc);
        sb.append("SELECT * FROM "); //$NON-NLS-1$
        sb.append(docPath);
        if (criteriaStrings.size() != 0) {
            sb.append(" WHERE "); //$NON-NLS-1$
            final Iterator criteriaIter = criteriaStrings.iterator();
            boolean moreThanOne = false;
            while (criteriaIter.hasNext()) {
                final String criteriaString = (String)criteriaIter.next();
                if (moreThanOne) {
                    sb.append(" AND "); //$NON-NLS-1$
                }
                sb.append(criteriaString);
                moreThanOne = true;
            }
            sb.append(" "); //$NON-NLS-1$
        }
        sb.append("; END"); //$NON-NLS-1$

        return sb.toString();

    }

    public String getFullName(final EObject object) {
        return TransformationHelper.getSqlEObjectFullName(object);
    }

    public void addInputVariables(Input input,
                                  List variables) {
        if (input != null) {
            XSDElementDeclaration element = input.getContentElement();
            if (element != null) {
                addInputVariables(new XsdInstanceNode(element), variables);
            }
        }
    }

    public void addInputVariables(XsdInstanceNode node,
                                  List variables) {
        if (node.isSelectable()) {
            variables.add(new InputVariable((XSDNamedComponent)node.getResolvedXsdComponent(), node.getName(), createXPath(node)));
        }
        XsdInstanceNode[] children = node.getChildren();
        for (int ndx = children.length; --ndx >= 0;) {
            addInputVariables(children[ndx], variables);
        } // for
    }
}
