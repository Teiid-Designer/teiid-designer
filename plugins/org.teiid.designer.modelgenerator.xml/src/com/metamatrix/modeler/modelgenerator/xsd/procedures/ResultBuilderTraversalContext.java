package com.metamatrix.modeler.modelgenerator.xsd.procedures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xsd.XSDTypeDefinition;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationMappingHelper;
import com.metamatrix.modeler.schema.tools.NameUtil;

/**
 * 
 * Passed along with the parsing of a XSD.
 * Responsible for determining when we are at the limit of what 
 * can be represented in a procedure result.
 *
 * Responsible for keeping a list of procedures created from XSD
 * so that we do not duplicate.
 */
public class ResultBuilderTraversalContext extends BaseTraversalContext implements TraversalContext {
	
	private ProcedureResult result;
	private Procedure procedure;
	//private SqlTransformationMappingRoot transformation;
	private List<Column> cachedColumns = new ArrayList();
	
	public static final String RESPONSE = "response_"; //$NON-NLS-1$
	public static final String XML_IN = "xml_in"; //$NON-NLS-1$
	
	public ResultBuilderTraversalContext(String procedureName, QName namespace, TraversalContext ctx, ProcedureBuilder builder) {
		super(procedureName, namespace, ctx, builder);
	}

	public ResultBuilderTraversalContext(String procedureName, QName namespace, ProcedureBuilder builder) {
		super(procedureName, namespace, builder);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.wsdl.TraversalContext#addResultColumn(java.lang.String, org.eclipse.xsd.XSDTypeDefinition)
	 */
	@Override
	public void addColumn(String name, XSDTypeDefinition type) throws ModelerCoreException {
		// If the procedure doesn't exist yet. 
		// Don't create a function if there's no data.
		// for nested elements with no scalars.
		if(result == null) {
			procedure = createProcedure(procedureName);
			result = procedure.getResult();
		}
		
		// Add a colum to the result for the data.
		Column resultCol = factory.createColumn();
		result.getColumns().add(resultCol);
		String uniqueName = getUniqueName(resultCol, NameUtil.normalizeName(name));
		resultCol.setName(uniqueName);
		resultCol.setNameInSource(uniqueName);
		resultCol.setType(datatypeManager.getDatatypeForXsdType(type));
		cachedColumns .add(resultCol);
		
		//tell the builder to use a new ctx when going futher
		//down the XML tree. 
		setReachedResultNode(true);
		
		
	}
	
	Procedure createProcedure(String procedureNameBase)
			throws ModelWorkspaceException, ModelerCoreException {
		procedure = factory.createProcedure();
		builder.getSchema().getProcedures().add(procedure);
		String uniqueName = getUniqueName(procedure, RESPONSE + NameUtil.normalizeName(procedureNameBase));
		procedure.setName(uniqueName);
		procedure.setNameInSource(procedureNameBase);

		ProcedureParameter param = factory.createProcedureParameter();
		procedure.getParameters().add(param);
		param.setDirection(DirectionKind.IN_LITERAL);
		param.setName(XML_IN);
		param.setNameInSource(XML_IN);
		param.setNullable(NullableType.NO_NULLS_LITERAL);
		param.setType(datatypeManager
				.getBuiltInDatatype(DatatypeConstants.BuiltInNames.XML_LITERAL));

		ProcedureResult result = factory.createProcedureResult();
		procedure.setResult(result);
		result.setName(NameUtil.normalizeName(procedureNameBase) + IBuilderConstants.V_FUNC_RESULT);
		result.setNameInSource(procedureNameBase);
		builder.addProcedure(procedureNameBase);
		
		//transformation = builder.getModelResource().getModelTransformations().createNewSqlTransformation(procedure);
		return procedure;
	}
	
	@Override
	public void createTransformation() {
		if(null != procedure) { 
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(IBuilderConstants.V_FUNC_PREAMBLE);
		sqlString.append(IBuilderConstants.V_FUNC_SPACE);
		sqlString.append(IBuilderConstants.V_FUNC_SELECT_FROM_T);
		sqlString.append(IBuilderConstants.V_FUNC_SPACE);
		sqlString.append(IBuilderConstants.V_FUNC_XMLTABLE);
		sqlString.append(IBuilderConstants.V_FUNC_OPEN);
		sqlString.append(IBuilderConstants.V_FUNC_SPACE);
		
		if(null != getNamespace() || !getNamespace().getNamespaceURI().isEmpty()) {
			sqlString.append(IBuilderConstants.V_FUNC_XMLNAMESPACES);
			sqlString.append(IBuilderConstants.V_FUNC_OPEN);
			sqlString.append(IBuilderConstants.V_FUNC_SPACE);
			sqlString.append(IBuilderConstants.V_FUNC_DEFAULT);
			sqlString.append(IBuilderConstants.V_FUNC_SPACE);
			sqlString.append(IBuilderConstants.V_FUNC_QUOTE);
			sqlString.append(getNamespace().getNamespaceURI());
			sqlString.append(IBuilderConstants.V_FUNC_QUOTE);
			sqlString.append(IBuilderConstants.V_FUNC_CLOSE);
			sqlString.append(IBuilderConstants.V_FUNC_SPACE);
			sqlString.append(IBuilderConstants.V_FUNC_COMMA);
		}
		
		sqlString.append(IBuilderConstants.V_FUNC_QUOTE);
		sqlString.append(getPath());
		sqlString.append(IBuilderConstants.V_FUNC_QUOTE);
		sqlString.append(IBuilderConstants.V_FUNC_SPACE);
		sqlString.append(IBuilderConstants.V_FUNC_PASSING);
		sqlString.append(IBuilderConstants.V_FUNC_SPACE);
		sqlString.append(XML_IN);
		sqlString.append(IBuilderConstants.V_FUNC_SPACE);
		sqlString.append(IBuilderConstants.V_FUNC_COLUMNS);
		
		boolean firstTime = true;
		for (Column column : cachedColumns) {
			if(!firstTime) {
				sqlString.append(IBuilderConstants.V_FUNC_COMMA);
				sqlString.append(IBuilderConstants.V_FUNC_SPACE);
			}
			sqlString.append(IBuilderConstants.V_FUNC_SPACE);
			sqlString.append(IBuilderConstants.V_FUNC_DOUBLE_QUOTE);
			sqlString.append(column.getNameInSource());
			sqlString.append(IBuilderConstants.V_FUNC_DOUBLE_QUOTE);
			sqlString.append(IBuilderConstants.V_FUNC_SPACE);
			sqlString.append(datatypeManager.getRuntimeTypeName(column.getType()));
			// Not sure we need the path
			//sqlString.append(V_FUNC_SPACE);
			//sqlString.append(column.getName());
			firstTime = false;
		}
		
		sqlString.append(IBuilderConstants.V_FUNC_CLOSE);
		sqlString.append(IBuilderConstants.V_FUNC_SPACE);
		sqlString.append(IBuilderConstants.V_FUNC_AS_T);
		sqlString.append(IBuilderConstants.V_FUNC_SPACE);
		sqlString.append(IBuilderConstants.V_FUNC_POSTSCRIPT);

		SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(procedure);
			TransformationHelper.setSqlString(root, sqlString.toString(),
					QueryValidator.SELECT_TRNS, true, this);
		TransformationMappingHelper.reconcileMappingsOnSqlChange((EObject) root, this);
		}
	}
	
	
    private String getUniqueName( final EObject eObject,
                                          final String proposedName ) {
        CoreArgCheck.isNotNull(eObject);
        final EStructuralFeature nameFeature = ModelerCore.getModelEditor().getNameFeature(eObject);
        if (nameFeature != null) {
            return generateUniqueInternalName(eObject.eContainer() == null ? eObject.eResource().getContents() : eObject.eContainer().eContents(),
                                       eObject,
                                       nameFeature,
                                       proposedName);
        }
        return proposedName;
    }

    private String generateUniqueInternalName( final EList siblings,
                                             final EObject eObject,
                                             final EStructuralFeature nameFeature,
                                             final String name ) {
        String newName = name;
        if (siblings != null) {
            final Set siblingNames = new HashSet();
            for (Iterator it = siblings.iterator(); it.hasNext();) {
                final EObject child = (EObject)it.next();
                if (eObject.getClass().equals(child.getClass())) {
                    siblingNames.add(child.eGet(nameFeature));
                }
            }
            boolean foundUniqueName = false;
            int index = 1;
            while (!foundUniqueName) {
                if (siblingNames.contains(newName)) {
                    newName = name + String.valueOf(index++);
                } else {
                    foundUniqueName = true;
                }
            }
        }
        return newName;
    }
}
