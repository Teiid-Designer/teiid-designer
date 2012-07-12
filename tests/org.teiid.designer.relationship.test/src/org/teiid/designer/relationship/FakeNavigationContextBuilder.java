/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship;

import org.eclipse.emf.common.util.URI;
import org.teiid.designer.metamodels.relationship.RelationshipPackage;
import org.teiid.designer.relationship.NavigationContext;
import org.teiid.designer.relationship.NavigationContextBuilder;
import org.teiid.designer.relationship.NavigationContextInfo;
import org.teiid.designer.relationship.NavigationNode;
import org.teiid.designer.relationship.NavigationNodeImpl;


/**
 * FakeNavigationContextBuilder
 */
public class FakeNavigationContextBuilder implements NavigationContextBuilder {

    /**
     * Construct an instance of FakeNavigationContextBuilder.
     */
    public FakeNavigationContextBuilder() {
        super();
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContextBuilder#buildNavigationContext(org.teiid.designer.relationship.NavigationContextInfo)
     */
    public NavigationContext buildNavigationContext( NavigationContextInfo info ) {
        final URI uri = URI.createURI(info.getFocusNodeUri());
        final NavigationNode node = new NavigationNodeImpl(uri,
                                                           "label", RelationshipPackage.eINSTANCE.getRelationshipType(), "path"); //$NON-NLS-1$ //$NON-NLS-2$
        return new FakeNavigationContextImpl(node, info);
    }

    /**
     * @see org.teiid.designer.relationship.NavigationContextBuilder#getAllNodes(java.lang.String)
     */
    public NavigationContext getAllNodes( String uriString ) {
        final URI uri = URI.createURI(uriString);
        final NavigationNode node = new NavigationNodeImpl(uri,
                                                           "label", RelationshipPackage.eINSTANCE.getRelationshipType(), "path"); //$NON-NLS-1$ //$NON-NLS-2$
        final NavigationContextInfo info = new NavigationContextInfo(uriString);
        return new FakeNavigationContextImpl(node, info);
    }

}
