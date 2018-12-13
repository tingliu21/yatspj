package cn.wizzer.modules.controllers.platform.evaluate;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.evaluate.Evaluate_qualify;
import cn.wizzer.modules.models.evaluate.Evaluate_records;
import cn.wizzer.modules.models.evaluate.Evaluate_remark;
import cn.wizzer.modules.models.monitor.Monitor_index;
import cn.wizzer.modules.models.monitor.Monitor_catalog;
import cn.wizzer.modules.models.evaluate.Evaluate_summary;
import cn.wizzer.modules.models.sys.Sys_user;
import cn.wizzer.modules.services.evaluate.EvaluateQualifyService;
import cn.wizzer.modules.services.evaluate.EvaluateRecordsService;
import cn.wizzer.modules.services.evaluate.EvaluateRemarkService;
import cn.wizzer.modules.services.evaluate.EvaluateSummaryService;
import cn.wizzer.modules.services.monitor.MonitorCatalogService;
import cn.wizzer.modules.services.monitor.MonitorIndexService;
import cn.wizzer.modules.services.sys.SysUnitService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
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
@At("/platform/evaluate/records")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateRecordsController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateRecordsService evaluateRecordsService;
	@Inject
	private SysUnitService sysUnitService;
	@Inject
	private MonitorIndexService monitorIndexService;
	@Inject
	private EvaluateQualifyService evaluateQualifyService;
	@Inject
	private EvaluateRemarkService evaluateRemarkService;
	@Inject
	private MonitorCatalogService monitorCatalogService;
	@Inject
	private EvaluateSummaryService evaluateSummaryService;


	@At({"","/?"})
	@Ok("beetl:/platform/evaluate/records/${req_attr.type}/index.html")
	@RequiresAuthentication
//	public void index(String type, HttpServletRequest req) {
//		req.setAttribute("type", type);
	public void index(String type, HttpServletRequest req) {
		req.setAttribute("type", type);
	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {

		//只审核已经提交的
		Cnd cnd = Cnd.where("status_s","=",true);



    	return evaluateRecordsService.data(length, start, draw, order, columns, cnd, "school");
    }

//    @At("/add/?")
	@At
    @Ok("beetl:/platform/evaluate/records/add.html")
    @RequiresAuthentication
//    public void add(String type,HttpServletRequest req) {
//		req.setAttribute("type", type);
	public void add(HttpServletRequest req) {

    }

    @At
    @Ok("json")
    @SLog(tag = "初始化学校评估数据", msg = "")
    public Object addDo(@Param("year") int year,@Param("unitType") String unitType,@Param("schoolIds") String[] schoolIds, HttpServletRequest req) {
		try {
			int totalWeights = monitorIndexService.getTotalWeights(unitType);

			if(schoolIds!=null&&schoolIds.length>0){

				for (String schoolid : schoolIds) {
					Evaluate_records records = new Evaluate_records();
					records.setYear(year);
					records.setSchoolId(schoolid);
					records.setWeights(totalWeights);
					//插入评估记录
					records = evaluateRecordsService.insert(records);
					List<Monitor_index> monitorIndexs = monitorIndexService.query(
							Cnd.where("unitType", "=", Strings.sBlank(unitType)).and("qualify", "=", true));


					for (Monitor_index index : monitorIndexs) {
						Evaluate_qualify qualify = new Evaluate_qualify();
						qualify.setEvaluateId(records.getId());
						qualify.setIndexId(index.getId());

						//插入达标行评估记录
						evaluateQualifyService.insert(qualify);


					}
					monitorIndexs = monitorIndexService.query(
							Cnd.where("unitType", "=", Strings.sBlank(unitType)).and("qualify", "=", true).and("hasChildren", "=", false));
					for (Monitor_index index : monitorIndexs) {
						Evaluate_remark remark = new Evaluate_remark();
						remark.setEvaluateId(records.getId());
						remark.setIndexid(index.getId());
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


				}

			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}

    }

    @At("/edit/?")
    @Ok("beetl:/platform/evaluate/records/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return evaluateRecordsService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Evaluate_records", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Evaluate_records evaluateRecords, HttpServletRequest req) {
		try {

			evaluateRecords.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateRecordsService.updateIgnoreNull(evaluateRecords);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


//    @At({"/delete","/delete/?"})
//    @Ok("json")
//    @SLog(tag = "删除Evaluate_records", msg = "ID:${args[2].getAttribute('id')}")
//    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
//		try {
//			if(ids!=null&&ids.length>0){
//				evaluateRecordsService.delete(ids);
//
//    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
//			}else{
//				evaluateRecordsService.delete(id);
//    			req.setAttribute("id", id);
//			}
//			return Result.success("system.success");
//		} catch (Exception e) {
//			return Result.error("system.error");
//		}
//    }
	@At({"/delete","/delete/?"})
	@Ok("json")
	@SLog(tag = "删除Evaluate_records", msg = "ID:${args[2].getAttribute('id')}")
	public Object delete(String id ,HttpServletRequest req) {
		try {

				evaluateRecordsService.deleteAndChild(id);
				//req.setAttribute("id", id);

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
			return evaluateRecordsService.fetch(id);

		}
		return null;
    }
	@At("/select")
	@Ok("beetl:/platform/evaluate/records/select.html")
	@RequiresAuthentication
	public void select(@Param("unitType") String unitType, HttpServletRequest req) {

		req.setAttribute("unitType", unitType);
	}

	@At("/selectData")
	@Ok("json:full")
	@RequiresAuthentication
	public Object selectData(@Param("unitType") String unitType,  @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
		if (!Strings.isBlank(unitType) && !"0".equals(unitType)) {
			cnd.and("unitType", "=", unitType );
		}
		return sysUnitService.data(length, start, draw, order, columns, cnd, null);

	}

}
