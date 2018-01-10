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
import net.zjedu.monitor.bean.Role;
import net.zjedu.monitor.bean.User;
import net.zjedu.monitor.bean.Xzqh;
import net.zjedu.monitor.bean.Right;

@IocBean // 还记得@IocBy吗? 这个跟@IocBy有很大的关系哦
@At("/right")
@Ok("json")
@Fail("http:500")
//@Filters(@By(type=CheckSession.class, args={"me", "/"}))
public class RightModule {

    @Inject
    protected Dao dao; // 就这么注入了,有@IocBean它才会生效
   

    @At
    public Object list() { 
    	Map<String, Object> result = new HashMap<String, Object>();
    	Cnd cnd = (Cnd) Cnd.where("parentid","is",null).asc("rightid");
        List<Right> list = dao.query(Right.class, cnd,null);  
        for(Right model : list){  
            List<Right> childRights = dao.query(Right.class, Cnd.where("parentid","=",model.getRightid()));
            model.setChildRights(childRights);
            
        }  
        
        return list;  
    }
    

}