package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.modules.services.evaluate.EvaluateAppendixService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
