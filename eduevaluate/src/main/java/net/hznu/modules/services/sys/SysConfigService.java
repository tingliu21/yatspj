package net.hznu.modules.services.sys;

import net.hznu.modules.models.sys.Sys_config;
import net.hznu.common.base.Service;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * Created by wizzer on 2016/6/22.
 */
@IocBean(args = {"refer:dao"})
public class SysConfigService extends Service<Sys_config> {
    public SysConfigService(Dao dao) {
        super(dao);
    }

}
