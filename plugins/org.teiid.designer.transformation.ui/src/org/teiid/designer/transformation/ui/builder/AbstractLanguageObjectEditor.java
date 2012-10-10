/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.ui.builder.model.ILanguageObjectEditorModel;
import org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener;


/**
 * AbstractLanguageObjectEditor
 *
 * @since 8.0
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
        CoreArgCheck.isNotNull(theParent);
        CoreArgCheck.isNotNull(theEditorType);
        CoreArgCheck.isNotNull(theModel);

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
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#acceptFocus()
     */
    @Override
	public abstract void acceptFocus();

    /**
     * Convenience method to add model listener.
     * 
     * @param theListener the listener being added
     * @return <code>true</code> if the listener was added; <code>false</code> otherwise.
     * @throws IllegalArgumentException if listener is <code>null</code>
     * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModel#addModelListener(ILanguageObjectEditorModelListener)
     */
    public boolean addModelListener( ILanguageObjectEditorModelListener theListener ) {
        return model.addModelListener(theListener);
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#clear()
     */
    @Override
	public void clear() {
        model.clear();
    }

    /**
     * Subclasses must create UI.
     */
    protected abstract void createUi( Composite theControl );

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#getEditorType()
     */
    @Override
	public Class getEditorType() {
        return editorType;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#getLanguageObject()
     */
    @Override
	public LanguageObject getLanguageObject() {
        return model.getLanguageObject();
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#getModel()
     */
    @Override
	public ILanguageObjectEditorModel getModel() {
        return model;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#getTitle()
     */
    @Override
	public abstract String getTitle();

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#getToolTipText()
     */
    @Override
	public abstract String getToolTipText();

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#getUi()
     */
    @Override
	public final Control getUi() {
        return pnlUi;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#hasChanged()
     */
    @Override
	public boolean hasChanged() {
        return model.hasChanged();
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#isComplete()
     */
    @Override
	public boolean isComplete() {
        return model.isComplete();
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#isEnabled()
     */
    @Override
	public boolean isEnabled() {
        return pnlUi.isEnabled();
    }

    /**
     * Convenience method to remove model listener.
     * 
     * @param theListener the listener being removed
     * @return <code>true</code> if the listener was removed; <code>false</code> otherwise.
     * @throws IllegalArgumentException if listener is <code>null</code>
     * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModel#removeModelListener(ILanguageObjectEditorModelListener)
     */
    public boolean removeModelListener( ILanguageObjectEditorModelListener theListener ) {
        return model.removeModelListener(theListener);
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#reset()
     */
    @Override
	public void reset() {
        model.reset();
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#save()
     */
    @Override
	public void save() {
        model.save();
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#setEditorType(java.lang.Class)
     */
    @Override
	public void setEditorType( Class theEditorType ) {
        CoreArgCheck.isNotNull(theEditorType, Util.getString(PREFIX + "nullEditorType")); //$NON-NLS-1$

        if (!LanguageObject.class.isAssignableFrom(theEditorType)) {
            CoreArgCheck.isTrue(LanguageObject.class.isAssignableFrom(theEditorType), Util.getString(PREFIX
                                                                                                     + "editorTypeNotLangObj")); //$NON-NLS-1$
        }

        editorType = theEditorType;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#setEnabled(boolean)
     */
    @Override
	public void setEnabled( boolean theEnableFlag ) {
        if (theEnableFlag) {
            WidgetUtil.enable(pnlUi);
        } else {
            WidgetUtil.disable(pnlUi);
        }
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#setModel(org.teiid.query.internal.ui.builder.model.ILanguageObjectEditorModel)
     */
    @Override
	public void setModel( ILanguageObjectEditorModel theModel ) {
        CoreArgCheck.isNotNull(theModel);
        CoreArgCheck.isEqual(editorType, theModel.getModelType());
        model = theModel;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#setLanguageObject(org.teiid.query.sql.LanguageObject)
     */
    @Override
	public abstract void setLanguageObject( LanguageObject theLanguageObject );

}
