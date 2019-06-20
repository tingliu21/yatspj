package cn.wizzer.modules.controllers.platform.sys;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.modules.services.sys.SysAppendixService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;

import javax.servlet.http.HttpServletRequest;

@IocBean
@At("/platform/sys/appendix")
@Filters({@By(type = PrivateFilter.class)})
public class SysAppendixController {
	private static final Log log = Logs.get();
	@Inject
	private SysAppendixService sysAppendixService;

	@At({"/delete","/delete/?"})
	@Ok("json")
	@SLog(tag = "删除Sys_appendix", msg = "ID:")
	public Object delete(String id,HttpServletRequest req) {
		try {
			sysAppendixService.delete(id);
			req.setAttribute("id", id);

			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}

	}


}
