/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelRegistry;
import com.metamatrix.modeler.core.search.commands.FindRelatedObjectsCommand;
import com.metamatrix.modeler.core.search.commands.FindRelationshipsCommand;
import com.metamatrix.modeler.core.search.runtime.RelatedObjectRecord;
import com.metamatrix.modeler.core.search.runtime.RelationshipRecord;
import com.metamatrix.modeler.relationship.NavigationContext;
import com.metamatrix.modeler.relationship.NavigationContextBuilder;
import com.metamatrix.modeler.relationship.NavigationContextInfo;
import com.metamatrix.modeler.relationship.NavigationLink;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.RelationshipPlugin;

/**
 * NavigationContextBuilderImpl is the primary implementation of {@link NavigationContextBuilder}
 */
public class NavigationContextBuilderImpl implements NavigationContextBuilder {

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$
    private NavigationContextInfo info = null;
    private NavigationContext context = null;
    private NavigationNode focusNode = null;
    private String nonfocusrole = EMPTY_STRING;
    private MetamodelRegistry registry = ModelerCore.getMetamodelRegistry();

    /**
     * Construct an instance of NavigationContextBuilder.
     */
    public NavigationContextBuilderImpl() {
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContextBuilder#buildNavigationContext(com.metamatrix.modeler.relationship.NavigationContextInfo)
     */
    public NavigationContext buildNavigationContext( NavigationContextInfo info ) {
        Assertion.isNotNull(info);
        this.info = info;

        setFocusNode(null);
        getAllNodes(info.getFocusNodeUri());

        return context;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContext#getAllNodes()
     */
    public NavigationContext getAllNodes( String focusNodeURI ) {
        FindRelatedObjectsCommand command = RelationshipPlugin.createFindRelatedObjectsCommand();
        IStatus status = null;
        command.setModelObjectUri(focusNodeURI);
        if (command.canExecute()) {
            status = command.execute();
        }

        if (status != null) {
            if (!status.isOK()) {
                String msg = status.getMessage();
                RelationshipPlugin.Util.log(IStatus.ERROR, msg);
            }
        }

        URI focusURI = URI.createURI(focusNodeURI);
        Collection collection = command.getRelatedObjectInfo();
        Iterator nodeIter = collection.iterator();
        RelatedObjectRecord record = null;
        NavigationNode node = null;
        NavigationLink link = null;
        while (nodeIter.hasNext()) {
            record = (RelatedObjectRecord)nodeIter.next();

            if (getFocusNode() == null) {
                createFocusNode(record);
                context = new NavigationContextImpl(focusNode, info);
            }

            node = createNavigationNode(record);
            link = createNavigationLink(record, getFocusNode().getLabel(), nonfocusrole);
            context.addNodeAndLink(node, link);
        }

        if (focusNode == null) // The selected node does not have
        // any relationships defined.
        {
            String pathInModel = ""; //$NON-NLS-1$ 
            // TODO Source from Record?
            focusNode = new NavigationNodeImpl(focusURI,
                                               "No relationships defined.", //$NON-NLS-1$ 
                                               // TODO Add object name?
                                               registry.getMetaClass(info.getFocusNodeMetaclassUri()), pathInModel,
                                               "No relationships..."); //$NON-NLS-1$

            context = new NavigationContextImpl(focusNode, info);
        }

        return context;
    }

    /**
     * Method to execute {@link FindRelationshipsCommand command} to retrieve link specific information to create the tooltip text
     * and the NavigationLink.
     * 
     * @param UUID for a given link
     * @return The {@link RelationshipRecord record} for the given link.
     */
    public RelationshipRecord getLinkInfo( String linkUUID ) {
        FindRelationshipsCommand command = RelationshipPlugin.createFindRelationshipsCommand();
        IStatus status = null;
        command.setRelationshipUUID(linkUUID);
        if (command.canExecute()) {
            status = command.execute();
        }

        if (status != null) {
            if (!status.isOK()) {
                String msg = status.getMessage();
                RelationshipPlugin.Util.log(IStatus.ERROR, msg);
            }
        }

        Collection collection = command.getRelationShipInfo();
        Iterator linkIter = collection.iterator();
        RelationshipRecord record = null;
        // TODO Update to expect only one record after layout changes?
        while (linkIter.hasNext()) {
            record = (RelationshipRecord)linkIter.next();
        }

        return record;
    }

    /**
     * Create a {@link NavigationNode node} from a given {@link RelatedObjectRecord}.
     * 
     * @param the record of related objects for the current focus node
     * @return a newly contructed {@link NavigationNode}.
     */
    public NavigationNode createNavigationNode( RelatedObjectRecord record ) {
        String name = record.getRelatedObjectName();
        NavigationNode node;
        nonfocusrole = record.getRelatedRoleName();
        String type = record.getMetaClassUri();

        URI uri = URI.createURI(record.getRelatedObjectUri());
        String pathInModel = ""; //$NON-NLS-1$
        String toolTip = buildNodeToolTip(name, type, record.getRelatedObjectUri(), pathInModel);
        node = new NavigationNodeImpl(uri, name, registry.getMetaClass(record.getRelatedMetaClassUri()), pathInModel, toolTip);

        return node;
    }

    /**
     * Create a {@link NavigationLink link} from a given {@link RelatedObjectRecord}.
     * 
     * @param the record of related objects for the current focus node
     * @param the focus node role
     * @param the non-focus node role
     * @return a newly contructed {@link NavigationLink}.
     */
    public NavigationLink createNavigationLink( RelatedObjectRecord record,
                                                String focusRole,
                                                String nonfocusrole ) {
        String uuid = record.getRelationshipUUID();
        NavigationLink link;

        RelationshipRecord linkRecord = getLinkInfo(uuid);

        URI uri = URI.createURI(linkRecord.getUri());

        String name = linkRecord.getName();
        String type = linkRecord.getTypeName();

        String toolTip = buildLinkToolTip(name, type);
        link = new NavigationLinkImpl(uri, name, type, focusRole, nonfocusrole, toolTip);
        return link;
    }

    /**
     * Generate a {@link NavigationNode node} (current focus node) given a RelatedObjectRecord.
     * 
     * @param the related object record.
     */
    private void createFocusNode( RelatedObjectRecord record ) {
        NavigationNode node;
        String name = record.getName();
        // ring role = record.getRoleName();
        String type = record.getMetaClassUri();
        URI uri = URI.createURI(record.getUri());
        String pathInModel = ""; //$NON-NLS-1$
        String toolTip = buildNodeToolTip(name, type, record.getUri(), pathInModel);
        node = new NavigationNodeImpl(uri, name, registry.getMetaClass(record.getMetaClassUri()), pathInModel, toolTip);
        setFocusNode(node);
    }

    /**
     * Build the tooltip text for a node.
     * 
     * @param the label text
     * @param the metaclass to be used for the Type field
     * @param the uri (minus the fragment) which represents the model field
     * @param the pathInModel which is the node path relative to the model
     */
    public String buildNodeToolTip( String label,
                                    String metaClass,
                                    String uri,
                                    String pathInModel ) {
        final Object[] params = new Object[] {label != null ? label : EMPTY_STRING, metaClass != null ? metaClass : EMPTY_STRING,
            uri != null ? uri : EMPTY_STRING, pathInModel != null ? pathInModel : EMPTY_STRING};
        final String tooltip = RelationshipPlugin.Util.getString("NavigationContextBuilderImpl.NodeTooltipText", params); //$NON-NLS-1$
        return tooltip;
    }

    /**
     * Build the tooltip text for a link.
     * 
     * @param the relationship name of the link (i.e. Author of)
     * @param the relationship type
     */
    public String buildLinkToolTip( String name,
                                    String type ) {
        final Object[] params = new Object[] {name != null ? name : EMPTY_STRING, type != null ? type : EMPTY_STRING};
        final String tooltip = RelationshipPlugin.Util.getString("NavigationContextBuilderImpl.LinkTooltipText", params); //$NON-NLS-1$
        return tooltip;
    }

    /**
     * returns the current focus node.
     * 
     * @return <@NavigationNode node> that is the current focus node.
     */
    public NavigationNode getFocusNode() {
        return this.focusNode;
    }

    /**
     * Set the current focus node.
     * 
     * @param node
     */
    public void setFocusNode( NavigationNode node ) {
        this.focusNode = node;
    }

}
