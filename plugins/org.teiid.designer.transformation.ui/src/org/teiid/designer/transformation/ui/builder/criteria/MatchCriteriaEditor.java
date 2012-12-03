/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder.criteria;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener;
import org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent;
import org.teiid.query.ui.builder.model.MatchCriteriaEditorModel;

/**
 * MatchCriteriaEditor
 *
 * @since 8.0
 */
public class MatchCriteriaEditor extends AbstractPredicateCriteriaTypeEditor {
    private final static String PREFIX = I18nUtil.getPropertyPrefix(MatchCriteriaEditor.class);
    private final static int ESC_CHAR_TEXT_WIDTH = (int)(Display.getCurrent().getBounds().width * .01);

    private IMatchCriteria matchCriteria;
    private CriteriaExpressionEditor leftEditor;
    private CriteriaExpressionEditor rightEditor;
    private Composite rightComponent;
    private Control leftComponent;
    private ViewController viewController;
    MatchCriteriaEditorModel theModel;
    private Text escCharText;

    public MatchCriteriaEditor( Composite parent,
                                MatchCriteriaEditorModel model ) {
        super(parent, IMatchCriteria.class, model);
        this.theModel = model;
        viewController = new ViewController();
        model.addModelListener(viewController);
        viewController.initialize();
    }

    @Override
    public String getToolTipText() {
        String tip = Util.getString(PREFIX + "toolTipText"); //$NON-NLS-1$
        return tip;
    }

    @Override
    public String getTitle() {
        String title = Util.getString(PREFIX + "title"); //$NON-NLS-1$
        return title;
    }

    @Override
	public Control createLeftComponent( Composite parent ) {
        leftEditor = new CriteriaExpressionEditor(parent, theModel.getLeftExpressionModel());
        leftComponent = leftEditor.getUi();
        return leftComponent;
    }

    @Override
	public Control createRightComponent( Composite parent ) {
        rightComponent = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        rightComponent.setLayout(layout);
        rightEditor = new CriteriaExpressionEditor(rightComponent, theModel.getRightExpressionModel());
        GridData rightEditorGridData = new GridData(GridData.FILL_BOTH);
        rightEditor.getUi().setLayoutData(rightEditorGridData);
        Composite escCharPanel = new Composite(rightComponent, SWT.NONE);
        GridLayout escLayout = new GridLayout();
        escCharPanel.setLayout(escLayout);
        escLayout.numColumns = 2;
        Label escCharLabel = new Label(escCharPanel, SWT.NONE);
        escCharLabel.setText(Util.getString(PREFIX + "escapeCharacter")); //$NON-NLS-1$
        escCharText = new Text(escCharPanel, SWT.SINGLE | SWT.BORDER);
        String escCharToolTipText = Util.getString(PREFIX + "escCharToolTipText"); //$NON-NLS-1$
        escCharText.setToolTipText(escCharToolTipText);
        escCharText.setTextLimit(1);
        escCharText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText( ModifyEvent ev ) {
                escapeCharacterModified();
            }
        });
        GridData escCharGridData = new GridData();
        escCharGridData.widthHint = ESC_CHAR_TEXT_WIDTH;
        escCharText.setLayoutData(escCharGridData);
        return rightComponent;
    }

    @Override
	public IExpression getLeftExpression() {
        IExpression leftExpression = null;
        if (matchCriteria != null) {
            leftExpression = matchCriteria.getLeftExpression();
        }
        return leftExpression;
    }

    @Override
	public IExpression getRightExpression() {
        IExpression rightExpression = null;
        if (matchCriteria != null) {
            rightExpression = matchCriteria.getRightExpression();
        }
        return rightExpression;
    }

    @Override
    public void setLanguageObject( ILanguageObject obj ) {
        CoreArgCheck.isInstanceOf(IMatchCriteria.class, obj);
        matchCriteria = (IMatchCriteria)obj;
        if (leftEditor != null) {
            leftEditor.setLanguageObject(getLeftExpression());
        }
        if (rightEditor != null) {
            rightEditor.setLanguageObject(getRightExpression());
        }
    }

    @Override
	public String[] getOperators() {
        return theModel.getOperators();
    }

    @Override
    public void acceptFocus() {
        leftEditor.acceptFocus();
    }

    @Override
	public void setOperator( String op ) {
        theModel.setCurrentOperator(op);
    }

    @Override
	public String getCurrentOperator() {
        return theModel.getCurrentOperator();
    }

    void escapeCharacterModified() {
        String textStr = escCharText.getText();
        if ((textStr != null) && (textStr.length() > 0)) {
            char newChar = escCharText.getText().charAt(0);
            theModel.setEscapeChar(newChar);
        } else {
            theModel.setEscapeChar(IMatchCriteria.NULL_ESCAPE_CHAR);
        }
    }

    void displayEscapeChar() {
        char newChar = theModel.getEscapeChar();
        String textStr = escCharText.getText();
        char oldChar;
        if ((textStr != null) && (textStr.length() > 0)) {
            oldChar = escCharText.getText().charAt(0);
        } else {
            oldChar = IMatchCriteria.NULL_ESCAPE_CHAR;
        }
        if (oldChar != newChar) {
            if (newChar != IMatchCriteria.NULL_ESCAPE_CHAR) {
                escCharText.setText(new String(new char[] {newChar}));
            } else {
                escCharText.setText(""); //$NON-NLS-1$
            }
        }
    }

    void displayLeftExpression() {
    }

    void displayRightExpression() {
    }

    void displayLanguageObjectChange() {
        displayLeftExpression();
        displayRightExpression();
        displayEscapeChar();
    }

    /**
     * The <code>ViewController</code> class is a view controller for the <code>FunctionEditor</code>.
     */
    private class ViewController implements ILanguageObjectEditorModelListener {
        public ViewController() {
            super();
        }

        public void initialize() {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
				public void run() {
                    modelChanged(new LanguageObjectEditorModelEvent(theModel, LanguageObjectEditorModelEvent.SAVED));
                }
            });
        }

        /**
         * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent)
         */
        @Override
		public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
            String type = theEvent.getType();
            if (type.equals(MatchCriteriaEditorModel.LEFT_EXPRESSION)) {
                displayLeftExpression();
            } else if (type.equals(MatchCriteriaEditorModel.RIGHT_EXPRESSION)) {
                displayRightExpression();
            } else if (type.equals(MatchCriteriaEditorModel.ESCAPE_CHAR)) {
                displayEscapeChar();
            } else if (type.equals(LanguageObjectEditorModelEvent.SAVED)) {
                displayLanguageObjectChange();
            }
        }
    }
}
