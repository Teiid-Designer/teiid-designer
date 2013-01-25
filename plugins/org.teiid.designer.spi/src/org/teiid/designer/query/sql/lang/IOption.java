/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import java.util.Collection;
import org.teiid.designer.query.sql.ILanguageVisitor;

/**
 *
 */
public interface IOption <LV extends ILanguageVisitor> extends ILanguageObject<LV> {

    /**
     * @return
     */
    boolean isNoCache();

    /**
     * @return
     */
    Collection<String> getNoCacheGroups();

    /**
     * @return
     */
    Collection<String> getDependentGroups();

    /**
     * @return
     */
    Collection<String> getNotDependentGroups();

}
