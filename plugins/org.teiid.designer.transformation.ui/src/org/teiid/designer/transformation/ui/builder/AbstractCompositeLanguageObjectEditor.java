/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder;

import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.teiid.core.util.CoreArgCheck;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.ui.builder.model.CompositeLanguageObjectEditorModel;
import org.teiid.query.ui.builder.model.ILanguageObjectEditorModel;
import org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener;
import org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent;

/**
 * The <code>AbstractCompositeLanguageObjectEditor</code> manages a set of
 */
public abstract class AbstractCompositeLanguageObjectEditor extends AbstractLanguageObjectEditor {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PREFIX = "AbstractCompositeLanguageObjectEditor."; //$NON-NLS-1$

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private ViewController controller;

    private ILanguageObjectEditor currentEditor;

    private List<ILanguageObjectEditor> editors;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public AbstractCompositeLanguageObjectEditor( Composite theParent,
                                                  Class theEditorType,
                                                  CompositeLanguageObjectEditorModel theModel ) {
        super(theParent, theEditorType, theModel);

        controller = new ViewController();
        theModel.addModelListener(controller);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#acceptFocus()
     */
    @Override
    public void acceptFocus() {
        if (currentEditor != null) {
            currentEditor.acceptFocus();
        }
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#clear()
     */
    @Override
    public void clear() {
        for (int size = editors.size(), i = 0; i < size; i++) {
            ILanguageObjectEditor editor = editors.get(i);
            editor.clear();
        }
        super.clear();
        setCurrentEditor(getDefaultEditor());
    }

    /**
     * Subclasses must return a non-empty list of <code>ILanguageObjectEditor</code>s.
     * 
     * @param theParent the widget container to be used by the editor
     * @return a collection of editors
     */
    protected abstract List createEditors( Composite theParent );

    protected void displayModelChanged() {
        CompositeLanguageObjectEditorModel model = (CompositeLanguageObjectEditorModel)getModel();
        ILanguageObjectEditorModel subModel = model.getCurrentModel();
        Class modelType = subModel.getModelType();

        for (int size = editors.size(), i = 0; i < size; i++) {
            ILanguageObjectEditor editor = editors.get(i);

            if (editor.getEditorType().isAssignableFrom(modelType)) {
                setCurrentEditor(editor);
                break;
            }
        }
    }

    /**
     * Subclasses must implement {@link #createEditors(Composite)} to create their UI.
     * 
     * @throws com.metamatrix.core.util.AssertionError if editor collection is <code>null</code> or empty
     * @see org.teiid.designer.transformation.ui.builder.AbstractLanguageObjectEditor
     */
    @Override
    final protected void createUi( Composite theControl ) {
        ViewForm form = new ViewForm(theControl, SWT.BORDER);
        form.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite pnl = new Composite(form, SWT.NONE);
        form.setContent(pnl);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        pnl.setLayout(layout);
        pnl.setLayoutData(new GridData(GridData.FILL_BOTH));

        editors = createEditors(pnl);
        CoreArgCheck.isNotEmpty(editors);
    }

    /**
     * Gets the editor currently selected.
     * 
     * @return the selected editor
     */
    protected ILanguageObjectEditor getCurrentEditor() {
        return currentEditor;
    }

    /**
     * Gets the editor to use when no editor is currently selected. This is called after <code>clear()</code> is called.
     * 
     * @return the default editor
     */
    protected abstract ILanguageObjectEditor getDefaultEditor();

    /**
     * Gets the collection of editors being managed.
     * 
     * @return the editors
     */
    protected List getEditors() {
        return editors;
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

    /**
     * Sets the given editor as being the current editor.
     * 
     * @param theEditor the editor to make the current editor
     * @throws IllegalArgumentException if editor is <code>null</code> or not found in editor collection
     */
    protected void setCurrentEditor( ILanguageObjectEditor theEditor ) {
        CoreArgCheck.isNotNull(theEditor);
        CoreArgCheck.contains(editors, theEditor);

        currentEditor = theEditor;
        CompositeLanguageObjectEditorModel model = (CompositeLanguageObjectEditorModel)getModel();
        model.setCurrentModel(currentEditor.getModel());
        currentEditor.acceptFocus();
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.AbstractLanguageObjectEditor#setModel(org.teiid.query.ui.builder.model.ILanguageObjectEditorModel)
     */
    @Override
    public void setModel( ILanguageObjectEditorModel theModel ) {
        super.setModel(theModel);
        if (!CompositeLanguageObjectEditorModel.class.isAssignableFrom(theModel.getClass())) {
            CoreArgCheck.isTrue(CompositeLanguageObjectEditorModel.class.isAssignableFrom(theModel.getClass()),
                                Util.getString(PREFIX + "wrongModelType", //$NON-NLS-1$
                                               new Object[] {theModel.getClass().getName()}));
        }
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.ILanguageObjectEditor#setLanguageObject(org.teiid.query.sql.LanguageObject)
     */
    @Override
    public void setLanguageObject( LanguageObject theLanguageObject ) {
        getModel().setLanguageObject(theLanguageObject);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // ViewController INNER CLASS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The <code>ViewController</code> class is a view controller for the <code>ConstantEditor</code>.
     */
    class ViewController implements ILanguageObjectEditorModelListener {
        /* (non-Javadoc)
         * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent)
         */
        @Override
		public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
            String type = theEvent.getType();

            if (type.equals(CompositeLanguageObjectEditorModel.MODEL_CHANGE)) {
                displayModelChanged();
            }
        }
    }
}
