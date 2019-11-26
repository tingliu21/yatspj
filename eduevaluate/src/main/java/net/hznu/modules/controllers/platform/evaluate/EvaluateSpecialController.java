package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.evaluate.Evaluate_records;
import net.hznu.modules.models.evaluate.Evaluate_special;
import net.hznu.modules.models.sys.Sys_unit;
import net.hznu.modules.models.sys.Sys_user;
import net.hznu.modules.services.evaluate.EvaluateRecordsService;
import net.hznu.modules.services.evaluate.EvaluateSpecialService;
import net.hznu.modules.services.sys.SysUnitService;
import net.hznu.modules.services.sys.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
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
	@Inject
	private SysUserService sysUserService;
	@Inject
	private EvaluateRecordsService evaluateRecordsService;
	@Inject
	private SysUnitService sysUnitService;

	@At("")
	@Ok("beetl:/platform/evaluate/special/index.html")
	@RequiresAuthentication
	public void index(HttpServletRequest req) {


	}

	@At
	@Ok("json")
	@RequiresAuthentication
	public Object data(@Param("unitid") String unitid, @Param("loginname") String loginname, @Param("nickname") String nickname, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
		/*if (!Strings.isBlank(unitid) && !"root".equals(unitid))
			cnd.and("unitid", "=", unitid);*/
			Sys_unit unit = sysUnitService.fetch(Cnd.where("aliasname","=","Special"));
			cnd.and("unitid", "=", unit.getId());
			if (!Strings.isBlank(loginname))
				cnd.and("loginname", "like", "%" + loginname + "%");
			if (!Strings.isBlank(nickname))
				cnd.and("nickname", "like", "%" + nickname + "%");
			return sysUserService.data(length, start, draw, order, columns, cnd, null);

	}

	/**
	 * ***************************开始专家分配*************************
	 * @param userId
	 * @param req
	 * @return
	 */
	@At("/assignReport/?")
	@Ok("beetl:/platform/evaluate/special/assignReport.html")
	@RequiresAuthentication
	public Object assignReport(String userId, HttpServletRequest req) {

		return sysUserService.fetch(userId);
	}

	@At("/assign")
	@Ok("beetl:/platform/evaluate/special/assign.html")
	@RequiresAuthentication
	public void assign() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object evaluateData(@Param("userid") String userid, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		String sql = "SELECT a.* FROM evaluate_record_view a,evaluate_special b WHERE a.id=b.evaluateid ";
		if (!Strings.isBlank(userid)) {
			sql += " and b.specialId='" + userid + "'";
		}

		String s = sql;
		if (order != null && order.size() > 0) {
			for (DataTableOrder o : order) {
				DataTableColumn col = columns.get(o.getColumn());
				s += " order by a." + Sqls.escapeSqlFieldValue(col.getData()).toString() + " " + o.getDir();
			}
		}
		return evaluateSpecialService.data(length, start, draw, Sqls.create(sql), Sqls.create(s));
	}
	@At
	@Ok("beetl:/platform/evaluate/special/selectEvaluate.html")
	@RequiresAuthentication
	public void selectEvaluate(HttpServletRequest req) {

	}
	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object selectData(@Param("userid") String userid, @Param("name") String name, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		String sql = "SELECT a.* FROM evaluate_record_view a WHERE 1=1 ";
		//已经分配过专家的评估记录不在列表中显示
		if (!Strings.isBlank(userid)) {
			sql += " and a.id NOT IN(SELECT b.evaluateid FROM evaluate_special b)";
		}
		if (!Strings.isBlank(name)) {
			sql += " and (a.xzqhmc like '%" + name + "%') ";
		}
		String s = sql;
		if (order != null && order.size() > 0) {
			for (DataTableOrder o : order) {
				DataTableColumn col = columns.get(o.getColumn());
				s += " order by a." + Sqls.escapeSqlFieldValue(col.getData()).toString() + " " + o.getDir();
			}
		}
		return evaluateSpecialService.data(length, start, draw, Sqls.create(sql), Sqls.create(s));
	}
	@At
	@Ok("json")
	@RequiresPermissions("sys.manager.role.user")
	@SLog(tag = "从专家评估列表中删除评估单位", msg = "专家名称:${args[2].getAttribute('nickname')},用户ID:${args[0]}")
	public Object delEvaluate(@Param("evaIds") String evaIds, @Param("userid") String userid, HttpServletRequest req) {
		try {
			String[] ids = StringUtils.split(evaIds, ",");

			//evaluateSpecialService.dao().clear("evaluate_special", Cnd.where("evaluateId", "in", ids).and("specialid", "=", userid));
			evaluateSpecialService.update(org.nutz.dao.Chain.make("specialId", null),Cnd.where("evaluateId","in",ids));
			Sys_user user = sysUserService.fetch(userid);
			req.setAttribute("name", user.getNickname());
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}

	@At
	@Ok("json")
	@RequiresPermissions("sys.manager.role.user")
	@SLog(tag = "添加评估单位到专家", msg = "专家名称:${args[2].getAttribute('nickname')},评估ID:${args[0]}")
	public Object pushEvaluate(@Param("evaIds") String evaIds, @Param("userid") String userid, HttpServletRequest req) {
		try {
			String[] ids = StringUtils.split(evaIds, ",");
			for (String s : ids) {
				if (!Strings.isEmpty(s)) {
					//原来在分配专家的时候才插入数据，现在改为数据导入的时候就有evaluate_special记录
//					evaluateSpecialService.insert("evaluate_special", org.nutz.dao.Chain.make("evaluateId", s).add("specialId", userid));
					evaluateSpecialService.update(org.nutz.dao.Chain.make("specialId", userid),Cnd.where("evaluateId","=",s));
				}
			}
			Sys_user user = sysUserService.fetch(userid);
			req.setAttribute("name", user.getNickname());
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}
	/**
	 * ***************************结束专家分配*************************
	 */


    @At
    @Ok("json")
    @SLog(tag = "修改评估报告", msg = "ID:${args[0].evaluateId}")
    public Object editDo(@Param("..") Evaluate_special evaluateSpecial, @Param("bSubmit") boolean bSubmit,HttpServletRequest req) {
		try {

			evaluateSpecial.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateSpecialService.updateIgnoreNull(evaluateSpecial);
			if(bSubmit){
				evaluateRecordsService.update(org.nutz.dao.Chain.make("status",true),Cnd.where("id","=",evaluateSpecial.getEvaluateId()));
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }
	@At
	@Ok("beetl:/platform/evaluate/special/reviews.html")
	@RequiresAuthentication
	public Object reviews(@Param("evaluateId") String evaluateId, HttpServletRequest req) {
		if (org.apache.commons.lang.StringUtils.isNotBlank(evaluateId)) {
			Evaluate_records record = evaluateRecordsService.fetch(evaluateId);
			req.setAttribute("evaluateId",evaluateId);
			req.setAttribute("xzqh", record.getUnitcode());
			req.setAttribute("xzqhmc",record.getXzqhmc());
		}
		return evaluateSpecialService.fetch(evaluateId);
	}

}
