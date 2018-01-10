package net.zjedu.monitor.module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.joda.time.DateTime;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.adaptor.VoidAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import net.zjedu.monitor.bean.DDMPointReport;
import net.zjedu.monitor.bean.DDMindex;
import net.zjedu.monitor.bean.MonitorDetail;
import net.zjedu.monitor.bean.MonitorIndex;
import net.zjedu.monitor.bean.MonitorValue;
import net.zjedu.monitor.bean.Result;
import net.zjedu.monitor.bean.User;
import net.zjedu.monitor.bean.Xzqh;
import net.zjedu.monitor.bean.graph.StatChart;
import net.zjedu.monitor.bean.graph.MonitorStat;
import net.zjedu.monitor.bean.graph.MonitorSumValue;
import net.zjedu.monitor.util.ImgBase64Util;
import net.zjedu.monitor.util.MonitorValueUtil;
import net.zjedu.monitor.util.UploadUtils;
import net.zjedu.monitor.util.XwpfUtil;

@IocBean // 还记得@IocBy吗? 这个跟@IocBy有很大的关系哦
@At("/monitor")
@Ok("json")
@Fail("http:500")
public class MonitorValueModule {
	private static final Logger logger = Logger.getLogger(MonitorValueModule.class);
	@Inject
	protected Dao dao; // 就这么注入了,有@IocBean它才会生效
	
	
	//2017-12-14
	@At
	//获得某年某县区的二级指标值、目标值以及省均值
	public Object getCountyScore2(@Param("year") int year, @Param("xzqhdm") String xzqh) {
		if (xzqh.trim() != "") {
			final List<MonitorStat> statResult = new ArrayList<MonitorStat>();
			String statXZQ = xzqh;
			//去掉末尾的0 
			if(xzqh.endsWith("00")){
				statXZQ = xzqh.substring(0, xzqh.lastIndexOf("00"));	
				if(statXZQ.endsWith("00")){
					statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));	
				}
			}

			String strSql = "SELECT xzqhdm,xzqhmc,index_0101, index_0102, index_0103, index_0201, index_0202,"
					+ "index_0203,index_0204,index_0301,index_0302,index_0303,index_0401,index_0402,index_0501 "
					+ " FROM v_xzqh_monitor2 where xzqhdm like '"+statXZQ+"%'";
			Sql sql = Sqls.create(strSql);
			sql.setCallback(new SqlCallback() {
				public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {

					MonitorStat monitorStat = new MonitorStat();
					double[] value = new double[13];
					while (resultSet.next()) {
						monitorStat.setName(resultSet.getString("xzqhmc"));
						
						//保留2位小数
						BigDecimal bg = new BigDecimal(resultSet.getDouble("index_0101"));
						value[0] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_0102"));
						value[1] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_0103"));
						value[2] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_0201"));
						value[3] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_0202"));
						value[4] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_0203"));
						value[5] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_0204"));
						value[6] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_0301"));
						value[7] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_0302"));
						value[8] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_0303"));
						value[9] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_0401"));
						value[10] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_0402"));
						value[11] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_0501"));
						value[12] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						
						monitorStat.setValue(value);
						statResult.add(monitorStat);
					}
					return monitorStat;
				}
			});
			dao.execute(sql);
			@SuppressWarnings("static-access")
			List<DDMindex> index1List = dao.query(DDMindex.class, Cnd.where("ilevel", "=", 2).asc("jczbdm"));
			MonitorStat monitorStat = new MonitorStat();
			monitorStat.setName("目标值");
			double[] value = new double[13];
			// 循环遍历13个二级指标，因为之前的返回结果已经排序过，所以就依次给value数值赋值即可。
			for (int i = 0; i < index1List.size(); i++) {
				DDMindex index1 = index1List.get(i);
				value[i] = index1.getPoint();
			}
			monitorStat.setValue(value);
			// 加入目标值
			statResult.add(monitorStat);
			
			String strSql1 = "SELECT xzqh.xzqhdm,xzqh.xzqhmc,avg(index_0101) as index1, avg(index_0102) as index2, avg(index_0103) as index3, avg(index_0201) as index4, avg(index_0202) as index5,"
					+ "avg(index_0203) as index6,avg(index_0204) as index7 ,avg(index_0301) as index8,avg(index_0302) as index9,avg(index_0303) as index10,avg(index_0401) as index11,avg(index_0402) as index12,avg(index_0501) as index13   "
					+ " FROM v_xzqh_monitor2,xzqh where xzqh.xzqhdm= '330000' Group by xzqh.xzqhdm,xzqh.xzqhmc";
			Sql sql1 = Sqls.create(strSql1);
			sql1.setCallback(new SqlCallback() {
				public Object invoke(Connection connection, ResultSet resultSet, Sql sql1) throws SQLException {

					MonitorStat monitorStat = new MonitorStat();
					double[] value = new double[13];
					while (resultSet.next()) {
						monitorStat.setName(resultSet.getString("xzqhmc"));
						
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
						bg = new BigDecimal(resultSet.getDouble("index6"));
						value[5] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index7"));
						value[6] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index8"));
						value[7] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index9"));
						value[8] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index10"));
						value[9] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index11"));
						value[10] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index12"));
						value[11] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index13"));
						value[12] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						
						monitorStat.setValue(value);
					}
					return monitorStat;
				}
			});
			dao.execute(sql1);
			statResult.add(sql1.getObject(MonitorStat.class));
           //省均值
			return statResult;
		} else {
			return null;
		}
	}
	
	// 2017-12-9 Created By Liut
		@At
		public Object getScore2(@Param("year") int year, @Param("xzqhdm") String xzqh) { 
			if (xzqh.trim() != "" && year != 0) {
				final List<MonitorStat> monitorList = new ArrayList<MonitorStat>();
				String statXZQ = xzqh;
				//去掉末尾的0 
				if(xzqh.endsWith("00")){
					statXZQ = xzqh.substring(0, xzqh.lastIndexOf("00"));	
					if(statXZQ.endsWith("00")){
						statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));	
					}
				}
				
				String strsql="SELECT xzqhdm,xzqhmc,index_yx1, index_yr2, index_cj3, index_jy4, index_sh5 FROM v_xzqh_monitor1 "
				 +" where xzqhdm like '"+statXZQ+"%'";
				Sql sql = Sqls.create(strsql);
				sql.setCallback(new SqlCallback() {
					public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
						//List<MonitorStat> monitorList = new ArrayList<MonitorStat>();
						if (resultSet.next()) {
							MonitorStat monitorValue = new MonitorStat();
							//monitorValue.setName(resultSet.getString("xzqhmc"));
							monitorValue.setName("得分");
							double[] value = new double[5];
							//保留2位小数
							BigDecimal bg = new BigDecimal(resultSet.getDouble("index_yx1"));												
							value[0] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							bg = new BigDecimal(resultSet.getDouble("index_yr2"));												
							value[1] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							bg = new BigDecimal(resultSet.getDouble("index_cj3"));												
							value[2] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							bg = new BigDecimal(resultSet.getDouble("index_jy4"));												
							value[3] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							bg = new BigDecimal(resultSet.getDouble("index_sh5"));												
							value[4] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							
							monitorValue.setValue(value);		
							monitorList.add(monitorValue);
						}
						return monitorList;
					}
				});
				dao.execute(sql);
				
				@SuppressWarnings("static-access")
				List<DDMindex> index1List = dao.query(DDMindex.class, Cnd.where("ilevel", "=", 1).asc("jczbdm"));
				MonitorStat monitorStat = new MonitorStat();
				monitorStat.setName("目标值");
				double[] value1 = new double[5];
				// 循环遍历5个一级指标，因为之前的返回结果已经排序过，所以就依次给value数值赋值即可。
				for (int i = 0; i < index1List.size(); i++) {
					DDMindex index1 = index1List.get(i);
					value1[i] = index1.getPoint();
				}
				monitorStat.setValue(value1);
				monitorList.add(monitorStat);
				
				String strSql = "SELECT xzqh.xzqhdm,xzqh.xzqhmc,avg(index_yx1) as index1, avg(index_yr2) as index2, avg(index_cj3) as index3, avg(index_jy4) as index4, avg(index_sh5) as index5  "
						+ " FROM v_xzqh_monitor1,xzqh where xzqh.xzqhdm= '330000' Group by xzqh.xzqhdm,xzqh.xzqhmc";
				Sql sql1 = Sqls.create(strSql);
				sql1.setCallback(new SqlCallback() {
					public Object invoke(Connection connection, ResultSet resultSet, Sql sql1) throws SQLException {

						MonitorStat monitorStat = new MonitorStat();
						while (resultSet.next()) {
							//monitorStat.setName(resultSet.getString("xzqhmc"));
							monitorStat.setName("省均值");
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
				dao.execute(sql1);
				monitorList.add(sql1.getObject(MonitorStat.class));	

			return monitorList;
			}else return null;
			
		}


		// 2017-12-8 Created By Liut
		@At
		//获得某年某县区的55个监测指标值
		public Object getCountyIndex(@Param("year") int year, @Param("xzqhdm") String xzqh) {
			if (xzqh.trim() != "") {
				final List<MonitorStat>monitorList  = new ArrayList<MonitorStat>();
				String statXZQ = xzqh;
				//去掉末尾的0 
				if(xzqh.endsWith("00")){
					statXZQ = xzqh.substring(0, xzqh.lastIndexOf("00"));	
					if(statXZQ.endsWith("00")){
						statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));	
					}
				}
				
					String strSql = "SELECT xzqhdm,xzqhmc,m1,m2,m3,m4,m5,m6,m7,m8,m9,m10,m11,m12,m13,m14,m15,m16,m17,m18,"
							+ "m19,m20,m21,m22,m23,m24,m25,m26,m27,m28,m29,m30,m31,m32,m33,m34,m35,m36,m37,m38,m39,m40,m41,m42,m43,m44,"
							+ "m45,m46,m47,m48,m49,m50,m51,m52,m53,m54 FROM v_xzqh_monitor_crosstab"
							+ " WHERE  xzqhdm like '"+statXZQ+"%'";
					Sql sql = Sqls.create(strSql);
					sql.setCallback(new SqlCallback() {
						public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
							//List<MonitorStat>monitorList  = new ArrayList<MonitorStat>();
							//MonitorStat monitorValue = new MonitorStat();
							while (resultSet.next()) {
								MonitorStat monitorValue = new MonitorStat();
								monitorValue.setName(resultSet.getString("xzqhmc"));
								double[] value = new double[54];
								for (int i = 0; i < 54; i++) {
									BigDecimal bg = new BigDecimal(resultSet.getDouble("m" + (i + 1)));
									value[i] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
								}
								monitorValue.setValue(value);
								monitorList.add(monitorValue);
							}
							return monitorList;
						}
					});
					dao.execute(sql);

					strSql = "select sum(weight) as score,mindex_id from monitor_index where mindex_id <55 group by mindex_id order by mindex_id";
					sql = Sqls.create(strSql);
					sql.setCallback(new SqlCallback() {
						public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {

							MonitorStat monitorStat = new MonitorStat();
							monitorStat.setName("目标值");
							double[] value = new double[54];

							while (resultSet.next()) {
								int i = resultSet.getInt("mindex_id");
								value[i - 1] = resultSet.getDouble("score");
														}
							monitorStat.setValue(value);

							return monitorStat;
						}
					});
					dao.execute(sql);
					// 加入目标值
					monitorList.add(sql.getObject(MonitorStat.class));
					
					String strSql1 = "SELECT xzqh.xzqhdm,xzqh.xzqhmc, avg(m1) as p1, avg(m2) as p2, avg(m3) as p3, avg(m4) as p4, avg(m5) as p5, avg(m6) as p6, avg(m7) as p7, avg(m8) as p8, "
							+ " avg(m9) as p9, avg(m10) as p10, avg(m11) as p11, avg(m12) as p12, avg(m13) as p13, avg(m14) as p14, avg(m15) as p15, avg(m16) as p16, avg(m17) as p17, avg(m18) as p18, "
							+ " avg(m19) as p19, avg(m20) as p20, avg(m21) as p21, avg(m22) as p22, avg(m23) as p23, avg(m24) as p24, avg(m25) as p25, avg(m26) as p26, avg(m27) as p27, avg(m28) as p28, "
							+ " avg(m29) as p29, avg(m30) as p30, avg(m31) as p31, avg(m32) as p32, avg(m33) as p33, avg(m34) as p34, avg(m35) as p35, avg(m36) as p36, avg(m37) as p37, avg(m38) as p38, "
							+ " avg(m39) as p39, avg(m40) as p40, avg(m41) as p41, avg(m42) as p42, avg(m43) as p43, avg(m44) as p44, avg(m45) as p45, avg(m46) as p46, avg(m47) as p47, avg(m48) as p48, "
							+ " avg(m49) as p49, avg(m50) as p50, avg(m51) as p51, avg(m52) as p52, avg(m53) as p53, avg(m54) as p54, avg(m55) as p55 FROM v_xzqh_monitor_crosstab, xzqh  "
							+ " WHERE  xzqh.xzqhdm= '330000' Group by xzqh.xzqhdm,xzqh.xzqhmc";
					Sql sql1 = Sqls.create(strSql1);
					sql1.setCallback(new SqlCallback() {
						public Object invoke(Connection connection, ResultSet resultSet, Sql sql1) throws SQLException {

							MonitorStat monitorStat = new MonitorStat();
							if (resultSet.next()) {
								monitorStat.setName(resultSet.getString("xzqhmc"));
								double[] value = new double[54];
								for (int i = 0; i < 54; i++) {
									BigDecimal bg = new BigDecimal(resultSet.getDouble("p" + (i + 1)));
									value[i] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
								}

								monitorStat.setValue(value);
							}
							return monitorStat;
						}
					});
					dao.execute(sql1);
					// 加入平均值
					monitorList.add(sql1.getObject(MonitorStat.class));
					return monitorList;
				} else {
					return null;
				}
		}
		
	@At
	public Object get(@Param("xzqhdm") String xzqh, @Param("year") int year) {
		Cnd cnd = Strings.isBlank(xzqh) ? null : Cnd.where("xzqhdm", "=", xzqh.trim());
		if (year != 0) {
			if (cnd == null)
				cnd = Cnd.where("m_year", "=", year);
			else
				cnd = cnd.and("m_year", "=", year);
		}
		MonitorDetail result = new MonitorDetail();
		result.setYear(year);
		result.setXzqhdm(xzqh);

		Class<? extends MonitorDetail> monitor = (Class<? extends MonitorDetail>) result.getClass();
		// 获得某年某行政区的所有监测指标值及得分
		List<MonitorValue> list = dao.query(MonitorValue.class, cnd, null);

		for (MonitorValue model : list) {
			result.setXzqhmc(model.getXzqhmc());
			String code = model.getMpoint_cd();
			String value_cd = "P" + code;
			String grade_cd = "G" + code;
			try {
				// 利用java反射机制得到类的属性
				Field f_value = monitor.getDeclaredField(value_cd);
				Field f_score = monitor.getDeclaredField(grade_cd);
				// 设置类属性的值
				double v = model.getValue();
				if (f_value.getGenericType().toString().equals("boolean")) {
					f_value.set(result, v > 0 ? true : false);
				} else {
					f_value.set(result, v);
				}

				f_score.set(result, model.getScore());

			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return result;
	}
	@At
	public Object getCityTotalScore(@Param("year") int year, @Param("xzqhdm") String xzqh) {
		if (xzqh.trim() != "") {
			String strSql = "SELECT xzqh.xzqhdm,xzqh.xzqhmc,avg(sum_value) as sumvalue "
					+" FROM v_xzqh_monitor0,xzqh where xzqh.xzqhdm = substring(v_xzqh_monitor0.xzqhdm,0,5) || '00'"
					+" group by xzqh.xzqhdm,xzqh.xzqhmc order by sumvalue";
			
			// 返回值数组，有11个对象，11个地市总得分的平均值
			/*List<MonitorSumValue> statResult = new ArrayList<MonitorSumValue>();
			
			List<Xzqh> list = dao.query(Xzqh.class, Cnd.where("level", "=", 2).asc("xzqhdm"),null);  
			for(Xzqh cityxzqh:list){
				String strxzqh = cityxzqh.getXzqhdm();		
				String statXZQ = strxzqh.replaceAll("00", "");
				
				String strSql = "SELECT xzqh.xzqhdm,xzqh.xzqhmc,avg(sum_value) as sumvalue  "
						+ " FROM v_xzqh_monitor0,xzqh where v_xzqh_monitor0.xzqhdm like '" + statXZQ
						+ "%' and xzqh.xzqhdm= '" + strxzqh + "' Group by xzqh.xzqhdm,xzqh.xzqhmc order by sumvalue";*/
				Sql sql = Sqls.create(strSql);
				sql.setCallback(new SqlCallback() {
					public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
						List<MonitorSumValue> monitorList = new ArrayList<MonitorSumValue>();
						
						while (resultSet.next()) {
							MonitorSumValue monitorValue = new MonitorSumValue();
							monitorValue.setName(resultSet.getString("xzqhmc"));
							BigDecimal bg = new BigDecimal(resultSet.getDouble("sumvalue"));
							
							monitorValue.setValue(bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							monitorList.add(monitorValue);
						}
						return monitorList;
					}
				});
				dao.execute(sql);
//				statResult.add(sql.getObject(MonitorSumValue.class));	
//			}
//			return statResult;
			return sql.getList(MonitorSumValue.class);
		} else {
			return null;
		}
	}
	// 2017-11-20 Created By Liut
	// 功能：根据行政区划代码，获取相应的一级指标统计值
	// 参数xzqhdm为要统计的行政区划代码，比如浙江省一级指标统计值，xzqhdm即为330000
	// 参数year为统计的年份数，暂时没用，预留
	@At
	public Object getAvgScore1(@Param("year") int year, @Param("xzqhdm") String xzqh) {
		if (xzqh.trim() != "") {
			// 返回值数组，有2个对象，平均值和目标值
			List<MonitorStat> statResult = new ArrayList<MonitorStat>();
			
			// 加入平均值
			MonitorValueUtil mUtils = new MonitorValueUtil(dao);
			statResult.add(mUtils.getAvgScore1ByXZQH(year,xzqh));

			// 2017-11-21 Added By Liut
			// 获取指标目标值
			@SuppressWarnings("static-access")
			List<DDMindex> index1List = dao.query(DDMindex.class, Cnd.where("ilevel", "=", 1).asc("jczbdm"));
			MonitorStat monitorStat = new MonitorStat();
			monitorStat.setName("目标值");
			double[] value = new double[5];
			// 循环遍历5个一级指标，因为之前的返回结果已经排序过，所以就依次给value数值赋值即可。
			for (int i = 0; i < index1List.size(); i++) {
				DDMindex index1 = index1List.get(i);
				value[i] = index1.getPoint();
			}
			monitorStat.setValue(value);
			// 加入目标值
			statResult.add(monitorStat);
			return statResult;

		} else {
			return null;
		}
	}
	// 2017-11-20 Created By Liut
	// 功能：根据行政区划代码，获取相应的一级指标统计值
	// 参数xzqhdm为要统计的行政区划代码，比如浙江省一级指标统计值，xzqhdm即为330000
	// 参数year为统计的年份数，暂时没用，预留
	@At
	public Object getCityAvgScore1(@Param("year") int year, @Param("xzqhdm") String xzqh) {
		if (xzqh.trim() != "") {
			// 返回值数组，有11个对象，11个地市的平均值
			List<MonitorStat> statResult = new ArrayList<MonitorStat>();
			List<Xzqh> list = dao.query(Xzqh.class, Cnd.where("level", "=", 2).asc("xzqhdm"),null);  
			MonitorValueUtil mUtils = new MonitorValueUtil(dao);
			for(Xzqh cityxzqh:list){
				// 加入每个地市的平均值
				statResult.add(mUtils.getAvgScore1ByXZQH(year,cityxzqh.getXzqhdm()));

			}
			return statResult;

		} else {
			return null;
		}
	}
	@At
	public Object getAvgScore2(@Param("year") int year, @Param("xzqhdm") String xzqh) {
		if (xzqh.trim() != "") {
			List<MonitorStat> statResult = new ArrayList<MonitorStat>();
			String statXZQ = xzqh.replaceAll("00", "");

			String strSql = "SELECT xzqh.xzqhdm,xzqh.xzqhmc,avg(index_0101) as index1, avg(index_0102) as index2, avg(index_0103) as index3, avg(index_0201) as index4, avg(index_0202) as index5,"
					+ "avg(index_0203) as index6,avg(index_0204) as index7 ,avg(index_0301) as index8,avg(index_0302) as index9,avg(index_0303) as index10,avg(index_0401) as index11,avg(index_0402) as index12,avg(index_0501) as index13   "
					+ " FROM v_xzqh_monitor2,xzqh where v_xzqh_monitor2.xzqhdm like '" + statXZQ
					+ "%' and xzqh.xzqhdm= '" + xzqh + "' Group by xzqh.xzqhdm,xzqh.xzqhmc";
			Sql sql = Sqls.create(strSql);
			sql.setCallback(new SqlCallback() {
				public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {

					MonitorStat monitorStat = new MonitorStat();
					double[] value = new double[13];
					while (resultSet.next()) {
						monitorStat.setName(resultSet.getString("xzqhmc"));
						
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
						bg = new BigDecimal(resultSet.getDouble("index6"));
						value[5] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index7"));
						value[6] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index8"));
						value[7] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index9"));
						value[8] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index10"));
						value[9] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index11"));
						value[10] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index12"));
						value[11] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index13"));
						value[12] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						
						monitorStat.setValue(value);
					}
					return monitorStat;
				}
			});
			dao.execute(sql);
			statResult.add(sql.getObject(MonitorStat.class));

			// 2017-11-21 Added By Liut
			// 获取指标目标值
			@SuppressWarnings("static-access")
			List<DDMindex> index1List = dao.query(DDMindex.class, Cnd.where("ilevel", "=", 2).asc("jczbdm"));
			MonitorStat monitorStat = new MonitorStat();
			monitorStat.setName("目标值");
			double[] value = new double[13];
			// 循环遍历5个一级指标，因为之前的返回结果已经排序过，所以就依次给value数值赋值即可。
			for (int i = 0; i < index1List.size(); i++) {
				DDMindex index1 = index1List.get(i);
				value[i] = index1.getPoint();
			}
			monitorStat.setValue(value);
			// 加入目标值
			statResult.add(monitorStat);
			return statResult;
		} else {
			return null;
		}
	}

	// 2017-11-20 Created By Liut
	// 功能：根据行政区划代码，获取相应的一级指标统计值
	// 参数xzqhdm为要统计的行政区划代码，比如浙江省一级指标统计值，xzqhdm即为330000
	// 参数year为统计的年份数，暂时没用，预留
	@At
	public Object getAvgScoreP(@Param("year") int year, @Param("xzqhdm") String xzqh) {
		if (xzqh.trim() != "") {
			// 返回值数组，有2个对象，平均值和目标值
			List<MonitorStat> statResult = new ArrayList<MonitorStat>();
			String statXZQ = xzqh.replaceAll("00", "");

			String strSql = "SELECT xzqh.xzqhdm,xzqh.xzqhmc, avg(m1) as p1, avg(m2) as p2, avg(m3) as p3, avg(m4) as p4, avg(m5) as p5, avg(m6) as p6, avg(m7) as p7, avg(m8) as p8, "
					+ " avg(m9) as p9, avg(m10) as p10, avg(m11) as p11, avg(m12) as p12, avg(m13) as p13, avg(m14) as p14, avg(m15) as p15, avg(m16) as p16, avg(m17) as p17, avg(m18) as p18, "
					+ " avg(m19) as p19, avg(m20) as p20, avg(m21) as p21, avg(m22) as p22, avg(m23) as p23, avg(m24) as p24, avg(m25) as p25, avg(m26) as p26, avg(m27) as p27, avg(m28) as p28, "
					+ " avg(m29) as p29, avg(m30) as p30, avg(m31) as p31, avg(m32) as p32, avg(m33) as p33, avg(m34) as p34, avg(m35) as p35, avg(m36) as p36, avg(m37) as p37, avg(m38) as p38, "
					+ " avg(m39) as p39, avg(m40) as p40, avg(m41) as p41, avg(m42) as p42, avg(m43) as p43, avg(m44) as p44, avg(m45) as p45, avg(m46) as p46, avg(m47) as p47, avg(m48) as p48, "
					+ " avg(m49) as p49, avg(m50) as p50, avg(m51) as p51, avg(m52) as p52, avg(m53) as p53, avg(m54) as p54, avg(m55) as p55 FROM v_xzqh_monitor_crosstab, xzqh  "
					+ " WHERE v_xzqh_monitor_crosstab.xzqhdm like '" + statXZQ + "%' and xzqh.xzqhdm= '" + xzqh
					+ "' Group by xzqh.xzqhdm,xzqh.xzqhmc";
			Sql sql = Sqls.create(strSql);
			sql.setCallback(new SqlCallback() {
				public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {

					MonitorStat monitorStat = new MonitorStat();
					if (resultSet.next()) {
						monitorStat.setName(resultSet.getString("xzqhmc"));
						double[] value = new double[54];
						for (int i = 0; i < 54; i++) {
							BigDecimal bg = new BigDecimal(resultSet.getDouble("p" + (i + 1)));
							value[i] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}

						monitorStat.setValue(value);
					}
					return monitorStat;
				}
			});
			dao.execute(sql);
			// 加入平均值
			statResult.add(sql.getObject(MonitorStat.class));

			strSql = "select sum(weight) as score,mindex_id from monitor_index where mindex_id <55 group by mindex_id order by mindex_id";
			sql = Sqls.create(strSql);
			sql.setCallback(new SqlCallback() {
				public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {

					MonitorStat monitorStat = new MonitorStat();
					monitorStat.setName("目标值");
					double[] value = new double[54];

					while (resultSet.next()) {
						int i = resultSet.getInt("mindex_id");
						value[i - 1] = resultSet.getDouble("score");
					}

					monitorStat.setValue(value);

					return monitorStat;
				}
			});
			dao.execute(sql);
			// 加入目标值
			statResult.add(sql.getObject(MonitorStat.class));
			return statResult;

		} else {
			return null;
		}
	}

	@At
	public Object getTotalScore(@Param("year") int year, @Param("xzqhdm") String xzqh) {
		if (xzqh.trim() != "" && year != 0) {
			String statXZQ = xzqh;
			//去掉末尾的0 
			if(xzqh.endsWith("00")){
				statXZQ = xzqh.substring(0, xzqh.lastIndexOf("00"));	
				if(statXZQ.endsWith("00")){
					statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));	
				}
			}
			Sql sql = Sqls
					.create("select xzqh.xzqhdm, xzqhmc, sum(m_score) as sum_value from monitor_value inner join xzqh on xzqh.xzqhdm=monitor_value.xzqhdm "
							+ " where m_year= " + year + " and monitor_value.xzqhdm like '" + statXZQ + "%' "
							+ " group by xzqh.xzqhdm,xzqh.xzqhmc order by sum_value");
			sql.setCallback(new SqlCallback() {
				public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
					List<MonitorSumValue> monitorList = new ArrayList<MonitorSumValue>();
					while (resultSet.next()) {
						MonitorSumValue monitorValue = new MonitorSumValue();
						
						monitorValue.setName(resultSet.getString("xzqhmc"));
						//保留2位小数
						BigDecimal bg = new BigDecimal(resultSet.getDouble("sum_value"));						
						
						monitorValue.setValue(bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						monitorList.add(monitorValue);
					}
					return monitorList;
				}
			});

			dao.execute(sql);
			return sql.getList(MonitorSumValue.class);

		} else
			return null;

	}
	
	@At
	public Object getScore_Old(@Param("year") int year) {
		// Sql sql = Sqls.create("select xzqhdm, sum(m_value) as sum_value from
		// monitor_value where m_year=" + year + " group by xzqhdm");
		Sql sql = Sqls
				.create("select xzqhmc, sum(m_score) as sum_value from monitor_value inner join xzqh on xzqh.xzqhdm=monitor_value.xzqhdm where m_year="
						+ year + " group by xzqh.xzqhmc order by sum_value desc");
		sql.setCallback(new SqlCallback() {
			public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
				Map<String, Double> map = new HashMap<String, Double>();
				while (resultSet.next()) {
					map.put(resultSet.getString("xzqhmc"), resultSet.getDouble("sum_value"));
				}
				return map;
			}
		});

		dao.execute(sql);
		Map<String, Double> map = sql.getObject(HashMap.class);
		List<MonitorSumValue> dataList = new ArrayList<MonitorSumValue>();
		for (String key : map.keySet()) {
			MonitorSumValue data = new MonitorSumValue();
			data.setName(key);
			data.setValue(map.get(key));
			dataList.add(data);
		}
		// return Json.toJson(dataList);
		return dataList;
	}

	@At
	public Object getScore1(@Param("year") int year, @Param("xzqhdm") String xzqh) { 
		if (xzqh.trim() != "" && year != 0) {
			String statXZQ = xzqh.replaceAll("00", "");
			Sql sql = Sqls.create("SELECT xzqhdm,xzqhmc,index_yx1, index_yr2, index_cj3, index_jy4, index_sh5 FROM v_xzqh_monitor1 "
				 +" where xzqhdm like '"+statXZQ+"%'");
			sql.setCallback(new SqlCallback() {
				public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
					List<MonitorStat> monitorList = new ArrayList<MonitorStat>();
					while (resultSet.next()) {
						MonitorStat monitorValue = new MonitorStat();
						monitorValue.setName(resultSet.getString("xzqhmc"));
						double[] value = new double[5];
						//保留2位小数
						BigDecimal bg = new BigDecimal(resultSet.getDouble("index_yx1"));												
						value[0] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_yr2"));												
						value[1] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_cj3"));												
						value[2] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_jy4"));												
						value[3] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						bg = new BigDecimal(resultSet.getDouble("index_sh5"));												
						value[4] = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						
						
						monitorValue.setValue(value);
						monitorList.add(monitorValue);
					}
					return monitorList;
				}
			});

			dao.execute(sql);
			return sql.getList(MonitorStat.class);
		}else return null;
		
	}

	@At
	public Object getScore1_Old(@Param("year") int year) {
		// Sql sql = Sqls.create("select xzqhdm, sum(m_value) as sum_value from
		// monitor_value where m_year=" + year + " group by xzqhdm");

		// Sql sql = Sqls.create("select xzqhmc, sum(m_score) as sum_value from
		// monitor_value inner join xzqh on xzqh.xzqhdm=monitor_value.xzqhdm +"
		// + " inner join monitor_index on monitor_index.mindex_cd =
		// monitor_value.mpoint_cd where m_year=" + year
		// + " and index1_tscore="+indexid+" group by xzqh.xzqhmc order by
		// sum_value desc");
		List<Result> resultlist = dao.query(Result.class, Cnd.orderBy().desc("value_t"), null);
		return resultlist;
	}

	@At
	@Ok("void")
	public void export(HttpServletResponse resp, @Param("xzqhdm") String xzqh, @Param("year") int year) {
		Cnd cnd = Strings.isBlank(xzqh) ? null : Cnd.where("xzqhdm", "=", xzqh.trim());
		if (year != 0) {
			if (cnd == null)
				cnd = Cnd.where("m_year", "=", year);
			else
				cnd = cnd.and("m_year", "=", year);
		}

		// 获得某年某行政区的所有监测指标值及得分
		List<MonitorValue> list = dao.query(MonitorValue.class, cnd, null);
		try (OutputStream out = resp.getOutputStream();) {
			Workbook wb = WorkbookFactory
					.create(new File("/Users/liuting/Documents/JavaWorkspace/edudevelop/conf/附件1.xls"));
			String filename = URLEncoder.encode("浙江省县（市、区）教育现代化发展水平监测报表.xls", "UTF-8");
			resp.addHeader("content-type", "application/shlnd.ms-excel;charset=utf-8");
			resp.addHeader("content-disposition", "attachment; filename=" + filename);
			// List<Name> names = (List<Name>) wb.getAllNames();
			for (MonitorValue model : list) {
				String code = model.getMpoint_cd();
				double v = model.getValue();
				String value_cd = "P" + code;
				String grade_cd = "G" + code;
				Name name_value = wb.getName(value_cd);
				CellReference cr = new CellReference(name_value.getRefersToFormula());
				Sheet s = wb.getSheet(cr.getSheetName());
				Row r = s.getRow(cr.getRow());
				if (r == null) {
					r = s.createRow(cr.getRow());
				}
				Cell c = r.getCell(cr.getCol());
				if (c == null) {
					c = r.createCell(cr.getCol());
				}
				c.setCellValue(v);

				Name name_score = wb.getName(grade_cd);
				cr = new CellReference(name_score.getRefersToFormula());
				s = wb.getSheet(cr.getSheetName());
				r = s.getRow(cr.getRow());
				if (r == null) {
					r = s.createRow(cr.getRow());
				}
				c = r.getCell(cr.getCol());
				if (c == null) {
					c = r.createCell(cr.getCol());
				}
				c.setCellValue(model.getScore());
			}

			// AreaReference aref = new AreaReference(name.getRefersToFormula(),
			// SpreadsheetVersion.EXCEL2007);
			// CellReference[] crefs = aref.getAllReferencedCells();
			wb.write(out); // 输出
			// FileOutputStream fileOut = new
			// FileOutputStream("/Users/liuting/Desktop/工作簿2.xls");
			// wb.write(fileOut);
			// fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@At
	@Ok("void")
	
	public void exportPng(HttpServletResponse resp,HttpServletRequest req, @Param("xzqhdm") String xzqh, @Param("year") int year,
			@Param("radar1")String picRadar1,@Param("bar2")String picBar2,@Param("bar55")String picBar55,@Param("bar_t")String picBart){
		logger.debug("导出word文件开始>>>>>>>>>>>>>");  
		//获取行政区划信息
		Xzqh xzqhbean = dao.fetch(Xzqh.class,xzqh); 
        Map<String,Object> params = packageObject(xzqh,xzqhbean.getXzqhmc(),year); 
        
		
		// 传递过正中  "+" 变为了 " "  
        picRadar1 = picRadar1.replaceAll(" ", "+");  
        picBar2 = picBar2.replaceAll(" ", "+");  
        picBar55 = picBar55.replaceAll(" ", "+");  
//        picBart = picBart.replaceAll(" ", "+");  
        
		ImgBase64Util imgUtil = new ImgBase64Util();
		String tempPath = req.getSession().getServletContext().getRealPath("/tmp");
		String picRadar1Path = imgUtil.decodeBase64(picRadar1, new File(tempPath));     // 读取图片信息，返回图片保存路径 
		String picBar2Path = imgUtil.decodeBase64(picBar2, new File(tempPath));     // 读取图片信息，返回图片保存路径 
		String picBar55Path = imgUtil.decodeBase64(picBar55, new File(tempPath));     // 读取图片信息，返回图片保存路径 
//		String picBartPath = imgUtil.decodeBase64(picBart, new File(tempPath));     // 读取图片信息，返回图片保存路径 
		
		StatChart chart = new StatChart();
		chart.setFilePath(picRadar1Path);
		chart.setFileType("png");
		chart.setHeight(350);
		chart.setWidth(350);
		params.put("${radar1}", chart);
		chart = new StatChart();
		chart.setFilePath(picBar2Path);
		chart.setFileType("png");
		chart.setHeight(270);
		chart.setWidth(420);
		params.put("${bar2}", chart);
		chart = new StatChart();
		chart.setFilePath(picBar55Path);
		chart.setFileType("png");
		chart.setHeight(710);
		chart.setWidth(420);
		params.put("${bar55}", chart);
//		chart = new StatChart();
//		chart.setFilePath(picBartPath);
//		chart.setFileType("png");
//		chart.setHeight(320);
//		chart.setWidth(420);
//		params.put("${bar_t}", chart);
        //读入word模板  
        System.out.println(getClass().getClassLoader().getResource(""));
        InputStream is = getClass().getClassLoader().getResourceAsStream("CountyTemplate.docx");  
        try {
        	String filename = "浙江省县（市、区）教育现代化发展水平报告_"+xzqhbean.getXzqhmc()+".docx";
			filename = URLEncoder.encode(filename, "UTF-8");
			XwpfUtil xwpfUtil = new XwpfUtil(); 
	        xwpfUtil.exportWord(params,is,resp,xwpfUtil,filename);  
	        logger.debug("导出word文件完成>>>>>>>>>>>>>"); 
        } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
		//添加图片  
        //String[] imgs = {"D:\\bar.png","D:\\pie.png"};  
		
   
	}
	@At
	@Ok("void")
	public void exportWord(HttpServletResponse resp, @Param("xzqhdm") String xzqh, @Param("year") int year) {
		logger.debug("导出word文件开始>>>>>>>>>>>>>");  
		//获取行政区划信息
		Xzqh xzqhbean = dao.fetch(Xzqh.class,xzqh); 
		
        Map<String,Object> params = packageObject(xzqh,xzqhbean.getXzqhmc(),year);  
        XwpfUtil xwpfUtil = new XwpfUtil();  
        //读入word模板  
        System.out.println(getClass().getClassLoader().getResource(""));
        InputStream is = getClass().getClassLoader().getResourceAsStream("CountyTemplate.docx");  
        try {
        	String filename = "浙江省县（市、区）教育现代化发展水平报告_"+xzqhbean.getXzqhmc()+".docx";
			filename = URLEncoder.encode(filename, "UTF-8");
		
	        xwpfUtil.exportWord(params,is,resp,xwpfUtil,filename);  
	        logger.debug("导出word文件完成>>>>>>>>>>>>>"); 
        } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/** 
     * 组装word文档中需要显示数据的集合 
     * @return 
     */  
    public Map<String, Object> packageObject(String xzqhdm, String xzqhmc,int year) {  
    	if (xzqhdm.trim() != "" && year!=0) {
    		Map<String,Object> params = new HashMap<String,Object>();  
	    	//报告生成日期    		
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
    		params.put("${date}",sdf.format(new Date()));
    		
    		//报告行政区划信息    		  		
//    		String pxzqhdm = xzqhdm.substring(0,4)+"00";
//    		Xzqh pxzqh = dao.fetch(Xzqh.class,pxzqhdm);
//    		params.put("${name}", pxzqh.getXzqhmc()+xzqhmc);
    		//省平均值
    		MonitorValueUtil mUtils = new MonitorValueUtil(dao);
    		List<MonitorValue> mValueList; 			
			double tScore =0.0;
    		mValueList = mUtils.getAvgScore(year, xzqhdm.substring(0,2)+"0000");
    		for(MonitorValue mValue:mValueList){
    			String strKey = String.format("${ap%s}", mValue.getMpoint_cd());
				double value = mValue.getScore();
				tScore +=value;
				//保留2位小数
				BigDecimal bg = new BigDecimal(value);	
				params.put(strKey,  bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
    		}
    		params.put("${ap_t}", new BigDecimal(tScore).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
    		//县得分
    		mValueList = mUtils.getScore(year, xzqhdm);
    		tScore =0.0;
    		for(MonitorValue mValue:mValueList){
    			String strKey = String.format("${p%s}", mValue.getMpoint_cd());
				double value = mValue.getScore();
				tScore += value;
				// 保留2位小数
				BigDecimal bg = new BigDecimal(value);
				params.put(strKey, bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			params.put("${p_t}", new BigDecimal(tScore).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			// 目标值
//			List<MonitorIndex> mIndexList;
//			mIndexList = mUtils.getWeight(year, xzqhdm);
			//目标值
    		List<MonitorIndex> mIndexList = dao.query(MonitorIndex.class, Cnd.where("mindex_cd","<>","99"),null);
			tScore = 0.0;
			for (MonitorIndex mIndex : mIndexList) {

				String strKey = String.format("${sp%s}", mIndex.getMindex_cd());
				double value = mIndex.getWeight();
				tScore += value;
				// 保留2位小数
				BigDecimal bg = new BigDecimal(value);
				params.put(strKey, bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			params.put("${sp_t}", new BigDecimal(tScore).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

			// 获取评语模板
			List<DDMPointReport> rptTemps = dao.query(DDMPointReport.class, Cnd.orderBy().asc("jczbdm").asc("jcddm"));

			// 评语
			String remark01 = "";
			String remark02 = "";
			String remark03 = "";
			String remark04 = "";
			String remark05 = "";

			for (DDMPointReport temp : rptTemps){
	    		//得到监测点代码和临界值
	    		String mpoint_cd = temp.getJcddm();
	    		String mindex_cd = temp.getJczbdm(); 
	    		double threshold = temp.getMvalue_threshold();
	    		
	    		//一级指标代码
	    		String mindex1 = mindex_cd.substring(0, 2);
	    		String mremark = "";
	    		Cnd cnd = Cnd.where("xzqhdm", "=", xzqhdm.trim()).and("year","=",year).and("mpoint_cd","like",mpoint_cd+"%");	    		
	    		//获取某年某xzq相应监测点的值
	    		List<MonitorValue> mValues = dao.query(MonitorValue.class, cnd);
	    		//某些监测点的值需要将小项的值汇总	    		
	    		double sumScore=0.0;
	    		
	    		for(MonitorValue mValue:mValues){
	    			BigDecimal bg = new BigDecimal(mValue.getScore());    			
	    			//sumScore +=mValue.getScore();	    			
	    			sumScore +=bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	    		}
	    		//评估分小于临界值时，自动生成评语
	    		String strRemark=String.format("指标%s：%s；", mpoint_cd,temp.getMremark());
	        	if(threshold==0){
	        		mremark = (sumScore==0)?strRemark:"";
	        	}else{
	        		mremark = (sumScore<threshold)?strRemark:"";
	        	}
	        	//按一级指标分5段评语来组织
	        	if(mindex1.equals("01")){
	        		remark01+=mremark;
	        	}else if(mindex1.equals("02")){
	        		remark02+=mremark;
	        	}else if(mindex1.equals("03")){
	        		remark03+=mremark;
	        	}else if(mindex1.equals("04")){
	        		remark04+=mremark;
	        	}else if(mindex1.equals("05")){
	        		remark05+=mremark;
	        	}
	    	}
	    	//相应的word模版中的变量
	    	
//    		if(remark01.trim()!=""){
//    			remark01 = remark01.substring(0,remark01.length()-1)+"。";
//    		}
//    		if(remark02.trim()!=""){
//    			remark02 = remark02.substring(0,remark02.length()-1)+"。";
//    		}
//    		if(remark03.trim()!=""){
//    			remark03 = remark03.substring(0,remark03.length()-1)+"。";
//    		}
//    		if(remark04.trim()!=""){
//    			remark04 = remark04.substring(0,remark04.length()-1)+"。";
//    		}
//    		if(remark05.trim()!=""){
//    			remark05 = remark05.substring(0,remark05.length()-1)+"。";
//    		}
	    	if(!remark01.trim().equals("")){
    			remark01 = remark01.substring(0,remark01.length()-1)+"。";
    		}
    		if(!remark02.trim().equals("")){
    			remark02 = remark02.substring(0,remark02.length()-1)+"。";
    		}
    		if(!remark03.trim().equals("")){
    			remark03 = remark03.substring(0,remark03.length()-1)+"。";
    		}
    		if(!remark04.trim().equals("")){
    			remark04 = remark04.substring(0,remark04.length()-1)+"。";
    		}
    		if(!remark05.trim().equals("")){
    			remark05 = remark05.substring(0,remark05.length()-1)+"。";
    		}

        	//word模版中对应的变量及替换的值
        	params.put("${mindex01}", remark01);
        	params.put("${mindex02}", remark02);
        	params.put("${mindex03}", remark03);
        	params.put("${mindex04}", remark04);
        	params.put("${mindex05}", remark05);
	    	return params;
    	}else
    		return null;
    }  
}
