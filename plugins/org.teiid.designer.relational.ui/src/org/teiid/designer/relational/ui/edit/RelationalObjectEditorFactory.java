/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui.edit;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Composite;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.model.RelationalTable;

public class RelationalObjectEditorFactory {

	public static RelationalEditorPanel getEditorPanel( IDialogStatusListener statusListener, 
														Composite parent, 
														RelationalReference relationalObject, 
														IFile modelFile ) {
		if( relationalObject instanceof RelationalTable ) {
			return new RelationalTableEditorPanel(parent, (RelationalTable)relationalObject, modelFile,statusListener);
		}
		
		return null;
	}
}
