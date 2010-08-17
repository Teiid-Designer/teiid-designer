package com.metamatrix.modeler.modelgenerator.wsdl.procedures;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDTypeDefinition;

import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationMappingHelper;

public class RequestBuilderTraversalContext extends BaseTraversalContext
		implements TraversalContext {

	public static final String CREATE = "create_";
	public static final String XML_OUT = "xml_out";

	private Procedure procedure;
	private List<ProcedureParameter> cachedParams = new ArrayList();
	private SqlTransformationMappingRoot transformation;
	private ProcedureResult procedureResult;

	public RequestBuilderTraversalContext(String procedureName, QName namespace,
			TraversalContext ctx, ProcedureBuilder builder) {
		super(procedureName, namespace, ctx, builder);
	}

	public RequestBuilderTraversalContext(String procedureName, QName namespace,
			ProcedureBuilder builder) {
		super(procedureName, namespace, builder);
	}

	@Override
	public void addColumn(String name, XSDTypeDefinition type)
			throws ModelerCoreException {
		if (procedure == null) {
			procedure = createProcedure(procedureName);
		}

		ProcedureParameter param = factory.createProcedureParameter();
		procedure.getParameters().add(param);
		param.setDirection(DirectionKind.IN_LITERAL);
		param.setName(name);
		param.setNameInSource(name);
		param.setNullable(NullableType.NULLABLE_LITERAL);
		param.setType(datatypeManager.getDatatypeForXsdType(type));
		setReachedResultNode(true);
		cachedParams.add(param);

		// see RevalidateModelTransformationsAction
	}

	Procedure createProcedure(String procedureNameBase)
			throws ModelWorkspaceException, ModelerCoreException {
		Procedure procedure = factory.createProcedure();
		builder.getSchema().getProcedures().add(procedure);
		procedure.setName(CREATE + procedureNameBase);
		procedure.setNameInSource(CREATE + procedureNameBase);
		builder.addProcedure(procedureNameBase);

		procedureResult = factory.createProcedureResult();
		procedure.setResult(procedureResult);
		procedureResult.setName(procedureNameBase);
		procedureResult.setNameInSource(procedureNameBase);
		Column resultCol = factory.createColumn();
		procedureResult.getColumns().add(resultCol);
		resultCol.setName(XML_OUT);
		resultCol
				.setType(datatypeManager
						.getBuiltInDatatype(DatatypeConstants.BuiltInNames.XML_LITERAL));

		//transformation = builder.getModelResource().getModelTransformations()
		//		.createNewSqlTransformation(procedure);
		return procedure;
	}

	public void createTransformation() {
		if (null != procedure) {
			StringBuffer sqlString = new StringBuffer();
			sqlString.append(IBuilderConstants.V_FUNC_PREAMBLE);
			sqlString.append(IBuilderConstants.V_FUNC_SPACE);
			sqlString.append(IBuilderConstants.V_FUNC_SELECT);
			sqlString.append(IBuilderConstants.V_FUNC_SPACE);
			sqlString.append(IBuilderConstants.V_FUNC_XML_ELEMENT);
			sqlString.append(IBuilderConstants.V_FUNC_OPEN);
			sqlString.append(IBuilderConstants.V_FUNC_SPACE);
			sqlString.append(IBuilderConstants.V_FUNC_NAME);
			sqlString.append(IBuilderConstants.V_FUNC_SPACE);
			sqlString.append(procedureResult.getName());
			sqlString.append(IBuilderConstants.V_FUNC_COMMA);
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
				sqlString.append(IBuilderConstants.V_FUNC_SPACE);
				sqlString.append(IBuilderConstants.V_FUNC_CLOSE);
				sqlString.append(IBuilderConstants.V_FUNC_COMMA);
			}
			
			boolean firstTime = true;
			for (ProcedureParameter param : cachedParams) {
				if (!firstTime) {
					sqlString.append(IBuilderConstants.V_FUNC_COMMA);
					sqlString.append(IBuilderConstants.V_FUNC_SPACE);
				}
				sqlString.append(IBuilderConstants.V_FUNC_XML_ELEMENT);
				sqlString.append(IBuilderConstants.V_FUNC_OPEN);
				sqlString.append(IBuilderConstants.V_FUNC_SPACE);
				sqlString.append(IBuilderConstants.V_FUNC_NAME);
				sqlString.append(IBuilderConstants.V_FUNC_SPACE);
				sqlString.append(param.getName());
				sqlString.append(IBuilderConstants.V_FUNC_COMMA);
				sqlString.append(IBuilderConstants.V_FUNC_SPACE);
				sqlString.append(param.getName());
				sqlString.append(IBuilderConstants.V_FUNC_CLOSE);
				firstTime = false;
			}
			sqlString.append(IBuilderConstants.V_FUNC_CLOSE);
			sqlString.append(IBuilderConstants.V_FUNC_SPACE);
			sqlString.append(IBuilderConstants.V_FUNC_AS_XML_OUT);
			sqlString.append(IBuilderConstants.V_FUNC_SPACE);
			sqlString.append(IBuilderConstants.V_FUNC_POSTSCRIPT);

			SqlTransformationMappingRoot root = (SqlTransformationMappingRoot) TransformationHelper
					.getTransformationMappingRoot(procedure);
			TransformationHelper.setSqlString(root, sqlString.toString(),
					QueryValidator.SELECT_TRNS, true, this);
			TransformationMappingHelper.reconcileMappingsOnSqlChange(
					(EObject) root, this);
		}
	}
}
