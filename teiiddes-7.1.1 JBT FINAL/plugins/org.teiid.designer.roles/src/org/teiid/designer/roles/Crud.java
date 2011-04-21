/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.roles;

public class Crud {
	public enum Type
	{
	  CREATE, READ, UPDATE, DELETE 
	}

//	public static final int CREATE = 1;
//	public static final int READ = 2;
//	public static final int UPDATE = 3;
//	public static final int DELETE = 4;
	
	public Boolean c;
	public Boolean r;
	public Boolean u;
	public Boolean d;
	
	public Crud(Boolean c, Boolean r, Boolean u, Boolean d) {
		super();
		this.c = c;
		this.r = r;
		this.u = u;
		this.d = d;
	}
	
	public Crud(Crud crud) {
		this(crud.c, crud.r, crud.u, crud.d);
	}

	public boolean equivalent(Object obj) {
		if( obj instanceof Crud ) {
			Crud target = (Crud)obj;
			if( !areSameState(this.c, target.c) ) return false;
			if( !areSameState(this.r, target.r) ) return false; 
			if( !areSameState(this.u, target.u) ) return false; 
			if( !areSameState(this.d, target.d) ) return false; 
		}
		return super.equals(obj);
	}
	
	private boolean areSameState(Boolean a, Boolean b ) {
		if( (a == null && b == null) || 
			(a == null && b == Boolean.FALSE) || 
			(b == null && a == Boolean.FALSE) ) {
			return true;
		}

		return false;
	}
	
	private boolean areSameBooleanValues(Boolean a, Boolean b ) {
		if( (a == null && b == null) || 
			(a == Boolean.FALSE && b == Boolean.FALSE) || 
			(a == Boolean.TRUE && b == Boolean.TRUE) ) {
			return true;
		}

		return false;
	}
	
	public boolean isSameAs(Crud target) {
		if( areSameBooleanValues(this.c, target.c) &&
			areSameBooleanValues(this.r, target.r) &&
			areSameBooleanValues(this.u, target.u) &&
			areSameBooleanValues(this.d, target.d) ) {
			return true;
		}
		return false;
	}
	
	public static Crud.Type getCrudType(int value ) {
		if( value == 1 ) {
			return Crud.Type.CREATE;
		}
		if( value == 2 ) {
			return Crud.Type.READ;
		}
		if( value == 3 ) {
			return Crud.Type.UPDATE;
		}
		if( value == 4 ) {
			return Crud.Type.DELETE;
		}
		return null;
	}
}
