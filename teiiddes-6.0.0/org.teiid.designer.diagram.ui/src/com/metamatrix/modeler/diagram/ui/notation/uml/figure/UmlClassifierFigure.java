/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.figure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.SimpleRaisedBorder;
import org.eclipse.draw2d.Triangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.figure.AbstractDiagramFigure;
import com.metamatrix.modeler.diagram.ui.figure.ExpandableFigure;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditFigure;
import com.metamatrix.modeler.internal.diagram.ui.DebugConstants;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

/**
 * UmlClassifierFigure
 */
public class UmlClassifierFigure extends AbstractDiagramFigure implements
                                                              DirectEditFigure,
                                                              ExpandableFigure {

    // ===========================================================================================================================
    // Constants

    private static final String COLLAPSE_TOOLTIP = DiagramUiConstants.Util.getString("UmlClassifierFigure.collapseTooltip.text"); // //$NON-NLS-1$
    private static final String EXPAND_TOOLTIP = DiagramUiConstants.Util.getString("UmlClassifierFigure.expandTooltip.text"); // //$NON-NLS-1$

    // ===========================================================================================================================
    // Static Variables

    private static int ySpacing = 2;

    // ===========================================================================================================================
    // Variables

    private Triangle topLeftArrow;
    private boolean expandable = false;

    private UmlClassifierHeader header;
    private UmlClassifierFooter footer;
    private ImageFigure errorIcon;
    private ImageFigure warningIcon;
    private ImageFigure extraImage;
    private int extraImagePosition = DiagramUiConstants.Position.UPPER_RIGHT;
    private Button editButton;

    // ===========================================================================================================================
    // Constructors

    /**
     * Construct an instance of UmlClassifierFigure.
     */
    public UmlClassifierFigure(String stereotype,
                               String name,
                               String location,
                               Image icon,
                               ColorPalette colorPalette) {
        super(colorPalette);

        init(stereotype, name, location, icon);

        createComponent();
    }

    public UmlClassifierFigure(DiagramModelNode diagramNode,
                               String stereotype,
                               String name,
                               String location,
                               Image icon,
                               ColorPalette colorPalette) {
        super(diagramNode, colorPalette);

        init(stereotype, name, location, icon);

        createComponent();
    }

    // ===========================================================================================================================
    // Methods

    @Override
    public void addEditButton(Image image) {
        if (image != null) {
            if (editButton != null)
                this.remove(editButton);

            editButton = new Button(image);
            editButton.setSize(new Dimension(image.getImageData().width + 6, image.getImageData().height + 4));
            editButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    // We need to call some generic edit event here
                    // swj: yes we do! I don't like that this button knows about recursion.
                    if (getDiagramModelNode() != null) {
                        final Object modelObject = getDiagramModelNode().getModelObject();
                        final String editorID = getDiagramModelNode().getEditorID();
                        if (modelObject != null
                            && modelObject instanceof EObject
                            && ModelEditorManager.canEdit((EObject)modelObject)) {
                            Display.getCurrent().asyncExec(new Runnable() {

                                public void run() {
                                    if (editorID != null)
                                        ModelEditorManager.edit((EObject)modelObject, editorID);
                                    else
                                        ModelEditorManager.edit((EObject)modelObject);
                                }
                            });
                        }
                    }
                }
            });

            this.add(editButton);
            // firstOverlayIcon.setSize(firstOverlayIcon.getPreferredSize());
            header.setLeftButton(true);
            this.layoutFigure();
            int iX = 3 + this.getExpansionFigure().getSize().width;
            // editButton.setLocation(new Point(5, 5));
            editButton.setLocation(new Point(iX, 4));
        } else {
            if (editButton != null) {
                this.remove(editButton);
                header.setLeftButton(false);
                this.layoutFigure();
            }
            editButton = null;
        }
    }

    private void addExpandControl() {
        topLeftArrow = new Triangle();

        UmlClassifierNode ucNode = (UmlClassifierNode)getDiagramModelNode();
        if (ucNode != null && ucNode.isExpanded()) {
            // System.out.println("[UmlClassifierFigure.addExpandControl] About to set direction SOUTH");
            topLeftArrow.setDirection(PositionConstants.SOUTH);
        } else {
            // System.out.println("[UmlClassifierFigure.addExpandControl] About to set direction EAST");
            topLeftArrow.setDirection(PositionConstants.EAST);
        }
        this.add(topLeftArrow);
        topLeftArrow.setBackgroundColor(ColorConstants.black);
        topLeftArrow.setForegroundColor(ColorConstants.black);
        topLeftArrow.setLineWidth(1);
        topLeftArrow.setSize(11, 9);
        topLeftArrow.setLocation(new Point(1, 2));
        topLeftArrow.setToolTip(createToolTip(EXPAND_TOOLTIP));
        topLeftArrow.setVisible(expandable);
    }

    @Override
    public void addImage(Image image,
                         int position) {
        if (image != null) {
            if (extraImage != null)
                this.remove(extraImage);

            extraImage = new ImageFigure(image);
            this.add(extraImage);
            extraImage.setSize(extraImage.getPreferredSize());
            extraImagePosition = position;
            setExtraImageLocation();
        } else {
            if (extraImage != null)
                this.remove(extraImage);
            extraImage = null;
        }
    }

    public void collapse() {
        // System.out.println("[UmlClassifierFigure.collapse] About to set direction EAST");
        topLeftArrow.setDirection(PositionConstants.EAST);
        topLeftArrow.setBackgroundColor(ColorConstants.black);
        topLeftArrow.setToolTip(createToolTip(EXPAND_TOOLTIP));
        topLeftArrow.setSize(11, 9);
        topLeftArrow.setLocation(new Point(0, 2));
        // this.setSize(new Dimension(defWidth, defHeight));
        this.repaint();
    }

    private double containerHeightRatio(UmlClassifierContainerFigure someContainer) {
        // Num objects in this container
        int nObjects = someContainer.getContentsPane().getChildren().size();
        if (nObjects == 0)
            nObjects = 1;
        int totalObjects = 0;

        List childFigures = getChildren();
        Iterator iter = childFigures.iterator();
        Object nextObject = null;

        while (iter.hasNext()) {
            nextObject = iter.next();
            if (nextObject instanceof UmlClassifierContainerFigure) {
                totalObjects += ((UmlClassifierContainerFigure)nextObject).getContentsPane().getChildren().size();
            }
        }
        if (totalObjects == 0)
            totalObjects = 1;

        double returnRatio = (double)nObjects / (double)totalObjects;

        return returnRatio;
    }

    private void createComponent() {

        int finalWidth = header.getSize().width;
        int finalHeight = header.getSize().height + 20;

        this.setSize(finalWidth, finalHeight);
        header.setSize(finalWidth, header.getSize().height);
        header.setLocation(new Point(0, 0));
        footer.setSize(finalWidth, footer.getSize().height);
        footer.setLocation(new Point(0, header.getSize().height));

    }

    public void expand() {
        // System.out.println("[UmlClassifierFigure.expand] About to set direction SOUTH");
        topLeftArrow.setDirection(PositionConstants.SOUTH);

        topLeftArrow.setBackgroundColor(ColorConstants.blue);
        topLeftArrow.setToolTip(createToolTip(COLLAPSE_TOOLTIP));
        topLeftArrow.setSize(10, 12);
        topLeftArrow.setLocation(new Point(3, 0));
        // this.setSize(new Dimension(100, defHeight));
        this.repaint();
    }

    private IFigure getContainerFigure(int type) {
        Iterator iter = getChildren().iterator();
        Object nextObject = null;
        while (iter.hasNext()) {
            nextObject = iter.next();
            if (nextObject instanceof UmlClassifierContainerFigure) {
                int contType = ((UmlClassifierContainerFigure)nextObject).getStackOrderValue();
                if (contType == type)
                    return (IFigure)nextObject;
            }
        }
        return null;
    }

    public Button getEditButton() {
        return this.editButton;
    }

    public IFigure getExpansionFigure() {
        return this.topLeftArrow;
    }

    public Label getLabelFigure() {
        return header.getNameLabel();
    }

    private int getMinimumContainersHeight() {
        // Num objects in this container
        int totalHeight = 0;

        List childFigures = getChildren();
        Iterator iter = childFigures.iterator();
        Object nextObject = null;

        while (iter.hasNext()) {
            nextObject = iter.next();
            if (nextObject instanceof UmlClassifierContainerFigure) {
                UmlClassifierContainerFigure fig = (UmlClassifierContainerFigure)nextObject;
                if( fig.getDiagramModelNode().isHeightFixed() ) {
                    totalHeight += fig.getDiagramModelNode().getFixedHeight();
                } else {
                    totalHeight += ((UmlClassifierContainerFigure)nextObject).getMinimumHeight();
                }
            }
        }

        return totalHeight;
    }

    public IFigure getNameFigure() {
        if (header != null) {
            return header.getNameFigure();
        }

        return null;
    }

    private List getOrderedContainerFigures() {
        List returnList = new ArrayList();

        for (int i = 0; i < 4; i++) {
            IFigure containerFigure = getContainerFigure(i);
            if (containerFigure != null) {
                returnList.add(containerFigure);
            }
        }

        return returnList;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#hiliteBackground(org.eclipse.swt.graphics.Color)
     */
    @Override
    public void hiliteBackground(Color hiliteColor) {
        header.hiliteBackground(hiliteColor);
    }

    private void init(String stereotype,
                      String name,
                      String location,
                      Image icon) {
        header = new UmlClassifierHeader(stereotype, name, location, icon, getColor(ColorPalette.SECONDARY_BKGD_COLOR_ID));
        this.add(header);
        footer = new UmlClassifierFooter();
        this.add(footer);

        this.setBorder(new SimpleRaisedBorder(2));
        this.setForegroundColor(ColorConstants.darkBlue);

        this.setBackgroundColor(getColor(ColorPalette.SECONDARY_BKGD_COLOR_ID)); // DiagramUiUtilities.GROUP_BKGRND_COLOR);
        header.setDefaultBkgdColor(getColor(ColorPalette.PRIMARY_BKGD_COLOR_ID));
        footer.setDefaultBkgdColor(getColor(ColorPalette.SECONDARY_BKGD_COLOR_ID));

        addExpandControl();

        header.setExpandControl(getExpansionFigure());
    }

    public boolean isExpandable() {
        return expandable;
    }

    @Override
    public void layoutFigure() {
        // let's get all container children and stack below header.
        int minWidth = 10;

        // Let's get the header minimum size here
        Dimension minHeaderSize = header.getInitialMinimumSize();

        int currentY = header.getSize().height + header.getLocation().y;
        minWidth = Math.max(minWidth, minHeaderSize.width);

        int leftX = 0;

        List containerFigures = getOrderedContainerFigures();
        Iterator iter = containerFigures.iterator();
        Object nextObject = null;
        while (iter.hasNext()) {
            nextObject = iter.next();
            if (nextObject instanceof UmlClassifierContainerFigure) {
                ((UmlClassifierContainerFigure)nextObject).setLocation(new Point(leftX, currentY));
                currentY += ((UmlClassifierContainerFigure)nextObject).getSize().height;
                minWidth = Math.max(minWidth, ((UmlClassifierContainerFigure)nextObject).getSize().width + ySpacing * 2);
            }
        }
        footer.setLocation(new Point(0, currentY));
        currentY += footer.getSize().height;

        header.setSize(minWidth, header.getSize().height);
        header.layoutThisFigure();

        // Reset currentY Location
        currentY = header.getSize().height + header.getLocation().y;
        iter = containerFigures.iterator();
        nextObject = null;
        UmlClassifierContainerFigure nextFigure = null;
        while (iter.hasNext()) {
            nextObject = iter.next();
            if (nextObject instanceof UmlClassifierContainerFigure) {
                nextFigure = (UmlClassifierContainerFigure)nextObject;
                nextFigure.setLocation(new Point(leftX, currentY));
                currentY += nextFigure.getSize().height;
                int currentH = nextFigure.getSize().height;
                nextFigure.setSize(minWidth, currentH);
            }
        }
        footer.setSize(minWidth, footer.getSize().height);
        footer.setLocation(new Point(0, currentY));
        currentY += footer.getSize().height;
        resetIconLocations();

        this.setSize(minWidth, currentY);

        if (DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_FIGURES)) {
            String message = "new SIZE = " + this.getSize(); //$NON-NLS-1$
            DiagramUiConstants.Util.print(DebugConstants.DIAGRAM_FIGURES, message);
        }
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.pushState();
        graphics.setForegroundColor(getLocalBackgroundColor());
        graphics.setBackgroundColor(getLocalForegroundColor());
        graphics.fillGradient(this.getBounds(), true);
        super.paint(graphics);
        paintSeparators(graphics);
        graphics.popState();
        graphics.restoreState();
    }

    private void paintSeparators(Graphics graphics) {
        int orgX = this.getBounds().x;
        int orgY = this.getBounds().y;
        int width = this.getBounds().width;
        // Draw header/container separator
        int currentY = orgY;
        graphics.setLineWidth(1);
        graphics.setForegroundColor(ColorConstants.darkGray);
        List containerFigures = getOrderedContainerFigures();
        for (Iterator iter = containerFigures.iterator(); iter.hasNext();) {
            IFigure nextFigure = (Figure)iter.next();
            currentY = orgY + nextFigure.getBounds().y + 1;
            graphics.drawLine(orgX + 1, currentY, orgX + width - 2, currentY);
        }

        currentY = orgY + footer.getBounds().y + 2;
        graphics.drawLine(orgX + 1, currentY, orgX + width - 2, currentY);
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#refreshFont()
     */
    @Override
    public void refreshFont() {
        super.refreshFont();
        header.refreshFont();
        layoutFigure();
        updateForSize(this.getSize());
    }

    private void resetIconLocations() {
        if (((Label)header.getNameFigure()).getIcon() != null) {
            Rectangle iconBounds = ((Label)header.getNameFigure()).getIconBounds();
            if (warningIcon != null) {
                int newX = iconBounds.x;
                int newY = iconBounds.y + iconBounds.height - warningIcon.getBounds().height;
                warningIcon.setLocation(new Point(newX, newY));
            }
            if (errorIcon != null) {
                int newX = iconBounds.x;
                int newY = iconBounds.y + iconBounds.height - errorIcon.getBounds().height;
                errorIcon.setLocation(new Point(newX, newY));
            }
        }
        setExtraImageLocation();

    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
        topLeftArrow.setVisible(expandable);
        // jh Lyra enh / defect 20421: Now that expanded state is controlled by a user preferece
        // it no longer makes sense to unconditionally expand or collapse here
        // if( expandState )
        // expand();
        // else
        // collapse();
        this.repaint();
    }

    private void setExtraImageLocation() {
        if (extraImage != null) {
            int newX = 0;
            int newY = 0;
            int iWidth = extraImage.getSize().width;
            int iHeight = extraImage.getSize().height;
            int thisWidth = getSize().width;
            int thisHeight = getSize().height;

            switch (extraImagePosition) {
                case DiagramUiConstants.Position.UPPER_LEFT: {
                    newX = 4;
                    newY = 2;
                }
                    break;
                case DiagramUiConstants.Position.UPPER_CENTER: {
                    newX = thisWidth / 2 - iWidth / 2;
                    newY = 2;
                }
                    break;
                case DiagramUiConstants.Position.UPPER_RIGHT: {
                    newX = thisWidth - 4 - iWidth;
                    newY = 2;
                }
                    break;

                case DiagramUiConstants.Position.CENTER_LEFT: {
                    newX = 4;
                    newY = thisHeight / 2 - iHeight / 2;
                }
                    break;
                case DiagramUiConstants.Position.CENTER_CENTER: {
                    newX = thisWidth / 2 - iWidth / 2;
                    newY = thisHeight / 2 - iHeight / 2;
                }
                    break;
                case DiagramUiConstants.Position.CENTER_RIGHT: {
                    newX = thisWidth - 2 - iWidth;
                    newY = thisHeight / 2 - iHeight / 2;
                }
                    break;

                case DiagramUiConstants.Position.LOWER_LEFT: {
                    newX = 4;
                    newY = thisHeight - 2 - iHeight;
                }
                    break;
                case DiagramUiConstants.Position.LOWER_CENTER: {
                    newX = thisWidth / 2 - iWidth / 2;
                    newY = thisHeight - 2 - iHeight;
                }
                    break;
                case DiagramUiConstants.Position.LOWER_RIGHT: {
                    newX = thisWidth - 4 - iWidth;
                    newY = thisHeight - 2 - iHeight;
                }
                    break;

                default: {
                    newX = 2;
                    newY = 2;
                }
                    break;
            }

            extraImage.setLocation(new Point(newX, newY));
        }
    }

    public void setNameFontStyle(int style) {
        header.setNameFontStyle(style);
        header.refreshFont();
    }

    @Override
    public void showSelected(boolean selected) {
        if (selected)
            this.setForegroundColor(getColor(ColorPalette.SELECTION_COLOR_ID));
        else
            this.setForegroundColor(getColor(ColorPalette.SECONDARY_BKGD_COLOR_ID));
    }

    private void stackFigure() {
        // let's get all container children and stack below header.
        int currentY = header.getSize().height + header.getLocation().y;

        int leftX = 0;
        List containerFigures = getOrderedContainerFigures();
        Iterator iter = containerFigures.iterator();
        Object nextObject = null;
        while (iter.hasNext()) {
            nextObject = iter.next();
            if (nextObject instanceof UmlClassifierContainerFigure) {
                ((UmlClassifierContainerFigure)nextObject).setLocation(new Point(leftX, currentY));
                currentY += ((UmlClassifierContainerFigure)nextObject).getSize().height;
            }
        }
        footer.setLocation(new Point(0, currentY));
    }

    @Override
    public void updateForError(boolean hasErrors) {
        if (hasErrors) {
            if (errorIcon == null) {
                errorIcon = new ImageFigure(DiagramUiPlugin.getDefault().getImage(PluginConstants.Images.ERROR_ICON));
                if (errorIcon != null) {
                    this.add(errorIcon);
                    errorIcon.setSize(errorIcon.getPreferredSize());
                }
            }
        } else if (errorIcon != null) {
            this.remove(errorIcon);
            errorIcon = null;
        }
        resetIconLocations();
    }

    @Override
    public void updateForName(String newName) {
        header.refreshName(newName);
    }

    public void updateForPath(String newPath) {
        header.refreshPath(newPath);
    }

    @Override
    public void updateForSize(Dimension newSize) {
        int leftoverHeight = newSize.height;

        header.setSize(newSize.width, header.getSize().height);
        leftoverHeight -= header.getSize().height;
        header.layoutThisFigure();

        boolean scrollingNeeded = true;
        int minimumContainerHeight = getMinimumContainersHeight();
        if (minimumContainerHeight < leftoverHeight)
            scrollingNeeded = false;

        int newWidth = newSize.width;

        List childFigures = getChildren();
        Iterator iter = childFigures.iterator();
        Object nextObject = null;
        UmlClassifierContainerFigure nextFigure = null;

        if (scrollingNeeded) {
            int availableHeight = leftoverHeight - 5;
            while (iter.hasNext()) {
                nextObject = iter.next();
                if (nextObject instanceof UmlClassifierContainerFigure) {
                    nextFigure = (UmlClassifierContainerFigure)nextObject;
                    int newH = (int)(availableHeight * containerHeightRatio(nextFigure));
                    nextFigure.setSize(newWidth, newH);
                    leftoverHeight -= newH;
                }
            }
        } else {
            while (iter.hasNext()) {
                nextObject = iter.next();
                if (nextObject instanceof UmlClassifierContainerFigure) {
                    nextFigure = (UmlClassifierContainerFigure)nextObject;
                    int newH = nextFigure.getMinimumHeight();
                    if( nextFigure.getDiagramModelNode().isHeightFixed() ) {
                        newH = nextFigure.getDiagramModelNode().getFixedHeight();
                    }
                    nextFigure.setSize(newWidth, newH);
                    leftoverHeight -= newH;
                }
            }
        }

        if (leftoverHeight < 10)
            leftoverHeight = 10;
        footer.setSize(newWidth, leftoverHeight);
        stackFigure();
        resetIconLocations();
        if (DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_FIGURES)) {
            String message = "new SIZE = " + this.getSize(); //$NON-NLS-1$
            DiagramUiConstants.Util.print(DebugConstants.DIAGRAM_FIGURES, message);
        }
    }

    @Override
    public void updateForWarning(boolean hasWarnings) {
        if (hasWarnings) {
            if (warningIcon == null) {
                warningIcon = new ImageFigure(DiagramUiPlugin.getDefault().getImage(PluginConstants.Images.WARNING_ICON));
                if (warningIcon != null) {
                    this.add(warningIcon);
                    warningIcon.setSize(warningIcon.getPreferredSize());
                }
            }
        } else if (warningIcon != null) {
            this.remove(warningIcon);
            warningIcon = null;
        }
        resetIconLocations();
    }
    
    /** 
     * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
     * @since 5.0
    protected void paintFigure(Graphics theGraphics) {
        super.paintFigure(theGraphics);
        this.getParent().repaint();
    }
    */

}
