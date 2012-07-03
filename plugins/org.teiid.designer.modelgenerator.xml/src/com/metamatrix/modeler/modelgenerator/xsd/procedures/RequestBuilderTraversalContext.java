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

public class RequestBuilderTraversalContext extends BaseTraversalContext
		implements TraversalContext {

	public static final String REQUEST = "request_"; //$NON-NLS-1$
	public static final String XML_OUT = "xml_out"; //$NON-NLS-1$

	private Procedure procedure;
	private List<ProcedureParameter> cachedParams = new ArrayList();
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
		String uniqueName = getUniqueName(param, NameUtil.normalizeName(name));
		param.setName(uniqueName);
		param.setNameInSource(uniqueName);
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
		String uniqueName = getUniqueName(procedure, REQUEST + NameUtil.normalizeName(procedureNameBase));
		procedure.setName(uniqueName);
		procedure.setNameInSource(procedureNameBase);
		builder.addProcedure(procedureNameBase);

		procedureResult = factory.createProcedureResult();
		procedure.setResult(procedureResult);
		procedureResult.setName(NameUtil.normalizeName(procedureNameBase) + IBuilderConstants.V_FUNC_RESULT);
		procedureResult.setNameInSource(procedureNameBase);
		Column resultCol = factory.createColumn();
		procedureResult.getColumns().add(resultCol);
		resultCol.setName(XML_OUT);
		resultCol
				.setType(datatypeManager
						.getBuiltInDatatype(DatatypeConstants.BuiltInNames.XML_LITERAL));

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
			sqlString.append(IBuilderConstants.V_FUNC_DOUBLE_QUOTE);
			sqlString.append(procedureResult.getNameInSource());
			sqlString.append(IBuilderConstants.V_FUNC_DOUBLE_QUOTE);
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
				sqlString.append(IBuilderConstants.V_FUNC_DOUBLE_QUOTE);
				sqlString.append(param.getNameInSource());
				sqlString.append(IBuilderConstants.V_FUNC_DOUBLE_QUOTE);
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
	
	private String getUniqueName(final EObject eObject, final String proposedName) {
		CoreArgCheck.isNotNull(eObject);
		final EStructuralFeature nameFeature = ModelerCore.getModelEditor().getNameFeature(eObject);
		if (nameFeature != null) {
			return generateUniqueInternalName(
					eObject.eContainer() == null ? eObject.eResource().getContents() : eObject.eContainer().eContents(),
					eObject, nameFeature, proposedName);
		}
		return proposedName;
	}

	private String generateUniqueInternalName(final EList siblings,
			final EObject eObject, final EStructuralFeature nameFeature,
			final String name) {
		String newName = name;
		if (siblings != null) {
			final Set siblingNames = new HashSet();
			for (Iterator it = siblings.iterator(); it.hasNext();) {
				final EObject child = (EObject) it.next();
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
