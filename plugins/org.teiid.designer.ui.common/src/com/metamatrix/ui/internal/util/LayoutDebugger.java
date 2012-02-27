/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * @author PForhan
 */
public class LayoutDebugger { // NO_UCD (Indicates this is ignored by unused code detection tool)

    public static void debugLayout(Composite composite)
    {
        debugLayout(composite, false, true, null);
    }

    /**
     * Turns on the debug mode on the given composite.
     *
     * @param composite  the composite to debug
     * @param gridColor  the color used to paint the grid
     */
    public static void debugLayout(Composite composite, Color gridColor)
    {
        debugLayout(composite, false, true, gridColor);
    }

    /**
     * Turns on the debug mode on the given composite.
     *
     * @param composite       the composite to debug
     * @param paintDiagonals  true to paint diagonals, false to not paint them
     */
    public static void debugLayout(Composite composite, boolean paintDiagonals)
    {
        debugLayout(composite, paintDiagonals, true, null);
    }

    /**
     * Turns on the debug mode on the given composite.
     *
     * @param composite       the composite to debug
     * @param paintDiagonals  true to paint diagonals, false to not paint them
     */
    public static void debugLayout(Composite composite, boolean paintDiagonals, boolean showDistance)
    {
        debugLayout(composite, paintDiagonals, showDistance, null);
    }

    /**
     * Turns on the debug mode on the given composite.
     *
     * @param composite       the composite to debug
     * @param paintDiagonals  true to paint diagonals, false to not paint them
     * @param gridColor       the color used to paint the grid
     */
    public static void debugLayout(
        Composite composite,
        boolean paintDiagonals,
        boolean showDistance,
        Color gridColor)
    {
        FormDebug debug = new FormDebug();

        if (gridColor == null)
        {
            gridColor = GlobalUiColorManager.getColor(new RGB(255, 0, 0));
            composite.addDisposeListener(debug);
        }
        debug.color          = gridColor;
        debug.paintDiagonals = paintDiagonals;
        debug.showDistance   = showDistance;
        debug.composite      = composite;

        // composite.addPaintListener(debug);
        listenToCompositeAndKids(composite, debug);
    }

    private static void listenToCompositeAndKids(Composite c, PaintListener l) {
        // listen to me:
        c.addPaintListener(l);

        // listen to kids:
        Control[] kids = c.getChildren();
        for (int i = 0; i < kids.length; i++) {
            Control ctrl = kids[i];
            if (ctrl instanceof Composite) {
                listenToCompositeAndKids((Composite) ctrl, l);
            } else {
                ctrl.addPaintListener(l);
            } //endif
        } // endfor
    }

    /**
     * Helper class which implements the grid painter.
     */
    static class FormDebug implements DisposeListener, PaintListener
    {
        Composite composite = null;
        Color color = null;
        boolean paintDiagonals = false;
        boolean showDistance;

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
         */
        public void paintControl(PaintEvent paintEvent)
        {
            GC gc = paintEvent.gc;
            gc.setForeground(color);
            drawOnControl((Control) paintEvent.widget, gc);
//            drawCompositeAndKids(composite, paintEvent.gc);
        }
       

//        private void drawCompositeAndKids(Composite c, GC gc) {
//            // draw kids first:
//            Control[] kids = c.getChildren();
//            for (int i = 0; i < kids.length; i++) {
//                Control ctrl = kids[i];
//                if (ctrl instanceof Composite) {
//                    drawCompositeAndKids((Composite) ctrl, gc);
//                } else {
//                    drawOnControl(ctrl, gc);
//                } //endif
//            } // endfor
//            // now, draw me:
//            drawOnControl(c, gc);
//        }

        private void drawOnControl(Control ctrl, GC gc) {
            Rectangle r = gc.getClipping();
            if (r.height == 0
             || r.width  == 0) {
                // use control's bounds:
                r = ctrl.getBounds();
            } // endif

            int width = r.width  - 1;
            int height = r.height - 1;

            int left = r.x;
            int top = r.y;

            int bottom  = top + height;
            int right   = left + width;

            // display distance to root debugged composite:
            if (showDistance) {
                int distance = getDistanceToDebugged(ctrl);
                int hcenter = (left+right)/2;
                gc.drawText(Integer.toString(distance),hcenter, top-2, true);
            }

            // Change the line type based on whether composite:
            if (ctrl instanceof Composite) {
                gc.setLineStyle(SWT.LINE_DOT);
            } // endif

            // draw surrounding box:
            gc.drawRectangle(left, top, width, height);

            // draw diagonals:
            if (paintDiagonals) {
                gc.drawLine(left, top, right, bottom);
                gc.drawLine(left, bottom, right, top);
            }
        }

        private int getDistanceToDebugged(Control c) {
            int rv = 0;
           
            Control walker = c;
            while (walker != composite) {
                walker = walker.getParent();
                rv++;
            } // endwhile

            return rv;
        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
         */
        public void widgetDisposed(DisposeEvent arg0)
        {
            //color.dispose();
        }

    }

}

