/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.event;

import java.util.List;


/** 
 * @since 4.3
 */
public interface IRevealHideListener {

    public boolean isRevealHideBehaviorEnabled();

    public void notifyElementsRevealed( Object source, List lstElements );

    public void notifyElementsHidden( Object source, List lstElements );
    
}
