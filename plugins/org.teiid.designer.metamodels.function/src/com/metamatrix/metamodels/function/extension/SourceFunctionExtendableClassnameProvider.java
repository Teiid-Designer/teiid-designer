/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function.extension;

import org.teiid.designer.core.extension.AbstractMetaclassNameProvider;
import com.metamatrix.metamodels.function.FunctionPackage;

/**
 * 
 */
public class SourceFunctionExtendableClassnameProvider extends AbstractMetaclassNameProvider {

    public SourceFunctionExtendableClassnameProvider() {
        super(FunctionPackage.eNS_URI);
    }

}
