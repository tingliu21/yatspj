package cn.wizzer.modules.services.evaluate;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.evaluate.Evaluate_special;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
@IocBean(args = {"refer:dao"})
public class EvaluateSpecialService extends Service<Evaluate_special> {
	private static final Log log = Logs.get();

    public EvaluateSpecialService(Dao dao) {
    	super(dao);
    }


}

