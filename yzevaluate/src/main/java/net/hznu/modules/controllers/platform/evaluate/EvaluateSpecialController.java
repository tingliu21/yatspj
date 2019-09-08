package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.filter.PrivateFilter;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;



@IocBean
@At("/platform/evaluate/special")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateSpecialController {
	private static final Log log = Logs.get();


	@At("")
	@Ok("beetl:/platform/evaluate/special/index.html")
	@RequiresAuthentication
	public void index() {

	}



}
