/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.xml.aspects.sql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingRoot;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.xml.PluginConstants;

/**
 * XmlContainerNodeSqlAspect
 */
public class XmlElementSqlAspect extends AbstractXmlDocumentEntitySqlAspect implements SqlColumnAspect {

    final static boolean SELECTABLE = true;
    final static boolean UPDATABLE = false;
    final static boolean AUTO_INCREMENT = false;
    final static boolean CASE_SENSITIVE = false;
    final static boolean SIGNED = false;
    final static boolean CURRENCY = false;
    final static boolean FIXED_LENGTH = false;
    final static boolean TRANSFORMATION_INPUT_PARAMETER = false;
    final static int SEARCH_TYPE = MetadataConstants.SEARCH_TYPES.SEARCHABLE; // searcheable
    final static Object DEFAULT_VALUE = null;
    final static Object MIN_VALUE = null;
    final static Object MAX_VALUE = null;
    final static int LENGTH = 0;
    final static int SCALE = 0;
    final static int NULL_TYPE = MetadataConstants.NULL_TYPES.NULLABLE; // nullable
    final static String FORMAT = null;
    final static int PRECISION = 0;
    final static int CHAR_OCTET_LENGTH = 0;
    final static int POSITION = 0;
    final static int RADIX = 0;
    final static int NULL_VALUES = 0;
    final static int DISTINCT_VALUES = 0;    

    // map between XmlElement and MappingClassColumn
    Map elementMap = null;
	private Set elementFullNames;

    /**
     * Construct an instance of XmlContainerNodeSqlAspect.
     * 
     */
    public XmlElementSqlAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(final char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.COLUMN);
    }

	/**
	 * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
	 */
	public String getName(final EObject eObject) {
		ArgCheck.isInstanceOf(XmlElement.class, eObject);

		IPath path = new Path(getShortName(eObject));
		EObject parent = eObject.eContainer();
		SqlAspect parentAspect = AspectManager.getSqlAspect(parent);
		while ( parentAspect != null ) {
            if ( parent instanceof XmlDocument ) {
                // XmlDocumentSqlAspect now implements SqlTableAspect AND SqlColumnAspect
                break;
            }
			if(parentAspect instanceof SqlColumnAspect) {
				String name = null;				
				if(parentAspect instanceof AbstractXmlDocumentEntitySqlAspect) {
					name = ((AbstractXmlDocumentEntitySqlAspect) parentAspect).getShortName(parent);
				} else {
					name = parentAspect.getName(parent);					
				}
				path = new Path("").append(name).append(path); //$NON-NLS-1$
			} else if (parentAspect instanceof SqlTableAspect) {
				break;
			}
			// Walk up to the parent ...
			parent = parent.eContainer();
			parentAspect = AspectManager.getSqlAspect(parent);
		}
		
		return path.toString().replace(IPath.SEPARATOR,'.');
	}

	@Override
    protected String getShortName(final EObject eObject) {
		ArgCheck.isInstanceOf(XmlElement.class, eObject);
		return ((XmlElement)eObject).getName();
	}

	@Override
    protected String getParentFullName(EObject eObject) {
		EObject parent = eObject.eContainer();
		if ( parent != null ) {
			SqlAspect parentAspect = AspectManager.getSqlAspect(parent);
			while ( parentAspect != null ) {
				if (parentAspect instanceof SqlTableAspect) {
					return parentAspect.getFullName(parent);
				}
				parent = parent.eContainer();
				if ( parent != null ) {
					parentAspect = AspectManager.getSqlAspect(parent);
				} else {
					return null;
				}
			}
		}
		return null;
	}

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getCharOctetLength(org.eclipse.emf.ecore.EObject)
     */
    public int getCharOctetLength(EObject eObject) {
        return CHAR_OCTET_LENGTH;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatype(org.eclipse.emf.ecore.EObject)
     */
    public EObject getDatatype(EObject eObject) {
        ArgCheck.isInstanceOf(XmlElement.class, eObject);
        XmlElement xmlElement = (XmlElement)eObject;
        if(elementMap == null) {
            populateMappingInfo(xmlElement);
        }
        
        Object lookupObj = elementMap.get(xmlElement);
        if(lookupObj == null && elementFullNames.contains(this.getFullName(xmlElement))){
        	populateMappingInfo(xmlElement);
        	lookupObj = elementMap.get(xmlElement);
        }
        
        Container cntr = ModelerCore.getContainer(eObject);
        	
        if(lookupObj != null) {
            Assertion.isInstanceOf(lookupObj, MappingClassColumn.class, null);
            MappingClassColumn mappingColumn = (MappingClassColumn) lookupObj;
            EObject type = mappingColumn.getType();
            return resolveWhenProxy(type, cntr);
        }

        try {
            final XSDComponent xsdComp = xmlElement.getXsdComponent();
            if(xsdComp instanceof XSDElementDeclaration) {
                final EObject type = ((XSDElementDeclaration)xsdComp).getTypeDefinition();
                if(type != null) {
                    return resolveWhenProxy(type, cntr);
                }
            }
            
            return ModelerCore.getDatatypeManager(eObject).getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING);
        } catch(Throwable e) {}// ignore

        return null;
    }
    
    private EObject resolveWhenProxy(EObject e, ResourceSet resolveContext) {
        EObject resolvedEObject = e;
        if (e != null && e.eIsProxy()) {
            resolvedEObject = EcoreUtil.resolve(e, resolveContext);
            if (resolvedEObject.eIsProxy()) {
                String msg = PluginConstants.Util.getString("XmlElementSqlAspect.Unable_to_resolve_proxy_with_uri",((InternalEObject)e).eProxyURI()); //$NON-NLS-1$
                PluginConstants.Util.log(IStatus.ERROR, msg);
            }
        }
        return resolvedEObject;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getNativeType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public String getNativeType(EObject eObject) {
        return null;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeName(org.eclipse.emf.ecore.EObject)
     */
    public String getDatatypeName(EObject eObject) {
        final EObject datatype = getDatatype(eObject);
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
        final String dtName = dtMgr.getName(datatype);
        return dtName == null ? DatatypeConstants.BuiltInNames.STRING : dtName;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeObjectID(org.eclipse.emf.ecore.EObject)
     */
    public String getDatatypeObjectID(EObject eObject) {
        EObject datatype = getDatatype(eObject);
        try {
            if(datatype == null) {
                datatype = ModelerCore.getWorkspaceDatatypeManager().getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING);
            }
        } catch(Throwable e) {}// ignore

        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
        final String uuid = dtMgr.getUuidString(datatype);
        return uuid == null ? "" : uuid; //$NON-NLS-1$
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDefaultValue(org.eclipse.emf.ecore.EObject)
     */
    public Object getDefaultValue(EObject eObject) {
        return DEFAULT_VALUE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getFormat(org.eclipse.emf.ecore.EObject)
     */
    public String getFormat(EObject eObject) {
        return FORMAT;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getLength(org.eclipse.emf.ecore.EObject)
     */
    public int getLength(EObject eObject) {
        return LENGTH;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getMaxValue(org.eclipse.emf.ecore.EObject)
     */
    public Object getMaxValue(EObject eObject) {
        return MAX_VALUE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getMinValue(org.eclipse.emf.ecore.EObject)
     */
    public Object getMinValue(EObject eObject) {
        return MIN_VALUE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getNullType(org.eclipse.emf.ecore.EObject)
     */
    public int getNullType(EObject eObject) {
        return NULL_TYPE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getPosition(org.eclipse.emf.ecore.EObject)
     */
    public int getPosition(EObject eObject) {
        return POSITION;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getPrecision(org.eclipse.emf.ecore.EObject)
     */
    public int getPrecision(EObject eObject) {
        return PRECISION;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getRadix(org.eclipse.emf.ecore.EObject)
     */
    public int getRadix(EObject eObject) {
        return RADIX;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDistinctValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public int getDistinctValues(EObject eObject) {
        return DISTINCT_VALUES;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getNullValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public int getNullValues(EObject eObject) {
        return NULL_VALUES;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getRuntimeType(org.eclipse.emf.ecore.EObject)
     */
    public String getRuntimeType(EObject eObject) {
        String runtimeType = null;
        EObject datatype = getDatatype(eObject);
        if(datatype != null) {
            runtimeType = ModelerCore.getDatatypeManager(eObject,true).getRuntimeTypeName(datatype);
        }
        if(runtimeType == null) {
            runtimeType = DatatypeConstants.RuntimeTypeNames.STRING;
        }
        return runtimeType;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getScale(org.eclipse.emf.ecore.EObject)
     */
    public int getScale(EObject eObject) {
        return SCALE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getSearchType(org.eclipse.emf.ecore.EObject)
     */
    public int getSearchType(EObject eObject) {
        return SEARCH_TYPE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isAutoIncrementable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isAutoIncrementable(EObject eObject) {
        return AUTO_INCREMENT;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isCaseSensitive(org.eclipse.emf.ecore.EObject)
     */
    public boolean isCaseSensitive(EObject eObject) {
        return CASE_SENSITIVE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isCurrency(org.eclipse.emf.ecore.EObject)
     */
    public boolean isCurrency(EObject eObject) {
        return CURRENCY;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isFixedLength(org.eclipse.emf.ecore.EObject)
     */
    public boolean isFixedLength(EObject eObject) {
        return FIXED_LENGTH;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isSelectable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSelectable(EObject eObject) {
        return SELECTABLE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isSigned(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSigned(EObject eObject) {
        return SIGNED;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isTranformationInputParameter(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isTranformationInputParameter(EObject eObject) {
        ArgCheck.isInstanceOf(XmlElement.class, eObject);
        final XmlElement element = (XmlElement)eObject; 
        
        if(element instanceof XmlRoot) {
            return false;
        }
        
        final XmlDocument doc = getDocParent(element);
        final XmlElement parent = getElementParent(element);
        if(doc == null || parent == null) {
            return false;
        }
        
        Resource eResource = element.eResource();
        if (eResource instanceof EmfResource) {
            EmfResource emfResource = (EmfResource)eResource;               
            ModelContents contents = emfResource.getModelContents();
            if (contents != null) {          
                for (final Iterator mappings = contents.getTransformations(doc).iterator(); mappings.hasNext();) {
                    final TreeMappingRoot tmr = (TreeMappingRoot)mappings.next();
                    if( tmr.getOutputs().contains(parent) ) {
                        final Iterator nested = tmr.getNested().iterator();
                        while(nested.hasNext() ) {
                            final Mapping mapping = (Mapping)nested.next();
                            if(mapping.getOutputs().contains(element)) {
                            	if (mapping.getInputs().size() == 0) {
                            		return false;
                            	}
                                Iterator i = mapping.getInputs().iterator();
                                while (i.hasNext()) {
                                	MappingClassColumn column = (MappingClassColumn)i.next();
                                	SqlAspect aspect = AspectManager.getSqlAspect(column);
                                	if (!(aspect instanceof SqlColumnAspect)) {
                                		return false;
                                	}
                                	if (!((SqlColumnAspect)aspect).isTranformationInputParameter(column)) {
                                		return false;
                                	}
                                }
                                return true;
                            }
                        }
                    }
                }
            }            
        }
        return false;
    }
        
    private XmlDocument getDocParent(final XmlElement child) {
        if(child == null) {
            return null;
        }
        
        EObject parent = child.eContainer();
        while(parent != null) {
            if(parent instanceof XmlDocument) {
                return (XmlDocument)parent;
            }
            
            parent = parent.eContainer();
        }
        
        return null;
    }
    
    private XmlElement getElementParent(final XmlElement child) {
        if(child == null) {
            return null;
        }
        
        EObject parent = child.eContainer();
        while(parent != null) {
            if(parent instanceof XmlElement) {
                return (XmlElement)parent;
            }
            
            parent = parent.eContainer();
        }
        
        return null;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isUpdatable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isUpdatable(EObject eObject) {
        return UPDATABLE;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetDatatype()
     * @since 4.2
     */
    public boolean canSetDatatype() {
        return false;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#setDatatype(org.eclipse.emf.ecore.EObject, com.metamatrix.metamodels.core.Datatype)
     */
    public void setDatatype(EObject eObject, EObject datatype) {
        throw new UnsupportedOperationException(PluginConstants.Util.getString("XmlElementSqlAspect.Datatype_cannot_be_set_on_an_XMLElement_1")); //$NON-NLS-1$
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetLength()
     * @since 4.2
     */
    public boolean canSetLength() {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#setLength(org.eclipse.emf.ecore.EObject, int)
     */
    public void setLength(EObject eObject, int length) {
        throw new UnsupportedOperationException(PluginConstants.Util.getString("XmlElementSqlAspect.Length_cannot_be_set_on_an_XMLElement_2")); //$NON-NLS-1$
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetNullType()
     * @since 4.2
     */
    public boolean canSetNullType() {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#setNullType(org.eclipse.emf.ecore.EObject, int)
     */
    public void setNullType(EObject eObject, int nullType) {
        throw new UnsupportedOperationException(PluginConstants.Util.getString("XmlElementSqlAspect.NullType_cannot_be_set_on_an_XMLElement_3")); //$NON-NLS-1$
    }
    
    Map getMappingInfo(EObject xmlEntity) {
        if(this.elementMap != null) {
            return this.elementMap;
        }
        populateMappingInfo(xmlEntity);
        
        return this.elementMap;
    }
    
    private void populateMappingInfo(EObject xmlEntity) {
        Resource resource = xmlEntity.eResource();

        // model contents for this resource 
        ModelContents mdlContents = new ModelContents(resource);
        XmlDocument document = getXmlDocument(xmlEntity);
        // fill the map with element to its mappingClass column value
        this.elementMap = new HashMap();  
        this.elementFullNames = new HashSet();
        // get the mapping root associated with the transformation
        Iterator rootIter =  mdlContents.getTransformations(document).iterator();
        while(rootIter.hasNext()) {
            MappingRoot mappingRoot = (MappingRoot) rootIter.next();
            // if there is a mapping root
            if(mappingRoot != null && mappingRoot instanceof TreeMappingRoot) {
                Iterator mappingIter = mappingRoot.getNested().iterator();
                while(mappingIter.hasNext()) {
                    Mapping nestedMapping = (Mapping) mappingIter.next();
                    // mapping Class columns
                    List inputs = nestedMapping.getInputs();
                    // xml elements
                    List outputs = nestedMapping.getOutputs();
                    if(!outputs.isEmpty() && !inputs.isEmpty()) {
                        Object output = outputs.iterator().next();
                        Object input = inputs.iterator().next();
                        elementMap.put(output, input);
                        if(output instanceof XmlElement){
                        	elementFullNames.add(this.getFullName((EObject)output));   
                        }
                    } 
                }
            }
        }
    }

    private XmlDocument getXmlDocument(EObject xmlElement) {
        EObject container = xmlElement.eContainer();
        EObject document = null;
        // append parent information in front of the eObject name
        while(container != null) {
            document = container;
            container = container.eContainer();
        }

        Assertion.isInstanceOf(document, XmlDocument.class, null);
        return (XmlDocument) document;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
    public boolean isDatatypeFeature(final EObject eObject, final EStructuralFeature eFeature) {
        // there is no datattype feature on xml attributes
        return false;
    }

}
