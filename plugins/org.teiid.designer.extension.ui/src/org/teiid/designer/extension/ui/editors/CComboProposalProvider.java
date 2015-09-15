/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.ui.editors;

import java.util.List;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.IControlContentAdapter2;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.teiid.core.designer.util.StringConstants;

/**
 * Provides matching CCombo items based on keystrokes for a readonly combo.
 */
abstract class CComboProposalProvider implements IControlContentAdapter, IControlContentAdapter2, IContentProposalProvider {

    private static final long DEFAULT_DURATION = 500;

    private final CCombo combo;
    private long duration = DEFAULT_DURATION;
    private String pattern = StringConstants.EMPTY_STRING;
    private long t1 = System.currentTimeMillis();
    private long t2;

    /**
     * @param combo the combo whose proposals are being requested (cannot be <code>null</code>)
     */
    protected CComboProposalProvider(final CCombo combo) {
        this(combo, DEFAULT_DURATION);
    }

    /**
     * @param combo the combo whose proposals are being requested (cannot be <code>null</code>)
     * @param duration the amount of time to wait before resetting the pattern used to match items
     */
    protected CComboProposalProvider(final CCombo combo,
                                     final long duration) {
        this.duration = duration;
        this.combo = combo;
        combo.addKeyListener(new KeyAdapter() {

            /**
             * @see org.eclipse.swt.events.KeyAdapter#keyPressed(org.eclipse.swt.events.KeyEvent)
             */
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
        });
    }

    /**
     * @return the characters that trigger auto-activation of proposals (can be <code>null</code>)
     */
    protected abstract char[] getActivationChars();

    /**
     * @see org.eclipse.jface.fieldassist.IControlContentAdapter#getControlContents(org.eclipse.swt.widgets.Control)
     */
    @Override
    public final String getControlContents(final Control control) {
        return this.combo.getText();
    }

    /**
     * @see org.eclipse.jface.fieldassist.IControlContentAdapter#getCursorPosition(org.eclipse.swt.widgets.Control)
     */
    @Override
    public final int getCursorPosition(final Control control) {
        return this.combo.getSelection().x;
    }

    /**
     * @see org.eclipse.jface.fieldassist.IControlContentAdapter#getInsertionBounds(org.eclipse.swt.widgets.Control)
     */
    @Override
    public final Rectangle getInsertionBounds(final Control control) {
        final int position = this.combo.getSelection().y;
        final String contents = this.combo.getText();

        final GC gc = new GC(this.combo);
        gc.setFont(this.combo.getFont());

        final Point extent = gc.textExtent(contents.substring(0, Math.min(position, contents.length())));
        gc.dispose();

        return new Rectangle(this.combo.getClientArea().x + extent.x, this.combo.getClientArea().y, 1,
                             this.combo.getClientArea().height);
    }

    /**
     * @see org.eclipse.jface.fieldassist.IContentProposalProvider#getProposals(java.lang.String, int)
     */
    @Override
    public final IContentProposal[] getProposals(final String contents,
                                                 final int position) {
        if (this.combo.getListVisible()) {
            return new IContentProposal[0];
        }

        final List<String> matches = proposalsFor(this.pattern);

        if (matches.isEmpty()) {
            return new IContentProposal[0];
        }

        final IContentProposal[] proposals = new IContentProposal[matches.size()];
        int i = 0;

        for (final String match : matches) {
            proposals[i++] = new ContentProposal(match);
        }

        return proposals;
    }

    /**
     * @see org.eclipse.jface.fieldassist.IControlContentAdapter2#getSelection(org.eclipse.swt.widgets.Control)
     */
    @Override
    public final Point getSelection(final Control control) {
        return this.combo.getSelection();
    }

    final void handleKeyPressed(final KeyEvent e) {
        this.t2 = System.currentTimeMillis();

        if ((this.t2 - this.t1) > this.duration) {
            this.pattern = StringConstants.EMPTY_STRING;
        }

        this.t1 = this.t2;

        if (!Character.isLetter(e.character)) {
            this.pattern = StringConstants.EMPTY_STRING;
        } else {
            this.pattern += e.character;
        }
    }

    /**
     * Initializes the proposal provider. Must be called after construction.
     */
    final void init() {
        final ContentProposalAdapter proposalAdapter = new ContentProposalAdapter(this.combo, this, this, null,
                                                                                  getActivationChars());
        proposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
    }

    /**
     * @see org.eclipse.jface.fieldassist.IControlContentAdapter#insertControlContents(org.eclipse.swt.widgets.Control, java.lang.String, int)
     */
    @Override
    public final void insertControlContents(final Control control,
                                            final String text,
                                            final int cursorPosition) {
        final String contents = this.combo.getText();
        final Point selection = this.combo.getSelection();

        final StringBuilder builder = new StringBuilder();
        builder.append(contents.substring(0, selection.x)).append(text);

        if (selection.y < contents.length()) {
            builder.append(contents.substring(selection.y, contents.length()));
        }

        setText(builder.toString());
        //
        //        selection.x = (selection.x + cursorPosition);
        //        selection.y = selection.x;
        //        this.combo.setSelection(selection);
    }

    /**
     * @param pattern the pattern to use (can be <code>null</code> or empty)
     * @return the proposals that match (never <code>null</code> but can be empty)
     */
    protected abstract List<String> proposalsFor(final String pattern);

    /**
     * @see org.eclipse.jface.fieldassist.IControlContentAdapter#setControlContents(org.eclipse.swt.widgets.Control, java.lang.String, int)
     */
    @Override
    public final void setControlContents(final Control control,
                                         final String contents,
                                         final int cursorPosition) {
        setText(contents);
        setCursorPosition(control, cursorPosition);
    }

    /**
     * @see org.eclipse.jface.fieldassist.IControlContentAdapter#setCursorPosition(org.eclipse.swt.widgets.Control, int)
     */
    @Override
    public final void setCursorPosition(final Control control,
                                        final int index) {
        this.combo.setSelection(new Point(index, index));
    }

    /**
     * @see org.eclipse.jface.fieldassist.IControlContentAdapter2#setSelection(org.eclipse.swt.widgets.Control, org.eclipse.swt.graphics.Point)
     */
    @Override
    public final void setSelection(final Control control,
                                   final Point range) {
        this.combo.setSelection(range);
    }

    private void setText(final String newSelection) {
        final int index = this.combo.indexOf(newSelection);

        if (index != -1) {
            this.combo.select(index);
        }
    }
}
