package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.base.Globals;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.evaluate.Evaluate_records;
import net.hznu.modules.models.sys.Sys_role;
import net.hznu.modules.models.sys.Sys_user;
import net.hznu.modules.services.evaluate.EvaluateCustomService;
import net.hznu.modules.services.evaluate.EvaluateRecordsService;
import net.hznu.modules.services.evaluate.EvaluateRemarkService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
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
@At("/platform/evaluate/special")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateSpecialController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateRecordsService evaluateRecordsService;
	@Inject
	private EvaluateRemarkService evaluateRemarkService;
	@Inject
	private EvaluateCustomService evaluateCustomService;

	@At("")
	@Ok("re")
	@RequiresPermissions("evaluate.verify.special")
	public String index(@Param("evaluateId") String evaluateId,@Param("unitType") String unitType,HttpServletRequest req) {
		if (!Strings.isBlank(evaluateId) && !"0".equals(evaluateId)) {
			Evaluate_records evaluate = evaluateRecordsService.fetch(evaluateId);
			evaluate = evaluateRecordsService.fetchLinks(evaluate,"school");
			req.setAttribute("schoolname",evaluate.getSchool().getName());
			req.setAttribute("evaluateId",evaluateId);
			req.setAttribute("unitType",unitType);

			//获取专家id
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();
			Sys_role role = evaluateRecordsService.getRoleInEvaluate(user.getId(),evaluateId);
			if(role!=null) {
				if (role.getCode().equalsIgnoreCase(Globals.CustomRole)) {
					return "beetl:/platform/evaluate/special/index_develop.html";
				} else {
					return "beetl:/platform/evaluate/special/index_basic.html";
				}
			}
		}return "beetl:/platform/evaluate/special/index_basic.html";

	}

	//专家只列出自己负责的指标
	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object basicdata(@Param("evaluateId") String evaluateId, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns){

		if (!Strings.isBlank(evaluateId) && !"0".equals(evaluateId)) {
			//获取专家id
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();

			Cnd cnd = Cnd.where("specialid ","=",user.getId());
			cnd.and("evaluateId", "like", "%" + evaluateId + "%").asc("location");

			return evaluateRemarkService.data(length, start, draw, order, columns, cnd, "");
		}
		return null;
	}
	//专家只列出自己负责的指标
	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object developdata(@Param("evaluateId") String evaluateId, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns){

		if (!Strings.isBlank(evaluateId) && !"0".equals(evaluateId)) {
			//获取专家id
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();

			Cnd cnd = Cnd.where("specialid","=",user.getId());
			cnd.and("evaluateId", "=",   evaluateId ).asc("location");

			return evaluateCustomService.data(length, start, draw, order, columns, cnd, "");
		}
		return null;
	}

}
