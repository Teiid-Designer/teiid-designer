/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

/**
 * @since 5.0
 */
public class Label extends CLabel {

    // ===========================================================================================================================
    // Constants

    private static final int DFLT_GAP = 5;
    private static final int DFLT_MARGIN = 3;
    private static final int DRAW_FLAGS = SWT.DRAW_MNEMONIC | SWT.DRAW_TAB | SWT.DRAW_TRANSPARENT | SWT.DRAW_DELIMITER;

    // ===========================================================================================================================
    // Variables

    private boolean skippedSuperclassListener;
    private int gap = DFLT_GAP;
    private int leftMargin = DFLT_MARGIN;
    private int rightMargin = DFLT_MARGIN;
    private int topMargin = DFLT_MARGIN;
    private int bottomMargin = DFLT_MARGIN;
    private Image backgroundImage;
    private Color[] gradientColors;
    private int[] gradientPercents;
    private boolean gradientVertical;

    // ===========================================================================================================================
    // Constructors

    public Label(Composite parent,
                 int style) {
        super(parent, style);
    }

    // ===========================================================================================================================
    // Methods

    /**
     * @see org.eclipse.swt.widgets.Control#addPaintListener(org.eclipse.swt.events.PaintListener)
     * @since 5.0
     */
    @Override
    public void addPaintListener(PaintListener listener) {
        if (skippedSuperclassListener) {
            super.addPaintListener(listener);
        } else {
            skippedSuperclassListener = !skippedSuperclassListener;
            super.addPaintListener(new PaintListener() {

                public void paintControl(PaintEvent event) {
                    paint(event);
                }
            });
        }
    }

    @Override
    public Point computeSize(int wHint,
                             int hHint,
                             boolean changed) {
        checkWidget();
        Point size = getTotalSize(getImage(), getText());
        if (wHint == SWT.DEFAULT) {
            size.x += this.leftMargin + this.rightMargin;
        } else {
            size.x = wHint;
        }
        if (hHint == SWT.DEFAULT) {
            size.y += this.topMargin + this.bottomMargin;
        } else {
            size.y = hHint;
        }
        return size;
    }

    /**
     * @see org.eclipse.swt.widgets.Widget#dispose()
     * @since 5.0
     */
    @Override
    public void dispose() {
        super.dispose();
        this.gradientColors = null;
        this.gradientPercents = null;
        this.backgroundImage = null;
    }

    /**
     * @return The number of pixels in this label's bottom margin. Default is {@value #DFLT_MARGIN}.
     * @see #getLeftMargin()
     * @see #getRightMargin()
     * @see #getTopMargin()
     * @see #setLeftMargin(int)
     * @see #setRightMargin(int)
     * @see #setTopMargin(int)
     * @see #setBottomMargin(int)
     * @see #setHorizontalMargin(int)
     * @see #setVerticalMargin(int)
     * @since 5.0
     */

    @Override
    public int getBottomMargin() {
        return this.bottomMargin;
    }

    /**
     * @return The number of pixels between this label's image and text, assuming both are not <code>null</code>. Default is
     *         {@value #DFLT_GAP}.
     * @since 5.0
     */
    public int getGap() {
        return this.gap;
    }

    /**
     * @return The number of pixels in this label's left margin. Default is {@value #DFLT_MARGIN}.
     * @see #getRightMargin()
     * @see #getTopMargin()
     * @see #getBottomMargin()
     * @see #setLeftMargin(int)
     * @see #setRightMargin(int)
     * @see #setTopMargin(int)
     * @see #setBottomMargin(int)
     * @see #setHorizontalMargin(int)
     * @see #setVerticalMargin(int)
     * @since 5.0
     */

    @Override
    public int getLeftMargin() {
        return this.leftMargin;
    }

    /**
     * @return The number of pixels in this label's right margin. Default is {@value #DFLT_MARGIN}.
     * @see #getLeftMargin()
     * @see #getTopMargin()
     * @see #getBottomMargin()
     * @see #setLeftMargin(int)
     * @see #setRightMargin(int)
     * @see #setTopMargin(int)
     * @see #setBottomMargin(int)
     * @see #setHorizontalMargin(int)
     * @see #setVerticalMargin(int)
     * @since 5.0
     */

    @Override
    public int getRightMargin() {
        return this.rightMargin;
    }

    /**
     * @return The number of pixels in this label's top margin. Default is {@value #DFLT_MARGIN}.
     * @see #getLeftMargin()
     * @see #getRightMargin()
     * @see #getBottomMargin()
     * @see #setLeftMargin(int)
     * @see #setRightMargin(int)
     * @see #setTopMargin(int)
     * @see #setBottomMargin(int)
     * @see #setHorizontalMargin(int)
     * @see #setVerticalMargin(int)
     * @since 5.0
     */

    @Override
    public int getTopMargin() {
        return this.topMargin;
    }

    private Point getTotalSize(Image image,
                               String text) {
        Point size = new Point(0, 0);
        if (image != null) {
            Rectangle bounds = image.getBounds();
            size.x += bounds.width;
            size.y += bounds.height;
        }
        GC gc = new GC(this);
        if (text != null && text.length() > 0) {
            Point extent = gc.textExtent(text, DRAW_FLAGS);
            size.x += extent.x;
            size.y = Math.max(size.y, extent.y);
            if (image != null) {
                size.x += this.gap;
            }
        } else {
            size.y = Math.max(size.y, gc.getFontMetrics().getHeight());
        }
        gc.dispose();
        return size;
    }

    void paint(PaintEvent event) {
        Rectangle bounds = getClientArea();
        if (bounds.width == 0 || bounds.height == 0) {
            return;
        }
        // Check if text needs to be shortened (using ellipses)
        boolean shortenText = false;
        String text = getText();
        Image img = getImage();
        int availableWidth = Math.max(0, bounds.width - this.leftMargin - this.rightMargin);
        Point size = getTotalSize(img, text);
        if (size.x > availableWidth) {
            shortenText = true;
        }
        // Split text into multiple lines if it contains line-feeds.
        String[] lines = (text == null ? null : splitString(text));
        // Shorten text if necessary
        GC gc = event.gc;
        if (shortenText) {
            size.x = 0;
            if (lines != null) {
                for (int ndx = 0; ndx < lines.length; ndx++) {
                    Point extent = gc.textExtent(lines[ndx], DRAW_FLAGS);
                    if (extent.x > availableWidth) {
                        lines[ndx] = shortenText(gc, lines[ndx], availableWidth);
                        size.x = Math.max(size.x, getTotalSize(null, lines[ndx]).x);
                    } else {
                        size.x = Math.max(size.x, extent.x);
                    }
                }
                if (getToolTipText() == null) {
                    super.setToolTipText(text);
                }
            }
        }
        // Determine horizontal position
        int x = bounds.x + this.leftMargin;
        int align = getAlignment();
        if (align == SWT.CENTER) {
            x = (bounds.width - size.x) / 2;
        } else if (align == SWT.RIGHT) {
            x = bounds.width - this.rightMargin - size.x;
        }
        // Draw a background image behind the text
        Color bkgd = getBackground();
        Color frgd = getForeground();
        try {
            if (this.backgroundImage != null) {
                // Draw a background image behind the text, tiling to fill space
                Rectangle imgBounds = this.backgroundImage.getBounds();
                gc.setBackground(getBackground());
                gc.fillRectangle(bounds);
                for (int imgX = 0; imgX < bounds.width; imgX += imgBounds.width) {
                    for (int imgY = 0; imgY < bounds.height; imgY += imgBounds.height) {
                        gc.drawImage(this.backgroundImage, imgX, imgY);
                    }
                }
            } else if (this.gradientColors != null && this.gradientColors.length > 0) {
                // Draw a gradient behind the text
                if (this.gradientColors.length == 1) {
                    if (this.gradientColors[0] != null) {
                        gc.setBackground(this.gradientColors[0]);
                    }
                    gc.fillRectangle(0, 0, bounds.width, bounds.height);
                } else {
                    Color lastColor = this.gradientColors[0];
                    int pos = 0;
                    for (int ndx = 0; ndx < this.gradientPercents.length; ++ndx) {
                        gc.setForeground(lastColor);
                        lastColor = this.gradientColors[ndx + 1];
                        gc.setBackground(lastColor);
                        if (this.gradientVertical) {
                            int hgt = (this.gradientPercents[ndx] * bounds.height / 100) - pos;
                            gc.fillGradientRectangle(0, pos, bounds.width, hgt, true);
                            pos += hgt;
                      
                        } else {
                            int wth = (this.gradientPercents[ndx] * bounds.width / 100) - pos;
                            gc.fillGradientRectangle(pos, 0, wth, bounds.height, false);
                            pos += wth;
                        }
                    }
                    if (this.gradientVertical && pos < bounds.height) {
                        gc.setBackground(bkgd);
                        gc.fillRectangle(0, pos, bounds.width, bounds.height - pos);
                    }
                    if (!this.gradientVertical && pos < bounds.width) {
                        gc.setBackground(bkgd);
                        gc.fillRectangle(pos, 0, bounds.width - pos, bounds.height);
                    }
                }
            } else {
                if ((getStyle() & SWT.NO_BACKGROUND) != 0) {
                    gc.setBackground(bkgd);
                    gc.fillRectangle(bounds);
                }
            }
        } catch (SWTException err) {
            if ((getStyle() & SWT.NO_BACKGROUND) != 0) {
                gc.setBackground(bkgd);
                gc.fillRectangle(bounds);
            }
        }
        // Draw the image
        if (img != null) {
            Rectangle imgBounds = img.getBounds();
            gc.drawImage(img,
                         0,
                      
                         0,
                         imgBounds.width,
                         imgBounds.height,
                         x,
                         (bounds.height - imgBounds.height) / 2,
                         imgBounds.width,
                         imgBounds.height);
            x += imgBounds.width + this.gap;
            size.x -= imgBounds.width + this.gap;
        }
        // Draw the text
        if (lines != null) {
            int lineHgt = gc.getFontMetrics().getHeight();
            int textHgt = lines.length * lineHgt;
            int lineY = Math.max(this.topMargin, bounds.y + (bounds.height - textHgt) / 2);
            gc.setForeground(frgd);
            for (int ndx = 0; ndx < lines.length; ndx++) {
                int lineX = x;
                if (lines.length > 1) {
                    if (align == SWT.CENTER) {
                        int lineWth = gc.textExtent(lines[ndx], DRAW_FLAGS).x;
                        lineX = x + Math.max(0, (size.x - lineWth) / 2);
                    }
                    if (align == SWT.RIGHT) {
                        int lineWth = gc.textExtent(lines[ndx], DRAW_FLAGS).x;
                        lineX = Math.max(x, bounds.x + bounds.width - this.rightMargin - lineWth);
                    }
                }
                gc.drawText(lines[ndx], lineX, lineY, DRAW_FLAGS);
                lineY += lineHgt;
            }
        }
    }

    /**
     * @see org.eclipse.swt.custom.CLabel#setBackground(org.eclipse.swt.graphics.Color)
     * @since 5.0
     */
    @Override
    public void setBackground(Color color) {
        // Return if color unchanged
        if (color != null
            && this.backgroundImage == null
            && this.gradientColors == null
            && this.gradientPercents == null
            && color.equals(getBackground())) {
            return;
        }
        super.setBackground(color);
        this.backgroundImage = null;
        this.gradientColors = null;
        this.gradientPercents = null;
        redraw();
    }

    /**
     * @see org.eclipse.swt.custom.CLabel#setBackground(org.eclipse.swt.graphics.Color[], int[], boolean)
     * @since 5.0
     */
    @Override
    public void setBackground(Color[] colors,
                              int[] percents,
                              boolean vertical) {
        checkWidget();
        if (colors != null) {
            if (percents == null || percents.length != colors.length - 1) {
                SWT.error(SWT.ERROR_INVALID_ARGUMENT);
            }
            if (getDisplay().getDepth() < 15) {
                // Don't use gradients on low color displays
                colors = new Color[] {
                    colors[colors.length - 1]
                };
                percents = new int[] {};
            }
            for (int ndx = 0; ndx < percents.length; ndx++) {
                if (percents[ndx] < 0 || percents[ndx] > 100) {
                    SWT.error(SWT.ERROR_INVALID_ARGUMENT);
                }
                if (ndx > 0 && percents[ndx] < percents[ndx - 1]) {
                    SWT.error(SWT.ERROR_INVALID_ARGUMENT);
                }
            }
        }
        Color bkgd = getBackground();
        if (this.backgroundImage == null) {
            // Return if gradient unchanged
            if ((this.gradientColors != null) && (colors != null) && (this.gradientColors.length == colors.length)) {
                boolean same = false;
                for (int
                ndx = 0; ndx < this.gradientColors.length; ndx++) {
                    same = (this.gradientColors[ndx] == colors[ndx])
                           || ((this.gradientColors[ndx] == null) && (colors[ndx] == bkgd))
                           || ((this.gradientColors[ndx] == bkgd) && (colors[ndx] == null));
                    if (!same) {
                        break;
                    }
                }
                if (same) {
                    for (int ndx = 0; ndx < this.gradientPercents.length; ndx++) {
                        same = this.gradientPercents[ndx] == percents[ndx];
                        if (!same) {
                            break;
                        }
                    }
                }
                if (same && this.gradientVertical == vertical) {
                    return;
                }
            }
        } else {
            this.backgroundImage = null;
        }
        // Store the new gradient
        if (colors == null) {
            this.gradientColors = null;
            this.gradientPercents = null;
            this.gradientVertical = false;
        } else {
            this.gradientColors = new Color[colors.length];
            for (int ndx = 0; ndx < colors.length; ++ndx) {
                this.gradientColors[ndx] = (colors[ndx] != null) ? colors[ndx] : bkgd;
            }
            this.gradientPercents = new int[percents.length];
            for (int ndx = 0; ndx < percents.length; ++ndx) {
                this.gradientPercents[ndx] = percents[ndx];
            }
            this.gradientVertical = vertical;
        }
        // Refresh with the new gradient
        redraw();
    }

    /**
     * @see org.eclipse.swt.custom.CLabel#setBackground(org.eclipse.swt.graphics.Image)
     * @since 5.0
     */
    @Override
    public void setBackground(Image image) {
        checkWidget();
        if (image == this.backgroundImage) {
            return;
        }
        if (image != null) {
            this.gradientColors = null;
            this.gradientPercents = null;
        }
        this.backgroundImage = image;
        redraw();
    }

    /**
     * @param margin
     *            The number of pixels in this label's bottom margin.
     * @see #getLeftMargin()
     * @see #getRightMargin()
     * @see #getTopMargin()
     * @see #getBottomMargin()
     * @see #setLeftMargin(int)
     * @see #setRightMargin(int)
     * @see #setTopMargin(int)
     * @see #setHorizontalMargin(int)
     * @see #setVerticalMargin(int)
     * @since 5.0
     */

    @Override
    public void setBottomMargin(int margin) {
        this.bottomMargin = Math.max(0, margin);
    }

    /**
     * @param gap
     *            The number of pixels between this label's image and text, assuming both are not <code>null</code>.
     * @since 5.0
     */
    public void setGap(int gap) {
        this.gap = Math.max(0, gap);
    }

    /**
     * @param margin
     *            The number of pixels in this label's left and right margins.
     * @see #getLeftMargin()
     * @see #getRightMargin()
     * @see #getTopMargin()
     * @see #getBottomMargin()
     * @see #setLeftMargin(int)
     * @see #setRightMargin(int)
     * @see #setTopMargin(int)
     * @see #setBottomMargin(int)
     * @see #setVerticalMargin(int)
     * @since 5.0
     */
    public void setHorizontalMargin(int margin) {
        this.leftMargin = this.rightMargin = Math.max(0, margin);
    }

    /**
     * @param margin
     *            The number of pixels in this label's left margin.
     * @see #getLeftMargin()
     * @see #getRightMargin()
     * @see #getTopMargin()
     * @see #getBottomMargin()
     * @see #setRightMargin(int)
     * @see #setTopMargin(int)
     * @see #setBottomMargin(int)
     * @see #setHorizontalMargin(int)
     * @see #setVerticalMargin(int)
     * @since 5.0
     */

    @Override
    public void setLeftMargin(int margin) {
        this.leftMargin = Math.max(0, margin);
    }

    /**
     * @param margin
     *            The number of pixels in this label's right margin.
     * @see #getLeftMargin()
     * @see #getRightMargin()
     * @see #getTopMargin()
     * @see #getBottomMargin()
     * @see #setLeftMargin(int)
     * @see #setTopMargin(int)
     * @see #setBottomMargin(int)
     * @see #setHorizontalMargin(int)
     * @see #setVerticalMargin(int)
     * @since 5.0
     */

    @Override
    public void setRightMargin(int margin) {
        this.rightMargin = Math.max(0, margin);
    }

    /**
     * @param margin
     *            The number of pixels in this label's top margin.
     * @see #getLeftMargin()
     * @see #getRightMargin()
     * @see #getTopMargin()
     * @see #getBottomMargin()
     * @see #setLeftMargin(int)
     * @see #setRightMargin(int)
     * @see #setBottomMargin(int)
     * @see #setHorizontalMargin(int)
     * @see #setVerticalMargin(int)
     * @since 5.0
     */

    @Override
    public void setTopMargin(int margin) {
        this.topMargin = Math.max(0, margin);
    }

    /**
     * @param margin
     *            The number of pixels in this label's top and bottom margins.
     * @see #getLeftMargin()
     * @see #getRightMargin()
     * @see #getTopMargin()
     * @see #getBottomMargin()
     * @see #setLeftMargin(int)
     * @see #setRightMargin(int)
     * @see #setTopMargin(int)
     * @see #setBottomMargin(int)
     * @see #setHorizontalMargin(int)
     * @since 5.0
     */
    public void setVerticalMargin(int margin) {
        this.topMargin = this.bottomMargin = Math.max(0, margin);
    }

    private String[] splitString(String text) {
        String[] lines = new String[1];
        int start = 0, pos;
        do {
            pos = text.indexOf('\n', start);
            if (pos == -1) {
                lines[lines.length - 1] = text.substring(start);
            } else {
                boolean crlf = (pos > 0) && (text.charAt(pos - 1) == '\r');
                lines[lines.length - 1] = text.substring(start, pos - (crlf ? 1 : 0));
                start = pos + 1;
                String[] newLines = new String[lines.length + 1];
                System.arraycopy(lines, 0, newLines, 0, lines.length);
                lines = newLines;
            }
        } while (pos != -1);
        return lines;
    }
}
