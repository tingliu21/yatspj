package net.hznu.modules.services.sys;

import net.hznu.common.base.Service;
import net.hznu.modules.models.sys.Sys_plugin;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(args = {"refer:dao"})
public class SysPluginService extends Service<Sys_plugin> {
    private static final Log log = Logs.get();

    public SysPluginService(Dao dao) {
        super(dao);
    }
}

