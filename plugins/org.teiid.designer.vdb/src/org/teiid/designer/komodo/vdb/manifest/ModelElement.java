/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb.manifest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.teiid.designer.komodo.vdb.Model;
import org.teiid.designer.komodo.vdb.ModelSource;
import org.teiid.designer.komodo.vdb.VdbImport;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbIndexedEntry.Problem;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbSource;
import org.teiid.designer.vdb.VdbUtil;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class ModelElement {

    /**
     */
    public static final String IMPORTS = "imports"; //$NON-NLS-1$
    
    /**
     */
    public static final String IMPORT_VDB_REFERENCE = "import-vdb-reference"; //$NON-NLS-1$
    
    /**
     * 
     */
    public static final String SUPPORTS_MULTI_SOURCE = "supports-multi-source-bindings"; //$NON-NLS-1$
    
    /**
     * 
     */
    public static final String MULTI_SOURCE_ADD_COLUMN = "multisource.addColumn"; //$NON-NLS-1$
    
    /**
     * 
     */
    public static final String MULTI_SOURCE_COLUMN_ALIAS = "multisource.columnName"; //$NON-NLS-1$

    private static final long serialVersionUID = 1L;

    @XmlAttribute( name = "name", required = true )
    private String name;

    @XmlAttribute( name = "type", required = true )
    private String type = "PHYSICAL"; //$NON-NLS-1$

    @XmlAttribute( name = "visible", required = true )
    private boolean visible = true;

    @XmlAttribute( name = "path" )
    private String path;

    @XmlElement( name = "description" )
    private String description;

    @XmlElement( name = "property", type = PropertyElement.class )
    private List<PropertyElement> properties;

    @XmlElement( name = "source", type = SourceElement.class )
    private List<SourceElement> sources;
    
    @XmlElement( name = "metadata", type = MetadataElement.class )
    private List<MetadataElement> metadata;

    @XmlElement( name = "validation-error", type = ProblemElement.class )
    private List<ProblemElement> problems;

    /**
     * Used by JAXB when loading a VDB
     */
    public ModelElement() {
    }

    /**
     * Used to save a model entry
     * 
     * @param entry
     * @throws Exception
     */
    ModelElement( final Model model ) throws Exception {
        super();
//        final String lastSeg = entry.getName().lastSegment();
//        final int ndx = lastSeg.lastIndexOf('.');
//        name = (ndx < 0 ? lastSeg : lastSeg.substring(0, ndx));
        name = model.getName();
        type = model.getModelType().toString();
        visible = model.isVisible();
        
        path = model.getName().toString();
        if( model.getDescription() != null && !model.getDescription().isEmpty() ) {
        	description = model.getDescription();
        }
        
        boolean isMultiSource = model.getSources().length > 1;
        for( ModelSource source : model.getSources() ) {
        	getSources().add(new SourceElement(source));
        }

        final List<PropertyElement> props = getProperties();


        if( isMultiSource ) {
        	props.add(new PropertyElement(SUPPORTS_MULTI_SOURCE, Boolean.toString(true)));
//        	if( entry.getSourceInfo().isAddColumn() ) {
//        		props.add(new PropertyElement(MULTI_SOURCE_ADD_COLUMN, Boolean.toString(true)));
//        	}
//        	String alias = entry.getSourceInfo().getColumnAlias();
//        	if( alias != null && alias.length() > 0 ) {
//        		props.add(new PropertyElement(MULTI_SOURCE_COLUMN_ALIAS, alias));
//        	}
        }

        
        if( model.getMetadata() != null && model.getMetadata().getSchemaText() != null ) {
        	getMetadata().add(new MetadataElement(
        			model.getMetadata().getSchemaText(), model.getMetadata().getType().name()));
        }
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return problems
     */
    public List<ProblemElement> getProblems() {
        if (problems == null) problems = new ArrayList<ProblemElement>();
        return problems;
    }

    /**
     * @return connectors
     */
    public List<SourceElement> getSources() {
        if (sources == null) sources = new ArrayList<SourceElement>();
        return sources;
    }
    
    /**
     * @return connectors
     */
    public List<MetadataElement> getMetadata() {
        if (metadata == null) metadata = new ArrayList<MetadataElement>();
        return metadata;
    }

    /**
     * @return type
     */
    public String getType() {
    	if( VdbUtil.DEPRECATED_TYPE.equalsIgnoreCase(type.toUpperCase())) {
        	return VdbUtil.OTHER;
    	}
        return type;
    }

    /**
     * @return visible
     */
    public boolean isVisible() {
        return visible;
    }
    
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("PropertyElement : "); //$NON-NLS-1$
        text.append("\n\t name  = ").append(getName()); //$NON-NLS-1$
        text.append("\n\t type = ").append(getType()); //$NON-NLS-1$
        text.append("\n\t visibility = ").append(isVisible()); //$NON-NLS-1$

        return text.toString();
	}
	

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return The list of properties for this entry; never <code>null</code>
     */
    public List<PropertyElement> getProperties() {
        if (properties == null) properties = new ArrayList<PropertyElement>();
        return properties;
    }
}
