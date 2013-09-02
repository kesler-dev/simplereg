package org.kesler.simplereg.logic.applicator;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.annotations.Proxy;

@Entity
@Table (name = "ApplicatorsUL")
@PrimaryKeyJoinColumn(name="ID", referencedColumnName="ID")
@Proxy(lazy=false)
public class ApplicatorUL extends Applicator {
	
	@ManyToOne
	@JoinColumn(name="ULID")
	private UL ul;

	@ManyToOne
	@JoinColumn(name="RepresID")
	private FL repres;

	public ApplicatorUL() {}

	public UL getUL() {
		return ul;
	}

	public void setUL(UL ul) {
		this.ul = ul;
	} 

	public FL getRepres() {
		return repres;
	}

	public void setRepres(FL repres) {
		this.repres = repres;
	}

	@Override
	public String getName() {
		String name = "";

		return name;
	}
}