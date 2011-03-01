/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.validation.rules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDTypeDefinition;
import org.teiid.query.sql.ProcedureReservedWords;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.visitor.GroupsUsedByElementsVisitor;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.util.XmlDocumentUtil;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.transformation.TransformationPlugin;

/**
 * This validation rule applys aaditional validation checks for sql transformations whose targets are mapping classes. Checks
 * applied: 1) ERROR -> If the query in the transformation editor is anything other than a Query or an UNION. 2) ERROR -> IF input
 * parameters are used in Select clase of any of the Queries in the transformation. 3) WARNING -> IF input parameters are not used
 * in the criteria of any of the queries in the transformation, for a recursive mapping class. This rule is called from
 * SqlTransformationMappingRootRule while validating a transformation and assumes that the command on which its applying
 * validation checks is valid.
 * 
 * @since 4.3
 */
public class MappingClassTransformationValidationHelper {

    /**
     * Validate the command defining the mapping class.
     * 
     * @since 4.2
     */
    public void validate( final Command command,
                          final SqlTransformationMappingRoot transRoot,
                          final ValidationResult validationResult ) {
        EObject targetObj = transRoot.getTarget();
        if (!(targetObj instanceof MappingClass)) {
            return;
        }

        MappingClass mappingClass = (MappingClass)targetObj;

        // MyDefect : 17749 Added for recursive mapping class validation
        if (mappingClass.isRecursionAllowed() && mappingClass.isRecursive() && mappingClass.eResource() instanceof EmfResource) {

            validateRecursiveMappingClass(mappingClass, validationResult);
        } else if (command instanceof Query) {
            validate((Query)command, mappingClass, validationResult);
        } else if (command instanceof SetQuery) {
            validate((SetQuery)command, mappingClass, validationResult);
        } else {
            ValidationProblem errorProblem = new ValidationProblemImpl(
                                                                       0,
                                                                       IStatus.ERROR,
                                                                       TransformationPlugin.Util.getString("MappingClassTransformationRule.Non-Query_NonUnion_transformation")); //$NON-NLS-1$
            validationResult.addProblem(errorProblem);
        }
    }

    void validate( final Query query,
                   final MappingClass mappingClass,
                   final ValidationResult validationResult ) {
        ValidationProblem problem2 = checkInputParamInCriteria(query, mappingClass);
        if (problem2 != null) {
            validationResult.addProblem(problem2);
            return;
        }
    }

    void validate( final SetQuery setQuery,
                   final MappingClass mappingClass,
                   final ValidationResult validationResult ) {
        boolean hasInputParamInCriteria = false;
        ValidationProblem inputCriteriaProblem = null;
        for (QueryCommand query : setQuery.getQueryCommands()) {
            if (query instanceof SetQuery) {
                validate((SetQuery)query, mappingClass, validationResult);
                return;
            }
            ValidationProblem problem2 = checkInputParamInCriteria((Query)query, mappingClass);
            if (problem2 != null && !hasInputParamInCriteria) {
                inputCriteriaProblem = problem2;
            } else {
                hasInputParamInCriteria = true;
            }
        }
        // if there is no good criteria (using input params)
        if (!hasInputParamInCriteria) {
            validationResult.addProblem(inputCriteriaProblem);
        }
    }

    /*
     * WARNING -> IF input parameters are not used in the criteria of any of the queries in the transformation,
     * for a recursive mapping class.
     */
    ValidationProblem checkInputParamInCriteria( final Query query,
                                                 final MappingClass mappingClass ) {
        if (mappingClass.isRecursive()) {
            boolean foundInParam = false;
            Criteria criteriaClause = query.getCriteria();
            if (criteriaClause != null) {
                Set<GroupSymbol> groups = GroupsUsedByElementsVisitor.getGroups(criteriaClause);
                if (groups.contains(new GroupSymbol(ProcedureReservedWords.INPUT))
                || groups.contains(new GroupSymbol(ProcedureReservedWords.INPUTS))) {
                    foundInParam = true;
                       
                }
            }
            if (!foundInParam) {
                return new ValidationProblemImpl(
                                                 0,
                                                 IStatus.WARNING,
                                                 TransformationPlugin.Util.getString("MappingClassTransformationRule.No_INPUT_Parameters_In_Criteria", mappingClass.getName())); //$NON-NLS-1$
            }
        }
        return null;
    }

    /**
     * MyDefect : 17749 Added method for recursive mapping class validation
     * 
     * @param mc
     * @param validationResult
     * @since 4.3
     */
    void validateRecursiveMappingClass( final MappingClass mc,
                                        final ValidationResult validationResult ) {

        // Apply test only to mapping classes that are marked as recursive ...
        EmfResource eResource = (EmfResource)mc.eResource();
        ModelContents contents = eResource.getModelContents();
        if (contents != null) {
            MappingClassSet mcset = (MappingClassSet)mc.eContainer();

            // Get a list of all TreeMappingRoots for the XmlDocument instance that contains this MappingClass
            // MappingClassSet.getTarget() returns a reference to its XmlDocument container
            // TreeMappingRoot.getTarget() returns a reference to its XmlDocument container
            List treeMappingRoots = contents.getTransformations(mcset.getTarget());

            // Construct a map of XmlDocumentNode to MappingClass instances
            Map xmlDocNodeToMappingClass = new HashMap();
            for (Iterator iter = treeMappingRoots.iterator(); iter.hasNext();) {
                Object obj = iter.next();
                if (obj instanceof TreeMappingRoot) {
                    TreeMappingRoot tmr = (TreeMappingRoot)obj;
                    // TreeMappingRoot.getInputs() returns a reference to its source MappingClass
                    CoreArgCheck.isEqual(1, tmr.getInputs().size());
                    for (final Iterator outIter = tmr.getOutputs().iterator(); outIter.hasNext();) {
                        xmlDocNodeToMappingClass.put(outIter.next(), tmr.getInputs().get(0));
                    }
                }
            }

            // For this MappingClass which is marked as recursive, try to find its recursion root.
            // If one is not found then this is an error
            for (Iterator iter = treeMappingRoots.iterator(); iter.hasNext();) {
                Object obj = iter.next();

                if (obj instanceof TreeMappingRoot) {
                    TreeMappingRoot tmr = (TreeMappingRoot)obj;

                    // Perform the validation checks using the TreeMappingRoot that references this MappingClass
                    if (tmr.getInputs().get(0) == mc) {
                        XmlElement element = (XmlElement)tmr.getOutputs().get(0);

                        MappingClass rootMappingClass = getRecusionRootMappingClass(element, xmlDocNodeToMappingClass);
                        if (rootMappingClass == null) {
                            // Mapping class is null for {0} class
                            String msg = TransformationPlugin.Util.getString("MappingClassTransformationRule.Mapping_Class_Is_Null_For_{0}_0", mc.getName()); //$NON-NLS-1$
                            ValidationProblem errorProblem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                            validationResult.addProblem(errorProblem);
                        }

                        // If a recursion root is found, check that its structure against the complimentary child mapping class
                        if (rootMappingClass != null) {
                            if (mc.getColumns().size() != rootMappingClass.getColumns().size()) {
                                // Mismatch number of column between mapping class {0} and {1}
                                String msg = TransformationPlugin.Util.getString("MappingClassTransformationRule.Mismatch_Number_Of_Column_{0}_AND_{1}_1", rootMappingClass.getName(), mc.getName()); //$NON-NLS-1$                                    
                                ValidationProblem errorProblem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                                validationResult.addProblem(errorProblem);
                                continue;
                            }

                            List childColumns = mc.getColumns();
                            List rootColumns = rootMappingClass.getColumns();
                            for (int i = 0, n = rootColumns.size(); i < n; i++) {
                                MappingClassColumn rootCol = (MappingClassColumn)rootColumns.get(i);
                                MappingClassColumn childCol = (MappingClassColumn)childColumns.get(i);
                                if (!rootCol.getName().equalsIgnoreCase(childCol.getName())) {
                                    // Mismatch column name between mapping class {0} and {1}
                                    String msg = TransformationPlugin.Util.getString("MappingClassTransformationRule.Mismatch_Number_Column_Name_{0}_AND_{1}_2", rootMappingClass.getName(), mc.getName()); //$NON-NLS-1$
                                    ValidationProblem errorProblem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                                    validationResult.addProblem(errorProblem);
                                }

                                if (rootCol.getType() != childCol.getType()) {
                                    // Mismatch column type between mapping class {0} and {1}
                                    String msg = TransformationPlugin.Util.getString("MappingClassTransformationRule.Mismatch_Column_Type_{0}_AND_{1}_3", rootMappingClass.getName(), mc.getName()); //$NON-NLS-1$                                        
                                    ValidationProblem errorProblem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                                    validationResult.addProblem(errorProblem);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param xmlElement
     * @param xmlDocNodeToMappingClass
     * @return
     * @since 4.3
     */
    private MappingClass getRecusionRootMappingClass( XmlElement xmlElement,
                                                      final Map xmlDocNodeToMappingClass ) {
        MappingClass mc = (MappingClass)xmlDocNodeToMappingClass.get(xmlElement);

        // The mapping class must be marked for recursion before proceeding
        if (mc != null && mc.isRecursionAllowed() && mc.isRecursive()) {

            // Get the XSD type of the Xml element
            final XSDComponent xsdComponent = xmlElement.getXsdComponent();
            XSDTypeDefinition type = XmlDocumentUtil.findXSDType(xsdComponent);

            // The search logic currently works by matching XSD types
            if (xsdComponent == null) {
                return null;
            }

            // Perform an upward search on the XML document trying to match XSD types
            EObject owner = xmlElement.eContainer();
            while (owner != null) {
                if (owner instanceof XmlElement) {
                    // The XML element must be bound to a mapping class ...
                    XSDComponent ownerXsdComponent = ((XmlElement)owner).getXsdComponent();
                    XSDTypeDefinition ownerType = XmlDocumentUtil.findXSDType(ownerXsdComponent);

                    // If the types match then check if it is bound to a mapping class
                    if (type != null && type == ownerType) {
                        mc = (MappingClass)xmlDocNodeToMappingClass.get(owner);
                        if (mc != null) {
                            return mc;
                        }
                        // Check if the mapping class is bound to the parent container node
                        if (owner.eContainer() instanceof XmlContainerNode) {
                            mc = (MappingClass)xmlDocNodeToMappingClass.get(owner.eContainer());
                            if (mc != null) {
                                return mc;
                            }
                        }
                    }
                }
                owner = owner.eContainer();
            }
        }
        return null;
    }

}
