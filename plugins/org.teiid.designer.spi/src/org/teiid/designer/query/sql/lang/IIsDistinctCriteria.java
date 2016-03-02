/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import org.teiid.designer.query.sql.ILanguageVisitor;

/**
 * @param <LV>
 */
public interface IIsDistinctCriteria<LV extends ILanguageVisitor> extends IPredicateCriteria<LV> {

    // Tagging interface
}
