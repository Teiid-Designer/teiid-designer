/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.product;

/**
 * Collection of Modeler product contexts used to determine if features are supported by the current
 * application.
 * @since 4.4
 */
public interface IProductContexts {
    
    /**
     * A list of categories used in defining the product contexts. Not visible outside the interface. 
     * @since 4.3
     */
    class Categories {
        /**
         * Product context category.
         * @since 4.4
         */
        private static final String PRODUCT = "product."; //$NON-NLS-1$
        
        /**
         * Window context category.
         * @since 4.4
         */
        private static final String WINDOW = "window."; //$NON-NLS-1$
        
        /**
         * Workbench context category.
         * @since 4.4
         */
        private static final String WORKBENCH = "workbench."; //$NON-NLS-1$
    }
    
    interface Product {
        /**
         * This context can be used to determine if a product is an IDE Application.
         * @since 4.4
         */
        IProductContext IDE_APPLICATION = new ProductContext(Categories.PRODUCT, "ideApplication"); //$NON-NLS-1$
    }
    
    /**
     * Collection of {@link IProductContext}s supporting the application window-related concepts.
     * @since 4.4
     */
    interface Window {
        /**
         * The context used to determine if the window toolbar is supported by a product.
         * @since 4.4
         */
        IProductContext COOL_BAR = new ProductContext(Categories.WINDOW, "coolBar"); //$NON-NLS-1$
        
        /**
         * The context used to determine if the window fast view bars is supported by a product.
         * @since 4.4
         */
        IProductContext FAST_VIEW_BARS = new ProductContext(Categories.WINDOW, "fastViewBars"); //$NON-NLS-1$
        
        /**
         * The context used to determine if the window menu bar is supported by a product.
         * @since 4.4
         */
        IProductContext MENU_BAR = new ProductContext(Categories.WINDOW, "menuBar"); //$NON-NLS-1$
    
        /**
         * The context used to determine if the window perspective bar is supported by a product.
         * @since 4.4
         */
        IProductContext PERSPECTIVE_BAR = new ProductContext(Categories.WINDOW, "perspectiveBar"); //$NON-NLS-1$
    
        /**
         * The context used to determine if the window progress bar is supported by a product.
         * @since 4.4
         */
        IProductContext PROGRESS_BAR = new ProductContext(Categories.WINDOW, "progressBar"); //$NON-NLS-1$
    
        /**
         * The context used to determine if the window status bar is supported by a product.
         * @since 4.4
         */
        IProductContext STATUS_BAR = new ProductContext(Categories.WINDOW, "statusBar"); //$NON-NLS-1$

        /**
         * The context used to determine if a dock bar is supported by a product.
         * @since 4.4
         */
        IProductContext DOCK_BAR = new ProductContext(Categories.WINDOW, "statusBar"); //$NON-NLS-1$
    }
    
    /**
     * Collection of {@link IProductContext}s supporting workbench-related concepts.
     * @since 4.4
     */
    interface Workbench {
        /**
         * The context used to determine if a view shortcut should be added to a perspective is supported by a product. The view
         * identifier should be used as the value when checking product support.
         * @since 4.4
         */
        IProductContext PERSPECTIVE_VIEW_SHORTCUT = new ProductContext(Categories.WORKBENCH, "perspectiveViewShortcut"); //$NON-NLS-1$

        /**
         * The context used to determine if workbench state should be saved and restored. This includes perspective state,
         * editor state, navigtor state, etc., and works with <code>IMemento</code>s.
         * @since 4.4
         */
        IProductContext SAVE_AND_RESTORE = new ProductContext(Categories.WORKBENCH, "saveAndRestore"); //$NON-NLS-1$

        /**
         * The context used to determine if an Eclipse view is supported by a product. The view identifier should be used as
         * the value when checking product support.
         * @since 4.4
         */
        IProductContext VIEW = new ProductContext(Categories.WORKBENCH, "view"); //$NON-NLS-1$
    }
    
}
