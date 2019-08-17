package net.hznu.modules.services.evaluate;

import io.jsonwebtoken.lang.Strings;
import net.hznu.common.base.Service;
import net.hznu.common.chart.MonitorStat;
import net.hznu.modules.models.evaluate.Evaluate_index;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@IocBean(args = {"refer:dao"})
public class EvaluateIndexService extends Service<Evaluate_index> {
	private static final Log log = Logs.get();

    public EvaluateIndexService(Dao dao) {
    	super(dao);
    }

    /*
    * 功能：根据行政区划，获得指标得分
    * viewname:视图名称
    * xzqh：为行政区划代码，如果为市级代码，则获取该市所有县级的得分
    * fieldnames：字段名称，如果为一级指标得分，则为index_01~index_05;
    *                     如果为二级指标得分，则为index_0101~index_0501;
    *                     如果为监测点得分，则为m1～m46
    * */
    public List<MonitorStat> getScoreByXZQH(String viewname, List<String> fieldnames, String xzqhdm) {
        String statXZQ =xzqhdm;

        if(xzqhdm.endsWith("00")) {
            statXZQ= xzqhdm.substring(0,xzqhdm.length()-2);
            if(statXZQ.endsWith("00")){
                statXZQ= statXZQ.substring(0,statXZQ.length()-2);
            }
        }
        Sql sql = Sqls.create("SELECT * FROM  "+viewname+ " where xzqhdm like '" + statXZQ + "%'");
        sql.setCallback(new SqlCallback() {
            public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
                List<MonitorStat> monitorList = new ArrayList<MonitorStat>();
                while (resultSet.next()) {
                    MonitorStat monitorValue = new MonitorStat();
                    monitorValue.setName(resultSet.getString("xzqhmc"));
                    double[] value = new double[fieldnames.size()];
                    for (int i = 0; i < fieldnames.size(); i++) {

                        String fieldname = fieldnames.get(i);
                        //保留2位小数
                        BigDecimal bg = new BigDecimal(resultSet.getDouble(fieldname));
                        value[i] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                    }

                    monitorValue.setValue(value);
                    monitorList.add(monitorValue);
                }
                return monitorList;
            }
        });

        dao().execute(sql);
        return sql.getList(MonitorStat.class);
    }
    /*
    * 功能：根据行政区划，获得指标平均得分
    * viewname:视图名称
    * xzqh：为行政区划代码，一般为省级或地市级行政区划代码
    * fieldnames：字段名称，如果为一级指标得分，则为index_01~index_05;
    *                     如果为二级指标得分，则为index_0101~index_0501;
    *                     如果为监测点得分，则为m1～m46
    * */
    public MonitorStat getAvgScoreByXZQH(String viewname, List<String> fieldnames, String xzqhdm){
        MonitorStat monitorStat=null;
        //去掉末尾的00,县级行政区划没法算平均
        if(xzqhdm.endsWith("00")){
            String statXZQ = xzqhdm.substring(0, xzqhdm.lastIndexOf("00"));
            if(statXZQ.endsWith("00")){
                statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));
            }

            String strSql = "SELECT sys_unit.unitcode,sys_unit.xzqhmc" ;
            for(int i=0;i<fieldnames.size();i++){
                String statFieldname = ", avg("+fieldnames.get(i)+") as "+fieldnames.get(i);
                strSql+=statFieldname;
            }
            strSql+=" FROM "+viewname +",sys_unit where sys_unit.unitcode='"+xzqhdm+"' and xzqhdm like '"+statXZQ+ "%' Group by unitcode,sys_unit.xzqhmc";

            Sql sql = Sqls.create(strSql);
            sql.setCallback(new SqlCallback() {
                public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {

                    MonitorStat monitorStat = new MonitorStat();
                    if (resultSet.next()) {
                        monitorStat.setName(resultSet.getString("xzqhmc"));
                        double[] value = new double[5];
                        for (int i = 0; i < fieldnames.size(); i++) {

                            String fieldname = fieldnames.get(i);
                            //保留2位小数
                            BigDecimal bg = new BigDecimal(resultSet.getDouble(fieldname));
                            value[i] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                        }
                        monitorStat.setValue(value);
                    }
                    return monitorStat;
                }
            });
            dao().execute(sql);
            monitorStat = sql.getObject(MonitorStat.class);
        }

        return monitorStat;
    }
}

