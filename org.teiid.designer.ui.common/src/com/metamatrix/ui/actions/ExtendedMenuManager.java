/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;

/**
 * The <code>ExtendedMenuManager</code> extends the functionality of {@link org.eclipse.jface.action.MenuManager}.
 * A property exists where duplicate {@link org.eclipse.jface.action.IAction}s
 * or {@link org.eclipse.jface.action.IContributionItem}s are not allowed. Duplicates have the same ID. IDs
 * that are empty or <code>null</code> are allowed to be added. Also items added using the <code>insertAfter</code>
 * and <code>insertBefore</code> methods will be added if they are added next to an item with the same ID.
 * By default duplicates are not allowed.
 */
public class ExtendedMenuManager extends MenuManager {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Property indicating if duplicates are allowed. */
    private boolean duplicatesAllowed = false;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Construct an instance of <code>ExtendedMenuManager</code> with no ID and no text.
     */
    public ExtendedMenuManager() {
        super();
    }

    /**
     * Construct an instance of <code>ExtendedMenuManager</code> with the specified text and no ID.
     * @param theText the menu text
     */
    public ExtendedMenuManager(String theText) {
        super(theText);
    }

    /**
     * Construct an instance of <code>ExtendedMenuManager</code> with the specified text and ID.
     * @param theText the menu text
     * @param theId the menu ID
     */
    public ExtendedMenuManager(String theText, String theId) {
        super(theText, theId);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * @see org.eclipse.jface.action.ContributionManager#add(org.eclipse.jface.action.IAction)
     */
    @Override
    public void add(IAction theAction) {
        if (isOkToAdd(theAction)) {
            super.add(theAction);
        }
    }

    /**
     * @see org.eclipse.jface.action.ContributionManager#add(org.eclipse.jface.action.IContributionItem)
     */
    @Override
    public void add(IContributionItem theItem) {
        if (isOkToAdd(theItem)) {
            super.add(theItem);
        }
    }

    /**
     * @see org.eclipse.jface.action.ContributionManager#appendToGroup(java.lang.String, org.eclipse.jface.action.IAction)
     */
    @Override
    public void appendToGroup(String theGroupName, IAction theAction) {
        if (isOkToAdd(theAction)) {
            super.appendToGroup(theGroupName, theAction);
        }
    }

    /**
     * @see org.eclipse.jface.action.ContributionManager#appendToGroup(java.lang.String, org.eclipse.jface.action.IContributionItem)
     */
    @Override
    public void appendToGroup(String theGroupName, IContributionItem theItem) {
        if (isOkToAdd(theItem)) {
            super.appendToGroup(theGroupName, theItem);
        }
    }

    /**
     * @see org.eclipse.jface.action.ContributionManager#insert(int, org.eclipse.jface.action.IContributionItem)
     */
    @Override
    public void insert(int theIndex, IContributionItem theItem) {
        if (isOkToAdd(theItem)) {
            super.insert(theIndex, theItem);
        }
    }

    /**
     * @see org.eclipse.jface.action.ContributionManager#insertAfter(java.lang.String, org.eclipse.jface.action.IAction)
     */
    @Override
    public void insertAfter(String theId, IAction theAction) {
        boolean insert = false;
        
        if ((theId != null) && theId.equals(theAction.getId())) {
            insert = true;
        } else if (isOkToAdd(theAction)) {
            insert = true;
        }

        if (insert) {
            super.insertAfter(theId, theAction);
        }
    }

    /**
     * @see org.eclipse.jface.action.ContributionManager#insertAfter(java.lang.String, org.eclipse.jface.action.IContributionItem)
     */
    @Override
    public void insertAfter(String theId, IContributionItem theItem) {
        boolean insert = false;
        
        if ((theId != null) && theId.equals(theItem.getId())) {
            insert = true;
        } else if (isOkToAdd(theItem)) {
            insert = true;
        }

        if (insert) {
            super.insertAfter(theId, theItem);
        }
    }

    /**
     * @see org.eclipse.jface.action.ContributionManager#insertBefore(java.lang.String, org.eclipse.jface.action.IAction)
     */
    @Override
    public void insertBefore(String theId, IAction theAction) {
        boolean insert = false;
        
        if ((theId != null) && theId.equals(theAction.getId())) {
            insert = true;
        } else if (isOkToAdd(theAction)) {
            insert = true;
        }

        if (insert) {
            super.insertBefore(theId, theAction);
        }
    }

    /**
     * @see org.eclipse.jface.action.ContributionManager#insertBefore(java.lang.String, org.eclipse.jface.action.IContributionItem)
     */
    @Override
    public void insertBefore(String theId, IContributionItem theItem) {
        boolean insert = false;
        
        if ((theId != null) && theId.equals(theItem.getId())) {
            insert = true;
        } else if (isOkToAdd(theItem)) {
            insert = true;
        }

        if (insert) {
            super.insertBefore(theId, theItem);
        }
    }
    
    /**
     * Inidicates if duplicate items are allowed.
     * @return <code>true</code> if allowed; <code>false</code> otherwise.
     */
    public boolean isDuplicatesAllowed() {
        return this.duplicatesAllowed;
    }
    
    /**
     * Indicates if the specified action can be added.
     * @param theAction the action requesting to be added
     * @return <code>true</code> if it can be added; <code>false</code> otherwise.
     */
    protected boolean isOkToAdd(IAction theAction) {
        return isOkToAdd(theAction.getId());
    }

    /**
     * Indicates if the specified item can be added.
     * @param theItem the item requesting to be added
     * @return <code>true</code> if it can be added; <code>false</code> otherwise.
     */
    protected boolean isOkToAdd(IContributionItem theItem) {
        return isOkToAdd(theItem.getId());
    }
    
    /**
     * Indicates if the specified identifier can be added. The identifier should belong to an action or an
     * item.
     * @param theId the identifier requesting to be added
     * @return <code>true</code> if it can be added; <code>false</code> otherwise.
     */
    protected boolean isOkToAdd(String theId) {
        return (this.duplicatesAllowed || (theId == null) || (theId.length() == 0) || (indexOf(theId) == -1));
    }

    /**
     * @see org.eclipse.jface.action.ContributionManager#prependToGroup(java.lang.String, org.eclipse.jface.action.IAction)
     */
    @Override
    public void prependToGroup(String theGroupName, IAction theAction) {
        if (isOkToAdd(theAction)) {
            super.prependToGroup(theGroupName, theAction);
        }
    }

    /**
     * @see org.eclipse.jface.action.ContributionManager#prependToGroup(java.lang.String, org.eclipse.jface.action.IContributionItem)
     */
    @Override
    public void prependToGroup(String theGroupName, IContributionItem theItem) {
        if (isOkToAdd(theItem)) {
            super.prependToGroup(theGroupName, theItem);
        }
    }
    
    /**
     * Sets if duplicate actions or items can be added.
     * @param theAllowedFlag the flag indicating if duplicates are allowed
     */
    public void setDuplicatesAllowed(boolean theAllowedFlag) {
        this.duplicatesAllowed = theAllowedFlag;
    }

}
