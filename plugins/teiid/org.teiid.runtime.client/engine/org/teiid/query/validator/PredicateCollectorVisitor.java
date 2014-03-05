/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.validator;

import java.util.ArrayList;
import java.util.Collection;
import org.teiid.designer.query.sql.IPredicateCollectorVisitor;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.PredicateCriteria;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.navigator.PreOrderNavigator;


/**
 * <p>Walk a tree of language objects and collect any predicate criteria that are found.
 * A predicate criteria is of the following types: </p>
 *
 * <ul>
 * <li>{@link CompareCriteria} CompareCriteria</li>
 * <li>{@link MatchCriteria} MatchCriteria</li>
 * <li>{@link SetCriteria} SetCriteria</li>
 * <li>{@link SubquerySetCriteria} SubquerySetCriteria</li>
 * <li>{@link IsNullCriteria} IsNullCriteria</li>
 * </ul>
 */
public class PredicateCollectorVisitor extends LanguageVisitor
    implements IPredicateCollectorVisitor<LanguageObject, Criteria> {

    private Collection<Criteria> predicates;

    /**
     * Construct a new visitor with the default collection type, which is a
     * {@link java.util.ArrayList}.
     * @param teiidVersion
     */
    public PredicateCollectorVisitor(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
        this.predicates = new ArrayList<Criteria>();
    }

    /**
     * Visit a language object and collect criteria.  This method should <b>NOT</b> be
     * called directly.
     * @param obj Language object
     */
    @Override
    public void visit(BetweenCriteria obj) {
        this.predicates.add(obj);
    }

    /**
     * Visit a language object and collect criteria.  This method should <b>NOT</b> be
     * called directly.
     * @param obj Language object
     */
    @Override
    public void visit(CompareCriteria obj) {
        this.predicates.add(obj);
    }

    /**
     * Visit a language object and collect criteria.  This method should <b>NOT</b> be
     * called directly.
     * @param obj Language object
     */
    @Override
    public void visit(IsNullCriteria obj) {
        this.predicates.add(obj);
    }

    /**
     * Visit a language object and collect criteria.  This method should <b>NOT</b> be
     * called directly.
     * @param obj Language object
     */
    @Override
    public void visit(MatchCriteria obj) {
        this.predicates.add(obj);
    }

    /**
     * Visit a language object and collect criteria.  This method should <b>NOT</b> be
     * called directly.
     * @param obj Language object
     */
    @Override
    public void visit(SetCriteria obj) {
        this.predicates.add(obj);
    }

    /**
     * @see LanguageVisitor#visit(ExistsCriteria)
     */
    @Override
    public void visit(ExistsCriteria obj) {
        this.predicates.add(obj);
    }

    /**
     * @see LanguageVisitor#visit(SubqueryCompareCriteria)
     */
    @Override
    public void visit(SubqueryCompareCriteria obj) {
        this.predicates.add(obj);
    }

	/**
	 * Visit a language object and collect criteria.  This method should <b>NOT</b> be
	 * called directly.
	 * @param obj Language object
	 */
	@Override
    public void visit(SubquerySetCriteria obj) {
		this.predicates.add(obj);
	}

    /**
     * Get a collection of predicates discovered while visiting.
     * @return Collection of {@link PredicateCriteria} subclasses.
     */
    public Collection<Criteria> getPredicates() {
        return this.predicates;
    }

    @Override
    public Collection<Criteria> findPredicates(LanguageObject obj) {
        if(obj != null) {
            PreOrderNavigator.doVisit(obj, this);
        }
        return getPredicates();
    }

    /**
     * Helper to quickly get the predicates from obj
     * @param obj Language object
     * @return collection of {@link Criteria} objects
     */
    public static final Collection<Criteria> getPredicates(LanguageObject obj) {
        PredicateCollectorVisitor visitor = new PredicateCollectorVisitor(obj.getTeiidVersion());
        return visitor.findPredicates(obj);
    }

}
