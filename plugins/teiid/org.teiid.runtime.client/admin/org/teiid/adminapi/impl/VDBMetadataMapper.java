/*ode.Id_ADD
 * JBoss, Home of Professional Open != Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.teiid.adminapi.impl;

import java.util.List;
import java.util.Map;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.ObjectListAttributeDefinition;
import org.jboss.as.controller.ObjectTypeAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.StringListAttributeDefinition;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.Request.ProcessingState;
import org.teiid.adminapi.Request.ThreadState;
import org.teiid.adminapi.VDB.ConnectionType;
import org.teiid.adminapi.VDB.Status;
import org.teiid.adminapi.impl.DataPolicyMetadata.PermissionMetaData;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.Messages.VDBMetadata;
 
public class VDBMetadataMapper implements MetadataMapper<VDBMetaData> {
	private static final String VDBNAME = "vdb-name"; //$NON-NLS-1$
	private static final String CONNECTIONTYPE = "connection-type"; //$NON-NLS-1$
	private static final String STATUS = "status"; //$NON-NLS-1$
	private static final String VERSION = "vdb-version"; //$NON-NLS-1$
	private static final String MODELS = "models"; //$NON-NLS-1$
	private static final String IMPORT_VDBS = "import-vdbs"; //$NON-NLS-1$
	private static final String OVERRIDE_TRANSLATORS = "override-translators"; //$NON-NLS-1$
	private static final String VDB_DESCRIPTION = "vdb-description"; //$NON-NLS-1$
	private static final String PROPERTIES = "properties"; //$NON-NLS-1$
	private static final String XML_DEPLOYMENT = "xml-deployment"; //$NON-NLS-1$
	private static final String DATA_POLICIES = "data-policies"; //$NON-NLS-1$
	private static final String DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String ENTRIES = "entries"; //$NON-NLS-1$
	
	public static VDBMetadataMapper INSTANCE = new VDBMetadataMapper();

	@Override
    public VDBMetaData unwrap(ModelNode node) {
		if (node == null)
			return null;
			
		VDBMetaData vdb = new VDBMetaData();
		if (node.has(VDBNAME)) {
			vdb.setName(node.get(VDBNAME).asString());
		}
		if (node.has(CONNECTIONTYPE)) {
			vdb.setConnectionType(node.get(CONNECTIONTYPE).asString());
		}
		if (node.has(STATUS)) {
			vdb.setStatus(node.get(STATUS).asString());
		}
		if (node.has(VERSION)) {
			vdb.setVersion(node.get(VERSION).asInt());
		}
		if(node.has(VDB_DESCRIPTION)) {
			vdb.setDescription(node.get(VDB_DESCRIPTION).asString());
		}
		if (node.has(XML_DEPLOYMENT)) {
			vdb.setXmlDeployment(node.get(XML_DEPLOYMENT).asBoolean());
		}

		//PROPERTIES
		if (node.get(PROPERTIES).isDefined()) {
			List<ModelNode> propNodes = node.get(PROPERTIES).asList();
			for (ModelNode propNode:propNodes) {
				String[] prop = PropertyMetaDataMapper.INSTANCE.unwrap(propNode);
				if (prop != null) {
					vdb.addProperty(prop[0], prop[1]);
				}
			}
		}
		
		// IMPORT-VDBS
		if (node.get(IMPORT_VDBS).isDefined()) {
			List<ModelNode> modelNodes = node.get(IMPORT_VDBS).asList();
			for(ModelNode modelNode:modelNodes) {
				VDBImportMetadata vdbImport = VDBImportMapper.INSTANCE.unwrap(modelNode);
				if (vdbImport != null) {
					vdb.getVDBImports().add(vdbImport);	
				}
			}
		}
		
		// ENTRIES
		if (node.get(ENTRIES).isDefined()) {
			List<ModelNode> modelNodes = node.get(ENTRIES).asList();
			for(ModelNode modelNode:modelNodes) {
				EntryMetaData entry = EntryMapper.INSTANCE.unwrap(modelNode);
				if (entry != null) {
					vdb.getEntries().add(entry);	
				}
			}
		}		
		
		// MODELS
		if (node.get(MODELS).isDefined()) {
			List<ModelNode> modelNodes = node.get(MODELS).asList();
			for(ModelNode modelNode:modelNodes) {
				ModelMetaData model = ModelMetadataMapper.INSTANCE.unwrap(modelNode);
				if (model != null) {
					vdb.addModel(model);	
				}
			}
		}
		
		// OVERRIDE_TRANSLATORS
		if (node.get(OVERRIDE_TRANSLATORS).isDefined()) {
			List<ModelNode> translatorNodes = node.get(OVERRIDE_TRANSLATORS).asList();
			for (ModelNode translatorNode:translatorNodes) {
				VDBTranslatorMetaData translator = VDBTranslatorMetaDataMapper.INSTANCE.unwrap(translatorNode);
				if (translator != null) {
					vdb.addOverideTranslator(translator);
				}
			}
		}
		
		// DATA_POLICIES
		if (node.get(DATA_POLICIES).isDefined()) {
			List<ModelNode> policiesNodes = node.get(DATA_POLICIES).asList();
			for (ModelNode policyNode:policiesNodes) {
				DataPolicyMetadata policy = DataPolicyMetadataMapper.INSTANCE.unwrap(policyNode);
				if (policy != null) {
					vdb.addDataPolicy(policy);	
				}
				
			}
		}
		unwrapDomain(vdb, node);
		return vdb;
	}
	
	@Override
    public ModelNode describe(ModelNode node) {
		addAttribute(node, VDBNAME, ModelType.STRING, true); 

		ModelNode connectionsAllowed = new ModelNode();
		connectionsAllowed.add(ConnectionType.NONE.toString());
		connectionsAllowed.add(ConnectionType.ANY.toString());
		connectionsAllowed.add(ConnectionType.BY_VERSION.toString());
		addAttribute(node, CONNECTIONTYPE, ModelType.STRING, false);
		node.get(CONNECTIONTYPE).get(ALLOWED).set(connectionsAllowed);
		
		ModelNode statusAllowed = new ModelNode();
		statusAllowed.add(Status.ACTIVE.toString());
		statusAllowed.add(Status.LOADING.toString());
		statusAllowed.add(Status.FAILED.toString());
		statusAllowed.add(Status.REMOVED.toString());
		addAttribute(node, STATUS, ModelType.STRING, true);
		node.get(STATUS).get(ALLOWED).set(statusAllowed);
		
		addAttribute(node, VERSION, ModelType.INT, true);
		addAttribute(node, VDB_DESCRIPTION, ModelType.STRING, false);
		addAttribute(node, XML_DEPLOYMENT, ModelType.BOOLEAN, false);
		
		ModelNode props = node.get(PROPERTIES);
		props.get(TYPE).set(ModelType.LIST);
		props.get(DESCRIPTION).set(Messages.getString(Messages.VDBMetadata.properties_describe));
		PropertyMetaDataMapper.INSTANCE.describe(props.get(VALUE_TYPE));

		ModelNode vdbImports = node.get(IMPORT_VDBS);	
		vdbImports.get(TYPE).set(ModelType.LIST);
		VDBImportMapper.INSTANCE.describe(vdbImports.get(VALUE_TYPE));
		vdbImports.get(DESCRIPTION).set(Messages.getString(Messages.VDBMetadata.import_vdbs_describe));
		
		ModelNode models = node.get( MODELS);	
		models.get(TYPE).set(ModelType.LIST);
		ModelMetadataMapper.INSTANCE.describe(models.get(VALUE_TYPE));
		models.get(DESCRIPTION).set(Messages.getString(Messages.VDBMetadata.models_describe));
		
		ModelNode translators = node.get(OVERRIDE_TRANSLATORS);
		translators.get(TYPE).set(ModelType.LIST);
		translators.get(DESCRIPTION).set(Messages.getString(Messages.VDBMetadata.override_translators_describe));
		VDBTranslatorMetaDataMapper.INSTANCE.describe(translators.get(VALUE_TYPE));
		
		ModelNode dataPolicies = node.get(DATA_POLICIES);
		dataPolicies.get(TYPE).set(ModelType.LIST);
		dataPolicies.get(DESCRIPTION).set(Messages.getString(Messages.VDBMetadata.data_policies_describe));
		DataPolicyMetadataMapper.INSTANCE.describe(dataPolicies.get(VALUE_TYPE));
		return node;
	}
	
	
	public AttributeDefinition[] getAttributeDefinitions() {
		ObjectListAttributeDefinition properties = ObjectListAttributeDefinition.Builder.of(PROPERTIES, PropertyMetaDataMapper.INSTANCE.getAttributeDefinition()).build();
		ObjectListAttributeDefinition vdbimports = ObjectListAttributeDefinition.Builder.of(IMPORT_VDBS, VDBImportMapper.INSTANCE.getAttributeDefinition()).build();
		ObjectListAttributeDefinition models = ObjectListAttributeDefinition.Builder.of(MODELS, ModelMetadataMapper.INSTANCE.getAttributeDefinition()).build();
		ObjectListAttributeDefinition translators = ObjectListAttributeDefinition.Builder.of(OVERRIDE_TRANSLATORS, VDBTranslatorMetaDataMapper.INSTANCE.getAttributeDefinition()).build();
		ObjectListAttributeDefinition policies = ObjectListAttributeDefinition.Builder.of(DATA_POLICIES, DataPolicyMetadataMapper.INSTANCE.getAttributeDefinition()).build();
		
		return new AttributeDefinition[] {
				new SimpleAttributeDefinition(VDBNAME, ModelType.STRING, false),
				new SimpleAttributeDefinition(CONNECTIONTYPE, ModelType.INT, false),
				new SimpleAttributeDefinition(STATUS, ModelType.BOOLEAN, false),
				new SimpleAttributeDefinition(VERSION, ModelType.BOOLEAN, false),
				new SimpleAttributeDefinition(VDB_DESCRIPTION, ModelType.BOOLEAN, true),
				new SimpleAttributeDefinition(XML_DEPLOYMENT, ModelType.BOOLEAN, true),
				properties,
				vdbimports,
				models,
				translators,
				policies
			};
	}	
	
	private static void addProperties(ModelNode node, AdminObjectImpl object) {
		Map<String, String> properties = object.getPropertiesMap();
		if (properties!= null && !properties.isEmpty()) {
			ModelNode propsNode = node.get(PROPERTIES); 
			for (Map.Entry<String, String> entry : properties.entrySet()) {
				propsNode.add(PropertyMetaDataMapper.INSTANCE.wrap(entry.getKey(), entry.getValue(), new ModelNode()));
			}
		}
	}
	
	/**
	 * model metadata mapper
	 */
	public static class ModelMetadataMapper implements MetadataMapper<ModelMetaData>{
		private static final String MODEL_NAME = "model-name"; //$NON-NLS-1$
		private static final String DESCRIPTION = "description"; //$NON-NLS-1$
		private static final String VISIBLE = "visible"; //$NON-NLS-1$
		private static final String MODEL_TYPE = "model-type"; //$NON-NLS-1$
		private static final String MODELPATH = "model-path"; //$NON-NLS-1$
		private static final String PROPERTIES = "properties"; //$NON-NLS-1$
		private static final String SOURCE_MAPPINGS = "source-mappings"; //$NON-NLS-1$
		private static final String VALIDITY_ERRORS = "validity-errors"; //$NON-NLS-1$
		private static final String METADATA= "metadata"; //$NON-NLS-1$
		private static final String METADATA_TYPE = "metadata-type"; //$NON-NLS-1$
		private static final String METADATA_STATUS = "metadata-status"; //$NON-NLS-1$
		
		
		public static ModelMetadataMapper INSTANCE = new ModelMetadataMapper();

		@Override
        public ModelMetaData unwrap(ModelNode node) {
			if (node == null) {
				return null;
			}
			
			ModelMetaData model = new ModelMetaData();
			if (node.has(MODEL_NAME)) {
				model.setName(node.get(MODEL_NAME).asString());
			}
			if (node.has(DESCRIPTION)) {
				model.setDescription(node.get(DESCRIPTION).asString());
			}
			if (node.has(VISIBLE)) {
				model.setVisible(node.get(VISIBLE).asBoolean());
			}
			if(node.has(MODEL_TYPE)) {
				model.setModelType(node.get(MODEL_TYPE).asString());
			}
			if(node.has(MODELPATH)) {
				model.setPath(node.get(MODELPATH).asString());
			}

			if (node.get(PROPERTIES).isDefined()) {
				List<ModelNode> propNodes = node.get(PROPERTIES).asList();
				for (ModelNode propNode:propNodes) {
					String[] prop = PropertyMetaDataMapper.INSTANCE.unwrap(propNode);
					if (prop != null) {
						model.addProperty(prop[0], prop[1]);
					}
				}
			}
		
			if (node.get(SOURCE_MAPPINGS).isDefined()) {
				List<ModelNode> sourceMappingNodes = node.get(SOURCE_MAPPINGS).asList();
				for (ModelNode sourceMapping:sourceMappingNodes) {
					SourceMappingMetadata source = SourceMappingMetadataMapper.INSTANCE.unwrap(sourceMapping);
					if (source != null) {
						model.addSourceMapping(source);
					}
				}
			}
			
			if (node.get(VALIDITY_ERRORS).isDefined()) {
				List<ModelNode> errorNodes = node.get(VALIDITY_ERRORS).asList();
				for(ModelNode errorNode:errorNodes) {
				    ModelMetaData.Message error = ValidationErrorMapper.INSTANCE.unwrap(errorNode);
					if (error != null) {
						model.addMessage(error);
					}
				}
			}
			if (node.get(METADATA).isDefined()) {
				model.setSchemaText(node.get(METADATA).asString());
			}
			if (node.get(METADATA_TYPE).isDefined()) {
				model.setSchemaSourceType(node.get(METADATA_TYPE).asString());
			}
			if (node.get(METADATA_STATUS).isDefined()) {
				model.setMetadataStatus(node.get(METADATA_STATUS).asString());
			}			
			return model;
		}
		
		@Override
        public ModelNode describe(ModelNode node) {
			ModelNode modelTypes = new ModelNode();
			modelTypes.add(Model.Type.PHYSICAL.toString());
			modelTypes.add(Model.Type.VIRTUAL.toString());
			modelTypes.add(Model.Type.FUNCTION.toString());
			modelTypes.add(Model.Type.OTHER.toString());
			addAttribute(node, MODEL_NAME, ModelType.STRING, true);
			
			
			addAttribute(node, DESCRIPTION, ModelType.STRING, false);
			addAttribute(node, VISIBLE, ModelType.BOOLEAN, false);
			addAttribute(node, MODEL_TYPE, ModelType.STRING, true);
			node.get(MODEL_TYPE).get(ALLOWED).set(modelTypes);
			addAttribute(node, MODELPATH, ModelType.STRING, false);
			
			ModelNode props = node.get(PROPERTIES);
			props.get(TYPE).set(ModelType.LIST);
			props.get(DESCRIPTION).set(Messages.getString(Messages.VDBMetadata.properties_describe));
			PropertyMetaDataMapper.INSTANCE.describe(props.get(VALUE_TYPE));

			ModelNode source = node.get(SOURCE_MAPPINGS);
			source.get(TYPE).set(ModelType.LIST);
			source.get(DESCRIPTION).set(Messages.getString(Messages.VDBMetadata.source_mappings_describe));
			SourceMappingMetadataMapper.INSTANCE.describe(source.get(VALUE_TYPE));

			ModelNode errors = node.get(VALIDITY_ERRORS);
			errors.get(TYPE).set(ModelType.LIST);
			errors.get(DESCRIPTION).set(Messages.getString(Messages.VDBMetadata.validity_errors_describe));
			ValidationErrorMapper.INSTANCE.describe(errors.get(VALUE_TYPE));
			
			ModelNode status = new ModelNode();
			status.add(Model.MetadataStatus.LOADING.name());
			status.add(Model.MetadataStatus.LOADED.name());
			status.add(Model.MetadataStatus.FAILED.name());
			status.add(Model.MetadataStatus.RETRYING.name());
			addAttribute(node, METADATA_STATUS, ModelType.STRING, true);
			node.get(METADATA_STATUS).get(ALLOWED).set(status);
			return node; 
		}
		
		public ObjectTypeAttributeDefinition getAttributeDefinition() {
			ObjectListAttributeDefinition properties = ObjectListAttributeDefinition.Builder.of(PROPERTIES, PropertyMetaDataMapper.INSTANCE.getAttributeDefinition()).build();
			ObjectListAttributeDefinition sourceMappings = ObjectListAttributeDefinition.Builder.of(SOURCE_MAPPINGS, SourceMappingMetadataMapper.INSTANCE.getAttributeDefinition()).build();
			ObjectListAttributeDefinition errors = ObjectListAttributeDefinition.Builder.of(VALIDITY_ERRORS, ValidationErrorMapper.INSTANCE.getAttributeDefinition()).build();
			
			return ObjectTypeAttributeDefinition.Builder.of("ModelMetadataMapper", //$NON-NLS-1$
				new AttributeDefinition[] {
					new SimpleAttributeDefinition(MODEL_NAME, ModelType.STRING, false),
					new SimpleAttributeDefinition(DESCRIPTION, ModelType.INT, true),
					new SimpleAttributeDefinition(VISIBLE, ModelType.INT, true),
					new SimpleAttributeDefinition(MODEL_TYPE, ModelType.BOOLEAN, false),
					new SimpleAttributeDefinition(MODELPATH, ModelType.BOOLEAN, true),
					new SimpleAttributeDefinition(METADATA_STATUS, ModelType.STRING, true),
					ObjectTypeAttributeDefinition.Builder.of(PROPERTIES, properties).build(),
					ObjectTypeAttributeDefinition.Builder.of(SOURCE_MAPPINGS, sourceMappings).build(),
					ObjectTypeAttributeDefinition.Builder.of(VALIDITY_ERRORS, errors).build(),
			}).build();
		}		
	}	
	
	/**
	 * vdb import mapper
	 */
	public static class VDBImportMapper implements MetadataMapper<VDBImportMetadata>{
		private static final String VDB_NAME = "import-vdb-name"; //$NON-NLS-1$
		private static final String VDB_VERSION = "import-vdb-version"; //$NON-NLS-1$
		private static final String IMPORT_POLICIES = "import-policies"; //$NON-NLS-1$
		
		public static VDBImportMapper INSTANCE = new VDBImportMapper();

		@Override
        public VDBImportMetadata unwrap(ModelNode node) {
			if (node == null) {
				return null;
			}
			
			VDBImportMetadata vdbImport = new VDBImportMetadata();
			if (node.has(VDB_NAME)) {
				vdbImport.setName(node.get(VDB_NAME).asString());
			}
			if (node.has(VDB_VERSION)) {
				vdbImport.setVersion(node.get(VDB_VERSION).asInt());
			}
			if (node.has(IMPORT_POLICIES)) {
				vdbImport.setImportDataPolicies(node.get(IMPORT_POLICIES).asBoolean());
			}
			return vdbImport;
		}
		
		@Override
        public ModelNode describe(ModelNode node) {
			addAttribute(node, VDB_NAME, ModelType.STRING, true);
			addAttribute(node, VDB_VERSION, ModelType.INT, true);
			addAttribute(node, IMPORT_POLICIES, ModelType.BOOLEAN, false);
			return node; 
		}
		
		public ObjectTypeAttributeDefinition getAttributeDefinition() {
			return ObjectTypeAttributeDefinition.Builder.of("VDBImportMapper", //$NON-NLS-1$
				new AttributeDefinition[] {
					new SimpleAttributeDefinition(VDB_NAME, ModelType.STRING, false),
					new SimpleAttributeDefinition(VDB_VERSION, ModelType.INT, false),
					new SimpleAttributeDefinition(IMPORT_POLICIES, ModelType.BOOLEAN, true)
			}).build();
		}
	}	
	
	/**
	 * validation error mapper
	 */
	public static class ValidationErrorMapper implements MetadataMapper<ModelMetaData.Message>{
		private static final String ERROR_PATH = "error-path"; //$NON-NLS-1$
		private static final String SEVERITY = "severity"; //$NON-NLS-1$
		private static final String MESSAGE = "message"; //$NON-NLS-1$
		
		
		public static ValidationErrorMapper INSTANCE = new ValidationErrorMapper();
		
		@Override
        public ModelMetaData.Message unwrap(ModelNode node) {
			if (node == null) {
				return null;
			}
			
			ModelMetaData.Message error = new ModelMetaData.Message();
			if (node.has(ERROR_PATH)) {
				error.setPath(node.get(ERROR_PATH).asString());
			}
			if (node.has(SEVERITY)) {
				error.setSeverity(ModelMetaData.Message.Severity.valueOf(node.get(SEVERITY).asString()));
			}
			if(node.has(MESSAGE)) {
				error.setValue(node.get(MESSAGE).asString());
			}
			return error;
		}
		
		@Override
        public ModelNode describe(ModelNode node) {
			addAttribute(node, ERROR_PATH, ModelType.STRING, false); 
			addAttribute(node, SEVERITY, ModelType.STRING, true);
			addAttribute(node, MESSAGE, ModelType.STRING, true);
			return node; 
		}
		
		public ObjectTypeAttributeDefinition getAttributeDefinition() {
			return ObjectTypeAttributeDefinition.Builder.of("ValidationErrorMapper", //$NON-NLS-1$
				new AttributeDefinition[] {
					new SimpleAttributeDefinition(ERROR_PATH, ModelType.STRING, true), 
					new SimpleAttributeDefinition(SEVERITY, ModelType.STRING, false),
					new SimpleAttributeDefinition(MESSAGE, ModelType.STRING, false)
			}).build();
		}
	}		
	
	/**
	 * Source Mapping Metadata mapper
	 */
	public static class SourceMappingMetadataMapper implements MetadataMapper<SourceMappingMetadata>{
		private static final String SOURCE_NAME = "source-name"; //$NON-NLS-1$
		private static final String JNDI_NAME = "jndi-name"; //$NON-NLS-1$
		private static final String TRANSLATOR_NAME = "translator-name"; //$NON-NLS-1$
		
		public static SourceMappingMetadataMapper INSTANCE = new SourceMappingMetadataMapper();
		
		@Override
        public SourceMappingMetadata unwrap(ModelNode node) {
			if (node == null) {
				return null;
			}
			SourceMappingMetadata source = new SourceMappingMetadata();
			if (node.has(SOURCE_NAME)) {
				source.setName(node.get(SOURCE_NAME).asString());
			}
			if (node.has(JNDI_NAME)) {
				source.setConnectionJndiName(node.get(JNDI_NAME).asString());
			}
			if (node.has(TRANSLATOR_NAME)) {
				source.setTranslatorName(node.get(TRANSLATOR_NAME).asString());
			}
			return source;
		}
		
		@Override
        public ModelNode describe(ModelNode node) {
			addAttribute(node, SOURCE_NAME, ModelType.STRING, true); 
			addAttribute(node, JNDI_NAME, ModelType.STRING, true);
			addAttribute(node, TRANSLATOR_NAME, ModelType.STRING, true);
			return node; 
		}		
		
		public ObjectTypeAttributeDefinition getAttributeDefinition() {
			return ObjectTypeAttributeDefinition.Builder.of("SourceMappingMetadataMapper", //$NON-NLS-1$
				new AttributeDefinition[] {
					new SimpleAttributeDefinition(SOURCE_NAME, ModelType.STRING, false),
					new SimpleAttributeDefinition(JNDI_NAME, ModelType.STRING, false),
					new SimpleAttributeDefinition(TRANSLATOR_NAME, ModelType.STRING, false)
			}).build();
		}		
	}		
	
	/**
	 * Source Mapping Metadata mapper
	 */
	public static class VDBTranslatorMetaDataMapper implements MetadataMapper<VDBTranslatorMetaData>{
		private static final String TRANSLATOR_NAME = "translator-name"; //$NON-NLS-1$
		private static final String BASETYPE = "base-type"; //$NON-NLS-1$
		private static final String TRANSLATOR_DESCRIPTION = "translator-description"; //$NON-NLS-1$
		private static final String PROPERTIES = "properties"; //$NON-NLS-1$
		private static final String MODULE_NAME = "module-name"; //$NON-NLS-1$
		
		
		public static VDBTranslatorMetaDataMapper INSTANCE = new VDBTranslatorMetaDataMapper();

		@Override
        public VDBTranslatorMetaData unwrap(ModelNode node) {
			if (node == null) {
				return null;
			}
			VDBTranslatorMetaData translator = new VDBTranslatorMetaData();
			if (node.has(TRANSLATOR_NAME)) {
				translator.setName(node.get(TRANSLATOR_NAME).asString());
			}
			if (node.has(BASETYPE)) {
				translator.setType(node.get(BASETYPE).asString());
			}
			if (node.has(TRANSLATOR_DESCRIPTION)) {
				translator.setDescription(node.get(TRANSLATOR_DESCRIPTION).asString());
			}
			if (node.has(MODULE_NAME)) {
				translator.setModuleName(node.get(MODULE_NAME).asString());
			}
			
			if (node.get(PROPERTIES).isDefined()) {
				List<ModelNode> propNodes = node.get(PROPERTIES).asList();
				for (ModelNode propNode:propNodes) {
					String[] prop = PropertyMetaDataMapper.INSTANCE.unwrap(propNode);
					if (prop != null) {
						translator.addProperty(prop[0], prop[1]);
					}
				}
			}
			unwrapDomain(translator, node);
			return translator;
		}
		
		@Override
        public ModelNode describe(ModelNode node) {
			addAttribute(node, TRANSLATOR_NAME, ModelType.STRING, true); 
			addAttribute(node, BASETYPE, ModelType.STRING, true);
			addAttribute(node, TRANSLATOR_DESCRIPTION, ModelType.STRING, false);
			addAttribute(node, MODULE_NAME, ModelType.STRING, false);
			
			ModelNode props = node.get(PROPERTIES);
			props.get(TYPE).set(ModelType.LIST);
			props.get(DESCRIPTION).set(Messages.getString(Messages.VDBMetadata.properties_describe));
			PropertyMetaDataMapper.INSTANCE.describe(props.get(VALUE_TYPE));
			return node; 
		}	
		
		public ObjectTypeAttributeDefinition getAttributeDefinition() {
			return ObjectTypeAttributeDefinition.Builder.of("VDBTranslatorMetaDataMapper", //$NON-NLS-1$
					getAttributeDefinitions()).build();
		}		
		
		public AttributeDefinition[] getAttributeDefinitions() {
			ObjectListAttributeDefinition properties = ObjectListAttributeDefinition.Builder.of(PROPERTIES, PropertyMetaDataMapper.INSTANCE.getAttributeDefinition()).build();
			return new AttributeDefinition[] {
					new SimpleAttributeDefinition(TRANSLATOR_NAME, ModelType.STRING, false),
					new SimpleAttributeDefinition(BASETYPE, ModelType.STRING, false),
					new SimpleAttributeDefinition(TRANSLATOR_DESCRIPTION, ModelType.STRING, true),
					new SimpleAttributeDefinition(MODULE_NAME, ModelType.STRING, true),	
					properties
			};
		}		
	}	
	
	
	/**
	 * Property Metadata mapper
	 */
	public static class PropertyMetaDataMapper {
		private static final String PROPERTY_NAME = "property-name"; //$NON-NLS-1$
		private static final String PROPERTY_VALUE = "property-value"; //$NON-NLS-1$
		
		public static PropertyMetaDataMapper INSTANCE = new PropertyMetaDataMapper();
		
		public ModelNode wrap(String key, String value, ModelNode node) {
			node.get(PROPERTY_NAME).set(key);
			node.get(PROPERTY_VALUE).set(value);
			return node;
		}
		
		public String[] unwrap(ModelNode node) {
			if(node == null) {
				return null;
			}
			String key = null;
			String value = null;
			if (node.has(PROPERTY_NAME)) {
				key = node.get(PROPERTY_NAME).asString();
			}
			if(node.has(PROPERTY_VALUE)) {
				value = node.get(PROPERTY_VALUE).asString();
			}
			return new String[] {key, value};
		}
		
		public ModelNode describe(ModelNode node) {
			addAttribute(node, PROPERTY_NAME, ModelType.STRING, true);
			addAttribute(node, PROPERTY_VALUE, ModelType.STRING, true);
			return node; 
		}
		
		public ObjectTypeAttributeDefinition getAttributeDefinition() {
			return ObjectTypeAttributeDefinition.Builder.of("PropertyMetaDataMapper", //$NON-NLS-1$
					new SimpleAttributeDefinition(PROPERTY_NAME, ModelType.STRING, false),
					new SimpleAttributeDefinition(PROPERTY_VALUE, ModelType.STRING, false)
			).build();
		}		
	}		
	
	
	/**
	 * Entry Mapper
	 */
	public static class EntryMapper implements MetadataMapper<EntryMetaData>{
		private static final String PATH = "path"; //$NON-NLS-1$
		
		public static EntryMapper INSTANCE = new EntryMapper();

		@Override
        public EntryMetaData unwrap(ModelNode node) {
			if (node == null) {
				return null;
			}
			
			EntryMetaData entry = new EntryMetaData();
			if (node.has(PATH)) {
				entry.setPath(node.get(PATH).asString());
			}
			
			if (node.has(DESCRIPTION)) {
				entry.setDescription(node.get(DESCRIPTION).asString());
			}
			
			//PROPERTIES
			if (node.get(PROPERTIES).isDefined()) {
				List<ModelNode> propNodes = node.get(PROPERTIES).asList();
				for (ModelNode propNode:propNodes) {
					String[] prop = PropertyMetaDataMapper.INSTANCE.unwrap(propNode);
					if (prop != null) {
						entry.addProperty(prop[0], prop[1]);
					}
				}
			}
			return entry;
		}
		
		@Override
        public ModelNode describe(ModelNode node) {
			addAttribute(node, PATH, ModelType.STRING, true);
			
			ModelNode props = node.get(PROPERTIES);
			props.get(TYPE).set(ModelType.LIST);
			props.get(DESCRIPTION).set(Messages.getString(Messages.VDBMetadata.properties_describe));
			PropertyMetaDataMapper.INSTANCE.describe(props.get(VALUE_TYPE));
			return node; 
		}
		
		public ObjectTypeAttributeDefinition getAttributeDefinition() {
			ObjectListAttributeDefinition properties = ObjectListAttributeDefinition.Builder.of(PROPERTIES, PropertyMetaDataMapper.INSTANCE.getAttributeDefinition()).build();
			return ObjectTypeAttributeDefinition.Builder.of("EntryMapper", //$NON-NLS-1$
				new AttributeDefinition[] {
					new SimpleAttributeDefinition(PATH, ModelType.STRING, false),
					properties
			}).build();
		}
		
	}		
	
	/**
	 * DataPolicy Metadata mapper
	 */
	public static class DataPolicyMetadataMapper implements MetadataMapper<DataPolicyMetadata>{
		private static final String POLICY_NAME = "policy-name"; //$NON-NLS-1$
		private static final String DATA_PERMISSIONS = "data-permissions"; //$NON-NLS-1$
		private static final String MAPPED_ROLE_NAMES = "mapped-role-names"; //$NON-NLS-1$
		private static final String ALLOW_CREATE_TEMP_TABLES = "allow-create-temp-tables"; //$NON-NLS-1$
		private static final String ANY_AUTHENTICATED = "any-authenticated"; //$NON-NLS-1$
		private static final String POLICY_DESCRIPTION = "policy-description"; //$NON-NLS-1$
		
		public static DataPolicyMetadataMapper INSTANCE = new DataPolicyMetadataMapper();

		@Override
        public DataPolicyMetadata unwrap(ModelNode node) {
			if(node == null) {
				return null;
			}
			DataPolicyMetadata policy = new DataPolicyMetadata();
			if (node.has(POLICY_NAME)) {
				policy.setName(node.get(POLICY_NAME).asString());
			}
			if (node.has(POLICY_DESCRIPTION)) {
				policy.setDescription(node.get(POLICY_DESCRIPTION).asString());
			}
			if (node.has(ALLOW_CREATE_TEMP_TABLES)) {
				policy.setAllowCreateTemporaryTables(node.get(ALLOW_CREATE_TEMP_TABLES).asBoolean());
			}
			if (node.has(ANY_AUTHENTICATED)) {
				policy.setAnyAuthenticated(node.get(ANY_AUTHENTICATED).asBoolean());
			}
			
			//DATA_PERMISSIONS
			if (node.get(DATA_PERMISSIONS).isDefined()) {
				List<ModelNode> permissionNodes = node.get(DATA_PERMISSIONS).asList();
				for (ModelNode permissionNode:permissionNodes) {
					PermissionMetaData permission = PermissionMetaDataMapper.INSTANCE.unwrap(permissionNode);
					if (permission != null) {
						policy.addPermission(permission);
					}
				}
			}

			//MAPPED_ROLE_NAMES
			if (node.get(MAPPED_ROLE_NAMES).isDefined()) {
				List<ModelNode> roleNameNodes = node.get(MAPPED_ROLE_NAMES).asList();
				for (ModelNode roleNameNode:roleNameNodes) {
					policy.addMappedRoleName(roleNameNode.asString());
				}			
			}
			return policy;
		}
		
		@Override
        public ModelNode describe(ModelNode node) {
			addAttribute(node, POLICY_NAME, ModelType.STRING, true);
			addAttribute(node, POLICY_DESCRIPTION, ModelType.STRING, false);
			addAttribute(node, ALLOW_CREATE_TEMP_TABLES, ModelType.BOOLEAN, false);
			addAttribute(node, ANY_AUTHENTICATED, ModelType.BOOLEAN, false);
			
			ModelNode permissions = node.get(DATA_PERMISSIONS);
			permissions.get(TYPE).set(ModelType.LIST);
			permissions.get(DESCRIPTION).set(Messages.getString(Messages.VDBMetadata.data_permissions_describe));
			
			PermissionMetaDataMapper.INSTANCE.describe(permissions.get(VALUE_TYPE));
			
			ModelNode roleNames = node.get(MAPPED_ROLE_NAMES);
			roleNames.get(TYPE).set(ModelType.LIST);
			roleNames.get(DESCRIPTION).set(Messages.getString(Messages.VDBMetadata.mapped_role_names_describe));
			roleNames.get(VALUE_TYPE).set(ModelType.STRING);
			return node; 
		}
		
		public ObjectTypeAttributeDefinition getAttributeDefinition() {
			ObjectListAttributeDefinition dataPermisstions = ObjectListAttributeDefinition.Builder.of(DATA_PERMISSIONS, PermissionMetaDataMapper.INSTANCE.getAttributeDefinition()).build();
			StringListAttributeDefinition roleNames = new StringListAttributeDefinition.Builder(MAPPED_ROLE_NAMES).build();
			return ObjectTypeAttributeDefinition.Builder.of("DataPolicyMetadataMapper", //$NON-NLS-1$
				new AttributeDefinition[] {
					new SimpleAttributeDefinition(POLICY_NAME, ModelType.STRING, true),
					new SimpleAttributeDefinition(POLICY_DESCRIPTION, ModelType.STRING, true),
					new SimpleAttributeDefinition(ALLOW_CREATE_TEMP_TABLES, ModelType.BOOLEAN, true),
					new SimpleAttributeDefinition(ANY_AUTHENTICATED, ModelType.BOOLEAN, true),
					dataPermisstions,
					roleNames
			}).build();
		}		
	}	
	
	public static class PermissionMetaDataMapper implements MetadataMapper<PermissionMetaData>{
		private static final String RESOURCE_NAME = "resource-name"; //$NON-NLS-1$
		private static final String ALLOW_CREATE = "allow-create"; //$NON-NLS-1$
		private static final String ALLOW_DELETE = "allow-delete"; //$NON-NLS-1$
		private static final String ALLOW_UPADTE = "allow-update"; //$NON-NLS-1$
		private static final String ALLOW_READ = "allow-read"; //$NON-NLS-1$
		private static final String ALLOW_EXECUTE = "allow-execute"; //$NON-NLS-1$
		private static final String ALLOW_ALTER = "allow-alter"; //$NON-NLS-1$
		private static final String ALLOW_LANGUAGE = "allow-language"; //$NON-NLS-1$
		private static final String CONDITION = "condition"; //$NON-NLS-1$
		private static final String MASK = "mask"; //$NON-NLS-1$
		private static final String ORDER = "order"; //$NON-NLS-1$
		private static final String CONSTRAINT = "constraint"; //$NON-NLS-1$
		
		public static PermissionMetaDataMapper INSTANCE = new PermissionMetaDataMapper();

		@Override
        public PermissionMetaData unwrap(ModelNode node) {
			if (node == null) {
				return null;
			}
			
			PermissionMetaData permission = new PermissionMetaData();
			if (node.get(RESOURCE_NAME) != null) {
				permission.setResourceName(node.get(RESOURCE_NAME).asString());
			}
			if (node.has(ALLOW_LANGUAGE)) {
				permission.setAllowLanguage(node.get(ALLOW_LANGUAGE).asBoolean());
				return permission;
			}
			if (node.has(ALLOW_CREATE)) {
				permission.setAllowCreate(node.get(ALLOW_CREATE).asBoolean());
			}
			if (node.has(ALLOW_DELETE)) {
				permission.setAllowDelete(node.get(ALLOW_DELETE).asBoolean());
			}
			if (node.has(ALLOW_UPADTE)) {
				permission.setAllowUpdate(node.get(ALLOW_UPADTE).asBoolean());
			}
			if (node.has(ALLOW_READ)) {
				permission.setAllowRead(node.get(ALLOW_READ).asBoolean());
			}
			if (node.has(ALLOW_EXECUTE)) {
				permission.setAllowExecute(node.get(ALLOW_EXECUTE).asBoolean());
			}
			if (node.has(ALLOW_ALTER)) {
				permission.setAllowAlter(node.get(ALLOW_ALTER).asBoolean());
			}
			if (node.has(CONDITION)) {
				permission.setCondition(node.get(CONDITION).asString());
			}
			if (node.has(MASK)) {
				permission.setMask(node.get(MASK).asString());
			}
			if (node.has(ORDER)) {
				permission.setOrder(node.get(ORDER).asInt());
			}
			if (node.has(CONSTRAINT)) {
				permission.setConstraint(node.get(CONSTRAINT).asBoolean());
			}
			return permission;
		}
		@Override
        public ModelNode describe(ModelNode node) {
			addAttribute(node, RESOURCE_NAME, ModelType.STRING, true);
			addAttribute(node, ALLOW_CREATE, ModelType.BOOLEAN, false);
			addAttribute(node, ALLOW_DELETE, ModelType.BOOLEAN, false);
			addAttribute(node, ALLOW_UPADTE, ModelType.BOOLEAN, false);
			addAttribute(node, ALLOW_READ, ModelType.BOOLEAN, false);
			addAttribute(node, ALLOW_EXECUTE, ModelType.BOOLEAN, false);
			addAttribute(node, ALLOW_ALTER, ModelType.BOOLEAN, false);
			addAttribute(node, ALLOW_LANGUAGE, ModelType.BOOLEAN, false);
			return node;
		}
		
		public ObjectTypeAttributeDefinition getAttributeDefinition() {
			return ObjectTypeAttributeDefinition.Builder.of("PermissionMetaData", //$NON-NLS-1$
				new AttributeDefinition[] {
					new SimpleAttributeDefinition(RESOURCE_NAME, ModelType.STRING, false),
					new SimpleAttributeDefinition(ALLOW_CREATE, ModelType.BOOLEAN, true),
					new SimpleAttributeDefinition(ALLOW_DELETE, ModelType.BOOLEAN, true),
					new SimpleAttributeDefinition(ALLOW_UPADTE, ModelType.BOOLEAN, true),
					new SimpleAttributeDefinition(ALLOW_READ, ModelType.BOOLEAN, true),
					new SimpleAttributeDefinition(ALLOW_EXECUTE, ModelType.BOOLEAN, true),
					new SimpleAttributeDefinition(ALLOW_ALTER, ModelType.BOOLEAN, true),
					new SimpleAttributeDefinition(ALLOW_LANGUAGE, ModelType.BOOLEAN, true)
			}).build();
		}
	}
	
	public static class EngineStatisticsMetadataMapper implements MetadataMapper<EngineStatisticsMetadata>{
		private static final String SESSION_COUNT = "session-count"; //$NON-NLS-1$
		private static final String TOTAL_MEMORY_USED_IN_KB = "total-memory-inuse-kb"; //$NON-NLS-1$
		private static final String MEMORY_IN_USE_BY_ACTIVE_PLANS = "total-memory-inuse-active-plans-kb";//$NON-NLS-1$
		private static final String DISK_WRITE_COUNT = "buffermgr-disk-write-count"; //$NON-NLS-1$
		private static final String DISK_READ_COUNT = "buffermgr-disk-read-count"; //$NON-NLS-1$
		private static final String CACHE_WRITE_COUNT = "buffermgr-cache-write-count"; //$NON-NLS-1$
		private static final String CACHE_READ_COUNT = "buffermgr-cache-read-count"; //$NON-NLS-1$
		private static final String DISK_SPACE_USED = "buffermgr-diskspace-used-mb"; //$NON-NLS-1$
		private static final String ACTIVE_PLAN_COUNT = "active-plans-count"; //$NON-NLS-1$
		private static final String WAITING_PLAN_COUNT = "waiting-plans-count"; //$NON-NLS-1$
		private static final String MAX_WAIT_PLAN_COUNT = "max-waitplan-watermark"; //$NON-NLS-1$
		
		public static EngineStatisticsMetadataMapper INSTANCE = new EngineStatisticsMetadataMapper();

		@Override
        public EngineStatisticsMetadata unwrap(ModelNode node) {
			if (node == null)
				return null;
				
			EngineStatisticsMetadata stats = new EngineStatisticsMetadata();
			stats.setSessionCount(node.get(SESSION_COUNT).asInt());
			stats.setTotalMemoryUsedInKB(node.get(TOTAL_MEMORY_USED_IN_KB).asLong());
			stats.setMemoryUsedByActivePlansInKB(node.get(MEMORY_IN_USE_BY_ACTIVE_PLANS).asLong());
			stats.setDiskWriteCount(node.get(DISK_WRITE_COUNT).asLong());
			stats.setDiskReadCount(node.get(DISK_READ_COUNT).asLong());
			stats.setCacheReadCount(node.get(CACHE_READ_COUNT).asLong());
			stats.setCacheWriteCount(node.get(CACHE_WRITE_COUNT).asLong());
			stats.setDiskSpaceUsedInMB(node.get(DISK_SPACE_USED).asLong());
			stats.setActivePlanCount(node.get(ACTIVE_PLAN_COUNT).asInt());
			stats.setWaitPlanCount(node.get(WAITING_PLAN_COUNT).asInt());
			stats.setMaxWaitPlanWaterMark(node.get(MAX_WAIT_PLAN_COUNT).asInt());
			
			unwrapDomain(stats, node);
			return stats;
		}
		
		@Override
        public ModelNode describe(ModelNode node) {
			addAttribute(node, SESSION_COUNT, ModelType.INT, true);
			addAttribute(node, TOTAL_MEMORY_USED_IN_KB, ModelType.LONG, true);
			addAttribute(node, MEMORY_IN_USE_BY_ACTIVE_PLANS, ModelType.LONG, true);
			addAttribute(node, DISK_WRITE_COUNT, ModelType.LONG, true);
			addAttribute(node, DISK_READ_COUNT, ModelType.LONG, true);
			addAttribute(node, CACHE_READ_COUNT, ModelType.LONG, true);
			addAttribute(node, CACHE_WRITE_COUNT, ModelType.LONG, true);
			addAttribute(node, DISK_SPACE_USED, ModelType.LONG, true);
			addAttribute(node, ACTIVE_PLAN_COUNT, ModelType.INT, true);
			addAttribute(node, WAITING_PLAN_COUNT, ModelType.INT, true);
			addAttribute(node, MAX_WAIT_PLAN_COUNT, ModelType.INT, true);
			return node;
		}
		
		public AttributeDefinition[] getAttributeDefinitions() {
			return new AttributeDefinition[] {
					new SimpleAttributeDefinition(SESSION_COUNT, ModelType.INT, false),
					new SimpleAttributeDefinition(TOTAL_MEMORY_USED_IN_KB, ModelType.LONG, false),
					new SimpleAttributeDefinition(MEMORY_IN_USE_BY_ACTIVE_PLANS, ModelType.LONG, false),
					new SimpleAttributeDefinition(DISK_WRITE_COUNT, ModelType.LONG, false),
					new SimpleAttributeDefinition(DISK_READ_COUNT, ModelType.LONG, false),
					new SimpleAttributeDefinition(CACHE_READ_COUNT, ModelType.LONG, false),
					new SimpleAttributeDefinition(CACHE_WRITE_COUNT, ModelType.LONG, false),
					new SimpleAttributeDefinition(DISK_SPACE_USED, ModelType.LONG, false),
					new SimpleAttributeDefinition(ACTIVE_PLAN_COUNT, ModelType.INT, false),
					new SimpleAttributeDefinition(WAITING_PLAN_COUNT, ModelType.INT, false),
					new SimpleAttributeDefinition(MAX_WAIT_PLAN_COUNT, ModelType.INT, false)
			};
		}		
	}	
	
	public static class CacheStatisticsMetadataMapper implements MetadataMapper<CacheStatisticsMetadata>{
		private static final String HITRATIO = "hit-ratio"; //$NON-NLS-1$
		private static final String TOTAL_ENTRIES = "total-entries"; //$NON-NLS-1$
		private static final String REQUEST_COUNT = "request-count"; //$NON-NLS-1$
		
		public static CacheStatisticsMetadataMapper INSTANCE = new CacheStatisticsMetadataMapper();

		@Override
        public CacheStatisticsMetadata unwrap(ModelNode node) {
			if (node == null)
				return null;
				
			CacheStatisticsMetadata cache = new CacheStatisticsMetadata();
			cache.setTotalEntries(node.get(TOTAL_ENTRIES).asInt());
			cache.setHitRatio(node.get(HITRATIO).asDouble());
			cache.setRequestCount(node.get(REQUEST_COUNT).asInt());
			
			unwrapDomain(cache, node);
			return cache;
		}
		
		@Override
        public ModelNode describe(ModelNode node) {
			addAttribute(node, TOTAL_ENTRIES, ModelType.STRING, true);
			addAttribute(node, HITRATIO, ModelType.STRING, true);
			addAttribute(node, REQUEST_COUNT, ModelType.STRING, true);
			return node; 		
		}
		
		public AttributeDefinition[] getAttributeDefinitions() {
			return new AttributeDefinition[] {
					new SimpleAttributeDefinition(TOTAL_ENTRIES, ModelType.STRING, false),
					new SimpleAttributeDefinition(HITRATIO, ModelType.STRING, false),
					new SimpleAttributeDefinition(REQUEST_COUNT, ModelType.STRING, false)
			};
		}
	}	
	
	public static class RequestMetadataMapper implements MetadataMapper<RequestMetadata>{
		private static final String TRANSACTION_ID = "transaction-id"; //$NON-NLS-1$
		private static final String NODE_ID = "node-id"; //$NON-NLS-1$
		private static final String SOURCE_REQUEST = "source-request"; //$NON-NLS-1$
		private static final String COMMAND = "command"; //$NON-NLS-1$
		private static final String START_TIME = "start-time"; //$NON-NLS-1$
		private static final String SESSION_ID = "session-id"; //$NON-NLS-1$
		private static final String EXECUTION_ID = "execution-id"; //$NON-NLS-1$
		private static final String STATE = "processing-state"; //$NON-NLS-1$
		private static final String THREAD_STATE = "thread-state"; //$NON-NLS-1$
		
		public static RequestMetadataMapper INSTANCE = new RequestMetadataMapper();

		@Override
        public RequestMetadata unwrap(ModelNode node) {
			if (node == null)
				return null;

			RequestMetadata request = new RequestMetadata();
			request.setExecutionId(node.get(EXECUTION_ID).asLong());
			request.setSessionId(node.get(SESSION_ID).asString());
			request.setStartTime(node.get(START_TIME).asLong());
			request.setCommand(node.get(COMMAND).asString());
			request.setSourceRequest(node.get(SOURCE_REQUEST).asBoolean());
			if (node.has(NODE_ID)) {
				request.setNodeId(node.get(NODE_ID).asInt());
			}
			if (node.has(TRANSACTION_ID)) {
				request.setTransactionId(node.get(TRANSACTION_ID).asString());
			}
			request.setState(ProcessingState.valueOf(node.get(STATE).asString()));
			request.setThreadState(ThreadState.valueOf(node.get(THREAD_STATE).asString()));
			
			unwrapDomain(request, node);
			return request;
		}
		
		@Override
        public ModelNode describe(ModelNode node) {
			addAttribute(node, EXECUTION_ID, ModelType.LONG, true);
			addAttribute(node, SESSION_ID, ModelType.STRING, true);
			addAttribute(node, START_TIME, ModelType.LONG, true);
			addAttribute(node, COMMAND, ModelType.STRING, true);
			addAttribute(node, SOURCE_REQUEST, ModelType.BOOLEAN, true);
			addAttribute(node, NODE_ID, ModelType.INT, false);
			addAttribute(node, TRANSACTION_ID, ModelType.STRING, false);
			addAttribute(node, STATE, ModelType.STRING, true);
			addAttribute(node, THREAD_STATE, ModelType.STRING, true);
			return node; 		
		}
		
		public AttributeDefinition[] getAttributeDefinitions() {
			return new AttributeDefinition[] {
					new SimpleAttributeDefinition(EXECUTION_ID, ModelType.LONG, false),
					new SimpleAttributeDefinition(SESSION_ID, ModelType.STRING, false),
					new SimpleAttributeDefinition(START_TIME, ModelType.LONG, false),
					new SimpleAttributeDefinition(COMMAND, ModelType.STRING, false),
					new SimpleAttributeDefinition(SOURCE_REQUEST, ModelType.BOOLEAN, false),
					new SimpleAttributeDefinition(NODE_ID, ModelType.INT, true),
					new SimpleAttributeDefinition(TRANSACTION_ID, ModelType.STRING, true),
					new SimpleAttributeDefinition(STATE, ModelType.STRING, false),
					new SimpleAttributeDefinition(THREAD_STATE, ModelType.STRING, false)
			};
		}
	}
	
	public static class SessionMetadataMapper implements MetadataMapper<SessionMetadata>{
		private static final String SECURITY_DOMAIN = "security-domain"; //$NON-NLS-1$
		private static final String VDB_VERSION = "vdb-version"; //$NON-NLS-1$
		private static final String VDB_NAME = "vdb-name"; //$NON-NLS-1$
		private static final String USER_NAME = "user-name"; //$NON-NLS-1$
		private static final String SESSION_ID = "session-id"; //$NON-NLS-1$
		private static final String LAST_PING_TIME = "last-ping-time"; //$NON-NLS-1$
		private static final String IP_ADDRESS = "ip-address"; //$NON-NLS-1$
		private static final String CLIENT_HOST_NAME = "client-host-address"; //$NON-NLS-1$
		private static final String CREATED_TIME = "created-time"; //$NON-NLS-1$
		private static final String APPLICATION_NAME = "application-name"; //$NON-NLS-1$
		private static final String CLIENT_HARDWARE_ADRESS = "client-hardware-address"; //$NON-NLS-1$
		
		public static SessionMetadataMapper INSTANCE = new SessionMetadataMapper();

		@Override
        public SessionMetadata unwrap(ModelNode node) {
			if (node == null)
				return null;
				
			SessionMetadata session = new SessionMetadata();
			if (node.has(APPLICATION_NAME)) {
				session.setApplicationName(node.get(APPLICATION_NAME).asString());
			}
			session.setCreatedTime(node.get(CREATED_TIME).asLong());
			
			if (node.has(CLIENT_HOST_NAME)) {
				session.setClientHostName(node.get(CLIENT_HOST_NAME).asString());
			}
			
			if (node.has(IP_ADDRESS)) {
				session.setIPAddress(node.get(IP_ADDRESS).asString());
			}
			
			session.setLastPingTime(node.get(LAST_PING_TIME).asLong());
			session.setSessionId(node.get(SESSION_ID).asString());
			session.setUserName(node.get(USER_NAME).asString());
			session.setVDBName(node.get(VDB_NAME).asString());
			session.setVDBVersion(node.get(VDB_VERSION).asInt());
			if (node.has(SECURITY_DOMAIN)) {
				session.setSecurityDomain(node.get(SECURITY_DOMAIN).asString());
			}
			if (node.has(CLIENT_HARDWARE_ADRESS)) {
				session.setClientHardwareAddress(node.get(CLIENT_HARDWARE_ADRESS).asString());
			}
			unwrapDomain(session, node);
			return session;
		}
		
		@Override
        public ModelNode describe(ModelNode node) {
			addAttribute(node, APPLICATION_NAME, ModelType.STRING, false);
			addAttribute(node, CREATED_TIME, ModelType.LONG, true);
			addAttribute(node, CLIENT_HOST_NAME, ModelType.LONG, true);
			addAttribute(node, IP_ADDRESS, ModelType.STRING, true);
			addAttribute(node, LAST_PING_TIME, ModelType.LONG, true);
			addAttribute(node, SESSION_ID, ModelType.STRING, true);
			addAttribute(node, USER_NAME, ModelType.STRING, true);
			addAttribute(node, VDB_NAME, ModelType.STRING, true);
			addAttribute(node, VDB_VERSION, ModelType.INT, true);
			addAttribute(node, SECURITY_DOMAIN, ModelType.STRING, false);
			return node;
		}
		
		public AttributeDefinition[] getAttributeDefinitions() {
			return new AttributeDefinition[] {
					new SimpleAttributeDefinition(APPLICATION_NAME, ModelType.STRING, true),
					new SimpleAttributeDefinition(CREATED_TIME, ModelType.LONG, false),
					new SimpleAttributeDefinition(CLIENT_HOST_NAME, ModelType.LONG, false),
					new SimpleAttributeDefinition(IP_ADDRESS, ModelType.STRING, false),
					new SimpleAttributeDefinition(LAST_PING_TIME, ModelType.LONG, false),
					new SimpleAttributeDefinition(SESSION_ID, ModelType.STRING, false),
					new SimpleAttributeDefinition(USER_NAME, ModelType.STRING, false),
					new SimpleAttributeDefinition(VDB_NAME, ModelType.STRING, false),
					new SimpleAttributeDefinition(VDB_VERSION, ModelType.INT, false),
					new SimpleAttributeDefinition(SECURITY_DOMAIN, ModelType.STRING, true)
			};
		}
	}	
	
	public static class TransactionMetadataMapper implements MetadataMapper<TransactionMetadata>{
		private static final String ID = "txn-id"; //$NON-NLS-1$
		private static final String SCOPE = "txn-scope"; //$NON-NLS-1$
		private static final String CREATED_TIME = "txn-created-time"; //$NON-NLS-1$
		private static final String ASSOCIATED_SESSION = "session-id"; //$NON-NLS-1$
		
		public static TransactionMetadataMapper INSTANCE = new TransactionMetadataMapper();

		@Override
        public TransactionMetadata unwrap(ModelNode node) {
			if (node == null)
				return null;

			TransactionMetadata transaction = new TransactionMetadata();
			transaction.setAssociatedSession(node.get(ASSOCIATED_SESSION).asString());
			transaction.setCreatedTime(node.get(CREATED_TIME).asLong());
			transaction.setScope(node.get(SCOPE).asString());
			transaction.setId(node.get(ID).asString());
			unwrapDomain(transaction, node);
			return transaction;
		}
		
		@Override
        public ModelNode describe(ModelNode node) {
			addAttribute(node, ASSOCIATED_SESSION, ModelType.STRING, true);
			addAttribute(node, CREATED_TIME, ModelType.LONG, true);
			addAttribute(node, SCOPE, ModelType.LONG, true);
			addAttribute(node, ID, ModelType.STRING, true);
			return node;
		}
		
		public AttributeDefinition[] getAttributeDefinitions() {
			return new AttributeDefinition[] {
					new SimpleAttributeDefinition(ASSOCIATED_SESSION, ModelType.STRING, false),
					new SimpleAttributeDefinition(CREATED_TIME, ModelType.LONG, false),
					new SimpleAttributeDefinition(SCOPE, ModelType.LONG, false),
					new SimpleAttributeDefinition(ID, ModelType.STRING, false)
			};
		};
	}	

	public static class WorkerPoolStatisticsMetadataMapper implements MetadataMapper<WorkerPoolStatisticsMetadata>{
		private static final String MAX_THREADS = "max-threads"; //$NON-NLS-1$
		private static final String HIGHEST_QUEUED = "highest-queued"; //$NON-NLS-1$
		private static final String QUEUED = "queued"; //$NON-NLS-1$
		private static final String QUEUE_NAME = "queue-name"; //$NON-NLS-1$
		private static final String TOTAL_SUBMITTED = "total-submitted"; //$NON-NLS-1$
		private static final String TOTAL_COMPLETED = "total-completed"; //$NON-NLS-1$
		private static final String HIGHEST_ACTIVE_THREADS = "highest-active-threads"; //$NON-NLS-1$
		private static final String ACTIVE_THREADS = "active-threads"; //$NON-NLS-1$
		
		public static WorkerPoolStatisticsMetadataMapper INSTANCE = new WorkerPoolStatisticsMetadataMapper();

		@Override
        public WorkerPoolStatisticsMetadata unwrap(ModelNode node) {
			if (node == null)
				return null;

			WorkerPoolStatisticsMetadata stats = new WorkerPoolStatisticsMetadata();
			stats.setActiveThreads(node.get(ACTIVE_THREADS).asInt());
			stats.setHighestActiveThreads(node.get(HIGHEST_ACTIVE_THREADS).asInt());
			stats.setTotalCompleted(node.get(TOTAL_COMPLETED).asLong());
			stats.setTotalSubmitted(node.get(TOTAL_SUBMITTED).asLong());
			stats.setQueueName(node.get(QUEUE_NAME).asString());
			stats.setQueued(node.get(QUEUED).asInt());
			stats.setHighestQueued(node.get(HIGHEST_QUEUED).asInt());
			stats.setMaxThreads(node.get(MAX_THREADS).asInt());
			unwrapDomain(stats, node);
			return stats;
		}
		
		@Override
        public ModelNode describe(ModelNode node) {
			addAttribute(node, ACTIVE_THREADS, ModelType.INT, true);
			addAttribute(node, HIGHEST_ACTIVE_THREADS, ModelType.INT, true);
			addAttribute(node, TOTAL_COMPLETED, ModelType.LONG, true);
			addAttribute(node, TOTAL_SUBMITTED, ModelType.LONG, true);
			addAttribute(node, QUEUE_NAME, ModelType.STRING, true);
			addAttribute(node, QUEUED, ModelType.INT, true);
			addAttribute(node, HIGHEST_QUEUED, ModelType.INT, true);
			addAttribute(node, MAX_THREADS, ModelType.INT, true);
			return node;
		}

		public AttributeDefinition[] getAttributeDefinitions() {
			return new AttributeDefinition[] { 
					new SimpleAttributeDefinition(ACTIVE_THREADS, ModelType.INT, false),
					new SimpleAttributeDefinition(HIGHEST_ACTIVE_THREADS, ModelType.INT, false),
					new SimpleAttributeDefinition(TOTAL_COMPLETED, ModelType.LONG, false),
					new SimpleAttributeDefinition(TOTAL_SUBMITTED, ModelType.LONG, false),
					new SimpleAttributeDefinition(QUEUE_NAME, ModelType.STRING, false),
					new SimpleAttributeDefinition(QUEUED, ModelType.INT, false),
					new SimpleAttributeDefinition(HIGHEST_QUEUED, ModelType.INT, false),
					new SimpleAttributeDefinition(MAX_THREADS, ModelType.INT, false)
				};
		}
	}
	
	public static void wrapDomain(AdminObjectImpl anObj, ModelNode node) {
		if (anObj.getServerGroup() != null) {
			node.get(SERVER_GROUP).set(anObj.getServerGroup());
		}
		if (anObj.getHostName() != null) {
			node.get(HOST_NAME).set(anObj.getHostName());
		}
		if (anObj.getServerName() != null) {
			node.get(SERVER_NAME).set(anObj.getServerName());
		}
	}
	
	public static void unwrapDomain(AdminObjectImpl anObj, ModelNode node) {
		if (node.get(SERVER_GROUP).isDefined()) {
			anObj.setServerGroup(node.get(SERVER_GROUP).asString());
		}
		if (node.get(HOST_NAME).isDefined()) {
			anObj.setHostName(node.get(HOST_NAME).asString());
		}
		if (node.get(SERVER_NAME).isDefined()) {
			anObj.setServerName(node.get(SERVER_NAME).asString());
		}
	}	
	
	private static final String SERVER_GROUP = "server-group"; //$NON-NLS-1$
	private static final String HOST_NAME = "host-name"; //$NON-NLS-1$
	private static final String SERVER_NAME = "server-name"; //$NON-NLS-1$
	private static final String UNDERSCORE_DESC = "_describe"; //$NON-NLS-1$
	private static final String TYPE = "type"; //$NON-NLS-1$
	private static final String REQUIRED = "required"; //$NON-NLS-1$
	private static final String ALLOWED = "allowed"; //$NON-NLS-1$
	private static final String VALUE_TYPE = "value-type"; //$NON-NLS-1$
	
	static ModelNode addAttribute(ModelNode node, String name, ModelType dataType, boolean required) {
		node.get(name, TYPE).set(dataType);
		String nameKey = name.replaceAll("-", "_") + UNDERSCORE_DESC; //$NON-NLS-1$ //$NON-NLS-2$
		VDBMetadata nameEnum = VDBMetadata.valueOf(nameKey);
        node.get(name, DESCRIPTION).set(Messages.getString(nameEnum));
        node.get(name, REQUIRED).set(required);
        return node;
    }
}


