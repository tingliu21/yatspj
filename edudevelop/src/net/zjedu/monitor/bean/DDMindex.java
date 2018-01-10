package net.zjedu.monitor.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("dd_mindex")
public class DDMindex {
	@Column("jczbdm")
	@Name
	private String jczbdm;
	@Column("indexname")
	private String indexname;
	@Column("point")
	private double point;
	@Column("ilevel")
	private int level;
	@Column("p_jczbdm")
	private String pjczbdm;
	
	public String getJczbdm() {
		return jczbdm;
	}
	public void setJczbdm(String jczbdm) {
		this.jczbdm = jczbdm;
	}
	public String getIndexname() {
		return indexname;
	}
	public void setIndexname(String indexname) {
		this.indexname = indexname;
	}
	public double getPoint() {
		return point;
	}
	public void setPoint(double point) {
		this.point = point;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getPjczbdm() {
		return pjczbdm;
	}
	public void setPjczbdm(String pjczbdm) {
		this.pjczbdm = pjczbdm;
	}
	
}
