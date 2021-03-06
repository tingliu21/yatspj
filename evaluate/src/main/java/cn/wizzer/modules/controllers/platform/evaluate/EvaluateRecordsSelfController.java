package cn.wizzer.modules.controllers.platform.evaluate;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.common.util.XwpfUtil;
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
import org.nutz.dao.entity.Record;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	@At
	@Ok("beetl:/platform/evaluate/records/self/index_basic.html")
	@RequiresAuthentication
	public void index_basic(HttpServletRequest req) {
		req.setAttribute("ctype",1);
	}

	@At
	@Ok("beetl:/platform/evaluate/records/self/index_standard.html")
	@RequiresAuthentication
	public void index_standard(HttpServletRequest req) {
		req.setAttribute("ctype",2);
	}

	@At
	@Ok("beetl:/platform/evaluate/records/self/index_develop.html")
	@RequiresAuthentication
	public void index_develop(HttpServletRequest req) {
		req.setAttribute("ctype",3);
	}

	@At
	@Ok("beetl:/platform/evaluate/records/self/index_download.html")
	@RequiresAuthentication
	public void index_download() {

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
							Cnd.where("unitType", "=", Strings.sBlank(unitType)).and("qualify", "=", true).and("hasChildren", "=", false));


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


//    @At({"/delete","/delete/?"})
//    @Ok("json")
//    @SLog(tag = "删除Evaluate_records_self", msg = "ID:${args[2].getAttribute('id')}")
//    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
//		try {
//			if(ids!=null&&ids.length>0){
//				evaluateRecordsSelfService.delete(ids);
//    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
//			}else{
//				evaluateRecordsSelfService.delete(id);
//    			req.setAttribute("id", id);
//			}
//			return Result.success("system.success");
//		} catch (Exception e) {
//			return Result.error("system.error");
//		}
//    }

	@At({"/delete","/delete/?"})
	@Ok("json")
	@SLog(tag = "删除Evaluate_records_self", msg = "ID:")
	public Object delete(String id ,HttpServletRequest req) {
		try {
			evaluateRecordsSelfService.deleteAndChild(id);
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
			return evaluateRecordsSelfService.fetch(id);

		}
		return null;
    }
	@At("/download/?")
	@Ok("void")
	@RequiresAuthentication
	public Object download(String id, HttpServletResponse resp) {
		if (!Strings.isBlank(id)) {
			Map<String,Object> wordDataMap = packageObject(id);
			XwpfUtil xwpfUtil = new XwpfUtil();
			//读入word模板
			InputStream is = getClass().getClassLoader().getResourceAsStream("template/SelfEvaluate.docx");
			try {
				String filename = "拱墅区现代优质学校评估自评表.docx";
				filename = URLEncoder.encode(filename, "UTF-8");

				xwpfUtil.exportWord(wordDataMap,is,resp,filename);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

	/**
	 * 组装word文档中需要显示数据的集合
	 * @return
	 */
	public Map<String, Object> packageObject(String evalId) {
		Map<String,Object> wordDataMap = new HashMap<String,Object>();
		Map<String, Object> parametersMap = new HashMap<String, Object>();

		List<Map<String, Object>> table_scale = new ArrayList<Map<String, Object>>();

		List<Record> recordsUnitInfo = evaluateRecordsSelfService.getUnitInfo(evalId);
		if (recordsUnitInfo.size() > 0) {
			parametersMap.put("unitname", recordsUnitInfo.get(0).getString("unitname"));
			parametersMap.put("address", recordsUnitInfo.get(0).getString("address"));
			parametersMap.put("website", recordsUnitInfo.get(0).getString("website"));
			parametersMap.put("telephone", recordsUnitInfo.get(0).getString("telephone"));
			parametersMap.put("email", recordsUnitInfo.get(0).getString("email"));
		}

		List<Record> recordsBasicEvalData = evaluateRecordsSelfService.getBasicEvalData(evalId);
		for (Record record : recordsBasicEvalData) {
			int location = record.getInt("location");
			if (record.get("qualify") != null) {
				if ((boolean) record.get("qualify"))
					parametersMap.put("s_i" + location, "是");
				else
					parametersMap.put("s_i" + location, "否");
			} else {
				parametersMap.put("s_i" + location, "");
			}
		}

		List<Record> recordsBasicSummaryData = evaluateRecordsSelfService.getBasicSummaryData(evalId);
		for (Record record : recordsBasicSummaryData) {
			int location = record.getInt("location");
			parametersMap.put("sintro"+location, record.getString("summary"));
		}

		List<Record> recordsRemarkData = evaluateRecordsSelfService.getRemarkData(evalId);
		for (Record record : recordsRemarkData) {
			int location = record.getInt("location");
			double score_s = record.getDouble("score_s");
			double score_p = record.getDouble("score_p");
			String remark_s = record.getString("remark_s");
			parametersMap.put("s_i" + location, formatDouble(score_s));
			parametersMap.put("p_i" + location, formatDouble(score_p));
			parametersMap.put("r_i" + location, remark_s);
		}

		List<Record> recordsScaleData = evaluateRecordsSelfService.getScaleData(evalId);
		for (Record record : recordsScaleData) {
			Map<String, Object> map=new HashMap<>();
			map.put("grade", record.getString("grade"));
			map.put("plannum", record.getInt("planenrollnum"));
			map.put("realnum", record.getInt("actualenrollnum"));
			map.put("classnum", record.getInt("classnum"));
			map.put("avgnum", record.getInt("averagenum"));
			map.put("extra", record.getString("instruction"));
			table_scale.add(map);
		}


		wordDataMap.put("table_scale", table_scale);
		wordDataMap.put("parametersMap", parametersMap);
		return wordDataMap;
	}

	public String formatDouble(double d) {
		BigDecimal bg = new BigDecimal(d).setScale(1, RoundingMode.UP);
		double num = bg.doubleValue();
		if (Math.round(num) - num == 0) {
			return String.valueOf((long) num);
		}
		return String.valueOf(num);
	}

//endregion

}
