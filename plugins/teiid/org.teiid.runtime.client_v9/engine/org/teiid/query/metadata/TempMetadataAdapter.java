/*
 * JBoss, Home of Professional Open Source.
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

package org.teiid.query.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IQueryNode;
import org.teiid.designer.xml.IMappingNode;
import org.teiid.metadata.Column;
import org.teiid.metadata.Procedure;
import org.teiid.metadata.Table;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.metadata.TempMetadataID.Type;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;


/**
 * <p>This is an adapter class, it contains another instance of 
 * IQueryMetadataInterface as well as a TempMetadataStore.  It defers to
 * either one of these when appropriate.</p>
 * 
 * <p>When a metadataID Object is requested for a group or element name, this
 * will first check the IQueryMetadataInterface.  If an ID wasn't found there,
 * it will then check the TempMetadataStore.</p>
 * 
 * <p>For methods that take a metadataID arg, this class may check whether it
 * is a TempMetadataID or not and react accordingly.</p>
 */
public class TempMetadataAdapter extends BasicQueryMetadataWrapper {

    private static final String SEPARATOR = "."; //$NON-NLS-1$

    /**
     * Temp model object id
     */
    public static final TempMetadataID TEMP_MODEL = new TempMetadataID("__TEMP__", Collections.EMPTY_LIST); //$NON-NLS-1$

    private TempMetadataStore tempStore;
    private Map<Object, Object> materializationTables;
    private Map<Object, QueryNode> queryNodes;
    private boolean session;
	
	/**
	 * @param metadata
	 * @param tempStore
	 */
	public TempMetadataAdapter(IQueryMetadataInterface metadata, TempMetadataStore tempStore) {
		super(metadata);
        this.tempStore = tempStore;
	}
    
    /**
     * @param metadata
     * @param tempStore
     * @param materializationTables
     * @param queryNodes
     */
    public TempMetadataAdapter(IQueryMetadataInterface metadata, TempMetadataStore tempStore, Map<Object, Object> materializationTables, Map<Object, QueryNode> queryNodes) {
    	super(metadata);
        this.tempStore = tempStore;
        this.materializationTables = materializationTables;
        this.queryNodes = queryNodes;
    } 
    
    /**
     * @return session
     */
    public boolean isSession() {
		return session;
	}
    
    /**
     * @param session
     */
    public void setSession(boolean session) {
		this.session = session;
	}
    
    @Override
    public IQueryMetadataInterface getSessionMetadata() {
    	if (isSession()) {
    		TempMetadataAdapter tma = new TempMetadataAdapter(new BasicQueryMetadata(getTeiidVersion()), this.tempStore);
    		tma.session = true;
    		return tma;
    	}
    	return this.actualMetadata.getSessionMetadata();
    }
    
    @Override
    protected IQueryMetadataInterface createDesignTimeMetadata() {
    	if (isSession()) {
    		return new TempMetadataAdapter(this.actualMetadata.getDesignTimeMetadata(), new TempMetadataStore());
    	}
		return new TempMetadataAdapter(this.actualMetadata.getDesignTimeMetadata(), tempStore, materializationTables, queryNodes);
    }
    
    /**
     * @return temp metadata store
     */
    public TempMetadataStore getMetadataStore() {
        return this.tempStore;    
    }
    
    /**
     * @return metadata underlying adapter
     */
    public IQueryMetadataInterface getMetadata() {
        return this.actualMetadata;
    }
    
    /**
     * Check metadata first, then check temp groups if not found
     */
    @Override
    public Object getElementID(String elementName)
        throws Exception {

        Object tempID = null;
        try {
            tempID = this.actualMetadata.getElementID(elementName);
        } catch (Exception e) {
            //ignore
        }

        if (tempID == null){
            tempID = this.tempStore.getTempElementID(elementName);
        }

        if(tempID != null) {
            return tempID;
        }
        throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30350, elementName));
    }
    
    /**
     * Check metadata first, then check temp groups if not found
     */
    @Override
    public Object getGroupID(String groupName)
        throws Exception {
        
        Object tempID = null;
        try {
            tempID = this.actualMetadata.getGroupID(groupName);
        } catch (Exception e) {
            //ignore
        }
        
        if (tempID == null){
            tempID = this.tempStore.getTempGroupID(groupName);
        }
        
        if(tempID != null) {
            return tempID;
        }
        throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30351, groupName));
    }

    @Override
    public Collection getGroupsForPartialName(String partialGroupName)
    		throws Exception {
    	Collection groups = super.getGroupsForPartialName(partialGroupName);
    	List<String> allGroups = new ArrayList<String>(groups);
    	for (Map.Entry<String, TempMetadataID> entry : tempStore.getData().entrySet()) {
            String name = entry.getKey();
            if (StringUtil.endsWithIgnoreCase(name, partialGroupName)
                    //don't want to match tables by anything less than the full name,
                    //since this should be a temp or a global temp and in the latter case there's a real metadata entry
                    //alternatively we could check to see if the name is already in the result list
                    && (name.length() == partialGroupName.length() || (entry.getValue().getMetadataType() != Type.TEMP && name.length() > partialGroupName.length() && name.charAt(name.length() - partialGroupName.length() - 1) == '.'))) {
                allGroups.add(name);
            }
        }
    	return allGroups;
    }

    @Override
    public Object getModelID(Object groupOrElementID)
        throws Exception {

    	groupOrElementID = getActualMetadataId(groupOrElementID);
    	
        if(groupOrElementID instanceof TempMetadataID) {
        	TempMetadataID tid = (TempMetadataID)groupOrElementID;
        	Object oid = tid.getOriginalMetadataID();
            if (oid instanceof Procedure) {
            	return actualMetadata.getModelID(oid);
            }
            return TempMetadataAdapter.TEMP_MODEL;    
        }
        //special handling for global temp tables
        Object id = groupOrElementID;
        if (groupOrElementID instanceof Column) {
        	id = ((Column)id).getParent();
        }
        if (id instanceof Table) {
        	Table t = (Table)id;
        	if (t.getTableType() == Table.Type.TemporaryTable && t.isVirtual()) {
        		return TempMetadataAdapter.TEMP_MODEL;
        	}
        }        
 		return this.actualMetadata.getModelID(groupOrElementID);
	}

	// SPECIAL: Override for temp groups
    @Override
    public String getFullName(Object metadataID)
        throws Exception {
		
		if(metadataID instanceof TempMetadataID) {
			return ((TempMetadataID)metadataID).getID();
		}
		return this.actualMetadata.getFullName(metadataID);
	}  
    
    @Override
    public String getName(Object metadataID) throws Exception {
    	if(metadataID instanceof TempMetadataID) {
    		TempMetadataID tid = (TempMetadataID)metadataID;
    		return tid.getName();
		}
		return this.actualMetadata.getName(metadataID);
    }

	// SPECIAL: Override for temp groups
    @Override
    public List getElementIDsInGroupID(Object groupID)
        throws Exception {
    	
    	groupID = getActualMetadataId(groupID);
		
		if(groupID instanceof TempMetadataID) { 
			return new ArrayList<Object>(((TempMetadataID)groupID).getElements());
		}
		return this.actualMetadata.getElementIDsInGroupID(groupID);		
	}

	// SPECIAL: Override for temp groups
    @Override
    public Object getGroupIDForElementID(Object elementID)
        throws Exception {
		
		if(elementID instanceof TempMetadataID) {
			String elementName = ((TempMetadataID)elementID).getID();
			String groupName = elementName.substring(0, elementName.lastIndexOf(SEPARATOR));
			return this.tempStore.getTempGroupID(groupName);
		}	
		return this.actualMetadata.getGroupIDForElementID(elementID);
	}

	// SPECIAL: Override for temp groups
	@Override
    public String getElementType(Object elementID)
		throws Exception {
		
		if(elementID instanceof TempMetadataID) { 
            TempMetadataID tempID = (TempMetadataID)elementID;
            if (tempID.getType() != null) {
                return DataTypeManagerService.getInstance(getTeiidVersion()).getDataTypeName( tempID.getType() );
            } 
            throw new AssertionError("No type set for element " + elementID); //$NON-NLS-1$
        }
		return this.actualMetadata.getElementType(elementID);
	}

    @Override
    public String getDefaultValue(Object elementID)
        throws Exception {
            
        if(elementID instanceof TempMetadataID) {
            return null;
        }
        return this.actualMetadata.getDefaultValue(elementID);
	}	

    @Override
    public Object getMaximumValue(Object elementID) throws Exception {
        if (elementID instanceof TempMetadataID) {
            TempMetadataID id = (TempMetadataID)elementID;
            elementID = id.getOriginalMetadataID();
            if (elementID == null) {
                return null;
            }
        }
        return this.actualMetadata.getMaximumValue(elementID);
    }

    @Override
    public Object getMinimumValue(Object elementID) throws Exception {
        if (elementID instanceof TempMetadataID) {
            TempMetadataID id = (TempMetadataID)elementID;
            elementID = id.getOriginalMetadataID();
            if (elementID == null) {
                return null;
            }
        }
        return this.actualMetadata.getMinimumValue(elementID);
    }

    /**
     * @see IQueryMetadataInterface#getDistinctValues(java.lang.Object)
     */
    @Override
    public float getDistinctValues(Object elementID) throws Exception {
        if(elementID instanceof TempMetadataID) {
            return -1;
        }         
        return this.actualMetadata.getDistinctValues(elementID);
    }

    /**
     * @see IQueryMetadataInterface#getNullValues(java.lang.Object)
     */
    @Override
    public float getNullValues(Object elementID) throws Exception {
        if (elementID instanceof TempMetadataID) {
            TempMetadataID id = (TempMetadataID)elementID;
            elementID = id.getOriginalMetadataID();
            if (elementID == null) {
                return -1;
            }
        }         
        return this.actualMetadata.getNullValues(elementID);
    }

    @Override
    public IQueryNode getVirtualPlan(Object groupID)
        throws Exception {
		
        if (this.queryNodes != null) {
        	QueryNode node = this.queryNodes.get(groupID);
        	if (node != null) {
        		return node;
        	}
        }
        
        if(groupID instanceof TempMetadataID && !(actualMetadata instanceof TempMetadataAdapter)) {
        	return ((TempMetadataID)groupID).getQueryNode();
        }            
   		return this.actualMetadata.getVirtualPlan(groupID);
	}
	
	// SPECIAL: Override for temp groups
    @Override
    public boolean isVirtualGroup(Object groupID)
        throws Exception {

		if(groupID instanceof TempMetadataID) {   
			return ((TempMetadataID)groupID).isVirtual();
		}	
		return this.actualMetadata.isVirtualGroup(groupID);
	}

    /** 
     * @see IQueryMetadataInterface#hasMaterialization(java.lang.Object)
     * @since 4.2
     */
    @Override
    public boolean hasMaterialization(Object groupID)
        throws Exception {
                
        // check if any dynamic materialization tables are defined
        if (this.materializationTables != null && this.materializationTables.containsKey(groupID)) {
            return true;
        }
        
        if(groupID instanceof TempMetadataID && !(actualMetadata instanceof TempMetadataAdapter)) {                         
            return false;
        }   
                
        return this.actualMetadata.hasMaterialization(groupID);
    }
    
    /** 
     * @see IQueryMetadataInterface#getMaterialization(java.lang.Object)
     * @since 4.2
     */
    @Override
    public Object getMaterialization(Object groupID) 
        throws Exception {
        
        // check if any dynamic materialization tables are defined
        if (this.materializationTables != null) {
            Object result = this.materializationTables.get(groupID);
            if (result != null) {
            	return result;
            }
        }
        
        if(groupID instanceof TempMetadataID && !(actualMetadata instanceof TempMetadataAdapter)) {                         
            return null;
        }   

        return this.actualMetadata.getMaterialization(groupID);
    }
    
    /** 
     * @see IQueryMetadataInterface#getMaterializationStage(java.lang.Object)
     * @since 4.2
     */
    @Override
    public Object getMaterializationStage(Object groupID) 
        throws Exception {
        
        if(groupID instanceof TempMetadataID) {                         
            return null;
        }   
        
        // we do not care about the dynamic materialization tables here as they are loaded dynamically.
        return this.actualMetadata.getMaterializationStage(groupID);
    }
    
    @Override
    public boolean isVirtualModel(Object modelID)
        throws Exception {

        if(modelID.equals(TEMP_MODEL)) {                         
            return false;
        }    
        return this.actualMetadata.isVirtualModel(modelID);
    }

	// --------------------- Implement OptimizerMetadata -------------------

	@Override
    public boolean elementSupports(Object elementID, int supportConstant)
        throws Exception {
		
        if (elementID instanceof TempMetadataID) {
            TempMetadataID id = (TempMetadataID)elementID;
            
            switch(supportConstant) {
                case SupportConstants.Element.SEARCHABLE_LIKE:   return true;
                case SupportConstants.Element.SEARCHABLE_COMPARE:return true;
                case SupportConstants.Element.SEARCHABLE_EQUALITY:return true;
                case SupportConstants.Element.SELECT:            return true;
                case SupportConstants.Element.NULL: {
                	if (id.isNotNull()) {
                		return false;
                	} 
                	if (id.isTempTable()) {
                		return true;
                	}
                	break;
                }
                case SupportConstants.Element.AUTO_INCREMENT:	 return id.isAutoIncrement();
                case SupportConstants.Element.UPDATE:			 return id.isTempTable() || id.isUpdatable();
                
            }
            
            // If this is a temp table column or real metadata is unknown, return hard-coded values
            elementID = id.getOriginalMetadataID();
            if(elementID == null || id.isTempTable()) {
                switch(supportConstant) {
                    case SupportConstants.Element.NULL:              return true;
                    case SupportConstants.Element.SIGNED:            return true;
                }
                
                return false;
            }
        }
        
        return this.actualMetadata.elementSupports(elementID, supportConstant);
	}

    /**
     * @see IQueryMetadataInterface#getIndexesInGroup(java.lang.Object)
     */
    @Override
    public Collection getIndexesInGroup(Object groupID)
        throws Exception {
    	
    	groupID = getActualMetadataId(groupID);
    	
        if(groupID instanceof TempMetadataID) {
        	List<TempMetadataID> result = ((TempMetadataID)groupID).getIndexes();
        	if (result == null) {
        		return Collections.emptyList();
        	}
        	return result;
        }
        return this.actualMetadata.getIndexesInGroup(groupID);   
    }

    @Override
    public Collection getUniqueKeysInGroup(Object groupID) 
        throws Exception {
    	
    	groupID = getActualMetadataId(groupID);
        
        if(groupID instanceof TempMetadataID) {
        	LinkedList<List<TempMetadataID>> result = new LinkedList<List<TempMetadataID>>();
        	TempMetadataID id = (TempMetadataID)groupID;
        	if (id.getPrimaryKey() != null) {
        		result.add(id.getPrimaryKey());
        	}
        	if (id.getUniqueKeys() != null) {
        		result.addAll(id.getUniqueKeys());
        	}
            return result;
        }
        return this.actualMetadata.getUniqueKeysInGroup(groupID);   
    }
    
    @Override
    public Collection getForeignKeysInGroup(Object groupID) 
        throws Exception {
        
    	groupID = getActualMetadataId(groupID);
    	
        if(groupID instanceof TempMetadataID) {
            return Collections.EMPTY_LIST;
        }
        return this.actualMetadata.getForeignKeysInGroup(groupID);   
    }    

    /**
     * @see IQueryMetadataInterface#getElementIDsInIndex(java.lang.Object)
     */
    @Override
    public List getElementIDsInIndex(Object index)
        throws Exception {
        return this.actualMetadata.getElementIDsInIndex(index);   
    }
    
    @Override
    public List getElementIDsInKey(Object keyID) 
        throws Exception {
        
    	if (keyID instanceof List) {
    		return (List)keyID;
    	}
    	
    	if (keyID instanceof TempMetadataID) {
    		TempMetadataID id = (TempMetadataID)keyID;
    		if (id.getMetadataType() == Type.INDEX) {
    			return id.getElements();
    		}
    	}
    	
        return this.actualMetadata.getElementIDsInKey(keyID);   
    }    

    @Override
    public boolean groupSupports(Object groupID, int groupConstant)
        throws Exception {
            
    	groupID = getActualMetadataId(groupID);

    	if(groupID instanceof TempMetadataID){
            return true;            
        }
        
        return this.actualMetadata.groupSupports(groupID, groupConstant);
    }
    
    @Override
    public IMappingNode getMappingNode(Object groupID)
        throws Exception {
            
        return this.actualMetadata.getMappingNode(groupID);
    }   

    @Override
    public boolean isXMLGroup(Object groupID)
        throws Exception {

        if(groupID instanceof TempMetadataID) {
            return ((TempMetadataID)groupID).getMetadataType() == Type.XML;
        }
        return this.actualMetadata.isXMLGroup(groupID);
    }

    /**
     * @see IQueryMetadataInterface#getVirtualDatabaseName()
     */
    @Override
    public String getVirtualDatabaseName() 
        throws Exception {
            
        return this.actualMetadata.getVirtualDatabaseName();
    }

	/**
	 * @see IQueryMetadataInterface#getAccessPatternsInGroup(Object)
	 */
	@Override
    public Collection getAccessPatternsInGroup(Object groupID)
		throws Exception {
		
		groupID = getActualMetadataId(groupID);
            
        if(groupID instanceof TempMetadataID) {
            TempMetadataID id = (TempMetadataID)groupID;
            
            return id.getAccessPatterns();
        }
        return this.actualMetadata.getAccessPatternsInGroup(groupID);            
	}

	/**
	 * @see IQueryMetadataInterface#getElementIDsInAccessPattern(Object)
	 */
	@Override
    public List getElementIDsInAccessPattern(Object accessPattern)
		throws Exception {
        
        if (accessPattern instanceof TempMetadataID) {
            TempMetadataID id = (TempMetadataID)accessPattern;
            if (id.getElements() != null) {
                return id.getElements();
            }
            return Collections.EMPTY_LIST;
        }
        
        return this.actualMetadata.getElementIDsInAccessPattern(accessPattern);
	}

	@Override
    public Collection getXMLTempGroups(Object groupID) 
        throws Exception{
    	
        if(groupID instanceof TempMetadataID) {
            return Collections.EMPTY_SET;
        }
        return this.actualMetadata.getXMLTempGroups(groupID);    
    }
    
    @Override
    public float getCardinality(Object groupID) 
    	throws Exception{
    	
    	groupID = getActualMetadataId(groupID);
    	
        if(groupID instanceof TempMetadataID) {	
           return ((TempMetadataID)groupID).getCardinality(); 
        }
        if (this.isSession() && groupID instanceof Table) {
        	Table t = (Table)groupID;
        	if (t.getTableType() == Table.Type.TemporaryTable && t.isVirtual()) {
        		TempMetadataID id = this.tempStore.getTempGroupID(t.getName());
        		if (id != null) {
        			return id.getCardinality();
        		}
        	}
        }
        return this.actualMetadata.getCardinality(groupID);    
    }

    @Override
    public List getXMLSchemas(Object groupID) throws Exception {
        if(groupID instanceof TempMetadataID) {
            return Collections.EMPTY_LIST;
        }
        return this.actualMetadata.getXMLSchemas(groupID);
    }

    @Override
    public Properties getExtensionProperties(Object metadataID)
        throws Exception {
            
    	metadataID = getActualMetadataId(metadataID);
    	
    	if (metadataID instanceof TempMetadataID) {
    		return IQueryMetadataInterface.EMPTY_PROPS;
    	}

        return actualMetadata.getExtensionProperties(metadataID);
    }

    @Override
    public int getElementLength(Object elementID) throws Exception {
        if (elementID instanceof TempMetadataID) {
            TempMetadataID id = (TempMetadataID)elementID;
            Object origElementID = id.getOriginalMetadataID();
            if (origElementID == null) {
                String type = getElementType(elementID);
                if(DataTypeManagerService.DefaultDataTypes.STRING.getId().equals(type)) {
                    return 255;
                }
                return 10;
            } 
            elementID = origElementID;
        }
        
        return actualMetadata.getElementLength(elementID);
    }
    
    @Override
    public int getPosition(Object elementID) throws Exception {
        if (elementID instanceof TempMetadataID) {
        	return ((TempMetadataID)elementID).getPosition();
        }
        return actualMetadata.getPosition(elementID);
    }

    @Override
    public int getPrecision(Object elementID) throws Exception {
        if (elementID instanceof TempMetadataID) {
            TempMetadataID id = (TempMetadataID)elementID;
            elementID = id.getOriginalMetadataID();
            if (elementID == null) {
                return 0;
            }
        }
        return actualMetadata.getPrecision(elementID);
    }

    @Override
    public int getRadix(Object elementID) throws Exception {
        if (elementID instanceof TempMetadataID) {
            TempMetadataID id = (TempMetadataID)elementID;
            elementID = id.getOriginalMetadataID();
            if (elementID == null) {
                return 0;
            }
        }
        return actualMetadata.getRadix(elementID);
    }

    @Override
    public int getScale(Object elementID) throws Exception {
        if (elementID instanceof TempMetadataID) {
            TempMetadataID id = (TempMetadataID)elementID;
            elementID = id.getOriginalMetadataID();
            if (elementID == null) {
                return 0;
            }
        }
        return actualMetadata.getScale(elementID);
    }        

    /**
     * Get the native type name for the element.
     * @since 4.2
     */
    @Override
    public String getNativeType(Object elementID) throws Exception {
        if (elementID instanceof TempMetadataID) {
            TempMetadataID id = (TempMetadataID)elementID;
            elementID = id.getOriginalMetadataID();
            if (elementID == null) {
                return ""; //$NON-NLS-1$
            }
        }
        
        return actualMetadata.getNativeType(elementID);
    }

	@Override
    public boolean isProcedure(Object elementID) throws Exception {
        if(elementID instanceof TempMetadataID) {
            Object oid = ((TempMetadataID) elementID).getOriginalMetadataID();
            if (oid != null) {
            	return actualMetadata.isProcedure(oid);
            }
        	return false; 
        }
        
        return actualMetadata.isProcedure(elementID);
	}
    
    @Override
    public boolean isTemporaryTable(Object groupID) throws Exception {
        if(groupID instanceof TempMetadataID) {
            return ((TempMetadataID)groupID).isTempTable();
        }
      
        if (groupID instanceof Table) {
        	Table t = (Table)groupID;
        	if (t.getTableType() == Table.Type.TemporaryTable) {
        		return true;
        	}
        }
        
        if( actualMetadata.isTemporaryTable(groupID) ) {
        	return true;
        }
        
        return false;
    }
    
    @Override
    public Object addToMetadataCache(Object metadataID, String key, Object value)
    		throws Exception {
    	if (metadataID instanceof TempMetadataID) {
    		TempMetadataID tid = (TempMetadataID)metadataID;
    		return tid.setProperty(key, value);
    	}
    	
    	return this.actualMetadata.addToMetadataCache(metadataID, key, value);
    }
    
    @Override
    public Object getFromMetadataCache(Object metadataID, String key)
    		throws Exception {
    	if (metadataID instanceof TempMetadataID) {
    		TempMetadataID tid = (TempMetadataID)metadataID;
    		return tid.getProperty(key);
    	}
    	
    	return this.actualMetadata.getFromMetadataCache(metadataID, key);
    }
    
    @Override
    public boolean isScalarGroup(Object groupID)
    		throws Exception {
    	if (groupID instanceof TempMetadataID) {
    		TempMetadataID tid = (TempMetadataID)groupID;
    		return tid.isScalarGroup();
    	}
    	
    	return this.actualMetadata.isScalarGroup(groupID);
    }
    
    @Override
    public Object getPrimaryKey(Object metadataID) {
    	
    	metadataID = getActualMetadataId(metadataID);
    	
    	if (metadataID instanceof TempMetadataID) {
    		return ((TempMetadataID)metadataID).getPrimaryKey();
    	}
    	return this.actualMetadata.getPrimaryKey(metadataID);
    }
    
    @Override
    public boolean isMultiSource(Object modelId) throws Exception {
    	if (modelId instanceof TempMetadataID) {
    		return false;
    	}
    	return this.actualMetadata.isMultiSource(modelId);
    }
    
    @Override
    public boolean isMultiSourceElement(Object elementId)
    		throws Exception {
    	if (elementId instanceof TempMetadataID) {
    		return false;
    	} 
    	return this.actualMetadata.isMultiSourceElement(elementId);
    }
    
    @Override
    public Map<Expression, Integer> getFunctionBasedExpressions(Object metadataID) {
    	if (metadataID instanceof TempMetadataID) {
    		return ((TempMetadataID)metadataID).getTableData().getFunctionBasedExpressions();
    	}
    	return super.getFunctionBasedExpressions(metadataID);
    }
    
    /**
     * @param id
     * @return actual metadata id if the object is a {@link TempMetadataID}
     */
    public static Object getActualMetadataId(Object id) {
    	if (!(id instanceof TempMetadataID)) {
    		return id;
    	}
    	TempMetadataID tid = (TempMetadataID)id;
    	Object oid = tid.getOriginalMetadataID();
        if (oid != null && tid.getTableData().getModel() != null) {
        	return tid.getOriginalMetadataID();
        }
        return tid;
    }
    
    @Override
    public String getExtensionProperty(Object metadataID, String key,
    		boolean checkUnqualified) {
        metadataID = getActualMetadataId(metadataID);
    	if (metadataID instanceof TempMetadataID) {
    		return null;
    	}
    	return super.getExtensionProperty(metadataID, key, checkUnqualified);
    }

}
