package cn.wizzer.modules.services.evaluate;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.evaluate.Evaluate_records;
import cn.wizzer.modules.models.monitor.Monitor_index;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

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
        dao().execute(Sqls.create("delete from evaluate_summary where evaluateid=@id")).setParam("id",evaluateId);
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
        int iRemarkIndex = count(Sqls.create("select count(indexid) from evaluate_remark where evaluateid = @eid and selfeva=@eva").setParam("eid", evaluateId).setParam("eva",true));

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

}

