/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.modelextension;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.properties.PropertyDefinition;
import org.teiid.designer.extension.cnd.CndTypeDefinition;
import org.teiid.designer.extension.properties.ExtendedModelObject;
import org.teiid.designer.extension.properties.IExtensionPropertiesHandler;
import org.teiid.designer.extension.properties.ModelObjectExtendedProperty;

import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelObjectAnnotationHelper;
import com.metamatrix.modeler.internal.core.workspace.ResourceAnnotationHelper;
import com.metamatrix.modeler.modelgenerator.salesforce.SalesforceConstants;

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
public class SalesforceExtentionPropertiesHandler implements IExtensionPropertiesHandler, SalesforceConstants {
	
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
			DASH + SP + CND.SUPPORTS_CREATE + SP + CND.BOOLEAN_TYPE_STR + SP + EQ + CND.FALSE_DEFAULT_STR + CR +
			DASH + SP + CND.SUPPORTS_DELETE + SP + CND.BOOLEAN_TYPE_STR + SP + EQ + CND.FALSE_DEFAULT_STR + CR +
			DASH + SP + CND.CUSTOM + SP + CND.BOOLEAN_TYPE_STR + SP + EQ + CND.FALSE_DEFAULT_STR + CR +
			DASH + SP + CND.SUPPORTS_LOOKUP + SP + CND.BOOLEAN_TYPE_STR + SP + EQ + CND.FALSE_DEFAULT_STR + CR +
			DASH + SP + CND.SUPPORTS_MERGE + SP + CND.BOOLEAN_TYPE_STR + SP + EQ + CND.FALSE_DEFAULT_STR + CR +
			DASH + SP + CND.SUPPORTS_QUERY + SP + CND.BOOLEAN_TYPE_STR + SP + EQ + CND.FALSE_DEFAULT_STR + CR +
			DASH + SP + CND.SUPPORTS_REPLICATE + SP + CND.BOOLEAN_TYPE_STR + SP + EQ + CND.FALSE_DEFAULT_STR + CR +
			DASH + SP + CND.SUPPORTS_RETRIEVE + SP + CND.BOOLEAN_TYPE_STR + SP + EQ + CND.FALSE_DEFAULT_STR + CR +
			DASH + SP + CND.SUPPORTS_SEARCH + SP + CND.BOOLEAN_TYPE_STR + SP + EQ + CND.FALSE_DEFAULT_STR + CR + CR +
			
			"[salesforce:columnCapabilities] > relational:column" + CR + //$NON-NLS-1$
			DASH + SP + CND.DEFAULTED + SP + CND.BOOLEAN_TYPE_STR + SP + EQ + CND.FALSE_DEFAULT_STR + CR +
			DASH + SP + CND.CALCULATED + SP + CND.BOOLEAN_TYPE_STR + SP + EQ + CND.FALSE_DEFAULT_STR + CR +
			DASH + SP + CND.CUSTOM + SP + CND.BOOLEAN_TYPE_STR + SP + EQ + CND.FALSE_DEFAULT_STR + CR +
			DASH + SP + CND.PICKLIST_VALUES + SP + CND.STRING_TYPE_STR + SP + "multiple" + CR; //$NON-NLS-1$
	
	public SalesforceExtentionPropertiesHandler() {
		super();
		initialize();
	}

	private void initialize() {
		propertyNameMap = new HashMap<String, String>();
		propertyNameMap.put(CND.CUSTOM, SF_Table.CUSTOM);
		propertyNameMap.put(CND.SUPPORTS_CREATE, SF_Table.SUPPORTS_CREATE);
		propertyNameMap.put(CND.SUPPORTS_DELETE, SF_Table.SUPPORTS_DELETE);
		propertyNameMap.put(CND.SUPPORTS_LOOKUP, SF_Table.SUPPORTS_LOOKUP);
		propertyNameMap.put(CND.SUPPORTS_MERGE, SF_Table.SUPPORTS_MERGE);
		propertyNameMap.put(CND.SUPPORTS_QUERY, SF_Table.SUPPORTS_QUERY);
		propertyNameMap.put(CND.SUPPORTS_REPLICATE, SF_Table.SUPPORTS_REPLICATE);
		propertyNameMap.put(CND.SUPPORTS_RETRIEVE, SF_Table.SUPPORTS_RETRIEVE);
		propertyNameMap.put(CND.SUPPORTS_SEARCH, SF_Table.SUPPORTS_SEARCH);
		propertyNameMap.put(CND.DEFAULTED, SF_Column.DEFAULTED);
		propertyNameMap.put(CND.CALCULATED, SF_Column.CALCULATED);
		propertyNameMap.put(CND.PICKLIST_VALUES, SF_Column.PICKLIST_VALUES);
		
		
		defaultTableProperties = new Properties();
		defaultTableProperties.put(SF_Table.CUSTOM, FALSE_STR);
		defaultTableProperties.put(SF_Table.SUPPORTS_CREATE, FALSE_STR);
		defaultTableProperties.put(SF_Table.SUPPORTS_DELETE, FALSE_STR);
		defaultTableProperties.put(SF_Table.SUPPORTS_LOOKUP, FALSE_STR);
		defaultTableProperties.put(SF_Table.SUPPORTS_MERGE, FALSE_STR);
		defaultTableProperties.put(SF_Table.SUPPORTS_QUERY, FALSE_STR);
		defaultTableProperties.put(SF_Table.SUPPORTS_REPLICATE, FALSE_STR);
		defaultTableProperties.put(SF_Table.SUPPORTS_RETRIEVE, FALSE_STR);
		defaultTableProperties.put(SF_Table.SUPPORTS_SEARCH, FALSE_STR);
		
		defaultColumnProperties = new Properties();
		defaultColumnProperties.put(SF_Column.CALCULATED, FALSE_STR);
		defaultColumnProperties.put(SF_Table.CUSTOM, FALSE_STR);
		defaultColumnProperties.put(SF_Column.DEFAULTED, FALSE_STR);
	}


	private void parseCnd() {
/*
		
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
*/
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.IExtensionPropertiesHandler#getID()
     */
	public String getID() {
		return ID;
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.IExtensionPropertiesHandler#getDisplayName()
     */
	public String getDisplayName() {
		return DISPLAY_NAME;
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
		 
		helper.addProperty(column, EXT_PROP_NAMESPACE_PREFIX + SF_Column.PICKLIST_VALUES, picklistValues.toString());
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.IExtensionPropertiesHandler#isExtendedKey(String)
     */
	public boolean isExtendedKey(String key) {
		return key != null && key.startsWith(EXT_PROP_NAMESPACE_PREFIX);
	}
	
	private String getKey(String fullKey) {
		if( isExtendedKey(fullKey)) {
			return fullKey.substring(EXT_PROP_NAMESPACE_PREFIX.length());
		}
		return null;
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.IExtensionPropertiesHandler#getMissingDefaultProperties(EObject, Properties)
     */
	public Properties getMissingDefaultProperties(EObject target, Properties props) {
		Properties missingProps = new Properties();
		if( target instanceof BaseTable ) {
			if( !props.containsKey(SF_Table.CUSTOM) ) missingProps.put(SF_Table.CUSTOM, FALSE_STR);
			if( !props.containsKey(SF_Table.SUPPORTS_CREATE) ) missingProps.put(SF_Table.SUPPORTS_CREATE, FALSE_STR);
			if( !props.containsKey(SF_Table.SUPPORTS_DELETE) ) missingProps.put(SF_Table.SUPPORTS_DELETE, FALSE_STR);
			if( !props.containsKey(SF_Table.SUPPORTS_LOOKUP) ) missingProps.put(SF_Table.SUPPORTS_LOOKUP, FALSE_STR);
			if( !props.containsKey(SF_Table.SUPPORTS_MERGE) ) missingProps.put(SF_Table.SUPPORTS_MERGE, FALSE_STR);
			if( !props.containsKey(SF_Table.SUPPORTS_QUERY) ) missingProps.put(SF_Table.SUPPORTS_QUERY, FALSE_STR);
			if( !props.containsKey(SF_Table.SUPPORTS_REPLICATE) ) missingProps.put(SF_Table.SUPPORTS_REPLICATE, FALSE_STR);
			if( !props.containsKey(SF_Table.SUPPORTS_RETRIEVE) ) missingProps.put(SF_Table.SUPPORTS_RETRIEVE, FALSE_STR);
			if( !props.containsKey(SF_Table.SUPPORTS_SEARCH) ) missingProps.put(SF_Table.SUPPORTS_SEARCH, FALSE_STR);
		} else if( target instanceof Column ) {
			if( !props.containsKey(SF_Column.CALCULATED) ) missingProps.put(SF_Column.CALCULATED, FALSE_STR);
			if( !props.containsKey(SF_Table.CUSTOM) ) missingProps.put(SF_Table.CUSTOM, FALSE_STR);
			if( !props.containsKey(SF_Column.DEFAULTED) ) missingProps.put(SF_Column.DEFAULTED, FALSE_STR);
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
	
    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.IExtensionPropertiesHandler#getExtendedProperties(EObject)
     */
	@Override
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
	
    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.IExtensionPropertiesHandler#getExtendedModelObject(EObject)
     */
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
	
    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.IExtensionPropertiesHandler#isApplicable(EObject)
     */
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
	
	public static boolean isSaleforceResource(final IResource resource) {
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
	
    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.IExtensionPropertiesHandler#isApplicable(IResource)
     */
	@Override
	public boolean isApplicable(IResource resource)  {
		return isSaleforceResource(resource);
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
    
    public PropertyDefinition getPropertyDefinition(EObject target, String key) throws ModelWorkspaceException {
    	for( PropertyDefinition propDef : getExtendedPropertyDefinitions(target) ) {
    		if( propDef.getId().equals(key) ) {
    			return propDef;
    		}
    	}
    	
    	return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.IExtensionPropertiesHandler#save(EObject, Collection)
     */
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
