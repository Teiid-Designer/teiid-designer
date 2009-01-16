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
package com.metamatrix.modeler.relationship.ui.figure;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.SimpleRaisedBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFont;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.figure.AbstractDiagramFigure;
import com.metamatrix.modeler.diagram.ui.figure.LabeledRectangleFigure;
import com.metamatrix.modeler.diagram.ui.util.ToolTipUtil;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditFigure;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.custom.CustomDiagramModelFactory;
import com.metamatrix.modeler.relationship.ui.diagram.RelationshipDiagramUtil;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;

public class RelationshipNodeFigure extends AbstractDiagramFigure implements DirectEditFigure {
    private ImageFigure errorIcon;
    private ImageFigure warningIcon;
    private boolean objectHasIcon = true;

    private LabeledRectangleFigure typeLabel;
    private LabeledRectangleFigure nameLabel;
    private LabeledRectangleFigure typePrefixLabel;
    private LabeledRectangleFigure stereotypeLabel;
    private LabeledRectangleFigure sourceRoleLabel;
    private LabeledRectangleFigure targetRoleLabel;
    private HeaderFigure header;
    private RectangleFigure sourceRoleRectangle;
    private RectangleFigure targetRoleRectangle;
    private static final int ySpacing = 2;
    private static final int xInset = 6;
    private int maxRoleWidth = 1;
    private Color defaultBkgdColor;

    Button editButton;
    private Button restoreButton;
    CustomDiagramModelFactory factory;

    public RelationshipNodeFigure( String name,
                                   Image nameIcon,
                                   String type,
                                   String sourceRole,
                                   Image sourceIcon,
                                   String targetRole,
                                   Image targetIcon,
                                   ColorPalette colorPalette ) {
        super(colorPalette);

        objectHasIcon = false;
        init(name, nameIcon, type, sourceRole, sourceIcon, targetRole, targetIcon, colorPalette);

        createComponent();
    }

    private void init( String name,
                       Image nameIcon,
                       String type,
                       String sourceRole,
                       Image sourceIcon,
                       String targetRole,
                       Image targetIcon,
                       ColorPalette colorPalette ) {

        header = new HeaderFigure();
        sourceRoleRectangle = new RectangleFigure();
        targetRoleRectangle = new RectangleFigure();

        this.add(header);
        this.add(sourceRoleRectangle);
        this.add(targetRoleRectangle);

        this.setBorder(new SimpleRaisedBorder(4));
        this.setForegroundColor(getColor(ColorPalette.SECONDARY_BKGD_COLOR_ID));

        this.setBackgroundColor(getColor(ColorPalette.PRIMARY_BKGD_COLOR_ID));
        header.setBackgroundColor(getColor(ColorPalette.PRIMARY_BKGD_COLOR_ID));

        header.setLineWidth(0);
        header.setForegroundColor(getColor(ColorPalette.SECONDARY_BKGD_COLOR_ID));
        header.setBackgroundColor(getColor(ColorPalette.PRIMARY_BKGD_COLOR_ID));

        stereotypeLabel = new LabeledRectangleFigure("<<Relationship>>", true, colorPalette); //$NON-NLS-1$

        stereotypeLabel.setTextColor(ColorConstants.darkGray);
        header.add(stereotypeLabel);

        if (type != null) {
            if (typePrefixLabel == null) {
                typePrefixLabel = new LabeledRectangleFigure("Type: ", true, colorPalette); //$NON-NLS-1$
                header.add(typePrefixLabel);
            }
            if (typeLabel == null) {
                typeLabel = new LabeledRectangleFigure(type, true, colorPalette);
                header.add(typeLabel);
            } else {
                typeLabel.getLabel().setText(type);
            }
            typePrefixLabel.setTextColor(ColorConstants.darkGray);
            typeLabel.setTextColor(ColorConstants.darkGray);
            typeLabel.layoutFigure();
            typePrefixLabel.layoutFigure();
        }
        if (name != null) {
            if (nameLabel == null) {
                if (nameIcon != null) nameLabel = new LabeledRectangleFigure(name, nameIcon, true, colorPalette);
                else nameLabel = new LabeledRectangleFigure(name, true, null);
                header.add(nameLabel);
            } else {
                nameLabel.getLabel().setText(name);
            }
            nameLabel.layoutFigure();
        }
        if (sourceIcon != null) sourceRoleLabel = new LabeledRectangleFigure(sourceRole, sourceIcon, false, null);
        else sourceRoleLabel = new LabeledRectangleFigure(sourceRole, false, null);

        sourceRoleRectangle.add(sourceRoleLabel);
        sourceRoleRectangle.setBackgroundColor(getColor(ColorPalette.PRIMARY_BKGD_COLOR_ID));
        sourceRoleLabel.layoutFigure();

        if (targetIcon != null) targetRoleLabel = new LabeledRectangleFigure(targetRole, targetIcon, false, null);
        else targetRoleLabel = new LabeledRectangleFigure(targetRole, false, null);

        targetRoleRectangle.add(targetRoleLabel);
        targetRoleRectangle.setBackgroundColor(getColor(ColorPalette.PRIMARY_BKGD_COLOR_ID));
        targetRoleLabel.layoutFigure();

        refreshFont();
    }

    private void createComponent() {
        setInitialSize();
        if (stereotypeLabel != null) stereotypeLabel.setBackgroundColor(this.getBackgroundColor());
        if (typeLabel != null) typeLabel.setBackgroundColor(this.getBackgroundColor());
        if (nameLabel != null) nameLabel.setBackgroundColor(this.getBackgroundColor());
        if (typePrefixLabel != null) typePrefixLabel.setBackgroundColor(this.getBackgroundColor());
        if (sourceRoleLabel != null) sourceRoleLabel.setBackgroundColor(this.getBackgroundColor());
        if (targetRoleLabel != null) targetRoleLabel.setBackgroundColor(this.getBackgroundColor());
    }

    private void setInitialSize() {
        int maxWidth = ySpacing;
        int maxHeight = ySpacing;
        int deltaHeight = ySpacing;
        int editButtonWidth = 0;
        int restoreButtonWidth = 0;
        if (editButton != null) editButtonWidth = editButton.getSize().width;
        if (restoreButton != null) restoreButtonWidth = restoreButton.getSize().width;

        if (stereotypeLabel != null) {
            deltaHeight += stereotypeLabel.getBounds().height;
            maxWidth = Math.max(maxWidth, (stereotypeLabel.getSize().width + editButtonWidth + restoreButtonWidth));
        }

        if (nameLabel != null) {
            deltaHeight += nameLabel.getBounds().height + ySpacing;
            maxWidth = Math.max(maxWidth, (nameLabel.getSize().width + editButtonWidth + restoreButtonWidth));
        }

        if (typeLabel != null) {
            deltaHeight += typeLabel.getBounds().height + ySpacing;
            if (typePrefixLabel != null) maxWidth = Math.max(maxWidth, typeLabel.getSize().width
                                                                       + typePrefixLabel.getSize().width);
            else maxWidth = Math.max(maxWidth, typeLabel.getSize().width);
        }

        deltaHeight += ySpacing;

        if (sourceRoleLabel != null && targetRoleLabel != null) {
            deltaHeight += sourceRoleLabel.getBounds().height;
            maxRoleWidth = Math.max(sourceRoleLabel.getSize().width, targetRoleLabel.getSize().width);

            maxWidth = Math.max(maxWidth, (2 * maxRoleWidth));
        }

        maxWidth += xInset * 4;
        maxHeight = Math.max(maxHeight, deltaHeight);
        maxHeight += 6;

        this.setSize(new Dimension(maxWidth, maxHeight));
        layoutFigure();
    }

    @Override
    protected boolean useLocalCoordinates() {
        return true;
    }

    @Override
    public void layoutFigure() {
        // Need to resize package to fit header
        int tempX = xInset;

        int currentY = ySpacing * 2;

        if (stereotypeLabel != null) {
            stereotypeLabel.setLocation(new Point(this.getBounds().width / 2 - stereotypeLabel.getBounds().width / 2, currentY));
            currentY += stereotypeLabel.getBounds().height;
        }

        if (nameLabel != null) {
            nameLabel.setLocation(new Point(xInset, currentY));
            currentY += nameLabel.getBounds().height;
        }

        if (typeLabel != null) {
            typePrefixLabel.setLocation(new Point(xInset, currentY));
            typeLabel.setLocation(new Point(typePrefixLabel.getLocation().x + typePrefixLabel.getSize().width, currentY));
            currentY += typeLabel.getBounds().height + ySpacing;
        }
        currentY += ySpacing * 2;

        int headerHeight = currentY;

        if (sourceRoleLabel != null) {
            sourceRoleLabel.setLocation(new Point(xInset, currentY));
        }
        if (targetRoleLabel != null) {
            tempX = this.getSize().width / 2;
            targetRoleLabel.setLocation(new Point(tempX + xInset, currentY));
        }
        currentY += ySpacing;

        header.setSize(this.getSize().width, headerHeight);
        sourceRoleRectangle.setLocation(new Point(0, headerHeight));
        sourceRoleRectangle.setSize(tempX, this.getSize().height - headerHeight);
        targetRoleRectangle.setLocation(new Point(tempX, headerHeight));
        targetRoleRectangle.setSize(this.getSize().width - tempX, this.getSize().height - headerHeight);

        resetIconLocations();
    }

    private void resetIconLocations() {
        if (objectHasIcon) {
            if (errorIcon != null) {
                errorIcon.setLocation(new Point(nameLabel.getLocation().x, nameLabel.getLocation().y));
            }
            if (warningIcon != null) {
                warningIcon.setLocation(new Point(nameLabel.getLocation().x, nameLabel.getLocation().y));
            }
        }
        setButtonLocation();
    }

    @Override
    public void updateForSize( Dimension newSize ) {
        resetIconLocations();
        // layoutFigure();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#updateForName(java.lang.String)
     */
    @Override
    public void updateForName( String newName ) {
        if (nameLabel != null) {
            nameLabel.updateForName(newName);
            setInitialSize();
            layoutFigure();
        }
    }

    public void updateForChange( String newType,
                                 String sourceRole,
                                 String targetRole ) {
        if (typeLabel != null) typeLabel.updateForName(newType);
        if (sourceRoleLabel != null) sourceRoleLabel.updateForName(sourceRole);
        if (targetRoleLabel != null) targetRoleLabel.updateForName(targetRole);
        setInitialSize();
        layoutFigure();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#refreshFont()
     */
    @Override
    public void refreshFont() {
        Font boldFont = ScaledFontManager.getFont(ScaledFont.BOLD_STYLE);
        Font smallerFont = getSmallerFont();

        if (stereotypeLabel != null) stereotypeLabel.updateForFont(smallerFont);
        if (typeLabel != null) typeLabel.updateForFont(smallerFont);
        if (typePrefixLabel != null) typePrefixLabel.updateForFont(smallerFont);
        if (nameLabel != null) nameLabel.updateForFont(boldFont);
        if (sourceRoleLabel != null) sourceRoleLabel.updateForFont(smallerFont);
        if (targetRoleLabel != null) targetRoleLabel.updateForFont(smallerFont);

        layoutFigure();
        setInitialSize();
        updateForSize(this.getSize());
    }

    private Font getSmallerFont() {
        return ScaledFontManager.getFont(ScaledFont.SMALLER_PLAIN_STYLE);
    }

    @Override
    public void hiliteBackground( Color hiliteColor ) {
        if (hiliteColor == null) this.setBackgroundColor(defaultBkgdColor);
        else this.setBackgroundColor(hiliteColor);

        if (stereotypeLabel != null) stereotypeLabel.setForegroundColor(this.getBackgroundColor());
        if (nameLabel != null) nameLabel.setForegroundColor(this.getBackgroundColor());
        if (typeLabel != null) typeLabel.setForegroundColor(this.getBackgroundColor());
    }

    /**
     * Customized method which provides the RelationshipNodeEditPart the ability to hilite either of the role containers for DND
     * 
     * @param hiliteColor
     * @param targetPoint
     * @since 4.2
     */
    public void hiliteBackground( Color hiliteColor,
                                  Point targetPoint ) {
        Point localPoint = new Point(targetPoint.x - this.getBounds().x, targetPoint.y - this.getBounds().y);
        if (sourceRoleRectangle.getBounds().contains(localPoint.x, localPoint.y)) {
            if (hiliteColor == null) sourceRoleRectangle.setBackgroundColor(defaultBkgdColor);
            else {
                sourceRoleRectangle.setBackgroundColor(hiliteColor);
                targetRoleRectangle.setBackgroundColor(defaultBkgdColor);
            }
            if (sourceRoleLabel != null) {
                sourceRoleLabel.setForegroundColor(sourceRoleRectangle.getBackgroundColor());
            }
        } else if (targetRoleRectangle.getBounds().contains(localPoint.x, localPoint.y)) {
            if (hiliteColor == null) targetRoleRectangle.setBackgroundColor(defaultBkgdColor);
            else {
                targetRoleRectangle.setBackgroundColor(hiliteColor);
                sourceRoleRectangle.setBackgroundColor(defaultBkgdColor);
            }
        } else {
            sourceRoleRectangle.setBackgroundColor(defaultBkgdColor);
            targetRoleRectangle.setBackgroundColor(defaultBkgdColor);
        }
        if (sourceRoleLabel != null) {
            sourceRoleLabel.setForegroundColor(sourceRoleRectangle.getBackgroundColor());
        }
        if (targetRoleLabel != null) {
            targetRoleLabel.setForegroundColor(targetRoleRectangle.getBackgroundColor());
        }
    }

    public int getDropTargetId( Point dropPoint ) {
        Point localPoint = new Point(dropPoint.x - this.getBounds().x, dropPoint.y - this.getBounds().y);
        if (sourceRoleRectangle.getBounds().contains(localPoint.x, localPoint.y)) {
            return PluginConstants.Drop.SOURCE_ROLE;
        } else if (targetRoleRectangle.getBounds().contains(localPoint.x, localPoint.y)) {
            return PluginConstants.Drop.TARGET_ROLE;
        } else {
            return PluginConstants.Drop.NOTHING;
        }
    }

    @Override
    public void updateForError( boolean hasErrors ) {
        if (objectHasIcon) {
            if (hasErrors) {
                if (errorIcon == null) {
                    errorIcon = new ImageFigure(DiagramUiPlugin.getDefault().getImage(PluginConstants.Images.ERROR_ICON));
                    if (errorIcon != null) {
                        this.add(errorIcon);
                        errorIcon.setLocation(new Point(nameLabel.getLocation().x, nameLabel.getLocation().y));
                        errorIcon.setSize(errorIcon.getPreferredSize());
                    }
                }
            } else if (errorIcon != null) {
                this.remove(errorIcon);
                errorIcon = null;
            }
        }
    }

    @Override
    public void updateForWarning( boolean hasWarnings ) {
        if (objectHasIcon) {
            if (hasWarnings) {
                if (warningIcon == null) {
                    warningIcon = new ImageFigure(DiagramUiPlugin.getDefault().getImage(PluginConstants.Images.WARNING_ICON));
                    if (warningIcon != null) {
                        this.add(warningIcon);
                        warningIcon.setLocation(new Point(nameLabel.getLocation().x, nameLabel.getLocation().y));
                        warningIcon.setSize(warningIcon.getPreferredSize());
                    }
                }
            } else if (warningIcon != null) {
                this.remove(warningIcon);
                warningIcon = null;
            }
        }
    }

    @Override
    public void addEditButton( Image image ) {
        if (image != null) {
            if (editButton != null) this.remove(editButton);

            editButton = new Button(image);
            editButton.setSize(new Dimension(image.getImageData().width + 6, image.getImageData().height + 4));

            editButton.addActionListener(new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    Display.getCurrent().asyncExec(new Runnable() {
                        public void run() {
                            // We need to call some generic edit event here
                            final Diagram diagram = RelationshipDiagramUtil.getRelationshipRelationshipDiagram(getDiagramModelNode().getModelObject(),
                                                                                                               editButton,
                                                                                                               true);
                            if (diagram != null) {
                                UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                                    public void run() {
                                        RelationshipDiagramUtil.openDiagram(diagram);
                                    }
                                });
                            }
                        }
                    });
                }
            });

            this.add(editButton);
            setInitialSize();
            layoutFigure();
            editButton.setToolTip(ToolTipUtil.createToolTip("View Owned Relationships")); //$NON-NLS-1$
        } else {
            if (editButton != null) this.remove(editButton);
            editButton = null;
        }

    }

    @Override
    public void addUpperLeftButton( Image image ) {
        if (image != null) {
            if (restoreButton != null) this.remove(restoreButton);

            restoreButton = new Button(image);
            restoreButton.setSize(new Dimension(image.getImageData().width + 6, image.getImageData().height + 4));

            restoreButton.addActionListener(new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    Display.getCurrent().asyncExec(new Runnable() {
                        public void run() {
                            // We need to call some generic edit event here
                            final Diagram diagram = RelationshipDiagramUtil.getRelationshipDiagram(getDiagramModelNode().getModelObject(),
                                                                                                   editButton,
                                                                                                   true);
                            if (diagram != null) {
                                UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                                    public void run() {
                                        factory.restoreRelationship(getDiagramModelNode().getModelObject(),
                                                                    getDiagramModelNode().getParent());
                                    }
                                });
                            }
                        }
                    });
                }
            });

            this.add(restoreButton);
            setInitialSize();
            layoutFigure();
            restoreButton.setToolTip(ToolTipUtil.createToolTip("Restore All Nodes")); //$NON-NLS-1$

        } else {
            if (restoreButton != null) this.remove(restoreButton);
            restoreButton = null;
        }

    }

    private void setButtonLocation() {
        if (editButton != null) {
            editButton.setLocation(getEditButtonLocation());
        }

        if (restoreButton != null) {
            restoreButton.setLocation(getRestoreButtonLocation());
        }
    }

    private Point getEditButtonLocation() {
        if (editButton != null) {
            return new Point(this.getBounds().width - 3 - editButton.getSize().width, 3);
        }
        return new Point(10, 10);
    }

    private Point getRestoreButtonLocation() {
        return new Point(3, 3);
    }

    /**
     * @param factory
     */
    public void setFactory( CustomDiagramModelFactory factory ) {
        this.factory = factory;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditFigure#getLabelFigure()
     */
    public Label getLabelFigure() {
        if (nameLabel != null) return nameLabel.getLabel();
        return null;
    }

    class HeaderFigure extends RectangleFigure {

        /**
         * @since 5.0
         */
        public HeaderFigure() {
            super();
        }

        @Override
        public void paint( Graphics graphics ) {
            super.paint(graphics);
        }

    }

}
