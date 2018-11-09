package cn.wizzer.modules.services.monitor;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.monitor.Monitor_index;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(args = {"refer:dao"})
public class MonitorIndexService extends Service<Monitor_index> {
	private static final Log log = Logs.get();

    public MonitorIndexService(Dao dao) {
    	super(dao);
    }

    public int getTotalWeights(String unitType){
        Sql sql = Sqls.create("select sum(weights) from monitor_index where unittype = @type").setParam("type", unitType);
        return count(sql);
    }
}

