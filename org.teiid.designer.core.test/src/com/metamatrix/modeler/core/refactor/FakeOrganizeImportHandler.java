/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.util.List;
import junit.framework.Assert;

/**
 * FakeOrganizeImportHandler
 */
public class FakeOrganizeImportHandler implements OrganizeImportHandler {

    private int choice;
    private int expectedOptionCount;
    private boolean checkExpectedOptionCount;
    private boolean handlerCalled;

    /**
     * Construct an instance of FakeOrganizeImportHandler.
     *
     */
    public FakeOrganizeImportHandler() {
        super();
    }

    public void setChoice( final int choice ) {
        this.choice = choice;
    }

    public void setExpectedOptionCount( final int expectedOptionCount ) {
        this.expectedOptionCount = expectedOptionCount;
    }

    public void setCheckExpectedOptionCount( final boolean check ) {
        this.checkExpectedOptionCount = check;
    }

    public boolean isHandlerCalled() {
        return this.handlerCalled;
    }

    /**
     * Always chooses the first option, or null if there aren't any
     * @see com.metamatrix.modeler.core.refactor.OrganizeImportHandler#choose(java.util.List)
     */
    public Object choose(final List options) {
        this.handlerCalled = true;
        if ( this.checkExpectedOptionCount ) {
            if ( options == null || options.isEmpty() ) {
                Assert.assertEquals(this.expectedOptionCount, 0);
            } else {
                Assert.assertEquals(this.expectedOptionCount, options.size());
            }
        }
        if ( options == null || options.isEmpty() ) {
            return null;
        }
        return options.get(this.choice);
    }

}
