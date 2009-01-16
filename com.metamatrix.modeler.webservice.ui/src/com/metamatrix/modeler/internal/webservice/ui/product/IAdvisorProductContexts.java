/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.webservice.ui.product;

import com.metamatrix.ui.product.IProductContext;
import com.metamatrix.ui.product.ProductContext;

/** 
 * @since 4.3
 */
public interface IAdvisorProductContexts {
    /**
     * A list of categories used in defining the product contexts. Not visible outside the interface. 
     * @since 4.3
     */
    class Categories {
        /**
         * Advisor Bar Perspective category.
         * @since 4.4
         */
        private static final String WEB_SERVICE_ADVISOR = "webSerivceAdvisor."; //$NON-NLS-1$
    }
    
    /**
     * A collection of {@link IProductContext}s specific to the Web Services Advisor.
     * @since 4.4
     */
    interface WebServiceAdvisor {
        /**
         * The context used to determine if the advisor Dockbar has a close button.
         * @since 4.4
         */
        IProductContext DOCKBAR_CLOSEABLE = new ProductContext(Categories.WEB_SERVICE_ADVISOR, "dockbarCloseable"); //$NON-NLS-1$

        /**
         * The context used to determine if the advisor has tools for selecting a VDB within
         *  the eclipse workspace.
         * @since 4.4
         */
        IProductContext WORKSPACE_VDB_SELECTION = new ProductContext(Categories.WEB_SERVICE_ADVISOR, "workspaceVDBSelection"); //$NON-NLS-1$
    }

//    /**
//     * A collection of values used when checking the {@link IModelerProductContexts.Actions#ACTION} product context. 
//     * @since 4.4
//     */
//    interface ActionValues extends IModelerProductContexts.ActionValues {
//        /**
//         * New VDB action ID.
//         * @since 4.4
//         */
//        String ID_EXECUTE_DEPLOYMENT_WIZARD_ACTION = InternalConstants.ACTION_PREFIX + "executeDeploymentWizard"; //$NON-NLS-1$
//    }

}
