/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import com.metamatrix.modeler.internal.ui.util.IModelerPerspectiveContributor;
import com.metamatrix.modeler.internal.ui.util.PerspectiveObject;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiConstants.Extensions;
import com.metamatrix.modeler.ui.UiConstants.ExtensionPoints.ModelerPerspectiveContributorExtension;

/**
 * ModelerPerspectiveFactory
 */
public class ModelerPerspectiveFactory
implements Extensions, IPerspectiveFactory, ModelerPerspectiveContributorExtension {

    /** Array of all extensions to the ModelerPerspectiveContributor extension point */
    private static List<IModelerPerspectiveContributor> contributors;
    private static PerspectiveObject[] contributedPOs;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs a new factory.
     */
    public ModelerPerspectiveFactory() {
        super();
        loadPerpectiveExtensions();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Adds a view to a folder. Also addes a menu item into Show->View.
     * @param theViewId the identifier of the view being added
     * @param theFolder the folder where the view is added
     * @param thePage the page where the menu item is added
     */
    private void addView(String theViewId,
                         IFolderLayout theFolder,
                         IPageLayout thePage) {
        theFolder.addView(theViewId);
        thePage.addShowViewShortcut(theViewId);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
     */
    public void createInitialLayout(IPageLayout theLayout) {
        theLayout.addPerspectiveShortcut(PERSPECTIVE);

        String editorArea = theLayout.getEditorArea();

        //
        // Create tree folder (left) - PackageExplorer, ModelExplorer
        //
        IFolderLayout topLeftFolder = theLayout.createFolder(TREE_FOLDER,
                                                         IPageLayout.LEFT,
                                                         (float)0.30,
                                                         editorArea);
        addView(Explorer.VIEW, topLeftFolder, theLayout);
        addView(OUTLINE_VIEW, topLeftFolder, theLayout);
        topLeftFolder.addPlaceholder(DATATYPE_HIERARCHY_VIEW);
        topLeftFolder.addPlaceholder(METAMODELS_VIEW);

        PerspectiveObject po = null;
        Collection<PerspectiveObject> otherViews = getViews(PerspectiveObject.TOP_LEFT, false);
        if( !otherViews.isEmpty() ) {
            for( Iterator<PerspectiveObject> iter = otherViews.iterator(); iter.hasNext(); ) {
                po = iter.next();
                if( po.isPlaceholder())
                    topLeftFolder.addPlaceholder(po.getViewId());
                else
                    addView(po.getViewId(), topLeftFolder, theLayout);
            }
        }

        // --------------------------------------
        // Create output folder (bottom right) - Tasks
        //
        IFolderLayout bottomRightFolder = theLayout.createFolder(OUTPUT_FOLDER,
                                                            IPageLayout.BOTTOM,
                                                            (float)0.75,
                                                            editorArea);

        addView(IPageLayout.ID_PROBLEM_VIEW, bottomRightFolder, theLayout);
        addView(ERROR_LOG_VIEW, bottomRightFolder, theLayout);
        bottomRightFolder.addPlaceholder(SEARCH_RESULT_VIEW);

        otherViews = getViews(PerspectiveObject.BOTTOM_RIGHT, false);
        if( !otherViews.isEmpty() ) {
            for( Iterator<PerspectiveObject> iter = otherViews.iterator(); iter.hasNext(); ) {
                po = iter.next();
                if( po.isPlaceholder())
                    bottomRightFolder.addPlaceholder(po.getViewId());
                else
                    addView(po.getViewId(), bottomRightFolder, theLayout);
            }
        }

        theLayout.addShowViewShortcut(SEARCH_RESULT_VIEW);

        //
        // Create properties folder (bottom left) - Properties, Keywords, Description
        //

        IFolderLayout bottomLeftFolder = theLayout.createFolder(PROPERTY_FOLDER,
                                                           IPageLayout.BOTTOM,
                                                           (float)0.5,
                                                           TREE_FOLDER);
        addView(PROPERTY_VIEW, bottomLeftFolder, theLayout);
//REMOVED FROM APOLLO GA:
//        addView(KEYWORDS_VIEW, propsFolder, theLayout);
        addView(DESCRIPTION_VIEW, bottomLeftFolder, theLayout);
//        propsFolder.addPlaceholder(NAVIGATOR_VIEW);
        bottomLeftFolder.addPlaceholder(TAGS_VIEW);

        otherViews = getViews(PerspectiveObject.BOTTOM_LEFT, false);
        if( !otherViews.isEmpty() ) {
            for( Iterator<PerspectiveObject> iter = otherViews.iterator(); iter.hasNext(); ) {
                po = iter.next();
                if( po.isPlaceholder())
                    bottomLeftFolder.addPlaceholder(po.getViewId());
                else
                    addView(po.getViewId(), bottomLeftFolder, theLayout);
            }
        }
        
        // --------------------------------------
        // Create Center Left Folder (additional views)
        //
        IFolderLayout centerLeftFolder;
        otherViews = getViews(PerspectiveObject.LEFT_CENTER, false);
        if( otherViews != null && !otherViews.isEmpty() ) {
            centerLeftFolder = theLayout.createFolder(CENTER_LEFT_FOLDER,
                                                       IPageLayout.TOP,
                                                       (float)0.4,
                                                       PROPERTY_FOLDER);
            for( Iterator<PerspectiveObject> iter = otherViews.iterator(); iter.hasNext(); ) {
                po = iter.next();
                if( po.isPlaceholder())
                    centerLeftFolder.addPlaceholder(po.getViewId());
                else
                    addView(po.getViewId(), centerLeftFolder, theLayout);
            }
        }
        //
        // add action sets to be installed in the toolbar
        //

//        theLayout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
//        theLayout.addActionSet(JavaUI.ID_ACTION_SET);
//        theLayout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
//        theLayout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);

        //
        // File->New actions
        //
        theLayout.addNewWizardShortcut(NEW_PROJECT_WIZARD);
        theLayout.addNewWizardShortcut(NEW_FOLDER_WIZARD);
        theLayout.addNewWizardShortcut(NEW_MODEL_WIZARD);
        theLayout.addNewWizardShortcut(NEW_VDB_WIZARD);
        theLayout.addNewWizardShortcut(NEW_MED_WIZARD);


    }

    private static void loadPerspectiveObjects() {
        //
        List<PerspectiveObject> poList = new ArrayList<PerspectiveObject>();
        boolean oK = true;
        Iterator<IModelerPerspectiveContributor> iter = contributors.iterator();
        IModelerPerspectiveContributor helper = null;
        while( iter.hasNext() && oK) {
            helper = iter.next();
            PerspectiveObject[] contributions = helper.getContributions();
            for( int i=0; i<contributions.length; i++ )
                poList.add(contributions[i]);
        }
        contributedPOs = new PerspectiveObject[poList.size()];
        if( !poList.isEmpty() ) {
            Iterator<PerspectiveObject> iter2 = poList.iterator();
            int i=0;
            while( iter2.hasNext() ) {
                contributedPOs[i++] = iter2.next();
            }
        }
    }

    private Collection<PerspectiveObject> getViews(int viewLocationId, boolean isPrimary) {
        if( contributedPOs.length == 0 )
            return Collections.emptyList();

        List<PerspectiveObject> views = new ArrayList<PerspectiveObject>();

        for( int i=0; i<contributedPOs.length; i++ ) {
            if( contributedPOs[i].locationID == viewLocationId )
                views.add(contributedPOs[i]);
        }

        if( views.isEmpty() )
            return Collections.emptyList();

        return views;
    }

    private static void loadPerpectiveExtensions() {
        contributors = new ArrayList<IModelerPerspectiveContributor>();

        // get the extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, ID);

        // get the all extensions to the NewChildAction extension point
        IExtension[] extensions = extensionPoint.getExtensions();

        // walk through the extensions and find all INewChildAction implementations
        for ( int i=0 ; i<extensions.length ; ++i ) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();
            try {

                // first, find the content provider instance and add it to the instance list
                for ( int j=0 ; j<elements.length ; ++j ) {
                    if ( elements[j].getName().equals(CLASS)) {
                        Object contributor = elements[j].createExecutableExtension(CLASSNAME);
                        if ( contributor instanceof IModelerPerspectiveContributor ) {
                            contributors.add((IModelerPerspectiveContributor)contributor);
                        }
                    }
                }

            } catch (Exception e) {
                // catch any Exception that occurred obtaining the configuration and log it
                String message = UiConstants.Util.getString("ModelerPerspectiveFactory.loadingExtensionsErrorMessage", //$NON-NLS-1$
                            extensions[i].getUniqueIdentifier());
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }

        loadPerspectiveObjects();
    }

}
