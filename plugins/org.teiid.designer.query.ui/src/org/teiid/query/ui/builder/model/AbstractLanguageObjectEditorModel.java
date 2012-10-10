/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.builder.model;

import java.util.ArrayList;
import java.util.List;

import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.ui.UiConstants;
import org.teiid.query.ui.builder.util.BuilderUtils;


/**
 * AbstractLanguageObjectEditorModel
 *
 * @since 8.0
 */
public abstract class AbstractLanguageObjectEditorModel implements ILanguageObjectEditorModel, UiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PREFIX = I18nUtil.getPropertyPrefix(AbstractLanguageObjectEditorModel.class);

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private LanguageObject savedLangObj;

    private List<ILanguageObjectEditorModelListener> listeners;

    protected boolean notifyListeners = true;

    /** The model language object type. */
    private Class modelType;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public AbstractLanguageObjectEditorModel( Class theType ) {
        setModelType(theType);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModel#addModelListeners(org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener)
     */
    @Override
	public boolean addModelListener( ILanguageObjectEditorModelListener theListener ) {
        CoreArgCheck.isNotNull(theListener);

        boolean result = false;

        if (listeners == null) {
            listeners = new ArrayList<ILanguageObjectEditorModelListener>();
        }

        if (!listeners.contains(theListener)) {
            result = listeners.add(theListener);
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModel#clear()
     */
    @Override
	public void clear() {
        if (savedLangObj != null) {
            savedLangObj = null;
            fireModelChanged(LanguageObjectEditorModelEvent.SAVED);
        }
    }

    /**
     * Notifies registered {@link org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener}s of the given
     * event. Listeners are notified in the order they registered in.
     * 
     * @param theEvent the event being broadcast
     */
    protected void fireModelChanged( String theType ) {
        if (notifyListeners && (listeners != null)) {
            LanguageObjectEditorModelEvent event = new LanguageObjectEditorModelEvent(this, theType);

            for (int size = listeners.size(), i = 0; i < size; i++) {
                ILanguageObjectEditorModelListener listener = listeners.get(i);
                listener.modelChanged(event);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModel#getLanguageObject()
     */
    @Override
	public abstract LanguageObject getLanguageObject();

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModel#getType()
     */
    @Override
	public Class getModelType() {
        return modelType;
    }

    /**
     * Gets the saved <code>LanguageObject</code>.
     * 
     * @return the <code>LanguageObject</code> or <code>null</code>
     */
    protected LanguageObject getSavedLanguageObject() {
        return savedLangObj;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModel#hasChanged()
     */
    @Override
	public boolean hasChanged() {
        // if no saved language object return true if complete
        // if there is a saved language object return true if the current value is complete and different
        boolean result = false;

        if (savedLangObj == null) {
            result = isComplete();
        } else {
            boolean complete = isComplete();
            if (complete) {
                result = (!savedLangObj.equals(getLanguageObject()));
            } else {
                result = false;
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModel#isComplete()
     */
    @Override
	public abstract boolean isComplete();

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModel#removeModelListener(org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener)
     */
    @Override
	public boolean removeModelListener( ILanguageObjectEditorModelListener theListener ) {
        CoreArgCheck.isNotNull(theListener);

        boolean result = false;

        if (listeners != null) {
            result = listeners.remove(theListener);

            if (listeners.isEmpty()) {
                listeners = null;
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModel#reset()
     */
    @Override
	public void reset() {
        setLanguageObject(savedLangObj);
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModel#save()
     */
    @Override
	public void save() {
        savedLangObj = getLanguageObject();
        fireModelChanged(LanguageObjectEditorModelEvent.SAVED);
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModel#setLanguageObject(org.teiid.query.sql.LanguageObject)
     */
    @Override
	public void setLanguageObject( LanguageObject theLangObj ) {
        if (theLangObj != null) {
            if (!modelType.isAssignableFrom(theLangObj.getClass())) {
                CoreArgCheck.isTrue(modelType.isAssignableFrom(theLangObj.getClass()),
                                    Util.getString(PREFIX + "wrongLangObjType", //$NON-NLS-1$
                                                   new Object[] {theLangObj.getClass().getName(), modelType.getName()}));
            }
        }

        // make sure no implicit functions get shown to user
        savedLangObj = (theLangObj == null) ? theLangObj : BuilderUtils.getBuilderLanguageObject(theLangObj);
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModel#setType(java.lang.Class)
     */
    @Override
	public void setModelType( Class theLanguageObjectClass ) {
        CoreArgCheck.isNotNull(theLanguageObjectClass, PREFIX + "nullType"); //$NON-NLS-1$
        CoreArgCheck.isTrue(LanguageObject.class.isAssignableFrom(theLanguageObjectClass), PREFIX + "modelTypeNotLangObj"); //$NON-NLS-1$

        modelType = theLanguageObjectClass;
    }

}
