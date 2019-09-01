package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.evaluate.Evaluate_special;
import net.hznu.modules.services.evaluate.EvaluateSpecialService;
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
@At("/platform/evaluate/special")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateSpecialController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateSpecialService evaluateSpecialService;

	@At("")
	@Ok("beetl:/platform/evaluate/special/index.html")
	@RequiresAuthentication
	public void index() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return evaluateSpecialService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/evaluate/special/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建Evaluate_special", msg = "")
    public Object addDo(@Param("..") Evaluate_special evaluateSpecial, HttpServletRequest req) {
		try {
			evaluateSpecialService.insert(evaluateSpecial);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/evaluate/special/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return evaluateSpecialService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Evaluate_special", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Evaluate_special evaluateSpecial, HttpServletRequest req) {
		try {

			evaluateSpecial.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateSpecialService.updateIgnoreNull(evaluateSpecial);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Evaluate_special", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				evaluateSpecialService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				evaluateSpecialService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/evaluate/special/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return evaluateSpecialService.fetch(id);

		}
		return null;
    }

}
