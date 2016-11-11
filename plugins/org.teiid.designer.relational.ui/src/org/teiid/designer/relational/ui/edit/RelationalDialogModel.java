/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui.edit;

import org.eclipse.core.resources.IFile;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.teiid.designer.relational.model.RelationalIndex;
import org.teiid.designer.relational.model.RelationalProcedure;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.model.RelationalView;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.editor.EditRelationalObjectDialogModel;
import org.teiid.designer.ui.common.eventsupport.IDialogStatusListener;

/**
 * Dialog model class designed to provide generic editor panels for specific relational object types
 *
 * @since 8.0
 */
public class RelationalDialogModel extends EditRelationalObjectDialogModel {

    /**
     * @param relationalObject
     * @param modelFile
     */
    public RelationalDialogModel(RelationalReference relationalObject,
                                 IFile modelFile) {
        super(relationalObject, modelFile);
    }

    @Override
    public RelationalEditorPanel getEditorPanel(IDialogStatusListener statusListener,
                                                Composite parent) {
        if (relationalObject instanceof RelationalView) {
            return new RelationalViewEditorPanel(parent, this, statusListener);
        } else if (relationalObject instanceof RelationalTable) {
            return new RelationalTableEditorPanel(parent, this, statusListener);
        }  else if (relationalObject instanceof RelationalProcedure) {
            return new RelationalProcedureEditorPanel(parent, this, statusListener);
        } else if (relationalObject instanceof RelationalIndex) {
            return new RelationalIndexEditorPanel(parent, this, statusListener);
        }

        return null;
    }

    @Override
    public String getDialogTitle() {
        if (relationalObject instanceof RelationalView) {
            return Messages.createRelationalViewTitle;
        } else if (relationalObject instanceof RelationalTable) {
            return Messages.createRelationalTableTitle;
        } else if (relationalObject instanceof RelationalProcedure) {
            RelationalProcedure procedure = (RelationalProcedure)relationalObject;
            if (procedure.isSourceFunction()) {
                return Messages.createRelationalSourceFunctionTitle;
            } else if (procedure.isFunction()) {
                return Messages.createRelationalUserDefinedFunctionTitle;
            } else if (procedure.isNativeQueryProcedure()) {
                return Messages.createRelationalNativeQueryProcedureTitle;
            }
            
            return Messages.createRelationalProcedureTitle;
        } else if (relationalObject instanceof RelationalIndex) {
            return Messages.createRelationalIndexTitle;
        }

        return NLS.bind(Messages.unsupportedObjectType, relationalObject.getClass().toString());
    }

    @Override
    public String getHelpText() {
        if (relationalObject instanceof RelationalView) {
            return Messages.createRelationalViewHelpText;
        } else if (relationalObject instanceof RelationalTable) {
            return Messages.createRelationalTableHelpText;
        } else if (relationalObject instanceof RelationalProcedure) {
            RelationalProcedure procedure = (RelationalProcedure)relationalObject;
            if (procedure.isSourceFunction()) {
                return Messages.createRelationalSourceFunctionHelpText;
            } else if (procedure.isFunction()) {
                return Messages.createRelationalUserDefinedFunctionHelpText;
            } else if (procedure.isNativeQueryProcedure()) {
                return Messages.createRelationalNativeQueryProcedureHelpText;
            }

            return Messages.createRelationalProcedureHelpText;
        } else if (relationalObject instanceof RelationalIndex) {
            return Messages.createRelationalIndexHelpText;
        }

        return NLS.bind(Messages.unsupportedObjectType, relationalObject.getClass().toString());
    }
}
