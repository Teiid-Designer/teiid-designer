/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui.editor.panels;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.ui.VdbUiConstants;

/**
 *
 */
public class DescriptionPanel {
	Vdb vdb;
	StyledTextEditor textEditor;
	
    static String i18n( final String id ) {
        return VdbUiConstants.Util.getString(id);
    }
    
	/**
     * @param parent
     * @param vdb
     */
    public DescriptionPanel(Composite parent, Vdb vdb) {
    	super();
    	this.vdb = vdb;
    	
    	createPanel(parent);
    }
    
	private void createPanel(Composite parent) {
    	Group descriptionGroup = WidgetFactory.createGroup(parent, i18n("description"), GridData.FILL_BOTH, 1, 1); //$NON-NLS-1$
    	
        this.textEditor = new StyledTextEditor(descriptionGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 1;

        this.textEditor.setLayoutData(gridData);
        this.textEditor.setText(vdb.getDescription());
        this.textEditor.getDocument().addDocumentListener(new IDocumentListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
             */
            @Override
            public void documentAboutToBeChanged( final DocumentEvent event ) {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
             */
            @Override
            public void documentChanged( final DocumentEvent event ) {
            	vdb.setDescription(textEditor.getText());
            }

        });
    }
	
	@SuppressWarnings("javadoc")
	public void close() {
        if (textEditor != null) textEditor.dispose();
	}
    
}
