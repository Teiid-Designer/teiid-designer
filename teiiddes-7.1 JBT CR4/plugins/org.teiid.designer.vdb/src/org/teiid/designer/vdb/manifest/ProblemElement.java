/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.manifest;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.eclipse.core.resources.IMarker;
import org.teiid.designer.vdb.VdbModelEntry.Problem;
import com.metamatrix.core.modeler.CoreModelerPlugin;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class ProblemElement implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute( name = "severity", required = true )
    private Severity severity;

    @XmlValue
    private String message;

    @XmlAttribute( name = "path" )
    private String location;

    /**
     * Used by JAXB
     */
    public ProblemElement() {
    }

    ProblemElement( final Problem problem ) {
        try {
            severity = (problem.getSeverity() == IMarker.SEVERITY_ERROR ? Severity.ERROR : Severity.WARNING);
            message = problem.getMessage();
            location = problem.getLocation();
        } catch (final Exception error) {
            throw CoreModelerPlugin.toRuntimeException(error);
        }
    }

    /**
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return severity
     */
    public Severity getSeverity() {
        return severity;
    }
}
