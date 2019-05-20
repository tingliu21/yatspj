package cn.wizzer.modules.services.monitor;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.monitor.Monitor_catalog;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(args = {"refer:dao"})
public class MonitorCatalogService extends Service<Monitor_catalog> {
	private static final Log log = Logs.get();

    public MonitorCatalogService(Dao dao) {
    	super(dao);
    }

    /**
     * 新增菜单
     *
     * @param catalog
     * @param pid
     */
    @Aop(TransAop.READ_COMMITTED)
    public void save(Monitor_catalog catalog, String pid, String unittype) {
        String path = "";
        if (!Strings.isEmpty(pid)) {
            Monitor_catalog pp = this.fetch(pid);
            path = pp.getPath();
        } else pid = "";
        catalog.setPath(getSubPath("monitor_catalog", "path", path));
        catalog.setUnitType(unittype);
        catalog.setParentId(pid);
        dao().insert(catalog);
        if (!Strings.isEmpty(pid)) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", pid));
        }
    }

    /**
     * 级联删除菜单
     *
     * @param catalog
     */
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(Monitor_catalog catalog) {
        //清空该指标目录下的所有子指标目录
        dao().execute(Sqls.create("delete from monitor_catalog where path like @path").setParam("path", catalog.getPath() + "%"));
        //清空该指标目录下的所有观测点（三级指标）
        dao().execute(Sqls.create("delete from monitor_index where catalogId=@id or catalogId in(SELECT id FROM monitor_catalog WHERE path like @path)").setParam("id", catalog.getId()).setParam("path", catalog.getPath() + "%"));
        //判断是否要把该指标目录的父指标目录的haschildren设为false
        if (!Strings.isEmpty(catalog.getParentId())) {
            int count = count(Cnd.where("parentId", "=", catalog.getParentId()));
            if (count < 1) {
                dao().execute(Sqls.create("update monitor_catalog set hasChildren=false where id=@pid").setParam("pid", catalog.getParentId()));
            }
        }
    }
}

