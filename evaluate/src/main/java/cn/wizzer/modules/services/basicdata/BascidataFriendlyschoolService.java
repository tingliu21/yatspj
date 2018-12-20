package cn.wizzer.modules.services.basicdata;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.basicdata.Bascidata_friendlyschool;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
@IocBean(args = {"refer:dao"})
public class BascidataFriendlyschoolService extends Service<Bascidata_friendlyschool> {
	private static final Log log = Logs.get();

    public BascidataFriendlyschoolService(Dao dao) {
    	super(dao);
    }
}

