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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.VdbModelEntry.Problem;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class ModelElement extends EntryElement {

	/**
	 * model class property key
     */
    public static final String MODEL_CLASS = "modelClass"; //$NON-NLS-1$
    
    /**
     */
    public static final String BUILT_IN = "builtIn"; //$NON-NLS-1$

    /**
     */
    public static final String INDEX_NAME = "indexName"; //$NON-NLS-1$

    /**
     */
    public static final String IMPORTS = "imports"; //$NON-NLS-1$

    private static final long serialVersionUID = 1L;

    @XmlAttribute( name = "name", required = true )
    private String name;

    @XmlAttribute( name = "type", required = true )
    private String type = "PHYSICAL"; //$NON-NLS-1$

    @XmlAttribute( name = "visible", required = true )
    private boolean visible = true;

    @XmlElement( name = "source", type = SourceElement.class )
    private List<SourceElement> sources;

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
     */
    ModelElement( final VdbModelEntry entry ) {
        super(entry);
        final String lastSeg = entry.getName().lastSegment();
        final int ndx = lastSeg.lastIndexOf('.');
        name = (ndx < 0 ? lastSeg : lastSeg.substring(0, ndx));
        type = entry.getType();
        visible = entry.isVisible();
        if (entry.getSourceName() != null) getSources().add(new SourceElement(entry));
        for (final Problem problem : entry.getProblems())
            getProblems().add(new ProblemElement(problem));
        final List<PropertyElement> props = getProperties();
        String modelClass = entry.getModelClass();
        if( modelClass != null ) {
        	props.add(new PropertyElement(MODEL_CLASS, entry.getModelClass()));
        }
        props.add(new PropertyElement(BUILT_IN, Boolean.toString(entry.isBuiltIn())));
        props.add(new PropertyElement(INDEX_NAME, entry.getIndexName()));
        for (final VdbModelEntry importedEntry : entry.getImports())
            props.add(new PropertyElement(IMPORTS, importedEntry.getName().toString()));
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
}
