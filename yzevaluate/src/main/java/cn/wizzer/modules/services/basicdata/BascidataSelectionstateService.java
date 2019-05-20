package cn.wizzer.modules.services.basicdata;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.basicdata.Bascidata_selectionstate;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
@IocBean(args = {"refer:dao"})
public class BascidataSelectionstateService extends Service<Bascidata_selectionstate> {
	private static final Log log = Logs.get();

    public BascidataSelectionstateService(Dao dao) {
    	super(dao);
    }
}

