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

	@At("")
	@Ok("beetl:/platform/evaluate/appendix/index.html")
	@RequiresAuthentication
	public void index() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("indexId") String indexId,@Param("evaluateId") String evaluateId,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {

		Cnd cnd = Cnd.NEW();

		if (!Strings.isBlank(evaluateId) && !"0".equals(evaluateId)) {
			cnd.and("evaluateId", "like", "%" + evaluateId + "%").and("indexId", "like", "%" + indexId + "%");

			return evaluateAppendixService.data(length, start, draw, order, columns, cnd, null);
		}
		return  null;
    }

    @At
    @Ok("beetl:/platform/evaluate/appendix/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建Evaluate_appendix", msg = "")
    public Object addDo(@Param("..") Evaluate_appendix evaluateAppendix, HttpServletRequest req) {
		try {
			evaluateAppendixService.insert(evaluateAppendix);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/evaluate/appendix/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return evaluateAppendixService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Evaluate_appendix", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Evaluate_appendix evaluateAppendix, HttpServletRequest req) {
		try {

			evaluateAppendix.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateAppendixService.updateIgnoreNull(evaluateAppendix);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Evaluate_appendix", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				evaluateAppendixService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				evaluateAppendixService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/evaluate/appendix/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return evaluateAppendixService.fetch(id);

		}
		return null;
    }

}
