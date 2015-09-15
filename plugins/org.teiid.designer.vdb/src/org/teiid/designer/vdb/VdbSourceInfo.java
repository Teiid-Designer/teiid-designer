/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb;

import static org.teiid.designer.vdb.Vdb.Event.MODEL_SOURCES;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringUtilities;

/**
 *
 */
public class VdbSourceInfo {
	
	/**
	 * 
	 */
	public static final String DEFAULT_SOURCE_NAME = "default"; //$NON-NLS-1$
	
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
	
    private List<VdbSource> sources;
    
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
    	this.sources = new ArrayList<VdbSource>();
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
		return this.sources;
	}
	
	/**
	 * @param name
	 * @param jndiName
	 * @param translatorName
	 * @return true if new VdbSource added
	 */
	public boolean add(String name, String jndiName, String translatorName) {
		if( getSource(name) != null )
		    return false;

		//
		// Avoid duplicate sources named differently but
		// with the same jndi and translator properties
		//
		for (VdbSource source : getSources()) {
		    if (source.getJndiName().equals(jndiName) && source.getTranslatorName().equals(translatorName))
		        return false;
		}
		
		VdbSource vdbSource = new VdbSource(getVdb(), name, jndiName, translatorName);
		this.sources.add(vdbSource);
		getVdb().setModified(this, MODEL_SOURCES, null, vdbSource);
		return true;
	}
	
	/**
	 * @param source
	 * @return true if new VdbSource removed
	 */
	public boolean removeSource(VdbSource source) {
		CoreArgCheck.isNotNull(source, "source"); //$NON-NLS-1$
		
		Iterator<VdbSource> iter = this.sources.iterator();
		while(iter.hasNext()) {
			VdbSource theSource = (VdbSource)iter.next();
			if(theSource.getName().equalsIgnoreCase(source.getName())) {
				iter.remove();
				getVdb().setModified(this, MODEL_SOURCES, theSource, null);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get the source with the specified name.  If not in the source list, returns null
	 * @param name the source name
	 * @return the VdbSource
	 */
	private VdbSource getSource(String name) {
		VdbSource result = null;
		for(VdbSource source : this.sources) {
			if(source.getName().equalsIgnoreCase(name)) {
				result = source;
				break;
			}
		}
		return result;
	}

	/**
	 * @param index the source array index
	 * @return the VdbSource instance if it exists in the sources map
	 */
	public VdbSource getSource(int index) {
		if( sources.isEmpty() ) {
			add(DEFAULT_SOURCE_NAME, null, null);
		}
		if(index>=0 && index<sources.size()  ) {
			VdbSource source = sources.get(index);
			return source;
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
