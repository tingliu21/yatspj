package net.hznu.modules.controllers.platform.basicdata;

import net.hznu.common.filter.PrivateFilter;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

@IocBean
@At("/platform/basicdata/dataview")
@Filters({@By(type = PrivateFilter.class)})
public class BasicdataDataviewController {
    private static final Log log = Logs.get();

    @At("")
    @Ok("beetl:/platform/basicdata/dataview/index.html")
    @RequiresAuthentication
    public void index() {

    }
}
