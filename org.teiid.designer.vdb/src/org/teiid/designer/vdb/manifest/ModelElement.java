/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.manifest;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.core.runtime.IPath;
import com.metamatrix.metamodels.core.ModelType;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class ModelElement extends EntryElement {

    private static final long serialVersionUID = 1L;

    @XmlAttribute( name = "name", required = true )
    private String name;

    @XmlAttribute( name = "type", required = true )
    private String type = "PHYSICAL"; //$NON-NLS-1$

    @XmlAttribute( name = "visible", required = true )
    private boolean visible = true;

    @XmlElement( name = "source", type = ConnectorElement.class )
    private List<ConnectorElement> connectors;

    @XmlElement( name = "validation-error", type = ProblemElement.class )
    private List<ProblemElement> problems;

    /**
     * Used by JAXB
     */
    public ModelElement() {
    }

    /**
     * @param name
     * @param description
     * @param type
     * @param visible
     */
    public ModelElement( final IPath name,
                         final String description,
                         final ModelType type,
                         final boolean visible ) {
        super(name, description);
        final String lastSeg = name.lastSegment();
        final int ndx = lastSeg.lastIndexOf('.');
        this.name = (ndx < 0 ? lastSeg : lastSeg.substring(0, ndx));
        this.type = type.getName();
        this.visible = visible;
    }

    /**
     * @return connectors
     */
    public List<ConnectorElement> getConnectors() {
        return connectors;
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
        return problems;
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
