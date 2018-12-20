package cn.wizzer.modules.controllers.platform.evaluate;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.evaluate.Evaluate_qualify;
import cn.wizzer.modules.models.evaluate.Evaluate_records;
import cn.wizzer.modules.models.evaluate.Evaluate_summary;
import cn.wizzer.modules.services.evaluate.EvaluateQualifyService;
import cn.wizzer.modules.services.evaluate.EvaluateRecordsService;
import cn.wizzer.modules.services.evaluate.EvaluateSummaryService;
import cn.wizzer.modules.services.monitor.MonitorIndexService;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
@At("/platform/evaluate/qualify")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateQualifyController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateQualifyService evaluateQualifyService;
	@Inject
	private MonitorIndexService monitorIndexService;
	@Inject
	private EvaluateRecordsService evaluateRecordsService;
	@Inject
	private EvaluateSummaryService evaluateSummaryService;

	@At({"","/?"})
	@Ok("beetl:/platform/evaluate/qualify/${req_attr.type}/index.html")
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
			cnd.and("evaluateId", "like", "%" + evaluateId + "%").and("catalogId", "like", "%" + catalogId + "%").asc("location");

			return evaluateQualifyService.data(length, start, draw, order, columns, cnd, "index");
		}
		return null;
    }
	//暂不新增，通过初始化新增
    @At
    @Ok("beetl:/platform/evaluate/qualify/add.html")
    @RequiresAuthentication
    public void add() {

    }
	//暂不新增，通过初始化新增
    @At
    @Ok("json")
    @SLog(tag = "新建Evaluate_qualify", msg = "")
    public Object addDo(@Param("..") Evaluate_qualify evaluateQualify, HttpServletRequest req) {
		try {
			evaluateQualifyService.insert(evaluateQualify);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/evaluate/qualify/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		Evaluate_qualify qualify = evaluateQualifyService.fetch(id);
		return evaluateQualifyService.fetchLinks(qualify,"index");

    }

    //学校自评页面
	@At("/selfeva/?")
	@Ok("beetl:/platform/evaluate/qualify/schooledit.html")
	@RequiresPermissions("evaluate.self.remark")
	public Object edit_self(String id) {
		Evaluate_qualify qualify = evaluateQualifyService.fetch(id);
		return evaluateQualifyService.fetchLinks(qualify,"index");

	}

	//学校自评结果提交
	@At("/selfevaDo")
	@Ok("json")
	@RequiresPermissions("evaluate.self.remark")
	@SLog(tag = "学校自评达标性指标", msg = "ID:${args[0].id}")
	public Object editDo_self(@Param("..") Evaluate_qualify evaluateQualify, HttpServletRequest req) {
		try {
			Evaluate_records records = evaluateRecordsService.fetch(evaluateQualify.getEvaluateId());
			if(records.isStatus_s()){
				return Result.error("已经提交审核，不能再修改评价了");
			}
			evaluateQualify.setOpBy(Strings.sNull(req.getAttribute("uid")));
			evaluateQualify.setOpAt((int) (System.currentTimeMillis() / 1000));
			//比较实际值与公式，确定是否达标

			String formula =monitorIndexService.fetch(evaluateQualify.getIndexId()).getFormula();
			Boolean qualify=evaluateQualifyService.isQualified(evaluateQualify.getPvalue(),formula);
			evaluateQualify.setQualify(qualify);
			evaluateQualify.setSelfeva(true);//该指标自评完成

			evaluateQualifyService.updateIgnoreNull(evaluateQualify);

			//修改records记录
			Evaluate_records evaluateRecords = evaluateRecordsService.fetch(evaluateQualify.getEvaluateId());
			double progress = evaluateRecordsService.getProgress_s(evaluateQualify.getEvaluateId());
			evaluateRecords.setProgress_s(progress);
//			if(progress==1.0){
//				evaluateRecords.setStatus_s(true);
//			}
			evaluateRecordsService.updateIgnoreNull(evaluateRecords);

			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}

	//学校自评概述页面
	@At("/summary")
	@Ok("beetl:/platform/evaluate/qualify/summary.html")
	@RequiresAuthentication
	public Object edit_selfsum(@Param("evaluateID") String evaluateID,@Param("catalogID") String catalogID,HttpServletRequest req) {
		List<Evaluate_summary> summary = evaluateSummaryService.query(Cnd.where("evaluateID", "=", evaluateID).and("catalogID", "=", catalogID));
		return summary.get(0);
	}


	//部门审核页面
	@At("/depteva/?")
	@Ok("beetl:/platform/evaluate/qualify/edit.html")
	@RequiresPermissions("evaluate.verify.dept")
	public Object edit_verify(String id) {
		Evaluate_qualify qualify = evaluateQualifyService.fetch(id);
		return evaluateQualifyService.fetchLinks(qualify,"index");

	}
	//部门审核结果提交
    @At("/deptevaDo")
    @Ok("json")
	@RequiresPermissions("evaluate.verify.dept")
    @SLog(tag = "部门审核评估达标性指标", msg = "ID:${args[0].id}")
    public Object editDo_verify(@Param("..") Evaluate_qualify evaluateQualify, HttpServletRequest req) {
		try {
			evaluateQualify.setOpBy(Strings.sNull(req.getAttribute("uid")));
			evaluateQualify.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateQualify.setVerifyeva(true);
			evaluateQualifyService.updateIgnoreNull(evaluateQualify);

			//修改records记录
			Evaluate_records evaluateRecords = evaluateRecordsService.fetch(evaluateQualify.getEvaluateId());
			double progress = evaluateRecordsService.getProgress_p(evaluateQualify.getEvaluateId());
			evaluateRecords.setProgress_p(progress);
			if(progress==1.0){
				evaluateRecords.setStatus_p(true);
			}
			evaluateRecordsService.updateIgnoreNull(evaluateRecords);

			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

	//暂不删除
    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Evaluate_qualify", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				evaluateQualifyService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				evaluateQualifyService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/evaluate/qualify/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return evaluateQualifyService.fetch(id);

		}
		return null;
    }

}
