/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.views.DSPAdvisorI18n;

/**
 * 
 */
public class AdvisorFixDialog {
    private Color backgroundColour = null;

    private Color foregroundColour = null;

    // private Color linkColour = null;

    protected Shell parentShell;

    protected Shell shell;

    protected String infopopText;

    protected FormToolkit toolkit;

    private static final String FIVE_SPACES = "     "; //$NON-NLS-1$

    /**
     * Constructor:
     * 
     * @param x the x mouse location in the current display
     * @param y the y mouse location in the current display
     */
    public AdvisorFixDialog( InfoPopAction[] actions,
                             int x,
                             int y,
                             FormToolkit toolkit ) {

        Display display = Display.getCurrent();
        if (display == null) {
            return;
        }
        this.toolkit = toolkit;

        backgroundColour = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
        foregroundColour = display.getSystemColor(SWT.COLOR_INFO_FOREGROUND);
        // linkColour = display.getSystemColor(SWT.COLOR_BLUE);
        parentShell = display.getActiveShell();

        shell = new Shell(parentShell, SWT.NONE);

        // PlatformUI.getWorkbench().getHelpSystem().setHelp(shell, IHelpUIConstants.F1_SHELL);

        shell.addListener(SWT.Deactivate, new Listener() {
            public void handleEvent( Event e ) {
                close();
            }
        });

        shell.addTraverseListener(new TraverseListener() {
            public void keyTraversed( TraverseEvent e ) {
                if (e.detail == SWT.TRAVERSE_ESCAPE) {
                    e.doit = true;
                }
            }
        });

        shell.addControlListener(new ControlAdapter() {
            @Override
            public void controlMoved( ControlEvent e ) {
                Rectangle clientArea = shell.getClientArea();
                shell.redraw(clientArea.x, clientArea.y, clientArea.width, clientArea.height, true);
                shell.update();
            }
        });

        // linkManager.setHyperlinkUnderlineMode(HyperlinkHandler.UNDERLINE_ALWAYS);
        createContents(shell, actions);
        shell.pack();
        // Correct x and y of the shell if it not contained within the screen
        int width = shell.getBounds().width;
        int height = shell.getBounds().height;

        Rectangle screen = display.getClientArea();
        // check lower boundaries
        // Add 10 pixels...
        x += 10;

        x = x >= screen.x ? x : screen.x;
        y = y >= screen.y ? y : screen.y;
        // check upper boundaries
        x = x + width <= screen.width ? x : screen.width - width;
        y = y + height <= screen.height ? y : screen.height - height;
        shell.setLocation(x, y);

        initAccessible(shell);
    }

    public synchronized void close() {
        try {
            if (shell != null) {
                shell.close();
                if (!shell.isDisposed()) shell.dispose();
                shell = null;
            }
        } catch (Throwable ex) {
        }
    }

    protected Control createContents( Composite contents,
                                      InfoPopAction[] actions ) {
        initAccessible(contents);
        contents.setBackground(backgroundColour);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 5;
        layout.marginWidth = 5;
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(GridData.FILL_BOTH));
        // create the dialog area and button bar
        if (actions.length == 0) {
            createInfoArea(contents, actions.length);
        }
        if (actions.length > 0) {
            Control c = createLinksArea(contents, actions);
            if (c != null) {
                // links exist, make them the only focusable controls
                contents.setTabList(new Control[] {c});
            }
        }
        return contents;
    }

    private Control createInfoArea( Composite parent,
                                    int nActions ) {
        // Create the text field.
        String styledText;
        if (nActions == 0) {
            styledText = DSPAdvisorI18n.AdvisorFixDialog_Message_NoActions;
        } else {
            styledText = DSPAdvisorI18n.AdvisorFixDialog_Message_AvailableActions;
        }

        Description text = new Description(parent, SWT.MULTI | SWT.READ_ONLY);
        text.addTraverseListener(new TraverseListener() {
            public void keyTraversed( TraverseEvent e ) {
                if (e.detail == SWT.TRAVERSE_ESCAPE) {
                    e.doit = true;
                }
            }
        });

        text.getCaret().setVisible(false);
        text.setBackground(backgroundColour);
        text.setForeground(foregroundColour);
        text.setFont(parent.getFont());
        int linkWidth = getLinksWidth(text);
        StyledLineWrapper content = new StyledLineWrapper(styledText, text, linkWidth + 70);
        text.setContent(content);
        text.setStyleRanges(content.getStyles());

        infopopText = text.getText();
        initAccessible(text);

        return text;
    }

    /**
     * Measures the longest label of related links
     * 
     * @param text
     * @return
     */
    private int getLinksWidth( Description text ) {
        int linkWidth = 200;

        return linkWidth;
    }

    private Control createLinksArea( Composite parent,
                                     InfoPopAction[] actions ) {
        // IHelpResource[] relatedTopics = context.getRelatedTopics();
        // if (relatedTopics == null)
        // return null;
        // Create control
        Composite composite = new Composite(parent, SWT.NONE);
        initAccessible(composite);

        // find out if any actions are TYPE_FIX, and/or TYPE_DO
        boolean hasFixes = false;
        boolean hasDos = false;
        boolean hasOthers = false;
        for (int i = 0; i < actions.length; i++) {
            if (!hasFixes && actions[i].getType() == InfoPopAction.TYPE_FIX) {
                hasFixes = true;
            }
            if (!hasDos && actions[i].getType() == InfoPopAction.TYPE_DO) {
                hasDos = true;
            }
            if (!hasOthers && actions[i].getType() == InfoPopAction.TYPE_OTHER) {
            	hasOthers = true;
            }
        }

        composite.setBackground(backgroundColour);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 2;
        layout.marginWidth = 0;
        layout.verticalSpacing = 3;
        layout.horizontalSpacing = 2;
        layout.numColumns = 3;
        composite.setLayout(layout);
        composite.setFont(parent.getFont());
        GridData data = new GridData(GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
        composite.setLayoutData(data);
        if (hasFixes) {
        	
        	addActions(composite, actions, InfoPopAction.TYPE_FIX);
        	
            // Create separator.
            // Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
            // label.setBackground(backgroundColour);
            // label.setForeground(foregroundColour);
            // data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING
            // | GridData.FILL_HORIZONTAL);
            // data.horizontalSpan = 3;
            // label.setLayoutData(data);

//            for (int i = 0; i < actions.length; i++) {
//                if (actions[i].getType() == InfoPopAction.TYPE_FIX) {
//                    if (actions[i].getDescription() != null) {
//                        // Add description here
//                        Label desc = new Label(composite, SWT.FILL);
//                        desc.setBackground(backgroundColour);
//                        desc.setForeground(foregroundColour);
//                        desc.setText(actions[i].getDescription());
//                        GridData gData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING
//                                                      | GridData.FILL_HORIZONTAL);
//                        gData.horizontalSpan = 3;
//                        desc.setLayoutData(gData);
//                    }
//                    // Indent the action.
//                    Label spacerLabel = new Label(composite, SWT.NONE);
//                    spacerLabel.setText(FIVE_SPACES);
//
//                    Label imageLabel = new Label(composite, SWT.NONE);
//                    imageLabel.setBackground(backgroundColour);
//                    imageLabel.setForeground(foregroundColour);
//                    if (actions[i].getImage() != null) {
//                        imageLabel.setImage(actions[i].getImage());
//                    }
//                    final Hyperlink actionLink = toolkit.createHyperlink(composite, actions[i].getAction().getText(), SWT.WRAP);
//                    toolkit.adapt(actionLink, true, true);
//                    actionLink.setBackground(backgroundColour);
//                    final IAction nextAction = actions[i].getAction();
//                    actionLink.setToolTipText(actions[i].getDescription());
//                    actionLink.addHyperlinkListener(new HyperlinkAdapter() {
//                        @Override
//                        public void linkActivated( HyperlinkEvent e ) {
//                            nextAction.run();
//                            close();
//                        }
//                    });
//                }
//            }
        }
        if (hasDos) {
            // Create separator.
            if (hasFixes) {
                Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
                label.setBackground(backgroundColour);
                label.setForeground(foregroundColour);
                data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING
                                    | GridData.FILL_HORIZONTAL);
                data.horizontalSpan = 3;
                label.setLayoutData(data);
            }
            
            addActions(composite, actions, InfoPopAction.TYPE_DO);

//            for (int i = 0; i < actions.length; i++) {
//                if (actions[i].getType() == InfoPopAction.TYPE_DO) {
//                    if (actions[i].getDescription() != null) {
//                        // Add description here
//                        Label desc = new Label(composite, SWT.FILL);
//                        desc.setBackground(backgroundColour);
//                        desc.setForeground(foregroundColour);
//                        desc.setText(actions[i].getDescription());
//                        GridData gData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING
//                                                      | GridData.FILL_HORIZONTAL);
//                        gData.horizontalSpan = 3;
//                        desc.setLayoutData(gData);
//                    }
//                    // Indent the action.
//                    Label spacerLabel = new Label(composite, SWT.NONE);
//                    spacerLabel.setText(FIVE_SPACES);
//
//                    Label imageLabel = new Label(composite, SWT.NONE);
//                    imageLabel.setBackground(backgroundColour);
//                    imageLabel.setForeground(foregroundColour);
//                    if (actions[i].getImage() != null) {
//
//                        imageLabel.setImage(actions[i].getImage());
//                    }
//                    final Hyperlink actionLink = toolkit.createHyperlink(composite, actions[i].getAction().getText(), SWT.WRAP);
//                    toolkit.adapt(actionLink, true, true);
//                    actionLink.setBackground(backgroundColour);
//                    final IAction nextAction = actions[i].getAction();
//                    actionLink.setToolTipText(actions[i].getDescription());
//                    actionLink.addHyperlinkListener(new HyperlinkAdapter() {
//                        @Override
//                        public void linkActivated( HyperlinkEvent e ) {
//                            nextAction.run();
//                            close();
//                        }
//                    });
//                }
//            }
        }
        
        if (hasOthers) {
            // Create separator.
            if (hasDos || hasFixes) {
                Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
                label.setBackground(backgroundColour);
                label.setForeground(foregroundColour);
                data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING
                                    | GridData.FILL_HORIZONTAL);
                data.horizontalSpan = 3;
                label.setLayoutData(data);
            }
            
            addActions(composite, actions, InfoPopAction.TYPE_OTHER);

//            for (int i = 0; i < actions.length; i++) {
//                if (actions[i].getType() == InfoPopAction.TYPE_OTHER) {
//                    if (actions[i].getDescription() != null) {
//                        // Add description here
//                        Label desc = new Label(composite, SWT.FILL);
//                        desc.setBackground(backgroundColour);
//                        desc.setForeground(foregroundColour);
//                        desc.setText(actions[i].getDescription());
//                        GridData gData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING
//                                                      | GridData.FILL_HORIZONTAL);
//                        gData.horizontalSpan = 3;
//                        desc.setLayoutData(gData);
//                    }
//                    // Indent the action.
//                    Label spacerLabel = new Label(composite, SWT.NONE);
//                    spacerLabel.setText(FIVE_SPACES);
//
//                    Label imageLabel = new Label(composite, SWT.NONE);
//                    imageLabel.setBackground(backgroundColour);
//                    imageLabel.setForeground(foregroundColour);
//                    if (actions[i].getImage() != null) {
//
//                        imageLabel.setImage(actions[i].getImage());
//                    }
//                    final Hyperlink actionLink = toolkit.createHyperlink(composite, actions[i].getAction().getText(), SWT.WRAP);
//                    toolkit.adapt(actionLink, true, true);
//                    actionLink.setBackground(backgroundColour);
//                    final IAction nextAction = actions[i].getAction();
//                    actionLink.setToolTipText(actions[i].getDescription());
//                    actionLink.addHyperlinkListener(new HyperlinkAdapter() {
//                        @Override
//                        public void linkActivated( HyperlinkEvent e ) {
//                            nextAction.run();
//                            close();
//                        }
//                    });
//                }
//            }
        }

        // Create separator.
        // Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        // label.setBackground(backgroundColour);
        // label.setForeground(foregroundColour);
        // data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
        // | GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
        // data.horizontalSpan = 3;
        // label.setLayoutData(data);
        // create link to the dynamic help
        // createDynamicHelpLink(composite);

        return composite;
    }
    
    private void addActions(Composite parent, InfoPopAction[] actions, int actionType) {
        for (int i = 0; i < actions.length; i++) {
            if (actions[i].getType() == actionType) {
                if (actions[i].getDescription() != null) {
                    // Add description here
                    Label desc = new Label(parent, SWT.FILL);
                    desc.setBackground(backgroundColour);
                    desc.setForeground(foregroundColour);
                    desc.setText(actions[i].getDescription());
                    GridData gData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING
                                                  | GridData.FILL_HORIZONTAL);
                    gData.horizontalSpan = 3;
                    desc.setLayoutData(gData);
                }
                // Indent the action.
                Label spacerLabel = new Label(parent, SWT.NONE);
                spacerLabel.setText(FIVE_SPACES);

                Label imageLabel = new Label(parent, SWT.NONE);
                imageLabel.setBackground(backgroundColour);
                imageLabel.setForeground(foregroundColour);
                if (actions[i].getImage() != null) {

                    imageLabel.setImage(actions[i].getImage());
                }
                final Hyperlink actionLink = toolkit.createHyperlink(parent, actions[i].getAction().getText(), SWT.WRAP);
                toolkit.adapt(actionLink, true, true);
                actionLink.setBackground(backgroundColour);
                final IAction nextAction = actions[i].getAction();
                actionLink.setToolTipText(actions[i].getDescription());
                actionLink.addHyperlinkListener(new HyperlinkAdapter() {
                    @Override
                    public void linkActivated( HyperlinkEvent e ) {
                        nextAction.run();
                        close();
                    }
                });
            }
        }
    }

    public synchronized void open() {
        try {
            shell.open();
        } catch (Throwable e) {
        	AdvisorUiConstants.UTIL.log(IStatus.ERROR, e, "An error occurred when opening context-sensitive help pop-up."); //$NON-NLS-1$

        }
    }

    // private Image getImage() {
    // return HelpUIResources.getImage(IHelpUIConstants.IMAGE_FILE_F1TOPIC);
    // }

    public boolean isShowing() {
        return (shell != null && !shell.isDisposed() && shell.isVisible());
    }

    private void initAccessible( final Control control ) {
        Accessible accessible = control.getAccessible();
        accessible.addAccessibleListener(new AccessibleAdapter() {
            @Override
            public void getName( AccessibleEvent e ) {
                e.result = infopopText;
            }

            @Override
            public void getHelp( AccessibleEvent e ) {
                e.result = control.getToolTipText();
            }
        });

        accessible.addAccessibleControlListener(new AccessibleControlAdapter() {
            @Override
            public void getChildAtPoint( AccessibleControlEvent e ) {
                Point pt = control.toControl(new Point(e.x, e.y));
                e.childID = (control.getBounds().contains(pt)) ? ACC.CHILDID_MULTIPLE : ACC.CHILDID_NONE;
            }

            @Override
            public void getLocation( AccessibleControlEvent e ) {
                Rectangle location = control.getBounds();
                Point pt = control.toDisplay(new Point(location.x, location.y));
                e.x = pt.x;
                e.y = pt.y;
                e.width = location.width;
                e.height = location.height;
            }

            @Override
            public void getChildCount( AccessibleControlEvent e ) {
                e.detail = 1;
            }

            @Override
            public void getRole( AccessibleControlEvent e ) {
                e.detail = ACC.ROLE_LABEL;
            }

            @Override
            public void getState( AccessibleControlEvent e ) {
                e.detail = ACC.STATE_READONLY;
            }
        });
    }

    public class Description extends StyledText {
        /**
         * @param parent
         * @param style
         */
        public Description( Composite parent,
                            int style ) {
            super(parent, style);
        }

        @Override
        public boolean setFocus() {
            return false;
        }

        @Override
        public boolean isFocusControl() {
            return false;
        }
    }
}
