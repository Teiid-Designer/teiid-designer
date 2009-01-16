/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.webservice.ui.editor;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;

import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.ui.OverlayImageIcon;
import com.metamatrix.modeler.webservice.procedure.XsdInstanceNode;
import com.metamatrix.modeler.webservice.ui.WebServiceUiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Grid;
import com.metamatrix.ui.tree.AbstractTreeContentProvider;

/**
 * @since 5.0.1
 */
public class XPathEditorPanel extends Grid implements
                                          IInternalUiConstants.Images {

    // ===========================================================================================================================
    // Constants

    private static final ILabelProvider LABEL_PROVIDER = ModelUtilities.getEMFLabelProvider();

    private static final WebServiceUiPlugin PLUGIN = WebServiceUiPlugin.getDefault();

    // ===========================================================================================================================
    // Variables

    private XsdInstanceNode root;
    private TextViewer textViewer;
    private Shell assistShell;

    // ===========================================================================================================================
    // Constructors

    /**
     * @param parent
     * @since 5.0.1
     */
    public XPathEditorPanel(Composite parent,
                            XSDElementDeclaration element) {
        super(parent, SWT.NO_TRIM);
        this.root = new XsdInstanceNode(element);
        getGridData().grabExcessHorizontalSpace = getGridData().grabExcessVerticalSpace = true;
        this.textViewer = new TextViewer(this, SWT.BORDER);
        this.textViewer.setDocument(new Document());
        StyledText textBox = this.textViewer.getTextWidget();
        textBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        textBox.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent event) {
                keyPressedInEditor(event);
            }
        });
    }

    // ===========================================================================================================================
    // Methods

    Control createInputSchemaTree(Shell shell) {
        final TreeViewer treeViewer = WidgetFactory.createTreeViewer(shell);
        treeViewer.setContentProvider(new AbstractTreeContentProvider() {

            @Override
            public Object[] getChildren(Object element) {
                return getNodeChildren(element);
            }

            public Object getParent(Object element) {
                return getNodeParent(element);
            }

            @Override
            public boolean hasChildren(Object element) {
                return getNodeHasChildren(element);
            }
        });
        treeViewer.setLabelProvider(new LabelProvider() {

            @Override
            public Image getImage(Object element) {
                return getNodeImage(element);
            }

            @Override
            public String getText(Object element) {
                return getNodeName(element);
            }
        });
        treeViewer.getTree().addTraverseListener(new TraverseListener() {
        
            public void keyTraversed(TraverseEvent event) {
                if (event.keyCode == SWT.ESC) {
                    event.doit = false;
                    ((Tree)event.widget).getShell().setVisible(false);
                }
            }
        
        });
        treeViewer.setInput(this.root);
        return treeViewer.getTree();
    }

    private int getMaxOccurs(XsdInstanceNode node) {
        XSDConcreteComponent comp = node.getXsdComponent();
        if (comp instanceof XSDParticle) {
            int max = ((XSDParticle)comp).getMaxOccurs();
            return (max > 0 ? max : Integer.MAX_VALUE - 1); // Integer.MAX_VALUE - 1 is max for Spinner wrapping to work
        }
        return 1;
    }

    Object[] getNodeChildren(Object element) {
        return ((XsdInstanceNode)element).getChildren();
    }

    boolean getNodeHasChildren(Object element) {
        XsdInstanceNode node = (XsdInstanceNode)element;
        Object[] children = node.getChildren();
        // Children created lazily, so in case one of node's children dynamically determined to be recursive, update any recursive
        // ancestors.
        for (XsdInstanceNode ancestor = node.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor.isRecursive()) {
                // this.treeViewer.update(ancestor, null);
            }
        }
        return (children.length > 0);
    }

    Image getNodeImage(Object element) {
        // There is an EMF bug that prevents maxOccurs values other than 1 from being stored correctly for particles with model
        // group definition content, so just show image of underlying model group
        XsdInstanceNode node = (XsdInstanceNode)element;
        XSDConcreteComponent comp = node.getXsdComponent();
        Image img;
        if (comp instanceof XSDParticle) {
            XSDParticle particle = (XSDParticle)comp;
            // Start with image of "real" component
            img = LABEL_PROVIDER.getImage(particle.getTerm());
            // If non-default minOccurs/maxOccurs, overlay occurs image
            int min = particle.getMinOccurs();
            int max = particle.getMaxOccurs();
            if (min != 1 || max != 1) {
                Image overlay;
                if (min == 0) {
                    if (max == 0) {
                        overlay = PLUGIN.getImage(OCCURS_ZERO);
                    } else if (max == 1) {
                        overlay = PLUGIN.getImage(OCCURS_ZERO_TO_ONE);
                    } else if (max > 1) {
                        overlay = PLUGIN.getImage(OCCURS_ZERO_TO_N);
                    } else { // Must be unbounded
                        overlay = PLUGIN.getImage(OCCURS_ZERO_TO_UNBOUNDED);
                    }
                } else if (min == 1) {
                    if (max > 1) {
                        overlay = PLUGIN.getImage(OCCURS_ONE_TO_N);
                    } else { // Must be unbounded
                        overlay = PLUGIN.getImage(OCCURS_ONE_TO_UNBOUNDED);
                    }
                } else { // Min > 1
                    if (max == min) {
                        overlay = PLUGIN.getImage(OCCURS_N);
                    } else if (max > min) {
                        overlay = PLUGIN.getImage(OCCURS_N_TO_M);
                    } else { // Must be unbounded
                        overlay = PLUGIN.getImage(OCCURS_N_TO_UNBOUNDED);
                    }
                }
                img = new OverlayImageIcon(img, overlay, OverlayImageIcon.BOTTOM_LEFT).getImage();
            }
        } else {
            img = LABEL_PROVIDER.getImage(comp);
        }
        // If recursive, overlay recursive image
        if (node.isRecursive()) {
            return new OverlayImageIcon(img, PLUGIN.getImage(RECURSIVE), OverlayImageIcon.TOP_RIGHT).getImage();
        }
        return img;
    }

    String getNodeName(Object element) {
        XsdInstanceNode node = (XsdInstanceNode)element;
        XSDConcreteComponent comp = node.getXsdComponent();
        if (comp instanceof XSDParticle) {
            comp = ((XSDParticle)comp).getTerm();
        }
        StringBuffer name = new StringBuffer(LABEL_PROVIDER.getText(comp));
        node.getChildren(); // Called to ensure subsequent call to hasSelectableChildren is accurate
        boolean editingApplicable = (node.hasSelectableChildren() || node.isSelected());
        if (!editingApplicable && node.isRecursive()) {
            editingApplicable = node.findRecursionRoot().hasSelectableChildren();
        }
        if (getMaxOccurs(node) > 1 && editingApplicable) {
            name.append(" ["); //$NON-NLS-1$
//            name.append(node.getOccurrence() + 1);
            name.append(']');
        }
        if (node.isRecursive() && editingApplicable) {
            name.append(" >"); //$NON-NLS-1$
//            name.append(node.getDepth() + 1);
        }
        return name.toString();
    }

    Object getNodeParent(Object element) {
        return ((XsdInstanceNode)element).getParent();
    }

    void keyPressedInEditor(KeyEvent event) {
        if (event.stateMask == SWT.CTRL && event.character == ' ') {
            if (this.assistShell == null) {
                this.assistShell = new Shell(getShell(), SWT.ON_TOP | SWT.RESIZE);
                GridLayout layout = new GridLayout();
                layout.marginHeight = layout.marginWidth = 0;
                this.assistShell.setLayout(layout);
                Control ctrl = createInputSchemaTree(this.assistShell);
                ctrl.setBackground(ctrl.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                this.assistShell.pack();
                StyledText textBox = this.textViewer.getTextWidget();
                Point loc = textBox.getCaret().getLocation();
                this.assistShell.setLocation(textBox.toDisplay(loc.x, loc.y + textBox.getLineHeight()));
                this.assistShell.open();
            } else {
                this.assistShell.setVisible(true);
            }
        }
    }
}
