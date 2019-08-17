package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Globals;
import net.hznu.common.base.Result;
import net.hznu.common.chart.MonitorStat;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.evaluate.Evaluate_index;
import net.hznu.modules.models.monitor.Monitor_catalog;
import net.hznu.modules.services.evaluate.EvaluateIndexService;
import net.hznu.modules.services.monitor.MonitorCatalogService;
import net.hznu.modules.services.monitor.MonitorIndexService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
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
	private MonitorIndexService monitorIndexService;
	@Inject
	private MonitorCatalogService monitorCatalogService;


	@At("/evaluate_county")
	@Ok("beetl:/platform/evaluate/evaluate_county.html")
	@RequiresAuthentication
	public void evaluate_county(HttpServletRequest req) {

		req.setAttribute("xzqh","330102");
	}
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

}
