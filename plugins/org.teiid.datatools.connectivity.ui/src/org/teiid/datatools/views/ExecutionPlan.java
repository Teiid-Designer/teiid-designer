/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.views;

/**
 * 
 */
public class ExecutionPlan {

    private PlanElement rootElement;

    public ExecutionPlan() {
    }

    public ExecutionPlan( PlanElement rootElement ) {
        this.rootElement = rootElement;
    }

    public void setRoot( PlanElement rootElement ) {
        this.rootElement = rootElement;
    }

    public PlanElement getRoot() {
        return this.rootElement;
    }
}
