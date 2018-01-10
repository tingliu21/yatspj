package net.zjedu.monitor.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

@Table("result_temp")
public class Result {
	@Column("xzqhmc")
	private String name;
	@Column
	private double value_t;
	@Column
	private double value_yx;
	@Column
	private double value_yr;
	@Column
	private double value_cj;
	@Column
	private double value_jy;
	public String getXzqhmc() {
		return name;
	}
	public void setXzqhmc(String xzqhmc) {
		this.name = xzqhmc;
	}
	public double getValue_t() {
		return value_t;
	}
	public void setValue_t(double value_t) {
		this.value_t = value_t;
	}
	public double getValue_yx() {
		return value_yx;
	}
	public void setValue_yx(double value_yx) {
		this.value_yx = value_yx;
	}
	public double getValue_yr() {
		return value_yr;
	}
	public void setValue_yr(double value_yr) {
		this.value_yr = value_yr;
	}
	public double getValue_cj() {
		return value_cj;
	}
	public void setValue_cj(double value_cj) {
		this.value_cj = value_cj;
	}
	public double getValue_jy() {
		return value_jy;
	}
	public void setValue_jy(double value_jy) {
		this.value_jy = value_jy;
	}
	
}
