package net.hznu.modules.services.basicdata;

import net.hznu.common.base.Service;
import net.hznu.modules.models.basicdata.Bascidata_teacher;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
@IocBean(args = {"refer:dao"})
public class BascidataTeacherService extends Service<Bascidata_teacher> {
	private static final Log log = Logs.get();

    public BascidataTeacherService(Dao dao) {
    	super(dao);
    }
}

