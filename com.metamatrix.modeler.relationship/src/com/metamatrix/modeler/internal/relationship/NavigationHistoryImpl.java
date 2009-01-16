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

package com.metamatrix.modeler.internal.relationship;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.relationship.NavigationContext;
import com.metamatrix.modeler.relationship.NavigationContextException;
import com.metamatrix.modeler.relationship.NavigationContextInfo;
import com.metamatrix.modeler.relationship.NavigationHistory;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.RelationshipPlugin;

/**
 * The NavigationHistoryImpl is the primary implementation of {@link NavigationHistory}
 */
public class NavigationHistoryImpl implements NavigationHistory {

    private final NavigationContextCache navContextCache;
    private NavigationContext currentContext;
    private final LinkedList backInfos;
    private final LinkedList forwardInfos;

    /**
     * Construct an instance of NavigationHistoryImpl.
     * 
     */
    public NavigationHistoryImpl(final NavigationContextCache cache) {
        super();
        ArgCheck.isNotNull(cache);
        this.navContextCache = cache;
        this.backInfos = new LinkedList();
        this.forwardInfos = new LinkedList();
    }

    protected NavigationContextInfo getInfo(final NavigationContext context) {
        return context.getInfo();
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationHistory#refresh()
     */
    public void refresh() throws NavigationContextException {
        final NavigationContextInfo currentInfo = this.currentContext != null ?
                                                  this.currentContext.getInfo() :
                                                  null;
        this.navContextCache.clearCache();
        if ( currentInfo != null ) {
            this.currentContext = this.navContextCache.getNavigationContext(currentInfo);
        }
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationHistory#getCurrent()
     */
    public NavigationContext getCurrent() {
        return this.currentContext;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationHistory#getPrevious()
     */
    public synchronized NavigationContext getPrevious() throws NavigationContextException {
        if (this.backInfos.isEmpty()) {
            return null;
        }
        // Remove the previous from the back list
        final NavigationContextInfo previousInfo = (NavigationContextInfo)this.backInfos.getLast();
        final NavigationContext previous = this.navContextCache.getNavigationContext(previousInfo);
        this.backInfos.removeLast(); // do this last in case we can't build a context

        // Add the current to the forward list ...
        final NavigationContextInfo currentInfo = getInfo(this.currentContext);
        this.forwardInfos.addFirst(currentInfo);

        // Set the current to the previous ...
        this.currentContext = previous;

        // Return the current ...
        return this.currentContext;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationHistory#getNext()
     */
    public synchronized NavigationContext getNext() throws NavigationContextException {
        if (this.forwardInfos.isEmpty()) {
            return null;
        }
        // Remove the previous from the forward list
        final NavigationContextInfo nextInfo = (NavigationContextInfo)this.forwardInfos.getFirst();
        final NavigationContext next = this.navContextCache.getNavigationContext(nextInfo);
        this.forwardInfos.removeFirst(); // do this last in case we can't build a context

        // Add the current to the back list ...
        final NavigationContextInfo currentInfo = getInfo(this.currentContext);
        this.backInfos.addLast(currentInfo);

        // Set the current to the next ...
        this.currentContext = next;

        // Return the current ...
        return this.currentContext;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationHistory#hasPrevious()
     */
    public boolean hasPrevious() {
        return !this.backInfos.isEmpty();
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationHistory#hasNext()
     */
    public boolean hasNext() {
        return !this.forwardInfos.isEmpty();
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationHistory#clearHistory()
     */
    public synchronized void clearHistory() {
        this.forwardInfos.clear();
        this.backInfos.clear();
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationHistory#getBackInfos()
     */
    public List getBackInfos() {
        return this.backInfos;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationHistory#getForwardInfos()
     */
    public List getForwardInfos() {
        return this.forwardInfos;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationHistory#selectFromHistory(com.metamatrix.modeler.relationship.NavigationContextInfo)
     */
    public synchronized NavigationContext selectFromHistory(NavigationContextInfo info)
        throws NavigationContextException {
        if (this.currentContext == null) {
            // Not contained by backs or forwards, so fail ...
            final Object[] params = new Object[] { info };
            final String msg = RelationshipPlugin.Util.getString("NavigationHistoryImpl.The_supplied_info_was_not_found_in_the_history", params); //$NON-NLS-1$
            throw new NavigationContextException(msg);
        }

        // See if moving backward or forwards ...
        if (this.backInfos.contains(info)) {
            doMoveBack(info);
        } else if (this.forwardInfos.contains(info)) {
            doMoveForward(info);
        } else {
            // Not contained by backs or forwards, so fail ...
            final Object[] params = new Object[] { info };
            final String msg = RelationshipPlugin.Util.getString("NavigationHistoryImpl.The_supplied_info_was_not_found_in_the_history", params); //$NON-NLS-1$
            throw new NavigationContextException(msg);
        }
        return this.currentContext;
    }

    protected void doMoveBack(final NavigationContextInfo info) throws NavigationContextException {
        // Get the navigation context for the info ...
        final NavigationContext selected = this.navContextCache.getNavigationContext(info);
        final NavigationContextInfo currentInfo = getInfo(this.currentContext);

        // Move the current into the forward ...
        this.forwardInfos.addFirst(currentInfo);

        // Move the backs into the forwards until we find the match ...
        final ListIterator iter = this.backInfos.listIterator(this.backInfos.size());
        while (iter.hasPrevious()) {
            final NavigationContextInfo tempInfo = (NavigationContextInfo)iter.previous();
            iter.remove();
            if (tempInfo.equals(info)) {
                // Found it, so set to current ...
                this.currentContext = selected;
                break; // stop
            }
            // No match, so move it to the forward ...
            this.forwardInfos.addFirst(tempInfo);
        }
    }

    protected void doMoveForward(final NavigationContextInfo info) throws NavigationContextException {
        // Get the navigation context for the info ...
        final NavigationContext selected = this.navContextCache.getNavigationContext(info);
        final NavigationContextInfo currentInfo = getInfo(this.currentContext);

        // Move the current into the back ...
        this.backInfos.addLast(currentInfo);

        // Move the forwards into the backs until we find the match ...
        final ListIterator iter = this.forwardInfos.listIterator();
        while (iter.hasNext()) {
            final NavigationContextInfo tempInfo = (NavigationContextInfo)iter.next();
            iter.remove();
            if (tempInfo.equals(info)) {
                // Found it, so set to current ...
                this.currentContext = selected;
                break; // stop
            }
            // No match, so move it to the forward ...
            this.backInfos.addLast(tempInfo);
        }
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationHistory#navigateTo(com.metamatrix.modeler.relatoinship.NavigationNode)
     */
    public NavigationContext navigateTo(NavigationNode focusObject) throws NavigationContextException {
        ArgCheck.isNotNull(focusObject);
        final URI uri = focusObject.getModelObjectUri();
        if (uri == null) {
            final String msg = RelationshipPlugin.Util.getString("NavigationContextInfo.Unable_to_obtain_URI_for_supplied_object", focusObject); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        return navigateTo(uri);
    }

    /**
      * @see com.metamatrix.modeler.relationship.NavigationHistory#navigateTo(org.eclipse.emf.ecore.EObject)
      */
    public NavigationContext navigateTo(EObject focusObject) throws NavigationContextException {
        ArgCheck.isNotNull(focusObject);
        final URI uri = this.navContextCache.getResolver().getUri(focusObject);
        if (uri == null) {
            final String msg = RelationshipPlugin.Util.getString("NavigationContextInfo.Unable_to_obtain_URI_for_supplied_object", focusObject); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        return navigateTo(uri, focusObject);
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationHistory#navigateTo(org.eclipse.emf.common.util.URI)
     */
    public synchronized NavigationContext navigateTo(final URI focusObjectUri) throws NavigationContextException {
        ArgCheck.isNotNull(focusObjectUri);

        EObject eObject = null;
        try {
            eObject = this.navContextCache.getResolver().resolve(focusObjectUri.toString());
        } catch (CoreException e) {
            String msg = e.getMessage();
            RelationshipPlugin.Util.log(IStatus.ERROR, msg);
        }        
        if (eObject == null) {
            final String msg = RelationshipPlugin.Util.getString("NavigationContextInfo.Unable_to_obtain_URI_for_supplied_object", focusObjectUri); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }

        final NavigationContextInfo info = new NavigationContextInfo(eObject,focusObjectUri.toString());
        // Get the navigation context for the info ...
        final NavigationContext selected = this.navContextCache.getNavigationContext(info);

        // Move the current onto the back infos ...
        if (this.currentContext != null) {
            final NavigationContextInfo currentInfo = getInfo(this.currentContext);
            this.backInfos.addLast(currentInfo);
        }
        this.currentContext = selected;
        // Clear the forward infos ...
        this.forwardInfos.clear();

        return this.currentContext;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationHistory#navigateTo(org.eclipse.emf.common.util.URI,
     *                                                                       org.eclipse.emf.ecore.EObject)
     */
    public synchronized NavigationContext navigateTo(final URI focusObjectUri, final EObject eObject)
        throws NavigationContextException {
        ArgCheck.isNotNull(focusObjectUri);
        final NavigationContextInfo info = new NavigationContextInfo(eObject,focusObjectUri.toString());
        // Get the navigation context for the info ...
        final NavigationContext selected = this.navContextCache.getNavigationContext(info);

        // Move the current onto the back infos ...
        if (this.currentContext != null) {
            final NavigationContextInfo currentInfo = getInfo(this.currentContext);
            this.backInfos.addLast(currentInfo);
        }
        this.currentContext = selected;
        // Clear the forward infos ...
        this.forwardInfos.clear();

        return this.currentContext;
    }

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.NavigationHistory#peakAtNext()
	 */
	public NavigationContext peakAtNext() throws NavigationContextException {
		if (this.forwardInfos.isEmpty()) {
			return null;
		}
		// Remove the previous from the forward list
		final NavigationContextInfo nextInfo = (NavigationContextInfo)this.forwardInfos.getFirst();
		final NavigationContext next = this.navContextCache.getNavigationContext(nextInfo);

		return next;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.NavigationHistory#peakAtPrevious()
	 */
	public NavigationContext peakAtPrevious() throws NavigationContextException {
		if (this.backInfos.isEmpty()) {
			return null;
		}
		// Remove the previous from the back list
		final NavigationContextInfo previousInfo = (NavigationContextInfo)this.backInfos.getLast();
		final NavigationContext previous = this.navContextCache.getNavigationContext(previousInfo);
		
		return previous;
	}

}
