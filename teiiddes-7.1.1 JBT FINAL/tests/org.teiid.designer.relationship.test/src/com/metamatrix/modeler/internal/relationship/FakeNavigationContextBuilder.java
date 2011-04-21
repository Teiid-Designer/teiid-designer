/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import org.eclipse.emf.common.util.URI;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.modeler.relationship.NavigationContext;
import com.metamatrix.modeler.relationship.NavigationContextBuilder;
import com.metamatrix.modeler.relationship.NavigationContextInfo;
import com.metamatrix.modeler.relationship.NavigationNode;

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
     * @see com.metamatrix.modeler.relationship.NavigationContextBuilder#buildNavigationContext(com.metamatrix.modeler.relationship.NavigationContextInfo)
     */
    public NavigationContext buildNavigationContext( NavigationContextInfo info ) {
        final URI uri = URI.createURI(info.getFocusNodeUri());
        final NavigationNode node = new NavigationNodeImpl(uri,
                                                           "label", RelationshipPackage.eINSTANCE.getRelationshipType(), "path"); //$NON-NLS-1$ //$NON-NLS-2$
        return new FakeNavigationContextImpl(node, info);
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationContextBuilder#getAllNodes(java.lang.String)
     */
    public NavigationContext getAllNodes( String uriString ) {
        final URI uri = URI.createURI(uriString);
        final NavigationNode node = new NavigationNodeImpl(uri,
                                                           "label", RelationshipPackage.eINSTANCE.getRelationshipType(), "path"); //$NON-NLS-1$ //$NON-NLS-2$
        final NavigationContextInfo info = new NavigationContextInfo(uriString);
        return new FakeNavigationContextImpl(node, info);
    }

}
