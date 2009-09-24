/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.editors;


/** 
 * This interface provides classes like <code>ModelEditor</code> to register itself with individual <code>ModelEditorPage</code>'s,
 * so it can determine with all editors have been full initialized and displayed. In the case of the <code>DiagramEditor</code>, 
 * the act of displaying a diagram, requires an async process that will potentially change the resource (i.e. make it dirty). 
 * The <code>ModelEditor</code> can now perform a save when the <code>DiagramEditor</code> has completed it's display.
 * 
 * see <code>IInitializationCompleteNotifier</code> for the companion notifier class
 * @since 4.3
 */
public interface IInitializationCompleteListener {

    void processInitializationComplete();
    
}
