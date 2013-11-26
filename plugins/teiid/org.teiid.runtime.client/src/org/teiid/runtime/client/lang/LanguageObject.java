/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.lang;

import org.teiid.runtime.client.lang.parser.SQLParser;
import org.teiid.runtime.client.lang.parser.SimpleNode;

/**
 *
 */
public abstract class LanguageObject extends SimpleNode {

    /**
     * @param i
     */
    public LanguageObject(int i) {
        super(i);
    }

    /**
     * @param p
     * @param i
     */
    public LanguageObject(SQLParser p, int i) {
        super(p, i);
      }
}
