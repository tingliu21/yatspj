package cn.wizzer.modules.controllers.platform.evaluate;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.evaluate.Evaluate_remark;
import cn.wizzer.modules.services.evaluate.EvaluateRemarkService;
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
@At("/platform/evaluate/remark")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateRemarkController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateRemarkService evaluateRemarkService;

	@At("/?")
	@Ok("beetl:/platform/evaluate/remark/index.html")
	@RequiresAuthentication
	//type取值有2个，self和special
	public void index(String type,@Param("evaluateId") String evaluateId,@Param("unitType") String unitType,HttpServletRequest req) {
		req.setAttribute("evaluateId",evaluateId);
		req.setAttribute("unitType",unitType);
		req.setAttribute("type", type);
	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("catalogId") String catalogId,@Param("evaluateId") String evaluateId,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {

		Cnd cnd = Cnd.NEW();
		if (!Strings.isBlank(evaluateId) && !"0".equals(evaluateId)) {
			cnd.and("evaluateId", "like", "%" + evaluateId + "%").and("catalogId", "like", "%" + catalogId + "%");;
			return evaluateRemarkService.data(length, start, draw, order, columns, cnd, "index");
		}
		return null;

    }
	//暂不新增，通过初始化新增
    @At
    @Ok("beetl:/platform/evaluate/remark/add.html")
    @RequiresAuthentication
    public void add() {

    }

	//暂不新增，通过初始化新增
    @At
    @Ok("json")
    @SLog(tag = "新建Evaluate_remark", msg = "")
    public Object addDo(@Param("..") Evaluate_remark evaluateRemark, HttpServletRequest req) {
		try {
			evaluateRemarkService.insert(evaluateRemark);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }
	//自评
    @At("/edit/?")
    @Ok("beetl:/platform/evaluate/remark/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		Evaluate_remark remark = evaluateRemarkService.fetch(id);
		return evaluateRemarkService.fetchLinks(remark,"index");
    }

	//自评
    @At
    @Ok("json")
    @SLog(tag = "修改Evaluate_remark", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Evaluate_remark evaluateRemark, HttpServletRequest req) {
		try {

			evaluateRemark.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateRemarkService.updateIgnoreNull(evaluateRemark);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

	//部门审核、专家审核
	@At("/edit/?")
	@Ok("beetl:/platform/evaluate/remark/edit.html")
	@RequiresAuthentication
	public Object edit_verify(String id) {
		Evaluate_remark remark = evaluateRemarkService.fetch(id);
		return evaluateRemarkService.fetchLinks(remark,"index");
	}

	//部门审核、专家审核
	@At
	@Ok("json")
	@SLog(tag = "修改Evaluate_remark", msg = "ID:${args[0].id}")
	public Object editDo_verify(@Param("..") Evaluate_remark evaluateRemark, HttpServletRequest req) {
		try {

			evaluateRemark.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateRemarkService.updateIgnoreNull(evaluateRemark);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}

	//暂不删除
    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Evaluate_remark", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				evaluateRemarkService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				evaluateRemarkService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/evaluate/remark/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return evaluateRemarkService.fetch(id);

		}
		return null;
    }

}
