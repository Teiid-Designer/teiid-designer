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
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.sql.LanguageObject;

/**
 * The <code>CompositeLanguageObjectEditorModel</code> manages a collection of <code>ILanguageObjectEditorModel</code>s.
 */
public abstract class CompositeLanguageObjectEditorModel extends AbstractLanguageObjectEditorModel {

    /** Logging prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(CompositeLanguageObjectEditorModel.class);

    /** Event type indicating the currently selected model has changed. */
    public static final String MODEL_CHANGE = "MODEL_CHANGE"; //$NON-NLS-1$

    /** The currently selected model from the model collection. */
    private ILanguageObjectEditorModel currentModel;

    /** Listener for changes in the model collection. */
    private ModelListener listener;

    /** Collection of <code>ILanguageObjectEditorModel</code>s being managed. */
    private List models;

    /**
     * Constructs a <code>CompositeLanguageObjectEditorModel</code> that handles <code>LanguageObject</code>s of the given type.
     * 
     * @param theLanguageObjectType the type of <code>LanguageObject</code>
     */
    public CompositeLanguageObjectEditorModel( Class theLanguageObjectType ) {
        super(theLanguageObjectType);
    }

    /**
     * Adds a model to the collection of models being managed by this model. Subclasses must add models at construction time.
     */
    protected void addModel( ILanguageObjectEditorModel theModel ) {
        if (models == null) {
            models = new ArrayList();
            listener = new ModelListener();
        }

        models.add(theModel);
        theModel.addModelListener(listener);

        if (currentModel == null) {
            currentModel = theModel;
        }
    }

    /**
     * Gets the current model.
     * 
     * @return the current model
     */
    public ILanguageObjectEditorModel getCurrentModel() {
        if (models == null) {
            Assertion.isNotNull(models, Util.getString(PREFIX + "noModelsFound", //$NON-NLS-1$
                                                       new Object[] {"getCurrentModel"})); //$NON-NLS-1$
        }

        return currentModel;
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#getLanguageObject()
     */
    @Override
    public LanguageObject getLanguageObject() {
        if (models == null) {
            Assertion.isNotNull(models, Util.getString(PREFIX + "noModelsFound", //$NON-NLS-1$
                                                       new Object[] {"getLanguageObject"})); //$NON-NLS-1$
        }

        LanguageObject langObj = null;
        if (currentModel != null) {
            langObj = currentModel.getLanguageObject();
        }
        return langObj;
    }

    /**
     * Handler for when one of the models this model is managing has changed.
     * 
     * @param theEvent the event being processed
     */
    protected void handleModelChanged( LanguageObjectEditorModelEvent theEvent ) {
        Object source = theEvent.getSource();

        if (source == currentModel) {
            fireModelChanged(theEvent.getType());
        }
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel#isComplete()
     */
    @Override
    public boolean isComplete() {
        if (models == null) {
            Assertion.isNotNull(models, Util.getString(PREFIX + "noModelsFound", //$NON-NLS-1$
                                                       new Object[] {"isComplete"})); //$NON-NLS-1$
        }

        boolean complete;
        if (getCurrentModel() == null) {
            complete = false;
        } else {
            ILanguageObjectEditorModel model = getCurrentModel();
            complete = model.isComplete();
        }
        return complete;
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#save()
     */
    @Override
    public void save() {
        super.save();
        currentModel.save();

        // clear all other models
        for (int size = models.size(), i = 0; i < size; i++) {
            ILanguageObjectEditorModel model = (ILanguageObjectEditorModel)models.get(i);

            if (model != currentModel) {
                // model.clear();
            }
        }
    }

    /**
     * Sets the given model as the current model. Fires an <code>LanguageObjectEditorModelEvent</code> if the current model
     * actually changes. The previous model is cleared.
     * 
     * @param theModel the model be set as the current model
     * @throws IllegalArgumentException if parameter is <code>null</code> or is not contained in the collection of models
     */
    public void setCurrentModel( ILanguageObjectEditorModel theModel ) {
        if (models == null) {
            ArgCheck.isNotNull(models, Util.getString(PREFIX + "noModelsFound", //$NON-NLS-1$
                                                      new Object[] {"setCurrentModel"})); //$NON-NLS-1$
        }

        ArgCheck.contains(models, theModel);

        if ((currentModel == null) || (currentModel != theModel)) {

            currentModel = theModel;
            fireModelChanged(MODEL_CHANGE);
        }
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#setLanguageObject(com.metamatrix.query.sql.LanguageObject)
     */
    @Override
    public void setLanguageObject( LanguageObject theLangObj ) {
        if (models == null) {
            Assertion.isNotNull(models, Util.getString(PREFIX + "noModelsFound", //$NON-NLS-1$
                                                       new Object[] {"setLanguageObject"})); //$NON-NLS-1$
        }

        super.setLanguageObject(theLangObj);
        LanguageObject langObj = getSavedLanguageObject();
        ILanguageObjectEditorModel newModel = null;

        if (langObj == null) {
            // set to first model
            newModel = (ILanguageObjectEditorModel)models.get(0);
        } else {
            Class langObjType = langObj.getClass();

            // find sub-model that can handle this type of language object
            for (int size = models.size(), i = 0; i < size; i++) {
                ILanguageObjectEditorModel model = (ILanguageObjectEditorModel)models.get(i);

                if (model.getModelType().isAssignableFrom(langObjType)) {
                    newModel = model;
                    break;
                }
            }

            // always should have a submodel that can handle the language object type
            if (newModel == null) {
                // TODO: WE don't have a mode editor for some types like Scalar SubQuery, but we don't want to
                // Throw up too many problem dialogs....
                //                Assertion.isNotNull(newModel, Util.getString(PREFIX + "noModelFoundForLangObj", //$NON-NLS-1$
                // new Object[] {langObj.getClass().getName()}));
                return;
            }
        }

        if (newModel == currentModel) {
            currentModel.setLanguageObject(langObj);
        } else {
            newModel.setLanguageObject(langObj);
            setCurrentModel(newModel);
        }

        // the call to super did a save but this was before the language object had been set
        // on the appropriate sub-model.
        // save();
    }

    /**
     * The <code>ModelListener</code> class processes changes to any model being managed by this model.
     */
    class ModelListener implements ILanguageObjectEditorModelListener {

        /**
         * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent)
         */
        public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
            handleModelChanged(theEvent);
        }

    }

}
