/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.layout.spring;

public class SpringNodeConstraints {
    private boolean _fixed;
    private double _weight;

    public SpringNodeConstraints() {
        this(false, 0.0);
    }

    public SpringNodeConstraints(boolean bool, double d) {
        _fixed = bool;
        _weight = d;
    }

    public double getWeight() {
        return _weight;
    }

    public boolean isFixed() {
        return _fixed;
    }

    public void setFixed(boolean bool) {
        _fixed = bool;
    }

    public void setWeight(double d) {
        _weight = d;
    }
}
