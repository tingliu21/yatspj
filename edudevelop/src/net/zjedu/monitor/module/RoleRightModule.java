package net.zjedu.monitor.module;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;

import net.zjedu.monitor.bean.Right;
import net.zjedu.monitor.bean.RoleRight;
import net.zjedu.monitor.bean.RoleRight;
import net.zjedu.monitor.bean.RoleRight;

@IocBean // 还记得@IocBy吗? 这个跟@IocBy有很大的关系哦
@At("/roleright")
@Ok("json")
@Fail("http:500")
//@Filters(@By(type=CheckSession.class, args={"me", "/"}))
public class RoleRightModule {

    @Inject
    protected Dao dao; // 就这么注入了,有@IocBean它才会生效
   
    @At
    public Object add(@Param("..")RoleRight roleRight) {
        NutMap re = new NutMap();
        String msg = checkRole(roleRight, true);
        if (msg != null){
            return re.setv("ok", false).setv("msg", msg);
        }
        
        roleRight = dao.insert(roleRight);
        return re.setv("ok", true).setv("data", roleRight);
    }
    @At
    public Object update(@Param("..")RoleRight roleRight) {
        NutMap re = new NutMap();
        String msg = checkRole(roleRight, false);
        if (msg != null){
            return re.setv("ok", false).setv("msg", msg);
        }
        
        dao.updateIgnoreNull(roleRight);// 
        return re.setv("ok", true);
    }
    @At
    public Object delete(@Param("id")int id) {
        
        dao.delete(RoleRight.class, id); // 再严谨一些的话,需要判断是否为>0
        return new NutMap().setv("ok", true);
    }
    @At
    public Object query(@Param("name")String name, @Param("rows") int rows,@Param("page")int page) {       
        Map<String, Object> result = new HashMap<String, Object>();  
        Cnd cnd = Strings.isBlank(name)? null : Cnd.where("rolename", "like", "%"+name+"%");
        Pager pager = new Pager();  
        pager.setPageNumber(page);  
        pager.setPageSize(rows); 
        List<RoleRight> list = dao.query(RoleRight.class, cnd,pager);  
        
        result.put("total", dao.query(RoleRight.class, null).size());  
        result.put("rows", list);  
        return result;  
    }
    @At
    public Object list() {             
        List<RoleRight> list = dao.query(RoleRight.class, null,null);  
        
        
        return list;  
    }
    protected String checkRole(RoleRight roleRight, boolean create) {
        if (roleRight == null) {
            return "空对象";
        }
//        if (create) {
//            if ((roleRight.getRightid()==0))
//                return "角色名不能为空";
//        } 
//        
//        if (create) {
//            int count = dao.count(RoleRight.class, Cnd.where("rolename", "=", roleRight.getRolename()));
//            if (count != 0) {
//                return "角色名已经存在";
//            }
//        } 
//        if (roleRight.getRolename() != null)
//            roleRight.setRolename(roleRight.getRolename().trim());
        return null;
    }

}