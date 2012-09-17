/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.metamodels.relational.extension;

import org.teiid.designer.core.extension.AbstractMetaclassNameProvider;
import org.teiid.designer.metamodels.core.CorePackage;

/**
 *
 */
public class CoreExtendableClassnameProvider extends AbstractMetaclassNameProvider {

    /**
     * 
     */
    public CoreExtendableClassnameProvider() {
        super(CorePackage.eNS_URI);
    }
}
