package net.zjedu.monitor.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

@Table ("users")
public class User {
	@Id
	private int userid;
	@Name
	@Column
	private String username;
	@Column
	private String password;
	@Column
	private String realname;
	
	@Column("roleid")
	private int roleid;	
	@One(target=Role.class,field="roleid") 
	private Role role;
	private String userrole;
	
	@Column
	private String tel;
	
	@Column("xzqhdm")
	private String xzqhdm;	
	@One(target=Xzqh.class,field="xzqhdm") 
	private Xzqh xzqh;
	private String zqname;
	
	@Column("enabled")
	private boolean status;
	
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	
	public int getRoleid() {
		return roleid;
	}
	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getUserrole() {
		return userrole;
	}
	public void setUserrole(String viewrole) {
		this.userrole = viewrole;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public String getXzqhdm() {
		return xzqhdm;
	}
	public void setXzqhdm(String xzqhdm) {
		this.xzqhdm = xzqhdm;
	}
	public Xzqh getXzqh() {
		return xzqh;
	}
	public void setXzqh(Xzqh xzqh) {
		this.xzqh = xzqh;
	}
	public String getZqname() {
		return zqname;
	}
	public void setZqname(String zqname) {
		this.zqname = zqname;
	}
	public boolean isEnabled() {
		return status;
	}
	public void setEnabled(boolean enabled) {
		this.status = enabled;
	}
	
}
