/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import com.metamatrix.ui.graphics.GlobalUiColorManager;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * DiagramViewForm was constructed to provide a ViewForm that took a toolbar on the left side instead of the "top". The code was
 * started using the org.eclipse.swt.custom.ViewForm class. (see IBM comment above) Instances of this class implement a Composite
 * that lays out three children vertically and allows programmatic control of layout and border parameters. ViewForm is used in
 * the workbench to implement a diagram view's toolbar local bar.
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>, it does not make sense to set a layout on it.
 * </p>
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>BORDER, FLAT</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(None)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class DiagramViewForm extends Composite {

    private SashForm sash;

    /**
     * marginWidth specifies the number of pixels of horizontal margin that will be placed along the left and right edges of the
     * form. The default value is 0.
     */
    public int marginWidth = 0;
    /**
     * marginHeight specifies the number of pixels of vertical margin that will be placed along the top and bottom edges of the
     * form. The default value is 0.
     */
    public int marginHeight = 0;

    /**
     * Color of innermost line of drop shadow border.
     */
    public static final RGB borderInsideRGB = new RGB(132, 130, 132);
    /**
     * Color of middle line of drop shadow border.
     */
    public static final RGB borderMiddleRGB = new RGB(143, 141, 138);
    /**
     * Color of outermost line of drop shadow border.
     */
    public static final RGB borderOutsideRGB = new RGB(171, 168, 165);

    // SWT widgets
    private ToolBar toolBar;

    private Control content;

    private Control control;

    // Configuration and state info
    private int drawLine2 = -1;

    private boolean showBorder = false;

    private int BORDER_TOP = 0;
    private int BORDER_BOTTOM = 0;
    private int BORDER_LEFT = 0;
    private int BORDER_RIGHT = 0;

    private Color borderColor1;
    private Color borderColor2;
    private Color borderColor3;

    private Rectangle oldArea;
    private static final int OFFSCREEN = -200;

    /**
     * Constructs a new instance of this class given its parent and a style value describing its behavior and appearance.
     * <p>
     * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to instances of
     * this class, or must be built by <em>bitwise OR</em>'ing together (that is, using the <code>int</code> "|" operator) two or
     * more of those <code>SWT</code> style constants. The class description lists the style constants that are applicable to the
     * class. Style bits are also inherited from superclasses.
     * </p>
     * 
     * @param parent a widget which will be the parent of the new instance (cannot be null)
     * @param style the style of widget to construct
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
     *            </ul>
     * @see SWT#BORDER
     * @see SWT#FLAT
     * @see #getStyle
     */
    public DiagramViewForm( Composite parent,
                            int style ) {
        super(parent, checkStyle(style));

        sash = new SashForm(this, SWT.HORIZONTAL);
        RowLayout layout = new RowLayout();
        sash.setLayout(layout);
        setContent(sash);

        borderColor1 = GlobalUiColorManager.getColor(borderInsideRGB);
        borderColor2 = GlobalUiColorManager.getColor(borderMiddleRGB);
        borderColor3 = GlobalUiColorManager.getColor(borderOutsideRGB);
        setBorderVisible((style & SWT.BORDER) != 0);

        addPaintListener(new PaintListener() {
            public void paintControl( PaintEvent event ) {
                onPaint(event.gc);
            }
        });
        addControlListener(new ControlAdapter() {
            @Override
            public void controlResized( ControlEvent e ) {
                onResize();
            }
        });

        addListener(SWT.Dispose, new Listener() {
            public void handleEvent( Event e ) {
                onDispose();
            }
        });
    }

    /**
     * Check the style bits to ensure that no invalid styles are applied.
     * 
     * @private
     */
    private static int checkStyle( int style ) {
        int mask = SWT.FLAT;
        return style & mask | SWT.NO_REDRAW_RESIZE;
    }

    @Override
    public Point computeSize( int wHint,
                              int hHint,
                              boolean changed ) {
        checkWidget();
        // size of title bar area
        Point toolBarSize = new Point(0, 0);
        int wHintToolBar = 25;
        if (toolBar != null) {
            ToolItem firstItem = toolBar.getItem(0);
            if (firstItem != null) {
            	wHintToolBar = firstItem.getWidth() + 4;
            }
            toolBarSize = toolBar.computeSize(wHintToolBar, hHint);
            toolBarSize.x += 1; // +1 for highlight line
        }

        Point size = new Point(0, 0);

        // calculate width of title bar
        size.x = toolBarSize.x;
        size.y = toolBarSize.y + 1; // +1 for highlight line

        if (content != null) {
            Point contentSize = new Point(0, 0);
            contentSize = content.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            size.y = Math.max(size.y, contentSize.y);
            size.x += contentSize.x + 1; // +1 for line bewteen content and toolbar
        }

        size.x += 2 * marginWidth;
        size.y += 2 * marginHeight;

        if (wHint != SWT.DEFAULT) size.x = wHint;
        if (hHint != SWT.DEFAULT) size.y = hHint;

        Rectangle trim = computeTrim(0, 0, size.x, size.y);
        
        return new Point(trim.width, trim.height);
    }

    @Override
    public Rectangle computeTrim( int x,
                                  int y,
                                  int width,
                                  int height ) {
        checkWidget();
        int trimX = x - BORDER_LEFT;
        int trimY = y - BORDER_TOP;
        int trimWidth = width + BORDER_LEFT + BORDER_RIGHT;
        int trimHeight = height + BORDER_TOP + BORDER_BOTTOM;
        return new Rectangle(trimX, trimY, trimWidth, trimHeight);
    }

    @Override
    public Rectangle getClientArea() {
        checkWidget();
        Rectangle clientArea = super.getClientArea();

        clientArea.x += BORDER_LEFT;
        clientArea.y += BORDER_TOP;
        clientArea.width -= BORDER_LEFT + BORDER_RIGHT;
        clientArea.height -= BORDER_TOP + BORDER_BOTTOM;
        return clientArea;
    }

    /**
     * Returns the content area.
     * 
     * @return the control in the content area of the pane or null
     */
    public Control getContent() {
        return content;
    }

    /**
     * Returns the content area.
     * 
     * @return the control in the content area of the pane or null
     */
    public Control getControllerControl() {
        return control;
    }

    /**
     * Returns the Control that appears in the top left corner of the pane. Typically this is a label such as CLabel.
     * 
     * @return the control in the top left corner of the pane or null
     */
    public Control getToolBar() {
        return toolBar;
    }

    @Override
    public void layout( boolean changed ) {
        checkWidget();

        Rectangle rect = getClientArea();
        drawLine2 = -1;

        int width = rect.x + marginWidth;

        Point toolBarSize = new Point(0, 0);
        if (toolBar != null && !toolBar.isDisposed()) {

            int wHintToolBar = 25;
            int count = toolBar.getItemCount();

            if (count > 0) {
                final ToolItem firstItem = toolBar.getItem(0);
                if (firstItem != null) {
                    wHintToolBar = 25; //firstItem.getWidth();
                }
            }

            toolBarSize = toolBar.computeSize(wHintToolBar, SWT.DEFAULT);

            Rectangle rectFinal = new Rectangle(0, 0, 10, 10);
            rectFinal.x = rect.x + marginWidth;
            rectFinal.y = rect.y + 1 + marginHeight;
            rectFinal.width = toolBarSize.x;
            rectFinal.height = rect.height - 2 * marginHeight;

            toolBar.setBounds(rectFinal);

            drawLine2 = toolBarSize.x + 3;
            width = toolBarSize.x + 4; // +1 for divider line
        }

        if (content != null && !content.isDisposed()) {

            Rectangle rectFinal = new Rectangle(0, 0, 10, 10);
            rectFinal.x = width;
            rectFinal.y = rect.y + marginHeight;
            rectFinal.width = rect.x + rect.width - width - marginWidth;
            rectFinal.height = rect.height - 2 * marginHeight;

            content.setBounds(rectFinal);
        }
        
        

    }

    void onDispose() {
        borderColor1 = null;
        borderColor2 = null;
        borderColor3 = null;

        toolBar = null;
        content = null;
        oldArea = null;
    }

    /**
     * Draws the focus border.
     */
    void onPaint( GC gc ) {
        Rectangle d = super.getClientArea();

        if (showBorder) {
            if ((getStyle() & SWT.FLAT) != 0) {
                gc.setForeground(borderColor1);
                gc.drawRectangle(d.x, d.y, d.x + d.width - 1, d.y + d.height - 1);
            } else {
                gc.setForeground(borderColor1);
                gc.drawRectangle(d.x, d.y, d.x + d.width - 3, d.y + d.height - 3);

                gc.setForeground(borderColor2);
                gc.drawLine(d.x + 1, d.y + d.height - 2, d.x + d.width - 1, d.y + d.height - 2);
                gc.drawLine(d.x + d.width - 2, d.y + 1, d.x + d.width - 2, d.y + d.height - 1);

                gc.setForeground(borderColor3);
                gc.drawLine(d.x + 2, d.y + d.height - 1, d.x + d.width - 2, d.y + d.height - 1);
                gc.drawLine(d.x + d.width - 1, d.y + 2, d.x + d.width - 1, d.y + d.height - 2);
            }
        }

        if (drawLine2 != -1) {
            // content separator line
            gc.setForeground(borderColor1);
            int y1 = d.y + BORDER_TOP;
            int y2 = d.y + d.height - BORDER_BOTTOM;
            gc.drawLine(drawLine2, y1, drawLine2, y2);
        }
        // highlight on top
        int x = drawLine2;

        if (x != -1) {
            gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));

            gc.drawLine(d.x + BORDER_LEFT + marginWidth, d.y + BORDER_TOP + marginHeight, x - 1, d.y + BORDER_TOP + marginHeight);

            gc.drawLine(d.x + BORDER_LEFT + marginWidth,
                        d.y + BORDER_TOP + marginHeight,
                        d.x + BORDER_LEFT + marginWidth,
                        d.y + d.height - BORDER_BOTTOM - marginHeight - 1);
        }

        gc.setForeground(getForeground());
    }

    void onResize() {
        toolBar.pack();
        layout();

        Rectangle area = super.getClientArea();
        if (oldArea == null || oldArea.width == 0 || oldArea.height == 0) {
            redraw();
        } else {
            int width = 0;
            if (oldArea.width < area.width) {
                width = area.width - oldArea.width + BORDER_RIGHT;
            } else if (oldArea.width > area.width) {
                width = BORDER_RIGHT;
            }
            redraw(area.x + area.width - width, area.y, width, area.height, false);

            int height = 0;
            if (oldArea.height < area.height) {
                height = area.height - oldArea.height + BORDER_BOTTOM;
            }
            if (oldArea.height > area.height) {
                height = BORDER_BOTTOM;
            }
            redraw(area.x, area.y + area.height - height, area.width, height, false);
        }
        oldArea = area;
    }

    /**
     * Sets the content. Setting the content to null will remove it from the pane - however, the creator of the content must
     * dispose of the content.
     * 
     * @param c the control to be displayed in the content area or null
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *            </ul>
     */
    public void setContent( Control content ) {
        checkWidget();
        if (this.content != null && !this.content.isDisposed()) {
            this.content.setBounds(OFFSCREEN, OFFSCREEN, 0, 0);
        }
        this.content = content;
        layout();
    }

    /**
     * Sets the content. Setting the content to null will remove it from the pane - however, the creator of the content must
     * dispose of the content.
     * 
     * @param c the control to be displayed in the content area or null
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *            </ul>
     */
    public void setControllerControl( Control newControl ) {
        checkWidget();

        if (this.control != null && !this.control.isDisposed()) {
            this.control.setBounds(OFFSCREEN, OFFSCREEN, 0, 0);
        }
        this.control = newControl;
        layout();
    }

    /**
     * Set the widget font. This will apply the font to the toolBar, topRight and topCenter widgets.
     */
    @Override
    public void setFont( Font f ) {
        super.setFont(f);
        if (toolBar != null && !toolBar.isDisposed()) toolBar.setFont(f);

        layout();
    }

    public void setInitialSashFormWeights() {
        // make sure weight is never less than 30% of the other weight.
        // if it is, make it 70/30
        int[] weights = WidgetUtil.getSashFormWeights(sash);

        if (weights.length == 2) {
            double d = (double)weights[0] / (double)weights[1] * 100D;

            if ((d < 30)) {
                // left side is too small:
                weights[0] = 30;
                weights[1] = 70;

            } else if (d > 40) {
                // left side is too big:
                weights[0] = 40;
                weights[1] = 60;
            } // endif
        }
        sash.setWeights(weights);
    }

    /**
     * Sets the layout which is associated with the receiver to be the argument which may be null.
     * <p>
     * Note : ViewForm does not use a layout class to size and position its children.
     * </p>
     * 
     * @param the receiver's new layout or null
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *            </ul>
     */
    @Override
    public void setLayout( Layout layout ) {
        checkWidget();
        return;
    }

    /**
     * Set the control that appears in the top left corner of the pane. Typically this is a label such as CLabel. The topLeft is
     * optional. Setting the top left control to null will remove it from the pane - however, the creator of the control must
     * dispose of the control.
     * 
     * @param c the control to be displayed in the top left corner or null
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *            </ul>
     */
    public void setToolBar( Control c ) {
        checkWidget();
        if (c != null && c.getParent() != this) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        if (this.toolBar != null && !this.toolBar.isDisposed()) {
            this.toolBar.setBounds(OFFSCREEN, OFFSCREEN, 0, 0);
        }
        this.toolBar = (ToolBar)c;
        layout();
    }

    /**
     * Specify whether the border should be displayed or not.
     * 
     * @param show true if the border should be displayed
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *            </ul>
     */
    public void setBorderVisible( boolean show ) {
        checkWidget();
        if (showBorder == show) return;

        showBorder = show;
        if (showBorder) {
            if ((getStyle() & SWT.FLAT) != 0) {
                BORDER_LEFT = BORDER_TOP = BORDER_RIGHT = BORDER_BOTTOM = 1;
            } else {
                BORDER_LEFT = BORDER_TOP = 1;
                BORDER_RIGHT = BORDER_BOTTOM = 3;
            }
        } else {
            BORDER_BOTTOM = BORDER_TOP = BORDER_LEFT = BORDER_RIGHT = 0;
        }

        layout();
        redraw();
    }

    public Control getSashForm() {
        return sash;
    }
}
