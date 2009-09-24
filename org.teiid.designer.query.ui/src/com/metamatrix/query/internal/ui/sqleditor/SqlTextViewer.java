/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.metamatrix.query.internal.ui.sqleditor;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import com.metamatrix.query.internal.ui.sqleditor.sql.ColorManager;
import com.metamatrix.query.internal.ui.sqleditor.sql.SqlPartitionScanner;

public class SqlTextViewer extends SourceViewer {

    IPresentationReconciler fPresentationReconciler;
    private IDocumentPartitioner partitioner;
    private static SqlPartitionScanner scanner = null;
    private final static String[] TYPES =
        new String[] { SqlPartitionScanner.SQL_CODE, SqlPartitionScanner.SQL_MULTILINE_COMMENT };

    //IPreferenceStore store;
    IAutoEditStrategy autoIndentStrategy;
    ITextDoubleClickStrategy doubleClickStrategy;
    IContentAssistant contentAssistant;
    ITextHover textHover;

    int iFormatDocumentCallCounter = 0;

    //private AnnotationModel fVisualAnnotationModel;
    SqlSourceViewerConfiguration configuration;

    @Override
    public void setDocument(IDocument dc) {
        //s_log.debug("setDocument:"+dc);
        IDocument previous = this.getDocument();
        if (previous != null) {
            partitioner.disconnect();
        }

        super.setDocument(dc);
        if (dc != null) {
            //s_log.debug("setDocument connect:"+dc);
            partitioner.connect(dc);
            dc.setDocumentPartitioner(partitioner);
        }

    }

    public SqlTextViewer(Composite parent, IVerticalRuler ruler, int style, ColorManager colorManager) {
        super(parent, ruler, style);

        configuration = new SqlSourceViewerConfiguration(colorManager);

        fPresentationReconciler = configuration.getPresentationReconciler(null);
        contentAssistant = configuration.getContentAssistant(null);
        if (contentAssistant != null) {
            contentAssistant.install(this);
        }

        if (fPresentationReconciler != null)
            fPresentationReconciler.install(this);

        doubleClickStrategy = configuration.getDoubleClickStrategy(null, ""); //$NON-NLS-1$

        autoIndentStrategy = configuration.getAutoEditStrategies(this, IDocument.DEFAULT_CONTENT_TYPE)[0];
        setAutoEditStrategies(new IAutoEditStrategy[] {autoIndentStrategy}, IDocument.DEFAULT_CONTENT_TYPE);

        textHover = configuration.getTextHover(this, IDocument.DEFAULT_CONTENT_TYPE);
        setTextHover(textHover, IDocument.DEFAULT_CONTENT_TYPE);

        partitioner = createSqlPartitioner();
        parent.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent event) {
                contentAssistant.uninstall();
            }
        });
    }

    /**
     * Return a partitioner for SQL files.
     */
    private FastPartitioner createSqlPartitioner() {
		return new FastPartitioner(getSqlPartitionScanner(), TYPES);
    }

    /**
     * Return a scanner for SQL partitions.
     */
    private SqlPartitionScanner getSqlPartitionScanner() {
        if (scanner == null)
            scanner = new SqlPartitionScanner();
        return scanner;
    }

    public void showAssistance() {
        contentAssistant.showPossibleCompletions();
    }

    public void handleDoubleClick() {
        doubleClickStrategy.doubleClicked(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.ISourceViewer#setDocument(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.source.IAnnotationModel)
     */
    @Override
    public void setDocument(IDocument document, IAnnotationModel annotationModel) {

        // original code:
        setDocument(document);

        if (annotationModel != null && document != null)
            annotationModel.connect(document);
    }

}
