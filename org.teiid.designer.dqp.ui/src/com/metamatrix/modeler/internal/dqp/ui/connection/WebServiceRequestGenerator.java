/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.connection;

import java.util.Collections;
import net.sourceforge.sqlexplorer.ext.IRequestDocumentGenerator;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.webservice.util.WebServiceUtil;

/**
 * This class generates template request documents for web service operations.
 * 
 * @since 5.0.1
 */
public class WebServiceRequestGenerator implements
                                       IRequestDocumentGenerator {

    // ===========================================================================================================================
    // Methods

    /**
     * @see net.sourceforge.sqlexplorer.ext.IRequestDocumentGenerator#generateRequestDocument(java.lang.String)
     * @since 5.0.1
     */
    public String generateRequestDocument(String webServiceModelUUID,
                                          String webServiceOperationUUID) {
        try {
            // ensure the model is loaded
            ObjectID modelID = IDGenerator.getInstance().stringToObject(webServiceModelUUID);
            Resource res = ModelerCore.getModelContainer().getResourceFinder().findByUUID(modelID, false);
            if (!res.isLoaded()) {
                res.load(Collections.EMPTY_MAP);
            }
            // find the object
            Object obj = ModelerCore.getModelContainer().getEObjectFinder().find(webServiceOperationUUID);
            if (obj instanceof Operation) {
                return WebServiceUtil.generateRequestDocument((Operation)obj, null);
            }
        } catch (Exception e) {
            DqpUiConstants.UTIL.log(e);
        }

        return null;
    }
}
