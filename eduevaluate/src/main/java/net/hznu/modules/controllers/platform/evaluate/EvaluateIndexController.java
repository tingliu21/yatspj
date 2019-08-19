package net.hznu.modules.controllers.platform.evaluate;

import com.sun.tools.corba.se.idl.constExpr.EvaluationException;
import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Globals;
import net.hznu.common.base.Result;
import net.hznu.common.chart.MonitorStat;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.evaluate.Evaluate_index;
import net.hznu.modules.models.evaluate.Evaluate_records;
import net.hznu.modules.models.monitor.Monitor_catalog;
import net.hznu.modules.models.monitor.Monitor_index;
import net.hznu.modules.models.sys.Sys_unit;
import net.hznu.modules.models.sys.Sys_user;
import net.hznu.modules.services.evaluate.EvaluateIndexService;
import net.hznu.modules.services.evaluate.EvaluateRecordsService;
import net.hznu.modules.services.monitor.MonitorCatalogService;
import net.hznu.modules.services.monitor.MonitorIndexService;
import org.apache.commons.lang.StringUtils;
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
import java.util.ArrayList;
import java.util.List;

@IocBean
@At("/platform/evaluate")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateIndexController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateIndexService evaluateIndexService;
	@Inject
	private EvaluateRecordsService evaluateRecordsService;
	@Inject
	private MonitorIndexService monitorIndexService;
	@Inject
	private MonitorCatalogService monitorCatalogService;


	//县监测级指标达成度
	@At("/evaluate_county")
	@Ok("re")
	//@Ok("beetl:/platform/evaluate/evaluate_county.html")
	@RequiresAuthentication
	public String evaluate_county(@Param("evaluateId") String evaluateId,HttpServletRequest req) {
		if(StringUtils.isNotBlank(evaluateId)){
			Evaluate_records record = evaluateRecordsService.fetch(evaluateId);
			record = evaluateRecordsService.fetchLinks(record,"unit");
			req.setAttribute("xzqh",record.getUnit().getUnitcode());
			return "beetl:/platform/evaluate/evaluate_county.html";
		}else{
			//获取当前用户id
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();

			Sys_unit sysUnit = user.getUnit();
			int level = sysUnit.getLevel();
			if(level == 3){
				req.setAttribute("xzqh",sysUnit.getUnitcode());
				return "beetl:/platform/evaluate/evaluate_county.html";
			}else{
				return "redirect:/platform/evaluate/records";
			}
		}
	}
	//县一级指标达成度图
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getCountyScore1(@Param("xzqhdm") String xzqh){
		List<MonitorStat> result = new ArrayList<>();

		String viewname = "v_xzqh_evaluate1_"+ Globals.EvaluateYear;
		//获取一级指标
		List<Monitor_catalog> catalogs = monitorCatalogService.query(Cnd.where("level","=",1).
				and("year","=",Globals.EvaluateYear).and("isshow","=",true).asc("catacode"));
		List<String> fieldnames = new ArrayList<>(catalogs.size());
		for(Monitor_catalog catalog : catalogs){
			String fieldname = "index_"+catalog.getCatacode();
			fieldnames.add(fieldname);
		}
		//加入县得分
		MonitorStat monitorStat = evaluateIndexService.getScoreByXZQH(viewname,fieldnames,xzqh).get(0);
		monitorStat.setName("得分");
		result.add(monitorStat);
		//加入省均值
		String proXZQH = xzqh.substring(0,2)+"0000";
		monitorStat = evaluateIndexService.getAvgScoreByXZQH(viewname,fieldnames,proXZQH);
		result.add(monitorStat);
		//加入目标值

		monitorStat = monitorCatalogService.getScoreByLevel(Globals.EvaluateYear,1);
		result.add(monitorStat);

		return result;
	}
	//县二级指标达成度图
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getCountyScore2(@Param("xzqhdm") String xzqh){
		List<MonitorStat> result = new ArrayList<>();

		String viewname = "v_xzqh_evaluate2_"+ Globals.EvaluateYear;
		//获取二级指标
		List<Monitor_catalog> catalogs = monitorCatalogService.query(Cnd.where("level","=",2).
				and("year","=",Globals.EvaluateYear).and("isshow","=",true).asc("catacode"));
		List<String> fieldnames = new ArrayList<>(catalogs.size());
		for(Monitor_catalog catalog : catalogs){
			String fieldname = "index_"+catalog.getCatacode();
			fieldnames.add(fieldname);
		}
		//加入县得分
		MonitorStat monitorStat = evaluateIndexService.getScoreByXZQH(viewname,fieldnames,xzqh).get(0);
		monitorStat.setName("得分");
		result.add(monitorStat);
		//加入省均值
		String proXZQH = xzqh.substring(0,2)+"0000";
		monitorStat = evaluateIndexService.getAvgScoreByXZQH(viewname,fieldnames,proXZQH);
		result.add(monitorStat);
		//加入目标值

		monitorStat = monitorCatalogService.getScoreByLevel(Globals.EvaluateYear,2);
		result.add(monitorStat);

		return result;
	}
	//县监测点达成度图
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getCountyScorep(@Param("xzqhdm") String xzqh){
		List<MonitorStat> result = new ArrayList<>();

		String viewname = "v_xzqh_evaluate_crosstab_"+ Globals.EvaluateYear;
		//获取二级指标
		List<Monitor_index> indexList = monitorIndexService.query(Cnd.where("level","=",1).
				and("year","=",Globals.EvaluateYear).and("isshow","=",true).asc("location"));
		List<String> fieldnames = new ArrayList<>(indexList.size());
		for(Monitor_index index : indexList){
			String fieldname = "m"+index.getLocation();
			fieldnames.add(fieldname);
		}
		//加入县得分
		MonitorStat monitorStat = evaluateIndexService.getScoreByXZQH(viewname,fieldnames,xzqh).get(0);
		monitorStat.setName("得分");
		result.add(monitorStat);
		//加入省均值
		String proXZQH = xzqh.substring(0,2)+"0000";
		monitorStat = evaluateIndexService.getAvgScoreByXZQH(viewname,fieldnames,proXZQH);
		result.add(monitorStat);
		//加入目标值
		monitorStat = monitorIndexService.getScoreByLevel(Globals.EvaluateYear,1);
		result.add(monitorStat);

		return result;
	}

}
