package net.zjedu.monitor.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

@Table("monitor_index")
public class MonitorIndex {
	@Column("weight")
	private double weight;
	@Column("mindex_cd")
	private String mindex_cd;
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public String getMindex_cd() {
		return mindex_cd;
	}
	public void setMindex_cd(String mindex_cd) {
		this.mindex_cd = mindex_cd;
	}
}
