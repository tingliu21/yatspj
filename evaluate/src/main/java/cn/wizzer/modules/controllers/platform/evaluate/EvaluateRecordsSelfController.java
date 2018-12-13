package cn.wizzer.modules.controllers.platform.evaluate;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.evaluate.*;
import cn.wizzer.modules.models.monitor.Monitor_catalog;
import cn.wizzer.modules.models.monitor.Monitor_index;
import cn.wizzer.modules.models.sys.Sys_user;
import cn.wizzer.modules.services.evaluate.EvaluateQualifyService;
import cn.wizzer.modules.services.evaluate.EvaluateRecordsSelfService;
import cn.wizzer.modules.services.evaluate.EvaluateRemarkService;
import cn.wizzer.modules.services.evaluate.EvaluateSummaryService;
import cn.wizzer.modules.services.monitor.MonitorCatalogService;
import cn.wizzer.modules.services.monitor.MonitorIndexService;
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
@At("/platform/evaluate/records/self")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateRecordsSelfController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateRecordsSelfService evaluateRecordsSelfService;
	@Inject
	private MonitorIndexService monitorIndexService;
	@Inject
	private MonitorCatalogService monitorCatalogService;
	@Inject
	private EvaluateQualifyService evaluateQualifyService;
	@Inject
	private EvaluateRemarkService evaluateRemarkService;
	@Inject
	private EvaluateSummaryService evaluateSummaryService;

	@At("")
	@Ok("beetl:/platform/evaluate/records/self/index.html")
	@RequiresAuthentication
	public void index() {

	}

	//学校自评列表
	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
		//获取当前用户
		Subject subject = SecurityUtils.getSubject();
		Sys_user user = (Sys_user) subject.getPrincipal();
		//评估单位仅显示自己单位的自评记录
		if(user!=null && user.getUnit().isEvaluate()){
			cnd = Cnd.where("schoolid", "=", user.getUnit().getId());
		}
		return evaluateRecordsSelfService.data(length, start, draw, order, columns, cnd, "school");
    }

    @At
    @Ok("beetl:/platform/evaluate/records/self/add.html")
    @RequiresAuthentication
    public void add(HttpServletRequest req) {
		//获取当前用户
		Subject subject = SecurityUtils.getSubject();
		Sys_user user = (Sys_user) subject.getPrincipal();
		//仅评估单位可以申报评估
		if(user!=null && user.getUnit().isEvaluate()){
			req.setAttribute("unitType",user.getUnit().getUnitType());
		}
    }

    @At
    @Ok("json")
    @SLog(tag = "初始化学校评估数据", msg = "")
    public Object addDo(@Param("year") int year,@Param("unitType") String unitType,@Param("schoolId") String schoolId, HttpServletRequest req) {

		try {
			int totalWeights = monitorIndexService.getTotalWeights(unitType);
				if(!Strings.isBlank(schoolId)){

					Evaluate_records_self records = new Evaluate_records_self();
					records.setYear(year);
					records.setSchoolId(schoolId);
					records.setWeights(totalWeights);
					//插入评估记录
					records = evaluateRecordsSelfService.insert(records);
					List<Monitor_index> monitorIndexs = monitorIndexService.query(
							Cnd.where("unitType", "=", Strings.sBlank(unitType)).and("qualify", "=", true));
					for (Monitor_index index : monitorIndexs) {
						Evaluate_qualify qualify = new Evaluate_qualify();
						qualify.setEvaluateId(records.getId());
						qualify.setIndexId(index.getId());

						//插入达标性评估记录
						evaluateQualifyService.insert(qualify);

					}
					monitorIndexs = monitorIndexService.query(
							Cnd.where("unitType", "=", Strings.sBlank(unitType)).and("qualify", "=", false));
					for (Monitor_index index : monitorIndexs) {
						Evaluate_remark remark = new Evaluate_remark();
						remark.setEvaluateId(records.getId());
						remark.setIndexid(index.getId());
						if(!index.isSelfeva()){
							//不需要学校自评指标,默认评估完成
							remark.setSelfeva(true);
						}
						//插入监测指标记录
						evaluateRemarkService.insert(remark);
					}

					List<Monitor_catalog> monitorCatalogs = monitorCatalogService.query(
							Cnd.where("unitType", "=", Strings.sBlank(unitType)).and("qualify", "=", true));


					for (Monitor_catalog catalog : monitorCatalogs) {//遍历monitorIndexs
						Evaluate_summary summary = new Evaluate_summary();//新建自评概述表
						summary.setEvaluateid(records.getId());
						summary.setCatalogid(catalog.getId());
						evaluateSummaryService.insert(summary);//插入自评概述

					}

					return Result.success("system.success");
				}

		} catch (Exception e) {
			return Result.error("system.error");
		}
		return Result.error("system.error");
    }
	@At({"/submit/?"})
	@Ok("json")
	@SLog(tag = "删除Evaluate_records_self", msg = "ID:${args[2].getAttribute('id')}")
	public Object submit(String id ,HttpServletRequest req) {
		try {

			evaluateRecordsSelfService.submit(id);
			req.setAttribute("id", id);

			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}
	//region 暂时没用，评估记录只有初始化操作和列表操作
	@At("/edit/?")
    @Ok("beetl:/platform/evaluate/records/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return evaluateRecordsSelfService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Evaluate_records_self", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Evaluate_records_self evaluateRecords, HttpServletRequest req) {
		try {

			evaluateRecords.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateRecordsSelfService.updateIgnoreNull(evaluateRecords);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Evaluate_records_self", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				evaluateRecordsSelfService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				evaluateRecordsSelfService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/evaluate/records/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return evaluateRecordsSelfService.fetch(id);

		}
		return null;
    }
	//endregion

}
