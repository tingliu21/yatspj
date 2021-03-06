package net.hznu.modules.services.monitor;

import net.hznu.common.base.Service;
import net.hznu.common.chart.MonitorStat;
import net.hznu.modules.models.monitor.Monitor_index;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.aop.Aop;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.List;

@IocBean(args = {"refer:dao"})
public class MonitorIndexService extends Service<Monitor_index> {
	private static final Log log = Logs.get();

    public MonitorIndexService(Dao dao) {
    	super(dao);
    }
    /**
     * 新增监测点
     *
     * @param index
     * @param pid
     */
    @Aop(TransAop.READ_COMMITTED)
    public void save(Monitor_index index, String pid) {
        String path = "";
        if (!Strings.isEmpty(pid)) {
            Monitor_index pp = this.fetch(pid);
            path = pp.getCode();
        } else pid = "";
        path = getSubCode("monitor_index",index.getYear(), "code", path);
        index.setCode(path);
        //获取第几级指标

        int level = path.length()/2;
        index.setLevel(level);
        index.setParentId(pid);
        dao().insert(index);
        if (!Strings.isEmpty(pid)) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", pid));
        }
    }
    public int getTotalWeights(int year){
        Sql sql = Sqls.create("select sum(weights) from monitor_index where year = @year").setParam("year", year);
        return count(sql);
    }

    public MonitorStat getScoreByLevel(int year,int level){
        MonitorStat monitorStat = new MonitorStat();
        //获取相应等级的指标体系
        List<Monitor_index> indexList = query(Cnd.where("level","=",level).and("year","=",year).and("isshow","=",true).asc("location"));

        monitorStat.setName("目标值");
        double[] value = new double[indexList.size()];
        for (int i = 0; i < indexList.size(); i++) {
            Monitor_index index1 = indexList.get(i);
            value[i] = index1.getWeights();
        }
        monitorStat.setValue(value);

        return monitorStat;

    }

}

