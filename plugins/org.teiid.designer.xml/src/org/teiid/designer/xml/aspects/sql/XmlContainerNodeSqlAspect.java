/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.aspects.sql;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;

/**
 * XmlContainerNodeSqlAspect
 */
public abstract class XmlContainerNodeSqlAspect extends AbstractXmlDocumentEntitySqlAspect {

    /**
     * Construct an instance of XmlContainerNodeSqlAspect.
     */
    public XmlContainerNodeSqlAspect( final MetamodelEntity entity ) {
        super(entity);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType( final char recordType ) {
        // No records should ever be made of these!
        return false;
    }

}
