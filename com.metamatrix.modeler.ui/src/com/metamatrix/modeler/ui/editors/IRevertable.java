/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.editors;

/** Used to describe something that can revert to a saved version of the document
  *  it manages.  Should this extend IEditorPart?  That would get us editor-related
  *  methods like isDirty() and save().
  * @author PForhan
  */
public interface IRevertable {
    public void doRevertToSaved();
}
