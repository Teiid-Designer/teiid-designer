/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.extension;

import java.util.Set;
import org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider;

/**
 * Provides extendable metaclass name provider based on model types.
 */
public interface ModelTypeMetaclassNameFactory {

    /**
     * @param modelTypes the model types that pertain to the provider being requested (can be <code>null</code> or empty)
     * @return the provider (never <code>null</code>)
     */
    ExtendableMetaclassNameProvider getProvider(final Set<String> modelTypes);

}
