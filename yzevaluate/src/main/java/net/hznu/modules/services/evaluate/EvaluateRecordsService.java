package net.hznu.modules.services.evaluate;

import net.hznu.common.base.Service;
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
import java.util.List;

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

    public Sys_role getRoleInEvaluate(String suid, String eid){
        Sql sql = Sqls.create("SELECT Sys_role.* FROM Sys_Role inner join evaluate_special_role on evaluate_special_role.roleid = Sys_role.id WHERE evaluateid=@evaluateid and specialid=@userid").setParam("evaluateid",eid).setParam("userid",suid);
        Entity<Sys_role> entity = dao().getEntity(Sys_role.class);
        sql.setEntity(entity);
        sql.setCallback(Sqls.callback.entities());
        dao().execute(sql);
        return sql.getObject(Sys_role.class);
    }
}

