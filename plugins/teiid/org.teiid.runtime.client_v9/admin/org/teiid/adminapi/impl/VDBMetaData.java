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
package org.teiid.adminapi.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.teiid.adminapi.DataPolicy;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.Translator;
import org.teiid.adminapi.VDB;
import org.teiid.core.util.CopyOnWriteLinkedHashMap;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;


public class VDBMetaData extends AdminObjectImpl implements VDB, Cloneable {

	private static final String VERSION_DELIM = "."; //$NON-NLS-1$

	private static final long serialVersionUID = -4723595252013356436L;

//	/**
//     * This ONLY exists to ensure that the serialisation framework
//     * has access to the anonymous class VDBMetaData$1.
//     *
//     * IT SHOULD NEVER BE USED FOR ANYTHING ELSE!!!
//     */
//    @Removed(Version.TEIID_8_0)
//	private transient ListOverMap<ModelMetaData> sevenModels = new ListOverMap<ModelMetaData>(new KeyBuilder<ModelMetaData>() {
//        private static final long serialVersionUID = 846247100420118961L;
//
//        @Override
//        public String getKey(ModelMetaData entry) {
//            return entry.getName();
//        }
//    });

//    /**
//     * This ONLY exists to ensure that the serialisation framework
//     * has access to the anonymous class VDBMetaData$2.
//     *
//     * IT SHOULD NEVER BE USED FOR ANYTHING ELSE!!!
//     */
//    @Removed(Version.TEIID_8_0)
//    private transient ListOverMap<VDBTranslatorMetaData> sevenTranslators = new ListOverMap<VDBTranslatorMetaData>(new KeyBuilder<VDBTranslatorMetaData>() {
//        private static final long serialVersionUID = 3890502172003653563L;
//
//        @Override
//        public String getKey(VDBTranslatorMetaData entry) {
//            return entry.getName();
//        }
//    }); 
    
//    /**
//     * This ONLY exists to ensure that the serialisation framework
//     * has access to the anonymous class VDBMetaData$3.
//     *
//     * IT SHOULD NEVER BE USED FOR ANYTHING ELSE!!!
//     */
//    @Removed(Version.TEIID_8_0)
//    private transient ListOverMap<DataPolicyMetadata> sevenDataPolicies = new ListOverMap<DataPolicyMetadata>(new KeyBuilder<DataPolicyMetadata>() {
//        private static final long serialVersionUID = 4954591545242715254L;
//
//        @Override
//        public String getKey(DataPolicyMetadata entry) {
//            return entry.getName();
//        }
//    }); 

	private LinkedHashMap<String, ModelMetaData> models = new LinkedHashMap<String, ModelMetaData>();

	private LinkedHashMap<String, VDBTranslatorMetaData> translators = new LinkedHashMap<String, VDBTranslatorMetaData>(); 

	private LinkedHashMap<String, DataPolicyMetadata> dataPolicies = new LinkedHashMap<String, DataPolicyMetadata>(); 

	private List<VDBImportMetadata> imports = new ArrayList<VDBImportMetadata>(2);

	private List<EntryMetaData> entries = new ArrayList<EntryMetaData>(2);
	
	private String version = "1"; //$NON-NLS-1$
	private String description;

	private boolean xmlDeployment = false;

	private volatile VDB.Status status = VDB.Status.ACTIVE;
	private ConnectionType connectionType = VDB.ConnectionType.BY_VERSION;
	
	private boolean removed;

	private long queryTimeout = Long.MIN_VALUE;

	private Set<String> importedModels = Collections.emptySet();

	private Map<String, Boolean> visibilityOverrides = new HashMap<String, Boolean>(2);

	private LinkedHashMap<String, ModelMetaData> convertModels(ListOverMap<ModelMetaData> overMap) {
        LinkedHashMap<String, ModelMetaData> newMap = new LinkedHashMap<String, ModelMetaData>();
        for (Entry<String, ModelMetaData> entry : overMap.getMap().entrySet()) {
            newMap.put(entry.getKey(), entry.getValue());
        }

        return newMap;
    }

    private LinkedHashMap<String, DataPolicyMetadata> convertDataPolicies(ListOverMap<DataPolicyMetadata> overMap) {
        LinkedHashMap<String, DataPolicyMetadata> newMap = new LinkedHashMap<String, DataPolicyMetadata>();
        for (Entry<String, DataPolicyMetadata> entry : overMap.getMap().entrySet()) {
            newMap.put(entry.getKey(), entry.getValue());
        }

        return newMap;
    }

    private LinkedHashMap<String, VDBTranslatorMetaData> convertTranslators(ListOverMap<VDBTranslatorMetaData> overMap) {
        LinkedHashMap<String, VDBTranslatorMetaData> newMap = new LinkedHashMap<String, VDBTranslatorMetaData>();
        for (Entry<String, VDBTranslatorMetaData> entry : overMap.getMap().entrySet()) {
            newMap.put(entry.getKey(), entry.getValue());
        }

        return newMap;
    }

    /*
     * Helper method for serialization to deal with differences between Teiid 7 and 8
     */
	@SuppressWarnings("nls")
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        GetField readFields = ois.readFields();

        /* models */
        Object serModels = readFields.get("models", null);
        if (isLinkedHashMap(serModels)) { /* Teiid Version 8+ */
            models = (LinkedHashMap<String, ModelMetaData>) serModels;
        } else if (isListOverMap(serModels)) { /* Teiid Version 7 */
            ListOverMap<ModelMetaData> overMap = (ListOverMap<ModelMetaData>) serModels;
            models = convertModels(overMap);
        } else
            throw new IllegalStateException();

        /* translators */
        Object serTranslators = readFields.get("translators", null);
        if (isLinkedHashMap(serModels)) { /* Teiid Version 8+ */
            translators = (LinkedHashMap<String, VDBTranslatorMetaData>) serTranslators;
        } else if (isListOverMap(serModels)) { /* Teiid Version 7 */
            ListOverMap<VDBTranslatorMetaData> overMap = (ListOverMap<VDBTranslatorMetaData>) serTranslators;
            translators = convertTranslators(overMap);
        } else
            throw new IllegalStateException();

        /* data policies */
        Object serDataPolicies = readFields.get("dataPolicies", null);
        if (isLinkedHashMap(serModels)) { /* Teiid Version 8+ */
            dataPolicies = (LinkedHashMap<String, DataPolicyMetadata>) serDataPolicies;
        } else if (isListOverMap(serModels)) { /* Teiid Version 7 */
            ListOverMap<DataPolicyMetadata> overMap = (ListOverMap<DataPolicyMetadata>) serDataPolicies;
            dataPolicies = convertDataPolicies(overMap);
        } else
            throw new IllegalStateException();

        version = (String)readFields.get("version", "1");
        description = (String) readFields.get("description", null);
        
        /* Teiid 8+ */
        imports = (List<VDBImportMetadata>) readFields.get("imports", null);
        entries = (List<EntryMetaData>) readFields.get("entries", null);
        xmlDeployment = readFields.get("xmlDeployment", false);
        status = (Status) readFields.get("status", null);
        connectionType = (ConnectionType) readFields.get("connectionType", null);
        removed = readFields.get("removed", false);
        queryTimeout = readFields.get("queryTimeout", Long.MIN_VALUE);
        importedModels = (Set<String>) readFields.get("importedModels", null);
    }

    public String getFullName() {
		return getName() + VERSION_DELIM + getVersion();
	}
	
	@Override
	public ConnectionType getConnectionType() {
		return this.connectionType;
	}
	
	public void setConnectionType(ConnectionType allowConnections) {
		this.connectionType = allowConnections;
	}
	
	public void setConnectionType(String allowConnections) {
		this.connectionType = ConnectionType.valueOf(allowConnections);
	}
	
	@Override
	public Status getStatus() {
		return this.status;
	}
	
	public synchronized void setStatus(Status s) {
		this.notifyAll();
		this.status = s;
	}
	
	public void setStatus(String s) {
		setStatus(Status.valueOf(s));
	}
	
	@Override
	public String getVersion() {
		return this.version;
	}
	
	public void setVersion(int version) {
		this.version = String.valueOf(version);
	}
	
	public void setVersion(String version) {
		this.version = version;
	} 

	@Override
	public List<Model> getModels(){
		return new ArrayList<Model>(this.models.values());
	}
	
	public LinkedHashMap<String, ModelMetaData> getModelMetaDatas() {
		return this.models;
	}
	
	/**
	 * @param models
	 */
	public void setModels(Collection<ModelMetaData> models) {
		this.models.clear();
		for (ModelMetaData obj : models) {
			addModel(obj);
		}
	}
	
	public ModelMetaData addModel(ModelMetaData m) {
		return this.models.put(m.getName(), m);
	}	
	
	@Override
	public List<Translator> getOverrideTranslators() {
		return new ArrayList<Translator>(this.translators.values());
	}
	
	public LinkedHashMap<String, VDBTranslatorMetaData> getOverrideTranslatorsMap() {
		return this.translators;
	}
	
	public void setOverrideTranslators(List<Translator> translators) {
		for (Translator t: translators) {
			this.translators.put(t.getName(), (VDBTranslatorMetaData)t);
		}
	}
	
	public void addOverideTranslator(VDBTranslatorMetaData t) {
		this.translators.put(t.getName(), t);
	}
	
	public boolean isOverideTranslator(String name) {
		return this.translators.containsKey(name);
	}
	
	@Override
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String desc) {
		this.description = desc;
	}

	@Override
	public List<String> getValidityErrors(){
		List<String> allErrors = new ArrayList<String>();
		for (ModelMetaData model:this.models.values()) {
			List<ModelMetaData.Message> errors = model.getMessages();
			if (errors != null && !errors.isEmpty()) {
				for (ModelMetaData.Message m:errors) {
					if (m.getSeverity() == ModelMetaData.Message.Severity.ERROR) {
						allErrors.add(m.getValue());
					}
				}
			}
		}
		return allErrors; 
	}
	
	@Override
    public boolean isValid() {
        return status == Status.ACTIVE && !hasErrors();
    } 	
	
	public boolean hasErrors() {
		for (ModelMetaData model : this.models.values()) {
			if (model.hasErrors()) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return getName()+VERSION_DELIM+getVersion()+ models.values(); 
	}
	
	@Override
    public boolean isVisible(String modelName) {
        ModelMetaData model = getModel(modelName);
        if (model == null) {
            return true;
        }
        if (!visibilityOverrides.isEmpty()) {
            Boolean result = visibilityOverrides.get(modelName);
            if (result != null) {
                return result;
            }
        }
        return model.isVisible();
    }

	public ModelMetaData getModel(String modelName) {
		return this.models.get(modelName);
	}
		
	/**
	 * If this is a *-vdb.xml deployment
	 * @return
	 */
	public boolean isXmlDeployment() {
		return xmlDeployment;
	}

	public void setXmlDeployment(boolean dynamic) {
		this.xmlDeployment = dynamic;
	}	
	
	@Override
	public List<DataPolicy> getDataPolicies(){
		return new ArrayList<DataPolicy>(this.dataPolicies.values());
	}	
	
	/**
	 * This method is required by the Management framework to write the mappings to the persistent form. The actual assignment is done
	 * in the VDBMetaDataClassInstancefactory
	 * @param policies
	 */
	public void setDataPolicies(List<DataPolicy> policies){
		this.dataPolicies.clear();
		for (DataPolicy policy:policies) {
			this.dataPolicies.put(policy.getName(), (DataPolicyMetadata)policy);
		}
	}	
	
	public DataPolicyMetadata addDataPolicy(DataPolicyMetadata policy){
		return this.dataPolicies.put(policy.getName(), policy);
	}
	
	public LinkedHashMap<String, DataPolicyMetadata> getDataPolicyMap() {
		return this.dataPolicies;
	}
	
	public VDBTranslatorMetaData getTranslator(String name) {
		return this.translators.get(name);
	}
	
	public boolean isPreview() {
		return Boolean.valueOf(getPropertyValue("preview")); //$NON-NLS-1$
	}
	public long getQueryTimeout() {
		if (queryTimeout == Long.MIN_VALUE) {
			String timeout = getPropertyValue("query-timeout"); //$NON-NLS-1$
			if (timeout != null) {
				queryTimeout = Math.max(0, Long.parseLong(timeout));
			} else {
				queryTimeout = 0;
			}
		}
		return queryTimeout;
	}	
	
	public List<VDBImportMetadata> getVDBImports() {
		return imports;
	}
	
	public Set<String> getImportedModels() {
		return importedModels;
	}
	
	public void setImportedModels(Set<String> importedModels) {
		this.importedModels = importedModels;
	}

	@Override
	public List<EntryMetaData> getEntries() {
		return this.entries;
	}
	
	public void setEntries(List<EntryMetaData> entries) {
		this.entries = entries;
	}
	
	@Override
	public VDBMetaData clone() {
		try {
			VDBMetaData clone = (VDBMetaData) super.clone();
			clone.models = new LinkedHashMap<String, ModelMetaData>(this.models);
			clone.attachments = new CopyOnWriteLinkedHashMap<Class<?>, Object>();
            clone.attachments.putAll(attachments);
            clone.dataPolicies = new LinkedHashMap<String, DataPolicyMetadata>(dataPolicies);
            clone.visibilityOverrides = new HashMap<String, Boolean>(visibilityOverrides);
            return clone;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Since(Version.TEIID_8_9)
	public void setVisibilityOverride(String name, boolean visible) {
        this.visibilityOverrides.put(name, visible);
    }

	@Since(Version.TEIID_8_9)
    public Map<String, Boolean> getVisibilityOverrides() {
        return visibilityOverrides;
    }
}
