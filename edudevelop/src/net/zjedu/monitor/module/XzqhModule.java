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

import net.zjedu.monitor.bean.Xzqh;
import net.zjedu.monitor.bean.Xzqh;

@IocBean // 还记得@IocBy吗? 这个跟@IocBy有很大的关系哦
@At("/xzqh")
@Ok("json")
@Fail("http:500")
//@Filters(@By(type=CheckSession.class, args={"me", "/"}))
public class XzqhModule {

    @Inject
    protected Dao dao; // 就这么注入了,有@IocBean它才会生效
   
    @At
    public Object add(@Param("..")Xzqh xzqh) {
        NutMap re = new NutMap();
        String msg = checkXZQH(xzqh, true);
        if (msg != null){
            return re.setv("ok", false).setv("msg", msg);
        }
        
        xzqh = dao.insert(xzqh);
        return re.setv("ok", true).setv("data", xzqh);
    }
    @At
    public Object update(@Param("..")Xzqh xzqh) {
        NutMap re = new NutMap();
        String msg = checkXZQH(xzqh, false);
        if (msg != null){
            return re.setv("ok", false).setv("msg", msg);
        }
        
        dao.updateIgnoreNull(xzqh);// 
        return re.setv("ok", true);
    }
    @At
    public Object delete(@Param("id")int id) {
        
        dao.delete(Xzqh.class, id); // 再严谨一些的话,需要判断是否为>0
        return new NutMap().setv("ok", true);
    }
    @At
    public Object query(@Param("code")String code,@Param("level")int level, @Param("rows") int rows,@Param("page")int page) {       
        Map<String, Object> result = new HashMap<String, Object>();  
        Cnd cnd = Strings.isBlank(code)? null : Cnd.where("xzqhdm", "like",code+"%");
        if(level!=0){
        	if(cnd == null) cnd = Cnd.where("level","=", level);
        	else cnd = cnd.and("level","=",level);
        }
        Pager pager = new Pager();  
        pager.setPageNumber(page);  
        pager.setPageSize(rows); 
        List<Xzqh> list = dao.query(Xzqh.class, cnd,pager);  
        
        result.put("total", dao.query(Xzqh.class, null).size());  
        result.put("rows", list);  
        return result;  
    }
    @At
    public Object list() {       

        List<Xzqh> list = dao.query(Xzqh.class, Cnd.orderBy().asc("xzqhdm"),null);  
        
        
        return list;  
    }
    @At
    public Object getXZQH(@Param("code")String code,@Param("level")int level){
    	List<Xzqh> list = dao.query(Xzqh.class, Cnd.where("level","=",level).and("xzqhdm", "like",code+"%").asc("xzqhdm"),null);  
        
        
        return list;
    }
    protected String checkXZQH(Xzqh xzqh, boolean create) {
        if (xzqh == null) {
            return "空对象";
        }
        if (create) {
            if (Strings.isBlank(xzqh.getXzqhdm()))
                return "行政区划代码不能为空";
        } 
        
        if (create) {
            int count = dao.count(Xzqh.class, Cnd.where("xzqhdm", "=", xzqh.getXzqhdm()));
            if (count != 0) {
                return "行政区划代码已经存在";
            }
        } 
        if (xzqh.getXzqhdm() != null)
            xzqh.setXzqhdm(xzqh.getXzqhdm().trim());
        return null;
    }

}