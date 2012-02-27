/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.forms;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.HyperlinkGroup;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class FormUtil {

    public static final String HTML_BEGIN = "<form><p>"; //$NON-NLS-1$
    public static final String HTML_END = "</p></form>"; //$NON-NLS-1$

    public static Section createSection( final IManagedForm managedForm,
                                         final FormToolkit toolkit,
                                         final Composite parent,
                                         String title,
                                         String description,
                                         int style,
                                         boolean shouldGiveUpVerticalSpaceWhenFolded ) {
        final Section section = toolkit.createSection(parent, style);
        section.setText(title);
        section.setDescription(description);
        section.getDescriptionControl().setFont(JFaceResources.getBannerFont());
        section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        section.clientVerticalSpacing = 5;
        section.descriptionVerticalSpacing = 5;
        section.marginHeight = 5;
        section.titleBarTextMarginWidth = 5;

        managedForm.addPart(new SectionPart(section));

        if (shouldGiveUpVerticalSpaceWhenFolded) {
            section.addExpansionListener(new ExpansionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.ui.forms.events.ExpansionAdapter#expansionStateChanged(org.eclipse.ui.forms.events.ExpansionEvent)
                 */
                @Override
                public void expansionStateChanged( ExpansionEvent e ) {
                    GridData gridData = (GridData)section.getLayoutData();
                    gridData.grabExcessVerticalSpace = e.getState();
                }
            });
        }

        section.addExpansionListener(new ExpansionAdapter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.ui.forms.events.ExpansionAdapter#expansionStateChanged(org.eclipse.ui.forms.events.ExpansionEvent)
             */
            @Override
            public void expansionStateChanged( ExpansionEvent e ) {
                managedForm.reflow(true);
            }
        });

        return section;
    }
    
    public static Button[] createSectionToolBar( Section section,
                                                 FormToolkit toolkit,
                                                 Image[] buttonImages ) {

        Composite toolBar = toolkit.createComposite(section, SWT.NONE);
        RowLayout layout = new RowLayout(SWT.HORIZONTAL);
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.spacing = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        toolBar.setLayout(layout);
        section.setTextClient(toolBar);

        final Image backgroundImage = section.getBackgroundImage();
        final Button[] buttons = new Button[buttonImages.length];
        int i = 0;

        for (Image image : buttonImages) {
            Button button = toolkit.createButton(toolBar, null, SWT.FLAT);
            button.setBackgroundImage(backgroundImage);
            button.setImage(image);
            buttons[i++] = button;
        }

        return buttons;
    }
    
	public static Button[] createSectionToolBar(Section section,
			FormToolkit toolkit, String[] actions) {

		Composite toolBar = toolkit.createComposite(section, SWT.NONE);
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.spacing = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		toolBar.setLayout(layout);
		section.setTextClient(toolBar);

		//final Image backgroundImage = section.getBackgroundImage();
		final Button[] buttons = new Button[actions.length];
		int i = 0;

		for (String text : actions) {
			Button button = toolkit.createButton(toolBar, null, SWT.FLAT);
			//button.setBackgroundImage(backgroundImage);
			button.setText(text);
			buttons[i++] = button;
		}

		return buttons;
	}

    public static boolean safeEquals( Object leftVal,
                                      Object rightVal ) {
        if (leftVal == null) {
            return rightVal == null;
        } // endif

        return leftVal.equals(rightVal);
    }

    public static boolean safeEquals( String leftVal,
                                      String rightVal,
                                      boolean treatNullAsEmpty ) {
        if (leftVal == null) {
            // left was null; right can be either null or (when treatNullAsEmpty) empty
            return rightVal == null || (treatNullAsEmpty && rightVal.length() == 0);
        } // endif

        if (leftVal.length() == 0) {
            // left was empty; right can be either empty or (when treatNullAsEmpty) null
            if (rightVal == null) {
                return treatNullAsEmpty;
            } // endif

            // right not null
            return rightVal.length() == 0;
        } // endif

        // left not null or empty, so right can't be either; just eq:
        return leftVal.equals(rightVal);
    }

    public static ScrolledForm getScrolledForm( Control c ) {
        Composite parent = c.getParent();

        while (parent != null) {
            if (parent instanceof ScrolledForm) {
                return (ScrolledForm)parent;
            } // endif
            parent = parent.getParent();
        } // endwhile

        // no scrolled form in hierarchy, return null:
        return null;
    }

    public static void tweakColors( FormToolkit ftk,
                                    Display display ) {
        ftk.refreshHyperlinkColors();
        HyperlinkGroup hlg = ftk.getHyperlinkGroup();
        if (hlg.getActiveForeground() == null) {
            hlg.setActiveForeground(display.getSystemColor(SWT.COLOR_RED));
            hlg.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
        } // endif
    }

}
