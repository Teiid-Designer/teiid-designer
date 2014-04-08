/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql;

import java.util.Collection;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;

/**
 *
 */
public interface IResolverVisitor<LO extends ILanguageObject, GS extends IGroupSymbol> {
    
    public static final String SHORT_NAME = "shortName"; //$NON-NLS-1$

    @Deprecated
    void setProperty(String propertyName, Object value);

    void resolveLanguageObject(LO obj, IQueryMetadataInterface metadata) throws Exception;

    void resolveLanguageObject(LO obj, Collection<GS> groups, IQueryMetadataInterface metadata) throws Exception;
}
