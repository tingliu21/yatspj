package net.hznu.modules.services.evaluate;

import net.hznu.common.base.Service;
import net.hznu.common.chart.MonitorSumValue;
import net.hznu.modules.models.evaluate.Evaluate_records;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.ArrayList;
import java.util.List;

@IocBean(args = {"refer:dao"})
public class EvaluateRecordsService extends Service<Evaluate_records> {
	private static final Log log = Logs.get();

    public EvaluateRecordsService(Dao dao) {
    	super(dao);
    }
    /**
     * 功能：根据行政区划，获得下辖所有县级行政区划的评估总分
     * xzqh：为行政区划代码，一般为省级或地市级行政区划代码
     * */
    public List<MonitorSumValue> getTotalScore(int year, String xzqhdm){
        String statXZQ = xzqhdm;
        if(xzqhdm.endsWith("00")) {
            statXZQ = xzqhdm.substring(0, xzqhdm.lastIndexOf("00"));
            if (statXZQ.endsWith("00")) {
                statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));
            }
        }
        Sql sql = Sqls.create("SELECT  unitcode as code, xzqhmc as name,score as value FROM evaluate_record_view where year =@year and unitcode like '"+statXZQ+"%' order by value");
        sql.params().set("year", year);
//        sql.params().set("xzqh", statXZQ);

        Entity<MonitorSumValue> entity = dao().getEntity(MonitorSumValue.class);
        sql.setEntity(entity);
        sql.setCallback(Sqls.callback.entities());
        dao().execute(sql);

        return sql.getList(MonitorSumValue.class);
    }
    /**
     * 功能：根据行政区划，获得评估总分的平均值
     * xzqh：为行政区划代码，一般为省级或地市级行政区划代码
     * */
    public List<MonitorSumValue> getCityAvgTotalScore(int year, String xzqhdm){
        String statXZQ = xzqhdm;
        if(xzqhdm.endsWith("00")) {
            statXZQ = xzqhdm.substring(0, xzqhdm.lastIndexOf("00"));
            if (statXZQ.endsWith("00")) {
                statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));
            }
        }
        Sql sql = Sqls.create("SELECT  sys_unit.unitcode as code, sys_unit.xzqhmc as name,cast(avg(score) as numeric(4,2)) as value FROM evaluate_record_view,sys_unit " +
                " where year =@year and evaluate_record_view.unitcode like '"+statXZQ+"%' and sys_unit.unitcode=substring(evaluate_record_view.unitcode,0,5) || '00' group by sys_unit.unitcode,sys_unit.xzqhmc  order by value");
        sql.params().set("year", year);
        sql.params().set("xzqh", xzqhdm);

        Entity<MonitorSumValue> entity = dao().getEntity(MonitorSumValue.class);
        sql.setEntity(entity);
        sql.setCallback(Sqls.callback.entities());
        dao().execute(sql);

        return sql.getList(MonitorSumValue.class);

    }

}

