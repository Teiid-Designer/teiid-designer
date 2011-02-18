/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/** A Category is a way multiple component representations of data values
  *  can be grouped together into the same section.
  * @author PForhan
  */
public class ComponentCategory implements Cloneable {
    //
    // Instance variables:
    //
    private final String  name;
    private final String  id;
    private final String  description;
    private final boolean userCollapsable;
    private List    componentSets = new ArrayList(); // list of linkedComponentSets, kept so that GUI components come up in the same order
    private Section section;
    private Composite sectionBody;
    private ScrolledForm formParent;
    private boolean visible = true;
    private boolean enabled = true;
    
    //
    // Constructors:
    //
    public ComponentCategory(String id, String name, String description, boolean userCollapsable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userCollapsable = userCollapsable;
    }

    //
    // Data methods:
    //
    public String getDescription() {
        return description;
    }
    
    public String getID() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isUserCollapsable() {
        return userCollapsable;
    }

    //
    // View methods:
    //
    
    /**
      * @return the parent containing the added controls.
      */
    public Composite addFormControl(final ScrolledForm parentForm, FormToolkit ftk) {
        // remember the parent:
        this.formParent = parentForm;

        // determine the column count:
        int containedCount = componentSets.size();

        if (name.length() > 0) {
            // create the section style:
            int sectionStyle = ExpandableComposite.TITLE_BAR;

            if (containedCount > 0) {
                // only expand if components are present:
                sectionStyle |= ExpandableComposite.EXPANDED;
            } // endif

            if (userCollapsable) {
                // add support for user twisting:
                sectionStyle |= ExpandableComposite.TWISTIE;// | Section.CLIENT_INDENT;
            } // endif

            if (description.length() > 0) {
                // display the description:
                sectionStyle |= Section.DESCRIPTION;
            } // endif

            // set up section:
            section = ftk.createSection(parentForm.getBody(), sectionStyle);
            section.setText(name);
            section.setDescription(description);

            // add expansion listener if needed:
            if (userCollapsable) {
                section.addExpansionListener(new ExpansionAdapter() {
                    @Override
                    public void expansionStateChanged(ExpansionEvent e) {
                        // long time = System.currentTimeMillis();
                        parentForm.reflow(true);
                        // System.out.println("Reflow time: "+ (System.currentTimeMillis() - time));
                    }
                });
            } // endif

            sectionBody = ftk.createComposite(section);
        } else {
            // not using a section, just do a composite:
            sectionBody = ftk.createComposite(parentForm.getBody());
        } // endif
        
        // set up body of section:---------------------
        
        // determine column count:
        int columns = 0;
        for (int i = 0; i < containedCount; i++) {
            LinkedComponentSet lcs = (LinkedComponentSet) componentSets.get(i);
            int colCnt = lcs.getControlCount();
            if (colCnt > columns) {
                columns = colCnt;
            } // endif
        } // endfor

        TableWrapLayout tl = new TableWrapLayout();
        tl.numColumns = columns;
        sectionBody.setLayout(tl);

        for (int i = 0; i < containedCount; i++) {
            LinkedComponentSet lcs = (LinkedComponentSet) componentSets.get(i);
            lcs.addFormControls(sectionBody, ftk, columns);
            lcs.setEditible(enabled);
        } // endfor

        sectionBody.pack(true);

        if (section != null) {
            section.setClient(sectionBody);
            section.pack(true);

            section.setVisible(visible);
            section.setExpanded(visible);

            return section;
        } // endif
        
        return sectionBody;
    }

//    public boolean isVisible() {
//        return visible;
//    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;

            if (formParent != null && !formParent.isDisposed()) {
                // see what to change:
                if (section != null) {
                    // change section:
                    section.setExpanded(visible);
                    section.setVisible(visible);
                } else {
                    // no section, just body:
                    sectionBody.setVisible(visible);
                } // endif
                formParent.reflow(true);
            } // endif
        } // endif
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        for (int i = 0; i < componentSets.size(); i++) {
            LinkedComponentSet lcs = (LinkedComponentSet) componentSets.get(i);
            lcs.setEditible(enabled);
        } // endfor
    }

    public void addComponentSet(LinkedComponentSet lcs) {
        componentSets.add(lcs);
    }

    public List getComponentSets() {
        return componentSets;
    }
    
    public void setMonitor(ComponentSetMonitor mon) {
        Iterator itor = componentSets.iterator();
        while (itor.hasNext()) {
            LinkedComponentSet lcs = (LinkedComponentSet) itor.next();
            lcs.setMonitor(mon);
        } // endwhile
    }

    //
    // Overrides:
    //
    @Override
    public Object clone() throws CloneNotSupportedException {
        ComponentCategory cc = (ComponentCategory) super.clone();
        cc.componentSets = new ArrayList(cc.componentSets);

        for (int i = 0; i < cc.componentSets.size(); i++) {
            LinkedComponentSet lcs = (LinkedComponentSet) cc.componentSets.get(i);
            cc.componentSets.set(i, lcs.cloneSet());
        } // endfor

        return cc;
    }

    public void reflowForm() {
        if (formParent != null) {
            formParent.reflow(true);
        } // endif
    }
}
