package com.metamatrix.modeler.modelgenerator.xsd.procedures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.modelgenerator.xml.XmlImporterUiPlugin;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.impl.ElementImpl;
import com.metamatrix.modeler.schema.tools.model.schema.impl.TypeDefinition;

public class ProcedureBuilder {

	RelationalFactory factory = com.metamatrix.metamodels.relational.RelationalPackage.eINSTANCE
	.getRelationalFactory();
	private Set<String> procedures = new HashSet<String>();
	private Schema operationSchema;
	private ITraversalCtxFactory traversalCtxFactory;
	private ModelResource modelResource;
	private List<TraversalContext> traversalContexts = new ArrayList();
	
	public ProcedureBuilder(Schema schema, ModelResource resource) {
		operationSchema = schema;
		modelResource = resource;
	}

	public boolean procedureExists(String name) {
		return procedures.contains(name);
	}
	
	public boolean addProcedure(String name) {
		return procedures.add(name);
	}

	public void build(List<XSDElementDeclaration> elements, ITraversalCtxFactory traversalCtxFactory)
			throws ModelerCoreException {
		CoreArgCheck.isTrue(elements.size() == 1, 
				XmlImporterUiPlugin.getDefault().getPluginUtil().getString("ProcedureBuilder.wrongNumberElementsReturnedFromXsd")); //$NON-NLS-1$
		XSDElementDeclaration element = elements.get(0);
		this.traversalCtxFactory = traversalCtxFactory;
		TraversalContext ctx = this.traversalCtxFactory.getTraversalContext(element.getName(), new QName(element.getTargetNamespace(), StringUtilities.EMPTY_STRING), this);
		traversalContexts.add(ctx);
		XSDTypeDefinition type = element.getType();
		String name = getName(type);
		if (!procedureExists(name)) {
			addProcedure(name);
			if (type instanceof XSDComplexTypeDefinition) {
				ctx.appendToPath(element.getName());
				XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) type;
				addComplexTypeDefToProcedureResult(complexType, ctx);
			} else if (type instanceof XSDSimpleTypeDefinition) {
				XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) element.getType();
				addElementDeclarationToProcedureResult(element, simpleType, ctx);
			}
		}
	}
	
	public void build(SchemaObject sObject, ITraversalCtxFactory traversalCtxFactory)
	throws ModelerCoreException {
		this.traversalCtxFactory = traversalCtxFactory;
		TraversalContext ctx = this.traversalCtxFactory.getTraversalContext(sObject.getName(), new QName(sObject.getNamespace(), StringUtilities.EMPTY_STRING), this);
		traversalContexts.add(ctx);
		XSDTypeDefinition type = sObject.getType();
		String name = getName(type);
		if (!procedureExists(name)) {
			addProcedure(name);
			if (type instanceof XSDComplexTypeDefinition) {
				ctx.appendToPath(sObject.getName());
				XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) type;
				addComplexTypeDefToProcedureResult(complexType, ctx);
			} else if (type instanceof XSDSimpleTypeDefinition) {
				if(sObject instanceof TypeDefinition) {
					XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) sObject.getType();
					addSimpleTypeDefToProcedureResult(simpleType, ctx);
				} else {
					XSDElementDeclaration element = ((ElementImpl) sObject)
					.getElem();
					XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) element.getType();
					addElementDeclarationToProcedureResult(element, simpleType, ctx);
				}
			}
		}
	}
	
	public void build(XSDSimpleTypeDefinition simpleType, String name,
			ITraversalCtxFactory traversalCtxFactory) throws ModelerCoreException {
		this.traversalCtxFactory = traversalCtxFactory;
		TraversalContext ctx = this.traversalCtxFactory.getTraversalContext(name, new QName(simpleType.getTargetNamespace(), simpleType.getName()), this);
		traversalContexts.add(ctx);
		addSimpleTypeDefToProcedureResult(simpleType, name, ctx);
	}
	
	public void createTransformations() {
		for (TraversalContext context : traversalContexts) {
			context.createTransformation();
		}
	}

	private String getName(XSDTypeDefinition type) {
		String name = type.getName();
		if(null == name) {
			name = type.getAliasName();
		}
		return name;
	}
	
	private void addElementDeclarationToProcedureResult(XSDElementDeclaration element, XSDSimpleTypeDefinition type, TraversalContext ctx) throws ModelerCoreException {
		String name = element.getName();
		if(null == name) {
			name = element.getAliasName();
		}
		ctx.addColumn(name, type);
		
	}

	private void addComplexTypeDefToProcedureResult(XSDComplexTypeDefinition complexType, TraversalContext ctx) throws ModelerCoreException {
		XSDComplexTypeContent content = complexType.getContent();
		if(content instanceof XSDSimpleTypeDefinition) {
			XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) content;
			addSimpleTypeDefToProcedureResult(simpleType, ctx);
		} else if(content instanceof XSDParticle) {
			XSDParticle particle = (XSDParticle) content;
			addXSDParticleToProcedureResult(particle, ctx);
		}
	}

	private void addXSDParticleToProcedureResult(XSDParticle particle, TraversalContext ctx) throws ModelerCoreException {
		XSDParticleContent content = particle.getContent();
		if(content instanceof XSDWildcard) {
			//nothing
		} else if (content instanceof XSDModelGroup) {
			XSDModelGroup group = (XSDModelGroup) content;
			EList<XSDParticle> contents = group.getContents();
			for (XSDParticle xsdParticle : contents) {
				addXSDParticleToProcedureResult(xsdParticle, ctx);
			}	
		} else if (content instanceof XSDElementDeclaration) {
			XSDElementDeclaration element = (XSDElementDeclaration) content;
			if(element.isElementDeclarationReference()) {
				element = element.getResolvedElementDeclaration();
			}
			if(element.getType() instanceof XSDSimpleTypeDefinition) {
				XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition) element.getType();
				addElementDeclarationToProcedureResult(element, type, ctx);
			} else {
				XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) element.getType();
				String name = element.getName();
				if(null == name) {
					name = element.getAliasName();
				}
				
				if(procedureExists(name)) {
					return;
				}
				ctx.appendToPath(name);
				if(particle.getMaxOccurs() > 1 || particle.getMaxOccurs() == -1 || ctx.isReachedResultNode()) {
					
					ctx = traversalCtxFactory.getTraversalContext(name, new QName(element.getTargetNamespace(), StringUtilities.EMPTY_STRING), ctx, this);
					traversalContexts.add(ctx);
					if(procedureExists(getName(type))){
						return;
					}
					
				}
				addComplexTypeDefToProcedureResult(type, ctx);
			}
		}else if (content instanceof XSDModelGroupDefinition) {
			XSDModelGroupDefinition groupDefinition = (XSDModelGroupDefinition) content;
			XSDModelGroup group = groupDefinition.getModelGroup();
			EList<XSDParticle> contents = group.getContents();
			for (XSDParticle xsdParticle : contents) {
				addXSDParticleToProcedureResult(xsdParticle, ctx);
			}
		}		
	}

	private void addSimpleTypeDefToProcedureResult(XSDSimpleTypeDefinition simpleType, TraversalContext ctx) throws ModelerCoreException {
		String name = simpleType.getName();
		if(null == name) {
			name = simpleType.getAliasName();
		}
		ctx.addColumn(name, simpleType);
		ctx.setReachedResultNode(true);
	}
	
	private void addSimpleTypeDefToProcedureResult(XSDSimpleTypeDefinition simpleType, String name, TraversalContext ctx) throws ModelerCoreException {
		ctx.addColumn(name, simpleType);
		ctx.setReachedResultNode(true);
	}

	public Schema getSchema() {
		return operationSchema;
	}

	public ModelResource getModelResource() {
		return modelResource;
	}
}
