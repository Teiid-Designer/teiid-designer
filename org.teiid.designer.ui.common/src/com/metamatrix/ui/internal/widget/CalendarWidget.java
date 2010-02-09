/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TypedListener;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.ui.internal.InternalUiConstants;

/**
 * CalendarWidget
 */
public class CalendarWidget extends Composite implements InternalUiConstants {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(CalendarWidget.class);

    int m_Day;

    int m_Month;

    int m_Year;

    boolean m_showCalendar;

    boolean m_showTime;

    private Button nextMonth;

    private Button nextYear;

    private Button prevMonth;

    private Button prevYear;

    private Label m_CalendarLabel;

    private TDatePanel m_DatePanel;

    private IntegerSpinner m_hourSpinner;

    private IntegerSpinner m_minuteSpinner;

    Composite pnlCalendar;

    private Composite pnlTime;

    /**
     * Construct an instance of <code>CalendarWidget</code>. The calendar will always be shown.
     * 
     * @param theParent
     * @param theStyle
     * @param theShowTime indicates if time should be shown
     */
    public CalendarWidget( Composite theParent,
                           int theStyle,
                           boolean theShowTime ) {
        this(theParent, theStyle, theShowTime, true);
    }

    /**
     * Construct an instance of <code>CalendarWidget</code>.
     * 
     * @param theParent
     * @param theStyle
     * @param theShowTime indicates if time should be shown
     * @param theShowCalendar indicates if the calendar should be shown
     */
    public CalendarWidget( Composite theParent,
                           int theStyle,
                           boolean theShowTime,
                           boolean theShowCalendar ) {
        super(theParent, theStyle);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 1;
        gridLayout.marginWidth = 1;
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 0;
        setLayout(gridLayout);
        setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));

        m_showTime = theShowTime;
        m_showCalendar = theShowCalendar;

        construct(this);

        // initialize state
        Calendar currentDate = Calendar.getInstance();
        m_Day = currentDate.get(Calendar.DATE);
        m_Month = currentDate.get(Calendar.MONTH);
        m_Year = currentDate.get(Calendar.YEAR);
        m_hourSpinner.setValue(currentDate.get(Calendar.HOUR_OF_DAY));
        m_minuteSpinner.setValue(currentDate.get(Calendar.MINUTE));
    }

    private void construct( Composite theParent ) {
        // 
        // calendar container
        //

        pnlCalendar = new Composite(theParent, SWT.NONE) {
            // need to override since computeSize does not consider if visible or not
            @Override
            public Point computeSize( int wHint,
                                      int hHint,
                                      boolean changed ) {
                if (!m_showCalendar) {
                    return new Point(0, 0);
                }
                return super.computeSize(wHint, hHint, changed);
            }
        };
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 5;
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        pnlCalendar.setLayout(gridLayout);
        pnlCalendar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        //
        // previous year button
        //

        prevYear = new Button(pnlCalendar, SWT.NONE);
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.heightHint = 20;
        gridData.widthHint = 20;
        prevYear.setLayoutData(gridData);
        prevYear.setText("<<"); //$NON-NLS-1$
        prevYear.setToolTipText(Util.getString(PREFIX + "previousYear")); //$NON-NLS-1$
        prevYear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                --m_Year;
                updateDate();
            }
        });

        //
        // previous month button
        //

        prevMonth = new Button(pnlCalendar, SWT.NONE);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.heightHint = gridData.widthHint = 20;
        prevMonth.setLayoutData(gridData);
        prevMonth.setText("<"); //$NON-NLS-1$
        prevMonth.setToolTipText(Util.getString(PREFIX + "previousMonth")); //$NON-NLS-1$
        prevMonth.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                --m_Month;
                updateDate();
            }
        });

        //
        // calendar label
        //

        m_CalendarLabel = new Label(pnlCalendar, SWT.CENTER);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.heightHint = prevYear.computeSize(20, 20).y;
        int minLabelChars = 20;
        gridData.minimumWidth = getMinCalWidth(m_CalendarLabel, minLabelChars);
        m_CalendarLabel.setLayoutData(gridData);

        //
        // next month button
        //

        nextMonth = new Button(pnlCalendar, SWT.NONE);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
        gridData.heightHint = gridData.widthHint = 20;
        nextMonth.setLayoutData(gridData);
        nextMonth.setText(">"); //$NON-NLS-1$
        nextMonth.setToolTipText(Util.getString(PREFIX + "nextMonth")); //$NON-NLS-1$
        nextMonth.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                ++m_Month;
                updateDate();
            }
        });

        //
        // next year button
        //

        nextYear = new Button(pnlCalendar, SWT.NONE);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
        gridData.heightHint = gridData.widthHint = 20;
        nextYear.setLayoutData(gridData);
        nextYear.setText(">>"); //$NON-NLS-1$
        nextYear.setToolTipText(Util.getString(PREFIX + "nextYear")); //$NON-NLS-1$
        nextYear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                ++m_Year;
                updateDate();
            }
        });

        //
        // calendar date panel
        //

        m_DatePanel = new TDatePanel(pnlCalendar, SWT.NONE);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 5;
        m_DatePanel.setLayoutData(gridData);

        // 
        // time container
        //

        pnlTime = new Composite(theParent, SWT.NONE) {
            @Override
            public Point computeSize( int wHint,
                                      int hHint,
                                      boolean changed ) {
                // need to override since computeSize does not consider if visible or not
                if (!m_showTime) {
                    return new Point(0, 0);
                }
                return super.computeSize(wHint, hHint, changed);
            }
        };
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 4;
        gridLayout2.marginHeight = 1;
        gridLayout2.marginWidth = 1;
        pnlTime.setLayout(gridLayout2);

        // 
        // hour label
        //

        Label lblHour = new Label(pnlTime, SWT.NONE);
        lblHour.setText(Util.getString(PREFIX + "lblHour")); //$NON-NLS-1$

        // 
        // hour spinner
        //

        m_hourSpinner = new IntegerSpinner(pnlTime, 0, 23);

        // 
        // minute label
        //

        Label lblMinute = new Label(pnlTime, SWT.NONE);
        lblMinute.setText(Util.getString(PREFIX + "lblMinute")); //$NON-NLS-1$

        // 
        // minute spinner
        //

        m_minuteSpinner = new IntegerSpinner(pnlTime, 0, 59);

        //
        // set initial state
        //

        if (!m_showCalendar) {
            showCalendar(false);
        }

        if (!m_showTime) {
            showTime(false);
        }

        panelChanged();
    }

    /**
     * Gets the avg width in pixels for the desired number of characters using the supplied label's font
     * 
     * @param label the supplied label
     * @param nChars number of chars to calculate avg width
     */
    private int getMinCalWidth( Label label,
                                int nChars ) {
        int widthPixels = 0;

        // Get font metric for labels font
        GC gc = new GC(label);
        gc.setFont(label.getFont());
        FontMetrics fontMetrics = gc.getFontMetrics();

        // calculate avg width for the supplied number of chars
        widthPixels = fontMetrics.getAverageCharWidth() * nChars;
        gc.dispose();

        return widthPixels;
    }

    public void addSelectionListener( SelectionListener theListener ) {
        checkWidget();

        if (theListener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        if (m_showCalendar) {
            TypedListener typedListener = new TypedListener(theListener);
            addListener(SWT.Selection, typedListener);
            addListener(SWT.DefaultSelection, typedListener);

            prevMonth.addSelectionListener(theListener);
            prevYear.addSelectionListener(theListener);
            nextMonth.addSelectionListener(theListener);
            nextYear.addSelectionListener(theListener);
        }

        if (m_showTime) {
            m_hourSpinner.addSelectionListener(theListener);
            m_minuteSpinner.addSelectionListener(theListener);
        }
    }

    void dateSelected( boolean good ) {
        Event event = new Event();
        event.doit = good;
        m_DatePanel.redraw();
        notifyListeners(SWT.Selection, event);
    }

    public Date getDate() {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.MONTH, getMonth());
        date.set(Calendar.DATE, getDay());
        date.set(Calendar.YEAR, getYear());

        return new Date(date.getTime().getTime());
    }

    public int getDay() {
        return m_Day;
    }

    public int getHour() {
        return m_hourSpinner.getIntegerValue();
    }

    public int getMinute() {
        return m_minuteSpinner.getIntegerValue();
    }

    public int getMonth() {
        return m_Month;
    }

    private String getMonthName( int theMonth ) {
        return m_DatePanel.getMonthName(theMonth - 1);
    }

    public Time getTime() {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, m_hourSpinner.getIntegerValue());
        date.set(Calendar.MINUTE, m_minuteSpinner.getIntegerValue());
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return new Time(date.getTime().getTime());
    }

    public Timestamp getTimestamp() {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.MONTH, getMonth());
        date.set(Calendar.DATE, getDay());
        date.set(Calendar.YEAR, getYear());
        date.set(Calendar.HOUR_OF_DAY, m_hourSpinner.getIntegerValue());
        date.set(Calendar.MINUTE, m_minuteSpinner.getIntegerValue());
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return new Timestamp(date.getTime().getTime());
    }

    public int getYear() {
        return m_Year;
    }

    public boolean isDateWidget() {
        return (m_showCalendar && !m_showTime);
    }

    public boolean isTimeWidget() {
        return (!m_showCalendar && m_showTime);
    }

    public boolean isTimestampWidget() {
        return (m_showCalendar && m_showTime);
    }

    void normalizeDay() {
        // used in case an invalid day or month is set by the onKeyDown method (ex. day < 0 or > 31)
        Calendar calendar = Calendar.getInstance();

        if (m_showCalendar) {
            calendar.set(Calendar.YEAR, m_Year);
            calendar.set(Calendar.MONTH, m_Month);
            calendar.set(Calendar.DAY_OF_MONTH, m_Day);

            m_Year = calendar.get(Calendar.YEAR);
            m_Month = calendar.get(Calendar.MONTH);
            m_Day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        if (m_showTime) {
            calendar.set(Calendar.HOUR_OF_DAY, m_hourSpinner.getIntegerValue());
            calendar.set(Calendar.MINUTE, m_minuteSpinner.getIntegerValue());

            m_hourSpinner.setValue(calendar.get(Calendar.HOUR_OF_DAY));
            m_minuteSpinner.setValue(calendar.get(Calendar.MINUTE));
        }
    }

    void panelChanged() {
        if (m_showCalendar) {
            m_CalendarLabel.setText(getMonthName(m_Month + 1) + ", " + m_Year); //$NON-NLS-1$
        }
    }

    public void removeSelectionListener( SelectionListener theListener ) { // NO_UCD (Indicates this is ignored by unused code detection tool)
        checkWidget();

        if (theListener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        removeListener(SWT.Selection, theListener);
        removeListener(SWT.DefaultSelection, theListener);
    }

    // public void setDay(int theDay) {
    // m_Day = theDay;
    // panelChanged();
    // redraw();
    // }
    //    
    public void setValue( Date theDate ) {
        Calendar date = Calendar.getInstance();
        date.setTime(theDate);

        m_Day = date.get(Calendar.DATE);
        m_Month = date.get(Calendar.MONTH);
        m_Year = date.get(Calendar.YEAR);

        panelChanged();
    }

    public void setValue( Time theTime ) {
        Calendar date = Calendar.getInstance();
        date.setTime(theTime);

        m_hourSpinner.setValue(date.get(Calendar.HOUR_OF_DAY));
        m_minuteSpinner.setValue(date.get(Calendar.MINUTE));

        panelChanged();
    }

    public void setValue( Timestamp theTimestamp ) {
        Calendar date = Calendar.getInstance();
        date.setTime(theTimestamp);

        m_Day = date.get(Calendar.DATE);
        m_Month = date.get(Calendar.MONTH);
        m_Year = date.get(Calendar.YEAR);
        m_hourSpinner.setValue(date.get(Calendar.HOUR_OF_DAY));
        m_minuteSpinner.setValue(date.get(Calendar.MINUTE));

        panelChanged();
    }

    public void showCalendar( boolean theShowFlag ) {
        m_showCalendar = theShowFlag;
        pnlCalendar.setVisible(theShowFlag);
    }

    public void showTime( boolean theShowFlag ) {
        m_showTime = theShowFlag;
        pnlTime.setVisible(theShowFlag);
    }

    void updateDate() {
        normalizeDay();
        m_DatePanel.redraw();
        panelChanged();
    }

    private class TDatePanel extends Canvas {

        private Display m_Display = Display.getCurrent();
        private Calendar m_Calendar = Calendar.getInstance();
        private int m_ColSize;
        private int m_RowSize;
        private String[] m_Days = new String[7];
        private String[] m_Months = new String[12];

        public TDatePanel( Composite parent,
                           int style ) {
            super(parent, style | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE);
            Calendar cal = Calendar.getInstance();
            m_Year = cal.get(Calendar.YEAR);
            m_Month = cal.get(Calendar.MONTH);
            m_Day = cal.get(Calendar.DATE);

            DateFormat format = new SimpleDateFormat("EE"); //$NON-NLS-1$

            for (int i = 1; i <= 7; ++i) {
                cal.set(Calendar.DAY_OF_WEEK, i);
                m_Days[i - 1] = format.format(cal.getTime()).substring(0, 2);
            }
            format = new SimpleDateFormat("MMMM"); //$NON-NLS-1$

            for (int i = 0; i < 12; ++i) {
                cal.set(Calendar.MONTH, i);
                m_Months[i] = format.format(cal.getTime());
            }

            GC gc = new GC(this);
            Point p = gc.stringExtent("Q"); //$NON-NLS-1$
            gc.dispose();
            m_ColSize = p.x * 3;
            m_RowSize = (int)(p.y * 1.2);

            addPaintListener(new PaintListener() {
                public void paintControl( PaintEvent event ) {
                    onPaint(event);
                }
            });

            addControlListener(new ControlAdapter() {
                @Override
                public void controlResized( ControlEvent e ) {
                    redraw();
                }
            });

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed( KeyEvent e ) {
                    onKeyDown(e);
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseDown( MouseEvent e ) {
                    onMouseDown(e);
                }
            });
        }

        private int calendarDayToNormal( int theDay ) {
            switch (theDay) {
                case Calendar.SUNDAY:
                    return 0;
                case Calendar.MONDAY:
                    return 1;
                case Calendar.TUESDAY:
                    return 2;
                case Calendar.WEDNESDAY:
                    return 3;
                case Calendar.THURSDAY:
                    return 4;
                case Calendar.FRIDAY:
                    return 5;
                case Calendar.SATURDAY:
                    return 6;
            }

            return -1;
        }

        @Override
        public Point computeSize( int wHint,
                                  int hHint,
                                  boolean changed ) {
            Point size = pnlCalendar.getSize();
            m_ColSize = (int)(size.x / 7D);
            return new Point((int)(size.x / 7D * 7), m_RowSize * 7);
        }

        private int getDayFromPoint( int x,
                                     int y ) {
            for (int i = 1; i <= 31; i++) {
                Point p = getDayPoint(i);
                Rectangle r = new Rectangle(p.x, p.y, m_ColSize, m_RowSize);

                if (r.contains(x, y)) return i;
            }
            return -1;
        }

        private String getDayName( int day ) {
            return m_Days[day];
        }

        private Point getDayPoint( int day ) {
            m_Calendar.set(Calendar.YEAR, m_Year);
            m_Calendar.set(Calendar.MONTH, m_Month);
            m_Calendar.set(Calendar.DAY_OF_MONTH, 1);
            int first_day_of_week = calendarDayToNormal(m_Calendar.get(Calendar.DAY_OF_WEEK)) - 1;

            m_Calendar.set(Calendar.DAY_OF_MONTH, day);
            int day_of_week = calendarDayToNormal(m_Calendar.get(Calendar.DAY_OF_WEEK));
            int x = (day_of_week) * m_ColSize;
            int y = (1 + (first_day_of_week + day) / 7) * m_RowSize;
            return new Point(x, y);

        }

        private int getMaxDay() {
            m_Calendar.set(Calendar.YEAR, m_Year);
            m_Calendar.set(Calendar.MONTH, m_Month);
            int day = 28;
            for (int i = 0; i < 10; i++) {

                m_Calendar.set(Calendar.DAY_OF_MONTH, day);
                if (m_Calendar.get(Calendar.MONTH) != m_Month) return day - 1;
                day++;
            }

            return -1;
        }

        String getMonthName( int month ) {
            return m_Months[month];
        }

        void onKeyDown( KeyEvent theEvent ) {
            if (theEvent.character == SWT.ESC) {
                dateSelected(false);
                return;
            }

            if ((theEvent.character == ' ') || (theEvent.character == '\r')) {
                dateSelected(true);
                return;
            }

            int oldDay = m_Day;
            int oldMonth = m_Month;

            if (theEvent.keyCode == SWT.ARROW_LEFT) {
                m_Day--;
            } else if (theEvent.keyCode == SWT.ARROW_RIGHT) {
                m_Day++;
            } else if (theEvent.keyCode == SWT.ARROW_UP) {
                m_Day -= 7;

                if (m_Day < 1) {
                    m_Day = oldDay;
                }
            } else if (theEvent.keyCode == SWT.ARROW_DOWN) {
                m_Day += 7;

                if (m_Day > getMaxDay()) {
                    m_Day = oldDay;
                }
            } else if (theEvent.keyCode == SWT.PAGE_UP) {
                m_Month--;
            } else if (theEvent.keyCode == SWT.PAGE_DOWN) {
                m_Month++;
            }

            normalizeDay();

            if ((m_Day != oldDay) || (m_Month != oldMonth)) {
                redraw();

                if (m_Month != oldMonth) {
                    panelChanged();
                }
            }
        }

        void onMouseDown( MouseEvent theEvent ) {
            int day = getDayFromPoint(theEvent.x, theEvent.y);

            if (day > 0) {
                m_Day = day;
                dateSelected(true);
            }
        }

        void onPaint( PaintEvent event ) {
            Rectangle rect = getClientArea();
            GC gc0 = event.gc;
            Image image = new Image(m_Display, rect.width, rect.height);

            GC gc = new GC(image);
            gc.setBackground(m_Display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            gc.fillRectangle(rect);

            int x = 0;
            int y = 0;

            for (int i = 0; i < 7; i++) {
                if (i == 0) {
                    gc.setForeground(m_Display.getSystemColor(SWT.COLOR_RED));
                } else {
                    gc.setForeground(m_Display.getSystemColor(SWT.COLOR_BLACK));
                }

                String text = getDayName(i);
                Point size = gc.stringExtent(text);
                gc.drawText(text, x + (m_ColSize - size.x) / 2, (m_RowSize - size.y) / 2, true);
                x += m_ColSize;
            }

            gc.setForeground(m_Display.getSystemColor(SWT.COLOR_BLACK));
            y += m_RowSize;
            gc.drawLine(0, 0, rect.width, 0);
            gc.drawLine(0, y - 1, rect.width, y - 1);

            m_Calendar.set(Calendar.YEAR, m_Year);
            m_Calendar.set(Calendar.MONTH, m_Month);

            int day = 1;

            Font stdFont = gc.getFont();
            FontData fontData = stdFont.getFontData()[0];
            Font boldFont = new Font(null, fontData.getName(), fontData.getHeight(), SWT.BOLD);

            while (true) {
                m_Calendar.set(Calendar.DAY_OF_MONTH, day);

                if (m_Calendar.get(Calendar.MONTH) != m_Month) {
                    break;
                }

                int day_of_week = calendarDayToNormal(m_Calendar.get(Calendar.DAY_OF_WEEK));
                Point p = getDayPoint(day);

                if (day == m_Day) {
                    gc.setFont(boldFont);
                    gc.setForeground(m_Display.getSystemColor(SWT.COLOR_BLACK));
                    gc.setBackground(m_Display.getSystemColor(SWT.COLOR_LIST_SELECTION));
                } else {
                    gc.setFont(stdFont);
                    gc.setBackground(m_Display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

                    if (day_of_week == 0) {
                        gc.setForeground(m_Display.getSystemColor(SWT.COLOR_RED));
                    } else {
                        gc.setForeground(m_Display.getSystemColor(SWT.COLOR_BLACK));
                    }
                }

                String text = "" + day; //$NON-NLS-1$
                Point size = gc.stringExtent(text);
                gc.drawText(text, p.x + ((m_ColSize - size.x) / 2), p.y + ((m_RowSize - size.y) / 2), true);

                day++;
            }

            gc0.drawImage(image, 0, 0);
            gc.dispose();
            image.dispose();
        }
    }
}
