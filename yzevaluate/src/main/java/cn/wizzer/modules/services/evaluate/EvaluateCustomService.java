package cn.wizzer.modules.services.evaluate;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.evaluate.Evaluate_custom;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
@IocBean(args = {"refer:dao"})
public class EvaluateCustomService extends Service<Evaluate_custom> {
    private static final Log log = Logs.get();

    public EvaluateCustomService(Dao dao) {
        super(dao);
    }

    //    //获得评估的自评总得分
//    public double getTotalScore_s(String evaluateId){
//        Sql sql = Sqls.create("select sum(score_s) from evaluate_custom where evaluateid = @eid").setParam("eid", evaluateId);
//        return score(sql);
//    }
//    //获得评估的审核评估总得分
//    public double getTotalScore_p(String evaluateId){
//        Sql sql = Sqls.create("select sum(score_p) from evaluate_custom where evaluateid = @eid").setParam("eid", evaluateId);
//        return score(sql);
//    }
    public double getTotalWeights(String evaluateId, String customid) {
        Sql sql;
        if (!Strings.isBlank(customid) && !"0".equals(customid)) {

            sql = Sqls.create("select sum(weights) from evaluate_custom where evaluateid = @eid and id!=@id").setParam("eid", evaluateId).setParam("id", customid);

        } else {
            sql = Sqls.create("select sum(weights) from evaluate_custom where evaluateid = @eid").setParam("eid", evaluateId);
        }

        double weights = score(sql);

        return weights;
    }

    /**
     * 级联删除评估记录
     * 2018-12-23 改为通过数据库设置外键级联删除
     *
     * @param customId
     */
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(String customId) {
        dao().execute(Sqls.create("delete from evaluate_appendix where remarkid=@id ").setParam("id", customId));
        //清空该发展性指标记录
        delete(customId);
    }
}
