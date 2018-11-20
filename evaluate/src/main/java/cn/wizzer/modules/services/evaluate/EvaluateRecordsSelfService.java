package cn.wizzer.modules.services.evaluate;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.evaluate.Evaluate_records_self;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
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
}

