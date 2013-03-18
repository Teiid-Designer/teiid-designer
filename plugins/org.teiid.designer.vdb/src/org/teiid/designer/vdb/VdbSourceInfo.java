/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb;

import static org.teiid.designer.vdb.Vdb.Event.MODEL_SOURCES;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.vdb.manifest.SourceElement;

/**
 *
 */
public class VdbSourceInfo {
	// Reference info from Teiid documentation
	/*
	 * The following is an example of vdb.xml model entry that supports multi-source bindings
	 <vdb name="vdbname" version="1">
	    <model visible="true" type="PHYSICAL" name="Customers" path="/Test/Customers.xmi">
	        <property name="supports-multi-source-bindings" value="true"/>
	        <!-- optional properties
	        <property name="multisource.columnName" value="source_name_alias"/>
	        <property name="multisource.addColumn" value="true"/>
	        -->
	        <source name="chicago" 	translator-name="oracle" connection-jndi-name="chicago-customers"/>
	        <source name="newyork"	translator-name="oracle" connection-jndi-name="newyork-customers"/>
	        <source name="la" 		translator-name="oracle" connection-jndi-name="la-customers"/>
	    </model>
	</vdb>
	 */
	
	private Vdb vdb;
	
    private Map<String, VdbSource> sources;
    
    private boolean supportsMultiSourceBindings;
    
    private boolean addColumn;
    
    private String columnAlias;
    
    /**
     * @param vdb 
     * 
     */
    public VdbSourceInfo(final Vdb vdb) {
    	super();
    	this.vdb = vdb;
    	this.sources = new HashMap<String, VdbSource>();
    }

	/**
	 * @param vdb
	 * @param sources
	 */
	public VdbSourceInfo(final Vdb vdb, final Collection<SourceElement> sources) {
    	 this(vdb);
    	 
    	 for( SourceElement element : sources ) {
    		 sources.add(element);
    	 }
	}

	/**
	 * @return the vdb
	 */
	public Vdb getVdb() {
		return vdb;
	}
	
    /**
	 * @return the sources
	 */
	public Collection<VdbSource> getSources() {
		return this.sources.values();
	}
	
	/**
	 * @param name
	 * @param jndiName
	 * @param translatorName
	 * @return true if new VdbSource added
	 */
	public boolean add(String name, String jndiName, String translatorName) {
		if(this.sources.get(name) == null ) {
			this.sources.put(name, new VdbSource(getVdb(), name, jndiName, translatorName));
			getVdb().setModified(this, MODEL_SOURCES, null, this.sources.get(name));
			return true;
		}
		
		return false;
	}
	
	/**
	 * @param name
	 * @return true if new VdbSource removed
	 */
	public boolean remove(String name) {
		CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
		
		if(this.sources.get(name) != null ) {
			VdbSource removedSource = this.sources.get(name);
			this.sources.remove(name);
			getVdb().setModified(this, MODEL_SOURCES, removedSource, null);
			return true;
		}
		
		return false;
	}
	
	/**
	 * @param source
	 * @return true if new VdbSource removed
	 */
	public boolean removeSource(VdbSource source) {
		CoreArgCheck.isNotNull(source, "source"); //$NON-NLS-1$
		
		if(this.sources.get(source.getName()) != null ) {
			this.sources.remove(source.getName());
			getVdb().setModified(this, MODEL_SOURCES, source, null);
			return true;
		}
		
		return false;
	}

	/**
	 * @param name
	 * @return the VdbSource instance if it exists in the sources map
	 */
	public VdbSource getSource(String name) {
		CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
		
		if( !sources.isEmpty() ) {
			return sources.values().iterator().next();
		}
		return this.sources.get(name);
	}
	
	/**
	 * @return the VdbSource instance if it exists in the sources map
	 */
	public VdbSource getSource() {
		// TODO: check for Multi-source
		if(!sources.isEmpty() ) {
			return sources.values().iterator().next();
		}
		
		return null;
	}
	
	/**
	 * @param index the source array index
	 * @return the VdbSource instance if it exists in the sources map
	 */
	public VdbSource getSource(int index) {
		// TODO: check for Multi-source
		if(!sources.isEmpty() && sources.size() < index ) {
			for( int i=0; i<sources.size(); i++ ) {
				VdbSource source = sources.values().iterator().next();
				if( i == index ) return source;
			}
		}
		
		return null;
	}
	
	/**
	 * @return the size of the sources collection
	 */
	public int getSourceCount() {
		return sources.size();
	}
	
	/**
	 * @return the size of the sources collection
	 */
	public boolean isEmpty() {
		return sources.isEmpty();
	}
	
	/**
	 * @return if number of sources for model is 
	 */
	public boolean isMultiSource() {
		return this.supportsMultiSourceBindings;
	}
	
	/**
	 * @param value boolean supports multi-source binding value 
	 */
	public void setIsMultiSource(boolean value) {
		if( value != this.supportsMultiSourceBindings ) {
			this.supportsMultiSourceBindings = value;
			getVdb().setModified(this, MODEL_SOURCES, !value, value);
		}
	}

    /**
	 * @return the columnAlias
	 */
	public String getColumnAlias() {
		return this.columnAlias;
	}

	/**
	 * @param columnAlias the columnAlias to set
	 */
	public void setColumnAlias(String columnAlias) {
		if( !StringUtilities.equals(this.columnAlias, columnAlias)) {
			this.columnAlias = columnAlias;
			getVdb().setModified(this, MODEL_SOURCES, this.columnAlias, columnAlias);
		}
	}

	/**
	 * @return the addColumn
	 */
	public boolean isAddColumn() {
		return this.addColumn;
	}

	/**
	 * @param addColumn the addColumn to set
	 */
	public void setAddColumn(boolean addColumn) {
		if( addColumn != this.addColumn) {
			this.addColumn = addColumn;
			getVdb().setModified(this, MODEL_SOURCES, !addColumn, addColumn);
		}
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("VdbSourceInfo : "); //$NON-NLS-1$
        text.append("\tvdb name =").append(getVdb().getName()); //$NON-NLS-1$
        text.append("\n\tsource count =").append(getSourceCount()); //$NON-NLS-1$
        text.append("\n\tis multi-source = ").append(isMultiSource()); //$NON-NLS-1$
        text.append("\n\tis add column = ").append(isAddColumn()); //$NON-NLS-1$
        text.append("\n\tcolumn alias =").append(getColumnAlias()); //$NON-NLS-1$

        return text.toString();
	}
}
