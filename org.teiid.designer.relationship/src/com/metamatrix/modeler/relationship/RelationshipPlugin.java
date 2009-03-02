/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.osgi.framework.BundleContext;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexSelectorFactory;
import com.metamatrix.modeler.core.search.commands.FindRelatedObjectsCommand;
import com.metamatrix.modeler.core.search.commands.FindRelationshipTypesCommand;
import com.metamatrix.modeler.core.search.commands.FindRelationshipsCommand;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.internal.core.index.ModelWorkspaceIndexSelectorFactory;
import com.metamatrix.modeler.internal.core.search.commands.FindRelatedObjectsCommandImpl;
import com.metamatrix.modeler.internal.core.search.commands.FindRelationshipTypesCommandImpl;
import com.metamatrix.modeler.internal.core.search.commands.FindRelationshipsCommandImpl;
import com.metamatrix.modeler.internal.relationship.NavigationContextBuilderImpl;
import com.metamatrix.modeler.internal.relationship.NavigationContextCache;
import com.metamatrix.modeler.internal.relationship.NavigationContextImpl;
import com.metamatrix.modeler.internal.relationship.NavigationHistoryImpl;
import com.metamatrix.modeler.internal.relationship.NavigationLinkImpl;
import com.metamatrix.modeler.internal.relationship.NavigationNodeImpl;
import com.metamatrix.modeler.internal.relationship.RelationshipEditorImpl;
import com.metamatrix.modeler.internal.relationship.RelationshipSearchImpl;
import com.metamatrix.modeler.internal.relationship.RelationshipTypeEditorImpl;


/**
 * The main plugin class to be used in the desktop.
 */
public class RelationshipPlugin extends Plugin {

    // =========================================================
    // Static

    public static RelationshipPlugin INSTANCE;

    public static RelationshipPlugin getDefault() {
        return INSTANCE;
    }
    
    // =========================================================
    // Constructor

    /**
     * Construct an instance of RelationshipPlugin.
     */
    public RelationshipPlugin() {
        INSTANCE = this;
    }
    
    /**
     * The plug-in identifier of this plugin
     */
    public static final String PLUGIN_ID = "org.teiid.designer.relationship"; //$NON-NLS-1$
    
    public static final String PACKAGE_ID = RelationshipPlugin.class.getPackage().getName();

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID,I18N_NAME,ResourceBundle.getBundle(I18N_NAME));

    public static final boolean DEBUG = false;

    private static final NavigationContextBuilder navContextBuilder = new NavigationContextBuilderImpl();
    private static final NavigationContextCache navContextCache = new NavigationContextCache(navContextBuilder);

    /**
     * ItemDelegator for finding EObject names
     */
    private static AdapterFactoryItemDelegator emfItemDelegator = new AdapterFactoryItemDelegator(ModelerCore.getMetamodelRegistry().getAdapterFactory());

    /**
     * Navigation History variable. This is the shared instance.
     */
    public static final NavigationHistory navHistory = new NavigationHistoryImpl(navContextCache);

    /**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 *
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
    @Override
	public void start( BundleContext context ) throws Exception {
		super.start(context);
        ((PluginUtilImpl)Util).initializePlatformLogger(this);
    }

    /**
     * Create an editor for the supplied {@link Relationship}.  If a new {@link Relationship} object
     * is to be edited, then the caller must create that instance using the
     * {@link com.metamatrix.metamodels.relationship.RelationshipFactory#createRelationship() RelationshipFactory}
     * method and pass that new Relationship object into this method.
     * @param relationship the relationship to be edited; may not be null
     * @return the relationship editor; never null
     */
    public static RelationshipEditor createEditor( final Relationship relationship ) {
        final boolean useTransactions = true;
        return new RelationshipEditorImpl(relationship,useTransactions);
    }

    /**
     * Create an editor for the supplied {@link RelationshipType}.  If a new {@link RelationshipType} object
     * is to be edited, then the caller must create that instance using the
     * {@link com.metamatrix.metamodels.relationship.RelationshipFactory#createRelationshipType() RelationshipFactory}
     * method and pass that new RelationshipType object into this method.
     * @param relationshipType the relationship type to be edited; may not be null
     * @return the relationship type editor; never null
     */
    public static RelationshipTypeEditor createEditor( final RelationshipType relationshipType ) {
        final boolean useTransactions = true;
        return new RelationshipTypeEditorImpl(relationshipType,useTransactions);
    }

	/**
	 * Returns the current Navigation History.
	 * @return the NavigationHistory object
	 */
	public static NavigationHistory getSharedNavigationHistory() {
	   return navHistory;
	}

	/**
	 * Creates and returns a new Navigation History object.
	 * @return a new Navigation History object.
	 */
	public static NavigationHistory createNavigationHistory() {
		return new NavigationHistoryImpl(navContextCache);
	}

    /**
    * Creates a command object to find objects that are related to another.
    * @return the new command object.
    * @see #execute(RelationshipSearchCommand)
    */
    public static FindRelatedObjectsCommand createFindRelatedObjectsCommand() {
		return new FindRelatedObjectsCommandImpl();
    }

    /**
    * Creates a command object to find relationships that satisfy a criteria.
    * @return the new command object.
    * @see #execute(RelationshipSearchCommand)
    */
    public static FindRelationshipsCommand createFindRelationshipsCommand() {
		return new FindRelationshipsCommandImpl();
    }

    /**
    * Creates a command object to find relationship types.
    * @return the new command object.
    * @see #execute(RelationshipSearchCommand)
    */
    public static FindRelationshipTypesCommand createFindRelationshipTypesCommand() {
		return new FindRelationshipTypesCommandImpl();
    }

    /**
     * Create a NavigationContext object from the specified modelObject, using the children
     * of the object to build links.  (Added by Steve Jacobs for modeler testing -- will move soon)
     * (Moved here by Ted Jones. Will be removed when the NavigationContextBuilder is implemented.)
     */

    public static NavigationContext createChildNavigationContext(EObject modelObject) throws Exception {
        URI uri;
        NavigationContextInfo info;
        NavigationContext context;
        NavigationNode node;
        NavigationLink link;

        List links;
        links = new ArrayList();

        uri = ModelerCore.getModelEditor().getUri(modelObject);
        if ( uri == null ) {
            final String msg = RelationshipPlugin.Util.getString("RelationshipPlugin.The_model_object_has_no_URI"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        String path = ModelerCore.getModelEditor().getFullPathToParent(modelObject).toString();
        String name = emfItemDelegator.getText(modelObject);
        node = new NavigationNodeImpl(uri, name, modelObject.eClass(), path);

        info = new NavigationContextInfo(modelObject,uri.toString());
        context = new NavigationContextImpl(node,info);

        List children = modelObject.eContents();
        Iterator iter = children.iterator();
        int i = 0;
        while ( iter.hasNext() ) {
            EObject child = (EObject) iter.next();
            uri = ModelerCore.getModelEditor().getUri(child);
            name = emfItemDelegator.getText(child);
            path = ModelerCore.getModelEditor().getFullPathToParent(child).toString();
            node = new NavigationNodeImpl(uri, name, child.eClass(), path);

            uri = ModelerCore.getModelEditor().getUri(child);
            link = new NavigationLinkImpl(uri, "Parent of", "Container"); //$NON-NLS-1$  //$NON-NLS-2$
            links.add(link);
            context.addNodeAndLink(node,link);
            ++i;
        }

        EObject parent = modelObject.eContainer();
        if ( parent != null ) {
            uri = ModelerCore.getModelEditor().getUri(parent);
            name = emfItemDelegator.getText(parent);
            path = ModelerCore.getModelEditor().getFullPathToParent(parent).toString();
            node = new NavigationNodeImpl(uri, name, parent.eClass(), path);

            uri = ModelerCore.getModelEditor().getUri(parent);
            link = new NavigationLinkImpl(uri, "Child of", "Container"); //$NON-NLS-1$  //$NON-NLS-2$
            links.add(link);
            context.addNodeAndLink(node,link);
        }

        return context;
    }

    /**
     * Create an object that performs searches for {@link Relationship} instances.
     * @return the search object; never null
     */
    public static RelationshipSearch createRelationshipSearch() {
        final IndexSelectorFactory factory = new ModelWorkspaceIndexSelectorFactory();
        final ModelWorkspace workspace = ModelerCore.getModelWorkspace();
        return new RelationshipSearchImpl(workspace,factory);
    }

}
