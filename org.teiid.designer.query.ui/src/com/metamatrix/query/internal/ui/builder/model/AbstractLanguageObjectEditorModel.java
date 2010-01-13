/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.model;

import java.util.ArrayList;
import java.util.List;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.internal.ui.builder.util.BuilderUtils;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.ui.UiConstants;

/**
 * AbstractLanguageObjectEditorModel
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

    private List listeners;

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
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#addModelListeners(com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener)
     */
    public boolean addModelListener( ILanguageObjectEditorModelListener theListener ) {
        ArgCheck.isNotNull(theListener);

        boolean result = false;

        if (listeners == null) {
            listeners = new ArrayList();
        }

        if (!listeners.contains(theListener)) {
            result = listeners.add(theListener);
        }

        return result;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#clear()
     */
    public void clear() {
        if (savedLangObj != null) {
            savedLangObj = null;
            fireModelChanged(LanguageObjectEditorModelEvent.SAVED);
        }
    }

    /**
     * Notifies registered {@link com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener}s of the given
     * event. Listeners are notified in the order they registered in.
     * 
     * @param theEvent the event being broadcast
     */
    protected void fireModelChanged( String theType ) {
        if (notifyListeners && (listeners != null)) {
            LanguageObjectEditorModelEvent event = new LanguageObjectEditorModelEvent(this, theType);

            for (int size = listeners.size(), i = 0; i < size; i++) {
                ILanguageObjectEditorModelListener listener = (ILanguageObjectEditorModelListener)listeners.get(i);
                listener.modelChanged(event);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#getLanguageObject()
     */
    public abstract LanguageObject getLanguageObject();

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#getType()
     */
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
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#hasChanged()
     */
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
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#isComplete()
     */
    public abstract boolean isComplete();

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#removeModelListener(com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener)
     */
    public boolean removeModelListener( ILanguageObjectEditorModelListener theListener ) {
        ArgCheck.isNotNull(theListener);

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
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#reset()
     */
    public void reset() {
        setLanguageObject(savedLangObj);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#save()
     */
    public void save() {
        savedLangObj = getLanguageObject();
        fireModelChanged(LanguageObjectEditorModelEvent.SAVED);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#setLanguageObject(com.metamatrix.query.sql.LanguageObject)
     */
    public void setLanguageObject( LanguageObject theLangObj ) {
        if (theLangObj != null) {
            if (!modelType.isAssignableFrom(theLangObj.getClass())) {
                Assertion.assertTrue(modelType.isAssignableFrom(theLangObj.getClass()),
                                     Util.getString(PREFIX + "wrongLangObjType", //$NON-NLS-1$
                                                    new Object[] {theLangObj.getClass().getName(), modelType.getName()}));
            }
        }

        // make sure no implicit functions get shown to user
        savedLangObj = (theLangObj == null) ? theLangObj : BuilderUtils.getBuilderLanguageObject(theLangObj);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#setType(java.lang.Class)
     */
    public void setModelType( Class theLanguageObjectClass ) {
        Assertion.isNotNull(theLanguageObjectClass, PREFIX + "nullType"); //$NON-NLS-1$
        Assertion.assertTrue(LanguageObject.class.isAssignableFrom(theLanguageObjectClass), PREFIX + "modelTypeNotLangObj"); //$NON-NLS-1$

        modelType = theLanguageObjectClass;
    }

}
