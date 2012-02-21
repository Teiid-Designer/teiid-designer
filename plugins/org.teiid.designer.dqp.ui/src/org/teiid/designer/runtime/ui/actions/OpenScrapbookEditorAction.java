/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.sqltools.editor.core.connection.ISQLEditorConnectionInfo;
import org.eclipse.datatools.sqltools.internal.sqlscrapbook.SqlscrapbookPlugin;
import org.eclipse.datatools.sqltools.internal.sqlscrapbook.editor.SQLScrapbookEditor;
import org.eclipse.datatools.sqltools.internal.sqlscrapbook.util.SQLFileUtil;
import org.eclipse.datatools.sqltools.sqleditor.SQLEditorConnectionInfo;
import org.eclipse.datatools.sqltools.sqleditor.SQLEditorStorage;
import org.eclipse.datatools.sqltools.sqleditor.SQLEditorStorageEditorInput;
import org.eclipse.datatools.sqltools.sqlscrapbook.actions.OpenScrapbookAction;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.ui.internal.util.UiUtil;

public class OpenScrapbookEditorAction extends OpenScrapbookAction {

	public OpenScrapbookEditorAction() {
		
	}
	
	public void run(IConnectionProfile profile, String vdbName) {
		
    	ISQLEditorConnectionInfo editorConnectionInfo = new SQLEditorConnectionInfo(null, profile.getName(), vdbName);
    		
		
		String scrap = StringUtilities.EMPTY_STRING;
		SQLEditorStorageEditorInput editorStorageEditorInput = new SQLEditorStorageEditorInput(scrap, scrap);
		
		editorStorageEditorInput.setStorage(new SQLEditorStorage("SQL " + vdbName,  //$NON-NLS-1$
				"// 1) Enter valid SQL query (i.e. SELECT * FROM FOO)\n" + //$NON-NLS-1$
				"// 2) Highlight query and right-click select \"Execute Selected Text\"" +  //$NON-NLS-1$ 
				"\n\nSELECT * FROM <table-name>")); //$NON-NLS-1$
		
		editorStorageEditorInput.setConnectionInfo(SQLFileUtil.getConnectionInfo4Scrapbook(editorConnectionInfo));

		IWorkbenchWindow window = UiUtil.getWorkbenchWindow();
		
		// the name will show as the title of the editor
		IEditorReference[] editors = window.getActivePage()
				.getEditorReferences();
		int suffix = 0;
		List editorNameList = new ArrayList();
		for (int i = 0; i < editors.length; i++) {
			editorNameList.add(editors[i].getName());
		}

		for (;;) {
			String name = "SQL Scrapbook" + Integer.toString(suffix); //$NON-NLS-1$
			if (!editorNameList.contains(name)) {
				editorStorageEditorInput.setName(name);
				try {
					window.getActivePage().openEditor(editorStorageEditorInput,
							SQLScrapbookEditor.EDITOR_ID);
				} catch (PartInitException e) {
					SqlscrapbookPlugin.log(e);
				}
				break;
			}
			suffix++;
		}
	}

}
