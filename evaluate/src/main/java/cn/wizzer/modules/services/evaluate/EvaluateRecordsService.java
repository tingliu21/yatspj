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
     *
     * @param evaluateId
     */
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(String evaluateId) {
        //dao().execute(Sqls.create("delete from evaluate_records where path like @path").setParam("path", catalog.getPath() + "%"));
        //清空该评估记录下的所有观测点评分
        dao().execute(Sqls.create("delete from evaluate_qualify where evaluateid=@id ").setParam("id", evaluateId));
        dao().execute(Sqls.create("delete from evaluate_remark where evaluateid=@id ").setParam("id", evaluateId));
        dao().execute(Sqls.create("delete from evaluate_summary where evaluateid=@id")).setParam("id",evaluateId);
        //清空该评估记录
        delete(evaluateId);


    }

    public double getProgress_s(String evaluateId){
        Sql sql = Sqls.create("select count(indexid) from evaluate_remark where evaluateid = @eid").setParam("eid", evaluateId);
        int iTotalIndex_q = count(Sqls.create("select count(indexid) from evaluate_qualify where evaluateid = @eid").setParam("eid", evaluateId));
        int iTotalIndex_r = count(Sqls.create("select count(indexid) from evaluate_remark where evaluateid = @eid").setParam("eid", evaluateId));
        int iQualifyIndex = count(Sqls.create("select count(indexid) from evaluate_qualify where evaluateid = @eid and selfeva=@eva").setParam("eid", evaluateId).setParam("eva",true));
        int iRemarkIndex = count(Sqls.create("select count(indexid) from evaluate_remark where evaluateid = @eid and selfeva=@eva").setParam("eid", evaluateId).setParam("eva",true));

        return (double)(iQualifyIndex+iRemarkIndex)/(iTotalIndex_q+iTotalIndex_r);
    }

    public double getProgress_p(String evaluateId){
        Sql sql = Sqls.create("select count(indexid) from evaluate_remark where evaluateid = @eid").setParam("eid", evaluateId);
        int iTotalIndex_q = count(Sqls.create("select count(indexid) from evaluate_qualify where evaluateid = @eid").setParam("eid", evaluateId));
        int iTotalIndex_r = count(Sqls.create("select count(indexid) from evaluate_remark where evaluateid = @eid").setParam("eid", evaluateId));
        int iQualifyIndex = count(Sqls.create("select count(indexid) from evaluate_qualify where evaluateid = @eid and verifyeva=@eva").setParam("eid", evaluateId).setParam("eva",true));
        int iRemarkIndex = count(Sqls.create("select count(indexid) from evaluate_remark where evaluateid = @eid and verifyeva=@eva").setParam("eid", evaluateId).setParam("eva",true));

        return (double)(iQualifyIndex+iRemarkIndex)/(iTotalIndex_q+iTotalIndex_r);
    }
    public NutMap getSpecialUser(){
        NutMap re = new NutMap();
        List<Record> records = list(Sqls.create("SELECT sys_user.id, loginname,nickname FROM sys_user inner join sys_unit on sys_unit.id = sys_user.unitid WHERE aliasname='Special' "));
        re.put("data", records);
        re.put("recordsTotal", records.size());
        return re;

    }

}

