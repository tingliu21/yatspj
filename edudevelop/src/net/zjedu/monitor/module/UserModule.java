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

import net.zjedu.monitor.bean.Role;
import net.zjedu.monitor.bean.User;
import net.zjedu.monitor.bean.Xzqh;

@IocBean // 还记得@IocBy吗? 这个跟@IocBy有很大的关系哦
@At("/user")
@Ok("json:{locked:'password',ignoreNull:true}")
@Fail("http:500")
//@Filters(@By(type=CheckSession.class, args={"me", "/"}))
public class UserModule {

    @Inject
    protected Dao dao; // 就这么注入了,有@IocBean它才会生效
    @At
    @Filters // 覆盖UserModule类的@Filter设置,因为登陆可不能要求是个已经登陆的Session
    public Object login(@Param("username")String name, @Param("password")String password, HttpSession session) {
        User user = dao.fetch(User.class, Cnd.where("username", "=", name).and("password", "=", password));
        if (user == null) {
            return false;
        } else {
            session.setAttribute("me", user.getUserid());
            return true;
        }
    }
    @At
    @Ok(">>:/")
    //必须使用>>:XXX 即302重定向,不要使用->:XXX内部重定向, 因为后者在shiro环境下会报错.
    //>> 和 -> 分别是 redirect 和 forward的缩写.
    public void logout(HttpSession session) {
        session.invalidate();
    }
    @At
    public Object add(@Param("..")User user) {
        NutMap re = new NutMap();
        String msg = checkUser(user, true);
        if (msg != null){
            return re.setv("ok", false).setv("msg", msg);
        }
        
        user = dao.insert(user);
        return re.setv("ok", true).setv("data", user);
    }
    @At
    public Object update(@Param("..")User user) {
        NutMap re = new NutMap();
        String msg = checkUser(user, false);
        if (msg != null){
            return re.setv("ok", false).setv("msg", msg);
        }
        user.setUsername(null);// 不允许更新用户名
        
        dao.updateIgnoreNull(user);// 真正更新的其实只有password和salt
        return re.setv("ok", true);
    }
    @At
    public Object delete(@Param("id")int id, @Attr("me")int me) {
        if (me == id) {
            return new NutMap().setv("ok", false).setv("msg", "不能删除当前用户!!");
        }
        dao.delete(User.class, id); // 再严谨一些的话,需要判断是否为>0
        return new NutMap().setv("ok", true);
    }
    @At
    public Object query(@Param("username")String name, @Param("roleid") int roleid, @Param("rows") int rows,@Param("page")int page) {
//        Cnd cnd = Strings.isBlank(name)? null : Cnd.where("username", "like", "%"+name+"%");
//        QueryResult qr = new QueryResult();
//        qr.setList(dao.query(User.class, cnd, pager));
//        pager.setRecordCount(dao.count(User.class, cnd));
//        qr.setPager(pager);
//        return qr; //默认分页是第1页,每页20条
        
        Map<String, Object> result = new HashMap<String, Object>();  
        Cnd cnd = Strings.isBlank(name)? null : Cnd.where("username", "like", "%"+name+"%");
        if(roleid!=0){
        	if(cnd == null) cnd = Cnd.where("roleid","=", roleid);
        	else cnd = cnd.and("roleid","=",roleid);
        }
        Pager pager = new Pager();  
        pager.setPageNumber(page);  
        pager.setPageSize(rows); 
        List<User> list = dao.query(User.class, cnd,pager);  
        for(User model : list){  
            dao.fetchLinks(model, "role");  
            Role role = model.getRole();  
            if(null!=role){  
                model.setUserrole(role.getRolename());  
            }  
            dao.fetchLinks(model, "xzqh");
            Xzqh xzqh= model.getXzqh();
            if(null!=xzqh){  
                model.setZqname(xzqh.getName()); 
            } 
        }  
        result.put("total", dao.query(User.class, null).size());  
        result.put("rows", list);  
        return result;  
    }
    protected String checkUser(User user, boolean create) {
        if (user == null) {
            return "空对象";
        }
        if (create) {
            if (Strings.isBlank(user.getUsername()) || Strings.isBlank(user.getPassword()))
                return "用户名/密码不能为空";
        } else {
            if (Strings.isBlank(user.getPassword()))
                return "密码不能为空";
        }
        String passwd = user.getPassword().trim();
        if (6 > passwd.length() || passwd.length() > 12) {
            return "密码长度错误";
        }
        user.setPassword(passwd);
        if (create) {
            int count = dao.count(User.class, Cnd.where("username", "=", user.getUsername()));
            if (count != 0) {
                return "用户名已经存在";
            }
        } 
        if (user.getUsername() != null)
            user.setUsername(user.getUsername().trim());
        return null;
    }

}