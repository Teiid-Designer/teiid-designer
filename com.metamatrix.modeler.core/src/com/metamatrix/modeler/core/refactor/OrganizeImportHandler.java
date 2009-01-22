/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.util.List;

/**
 * This interface represents a handler that a 
 * {@link com.metamatrix.modeler.core.refactor.OrganizeImportCommand OrganizeImportCommand} uses
 * when there are ambiguous choices.
 */
public interface OrganizeImportHandler {

    /**
     * Request for this handler to identify which, if any, of the supplied choices should be used.
     * @param options the list of choices; never null
     * @return the choice, which must be one of the objects in the <code>options</code> list;
     * may be null if no choice should be made
     */
    public Object choose( final List options );

}
