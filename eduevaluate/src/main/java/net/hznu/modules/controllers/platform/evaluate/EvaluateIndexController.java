package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.evaluate.Evaluate_index;
import net.hznu.modules.services.evaluate.EvaluateIndexService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/evaluate/index")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateIndexController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateIndexService evaluateIndexService;



	@At("/evaluate_county")
	@Ok("beetl:/platform/evaluate/evaluate_county.html")
	@RequiresAuthentication
	public void evaluate_county(HttpServletRequest req) {

		req.setAttribute("xzqh","330102");
	}

}
