/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel;
import com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.builder.ILanguageObjectEditor;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * AbstractLanguageObjectEditor
 */
public abstract class AbstractLanguageObjectEditor implements ILanguageObjectEditor, UiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PREFIX = "AbstractLanguageObjectEditor."; //$NON-NLS-1$

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** The type of language object being created/edited. */
    private Class editorType;

    /** The model. */
    private ILanguageObjectEditorModel model;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONTROLS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** The UI control. */
    private Composite pnlUi;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs an <code>AbstractLanguageObjectEditor</code> with the given type.
     * 
     * @param theParent the UI parent
     * @param theEditorType the type of <code>LanguageObject</code> being edited
     * @param theModel the editor's model
     * @throws IllegalArgumentException if any of the parameters are <code>null</code>
     */
    protected AbstractLanguageObjectEditor( Composite theParent,
                                            Class theEditorType,
                                            ILanguageObjectEditorModel theModel ) {
        ArgCheck.isNotNull(theParent);
        ArgCheck.isNotNull(theEditorType);
        ArgCheck.isNotNull(theModel);

        setEditorType(theEditorType);
        setModel(theModel);

        pnlUi = new Composite(theParent, SWT.NONE);
        pnlUi.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        pnlUi.setLayout(layout);

        createUi(pnlUi);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#acceptFocus()
     */
    public abstract void acceptFocus();

    /**
     * Convenience method to add model listener.
     * 
     * @param theListener the listener being added
     * @return <code>true</code> if the listener was added; <code>false</code> otherwise.
     * @throws IllegalArgumentException if listener is <code>null</code>
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#addModelListener(ILanguageObjectEditorModelListener)
     */
    public boolean addModelListener( ILanguageObjectEditorModelListener theListener ) {
        return model.addModelListener(theListener);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#clear()
     */
    public void clear() {
        model.clear();
    }

    /**
     * Subclasses must create UI.
     */
    protected abstract void createUi( Composite theControl );

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#getEditorType()
     */
    public Class getEditorType() {
        return editorType;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#getLanguageObject()
     */
    public LanguageObject getLanguageObject() {
        return model.getLanguageObject();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#getModel()
     */
    public ILanguageObjectEditorModel getModel() {
        return model;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#getTitle()
     */
    public abstract String getTitle();

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#getToolTipText()
     */
    public abstract String getToolTipText();

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#getUi()
     */
    public final Control getUi() {
        return pnlUi;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#hasChanged()
     */
    public boolean hasChanged() {
        return model.hasChanged();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#isComplete()
     */
    public boolean isComplete() {
        return model.isComplete();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#isEnabled()
     */
    public boolean isEnabled() {
        return pnlUi.isEnabled();
    }

    /**
     * Convenience method to remove model listener.
     * 
     * @param theListener the listener being removed
     * @return <code>true</code> if the listener was removed; <code>false</code> otherwise.
     * @throws IllegalArgumentException if listener is <code>null</code>
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#removeModelListener(ILanguageObjectEditorModelListener)
     */
    public boolean removeModelListener( ILanguageObjectEditorModelListener theListener ) {
        return model.removeModelListener(theListener);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#reset()
     */
    public void reset() {
        model.reset();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#save()
     */
    public void save() {
        model.save();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#setEditorType(java.lang.Class)
     */
    public void setEditorType( Class theEditorType ) {
        if (theEditorType == null) {
            Assertion.isNotNull(theEditorType, Util.getString(PREFIX + "nullEditorType")); //$NON-NLS-1$
        }
        if (!LanguageObject.class.isAssignableFrom(theEditorType)) {
            Assertion.assertTrue(LanguageObject.class.isAssignableFrom(theEditorType), Util.getString(PREFIX
                                                                                                      + "editorTypeNotLangObj")); //$NON-NLS-1$
        }

        editorType = theEditorType;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#setEnabled(boolean)
     */
    public void setEnabled( boolean theEnableFlag ) {
        if (theEnableFlag) {
            WidgetUtil.enable(pnlUi);
        } else {
            WidgetUtil.disable(pnlUi);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#setModel(com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel)
     */
    public void setModel( ILanguageObjectEditorModel theModel ) {
        Assertion.isNotNull(theModel);
        Assertion.isEqual(editorType, theModel.getModelType());
        model = theModel;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#setLanguageObject(com.metamatrix.query.sql.LanguageObject)
     */
    public abstract void setLanguageObject( LanguageObject theLanguageObject );

}
