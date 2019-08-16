package net.hznu.modules.services.evaluate;

import net.hznu.common.base.Service;
import net.hznu.modules.models.evaluate.Evaluate_index;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
@IocBean(args = {"refer:dao"})
public class EvaluateIndexService extends Service<Evaluate_index> {
	private static final Log log = Logs.get();

    public EvaluateIndexService(Dao dao) {
    	super(dao);
    }
}

