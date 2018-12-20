package cn.wizzer.modules.services.evaluate;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.evaluate.Evaluate_records_self;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import java.util.List;

@IocBean(args = {"refer:dao"})
public class EvaluateRecordsSelfService extends Service<Evaluate_records_self> {
	private static final Log log = Logs.get();

    public EvaluateRecordsSelfService(Dao dao) {
    	super(dao);
    }
    /**
     * 级联删除评估记录
     *
     * @param evaluateId
     */
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(String evaluateId) {
        //清空该评估记录
        delete(evaluateId);

        //dao().execute(Sqls.create("delete from evaluate_records where path like @path").setParam("path", catalog.getPath() + "%"));
        //清空该评估记录下的所有观测点评分
        dao().execute(Sqls.create("delete from evaluate_qualify where evaluateid=@id ").setParam("id", evaluateId));
        dao().execute(Sqls.create("delete from evaluate_remark where evaluateid=@id ").setParam("id", evaluateId));



    }
    /**
     * 提交评估记录
     *
     * @param evaluateId
     */
    @Aop(TransAop.READ_COMMITTED)
    public void submit(String evaluateId){

        dao().execute(Sqls.create("update evaluate_records set selfeva = true where evaluateid=@id ").setParam("id", evaluateId));
    }
    public List<Record> getUnitInfo(String evalId) {
        return list(Sqls.create("select t1.name as unitname, t1.address as address, t1.website as website, t1.telephone as telephone, t1.email as email from sys_unit t1 join evaluate_records t2 on t1.id=t2.schoolid where t2.id=@id").setParam("id", evalId));

    }

    public List<Record> getBasicEvalData(String evalId) {
        return list(Sqls.create("select t1.qualify as qualify, t2.location from evaluate_qualify t1 join monitor_index t2 on t1.indexid=t2.id where evaluateid=@id order by location").setParam("id", evalId));
    }

    public List<Record> getBasicSummaryData(String evalId) {
        return list(Sqls.create("select t1.summary, t2.location from evaluate_summary t1 join monitor_catalog t2 on t1.catalogid=t2.id where evaluateid=@id order by location").setParam("id", evalId));
    }

    public List<Record> getRemarkData(String evalId) {
        return list(Sqls.create("select t1.score_s, t1.remark_s, t1.score_p, t2.location from evaluate_remark t1 join monitor_index t2 on t1.indexid=t2.id where evaluateid=@id order by location").setParam("id", evalId));
    }

    public List<Record> getScaleData(String evalId) {
        return list(Sqls.create("select t1.grade,t1.planenrollnum,t1.actualenrollnum, t1.classnum,t1.averagenum,t1.instruction from bascidata_scale t1 join evaluate_records t2 on t1.schoolid=t2.schoolid and t1.year=t2.year where t2.id=@id").setParam("id", evalId));
    }

}

