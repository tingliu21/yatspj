package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.evaluate.Evaluate_opinion;
import net.hznu.modules.models.sys.Sys_user;
import net.hznu.modules.services.evaluate.EvaluateOpinionService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
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
@At("/platform/evaluate/opinion")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateOpinionController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateOpinionService evaluateOpinionService;

	@At("")
	@Ok("beetl:/platform/evaluate/opinion/index.html")
	@RequiresAuthentication
	public void index() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return evaluateOpinionService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/evaluate/opinion/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建Evaluate_opinion", msg = "")
    public Object addDo(@Param("..") Evaluate_opinion evaluateOpinion, HttpServletRequest req) {
		try {
			evaluateOpinionService.insert(evaluateOpinion);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

	@At("/edit/?")
    @Ok("beetl:/platform/evaluate/opinion/edit.html")
    @RequiresAuthentication
    public Object edit(String evaluateId) {
		Evaluate_opinion evaluateOpinion = null;
		if (!Strings.isBlank(evaluateId)) {
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();
			evaluateOpinion = evaluateOpinionService.fetch(Cnd.where("evaluateid","=",evaluateId).and("specialid","=",user.getId()));
			if(evaluateOpinion==null){
				evaluateOpinion = new Evaluate_opinion();
				evaluateOpinion.setEvaluateId(evaluateId);
			}
		}
		return  evaluateOpinion;
    }

    @At
    @Ok("json")
    @SLog(tag = "评估意见综述", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Evaluate_opinion evaluateOpinion, HttpServletRequest req) {
		try {
			evaluateOpinion.setSpecialId(Strings.sNull(req.getAttribute("uid")));
			evaluateOpinion.setOpBy(Strings.sNull(req.getAttribute("uid")));
			evaluateOpinion.setOpAt((int) (System.currentTimeMillis() / 1000));
			if(Strings.isBlank(evaluateOpinion.getId())){
				evaluateOpinionService.insert(evaluateOpinion);
			}else{
				evaluateOpinionService.updateIgnoreNull(evaluateOpinion);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Evaluate_opinion", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				evaluateOpinionService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				evaluateOpinionService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/evaluate/opinion/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return evaluateOpinionService.fetch(id);

		}
		return null;
    }

}
