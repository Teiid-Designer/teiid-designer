/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.manifest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbIndexedEntry.Problem;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbSource;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.dynamic.DynamicModel;
import org.teiid.designer.vdb.manifest.adapters.XmlVdbAdapters;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class ModelElement extends EntryElement {

	/**
	 * model class property key
     */
    public static final String MODEL_UUID = "modelUuid"; //$NON-NLS-1$
    
	/**
	 * model class property key
     */
    public static final String MODEL_CLASS = "modelClass"; //$NON-NLS-1$
    
    /**
     */
    public static final String BUILT_IN = "builtIn"; //$NON-NLS-1$

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

    @XmlAttribute( name = "type", required = false )
    @XmlJavaTypeAdapter (XmlVdbAdapters.ModelTypeAttributeAdapter.class)
    private String type = "PHYSICAL"; //$NON-NLS-1$

    @XmlAttribute( name = "visible", required = false )
    @XmlJavaTypeAdapter (XmlVdbAdapters.VisibleAttributeAdapter.class)
    private Boolean visible = true;

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
    ModelElement( final VdbModelEntry entry ) throws Exception {
        super(entry);
        final String lastSeg = entry.getPath().lastSegment();
        final int ndx = lastSeg.lastIndexOf('.');
        name = (ndx < 0 ? lastSeg : lastSeg.substring(0, ndx));
        type = entry.getType();
        visible = entry.isVisible();
        for( VdbSource source : entry.getSourceInfo().getSources() ) {
        	getSources().add(new SourceElement(source));
        }
        for (final Problem problem : entry.getProblems())
            getProblems().add(new ProblemElement(problem));
        final List<PropertyElement> props = getProperties();
        String modelUuid = entry.getModelUuid();
        if( modelUuid != null ) {
        	props.add(new PropertyElement(MODEL_UUID, entry.getModelUuid()));
        }
        String modelClass = entry.getModelClass();
        if( modelClass != null ) {
        	props.add(new PropertyElement(MODEL_CLASS, entry.getModelClass()));
        }
        props.add(new PropertyElement(BUILT_IN, Boolean.toString(entry.isBuiltIn())));
        if( entry.getSourceInfo().isMultiSource() ) {
        	props.add(new PropertyElement(SUPPORTS_MULTI_SOURCE, Boolean.toString(true)));
        	if( entry.getSourceInfo().isAddColumn() ) {
        		props.add(new PropertyElement(MULTI_SOURCE_ADD_COLUMN, Boolean.toString(true)));
        	}
        	String alias = entry.getSourceInfo().getColumnAlias();
        	if( alias != null && alias.length() > 0 ) {
        		props.add(new PropertyElement(MULTI_SOURCE_COLUMN_ALIAS, alias));
        	}
        }
        for (final VdbEntry importedEntry : entry.getImports())
            props.add(new PropertyElement(IMPORTS, importedEntry.getName().toString()));
        for (final String importedVdbName : entry.getImportVdbNames())
            props.add(new PropertyElement(IMPORT_VDB_REFERENCE, importedVdbName));
        
        if( entry.getSchemaText() != null ) {
        	getMetadata().add(new MetadataElement(entry.getSchemaText(), entry.getType()));
        }
    }

    /**
     * Used to save a model entry
     * 
     * @param model
     * @throws Exception
     */
    ModelElement( final DynamicModel model ) throws Exception {
        super();
        name = model.getName();
        type = model.getModelType().toString();
        visible = model.isVisible();
        path = null; // Not used by dynamic vdbs

        if( model.getDescription() != null && !model.getDescription().isEmpty() ) {
            description = model.getDescription();
        }

        for( VdbSource source : model.getSources() ) {
            getSources().add(new SourceElement(source));
        }

        final List<PropertyElement> props = getProperties();

        if (model.isMultiSource())
            props.add(new PropertyElement(SUPPORTS_MULTI_SOURCE, Boolean.toString(model.isMultiSource())));

        if (model.doAddColumn())
            props.add(new PropertyElement(MULTI_SOURCE_ADD_COLUMN, Boolean.toString(model.doAddColumn())));

        String alias = model.getColumnAlias();
        if( alias != null && alias.length() > 0 )
            props.add(new PropertyElement(MULTI_SOURCE_COLUMN_ALIAS, alias));

        for (Map.Entry<Object, Object> entry : model.getProperties().entrySet()) {
            props.add(new PropertyElement(entry.getKey().toString(), entry.getValue().toString()));
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
}
