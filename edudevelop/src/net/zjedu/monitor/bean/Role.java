package net.zjedu.monitor.bean;

import java.util.Date;
import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table ("t_roles")
public class Role {
	@Id
	private int roleid;
	@Column
	private String rolename;
	@Column
	private Date createtime;
	@Many(target=RoleRight.class,field="roleid")
	private List<RoleRight> rolerights;
	private String rights;
	public int getRoleid() {
		return roleid;
	}
	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}
	public String getRolename() {
		return rolename;
	}
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public List<RoleRight> getRolerights() {
		return rolerights;
	}
	public void setRolerights(List<RoleRight> rolerights) {
		this.rolerights = rolerights;
	}
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	
}
