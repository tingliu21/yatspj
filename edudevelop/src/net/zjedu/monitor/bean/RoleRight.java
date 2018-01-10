package net.zjedu.monitor.bean;

import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Readonly;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.View;
@Table("r_role_right")
@View("v_role_right")
@PK( {"roleid", "rightid"} )
public class RoleRight {
	

	@Column
	private int roleid;
	@Column
	private int rightid;
	@Column
	@Readonly 
	private String rightname;
	
	public RoleRight() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RoleRight(int roleid, int rightid) {
		super();
		this.roleid = roleid;
		this.rightid = rightid;
	}
	public int getRoleid() {
		return roleid;
	}
	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}
	public int getRightid() {
		return rightid;
	}
	public void setRightid(int rightid) {
		this.rightid = rightid;
	}
	public String getRightname() {
		return rightname;
	}
	public void setRightname(String rightname) {
		this.rightname = rightname;
	}
	
	
}
