/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;


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
        CoreArgCheck.isNotNull(cache);
        this.navContextCache = cache;
        this.backInfos = new LinkedList();
        this.forwardInfos = new LinkedList();
    }

    protected NavigationContextInfo getInfo(final NavigationContext context) {
        return context.getInfo();
    }

    /**
     * @see org.teiid.designer.relationship.NavigationHistory#refresh()
     */
    @Override
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
     * @see org.teiid.designer.relationship.NavigationHistory#getCurrent()
     */
    @Override
	public NavigationContext getCurrent() {
        return this.currentContext;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationHistory#getPrevious()
     */
    @Override
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
     * @see org.teiid.designer.relationship.NavigationHistory#getNext()
     */
    @Override
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
     * @see org.teiid.designer.relationship.NavigationHistory#hasPrevious()
     */
    @Override
	public boolean hasPrevious() {
        return !this.backInfos.isEmpty();
    }

    /**
     * @see org.teiid.designer.relationship.NavigationHistory#hasNext()
     */
    @Override
	public boolean hasNext() {
        return !this.forwardInfos.isEmpty();
    }

    /**
     * @see org.teiid.designer.relationship.NavigationHistory#clearHistory()
     */
    @Override
	public synchronized void clearHistory() {
        this.forwardInfos.clear();
        this.backInfos.clear();
    }

    /**
     * @see org.teiid.designer.relationship.NavigationHistory#getBackInfos()
     */
    @Override
	public List getBackInfos() {
        return this.backInfos;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationHistory#getForwardInfos()
     */
    @Override
	public List getForwardInfos() {
        return this.forwardInfos;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationHistory#selectFromHistory(org.teiid.designer.relationship.NavigationContextInfo)
     */
    @Override
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
     * @see org.teiid.designer.relationship.NavigationHistory#navigateTo(org.teiid.designer.relatoinship.NavigationNode)
     */
    @Override
	public NavigationContext navigateTo(NavigationNode focusObject) throws NavigationContextException {
        CoreArgCheck.isNotNull(focusObject);
        final URI uri = focusObject.getModelObjectUri();
        if (uri == null) {
            final String msg = RelationshipPlugin.Util.getString("NavigationContextInfo.Unable_to_obtain_URI_for_supplied_object", focusObject); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        return navigateTo(uri);
    }

    /**
      * @see org.teiid.designer.relationship.NavigationHistory#navigateTo(org.eclipse.emf.ecore.EObject)
      */
    @Override
	public NavigationContext navigateTo(EObject focusObject) throws NavigationContextException {
        CoreArgCheck.isNotNull(focusObject);
        final URI uri = this.navContextCache.getResolver().getUri(focusObject);
        if (uri == null) {
            final String msg = RelationshipPlugin.Util.getString("NavigationContextInfo.Unable_to_obtain_URI_for_supplied_object", focusObject); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        return navigateTo(uri, focusObject);
    }

    /**
     * @see org.teiid.designer.relationship.NavigationHistory#navigateTo(org.eclipse.emf.common.util.URI)
     */
    @Override
	public synchronized NavigationContext navigateTo(final URI focusObjectUri) throws NavigationContextException {
        CoreArgCheck.isNotNull(focusObjectUri);

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
     * @see org.teiid.designer.relationship.NavigationHistory#navigateTo(org.eclipse.emf.common.util.URI,
     *                                                                       org.eclipse.emf.ecore.EObject)
     */
    @Override
	public synchronized NavigationContext navigateTo(final URI focusObjectUri, final EObject eObject)
        throws NavigationContextException {
        CoreArgCheck.isNotNull(focusObjectUri);
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
	 * @See org.teiid.designer.relationship.NavigationHistory#peakAtNext()
	 */
	@Override
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
	 * @See org.teiid.designer.relationship.NavigationHistory#peakAtPrevious()
	 */
	@Override
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
