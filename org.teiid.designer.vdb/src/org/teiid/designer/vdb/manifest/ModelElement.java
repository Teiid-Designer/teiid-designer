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

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class ModelElement extends EntryElement {

    /**
     */
    public static final String BUILT_IN = "builtIn"; //$NON-NLS-1$

    /**
     */
    public static final String INDEX_NAME = "indexName"; //$NON-NLS-1$

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
     * Used by JAXB
     */
    public ModelElement() {
    }

    /**
     * @param entry
     */
    ModelElement( final VdbModelEntry entry ) {
        super(entry);
        // TODO: problems, dependencies
        final String lastSeg = entry.getName().lastSegment();
        final int ndx = lastSeg.lastIndexOf('.');
        name = (ndx < 0 ? lastSeg : lastSeg.substring(0, ndx));
        type = entry.getType().getName();
        visible = entry.isVisible();
        final String source = entry.getDataSource();
        if (source != null) getSources().add(new SourceElement(source));
        getProperties().add(new PropertyElement(BUILT_IN, Boolean.toString(entry.isBuiltIn())));
        getProperties().add(new PropertyElement(INDEX_NAME, entry.getIndexName()));
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
        return type;
    }

    /**
     * @return visible
     */
    public boolean isVisible() {
        return visible;
    }
}
