package cn.wizzer.modules.services.monitor;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.monitor.Monitor_index;
import org.nutz.dao.entity.Record;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.List;

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
    //获取专家审核指标
    public List<Record> getSpecialIndex(String unittype){
        Sql sql = Sqls.create("select index.id, sys_role.code as rolecode from monitor_index as index inner join sys_role on sys_role.id = index.masterrole inner join sys_unit on sys_unit.id = index.department where sys_unit.aliasname='Special' and index.unittype=@unittype").setParam("unittype", unittype);
        List<Record> indexlist = list(sql);
        return  indexlist;

    }
}

