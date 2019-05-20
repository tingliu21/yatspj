package cn.wizzer.modules.services.evaluate;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.evaluate.Evaluate_opinion;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
@IocBean(args = {"refer:dao"})
public class EvaluateOpinionService extends Service<Evaluate_opinion> {
	private static final Log log = Logs.get();

    public EvaluateOpinionService(Dao dao) {
    	super(dao);
    }
}

