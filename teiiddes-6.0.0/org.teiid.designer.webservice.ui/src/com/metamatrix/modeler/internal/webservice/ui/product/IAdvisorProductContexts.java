/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
