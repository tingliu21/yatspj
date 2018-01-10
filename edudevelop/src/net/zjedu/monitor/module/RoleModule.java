package net.zjedu.monitor.module;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;

import net.zjedu.monitor.bean.Right;
import net.zjedu.monitor.bean.Role;
import net.zjedu.monitor.bean.RoleRight;
import net.zjedu.monitor.bean.Role;

@IocBean // 还记得@IocBy吗? 这个跟@IocBy有很大的关系哦
@At("/role")
@Ok("json")
@Fail("http:500")
//@Filters(@By(type=CheckSession.class, args={"me", "/"}))
public class RoleModule {

    @Inject
    protected Dao dao; // 就这么注入了,有@IocBean它才会生效
   
    @At
    public Object add(@Param("..")Role role,@Param("rightids")String rightids ) {
        NutMap re = new NutMap();
        String msg = checkRole(role, rightids, true);
        if (msg != null){
            return re.setv("ok", false).setv("msg", msg);
        }        
        role = dao.insert(role);
        //插入r_user_right表
    	int roleid = role.getRoleid();
    	String[] ids = rightids.split(",");
    	for(int i =0;i<ids.length;i++){
    		int rightid = Integer.parseInt(ids[i]);
    		RoleRight roleright = new RoleRight(roleid,rightid);
			dao.insert(roleright);
    	}
        return re.setv("ok", true).setv("data", role);
    }
    @At
    public Object update(@Param("..")Role role,@Param("rightids")String rightids) {
        NutMap re = new NutMap();
        String msg = checkRole(role, rightids, false);
        if (msg != null){
        	
            return re.setv("ok", false).setv("msg", msg);
        }
        
        dao.updateIgnoreNull(role);// 
        //插入r_user_right表
    	int roleid = role.getRoleid();
    	String[] ids = rightids.split(",");
    	for(int i =0;i<ids.length;i++){
    		int rightid = Integer.parseInt(ids[i]);
    		RoleRight roleright = dao.fetchx(RoleRight.class,roleid,rightid);
    		if(roleright == null) {
    			roleright = new RoleRight(roleid,rightid);
    			dao.insert(roleright);
    		}
    	}
    	role = dao.fetch(Role.class,roleid);
        return re.setv("ok", true).setv("data", role);
    }
    @At
    public Object delete(@Param("id")int id) {
        
        dao.delete(Role.class, id); // 再严谨一些的话,需要判断是否为>0
        return new NutMap().setv("ok", true);
    }
    @At
    public Object query(@Param("name")String name, @Param("rows") int rows,@Param("page")int page) {       
        Map<String, Object> result = new HashMap<String, Object>();  
        Cnd cnd = Strings.isBlank(name)? null : Cnd.where("rolename", "like", "%"+name+"%");
        Pager pager = new Pager();  
        pager.setPageNumber(page);  
        pager.setPageSize(rows); 
        List<Role> list = dao.query(Role.class, cnd,pager);  
        for(Role model : list){  
        	dao.fetchLinks(model, "rolerights"); 
        	List<RoleRight> rolerights = model.getRolerights();
        	String rightnames="";
            for (RoleRight right : rolerights){
            	rightnames+=right.getRightname()+";";
            }
            model.setRights(rightnames);
            
        }
        result.put("total", dao.query(Role.class, null).size());  
        result.put("rows", list);  
        return result;  
    }
    @At
    public Object list() {             
        List<Role> list = dao.query(Role.class, null,null);  
          
        
        return list;  
    }
    protected String checkRole(Role role, String rightids, boolean create) {
        if (role == null) {
            return "空对象";
        }
        if (create) {
            if (Strings.isBlank(role.getRolename()))
                return "角色名不能为空";
        } 
        
        if (create) {
            int count = dao.count(Role.class, Cnd.where("rolename", "=", role.getRolename()));
            if (count != 0) {
                return "角色名已经存在";
            }
        } 
        if (create) {
        	if (Strings.isBlank(role.getRolename()))
        	{
                return "角色没有分配权限";
            }
        } 
        if (role.getRolename() != null)
            role.setRolename(role.getRolename().trim());
        return null;
    }

}