/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function.extension;

import org.teiid.designer.core.extension.AbstractMetaclassNameProvider;
import org.teiid.designer.metamodels.function.FunctionPackage;

/**
 * 
 *
 * @since 8.0
 */
public class FunctionModelExtendableClassnameProvider extends AbstractMetaclassNameProvider {

    /**
     * 
     */
    public FunctionModelExtendableClassnameProvider() {
        super(FunctionPackage.eNS_URI);
    }

}
