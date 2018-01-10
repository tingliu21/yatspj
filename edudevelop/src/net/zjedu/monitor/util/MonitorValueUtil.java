package net.zjedu.monitor.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import net.zjedu.monitor.bean.MonitorIndex;
import net.zjedu.monitor.bean.MonitorValue;
import net.zjedu.monitor.bean.Xzqh;
import net.zjedu.monitor.bean.graph.MonitorStat;
import net.zjedu.monitor.bean.graph.MonitorSumValue;

public class MonitorValueUtil {
	protected Dao dao;
	public MonitorValueUtil(Dao dao) {
		super();
		this.dao = dao;
	}
	
	//获取xzq的所有103个详细监测点的目标值，xzqhdm为省或地市级行政区划
			public List<MonitorIndex> getWeight(int year, String xzqhdm){
				List<MonitorIndex> monitorIndexList = null;
				if (xzqhdm.trim() != "" && year != 0) {
						String strSql ="SELECT  mindex_cd,weight FROM monitor_index order by mindex_cd";
						Sql sql = Sqls.create(strSql);
						sql.setCallback(new SqlCallback() {
							public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
								List<MonitorIndex> monitorIndexList = new ArrayList<MonitorIndex>();
								
								while (resultSet.next()) {
									MonitorIndex monitorIndex = new MonitorIndex();
									monitorIndex.setMindex_cd( resultSet.getString("mindex_cd"));
									monitorIndex.setWeight(resultSet.getDouble("weight"));
									monitorIndexList.add(monitorIndex);
								}					
								return monitorIndexList;
							}
						});
						dao.execute(sql);
						return sql.getList(MonitorIndex.class);
					}
				return monitorIndexList;
			}


	//获取xzq的所有103个监测点值，xzqhdm为县级行政区划
	public List<MonitorValue> getScore(int year, String xzqhdm){
		
		Cnd cnd = Strings.isBlank(xzqhdm) ? null : Cnd.where("xzqhdm", "=", xzqhdm.trim());
		if (year != 0) {
			if (cnd == null)
				cnd = Cnd.where("m_year", "=", year);
			else
				cnd = cnd.and("m_year", "=", year);
		}
		// 获得某年某行政区的所有监测指标值及得分
		return dao.query(MonitorValue.class, cnd, null);
	}
	//获取xzq的所有103个详细监测点的平均值，xzqhdm为省或地市级行政区划
	public List<MonitorValue> getAvgScore(int year, String xzqhdm){
		List<MonitorValue> monitorValueList = null;
		if (xzqhdm.trim() != "" && year != 0) {
			//去掉末尾的00,县级行政区划没法算平均
			if(xzqhdm.endsWith("00")){
				String statXZQ = xzqhdm.substring(0, xzqhdm.lastIndexOf("00"));	
				if(statXZQ.endsWith("00")){
					statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));	
				}
				String strSql ="SELECT  mpoint_cd, avg(m_score) as ap FROM monitor_value   where m_year="+
						+year+" and xzqhdm like '" + statXZQ+"%' group by mpoint_cd  order by mpoint_cd";
				Sql sql = Sqls.create(strSql);
				sql.setCallback(new SqlCallback() {
					public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
						List<MonitorValue> monitorValueList = new ArrayList<MonitorValue>();
						
						while (resultSet.next()) {
							MonitorValue monitorValue = new MonitorValue();
							monitorValue.setMpoint_cd( resultSet.getString("mpoint_cd"));
							monitorValue.setScore(resultSet.getDouble("ap"));
							monitorValueList.add(monitorValue);
						}					
						return monitorValueList;
					}
				});
				dao.execute(sql);
				return sql.getList(MonitorValue.class);
			}
		}
		return monitorValueList;
	}
	//获取xzqh的总分，若为省级或地市级行政区划，则获取其所辖的县行政区划的总分
	public List<MonitorSumValue> getTotalScore(int year,Xzqh xzqh){ 
		List<MonitorSumValue> monitorSumList = new ArrayList<MonitorSumValue>();
		String xzqhdm = xzqh.getXzqhdm();
		//去掉末尾的00
		if(xzqhdm.endsWith("00")){
			String statXZQ = xzqhdm.substring(0, xzqhdm.lastIndexOf("00"));	
			if(statXZQ.endsWith("00")){
				statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));	
			}
			List<Xzqh> xzqhList = dao.query(Xzqh.class, Cnd.where("xzqhdm", "like",statXZQ+"%").and("upload", "is", true).asc("xzqhdm"),null);
			for(Xzqh xzq: xzqhList){
				
				MonitorSumValue monitorSumValue = getTotalScoreByXZQH(year,xzq);
				
				monitorSumList.add(monitorSumValue);
			}
		}else{
			MonitorSumValue monitorSumValue = getTotalScoreByXZQH(year,xzqh);
			monitorSumList.add(monitorSumValue);
		}
		return monitorSumList;
	}
	//获取xzqh的总分，该xzqh为县级行政区划
	public MonitorSumValue getTotalScoreByXZQH(int year, Xzqh xzqh){
		MonitorSumValue sumValue =null;
		if (xzqh!=null && year != 0) {
			
			String xzqhdm = xzqh.getXzqhdm();
			Sql sql = Sqls
					.create("select xzqhdm, sum(m_score) as sum_value from monitor_value "
							+ " where m_year= " + year + " and xzqhdm = '" + xzqhdm + "' "
							+ " order by sum_value");
			sql.setCallback(new SqlCallback() {
				public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
					
					if (resultSet.next()) {
						MonitorSumValue monitorValue = new MonitorSumValue();
						
						monitorValue.setCode(resultSet.getString("xzqhdm"));
						//保留2位小数
						BigDecimal bg = new BigDecimal(resultSet.getDouble("sum_value"));						
						
						monitorValue.setValue(bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						return monitorValue;
					}else return null;
					
				}
			});

			dao.execute(sql);
			sumValue = sql.getObject(MonitorSumValue.class);
			if(sumValue!=null) sumValue.setName(xzqh.getXzqhmc());
		}
		return sumValue;
	}
	
	//根据行政区统计1级指标的平均分，xzqh一般为省级或地市级行政区划代码
	public MonitorStat getAvgScore1ByXZQH(int year, String xzqhdm){
		MonitorStat monitorStat=null;
		if (xzqhdm.trim() != "" && year != 0) {
			//去掉末尾的00,县级行政区划没法算平均
			if(xzqhdm.endsWith("00")){
				String statXZQ = xzqhdm.substring(0, xzqhdm.lastIndexOf("00"));	
				if(statXZQ.endsWith("00")){
					statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));	
				}
			
				String strSql = "SELECT xzqh.xzqhdm,xzqh.xzqhmc,avg(index_yx1) as index1, avg(index_yr2) as index2, avg(index_cj3) as index3, avg(index_jy4) as index4, avg(index_sh5) as index5  "
						+ " FROM v_xzqh_monitor1,xzqh where v_xzqh_monitor1.xzqhdm like '" + statXZQ
						+ "%' and xzqh.xzqhdm= '" + xzqhdm + "' Group by xzqh.xzqhdm,xzqh.xzqhmc";
				Sql sql = Sqls.create(strSql);
				sql.setCallback(new SqlCallback() {
					public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
	
						MonitorStat monitorStat = new MonitorStat();
						while (resultSet.next()) {
							monitorStat.setName(resultSet.getString("xzqhmc"));
							double[] value = new double[5];
							//保留2位小数
							BigDecimal bg = new BigDecimal(resultSet.getDouble("index1"));
							value[0] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							bg = new BigDecimal(resultSet.getDouble("index2"));
							value[1] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							bg = new BigDecimal(resultSet.getDouble("index3"));
							value[2] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							bg = new BigDecimal(resultSet.getDouble("index4"));
							value[3] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							bg = new BigDecimal(resultSet.getDouble("index5"));
							value[4] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							monitorStat.setValue(value);
						}
						return monitorStat;
					}
				});
				dao.execute(sql);
				monitorStat = sql.getObject(MonitorStat.class);									
			}
		}
		return monitorStat;
	}
	
}
