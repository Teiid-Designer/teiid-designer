/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.lang;

import org.teiid.designer.annotation.Removed;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.symbol.Expression;

/**
 * Interface only applicable to the Teiid 7 parser
 */
@Removed(Version.TEIID_8_0)
public interface SingleElementSymbol extends Expression {

}
