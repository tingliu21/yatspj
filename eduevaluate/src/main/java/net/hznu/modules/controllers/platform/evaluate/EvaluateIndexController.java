package net.hznu.modules.controllers.platform.evaluate;

import com.sun.tools.corba.se.idl.constExpr.EvaluationException;
import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Globals;
import net.hznu.common.base.Result;
import net.hznu.common.chart.MonitorStat;
import net.hznu.common.chart.MonitorSumValue;
import net.hznu.common.chart.StatChart;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.common.util.ImgBase64Util;
import net.hznu.common.util.XwpfUtil;
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
import net.hznu.modules.services.sys.SysUnitService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	@Inject
	private SysUnitService sysUnitService;

	@At
	@Ok("beetl:/platform/evaluate/indexvalue.html")
	@RequiresAuthentication
	public String indexvalue(@Param("evaluateId") String evaluateId, HttpServletRequest req) {

		if (StringUtils.isNotBlank(evaluateId)) {
			req.setAttribute("evaluateId", evaluateId);
			return "beetl:/platform/evaluate/indexvalue.html";
		} else {
			//获取当前用户id
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();

			Sys_unit sysUnit = user.getUnit();
			int level = sysUnit.getLevel();
			if (level == 3) {
				req.setAttribute("xzqhdm", sysUnit.getUnitcode());
				return "beetl:/platform/evaluate/indexvalue.html";
			} else {
				return "redirect:/platform/evaluate/records";
			}
		}
	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("evaluateId") String evaluateId,@Param("year") int year,@Param("xzqhdm") String xzqhdm,@Param("catacode") String catacode, @Param("code") String code,
						   @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
		if(StringUtils.isNotBlank(evaluateId)){
			cnd = cnd.and("evaluateId","=",evaluateId);
		}else if(StringUtils.isNotBlank(xzqhdm)){
			cnd = cnd.and("unitcode","=",xzqhdm).and("year","=",year);
		}
		if(StringUtils.isBlank(catacode)){
			cnd = cnd.and("catacode","like",catacode);
		}
		if(StringUtils.isNotBlank(code)){
			cnd = cnd.and("code","like",code);
		}

		return evaluateIndexService.data(length, start, draw, order, columns, cnd, "");
	}
	//县监测级指标达成度
	@At("/evaluate_county")
	@Ok("re")
	//@Ok("beetl:/platform/evaluate/evaluate_county.html")
	@RequiresAuthentication
	public String evaluate_county(@Param("evaluateId") String evaluateId, HttpServletRequest req) {
		if (StringUtils.isNotBlank(evaluateId)) {
			Evaluate_records record = evaluateRecordsService.fetch(evaluateId);

			req.setAttribute("xzqh", record.getUnitcode());
			req.setAttribute("xzqhmc",record.getXzqhmc());
			return "beetl:/platform/evaluate/evaluate_county.html";
		} else {
			//获取当前用户id
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();

			Sys_unit sysUnit = user.getUnit();
			int level = sysUnit.getLevel();
			if (level == 3) {
				req.setAttribute("xzqh", sysUnit.getUnitcode());
				req.setAttribute("xzqhmc",sysUnit.getXzqhmc());
				return "beetl:/platform/evaluate/evaluate_county.html";
			} else {
				return "redirect:/platform/evaluate/records";
			}
		}
	}
	//省监测级指标达成度
	@At("/evaluate_province")
	@Ok("beetl:/platform/evaluate/evaluate_province.html")
	@RequiresAuthentication
	public void evaluate_province(HttpServletRequest req) {
		req.setAttribute("xzqh", "330000");
	}
	//县一级指标达成度图
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getCountyScore1(@Param("xzqhdm") String xzqh) {
		List<MonitorStat> result = new ArrayList<>();

		String viewname = "v_xzqh_evaluate1_" + Globals.EvaluateYear;
		//获取一级指标
		List<Monitor_catalog> catalogs = monitorCatalogService.query(Cnd.where("level", "=", 1).
				and("year", "=", Globals.EvaluateYear).and("isshow", "=", true).asc("catacode"));
		List<String> fieldnames = new ArrayList<>(catalogs.size());
		for (Monitor_catalog catalog : catalogs) {
			String fieldname = "index_" + catalog.getCatacode();
			fieldnames.add(fieldname);
		}
		//加入县得分
		MonitorStat monitorStat = evaluateIndexService.getScoreByXZQH(viewname, fieldnames, xzqh).get(0);
		monitorStat.setName("得分");
		result.add(monitorStat);
		//加入省均值
		String proXZQH = xzqh.substring(0, 2) + "0000";
		monitorStat = evaluateIndexService.getAvgScoreByXZQH(viewname, fieldnames, proXZQH);
		result.add(monitorStat);
		//加入目标值

		monitorStat = monitorCatalogService.getScoreByLevel(Globals.EvaluateYear, 1);
		result.add(monitorStat);

		return result;
	}

	//县二级指标达成度图
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getCountyScore2(@Param("xzqhdm") String xzqh) {
		List<MonitorStat> result = new ArrayList<>();

		String viewname = "v_xzqh_evaluate2_" + Globals.EvaluateYear;
		//获取二级指标
		List<Monitor_catalog> catalogs = monitorCatalogService.query(Cnd.where("level", "=", 2).
				and("year", "=", Globals.EvaluateYear).and("isshow", "=", true).asc("catacode"));
		List<String> fieldnames = new ArrayList<>(catalogs.size());
		for (Monitor_catalog catalog : catalogs) {
			String fieldname = "index_" + catalog.getCatacode();
			fieldnames.add(fieldname);
		}
		//加入县得分
		MonitorStat monitorStat = evaluateIndexService.getScoreByXZQH(viewname, fieldnames, xzqh).get(0);
		monitorStat.setName("得分");
		result.add(monitorStat);
		//加入省均值
		String proXZQH = xzqh.substring(0, 2) + "0000";
		monitorStat = evaluateIndexService.getAvgScoreByXZQH(viewname, fieldnames, proXZQH);
		result.add(monitorStat);
		//加入目标值

		monitorStat = monitorCatalogService.getScoreByLevel(Globals.EvaluateYear, 2);
		result.add(monitorStat);

		return result;
	}

	//县监测点达成度图
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getCountyScorep(@Param("xzqhdm") String xzqh) {
		List<MonitorStat> result = new ArrayList<>();

		String viewname = "v_xzqh_evaluate_crosstab_" + Globals.EvaluateYear;
		//获取二级指标
		List<Monitor_index> indexList = monitorIndexService.query(Cnd.where("level", "=", 1).
				and("year", "=", Globals.EvaluateYear).and("isshow", "=", true).asc("location"));
		List<String> fieldnames = new ArrayList<>(indexList.size());
		for (Monitor_index index : indexList) {
			String fieldname = "m" + index.getLocation();
			fieldnames.add(fieldname);
		}
		//加入县得分
		MonitorStat monitorStat = evaluateIndexService.getScoreByXZQH(viewname, fieldnames, xzqh).get(0);
		monitorStat.setName("得分");
		result.add(monitorStat);
		//加入省均值
		String proXZQH = xzqh.substring(0, 2) + "0000";
		monitorStat = evaluateIndexService.getAvgScoreByXZQH(viewname, fieldnames, proXZQH);
		result.add(monitorStat);
		//加入目标值
		monitorStat = monitorIndexService.getScoreByLevel(Globals.EvaluateYear, 1);
		result.add(monitorStat);

		return result;
	}
	//省一级指标达成度图
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getAvgScore1(@Param("xzqhdm") String xzqh) {
		List<MonitorStat> result = new ArrayList<>();

		String viewname = "v_xzqh_evaluate1_" + Globals.EvaluateYear;
		//获取一级指标
		List<Monitor_catalog> catalogs = monitorCatalogService.query(Cnd.where("level", "=", 1).
				and("year", "=", Globals.EvaluateYear).and("isshow", "=", true).asc("catacode"));
		List<String> fieldnames = new ArrayList<>(catalogs.size());
		for (Monitor_catalog catalog : catalogs) {
			String fieldname = "index_" + catalog.getCatacode();
			fieldnames.add(fieldname);
		}

		//加入省均值
		String proXZQH = xzqh.substring(0, 2) + "0000";
		MonitorStat monitorStat = evaluateIndexService.getAvgScoreByXZQH(viewname, fieldnames, proXZQH);
		result.add(monitorStat);
		//加入目标值

		monitorStat = monitorCatalogService.getScoreByLevel(Globals.EvaluateYear, 1);
		result.add(monitorStat);

		return result;
	}

	//省二级指标达成度图
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getAvgScore2(@Param("xzqhdm") String xzqh) {
		List<MonitorStat> result = new ArrayList<>();

		String viewname = "v_xzqh_evaluate2_" + Globals.EvaluateYear;
		//获取二级指标
		List<Monitor_catalog> catalogs = monitorCatalogService.query(Cnd.where("level", "=", 2).
				and("year", "=", Globals.EvaluateYear).and("isshow", "=", true).asc("catacode"));
		List<String> fieldnames = new ArrayList<>(catalogs.size());
		for (Monitor_catalog catalog : catalogs) {
			String fieldname = "index_" + catalog.getCatacode();
			fieldnames.add(fieldname);
		}

		//加入省均值
		String proXZQH = xzqh.substring(0, 2) + "0000";
		MonitorStat monitorStat = evaluateIndexService.getAvgScoreByXZQH(viewname, fieldnames, proXZQH);
		result.add(monitorStat);
		//加入目标值

		monitorStat = monitorCatalogService.getScoreByLevel(Globals.EvaluateYear, 2);
		result.add(monitorStat);

		return result;
	}

	//省监测点达成度图
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getAvgScorep(@Param("xzqhdm") String xzqh) {
		List<MonitorStat> result = new ArrayList<>();

		String viewname = "v_xzqh_evaluate_crosstab_" + Globals.EvaluateYear;
		//获取二级指标
		List<Monitor_index> indexList = monitorIndexService.query(Cnd.where("level", "=", 1).
				and("year", "=", Globals.EvaluateYear).and("isshow", "=", true).asc("location"));
		List<String> fieldnames = new ArrayList<>(indexList.size());
		for (Monitor_index index : indexList) {
			String fieldname = "m" + index.getLocation();
			fieldnames.add(fieldname);
		}

		//加入省均值
		String proXZQH = xzqh.substring(0, 2) + "0000";
		MonitorStat monitorStat = evaluateIndexService.getAvgScoreByXZQH(viewname, fieldnames, proXZQH);
		result.add(monitorStat);
		//加入目标值
		monitorStat = monitorIndexService.getScoreByLevel(Globals.EvaluateYear, 1);
		result.add(monitorStat);

		return result;
	}
	//getCityTotalScore
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getCityTotalScore(@Param("xzqhdm") String xzqh) {
		List<MonitorSumValue> monitorSumValueList = evaluateRecordsService.getCityAvgTotalScore(Globals.EvaluateYear,xzqh);

		return monitorSumValueList;
	}
	@At
	@Ok("void")
	public void exportCounty(HttpServletResponse resp, HttpServletRequest req, @Param("xzqhdm") String xzqh,
						  @Param("radar1") String picRadar1, @Param("bar2") String picBar2, @Param("barp") String picBarp) {
		log.debug("导出word文件开始>>>>>>>>>>>>>");
		//获取行政区划信息
		Sys_unit unit = sysUnitService.query(Cnd.where("unitcode","=",xzqh)).get(0);
		Map<String,Object> params = evaluateIndexService.packageObject(xzqh,unit.getXzqhmc(),Globals.EvaluateYear);

		// 传递过正中  "+" 变为了 " "
		picRadar1 = picRadar1.replaceAll(" ", "+");
		picBar2 = picBar2.replaceAll(" ", "+");
		picBarp = picBarp.replaceAll(" ", "+");
//        picBart = picBart.replaceAll(" ", "+");

		ImgBase64Util imgUtil = new ImgBase64Util();
		String tempPath = req.getSession().getServletContext().getRealPath("/tmp");
		String picRadar1Path = imgUtil.decodeBase64(picRadar1, new File(tempPath));     // 读取图片信息，返回图片保存路径
		String picBar2Path = imgUtil.decodeBase64(picBar2, new File(tempPath));     // 读取图片信息，返回图片保存路径
		String picBar55Path = imgUtil.decodeBase64(picBarp, new File(tempPath));     // 读取图片信息，返回图片保存路径
//		String picBartPath = imgUtil.decodeBase64(picBart, new File(tempPath));     // 读取图片信息，返回图片保存路径

		StatChart chart = new StatChart();
		chart.setFilePath(picRadar1Path);
		chart.setFileType("png");
		chart.setHeight(350);
		chart.setWidth(350);
		params.put("${radar1}", chart);
		chart = new StatChart();
		chart.setFilePath(picBar2Path);
		chart.setFileType("png");
		chart.setHeight(270);
		chart.setWidth(420);
		params.put("${bar2}", chart);
		chart = new StatChart();
		chart.setFilePath(picBar55Path);
		chart.setFileType("png");
		chart.setHeight(710);
		chart.setWidth(420);
		params.put("${bar55}", chart);
//		chart = new StatChart();
//		chart.setFilePath(picBartPath);
//		chart.setFileType("png");
//		chart.setHeight(320);
//		chart.setWidth(420);
//		params.put("${bar_t}", chart);
		//读入word模板

		System.out.println(getClass().getClassLoader().getResource("template/Countytemplate_"+Globals.EvaluateYear+".docx"));
		InputStream is = getClass().getClassLoader().getResourceAsStream("template/Countytemplate_"+Globals.EvaluateYear+".docx");
		try {
			String filename = "浙江省县（市、区）教育现代化发展水平报告_"+unit.getXzqhmc()+".docx";
			filename = URLEncoder.encode(filename, "UTF-8");

			XwpfUtil xwpfUtil = new XwpfUtil();
			xwpfUtil.exportWord(params,is,resp,xwpfUtil,filename);
//			xwpfUtil.exportWord(resp);
//			resp.setContentType("text/pain");
//			resp.setHeader("Content-Disposition","attachment;filename=problems.txt");
//			resp.getOutputStream().flush();
//			resp.getOutputStream().close();
			log.debug("导出word文件完成>>>>>>>>>>>>>");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}