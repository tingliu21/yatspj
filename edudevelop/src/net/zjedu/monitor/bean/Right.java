package net.zjedu.monitor.bean;

import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table ("t_rights")
public class Right {
	@Id
	@Column("rightid")
	private int id;
	@Column("rightname")
	private String text;
	@Column("parentid")
	private int parentid;
	
	private List<Right> children;
	
	
	public int getRightid() {
		return id;
	}
	public void setRightid(int rightid) {
		this.id = rightid;
	}
	public String getRightname() {
		return text;
	}
	public void setRightname(String rightname) {
		this.text = rightname;
	}
	public int getParentid() {
		return parentid;
	}
	public void setParentid(int parentid) {
		this.parentid = parentid;
	}
	public List<Right> getChildRights() {
		return children;
	}
	public void setChildRights(List<Right> childRights) {
		this.children = childRights;
	}
}
