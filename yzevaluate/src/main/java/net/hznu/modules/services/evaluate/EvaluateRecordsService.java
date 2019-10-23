package net.hznu.modules.services.evaluate;


import net.hznu.common.base.Service;
import net.hznu.modules.models.evaluate.Evaluate_records;
import net.hznu.modules.models.sys.Sys_role;
import org.apache.commons.lang3.StringUtils;
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
        Sql sql = Sqls.create("SELECT id FROM evaluate_records WHERE taskname = @taskname order by score_p desc").setParam("taskname",taskname);
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
        Sql sql = Sqls.create("SELECT monitor_catalog.path,sum(score_p) as score_p, string_agg(advantage,'\n') as advantage, string_agg(disadvantage,'\n') as disadvantage,string_agg(remark_p,'\n') as remark_p" +
                "  FROM evaluate_remark_view " +
                "  inner join monitor_catalog on catalogid = monitor_catalog.id" +
                "  where depttype ='Special' and evaluateid=@evaluateid " +
                "  group by monitor_catalog.path").setParam("evaluateid",eid);
        return list(sql);
    }

    public List<Record> getRemarkData(String eid){
        //Sql sql = Sqls.create("select location,sum(score_s) as score_s, sum(score_p) as score_p ,string_agg(remark_s,'\n' order by indexname ) as remark_s,string_agg(id,',') as id FROM evaluate_remark_view where evaluateid=@eid group by location").setParam("evaluateid",eid);
        //return list(sql);
        List<Record> remarkList = list(Sqls.create("select location,sum(score_s) as score_s, sum(score_p) as score_p ,string_agg(remark_s,'\n' order by indexname ) as remark_s,advantage,disadvantage,remark_p,string_agg(id,',') as id FROM evaluate_remark_view where evaluateid=@eid group by location,advantage,disadvantage,remark_p")
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
     * 给定评估记录ID，按二级指标汇总各学校的指标得分情况，以及按专家角色汇总各学校不同专家的打分情况
     * @param evaluateIds 评估记录Id
     * @return 返回给前端 10行一级指标+1行总分+4行角色打分，共15行的报表，报表列数依据幼儿园名单而定
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

        String strSql="SELECT monitor_catalog.location as no,monitor_catalog.name || (case sys_unit.aliasname when 'Special' then '(专家)' when 'Education' then '(科室)' else null end) as indexname,(sum(monitor_index.weights)/"+evaluateIds.length+") ::numeric(3,0) as weights,";
        String strSchoolSql="";
        int i=0;
        for( ;i<evaluateIds.length;i++){
            String eid =evaluateIds[i];
            col = new HashMap<>();
            col.put("targets",i+3);
            col.put("data","e_"+eid);
            Evaluate_records records = fetch(eid);
            col.put("title",fetchLinks(records,"school").getSchool().getName());
            cols.add(col);

            strSchoolSql+=" sum(case when evaluateid='"+eid+"' then score_p else 0 end) as e_"+eid+",";
        }
        col = new HashMap<>();
        col.put("targets",i+3);
        col.put("data","avg_group");
        col.put("title","组平均得分");
        cols.add(col);

        //求平均分
        strSchoolSql+=" COALESCE(sum(score_p)/"+evaluateIds.length+",0)::numeric(4,2) as avg_group";
        strSql += strSchoolSql;
        strSql += " FROM evaluate_remark inner join monitor_index on monitor_index.id = evaluate_remark.indexid \n" +
                " inner join monitor_catalog on monitor_index.catalogid = monitor_catalog.id \n"+
                " inner JOIN sys_unit ON monitor_index.department::text = sys_unit.id::text where evaluateid in (@evaIds) \n"+
                " group by monitor_catalog.location,monitor_catalog.name,sys_unit.aliasname order by monitor_catalog.location,sys_unit.aliasname";

        Sql sql = Sqls.create(strSql).setParam("evaIds",evaluateIds);
        List<Record> remarkData = list(sql);

        //发展性指标
        strSql = "SELECT 10 as no, '发展性指标' as indexname,10 as weights,";
        strSql += strSchoolSql;
        strSql +=" FROM evaluate_custom where evaluateid in (@evaIds)";
        sql = Sqls.create(strSql).setParam("evaIds",evaluateIds);
        sql.setCallback(Sqls.callback.record());
        this.dao().execute(sql);
        Record custom = sql.getObject(Record.class);
        remarkData.add(custom);

//        remarkData.addAll(custom);
        //当前总分
        List<Evaluate_records> recordsList = query(Cnd.where("id","in",evaluateIds).desc("score_p"));
        double totalScore=0.0;
        Record record = new Record();
        record.put("no","");
        record.put("indexname","总分");
        record.put("weights",recordsList.get(0).getWeights());
        for(Evaluate_records evaluate_records : recordsList){
            totalScore +=evaluate_records.getScore_p();
            record.put("e_"+evaluate_records.getId(),evaluate_records.getScore_p());
        }
        record.put("avg_group",String.format("%.2f",totalScore/evaluateIds.length));
        remarkData.add(record);

        Record record1 = custom.clone();
        record1.put("no","");
        record1.put("indexname","专家1");
        remarkData.add(record1);
        //按专家分组
        strSql = "SELECT '' as no,masterrolename as indexname,(sum(weights)/12)  ::numeric(8,0) as weights, ";
        strSql += strSchoolSql;
        strSql +=" FROM evaluate_remark inner join monitor_index_view on monitor_index_view.id = evaluate_remark.indexid \n"+
                " where deptname like'评估专家组%' and evaluateid in (@evaIds) \n"+
                " group by masterrolename order by masterrolename";
        sql = Sqls.create(strSql).setParam("evaIds",evaluateIds);
        List<Record> special = list(sql);

        remarkData.addAll(special);

        re.put("rowdata", remarkData);
        re.put("col_define", cols);
        return re;

    }
    public List<Record> getSchoolReport_Basic(String Taskname){
        //基础性指标
        String strSql="SELECT evaluateid,";
        String strIndexSql="";
        for(int i=1;i<10;i++){//9个基础性二级指标
            strIndexSql+=" sum(case when monitor_catalog.location="+i+" then evaluate_remark.score_p else 0 end) as index_"+i+",";
        }
        strIndexSql += " COALESCE(sum(evaluate_remark.score_p),0) as indexsum,\n"+
                " sum(case when sys_role.code='special2' then evaluate_remark.score_p else 0 end) as special2," +
                " sum(case when sys_role.code='special3' then evaluate_remark.score_p else 0 end) as special3," +
                " sum(case when sys_role.code='special4' then evaluate_remark.score_p else 0 end) as special4 \n";
        strSql += strIndexSql;
        //分组总分

        strSql += " FROM evaluate_records inner join evaluate_remark on evaluate_records.id=evaluate_remark.evaluateid\n"+
                " inner join monitor_index on monitor_index.id = evaluate_remark.indexid\n" +
                " inner join monitor_catalog on monitor_index.catalogid = monitor_catalog.id\n" +
                " LEFT JOIN sys_role ON monitor_index.masterrole = sys_role.id\n"+
                " where taskname = @taskname "+
                " group by evaluateid ,evaluate_records.score_p order by evaluate_records.score_p desc";

        Sql sql = Sqls.create(strSql).setParam("taskname",Taskname);
        List<Record> remarkData = list(sql);
        return remarkData;
    }
    public List<Record> getSchoolReport_Develop(String Taskname){
        //发展性指标
        String strSql="SELECT evaluateid,sys_unit.name,COALESCE(sum(evaluate_custom.score_p),0) as customsum,"+
                " evaluate_records.score_p as sum";

        strSql += " FROM evaluate_records inner join evaluate_custom on evaluate_records.id=evaluate_custom.evaluateid\n"+
                " inner join sys_unit on sys_unit.id = schoolid\n" +
                " where evaluate_records.taskname = @taskname "+
                " group by evaluateid ,evaluate_records.score_p,sys_unit.name order by evaluate_records.score_p desc";

        Sql sql = Sqls.create(strSql).setParam("taskname",Taskname);
        List<Record> remarkData = list(sql);
        return remarkData;
    }
    /**
     * 得到基础性指标的平均分
     * @return
     */
    public List<Record> getStatReport_Basic(String taskname){
        //基础性指标
        List<Record> remarkData =new ArrayList<>();
        String strIndexSql="";
        for(int i=1;i<10;i++){//9个基础性二级指标
            strIndexSql+=" (sum(case when monitor_catalog.location="+i+" then evaluate_remark.score_p else 0 end)/count(distinct evaluateid)):: numeric(5,2)  as index_"+i+",";

        }
        strIndexSql += " (COALESCE(sum(evaluate_remark.score_p),0)/count(distinct evaluateid)):: numeric(5,2) as indexsum,\n"+
                " (sum(case when sys_role.code='special2' then evaluate_remark.score_p else 0 end)/count(distinct evaluateid)):: numeric(5,2) as special2," +
                " (sum(case when sys_role.code='special3' then evaluate_remark.score_p else 0 end)/count(distinct evaluateid)):: numeric(5,2) as special3," +
                " (sum(case when sys_role.code='special4' then evaluate_remark.score_p else 0 end)/count(distinct evaluateid)):: numeric(5,2) as special4 \n" +
                 " FROM evaluate_records inner join evaluate_remark on evaluate_records.id=evaluate_remark.evaluateid\n"+
                " inner join monitor_index on monitor_index.id = evaluate_remark.indexid\n" +
                " inner join monitor_catalog on monitor_index.catalogid = monitor_catalog.id\n" +
                " LEFT JOIN sys_role ON monitor_index.masterrole = sys_role.id\n";
        String strSql ="";
        //没给评估任务参数
        if(StringUtils.isBlank(taskname)) {
            strSql = "SELECT '全区平均分' as name," + strIndexSql;
            Sql sql = Sqls.create(strSql);
            sql.setCallback(Sqls.callback.record());
            this.dao().execute(sql);
            //全区平均分
            Record stat = sql.getObject(Record.class);
            remarkData.add(stat);

            strSql = "SELECT taskname, taskname ||'平均分' as name," + strIndexSql + " Group by taskname order by case taskname\n" +
                    "  when '第一组评估' then 1\n" +
                    "  when '第二组评估' then 2\n" +
                    "  when '第三组评估' then 3\n" +
                    "  when '第四组评估' then 4\n" +
                    "  when '第五组评估' then 5\n" +
                    "  when '第六组评估' then 6\n" +
                    "  end ";
            sql = Sqls.create(strSql);
            //加入分组平均分
            remarkData.addAll(list(sql));
        }else{
            //给定评估任务参数
            strSql = "SELECT taskname, taskname ||'平均分' as name," + strIndexSql + " where taskname=@taskname group by taskname";
            Sql sql = Sqls.create(strSql).setParam("taskname",taskname);
            //加入分组平均分
            remarkData.addAll(list(sql));
        }
        return remarkData;
    }
    /**
     * 得到发展性指标的平均分
     * @return
     */
    public List<Record> getStatReport_Develop(String taskname){
        //发展性指标
        List<Record> customData =new ArrayList<>();
        String strIndexSql=" (COALESCE(sum(evaluate_custom.score_p),0)/count(distinct evaluateid)):: numeric(5,2) as customsum"+
                " FROM evaluate_records inner join evaluate_custom on evaluate_records.id=evaluate_custom.evaluateid \n";
        String strSql ="";
        //没给评估任务参数
        if(StringUtils.isBlank(taskname)) {

            strSql = "SELECT '全区平均分' as name," + strIndexSql;
            Sql sql = Sqls.create(strSql);
            sql.setCallback(Sqls.callback.record());
            this.dao().execute(sql);
            //全区平均分
            Record stat = sql.getObject(Record.class);
            customData.add(stat);
            strSql = "SELECT evaluate_records.taskname,evaluate_records.taskname ||'平均分' as name," + strIndexSql + " Group by evaluate_records.taskname order by case evaluate_records.taskname\n" +
                    "  when '第一组评估' then 1\n" +
                    "  when '第二组评估' then 2\n" +
                    "  when '第三组评估' then 3\n" +
                    "  when '第四组评估' then 4\n" +
                    "  when '第五组评估' then 5\n" +
                    "  when '第六组评估' then 6\n" +
                    "  end ";
            sql = Sqls.create(strSql);
            //加入分组平均分
            customData.addAll(list(sql));
        }else{
            strSql = "SELECT evaluate_records.taskname,evaluate_records.taskname ||'平均分' as name," + strIndexSql + " where evaluate_records.taskname=@taskname group by evaluate_records.taskname";
            Sql sql = Sqls.create(strSql).setParam("taskname",taskname);
            //加入分组平均分
            customData.addAll(list(sql));
        }
        return customData;
    }

    /**
     * 按3级指标获取学校的评估得分和评估意见
     * @param Taskname
     * @return
     */
    public List<Record> getSchoolReport_Basic3(String Taskname){
        //基础性指标
        String strSql="SELECT evaluateid,";
        String strIndexSql="";
        for(int i=1;i<=25;i++){//25个基础性三级指标
            strIndexSql+=" sum(case when monitor_index.location="+i+" then evaluate_remark.score_p else 0 end) as index_"+i
                    +",string_agg(case when monitor_index.location="+i+" then COALESCE(remark_p,'')||COALESCE(disadvantage,'')  else '' end,'') as remark_"+i+",";
        }
        strIndexSql += " COALESCE(sum(evaluate_remark.score_p),0) as indexsum\n";

        strSql += strIndexSql;
        //分组总分

        strSql += " FROM evaluate_records inner join evaluate_remark on evaluate_records.id=evaluate_remark.evaluateid\n"+
                " inner join monitor_index on monitor_index.id = evaluate_remark.indexid\n" +
                " where taskname = @taskname "+
                " group by evaluateid ,evaluate_records.score_p order by evaluate_records.score_p desc";

        Sql sql = Sqls.create(strSql).setParam("taskname",Taskname);
        List<Record> remarkData = list(sql);
        return remarkData;
    }

    /**
     * 获取学校发展性指标评估得分和评估意见
     * @param Taskname
     * @return
     */
    public List<Record> getSchoolReport_Develop3(String Taskname){
        //发展性指标
        String strSql="SELECT evaluateid,sys_unit.name,COALESCE(sum(evaluate_custom.score_p),0) as customsum,"+
                " string_agg(disadvantage ,'') as customremark,evaluate_records.score_p as sum";

        strSql += " FROM evaluate_records inner join evaluate_custom on evaluate_records.id=evaluate_custom.evaluateid\n"+
                " inner join sys_unit on sys_unit.id = schoolid\n" +
                " where evaluate_records.taskname = @taskname "+
                " group by evaluateid ,evaluate_records.score_p,sys_unit.name order by evaluate_records.score_p desc";

        Sql sql = Sqls.create(strSql).setParam("taskname",Taskname);
        List<Record> remarkData = list(sql);
        return remarkData;
    }
}

