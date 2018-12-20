package cn.wizzer.modules.controllers.platform.evaluate;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.evaluate.Evaluate_summary;
import cn.wizzer.modules.services.evaluate.EvaluateSummaryService;
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
@At("/platform/evaluate/summary")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateSummaryController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateSummaryService evaluateSummaryService;

	@At("")
	@Ok("beetl:/platform/evaluate/summary/index.html")
	@RequiresAuthentication
	public void index() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return evaluateSummaryService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/evaluate/summary/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建Evaluate_summary", msg = "")
    public Object addDo(@Param("..") Evaluate_summary evaluateSummary, HttpServletRequest req) {
		try {
			evaluateSummaryService.insert(evaluateSummary);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/evaluate/summary/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return evaluateSummaryService.fetch(id);
    }

	//修改自评概述
    @At
    @Ok("json")
    @SLog(tag = "修改Evaluate_summary", msg = "ID:${args[0].id}")
    public Object summaryDo(@Param("..") Evaluate_summary evaluateSummary, HttpServletRequest req) {
		try {

			evaluateSummary.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateSummaryService.updateIgnoreNull(evaluateSummary);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Evaluate_summary", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				evaluateSummaryService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				evaluateSummaryService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/evaluate/summary/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return evaluateSummaryService.fetch(id);

		}
		return null;
    }

}
