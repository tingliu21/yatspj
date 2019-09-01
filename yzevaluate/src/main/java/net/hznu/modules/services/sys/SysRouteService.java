package net.hznu.modules.services.sys;

import net.hznu.common.base.Service;
import net.hznu.modules.models.sys.Sys_route;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * Created by Wizzer on 2016/7/31.
 */
@IocBean(args = {"refer:dao"})
public class SysRouteService extends Service<Sys_route> {
    private static final Log log = Logs.get();

    public SysRouteService(Dao dao) {
        super(dao);
    }
}
