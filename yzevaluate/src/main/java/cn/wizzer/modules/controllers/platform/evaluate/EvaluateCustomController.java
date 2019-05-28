package cn.wizzer.modules.controllers.platform.evaluate;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.evaluate.Evaluate_custom;
import cn.wizzer.modules.services.evaluate.EvaluateCustomService;
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
@At("/platform/evaluate/custom")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateCustomController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateCustomService evaluateCustomService;

	@At("")
	@Ok("beetl:/platform/evaluate/custom/index.html")
	@RequiresAuthentication
	public void index() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return evaluateCustomService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/evaluate/custom/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建Evaluate_custom", msg = "")
    public Object addDo(@Param("..") Evaluate_custom evaluateCustom, HttpServletRequest req) {
		try {
			evaluateCustomService.insert(evaluateCustom);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/evaluate/custom/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return evaluateCustomService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Evaluate_custom", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Evaluate_custom evaluateCustom, HttpServletRequest req) {
		try {

			evaluateCustom.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateCustomService.updateIgnoreNull(evaluateCustom);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Evaluate_custom", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				evaluateCustomService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				evaluateCustomService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/evaluate/custom/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return evaluateCustomService.fetch(id);

		}
		return null;
    }

}
