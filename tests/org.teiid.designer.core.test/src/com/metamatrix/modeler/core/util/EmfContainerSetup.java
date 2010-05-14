/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * This class only runs within the plug-in environment and is called reflectively by the {@link EmfTestSetup} class.
 * @since 3.1
 * @version 3.1
 * @author <a href="mailto:jverhaeg@metamatrix.com">John P. A. Verhaeg</a>
 */
public final class EmfContainerSetup {
    //############################################################################################################################
	//# Instance Methods                                                                                                         #
	//############################################################################################################################

    /**
     * Creates the list of containers named by the keys within the specified map, marking the one, if any, that matches the
     * specified primary container as such.  The values for each map entry contain a list of model names that are then added to
     * each respective container.
     * @param primaryContainer The name of the primary container, if any.  May be null.
     * @since 3.1
     */
    public void containerSetUp(final Map containers, final String primaryContainer) {
        final Set set = containers.entrySet();
        for (final Iterator ctnrIter = set.iterator();  ctnrIter.hasNext();) {
            final Entry entry = (Entry)ctnrIter.next();
            final String ctnr = (String)entry.getKey();
            EmfContainerUtils.createContainer(ctnr, ctnr.equals(primaryContainer));
            final List models = (List)entry.getValue();
            for (final Iterator modelIter = models.iterator();  modelIter.hasNext();) {
                EmfContainerUtils.addModel((String)modelIter.next(), ctnr);
            }
        }
    } 

    /**
     * Shutsdown the containers named within the specified collection.
     * @since 3.1
     */
    public void containerTearDown(final Collection containers) {
        for (final Iterator iter = containers.iterator();  iter.hasNext();) {
            EmfContainerUtils.shutdownContainer((String)iter.next());
        }
    } 
}
