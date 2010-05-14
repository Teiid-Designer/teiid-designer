/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlEnum( value = String.class )
public enum Severity {

    /**
     */
    @XmlEnumValue( "ERROR" )
    ERROR,

    /**
     */
    @XmlEnumValue( "WARNING" )
    WARNING;
}
