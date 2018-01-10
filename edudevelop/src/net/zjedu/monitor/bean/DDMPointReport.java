package net.zjedu.monitor.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("dd_mpoint_report")
public class DDMPointReport {
	@Column
	@Name
	private String jcddm ;
	@Column
	private String mpointname ;
	@Column
	private int mpoint_id;
	@Column
	private double mvalue_threshold ;
	@Column
	private String mremark;
	@Column
	private String jczbdm;
	public String getJcddm() {
		return jcddm;
	}
	public void setJcddm(String jcddm) {
		this.jcddm = jcddm;
	}
	public String getMpointname() {
		return mpointname;
	}
	public void setMpointname(String mpointname) {
		this.mpointname = mpointname;
	}
	public int getMpoint_id() {
		return mpoint_id;
	}
	public void setMpoint_id(int mpoint_id) {
		this.mpoint_id = mpoint_id;
	}
	public double getMvalue_threshold() {
		return mvalue_threshold;
	}
	public void setMvalue_threshold(double mvalue_threshold) {
		this.mvalue_threshold = mvalue_threshold;
	}
	public String getMremark() {
		return mremark;
	}
	public void setMremark(String mremark) {
		this.mremark = mremark;
	}
	public String getJczbdm() {
		return jczbdm;
	}
	public void setJczbdm(String jczbdm) {
		this.jczbdm = jczbdm;
	}
	
}
