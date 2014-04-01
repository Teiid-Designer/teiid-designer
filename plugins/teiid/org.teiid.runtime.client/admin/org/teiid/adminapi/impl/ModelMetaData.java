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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.teiid.adminapi.Model;
import org.teiid.core.util.ArgCheck;
import org.teiid.core.util.CopyOnWriteLinkedHashMap;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.annotation.Since;


public class ModelMetaData extends AdminObjectImpl implements Model, Serializable {
	
	private static final int DEFAULT_ERROR_HISTORY = 10;
	private static final String SUPPORTS_MULTI_SOURCE_BINDINGS_KEY_OLD = "supports-multi-source-bindings"; //$NON-NLS-1$
	private static final String SUPPORTS_MULTI_SOURCE_BINDINGS_KEY = "multisource"; //$NON-NLS-1$
	private static final long serialVersionUID = 3714234763056162230L;

	/**
     * This ONLY exists to ensure that the serialisation framework
     * has access to the anonymous class ModelMetaData$1.
     *
     * IT SHOULD NEVER BE USED FOR ANYTHING ELSE!!!
     */
    @Removed("8.0.0")
	private transient ListOverMap<SourceMappingMetadata> sevenSources = new ListOverMap<SourceMappingMetadata>(new KeyBuilder<SourceMappingMetadata>() {
        private static final long serialVersionUID = 2273673984691112369L;

        @Override
        public String getKey(SourceMappingMetadata entry) {
            return entry.getName();
        }
    });

	protected Map<String, SourceMappingMetadata> sources = new CopyOnWriteLinkedHashMap<String, SourceMappingMetadata>();	
	protected String modelType = Type.PHYSICAL.name();
	protected String description;	
	protected String path; 
    protected Boolean visible = true;

    @Since("8.0.0")
    protected List<Message> messages;

    protected transient List<Message> runtimeMessages;

    @Since("8.0.0")
    protected String schemaSourceType;

    @Since("8.0.0")
	protected String schemaText;

    @Since("8.0.0")
	protected MetadataStatus metadataStatus = MetadataStatus.LOADING;

	private Map<String, SourceMappingMetadata> convertSources(ListOverMap<SourceMappingMetadata> overMap) {
        Map<String, SourceMappingMetadata> newMap = new CopyOnWriteLinkedHashMap<String, SourceMappingMetadata>();
        for (Entry<String, SourceMappingMetadata> entry : overMap.getMap().entrySet()) {
            newMap.put(entry.getKey(), entry.getValue());
        }

        return newMap;
    }

	/*
     * Helper method for serialization to deal with differences between Teiid 7 and 8
     */
	@SuppressWarnings("nls")
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        GetField serFields = ois.readFields();

        Object serSources = serFields.get("sources", null);
        if (isLinkedHashMap(serSources)) { /* Teiid Version 8+ */
            sources = newCopyOnWriteLinkedHashMap((LinkedHashMap<String, SourceMappingMetadata>) serSources);
        } else if (isListOverMap(serSources)) { /* Teiid Version 7 */
            ListOverMap<SourceMappingMetadata> overMap = (ListOverMap<SourceMappingMetadata>)serSources;
            sources = convertSources(overMap);
        } else
            throw new IllegalStateException();

        modelType = (String) serFields.get("modelType", null);
        description= (String) serFields.get("description", null);
        path = (String) serFields.get("path", null);
        visible = (Boolean) serFields.get("visible", null);

        /* Teiid 8+ */
        messages = (List<Message>) serFields.get("messages", null);
        schemaSourceType = (String) serFields.get("schemaSourceType", null);
        schemaText = (String) serFields.get("schemaText", null);
        metadataStatus = (MetadataStatus) serFields.get("metadataStatus", null);
    }

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	
	@Override
    public boolean isSource() {
		return getModelType() == Model.Type.PHYSICAL;
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public Type getModelType() {
		try {
			return Type.valueOf(modelType);
		} catch(IllegalArgumentException e) {
			return Type.OTHER;
		}
	}
	
    public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}	

	@Override
    public boolean isSupportsMultiSourceBindings() {
		return this.sources.size() > 1 || Boolean.parseBoolean(getPropertyValue(SUPPORTS_MULTI_SOURCE_BINDINGS_KEY)) || Boolean.parseBoolean(getPropertyValue(SUPPORTS_MULTI_SOURCE_BINDINGS_KEY_OLD));
    }    
	
    public void setSupportsMultiSourceBindings(boolean supports) {
        addProperty(SUPPORTS_MULTI_SOURCE_BINDINGS_KEY, Boolean.toString(supports));
    }

    public void setModelType(Model.Type modelType) {
        this.modelType = modelType.name();
    }
    
    public void setModelType(String modelType) {
    	if (modelType != null) {
    		this.modelType = modelType.toUpperCase();
    	} else {
    		this.modelType = null;
    	}
    }    
    
	@Override
	public MetadataStatus getMetadataStatus() {
		return metadataStatus;
	}

    public void setMetadataStatus(Model.MetadataStatus status) {
        this.metadataStatus = status;
    }
    
    void setMetadataStatus(String status) {
    	if (status != null) {
    		this.metadataStatus = Model.MetadataStatus.valueOf(status);
    	}
    }
    
    public String toString() {
		return getName() + this.sources;
    }
    
    public void setVisible(Boolean value) {
    	this.visible = value;
    }    

	public List<SourceMappingMetadata> getSourceMappings(){
		return new ArrayList<SourceMappingMetadata>(this.sources.values());
	}
	
	public Map<String, SourceMappingMetadata> getSources() {
		return sources;
	}
	
	public SourceMappingMetadata getSourceMapping(String sourceName){
		return this.sources.get(sourceName);
	}	
    
	public void setSourceMappings(List<SourceMappingMetadata> sources){
		this.sources.clear();
		for (SourceMappingMetadata source: sources) {
			addSourceMapping(source.getName(), source.getTranslatorName(), source.getConnectionJndiName());
		}
	}      
    
    @Override
    public List<String> getSourceNames() {
    	return new ArrayList<String>(this.sources.keySet());
	}
    
    @Override
    public String getSourceConnectionJndiName(String sourceName) {
    	SourceMappingMetadata s = this.sources.get(sourceName);
    	if (s == null) {
    		return null;
    	}
    	return s.getConnectionJndiName();
	}
    
    @Override
    public String getSourceTranslatorName(String sourceName) {
    	SourceMappingMetadata s = this.sources.get(sourceName);
    	if (s == null) {
    		return null;
    	}
    	return s.getTranslatorName();
	}    
    
	public SourceMappingMetadata addSourceMapping(String name, String translatorName, String connJndiName) {
		return this.sources.put(name, new SourceMappingMetadata(name, translatorName, connJndiName));
	}
	
	public void addSourceMapping(SourceMappingMetadata source) {
		this.addSourceMapping(source.getName(), source.getTranslatorName(), source.getConnectionJndiName());
	}
	
	public synchronized boolean hasErrors() {
		if (this.messages != null) {
			for (Message error : this.messages) {
				if (error.getSeverity() == Message.Severity.ERROR) {
					return true;
				}
			}
		}
		if (this.runtimeMessages != null) {
			for (Message error : this.runtimeMessages) {
				if (error.getSeverity() == Message.Severity.ERROR) {
					return true;
				}
			}
		}
		return false;	
	}
	
	public synchronized List<Message> getMessages(){
		return getMessages(true);
	}	
	
	public synchronized List<Message> getMessages(boolean includeRuntime){
		if (this.messages == null && this.runtimeMessages == null) {
			return Collections.emptyList();
		}
		List<Message> list = new ArrayList<Message>();
		if (this.messages != null) {
			list.addAll(messages);
		}
		if (includeRuntime && this.runtimeMessages != null) {
			list.addAll(runtimeMessages);
		}
		return list;
	}
	
    public Message addMessage(String severity, String message) {
        Message ve = new Message(Message.Severity.valueOf(severity), message);
        addMessage(ve);
        return ve;
    }
    
	public synchronized boolean hasRuntimeMessages(){
		return this.runtimeMessages != null && !this.runtimeMessages.isEmpty();
	}    
    
    public synchronized Message addRuntimeError(String message) {
    	return addRuntimeMessage(Message.Severity.ERROR, message);
    }    
    
    public synchronized Message addRuntimeMessage(Message.Severity severity, String message) {
        Message ve = new Message(severity, message);
        if (this.runtimeMessages == null) {
            this.runtimeMessages = new LinkedList<Message>();
        }
        this.runtimeMessages.add(ve);
        if (this.runtimeMessages.size() > DEFAULT_ERROR_HISTORY) {
        	this.runtimeMessages.remove(0);
        }
        return ve;
    } 
    
    public synchronized Message addMessage(Message ve) {
        if (this.messages == null) {
            this.messages = new LinkedList<Message>();
        }
        this.messages.add(ve);
        return ve;
    }
    
    public synchronized void clearRuntimeMessages() {
		runtimeMessages = null;
    }  
    
    public synchronized void clearMessages() {
    	clearRuntimeMessages();
    	this.messages = null;
    } 
	
    public static class Message implements Serializable{
		private static final long serialVersionUID = 2044197069467559527L;

		public enum Severity {ERROR, WARNING, INFO};
    	
        protected String value;
        protected Severity severity;
        protected String path;
        
		public Message() {};
        
        public Message(Severity severity, String msg) {
        	this.severity = severity;
        	ArgCheck.isNotNull(msg);
        	this.value = msg;
        }
    	
        public String getValue() {
			return value;
		}

		public void setValue(String value) {
			ArgCheck.isNotNull(value);
			this.value = value;
		}

		public Severity getSeverity() {
			return severity;
		}

		public void setSeverity(Severity severity) {
			this.severity = severity;
		}       
		
        public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}		

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Message other = (Message) obj;
			if (severity == null) {
				if (other.severity != null)
					return false;
			} else if (!severity.equals(other.severity))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}		
    }

    public String getSchemaSourceType() {
		return schemaSourceType;
	}

	public void setSchemaSourceType(String schemaSourceType) {
		this.schemaSourceType = schemaSourceType;
	}

	@Override
	public String getSchemaText() {
		return schemaText;
	}

	public void setSchemaText(String schemaText) {
		this.schemaText = schemaText;
	}
	
	@Override
	public List<String> getValidityErrors() {
		List<String> allErrors = new ArrayList<String>();
		List<Message> errors = getMessages();
		if (errors != null && !errors.isEmpty()) {
			for (Message m:errors) {
				if (m.getSeverity() == Message.Severity.ERROR) {
					allErrors.add(m.getValue());
				}
			}
		}
		return allErrors;
	}
	
	
	
}
