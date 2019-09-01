package net.hznu.modules.services.basicdata;

import net.hznu.common.base.Service;
import net.hznu.modules.models.basicdata.Bascidata_teacherlist;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
@IocBean(args = {"refer:dao"})
public class BascidataTeacherlistService extends Service<Bascidata_teacherlist> {
	private static final Log log = Logs.get();

    public BascidataTeacherlistService(Dao dao) {
    	super(dao);
    }
}

