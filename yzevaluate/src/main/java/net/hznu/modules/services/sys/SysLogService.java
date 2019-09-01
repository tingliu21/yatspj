package net.hznu.modules.services.sys;

import net.hznu.common.base.Service;
import net.hznu.modules.models.sys.Sys_log;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * Created by wizzer on 2016/6/29.
 */
@IocBean(args = {"refer:dao"})
public class SysLogService extends Service<Sys_log> {
    public SysLogService(Dao dao) {
        super(dao);
    }
}
