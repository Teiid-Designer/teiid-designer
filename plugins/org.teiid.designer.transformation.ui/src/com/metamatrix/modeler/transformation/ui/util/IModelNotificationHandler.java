/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.util;

import java.util.Collection;

import com.metamatrix.modeler.ui.event.ModelResourceEvent;

/**
 * Interface to expose methods for handling model object change notifications
 */
public interface IModelNotificationHandler {
    public static final boolean NOT_SIGNIFICANT = false;
    public static final boolean IS_UNDOABLE = true;
    public static final boolean NOT_UNDOABLE = false;
    
    /**
     * Handle {@link Collection} of notifications and a transaction source
     * 
     * @param notifications
     * @param txnSource
     */
	void handleNotifications( Collection notifications, Object txnSource);
	
	/**
	 * Allows processing a {@link ModelResourceEvent}
	 * 
	 * In the case of handling events for transformations, the processing might invalidate cached SQL Status objects and
	 * force a rebuild/revalidation
	 * 
	 * @param event the model resource event
	 */
	void processModelResourceEvent(ModelResourceEvent event);
	
	/**
	 * Simple method which returns whether or not a changed object needs to be pre-filtered
	 * 
	 * @param object
	 * @return tru of object should be handled
	 */
	boolean shouldHandleChangedObject(Object object);
}
