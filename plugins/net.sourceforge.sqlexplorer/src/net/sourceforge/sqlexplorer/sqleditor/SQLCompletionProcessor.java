package net.sourceforge.sqlexplorer.sqleditor;

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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.dbviewer.model.IDbModel;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.dbviewer.model.TableObjectTypeNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.model.utility.Dictionary;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class SQLCompletionProcessor implements IContentAssistProcessor {

    private Image colImage;
    private Image tableImage;
    private Image viewImage;
    private Image keywordImage;
    private Image catalogImage;
    static String sep = System.getProperty("line.separator");

    public void dispose() {
        if (catalogImage != null) {
            try {
                catalogImage.dispose();
                catalogImage = null;
            } catch (Throwable e) {
                SQLExplorerPlugin.error("Error disposing images", e); //$NON-NLS-1$
            }
        }
        if (keywordImage != null) {
            try {
                keywordImage.dispose();
                keywordImage = null;
            } catch (Throwable e) {
                SQLExplorerPlugin.error("Error disposing images", e); //$NON-NLS-1$
            }
        }
        if (colImage != null) {
            try {
                colImage.dispose();
                colImage = null;
            } catch (Throwable e) {
                SQLExplorerPlugin.error("Error disposing images", e); //$NON-NLS-1$
            }
        }
        if (tableImage != null) {
            try {
                tableImage.dispose();
                tableImage = null;
            } catch (Throwable e) {
                SQLExplorerPlugin.error("Error disposing images", e); //$NON-NLS-1$
            }
        }
        if (viewImage != null) {
            try {
                viewImage.dispose();
                viewImage = null;
            } catch (Throwable e) {
                SQLExplorerPlugin.error("Error disposing images", e); //$NON-NLS-1$
            }
        }
    }

    private Dictionary dictionary;

    public SQLCompletionProcessor( Dictionary dictionary ) {
        this.dictionary = dictionary;

        try {
            colImage = new Image(null, SqlexplorerImages.getColumnIcon().openStream());
            tableImage = new Image(null, SqlexplorerImages.getTableIcon().openStream());
            viewImage = new Image(null, SqlexplorerImages.getViewIcon().openStream());
            keywordImage = new Image(null, SqlexplorerImages.getKeywordIcon().openStream());
            catalogImage = new Image(null, SqlexplorerImages.getDatabaseNodeIcon().openStream());
        } catch (Throwable e) {
            SQLExplorerPlugin.error("Error creating images", e); //$NON-NLS-1$
        }
    }

    private char[] fProposalAutoActivationSet;

    public void setCompletionProposalAutoActivationCharacters( char[] activationSet ) {
        fProposalAutoActivationSet = activationSet;
    }

    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer,
     *      int)
     */
    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer,
                                                             int documentOffset ) {
        if (dictionary == null) return null;
        String text = viewer.getDocument().get();
        String string = text.substring(0, documentOffset);

        if (string.equals("")) //$NON-NLS-1$
        return null;
        int position = string.length() - 1;
        char character;

        while (position > 0) {
            character = string.charAt(position);
            if (!Character.isJavaIdentifierPart(character) && (character != '.')) break;
            --position;
        }
        if (position == 0) position = -1;
        string = string.substring(position + 1);
        // JFaceDbcPlugin.error("String: "+string,new Exception());
        if (string == null || string.equals("")) return null;

        string = string.toLowerCase();

        int length = string.length();
        if (length < 1) return null;
        int dotIndex = string.lastIndexOf("."); //$NON-NLS-1$
        if (string.charAt(length - 1) == ' ') {
            return null;
        } else if (string.charAt(length - 1) == '.') {// Last typed character is '.'
            String name = string.substring(0, length - 1);
            if (name == null) return null;
            int otherDot = name.lastIndexOf(".");
            if (otherDot != -1) name = name.substring(otherDot + 1);
            if (name == null || name.equals("")) return null;
            TreeSet st = (TreeSet)dictionary.getColumnListByTableName(name);
            if (st != null) {
                ArrayList list = (ArrayList)dictionary.getByTableName(name);
                if (list == null) return null;
                TableNode nd = null;
                if (list.size() == 1) nd = (TableNode)list.get(0);
                else return null;
                Object[] obj = st.toArray();
                String[] arr = new String[obj.length];
                System.arraycopy(obj, 0, arr, 0, obj.length);

                ICompletionProposal[] result = new ICompletionProposal[arr.length];
                String tableDesc = null;
                if (nd != null) tableDesc = nd.getTableDesc();
                for (int i = 0; i < arr.length; i++) {
                    result[i] = new CompletionProposal(arr[i], documentOffset, 0, arr[i].length(), colImage, arr[i], null,
                                                       tableDesc);
                }
                return result;
            }
            IDbModel node = (IDbModel)dictionary.getByCatalogSchemaName(name);
            if (node != null) {
                Object children[] = node.getChildren();
                ArrayList propList = new ArrayList();
                for (int i = 0; i < children.length; i++) {
                    String childName = children[i].toString().toLowerCase();
                    if (childName.equals("table") || childName.equals("view")) {
                        Object[] tables = ((IDbModel)children[i]).getChildren();
                        for (int j = 0; j < tables.length; j++) {
                            Image tmpImage = null;
                            String tableName = tables[j].toString();
                            if (tables[j] instanceof TableNode) {
                                if (((TableNode)tables[j]).isTable()) tmpImage = tableImage;
                                else if (((TableNode)tables[j]).isView()) tmpImage = viewImage;
                                propList.add(new ExtendedCompletionProposal(tableName, documentOffset, 0, tableName.length(),
                                                                            tmpImage, tableName, (TableNode)tables[j]));
                            }

                        }
                    }
                }
                ICompletionProposal[] res = new ICompletionProposal[propList.size()];
                System.arraycopy(propList.toArray(), 0, res, 0, propList.size());
                Arrays.sort(res, new ICompletionProposalComparator());
                return res;
            }
        } else if (dotIndex == -1)// The string does not contain "."
        {
            String[] keywordProposal = Dictionary.matchKeywordsPrefix(string);
            ICompletionProposal[] resKey = new ICompletionProposal[keywordProposal.length];
            for (int i = 0; i < keywordProposal.length; i++) {
                resKey[i] = new CompletionProposal(keywordProposal[i], documentOffset - length, length,
                                                   keywordProposal[i].length(), keywordImage, keywordProposal[i], null, null);
            }
            // JFaceDbcPlugin.error("Completion "+keywordProposal.length,new Exception());
            String[] proposalsString = dictionary.matchTablePrefix(string.toLowerCase());

            ArrayList propList = new ArrayList();
            for (int i = 0; i < proposalsString.length; i++) {
                ArrayList ls = dictionary.getTableObjectList(proposalsString[i]);
                for (int j = 0; j < ls.size(); j++) {
                    // Object nd=ls.get(j);
                    TableNode tbNode = (TableNode)ls.get(j);
                    Image tmpImage = null;
                    if (tbNode.isView()) tmpImage = viewImage;
                    else if (tbNode.isTable()) 
                    tmpImage = tableImage;

                    // try{
                    // tbNode.getParent()
                    // ContextInformation cInfo=new ContextInformation(tmpImage,proposalsString[i],proposalsString[i]);
                    ICompletionProposal cmp = new ExtendedCompletionProposal(proposalsString[i], documentOffset - length, length,
                                                                             proposalsString[i].length(), tmpImage,
                                                                             proposalsString[i], tbNode);
                    propList.add(cmp);
                    // }catch(Exception e){
                    //	JFaceDbcPlugin.error("Error calling completion proposal",e); //$NON-NLS-1$
                    // }
                }
            }
            String[] proposalsString2 = dictionary.matchCatalogSchemaPrefix(string.toLowerCase());
            ICompletionProposal[] resKey2 = new ICompletionProposal[proposalsString2.length];
            for (int i = 0; i < proposalsString2.length; i++) {
                resKey2[i] = new CompletionProposal(proposalsString2[i], documentOffset - length, length,
                                                    proposalsString2[i].length(), catalogImage, proposalsString2[i], null, null);
            }

            ICompletionProposal[] res = new ICompletionProposal[propList.size() + keywordProposal.length + resKey2.length];
            System.arraycopy(resKey, 0, res, 0, resKey.length);
            System.arraycopy(propList.toArray(), 0, res, resKey.length, propList.size());
            System.arraycopy(resKey2, 0, res, resKey.length + propList.size(), resKey2.length);
            Arrays.sort(res, new ICompletionProposalComparator());
            return res;
        } else if (dotIndex != -1) {
            String firstPart = string.substring(0, dotIndex);
            int otherDot = firstPart.indexOf(".");
            if (otherDot != -1) firstPart = firstPart.substring(otherDot + 1);
            String lastPart = string.substring(dotIndex + 1);
            if (lastPart == null || firstPart == null || lastPart.equals("") || firstPart.equals("")) return null;
            TreeSet st = (TreeSet)dictionary.getColumnListByTableName(firstPart);
            if (st != null) {
                Iterator iter = st.iterator();
                ArrayList propList = new ArrayList();
                while (iter.hasNext()) {
                    String colName = (String)iter.next();
                    int length2 = lastPart.length();
                    if (colName.length() >= length2) {
                        if ((colName.substring(0, lastPart.length())).equalsIgnoreCase(lastPart)) {
                            CompletionProposal cmp = new CompletionProposal(colName, documentOffset - length2, length2,
                                                                            colName.length(), colImage, colName, null, null);
                            propList.add(cmp);
                        }
                    }
                }
                ICompletionProposal[] res = new ICompletionProposal[propList.size()];
                System.arraycopy(propList.toArray(), 0, res, 0, propList.size());
                return res;
            }
            IDbModel node = (IDbModel)dictionary.getByCatalogSchemaName(firstPart);
            if (node != null) {
                String[] proposalsString = dictionary.matchTablePrefix(lastPart.toLowerCase());
                ArrayList propList = new ArrayList();
                for (int i = 0; i < proposalsString.length; i++) {
                    ArrayList ls = dictionary.getTableObjectList(proposalsString[i]);
                    for (int j = 0; j < ls.size(); j++) {
                        TableNode tbNode = (TableNode)ls.get(j);
                        Image tmpImage = null;
                        TableObjectTypeNode totn = (TableObjectTypeNode)tbNode.getParent();
                        // String title=totn.getTitle();
                        IDbModel catSchema = (IDbModel)totn.getParent();
                        if (catSchema == node) {
                            if (tbNode.isView()) tmpImage = viewImage;
                            else if (tbNode.isTable()) tmpImage = tableImage;
                            ICompletionProposal cmp = new ExtendedCompletionProposal(proposalsString[i], documentOffset
                                                                                                         - lastPart.length(),
                                                                                     lastPart.length(),
                                                                                     proposalsString[i].length(), tmpImage,
                                                                                     proposalsString[i], tbNode);
                            propList.add(cmp);
                        }
                    }
                }
                ICompletionProposal[] res = new ICompletionProposal[propList.size()];
                System.arraycopy(propList.toArray(), 0, res, 0, propList.size());
                return res;
            }
        }
        return null;

    }

    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer,
     *      int)
     */
    public IContextInformation[] computeContextInformation( ITextViewer arg0,
                                                            int arg1 ) {
        return null;
    }

    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
     */
    public char[] getCompletionProposalAutoActivationCharacters() {
        return fProposalAutoActivationSet;
    }

    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
     */
    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
     */
    public String getErrorMessage() {
        return null;
    }

    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
     */
    public IContextInformationValidator getContextInformationValidator() {
        return null;
    }

}

class ExtendedCompletionProposal implements ICompletionProposal {
    CompletionProposal compProposal;
    TableNode tn;

    public ExtendedCompletionProposal( String proposalsString,
                                       int i,
                                       int j,
                                       int k,
                                       Image tmpImage,
                                       String str,
                                       TableNode tb ) {
        compProposal = new CompletionProposal(proposalsString, i, j, k, tmpImage, str, null, null);
        this.tn = tb;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
     */
    public void apply( IDocument document ) {
        compProposal.apply(document);

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
     */
    public Point getSelection( IDocument document ) {
        return compProposal.getSelection(document);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
     */
    public String getAdditionalProposalInfo() {
        return tn.getTableDesc();

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
     */
    public String getDisplayString() {
        return compProposal.getDisplayString();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
     */
    public Image getImage() {
        return compProposal.getImage();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
     */
    public IContextInformation getContextInformation() {
        return compProposal.getContextInformation();
    }
}

class ICompletionProposalComparator implements Comparator {

    public int compare( Object o1,
                        Object o2 ) {
        ICompletionProposal i1 = (ICompletionProposal)o1;
        ICompletionProposal i2 = (ICompletionProposal)o2;
        String s1 = i1.getDisplayString();
        String s2 = i2.getDisplayString();
        return Collator.getInstance().compare(s1, s2);

    }
}
