/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.teiid.designer.relational.model.RelationalProcedure;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.model.RelationalViewIndex;
import org.teiid.designer.relational.model.RelationalViewProcedure;
import org.teiid.designer.relational.model.RelationalViewTable;
import org.teiid.designer.relational.ui.edit.RelationalEditorPanel;
import org.teiid.designer.relational.ui.edit.RelationalIndexEditorPanel;
import org.teiid.designer.relational.ui.editor.EditRelationalObjectDialogModel;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.ui.common.eventsupport.IDialogStatusListener;

/**
 *
 */
public class TransformationDialogModel extends EditRelationalObjectDialogModel {

	/**
     * @param relationalObject
     * @param modelFile
     */
    public TransformationDialogModel(final RelationalReference relationalObject, final IFile modelFile) {
        super(relationalObject, modelFile);
    }

    @Override
	public RelationalEditorPanel getEditorPanel( IDialogStatusListener statusListener, Composite parent) {
		if( relationalObject instanceof RelationalViewTable ) {
			return new ViewTableEditorPanel(parent, this, statusListener);
		}
		else if( relationalObject instanceof RelationalViewProcedure ) {
			return new ViewProcedureEditorPanel(parent, this, statusListener);
		}
		else if( relationalObject instanceof RelationalViewIndex ) {
			return new RelationalIndexEditorPanel(parent, this, statusListener);
		}

		return null;
	}

	@Override
	public String getDialogTitle() {
		if( relationalObject instanceof RelationalViewTable ) {
        	return Messages.createRelationalViewTableTitle;
        } else if( relationalObject instanceof RelationalViewProcedure ) {
        	if( ((RelationalProcedure)relationalObject).isFunction() ) {
        		return Messages.createRelationalViewUserDefinedFunctionTitle;
        	}
        	return Messages.createRelationalViewProcedureTitle;
        } else if( relationalObject instanceof RelationalViewIndex ) {
        	return Messages.createRelationalViewIndexTitle;
        }

        return NLS.bind(Messages.unsupportedObjectType, relationalObject.getClass().toString());
	}

	@Override
	public String getHelpText() {
		if( relationalObject instanceof RelationalViewTable ) {
        	return Messages.createRelationalViewTableHelpText;
        } else if( relationalObject instanceof RelationalViewProcedure ) {
        	if( ((RelationalProcedure)relationalObject).isFunction() ) {
        		return Messages.createRelationalViewUserDefinedFunctionHelpText;
        	}
        	return Messages.createRelationalViewProcedureHelpText;
        } else if( relationalObject instanceof RelationalViewIndex ) {
        	return Messages.createRelationalViewIndexHelpText;
        }

        return NLS.bind(Messages.unsupportedObjectType, relationalObject.getClass().toString());
	}
}
