package net.zjedu.monitor.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

@Table("datatable_value")
public class DataValue {
	@Column("xzqhdm")
	private String xzqhdm;
	@Column("dt_year")
	private int year;
	@One(target=Xzqh.class,field="xzqhdm") 
	private String xzqhmc;
	@Column("dt_value")
	private double value;
	@Column("code")
	private String dt_cd;
	public String getXzqhdm() {
		return xzqhdm;
	}
	public void setXzqhdm(String xzqhdm) {
		this.xzqhdm = xzqhdm;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getXzqhmc() {
		return xzqhmc;
	}
	public void setXzqhmc(String xzqhmc) {
		this.xzqhmc = xzqhmc;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String getDt_cd() {
		return dt_cd;
	}
	public void setDt_cd(String dt_cd) {
		this.dt_cd = dt_cd;
	}
	
}
