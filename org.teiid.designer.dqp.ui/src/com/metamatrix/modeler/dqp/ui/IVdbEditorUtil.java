/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.ui;

import org.eclipse.ui.PartInitException;
import com.metamatrix.vdb.edit.VdbContextEditor;
import com.metamatrix.vdb.edit.VdbEditingContext;


/** 
 * @since 4.3
 */
public interface IVdbEditorUtil {

    /**
     * Open the Connector Bindings editor for the specified <code>VdbEditingContext</code> 
     * @param context
     * @throws PartInitException
     * @since 4.3
     */
    public void openConnectorBindingsEditor(VdbEditingContext context) throws PartInitException;
    
    /**
     * Display the Problems associated with the specified <code>VdbEditingContext</code> 
     * @param context
     * @throws PartInitException
     * @since 4.3
     */
    public void displayVdbProblems(VdbEditingContext context) throws PartInitException;
    
    /**
     * Open the VdbEditor  for the specified <code>VdbEditingContext</code> and activate the specified tab.
     * If the VdbEditor for this context is already open, the editor will be activated and the specified
     * tab will be brought to front. 
     * @param context
     * @param tabId the ID of the Vdb Editor tab to be brought to front. May be null. For tab ID constants
     * see <code>VdbUiConstants</code>.  If a tab is not supported for the running product, the method
     * will simply return.
     * @throws PartInitException
     * @since 4.3
     */
    public void openVdbEditor(VdbEditingContext context, String tabId) throws PartInitException;

    /**
     * Open the Connector Bindings editor for the specified <code>VdbEditingContext</code> 
     * @param context
     * @throws PartInitException
     * @since 4.3
     */
    public void openConnectorBindingsEditor(VdbContextEditor context) throws PartInitException;
    
    /**
     * Display the Problems associated with the specified <code>VdbEditingContext</code> 
     * @param context
     * @throws PartInitException
     * @since 4.3
     */
    public void displayVdbProblems(VdbContextEditor context) throws PartInitException;
    
    /**
     * Open the VdbEditor  for the specified <code>VdbEditingContext</code> and activate the specified tab.
     * If the VdbEditor for this context is already open, the editor will be activated and the specified
     * tab will be brought to front. 
     * @param context
     * @param tabId the ID of the Vdb Editor tab to be brought to front. May be null. For tab ID constants
     * see <code>VdbUiConstants</code>.  If a tab is not supported for the running product, the method
     * will simply return.
     * @throws PartInitException
     * @since 4.3
     */
    public void openVdbEditor(VdbContextEditor context, String tabId) throws PartInitException;
    
}
