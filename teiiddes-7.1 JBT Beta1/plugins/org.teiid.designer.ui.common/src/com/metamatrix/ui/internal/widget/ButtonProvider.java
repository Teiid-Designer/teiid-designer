/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Button;

/**
 * 
 */
public interface ButtonProvider {

    /**
     * @return the {@link Button button's} text
     */
    String getText();

    /**
     * @param selection
     * @return <code>true</code> if the {@link Button button} is enabled
     */
    boolean isEnabled( IStructuredSelection selection );

    /**
     * @param selection
     */
    void selected( IStructuredSelection selection );
}
