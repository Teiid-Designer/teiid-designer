/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.symbol;

import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

/**
 *
 */
@Since(Version.TEIID_8_10)
public interface IXMLCast<LV extends ILanguageVisitor> extends IExpression<LV> {

}
