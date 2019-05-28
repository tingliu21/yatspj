package cn.wizzer.modules.services.evaluate;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.evaluate.Evaluate_remark;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
@IocBean(args = {"refer:dao"})
public class EvaluateRemarkService extends Service<Evaluate_remark> {
	private static final Log log = Logs.get();

    public EvaluateRemarkService(Dao dao) {
    	super(dao);
    }

    //获得评估的自评总得分
    public double getTotalScore_s(String evaluateId){
        Sql sql = Sqls.create("select sum(score_s) from evaluate_remark where evaluateid = @eid").setParam("eid", evaluateId);
        return score(sql);
    }
    //获得评估的审核评估总得分
    public double getTotalScore_p(String evaluateId){
        Sql sql = Sqls.create("select sum(score_p) from evaluate_remark where evaluateid = @eid").setParam("eid", evaluateId);
        return score(sql);
    }

}

