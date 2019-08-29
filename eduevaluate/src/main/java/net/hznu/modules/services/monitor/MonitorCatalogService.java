package net.hznu.modules.services.monitor;

import net.hznu.common.base.Service;
import net.hznu.common.chart.MonitorStat;
import net.hznu.modules.models.monitor.Monitor_catalog;
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

import java.util.List;

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
    public void save(Monitor_catalog catalog, String pid) {
        String catacode = "";
        if (!Strings.isEmpty(pid)) {
            Monitor_catalog pp = this.fetch(pid);
            catacode = pp.getCatacode();
        } else pid = "";
        catacode = getSubCode("monitor_catalog",catalog.getYear(), "catacode", catacode);
        catalog.setCatacode(catacode);
        //获取第几级指标

        int level = catacode.length()/2;
        catalog.setLevel(level);

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
        dao().execute(Sqls.create("delete from monitor_catalog where catacode like @catacode").setParam("catacode", catalog.getCatacode() + "%"));
        //清空该指标目录下的所有观测点（三级指标）
        dao().execute(Sqls.create("delete from monitor_index where catalogId=@id or catalogId in(SELECT id FROM monitor_catalog WHERE catacode like @catacode)").setParam("id", catalog.getId()).setParam("catacode", catalog.getCatacode() + "%"));
        //判断是否要把该指标目录的父指标目录的haschildren设为false
        if (!Strings.isEmpty(catalog.getParentId())) {
            int count = count(Cnd.where("parentId", "=", catalog.getParentId()));
            if (count < 1) {
                dao().execute(Sqls.create("update monitor_catalog set hasChildren=false where id=@pid").setParam("pid", catalog.getParentId()));
            }
        }
    }
    public MonitorStat getScoreByLevel(int year,int level){
        MonitorStat monitorStat = new MonitorStat();
        //获取相应等级的指标体系
        List<Monitor_catalog> catalogs = query(Cnd.where("level","=",level).and("year","=",year).and("isshow","=",true).asc("catacode"));

        monitorStat.setName("目标值");
        double[] value = new double[catalogs.size()];
        for (int i = 0; i < catalogs.size(); i++) {
            Monitor_catalog index1 = catalogs.get(i);
            value[i] = index1.getWeights();
        }
        monitorStat.setValue(value);

        return monitorStat;

    }
}

