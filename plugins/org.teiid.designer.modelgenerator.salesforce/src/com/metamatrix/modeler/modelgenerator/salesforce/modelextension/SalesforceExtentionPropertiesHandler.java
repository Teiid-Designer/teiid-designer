/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.modelextension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.jcr.PropertyType;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeTypeDefinition;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.modeshape.graph.ExecutionContext;
import org.modeshape.graph.property.NamespaceRegistry.Namespace;
import org.modeshape.jcr.CndNodeTypeReader;
import org.teiid.core.properties.PropertyDefinition;
import org.teiid.designer.extension.cnd.CndNamespace;
import org.teiid.designer.extension.cnd.CndPropertyDefinition;
import org.teiid.designer.extension.cnd.CndTypeDefinition;
import org.teiid.designer.extension.manager.ExtendedModelConstants;
import org.teiid.designer.extension.manager.ExtendedModelObject;
import org.teiid.designer.extension.manager.ExtensionPropertiesManager;
import org.teiid.designer.extension.manager.IExtensionPropertiesHandler;
import org.teiid.designer.extension.manager.ModelExtensionDefinition;
import org.teiid.designer.extension.manager.ModelObjectExtendedProperty;

import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelObjectAnnotationHelper;
import com.metamatrix.modeler.internal.core.workspace.ResourceAnnotationHelper;

/**
 * 	This class assumes that a model has an Annotation on it's ModelAnnotation that contains the following key/value tag
 *
 *  <tags xmi:uuid="mmuuid:4f9a04b0-80a1-4182-b6ef-a7b64644e657" key="ext-id:salesforce" value="org.teiid.designer.model.extension.salesforce"/>
 *	<tags xmi:uuid="mmuuid:4f9a04b0-80a1-4182-b6ef-a7b88844e657" key="ext-namespace:salesfoce" value="http://org.teiid.designer/metamodels/Salesforce"/>
 *  <tags xmi:uuid="mmuuid:95f0a8ec-3a48-481e-b44d-26640689564d" key="ext-cnd:salesforce" 
 *           value="&lt;salesforce='http://www.metamatrix.com/metamodels/Salesforce'>&#xa;&lt;
 *              relational='http://www.metamatrix.com/metamodels/Relational'>&#xa;&#xa;
 *               [salesforce:tableCapabilities] > relational:baseTable&#xa;
 *               - salesforce:supportsCreate (boolean) = 'false'&#xa;
 *               - salesforce:supportsDelete (boolean) = 'false'&#xa;
 *               - salesforce:custom (boolean) = 'false'&#xa;
 *               - salesforce:supportsIDLookup (boolean) = 'false'&#xa;
 *               - salesforce:supportsMerge (boolean) = 'false'&#xa;
 *               - salesforce:supportsQuery (boolean) = 'false'&#xa;
 *               - salesforce:supportsReplicate (boolean) = 'false'&#xa;
 *               - salesforce:supportsRetrieve (boolean) = 'false'&#xa;
 *               - salesforce:supportsSearch (boolean) = 'false'&#xa;&#xa;
 *               [salesforce:columnCapabilities] > relational:column&#xa;
 *               - salesforce:defaultedOnCreate (boolean) = 'false'&#xa;
 *               - salesforce:calculated (boolean) = 'false'&#xa;
 *               - salesforce:custom (boolean) = 'false'&#xa;
 *               - salesforce:picklistValues (string) multiple&#xa;"/>
 *
 */
public class SalesforceExtentionPropertiesHandler implements IExtensionPropertiesHandler {

	public static final String MODEL_EXTENSION_ID = "org.teiid.designer.model.extension.salesforce"; //$NON-NLS-1$
	public static final String NAMESPACE = "http://org.teiid.designer/metamodels/Salesforce"; //$NON-NLS-1$
	public static final String ID = "salesforce"; //$NON-NLS-1$
	public static final String DISPLAY_NAME = "Salesforce"; //$NON-NLS-1$
	
	// Need to construct unique ID : org.teiid.designer.model.extension:salesforce
	public static final String EXTENSION_FULL_ID_KEY = EXTENSION_ID_PREFIX + ID;
	public static final String EXTENSION_FULL_CND_KEY = EXTENSION_CND_PREFIX + ID;
	public static final String EXTENSION_FULL_NAMESPACE_KEY = EXTENSION_NAMEPSACE_PREFIX + ID;
	private static char CR = '\n';
	private static final String TABLE_NODE_TYPE_NAME = "salesforce:tableCapabilities"; //$NON-NLS-1$
	private static final String COLUMN_NODE_TYPE_NAME = "salesforce:columnCapabilities"; //$NON-NLS-1$

	static final String EXT_PROP_NAMESPACE_PREFIX = ExtensionPropertiesManager.createExtendedModelNamespace(ID);

	static final String TABLE_SUPPORTS_CREATE = "Supports Create"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_DELETE = "Supports Delete"; //$NON-NLS-1$
	static final String TABLE_CUSTOM = "Custom"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_LOOKUP = "Supports ID Lookup"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_MERGE = "Supports Merge"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_QUERY = "Supports Query"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_REPLICATE = "Supports Replicate"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_RETRIEVE = "Supports Retrieve"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_SEARCH = "Supports Search"; //$NON-NLS-1$
	
	static final String COLUMN_DEFAULTED = "Defaulted on Create"; //$NON-NLS-1$
	static final String COLUMN_CALCULATED = "Calculated"; //$NON-NLS-1$
	static final String COLUMN_PICKLIST_VALUES = "Picklist Values"; //$NON-NLS-1$
	
	static final String SF_SUPPORTS_CREATE = ID + ":supportsCreate"; //$NON-NLS-1$
	static final String SF_SUPPORTS_DELETE = ID + ":supportsDelete"; //$NON-NLS-1$
	static final String SF_CUSTOM = ID + ":custom"; //$NON-NLS-1$
	static final String SF_SUPPORTS_LOOKUP = ID + ":supportsIDLookup"; //$NON-NLS-1$
	static final String SF_SUPPORTS_MERGE = ID + ":supportsMerge"; //$NON-NLS-1$
	static final String SF_SUPPORTS_QUERY = ID + ":supportsQuery"; //$NON-NLS-1$
	static final String SF_SUPPORTS_REPLICATE = ID + ":supportsReplicate"; //$NON-NLS-1$
	static final String SF_SUPPORTS_RETRIEVE = ID + ":supportsRetrieve"; //$NON-NLS-1$
	static final String SF_SUPPORTS_SEARCH = ID + ":supportsSearch"; //$NON-NLS-1$
	static final String SF_DEFAULTED = ID + ":defaultedOnCreate"; //$NON-NLS-1$
	static final String SF_CALCULATED = ID + ":calculated"; //$NON-NLS-1$
	static final String SF_PICKLIST_VALUES = ID + ":picklistValues"; //$NON-NLS-1$
	
	static final String FALSE_STR = Boolean.FALSE.toString();
	static final String[] ALLOWED_BOOLEAN_VALUES = new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() };
	static final String BOOLEAN_TYPE_STR = "(boolean)"; //$NON-NLS-1$
	static final String STRING_TYPE_STR = "(string)"; //$NON-NLS-1$
	static final String FALSE_DEFAULT_STR = "'false'"; //$NON-NLS-1$
	static final char SP = ' ';
	static final char EQ = '=';
	static final char DASH = '-';
	
	private static final ModelObjectAnnotationHelper helper = new ModelObjectAnnotationHelper();
	private static final ResourceAnnotationHelper resourceHelper = new ResourceAnnotationHelper();
	
	private Properties defaultTableProperties;
	private Properties defaultColumnProperties;
	private final Properties emptyProperties = new Properties();
	private Map<String, String> propertyNameMap;
	
	private CndTypeDefinition defaultTableTypeDefinition;
	private CndTypeDefinition defaultColumnTypeDefinition;
	
	private boolean cndWasParsed = false;
	
	/**
	 * The following String defines the CND string that can be read by Modeshape's CndNodeTypeReader.read() method to
	 * in order to be both sequenced when published AND decoded when edited or viewed in Teiid Designer.
	 */
	public static final String SF_CND_STRING = 
			"<" + ID + "=\'" +  NAMESPACE + "\'>" + CR + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"<relational='http://www.metamatrix.com/metamodels/Relational'>" + CR + CR + //$NON-NLS-1$
			
			"[salesforce:tableCapabilities] > relational:baseTable" + CR + //$NON-NLS-1$
			DASH + SP + SF_SUPPORTS_CREATE + SP + BOOLEAN_TYPE_STR + SP + EQ + FALSE_DEFAULT_STR + CR +
			DASH + SP + SF_SUPPORTS_DELETE + SP + BOOLEAN_TYPE_STR + SP + EQ + FALSE_DEFAULT_STR + CR +
			DASH + SP + SF_CUSTOM + SP + BOOLEAN_TYPE_STR + SP + EQ + FALSE_DEFAULT_STR + CR +
			DASH + SP + SF_SUPPORTS_LOOKUP + SP + BOOLEAN_TYPE_STR + SP + EQ + FALSE_DEFAULT_STR + CR +
			DASH + SP + SF_SUPPORTS_MERGE + SP + BOOLEAN_TYPE_STR + SP + EQ + FALSE_DEFAULT_STR + CR +
			DASH + SP + SF_SUPPORTS_QUERY + SP + BOOLEAN_TYPE_STR + SP + EQ + FALSE_DEFAULT_STR + CR +
			DASH + SP + SF_SUPPORTS_REPLICATE + SP + BOOLEAN_TYPE_STR + SP + EQ + FALSE_DEFAULT_STR + CR +
			DASH + SP + SF_SUPPORTS_RETRIEVE + SP + BOOLEAN_TYPE_STR + SP + EQ + FALSE_DEFAULT_STR + CR +
			DASH + SP + SF_SUPPORTS_SEARCH + SP + BOOLEAN_TYPE_STR + SP + EQ + FALSE_DEFAULT_STR + CR + CR +
			
			"[salesforce:columnCapabilities] > relational:column" + CR + //$NON-NLS-1$
			DASH + SP + SF_DEFAULTED + SP + BOOLEAN_TYPE_STR + SP + EQ + FALSE_DEFAULT_STR + CR +
			DASH + SP + SF_CALCULATED + SP + BOOLEAN_TYPE_STR + SP + EQ + FALSE_DEFAULT_STR + CR +
			DASH + SP + SF_CUSTOM + SP + BOOLEAN_TYPE_STR + SP + EQ + FALSE_DEFAULT_STR + CR +
			DASH + SP + SF_PICKLIST_VALUES + SP + STRING_TYPE_STR + SP + "multiple" + CR; //$NON-NLS-1$
	
//	/**
//	 * The following String defines a constant to store the mapping between the CND table property name and the actual
//	 * Name segment of the property stored in the tag. Since CND properties have no inherent Description or Display
//	 * Name, we needed a way to persist and find the Display Name
//	 */
//	public static final String SF_TABLE_PROPERTIES_MAP = 
//		SF_SUPPORTS_CREATE + DELIM + TABLE_SUPPORTS_CREATE + DELIM +
//		SF_SUPPORTS_DELETE + DELIM + TABLE_SUPPORTS_DELETE + DELIM +
//		SF_CUSTOM + DELIM + TABLE_CUSTOM + DELIM +
//		SF_SUPPORTS_LOOKUP + DELIM + TABLE_SUPPORTS_LOOKUP + DELIM +
//		SF_SUPPORTS_MERGE + DELIM + TABLE_SUPPORTS_MERGE + DELIM +
//		SF_SUPPORTS_QUERY + DELIM + TABLE_SUPPORTS_QUERY + DELIM +
//		SF_SUPPORTS_REPLICATE + DELIM + TABLE_SUPPORTS_REPLICATE + DELIM +
//		SF_SUPPORTS_RETRIEVE + DELIM + TABLE_SUPPORTS_RETRIEVE + DELIM +
//		SF_SUPPORTS_SEARCH + DELIM + TABLE_SUPPORTS_SEARCH;
//
//	/**
//	 * The following String defines a constant to store the mapping between the CND column property name and the actual
//	 * Name segment of the property stored in the tag. Since CND properties have no inherent Description or Display
//	 * Name, we needed a way to persist and find the Display Name
//	 */
//	public static final String SF_COLUMN_PROPERTIES_MAP = 
//		SF_DEFAULTED + DELIM + COLUMN_DEFAULTED + DELIM +
//		SF_CALCULATED + DELIM + COLUMN_CALCULATED + DELIM +
//		SF_DEFAULTED + DELIM + TABLE_CUSTOM + DELIM +
//		SF_PICKLIST_VALUES + DELIM + COLUMN_PICKLIST_VALUES;
	
	public SalesforceExtentionPropertiesHandler() {
		super();
		initialize();
	}

	private void initialize() {
		propertyNameMap = new HashMap<String, String>();
		propertyNameMap.put(SF_CUSTOM, TABLE_CUSTOM);
		propertyNameMap.put(SF_SUPPORTS_CREATE, TABLE_SUPPORTS_CREATE);
		propertyNameMap.put(SF_SUPPORTS_DELETE, TABLE_SUPPORTS_DELETE);
		propertyNameMap.put(SF_SUPPORTS_LOOKUP, TABLE_SUPPORTS_LOOKUP);
		propertyNameMap.put(SF_SUPPORTS_MERGE, TABLE_SUPPORTS_MERGE);
		propertyNameMap.put(SF_SUPPORTS_QUERY, TABLE_SUPPORTS_QUERY);
		propertyNameMap.put(SF_SUPPORTS_REPLICATE, TABLE_SUPPORTS_REPLICATE);
		propertyNameMap.put(SF_SUPPORTS_RETRIEVE, TABLE_SUPPORTS_RETRIEVE);
		propertyNameMap.put(SF_SUPPORTS_SEARCH, TABLE_SUPPORTS_SEARCH);
		propertyNameMap.put(SF_DEFAULTED, COLUMN_DEFAULTED);
		propertyNameMap.put(SF_CALCULATED, COLUMN_CALCULATED);
		propertyNameMap.put(SF_PICKLIST_VALUES, COLUMN_PICKLIST_VALUES);
		
		
		defaultTableProperties = new Properties();
		defaultTableProperties.put(TABLE_CUSTOM, FALSE_STR);
		defaultTableProperties.put(TABLE_SUPPORTS_CREATE, FALSE_STR);
		defaultTableProperties.put(TABLE_SUPPORTS_DELETE, FALSE_STR);
		defaultTableProperties.put(TABLE_SUPPORTS_LOOKUP, FALSE_STR);
		defaultTableProperties.put(TABLE_SUPPORTS_MERGE, FALSE_STR);
		defaultTableProperties.put(TABLE_SUPPORTS_QUERY, FALSE_STR);
		defaultTableProperties.put(TABLE_SUPPORTS_REPLICATE, FALSE_STR);
		defaultTableProperties.put(TABLE_SUPPORTS_RETRIEVE, FALSE_STR);
		defaultTableProperties.put(TABLE_SUPPORTS_SEARCH, FALSE_STR);
		
		defaultColumnProperties = new Properties();
		defaultColumnProperties.put(COLUMN_CALCULATED, FALSE_STR);
		defaultColumnProperties.put(TABLE_CUSTOM, FALSE_STR);
		defaultColumnProperties.put(COLUMN_DEFAULTED, FALSE_STR);
	}


	private void parseCnd() {
		
		
		ExecutionContext context = new ExecutionContext();
		
		// The original context will be loaded with basic namespaces
		Set<Namespace> basicNamespaces= context.getNamespaceRegistry().getNamespaces();
		
		// This reader is needed until Modeshape 2.5 Beta1 because the reader clones the context and it's registry isn't 
		// exposed so nay "new namespaces" aren't exposed either.
		// The temp class exposes the registry so we can determine which ones are NEW
		CndNodeTypeReader reader = new CndNodeTypeReader(context);

		// or read the CND from a string ...
		String cndContent = SalesforceExtentionPropertiesHandler.SF_CND_STRING;

		reader.read(cndContent, "string"); //$NON-NLS-1$

		// Get the node type definitions that were read in ...
		NodeTypeDefinition[] types = reader.getNodeTypeDefinitions();
		
		Set<Namespace> allNamespaces = reader.getNamespaceRegistry().getNamespaces();
		
		// Create the full Model Extension Definition object this will hold the structure for ALL of the Salesforce
		// Model extension properties.
		ModelExtensionDefinition med = new ModelExtensionDefinition();
		
		// Need to extract the "New" name-spaces from the reader
		for( Namespace ns : allNamespaces ) {
			if( !basicNamespaces.contains(ns) ) {
				if( ExtendedModelConstants.METAMODEL_URI_MAP.containsKey(ns.getPrefix())) {
					med.setMetamodelNamespace(new CndNamespace(ns.getPrefix(), ns.getNamespaceUri()));
				} else {
					med.setExtensionNamespace(new CndNamespace(ns.getPrefix(), ns.getNamespaceUri()));
				}
			}
		}

		// Iterate on the node type definitions and create CndTypeDefinition objects.
		for (NodeTypeDefinition typeDef : types) {
//			CndTypeDefinition cndTypeDef = new CndTypeDefinition();
//			cndTypeDef.setType(new PrefixedName(typeDef.getName()));
			String prefixedType = typeDef.getName();

			// For each node type get the name and first super type names
			
			// Super type will contain the key to which meta class will be extended.
			String[] superTypeNames = typeDef.getDeclaredSupertypeNames(); // WILL BE relatonal:baseTable
//			cndTypeDef.setSuperType(new PrefixedName(superTypeNames[0]));
			String prefixedSuperType = superTypeNames[0];
			
			Collection<PropertyDefinition> props = new ArrayList<PropertyDefinition>();
			
			for (javax.jcr.nodetype.PropertyDefinition propDef : typeDef.getDeclaredPropertyDefinitions()) {
				// For Each property, get the Type and default value from the CND nodes and find
				// the mapped Display Name and ID from the name map.
				
				String propTypeStr = CndPropertyDefinition.TYPE_STRINGS[propDef.getRequiredType()];
				CndPropertyDefinition prop = new CndPropertyDefinition(propTypeStr);
				prop.setModifiable(true);
				if (propDef.getRequiredType() == PropertyType.BOOLEAN) {
					prop.setAllowedValues(ALLOWED_BOOLEAN_VALUES);
				}
				
				// Convert the Modeshape/CND property name to the name required by source metadata so the right key
				// on the tag is created
				prop.setDisplayName(propertyNameMap.get(propDef.getName()));
				prop.setId(propertyNameMap.get(propDef.getName()));

				// Get the default String value
				// Assumes that there is only one default value for now, but CND/ModeShape allows for multiple
				
				Value[] defValue = propDef.getDefaultValues();
				if( defValue != null && defValue.length > 0 ) {
					if (propDef.getRequiredType() == PropertyType.BOOLEAN) {
						String value = defValue[0].toString();
						if( value.toUpperCase().contains(FALSE_STR.toUpperCase())) {
							prop.setDefaultValue(Boolean.FALSE.toString());
						} else {
							prop.setDefaultValue(Boolean.TRUE.toString());
						}
					} else {
						prop.setDefaultValue(defValue[0].toString());
					}
				}
				props.add(prop);
			}
			
			// Assumes that there is only one super-type name, but theoretically it could be "many"?
			CndTypeDefinition cndTypeDef = 
				new CndTypeDefinition(	prefixedType, 
										prefixedSuperType,
										props.toArray(new PropertyDefinition[props.size()]));
			if( prefixedType.equals(TABLE_NODE_TYPE_NAME)) {
				defaultTableTypeDefinition = cndTypeDef;
			} else if( prefixedType.equals(COLUMN_NODE_TYPE_NAME)) {
				defaultColumnTypeDefinition = cndTypeDef;
			}
		}
		
		this.cndWasParsed = true;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getDisplayName() {
		return DISPLAY_NAME;
	}


	
	public void setTableQueryable(BaseTable table, Boolean bool) throws ModelerCoreException {
		// need to end up with the following tag:
		// <tags xmi:uuid="mmuuid:bf276c12-b07c-4473-8764-5ab7b14ed639" key="extension:sf:Supports Query" value="true"/>
		helper.addProperty(table, EXT_PROP_NAMESPACE_PREFIX + TABLE_SUPPORTS_QUERY, bool.toString());
	}

	public void setTableDeletable(BaseTable table, Boolean bool) throws ModelerCoreException {
		helper.addProperty(table, EXT_PROP_NAMESPACE_PREFIX + TABLE_SUPPORTS_DELETE, bool.toString());		
	}
	
	public void setTableCreatable(BaseTable table, Boolean bool) throws ModelerCoreException {
		helper.addProperty(table, EXT_PROP_NAMESPACE_PREFIX + TABLE_SUPPORTS_CREATE, bool.toString());	
	}
	
	public void setTableSearchable(BaseTable table, Boolean bool) throws ModelerCoreException {
		helper.addProperty(table, EXT_PROP_NAMESPACE_PREFIX + TABLE_SUPPORTS_SEARCH, bool.toString());		
	}
	
	public void setTableReplicate(BaseTable table, Boolean bool) throws ModelerCoreException {
		helper.addProperty(table, EXT_PROP_NAMESPACE_PREFIX + TABLE_SUPPORTS_REPLICATE, bool.toString());;		
	}
	
	public void setTableRetrieve(BaseTable table, Boolean bool) throws ModelerCoreException {
		helper.addProperty(table, EXT_PROP_NAMESPACE_PREFIX + TABLE_SUPPORTS_RETRIEVE, bool.toString());		
	}

	
	/**
	 * Set the value of the Picklist Values column attribute.
	 * @param table
	 * @param className
	 */
	public void setAllowedColumnValues(Column column, List allowedValues) throws ModelerCoreException {
		StringBuffer picklistValues = new StringBuffer();
		Iterator iter = allowedValues.iterator();
		while (iter.hasNext()) {
			picklistValues.append((String)iter.next());
			if(iter.hasNext()) {
				picklistValues.append(',');
			}	
		}
		 
		helper.addProperty(column, EXT_PROP_NAMESPACE_PREFIX + COLUMN_PICKLIST_VALUES, picklistValues.toString());
	}

	public void setColumnCustom(Column column, Boolean bool) throws ModelerCoreException {
		helper.addProperty(column, EXT_PROP_NAMESPACE_PREFIX + TABLE_CUSTOM, bool.toString());	
	}

	public void setColumnCalculated(Column column, Boolean bool) throws ModelerCoreException {
		helper.addProperty(column, EXT_PROP_NAMESPACE_PREFIX + COLUMN_CALCULATED, bool.toString());	
	}

	public void setColumnDefaultedOnCreate(Column column, Boolean bool) throws ModelerCoreException {
		helper.addProperty(column, EXT_PROP_NAMESPACE_PREFIX + COLUMN_DEFAULTED, bool.toString());	
	}
	
	public boolean isExtendedKey(String key) {
		return key != null && key.startsWith(EXT_PROP_NAMESPACE_PREFIX);
	}
	
	private String getKey(String fullKey) {
		if( isExtendedKey(fullKey)) {
			return fullKey.substring(EXT_PROP_NAMESPACE_PREFIX.length());
		}
		return null;
	}
	
	public Properties getMissingDefaultProperties(EObject target, Properties props) {
		Properties missingProps = new Properties();
		if( target instanceof BaseTable ) {
			if( !props.containsKey(TABLE_CUSTOM) ) missingProps.put(TABLE_CUSTOM, FALSE_STR);
			if( !props.containsKey(TABLE_SUPPORTS_CREATE) ) missingProps.put(TABLE_SUPPORTS_CREATE, FALSE_STR);
			if( !props.containsKey(TABLE_SUPPORTS_DELETE) ) missingProps.put(TABLE_SUPPORTS_DELETE, FALSE_STR);
			if( !props.containsKey(TABLE_SUPPORTS_LOOKUP) ) missingProps.put(TABLE_SUPPORTS_LOOKUP, FALSE_STR);
			if( !props.containsKey(TABLE_SUPPORTS_MERGE) ) missingProps.put(TABLE_SUPPORTS_MERGE, FALSE_STR);
			if( !props.containsKey(TABLE_SUPPORTS_QUERY) ) missingProps.put(TABLE_SUPPORTS_QUERY, FALSE_STR);
			if( !props.containsKey(TABLE_SUPPORTS_REPLICATE) ) missingProps.put(TABLE_SUPPORTS_REPLICATE, FALSE_STR);
			if( !props.containsKey(TABLE_SUPPORTS_RETRIEVE) ) missingProps.put(TABLE_SUPPORTS_RETRIEVE, FALSE_STR);
			if( !props.containsKey(TABLE_SUPPORTS_SEARCH) ) missingProps.put(TABLE_SUPPORTS_SEARCH, FALSE_STR);
		} else if( target instanceof Column ) {
			if( !props.containsKey(COLUMN_CALCULATED) ) missingProps.put(COLUMN_CALCULATED, FALSE_STR);
			if( !props.containsKey(TABLE_CUSTOM) ) missingProps.put(TABLE_CUSTOM, FALSE_STR);
			if( !props.containsKey(COLUMN_DEFAULTED) ) missingProps.put(COLUMN_DEFAULTED, FALSE_STR);
			//if( !props.containsKey(COLUMN_PICKLIST_VALUES) ) missingProps.put(COLUMN_PICKLIST_VALUES, StringUtil.Constants.EMPTY_STRING);
		}
		
		return missingProps;
	}
	
	private Properties getDefaultProperties(EObject target) throws ModelWorkspaceException {
		if( isApplicable(target ) ) {
			if( target instanceof BaseTable ) {
				return defaultTableProperties;
			} else if( target instanceof Column ) {
				return defaultColumnProperties;
			}
		}
		
		return emptyProperties;
	}
	
	public Properties getExtendedProperties(EObject target) throws ModelerCoreException {
		if( target == null ) {
			return null;
		}
		Properties rawProps = helper.getProperties(target, EXT_PROP_NAMESPACE_PREFIX);
		
		if( !rawProps.isEmpty() ) {
			Properties resultProps = new Properties();
			
			for( Object keyObj : rawProps.keySet()) {
				if( keyObj instanceof String ) {
					String rawKey = (String)keyObj;
					String keyStr = getKey(rawKey);
					String value = (String)rawProps.get(rawKey);
					if( value != null ) {
						resultProps.put(keyStr, value);
					}
				}
			}
			
			Properties missingProps = getMissingDefaultProperties(target, resultProps);
			resultProps.putAll(missingProps);
			
			return resultProps;
		}

		return getDefaultProperties(target);
	}
	
	public Collection<ModelObjectExtendedProperty> getModelObjectExtendedProperties(EObject target) throws ModelerCoreException {
		if( target == null ) {
			return null;
		}
		
		Properties rawProps = helper.getProperties(target, EXT_PROP_NAMESPACE_PREFIX);
		
		Map<String, ModelObjectExtendedProperty> moepMap = new HashMap<String, ModelObjectExtendedProperty>();
		if( !rawProps.isEmpty() ) {
			Properties resultProps = new Properties();
			
			for( Object keyObj : rawProps.keySet()) {
				if( keyObj instanceof String ) {
					String rawKey = (String)keyObj;
					String keyStr = getKey(rawKey);
					String value = (String)rawProps.get(rawKey);
					if( value != null ) {
						resultProps.put(keyStr, value);
						PropertyDefinition propDef = getPropertyDefinition(target, keyStr);
						
						moepMap.put(keyStr, new ModelObjectExtendedProperty(propDef, value));
					}
				}
			}
		}
		
		// Now we fill the remaining "default" definitions
		for( PropertyDefinition propDef : getExtendedPropertyDefinitions(target)) {
			if( !moepMap.containsKey(propDef.getId()) ) {
				moepMap.put(propDef.getId(), new ModelObjectExtendedProperty(propDef, null));
			}
		}
		return moepMap.values();
	}
	
	public ExtendedModelObject getExtendedModelObject(final EObject target ) throws ModelerCoreException {
		Properties props = getExtendedProperties(target);
		ModelResource modelResource = ModelerCore.getModelEditor().findModelResource(target);
		if( props != null && modelResource != null ) {
			IResource resource = modelResource.getUnderlyingResource();
			String name = ModelerCore.getModelEditor().getName(target);
			Collection<ModelObjectExtendedProperty> moepList = getModelObjectExtendedProperties(target);
			ExtendedModelObject emo = new ExtendedModelObject(this, resource, target, name, moepList);
			return emo;
		}
		
		return null;
	}
	
	public boolean hasExtendedProperties(EObject target) throws ModelerCoreException {
		if( target == null ) {
			return false;
		}
		Properties rawProps = helper.getProperties(target, EXT_PROP_NAMESPACE_PREFIX);
		
		if( !rawProps.isEmpty() ) {
			return true;
		}

		return false;
	}
	
	private boolean isApplicableObject(EObject target) {
		if( target instanceof BaseTable || target instanceof Column ) {
			return true;
		}
		
		return false;
	}
	
	public boolean isApplicable(EObject target)  {
		ModelResource modelResource = ModelerCore.getModelEditor().findModelResource(target);
		if( modelResource != null ) {
			String value = null;
			try {
				value = (String)resourceHelper.getPropertyValue(modelResource, EXTENSION_FULL_ID_KEY);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return isApplicableObject(target) && MODEL_EXTENSION_ID.equalsIgnoreCase(value);
		}
		
		return false;
	}
	
	public boolean isApplicable(IResource resource)  {
		if( resource instanceof IFile ) {
			try {
				ModelResource modelResource = ModelerCore.getModelEditor().findModelResource((IFile)resource);
				if( modelResource != null ) {
					String value = null;
						value = (String)resourceHelper.getPropertyValue(modelResource, EXTENSION_FULL_ID_KEY);
		
					return MODEL_EXTENSION_ID.equalsIgnoreCase(value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
    public PropertyDefinition[] getExtendedPropertyDefinitions(EObject target ) throws ModelWorkspaceException {
        if( !cndWasParsed ) {
        	parseCnd();
        }
        
		if( isApplicable(target ) ) {
			if( target instanceof BaseTable ) {
				return defaultTableTypeDefinition.getPropertyDefinitions();
			} else if( target instanceof Column ) {
				return defaultColumnTypeDefinition.getPropertyDefinitions();
			}
		}

        return null;
    }
    
    private PropertyDefinition getPropertyDefinition(EObject target, String key) throws ModelWorkspaceException {
    	for( PropertyDefinition propDef : getExtendedPropertyDefinitions(target) ) {
    		if( propDef.getId().equals(key) ) {
    			return propDef;
    		}
    	}
    	
    	return null;
    }

	@Override
	public void save(EObject target, Collection<ModelObjectExtendedProperty> properties) throws ModelerCoreException {
		// Clear/remove old properties
		helper.removeProperties(target, EXT_PROP_NAMESPACE_PREFIX);
		
		// Walk through the properties, if the "value" == null or is different than the default, then write it out.
		for( ModelObjectExtendedProperty prop : properties) {
			if( !prop.isDefaultValue() ) {
				helper.addProperty(target, EXT_PROP_NAMESPACE_PREFIX + prop.getDefinition().getId(), prop.getValue());
			}
		}
	}
    
}
