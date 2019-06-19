package cn.wizzer.modules.controllers.platform.evaluate;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.evaluate.Evaluate_appendix;
import cn.wizzer.modules.services.evaluate.EvaluateAppendixService;
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
@At("/platform/evaluate/appendix")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateAppendixController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateAppendixService evaluateAppendixService;

	@At({"/delete","/delete/?"})
	@Ok("json")
	@SLog(tag = "删除Evaluate_appendix", msg = "ID:")
	public Object delete(String id,HttpServletRequest req) {
		try {
			evaluateAppendixService.delete(id);
			req.setAttribute("id", id);

			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}

	}


}
