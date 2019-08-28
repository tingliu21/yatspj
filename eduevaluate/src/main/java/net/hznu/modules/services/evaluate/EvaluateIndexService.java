package net.hznu.modules.services.evaluate;

import net.hznu.common.base.Service;
import net.hznu.common.chart.MonitorStat;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.common.page.OffsetPager;
import net.hznu.modules.models.evaluate.Evaluate_index;

import net.hznu.modules.models.evaluate.Evaluate_records;
import net.hznu.modules.models.evaluate.Evaluate_special;
import net.hznu.modules.models.monitor.Monitor_catalog;
import net.hznu.modules.models.monitor.Monitor_index;
import net.hznu.modules.models.sys.Sys_config;
import org.apache.commons.lang.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.util.cri.SqlExpression;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@IocBean(args = {"refer:dao"})
public class EvaluateIndexService extends Service<Evaluate_index> {
	private static final Log log = Logs.get();

    public EvaluateIndexService(Dao dao) {
    	super(dao);
    }

    /**
     * @param length   页大小
     * @param start    start
     * @param draw     draw
     * @param orders   排序
     * @param columns  字段
     * @param cnd      查询条件
     * @param linkname 关联查询
     * @return 返回指标列表，其中指标名称含完整路径
     */
    public NutMap data(int length, int start, int draw, List<DataTableOrder> orders, List<DataTableColumn> columns, Cnd cnd, String linkname) {
        NutMap re = new NutMap();
        if (orders != null && orders.size() > 0) {
            for (DataTableOrder order : orders) {
                DataTableColumn col = columns.get(order.getColumn());
                cnd.orderBy(Sqls.escapeSqlFieldValue(col.getData()).toString(), order.getDir());
            }
        }
        Pager pager = new OffsetPager(start, length);

        re.put("recordsFiltered", count(cnd));
        List<Evaluate_index> list = query(cnd, pager);
        if (!Strings.isBlank(linkname)) {
            this.dao().fetchLinks(list, linkname);
        }
        //遍历list，修改指标名称
        for(Evaluate_index index :list){
            String code = index.getCode();
            if(code.length()>2){
                SqlExpressionGroup group = new SqlExpressionGroup();
                String t_code = code.substring(0,code.length()-2);
                SqlExpression sqlExpression =Cnd.exps("code","=",t_code);
                group.or(sqlExpression);
                if(t_code.length()>2){
                    t_code = t_code.substring(0,t_code.length()-2);
                    sqlExpression =Cnd.exps("code","=",t_code);
                    group.or(sqlExpression);
                }
                String fullname="";
                //获取完整指标名称
                List<Monitor_index> mindexList = dao().query(Monitor_index.class,cnd.where("year","=",index.getYear()).and(group).asc("code"));
                for(Monitor_index mindex:mindexList){
                    fullname = mindex.getName()+"/";
                }
                fullname = fullname+index.getName();
                index.setName(fullname);

            }
        }
        re.put("data", list);
        re.put("draw", draw);
        re.put("recordsTotal", length);
        return re;
    }

    /**
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
    /**
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
                        double[] value = new double[fieldnames.size()];
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

    /**
     * 功能：获取各指标的省均值或市均值
     * xzqhdm：为行政区划代码，一般为省级或地市级行政区划代码
     * @return
     */
    public List<Evaluate_index> getAvgEvaluateIndex(int year, String xzqhdm){
        if(xzqhdm.endsWith("00")) {
            String statXZQ = xzqhdm.substring(0, xzqhdm.lastIndexOf("00"));
            if (statXZQ.endsWith("00")) {
                statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));
            }
            Sql sql = Sqls.create("SELECT  code, cast(avg(score) as numeric(4,2)) as score FROM evaluate_index_view where year =@year and unitcode like '"+statXZQ+"%' group by code  order by code");
            sql.params().set("year",year);
//            sql.params().set("xzqh",statXZQ);

            Entity<Evaluate_index> entity = dao().getEntity(Evaluate_index.class);
            sql.setEntity(entity);
            sql.setCallback(Sqls.callback.entities());
            dao().execute(sql);

            return sql.getList(Evaluate_index.class);
        }else
            return null;
    }
    /**
     * 组装word文档中需要显示表格数据的集合
     * @return
     */
    public Map<String, Object> packageParaObject(String evaluateId) {
        if (StringUtils.isNotBlank(evaluateId)) {
            Map<String,Object> params = new HashMap<String,Object>();
            //报告生成日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
            params.put("${date}",sdf.format(new Date()));

            Evaluate_special specialReport = dao().fetch(Evaluate_special.class,evaluateId);
            params.put("${remark1}",specialReport.getRemark1());
            params.put("${remark2}",specialReport.getRemark2());
            params.put("${remarkp}",specialReport.getRemarkp());

            //评语建议

            //报告行政区划信息
//    		String pxzqhdm = xzqhdm.substring(0,4)+"00";
//    		Xzqh pxzqh = dao.fetch(Xzqh.class,pxzqhdm);
//    		params.put("${name}", pxzqh.getXzqhmc()+xzqhmc);


            return params;
        }else
            return null;
    }
    /**
     * 组装word文档中需要显示表格数据的集合
     * @return
     */
    public Map<String, Object> packageTableObject(String xzqhdm, String xzqhmc, int year) {
        if (xzqhdm.trim() != "" && year!=0) {
            Map<String,Object> params = new HashMap<String,Object>();

            //省平均值
            List<Evaluate_index> mValueList = getAvgEvaluateIndex(year,xzqhdm.substring(0,2)+"0000");
            double tScore =0.0;

            for(Evaluate_index mValue:mValueList){
                String strKey = String.format("${as%s}", mValue.getCode());
                double value = mValue.getScore();
                tScore +=value;
                //保留2位小数
                BigDecimal bg = new BigDecimal(value);
                params.put(strKey,  bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            params.put("${as_t}", new BigDecimal(tScore).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            //县评估值及得分
            mValueList = query(Cnd.where("year","=",year).and("unitcode","=",xzqhdm));
            tScore =0.0;
            for(Evaluate_index mValue:mValueList){
                String strKey = String.format("${s%s}", mValue.getCode());
                Double value = mValue.getScore();
                if(value!=null){
                    tScore += value;
                    // 保留2位小数
                    BigDecimal bg = new BigDecimal(value);
                    params.put(strKey, bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }
                //县评估值
                strKey = String.format("${v%s}", mValue.getCode());
                if(StringUtils.isNotBlank(mValue.getSvalue())) {
                    params.put(strKey, mValue.getSvalue());
                }
            }
            params.put("${s_t}", new BigDecimal(tScore).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());


            return params;
        }else
            return null;
    }
    public void generateRemark(Evaluate_records evaluate){
        //获取各级指标评语模版
        Map<String, String> tplConfig = new HashMap<> ();
        List<Sys_config> configList = dao().query(Sys_config.class, Cnd.where("configkey","like","tpl%"));
        for (Sys_config sysConfig : configList) {
            tplConfig.put(sysConfig.getConfigKey(), sysConfig.getConfigValue());
        }

        //评估相关信息
        String xzqh = evaluate.getUnitcode();
        int year = evaluate.getYear();
        String proXZQH = xzqh.substring(0, 2) + "0000";

        String remark1,remark2,remarkp,strLoop;
        int num_0=0,num_b=0,num_s =0;
        String str_0="",str_b="",str_s="";

        //获取指标值
        //总分
        double as_t = score(Sqls.create("select avg(score) :: numeric(5,2) FROM evaluate_records where year=@year").setParam("year",year));
        remark1 =tplConfig.get("tpl1").replace("@year",String.valueOf(year)).replace("@s_t",String.valueOf(evaluate.getScore()));
        if(evaluate.getScore()>as_t){
            remark1 = remark1.replace("@c_t","高").replace("@ad_t",String.format("%.2f",evaluate.getScore()-as_t));
        }else if(evaluate.getScore()<as_t){
            remark1 = remark1.replace("@c_t","低").replace("@ad_t",String.format("%.2f",as_t-evaluate.getScore()));
        }else{
            remark1 = remark1.replace("@c_t","等").replace("@ad_t","");
        }

        double[] values_c,values_p;
        List<String> fieldnames = new ArrayList<>();
        String viewname = "v_xzqh_evaluate1_" + year;
        //获取一级指标
        List<Monitor_catalog> catalogs = dao().query(Monitor_catalog.class,Cnd.where("level", "=", 1).
                and("year", "=", year).and("weights", ">", 0).asc("catacode"));
        for (Monitor_catalog catalog : catalogs) {
            String fieldname = "index_" + catalog.getCatacode();
            fieldnames.add(fieldname);
        }
        //县得分
        values_c = getScoreByXZQH(viewname, fieldnames, xzqh).get(0).getValue();
        //省均值
        values_p = getAvgScoreByXZQH(viewname, fieldnames, proXZQH).getValue();

        //一级指标达成度说明
        strLoop= "";
        for(int i=0;i<values_c.length;i++){
            strLoop += tplConfig.get("tpl1_l").replace("@n_i",catalogs.get(i).getName()).replace("@s_i",String.format("%.2f",values_c[i]));
            if(values_c[i]>values_p[i]){
                strLoop = strLoop.replace("@c_i","高").replace("@d_i",String.format("%.2f",values_c[i]-values_p[i]));
            }else if(values_c[i]<values_p[i]){
                strLoop = strLoop.replace("@c_i","低").replace("@d_i",String.format("%.2f",values_p[i]-values_c[i]));
            }else{
                strLoop = strLoop.replace("@c_i","等").replace("@d_i","");
            }
        }
        //去掉末尾的分号；
        strLoop = strLoop.substring(0,strLoop.length()-1);
        remark1 = remark1.replace("@tpl1_l",strLoop);
        //二级指标评语
        viewname = "v_xzqh_evaluate2_" + year;
        catalogs = dao().query(Monitor_catalog.class,Cnd.where("level", "=", 2).
                and("year", "=", year).and("weights", ">", 0).asc("catacode"));
        fieldnames = new ArrayList<>(catalogs.size());
        for (Monitor_catalog catalog : catalogs) {
            String fieldname = "index_" + catalog.getCatacode();
            fieldnames.add(fieldname);

        }
        //县得分
        values_c = getScoreByXZQH(viewname, fieldnames, xzqh).get(0).getValue();
        //省均值
        values_p = getAvgScoreByXZQH(viewname, fieldnames, proXZQH).getValue();
        //二级指标达成度说明
        strLoop= "";
        for(int i=0;i<values_c.length;i++){

            if(values_c[i]>values_p[i]){
                num_b++;
                str_b += tplConfig.get("tpl2_d_l").replace("@n_i",catalogs.get(i).getName()).replace("@c_i","高").replace("@d_i",String.format("%.2f",values_c[i]-values_p[i]));

            }else if(values_c[i]<values_p[i]){
                num_s++;
                str_s += tplConfig.get("tpl2_d_l").replace("@n_i",catalogs.get(i).getName()).replace("@c_i","低").replace("@d_i",String.format("%.2f",values_p[i]-values_c[i]));

            }
        }
        //去掉末尾的分号；
        if(str_b!="") {
            str_b = str_b.substring(0,str_b.length()-1);
            strLoop = tplConfig.get("tpl2_l").replace("@c_i","高").replace("@cnt",String.valueOf(num_b)).replace("@tpl2_d_l",str_b);
        }
        if(str_s!="") {
            str_s = str_s.substring(0,str_s.length()-1);
            strLoop	+=	tplConfig.get("tpl2_l").replace("@c_i","低").replace("@cnt",String.valueOf(num_s)).replace("@tpl2_d_l",str_s);
            strLoop = strLoop.substring(0,strLoop.length()-1);
        }
        remark2= tplConfig.get("tpl2").replace("@cnt",String.valueOf(fieldnames.size())).replace("@tpl2_l",strLoop);
        //监测点评语
        viewname = "v_xzqh_evaluate_crosstab_" +year;
        List<Monitor_index> indexList = dao().query(Monitor_index.class,Cnd.where("level", "=", 1).
                and("year", "=", year).and("weights", ">", 0).asc("location"));
        fieldnames = new ArrayList<>(indexList.size());
        for (Monitor_index index : indexList) {
            String fieldname = "m" + index.getLocation();
            fieldnames.add(fieldname);
        }
        //县得分
        values_c = getScoreByXZQH(viewname, fieldnames, xzqh).get(0).getValue();
        //省均值
        values_p = getAvgScoreByXZQH(viewname, fieldnames, proXZQH).getValue();
        //监测点达成度说明
        strLoop= "";
        num_s =0;str_s="";
        for(int i=0;i<values_c.length;i++){
            if(values_c[i]==0){
                num_0++;
                str_0 += tplConfig.get("tplp_l").replace("@n_i",indexList.get(i).getName());
            }else if(values_c[i]<values_p[i]){
                num_s++;
                str_s += tplConfig.get("tplp_l").replace("@n_i",indexList.get(i).getName());
            }
        }
        //去掉末尾顿号；
        if(str_0!="") {
            str_0 = str_0.substring(0,str_0.length()-1);
            strLoop = tplConfig.get("tplp_0").replace("@cnt_0",String.valueOf(num_0)).replace("@tplp_l",str_0);
        }
        remarkp = tplConfig.get("tplp").replace("@tplp_0",strLoop);
        strLoop="";
        if(str_s!="") {
            str_s = str_s.substring(0,str_s.length()-1);
            strLoop	= tplConfig.get("tplp_s").replace("@cnt_s",String.valueOf(num_s)).replace("@tplp_l",str_s);

        }
        remarkp = remarkp.replace("@cnt",String.valueOf(fieldnames.size())).replace("@tplp_s",strLoop);

        dao().execute(Sqls.create("insert into evaluate_special(evaluateid,remark1,remark2,remarkp) values (@evaluateid,@remark1,@remark2,@remarkp)")
                .setParam("evaluateid",evaluate.getId()).setParam("remark1",remark1).setParam("remark2",remark2).setParam("remarkp",remarkp));

    }

}

