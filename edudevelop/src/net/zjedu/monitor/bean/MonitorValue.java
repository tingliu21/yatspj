package net.zjedu.monitor.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

@Table("monitor_value")
public class MonitorValue {
	@Column("xzqhdm")
	private String xzqhdm;
	@Column("m_year")
	private int year;
	@One(target=Xzqh.class,field="xzqhdm") 
	private String xzqhmc;
	@Column("m_value")
	private double value;
	@Column("m_score")
	private double score;
	@Column("mpoint_cd")
	private String mpoint_cd;
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
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getMpoint_cd() {
		return mpoint_cd;
	}
	public void setMpoint_cd(String mpoint_cd) {
		this.mpoint_cd = mpoint_cd;
	}
}
