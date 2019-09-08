package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.common.util.XwpfUtil;
import net.hznu.modules.models.monitor.Monitor_index;
import net.hznu.modules.models.sys.Sys_unit;
import net.hznu.modules.models.sys.Sys_user;
import net.hznu.modules.services.evaluate.EvaluateSummaryService;
import net.hznu.modules.services.monitor.MonitorCatalogService;
import net.hznu.modules.services.monitor.MonitorIndexService;
import net.hznu.modules.services.sys.SysUnitService;
import net.hznu.modules.services.sys.SysUserService;
import net.hznu.modules.models.evaluate.Evaluate_records;
import net.hznu.modules.models.evaluate.Evaluate_remark;
import net.hznu.modules.services.evaluate.EvaluateRecordsService;
import net.hznu.modules.services.evaluate.EvaluateRemarkService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.entity.Record;
import org.nutz.dao.Cnd;
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
import java.util.*;

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
	private SysUserService sysUserService;
	@Inject
	private MonitorIndexService monitorIndexService;

	@Inject
	private EvaluateRemarkService evaluateRemarkService;
	@Inject
	private MonitorCatalogService monitorCatalogService;
	@Inject
	private EvaluateSummaryService evaluateSummaryService;



//	@At({"","/?"})
//	@Ok("beetl:/platform/evaluate/records/${req_attr.type}/index.html")
//	@RequiresAuthentication
////	public void index(String type, HttpServletRequest req) {
////		req.setAttribute("type", type);
//	public void index(String type, HttpServletRequest req) {
//
//		req.setAttribute("type", type);
//	}
	@At("/special")
	@Ok("beetl:/platform/evaluate/records/special/index.html")
	@RequiresAuthentication
	public void specindex( HttpServletRequest req) {

	}

	@At("/special/assign")
	@Ok("beetl:/platform/evaluate/records/special/assign.html")
	@RequiresAuthentication
	public void assign( HttpServletRequest req) {
		req.setAttribute("unitType","111");
	}

	//评估专家分配页面的评估组别树
	@At({"/tree","/tree/?"})
	@Ok("json")
	@RequiresAuthentication
	public Object tree(String evatype,@Param("pid") String pid) {
		Cnd cnd = Cnd.where("parentId", "=", Strings.sBlank(pid));

		List<Record> list = evaluateRecordsService.getGrouplist();

		List<Map<String, Object>> tree = new ArrayList<>();
		/*for (Evaluate_records records : list) {
			Map<String, Object> obj = new HashMap<>();
			obj.put("id", records.getId());
			obj.put("text", records.getName());
			//obj.put("children", records.isHasChildren());
			tree.add(obj);
		}*/
 		for(Record record:list) {
			Map<String, Object> obj = new HashMap<>();
			obj.put("id", record.getString("id"));
			obj.put("text", record.getString("taskname"));
			obj.put("children",true);
			tree.add(obj);
		}
		return tree;
	}
	//评估专家分配页面的评估组别树的子树
	@At({"/schooltree","/schooltree/?"})
	@Ok("json")
	@RequiresAuthentication
	public Object schooltree(String evatype,@Param("taskname") String taskname) {
		Cnd cnd = Cnd.NEW();
		if (!Strings.isBlank(taskname)) {
			cnd.and("taskname", "like", "%" + taskname + "%");
		}
		List<Record> list = evaluateRecordsService.getSchoollistbygroup(taskname);
		List<Map<String, Object>>tree = new ArrayList<>();

		for(Record record:list) {
			Map<String, Object> obj = new HashMap<>();
			obj.put("id", record.getString("id"));
			obj.put("text", record.getString("name"));
			obj.put("children",false);
			tree.add(obj);
		}
		return tree;
	}
	@At("")
	@Ok("beetl:/platform/evaluate/records/index.html")
	@RequiresAuthentication
	public void index( HttpServletRequest req) {

	}
	//部门审核列表
	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("year") int year, @Param("taskname") String taskname, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {

		//只审核已经提交的
		Cnd cnd = Cnd.NEW();
		if (year!=0) {
			cnd.and("year", "=",  year );
		}
		if (!Strings.isBlank(taskname)) {
			cnd.and("taskname", "like", "%" + taskname + "%");
		}
    	return evaluateRecordsService.data(length, start, draw, order, columns, cnd, "school");
    }
    //获取专家评审列表
	@At
	@Ok("json:full")
	@RequiresPermissions("evaluate.verify.special")
	public Object specdata(@Param("year") int year, @Param("taskname") String taskname,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
		//获取专家id
		Subject subject = SecurityUtils.getSubject();
		Sys_user user = (Sys_user) subject.getPrincipal();
		//2019-01-08 超级专家可以审核所有的学校
		if(!subject.isPermitted("evaluate.verify.special.all")) {
			List<String> evaluateidList = evaluateRecordsService.getEvaluateIdsBySpecial(user.getId());


			//2018-12-21修改，可以先由专家审核,专家只审核自己分配的学校
			cnd = Cnd.where("id", "in", evaluateidList).and("status_p", "=", false);
		}
		if (year!=0) {
			cnd.and("year", "=",  year );
		}
		if (!Strings.isBlank(taskname)) {
			cnd.and("taskname", "like", "%" + taskname + "%");
		}

		return evaluateRecordsService.data(length, start, draw, order, columns, cnd, "school");
	}

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
    public Object addDo(@Param("year") int year,@Param("taskname")String taskname,@Param("schoolIds") String[] schoolIds,
//						@Param("specialIds") String[] specialIds,
						HttpServletRequest req) {
		try {
			if(schoolIds!=null&&schoolIds.length>0){
				for (String schoolid : schoolIds) {
					//获取学校类别
					Sys_unit school = sysUnitService.fetch(schoolid);
					String unitType =school.getUnitType();
					int totalWeights = 100;
					//这里统一按100分处理
//					totalWeights= monitorIndexService.getTotalWeights(unitType);

					Evaluate_records records = new Evaluate_records();
					records.setYear(year);
					records.setTaskname(taskname);
					records.setSchoolId(schoolid);
					records.setWeights(totalWeights);

					//插入评估记录
					records = evaluateRecordsService.insert(records);
					List<Monitor_index> monitorIndexs = monitorIndexService.query(
							Cnd.where("unitType", "=", Strings.sBlank(unitType)));

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

					//学校自评概述是否要填，原来基础性指标是要填写的，这里先不填
					/*List<Monitor_catalog> monitorCatalogs = monitorCatalogService.query(
							Cnd.where("unitType", "=", Strings.sBlank(unitType))
									.and("qualify", "=", true)
									.and("hasChildren", "=", false));


					for (Monitor_catalog catalog : monitorCatalogs) {//遍历monitorIndexs
						Evaluate_summary summary = new Evaluate_summary();//新建自评概述表
						summary.setEvaluateid(records.getId());
						summary.setCatalogid(catalog.getId());
						evaluateSummaryService.insert(summary);//插入自评概述

					}*/
					//原来这里分配评估专家，现在移到专门的模块

				}

			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}

    }


	@At({"/locked/?"})
	@Ok("json")
	@SLog(tag = "锁定评估", msg = "ID:${args[2].getAttribute('id')}")
	public Object locked(String id ,HttpServletRequest req) {
		try {
			Evaluate_records records = evaluateRecordsService.fetch(id);
			evaluateRecordsService.update(org.nutz.dao.Chain.make("locked", true), Cnd.where("id", "=", id));
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}

	@At({"/unlocked/?"})
	@Ok("json")
	@SLog(tag = "解锁评估", msg = "ID:${args[2].getAttribute('id')}")
	public Object unlocked(String id ,HttpServletRequest req) {
		try {
			Evaluate_records records = evaluateRecordsService.fetch(id);
			evaluateRecordsService.update(org.nutz.dao.Chain.make("locked", false), Cnd.where("id", "=", id));
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}


	@At({"/delete","/delete/?"})
	@Ok("json")
	@SLog(tag = "删除Evaluate_records", msg = "ID:${args[0]}")
	public Object delete(String id ,HttpServletRequest req) {
		try {
				//通过级联删除其他外键表记录
				evaluateRecordsService.delete(id);
				//evaluateRecordsService.deleteAndChild(id);
				//req.setAttribute("id", id);

			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
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
	@At("/selectindex")
	@Ok("beetl:/platform/evaluate/records/selectspecial.html")
	@RequiresAuthentication
	public void selectindex(@Param("unitType") String unitType, HttpServletRequest req) {

		req.setAttribute("unitType", unitType);
	}
	@At("/selectspecialdata")
	@Ok("json:full")
	@RequiresAuthentication
	public Object selectspecialdata( @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {

		return evaluateRecordsService.getSpecialUser();

	}
	//获取评估分配的专家
	@At("/specialdata")
	@Ok("json:full")
	@RequiresAuthentication
	public Object specialdata(@Param("recordIds") String[] recordIds, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		if(recordIds!=null && recordIds.length>0) {
			return evaluateRecordsService.getEvaluateSpecial(recordIds);
		}else return null;

	}

	@At("/special/pushSpecial")
	@Ok("json")
//	@RequiresPermissions("sys.manager.role.user")
	@SLog(tag = "分配专家到评估记录", msg = "评估记录Id:${args[1]},专家ID:${args[0]}")
	public Object pushSpecial(@Param("specialIds") String specialIds, @Param("evaluateid") String evaluateid,@Param("roleid") String roleid, HttpServletRequest req) {
		try {
			String[] ids = StringUtils.split(specialIds, ",");
			String[] evaluateIds = StringUtils.split(evaluateid, ",");
			for (String eid : evaluateIds) {
				for (String s : ids) {
					if (!Strings.isEmpty(s)) {
						evaluateRecordsService.assignEvaluateSpecial(eid,roleid,s);
					}
				}
			}

			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}
	@At("/special/download/?")
	@Ok("void")
	@RequiresAuthentication
	public Object spec_download(String id, HttpServletResponse resp) {
		if (!Strings.isBlank(id)) {
			Map<String,Object> wordDataMap = packageObject(id);
			XwpfUtil xwpfUtil = new XwpfUtil();
			//读入word模板
			InputStream is = getClass().getClassLoader().getResourceAsStream("template/SpecialEvaluate.docx");
			try {
				String filename = "鄞州学校发展性评价专家评估表.docx";
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



		Evaluate_records evaluate = evaluateRecordsService.fetch(evalId);
		evaluate = evaluateRecordsService.fetchLinks(evaluate,"school");
		parametersMap.put("unitname", evaluate.getSchool().getName());

		List<Evaluate_remark> remarklist = evaluateRemarkService.query(Cnd.where("depttype","=","Special").and("evaluateId", "=", evalId ));

		for (Evaluate_remark remark : remarklist) {
			int location = remark.getLocation();
			double score_p = remark.getScore_p();
			String advantage = remark.getAdvantage();
			String disadvantae = remark.getDisadvantage();
			String suggestion = remark.getSuggestion();

			parametersMap.put("p_i" + location, formatDouble(score_p));
			parametersMap.put("p_advantage" + location, advantage);
			parametersMap.put("p_disadvantage" + location, disadvantae);
			parametersMap.put("p_suggestion" + location, suggestion);

		}
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
}
