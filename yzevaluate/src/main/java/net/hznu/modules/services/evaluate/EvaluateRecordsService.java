package net.hznu.modules.services.evaluate;

import net.hznu.common.base.Service;
import net.hznu.common.chart.IndexValueStat;
import net.hznu.modules.models.evaluate.Evaluate_records;
import net.hznu.modules.models.sys.Sys_role;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class EvaluateRecordsService extends Service<Evaluate_records> {
	private static final Log log = Logs.get();

    public EvaluateRecordsService(Dao dao) {
    	super(dao);
    }
    /**
     * 级联删除评估记录
     * 2018-12-23 改为通过数据库设置外键级联删除
     * @param evaluateId
     */
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(String evaluateId) {
        //dao().execute(Sqls.create("delete from evaluate_records where path like @path").setParam("path", catalog.getPath() + "%"));
        //清空该评估记录下的所有观测点评分

        dao().execute(Sqls.create("delete from evaluate_remark where evaluateid=@id ").setParam("id", evaluateId));
        dao().execute(Sqls.create("delete from evaluate_custom where evaluateid=@id ").setParam("id", evaluateId));
        dao().execute(Sqls.create("delete from evaluate_summary where evaluateid=@id").setParam("id",evaluateId));
        dao().execute(Sqls.create("delete from evaluate_appendix where evaluateid=@id").setParam("id",evaluateId));
        //清空该评估记录
        delete(evaluateId);


    }

    public double getProgress_s(String evaluateId){
        Sql sql = Sqls.create("select count(indexid) from evaluate_remark where evaluateid = @eid").setParam("eid", evaluateId);
        int iTotalIndex_r = count(Sqls.create("select count(indexid) from evaluate_remark inner join monitor_index on monitor_index.id = evaluate_remark.indexid " +
                " where monitor_index.selfeva is true and evaluateid = @eid").setParam("eid", evaluateId));
        int iTotalIndex_c = count(Sqls.create("select count(indexname) from evaluate_custom where evaluateid = @eid").setParam("eid", evaluateId));
        int iCustomIndex = count(Sqls.create("select count(indexname) from evaluate_custom where evaluateid = @eid and selfeva=@eva").setParam("eid", evaluateId).setParam("eva",true));
        int iRemarkIndex = count(Sqls.create("select count(indexid) from evaluate_remark inner join monitor_index on monitor_index.id = evaluate_remark.indexid " +
                " where monitor_index.selfeva is true and evaluateid = @eid and evaluate_remark.selfeva=@eva").setParam("eid", evaluateId).setParam("eva",true));

        return (double)(iRemarkIndex+iCustomIndex)/(iTotalIndex_r+iTotalIndex_c);
    }

    public double getProgress_p(String evaluateId){
        Sql sql = Sqls.create("select count(indexid) from evaluate_remark where evaluateid = @eid").setParam("eid", evaluateId);
        int iTotalIndex_r = count(Sqls.create("select count(indexid) from evaluate_remark where evaluateid = @eid").setParam("eid", evaluateId));
        int iTotalIndex_c = count(Sqls.create("select count(indexname) from evaluate_custom where evaluateid = @eid").setParam("eid", evaluateId));

        int iRemarkIndex = count(Sqls.create("select count(indexid) from evaluate_remark where evaluateid = @eid and verifyeva=@eva").setParam("eid", evaluateId).setParam("eva",true));
        int iCustomIndex = count(Sqls.create("select count(indexname) from evaluate_custom where evaluateid = @eid and verifyeva=@eva").setParam("eid", evaluateId).setParam("eva",true));

        return (double)(iRemarkIndex+iCustomIndex)/(iTotalIndex_r+iTotalIndex_c);
    }
    public NutMap getSpecialUser(){
        NutMap re = new NutMap();
        List<Record> records = list(Sqls.create("SELECT sys_user.id, loginname,nickname FROM sys_user inner join sys_unit on sys_unit.id = sys_user.unitid WHERE aliasname='Special' "));
        re.put("data", records);
        re.put("recordsTotal", records.size());
        return re;

    }
    //获得评估的自评总得分
    public double getTotalScore_s(String evaluateId){

        double score_r = score(Sqls.create("select sum(score_s) from evaluate_remark where evaluateid = @eid").setParam("eid", evaluateId));
        double score_c = score(Sqls.create("select sum(score_s) from evaluate_custom where evaluateid = @eid").setParam("eid", evaluateId));
        return score_r+score_c;
    }
    //获得评估的审核评估总得分
    public double getTotalScore_p(String evaluateId){
        double score_r = score(Sqls.create("select sum(score_p) from evaluate_remark where evaluateid = @eid").setParam("eid", evaluateId));
        double score_c = score(Sqls.create("select sum(score_p) from evaluate_custom where evaluateid = @eid").setParam("eid", evaluateId));
        return score_r+score_c;


    }

    //获取评估组别
    public List<Record> getGrouplist(){
        Sql sql = Sqls.create("Select taskname, string_agg(id,',') as id from evaluate_records group by taskname order by case taskname\n" +
                "when '第一组评估' then 1\n" +
                "when '第二组评估' then 2 \n" +
                "when '第三组评估' then 3 \n" +
                "when '第四组评估' then 4 \n" +
                "when '第五组评估' then 5 \n" +
                "when '第六组评估' then 6  \n" +
                "end ");
        List<Record> indexlist = list(sql);
        return  indexlist;
    }
    //按组获取学校名称
    public List<Record> getSchoollistbygroup(String taskname){
        Sql sql = Sqls.create("SELECT  evaluate_records.id,sys_unit.name FROM evaluate_records inner join sys_unit on schoolid=sys_unit.id where taskname= @taskname").setParam("taskname", taskname);
        List<Record> indexlist = list(sql);
        return  indexlist;
    }


    /**
     * 获取本项目的专家组角色，并查找评估记录中是否有给各专家组角色赋予相应的专家名单
     * @param evaluateIds 评估记录Id
     * @return 返回给前端 具体的角色及专家数组列表，以及记录数，该项目是4位专家角色
     */
    public NutMap getEvaluateSpecial(String[] evaluateIds){
        NutMap re = new NutMap();
        Sql sql = Sqls.create("SELECT sys_role.id,sys_role.name,sys_role.code FROM sys_role inner join sys_unit on sys_unit.id = sys_role.unitid WHERE sys_unit.aliasname='Special' order by sys_role.name");
        Entity<Sys_role> entity = dao().getEntity(Sys_role.class);
        sql.setEntity(entity);
        sql.setCallback(Sqls.callback.entities());
        dao().execute(sql);
        List<Sys_role> roles = sql.getList(Sys_role.class);

        List<Record> roleRecords = new ArrayList<>();


        for(Sys_role role:roles){
            Record r = new Record();
            String roleId = role.getId();
            r.put("id",roleId);
            r.put("rolename",role.getName());

            sql = Sqls.create("SELECT distinct sys_user.nickname FROM sys_user inner join evaluate_special_role on evaluate_special_role.specialid = sys_user.id WHERE evaluate_special_role.roleid=@roldid and evaluateid in (@evaluateid)");
            sql.params().set("roldid",roleId);
            sql.params().set("evaluateid", evaluateIds);
            sql.setCallback(new SqlCallback() {
                public Object invoke(Connection conn, ResultSet rs, Sql sql)
                        throws SQLException {
                    String username = "";
                    while (rs != null && rs.next()) {
                        username += rs.getString(1) + ";";
                    }
                    return username;
                }
            });
            this.dao().execute(sql);
            r.put("username",sql.getString());
            roleRecords.add(r);
        }

        re.put("data", roleRecords);
        re.put("recordsTotal", roleRecords.size());
        return re;

    }

    /**
     * 给一组评估记录和专家角色分配专家
     * @param evaluateid
     * @param roleid
     * @param uid
     */
    public void assignEvaluateSpecial(String evaluateid, String roleid,String uid){
        this.dao().clear("evaluate_special_role", Cnd.where("evaluateid","=",evaluateid).and("roleid","=",roleid));
        insert("evaluate_special_role", org.nutz.dao.Chain.make("evaluateid", evaluateid).add("specialid",uid).add("roleid", roleid));
    }

    /**
     * 获取专家参与的评估ID
     * @param suid 专家的用户id
     * @return  专家负责的评估ID
     */
    public List<String> getEvaluateIdsBySpecial(String suid){
        Sql sql = Sqls.create("SELECT distinct evaluateid FROM evaluate_special_role WHERE specialid=@userid").setParam("userid",suid);
        sql.setCallback(new SqlCallback() {
            public Object invoke(Connection conn, ResultSet rs, Sql sql)
                    throws SQLException {
                List<String> evaluateIds = new ArrayList<>();

                while (rs != null && rs.next()) {
                    String eid = rs.getString(1) ;
                    evaluateIds.add(eid);
                }
                return evaluateIds;
            }
        });
        this.dao().execute(sql);
        return sql.getList(String.class);
    }
    /**
     * 获取专家参与的评估ID
     * @param suid 专家的用户id
     * @return  专家负责的评估ID
     */
    public List<String> getTasknameBySpecial(String suid){
        Sql sql = Sqls.create("SELECT distinct evaluate_records.taskname FROM evaluate_special_role inner join evaluate_records on evaluate_records.id = evaluate_special_role.evaluateid WHERE evaluate_special_role.specialid=@userid").setParam("userid",suid);
        sql.setCallback(new SqlCallback() {
            public Object invoke(Connection conn, ResultSet rs, Sql sql)
                    throws SQLException {
                List<String> evaluateIds = new ArrayList<>();

                while (rs != null && rs.next()) {
                    String eid = rs.getString(1) ;
                    evaluateIds.add(eid);
                }
                return evaluateIds;
            }
        });
        this.dao().execute(sql);
        return sql.getList(String.class);
    }
    /**
     * 获取专家参与的评估ID
     * @param suid 专家的用户id
     * @return  专家负责的评估ID
     */
    public List<String> getEvaluateIdsByTaskname(String taskname){
        Sql sql = Sqls.create("SELECT distinct id FROM evaluate_records WHERE taskname = @taskname").setParam("taskname",taskname);
        sql.setCallback(new SqlCallback() {
            public Object invoke(Connection conn, ResultSet rs, Sql sql)
                    throws SQLException {
                List<String> evaluateIds = new ArrayList<>();

                while (rs != null && rs.next()) {
                    String eid = rs.getString(1) ;
                    evaluateIds.add(eid);
                }
                return evaluateIds;
            }
        });
        this.dao().execute(sql);
        return sql.getList(String.class);
    }
    public List<Record> getSpecialRemarkData(String eid){
        Sql sql = Sqls.create("SELECT monitor_catalog.path,sum(score_p) as score_p, string_agg(advantage,'\n') as advantage, string_agg(disadvantage,'\n') as disadvantage" +
                "  FROM evaluate_remark_view " +
                "  inner join monitor_catalog on catalogid = monitor_catalog.id" +
                "  where depttype ='Special' and evaluateid=@evaluateid " +
                "  group by monitor_catalog.path").setParam("evaluateid",eid);
        return list(sql);
    }

    public List<Record> getRemarkData(String eid){
        //Sql sql = Sqls.create("select location,sum(score_s) as score_s, sum(score_p) as score_p ,string_agg(remark_s,'\n' order by indexname ) as remark_s,string_agg(id,',') as id FROM evaluate_remark_view where evaluateid=@eid group by location").setParam("evaluateid",eid);
        //return list(sql);
        List<Record> remarkList = list(Sqls.create("select location,sum(score_s) as score_s, sum(score_p) as score_p ,string_agg(remark_s,'\n' order by indexname ) as remark_s,string_agg(id,',') as id FROM evaluate_remark_view where evaluateid=@eid group by location ")
                .setParam("eid",eid));
        return remarkList;
    }

    public Sys_role getRoleInEvaluate(String suid, String eid){
        Sql sql = Sqls.create("SELECT Sys_role.* FROM Sys_Role inner join evaluate_special_role on evaluate_special_role.roleid = Sys_role.id WHERE evaluateid=@evaluateid and specialid=@userid").setParam("evaluateid",eid).setParam("userid",suid);
        Entity<Sys_role> entity = dao().getEntity(Sys_role.class);
        sql.setEntity(entity);
        sql.setCallback(Sqls.callback.entities());
        dao().execute(sql);
        return sql.getObject(Sys_role.class);
    }
    public Record getSummaryInfo(String eid){
        Sql sql = Sqls.create("SELECT summaryurl,verifyreport,uploaderid,sys_user.nickname from evaluate_records inner join sys_user on sys_user.id = evaluate_records.uploaderid WHERE evaluate_records.id=@eid").setParam("eid",eid);
        sql.setCallback(new SqlCallback() {
            public Object invoke(Connection conn, ResultSet rs, Sql sql)
                    throws SQLException {
                if (rs != null && rs.next()) {
                    Record record = new Record();
                    record.put("summaryurl",rs.getString("summaryurl"));
                    record.put("verifyreport",rs.getBoolean("verifyreport"));
                    record.put("uploaderid",rs.getString("uploaderid"));
                    record.put("uploader",rs.getString("nickname"));
                    return record;
                }else {
                    return null;
                }

            }
        });
        this.dao().execute(sql);
        return sql.getObject(Record.class);
    }

    /**
     * 获取本项目的专家组角色，并查找评估记录中是否有给各专家组角色赋予相应的专家名单
     * @param evaluateIds 评估记录Id
     * @return 返回给前端 具体的角色及专家数组列表，以及记录数，该项目是4位专家角色
     */
    public NutMap getIndexReport(String[] evaluateIds){
        NutMap re = new NutMap();
        List<Map<String,Object>> cols =new ArrayList<>();
        Map<String,Object> col = new HashMap<>();
        col.put("targets",0);
        col.put("data","no");
        col.put("title","序号");
        cols.add(col);
        col = new HashMap<>();
        col.put("targets",1);
        col.put("data","indexname");
        col.put("title","指标名称");
        cols.add(col);
        col = new HashMap<>();
        col.put("targets",2);
        col.put("data","weights");
        col.put("title","权重分");
        cols.add(col);

        String strSql="SELECT monitor_catalog.location as no,monitor_catalog.name as indexname,monitor_catalog.weights,";
        String strSchoolSql="";
        for(int i=0;i<evaluateIds.length;i++){
            String eid =evaluateIds[i];
            col = new HashMap<>();
            col.put("targets",i+3);
            col.put("data","e_"+eid);
            Evaluate_records records = fetch(eid);
            col.put("title",fetchLinks(records,"school").getSchool().getName());
            cols.add(col);

            strSchoolSql+=" sum(case when evaluateid='"+eid+"' then score_p else null end) as e_"+eid+",";
        }
        strSql += strSchoolSql.substring(0,strSchoolSql.length()-1);
        strSql += " FROM evaluate_remark inner join monitor_index on monitor_index.id = evaluate_remark.indexid \n" +
                " inner join monitor_catalog on monitor_index.catalogid = monitor_catalog.id \n where evaluateid in (@evaIds) \n"+
                " group by monitor_catalog.location,monitor_catalog.name,monitor_catalog.weights order by monitor_catalog.location";

        Sql sql = Sqls.create(strSql).setParam("evaIds",evaluateIds);
        List<Record> remarkData = list(sql);

        //发展性指标
        strSql = "SELECT 10 as no, '发展性指标' as indexname,10 as weights,";
        strSql += strSchoolSql.substring(0,strSchoolSql.length()-1);
        strSql +=" FROM evaluate_custom where evaluateid in (@evaIds)";
        sql = Sqls.create(strSql).setParam("evaIds",evaluateIds);
        List<Record> custom = list(sql);

        remarkData.addAll(custom);
        re.put("rowdata", remarkData);
        re.put("col_define", cols);
        return re;

    }
}

