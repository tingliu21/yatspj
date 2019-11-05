package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.base.Globals;
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
import net.hznu.modules.services.evaluate.EvaluateSpecialService;
import net.hznu.modules.services.monitor.MonitorCatalogService;
import net.hznu.modules.services.monitor.MonitorIndexService;
import net.hznu.modules.services.sys.SysUnitService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

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
	private EvaluateSpecialService evaluateSpecialService;
	@Inject
	protected Dao dao;
	@Inject
	private SysUnitService sysUnitService;

	@At
	@Ok("re")
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
				//req.setAttribute("cataList",cataList);
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
		if(StringUtils.isNotBlank(catacode)){
			cnd = cnd.and("catacode","like",catacode+"%");
		}
		if(StringUtils.isNotBlank(code)){
			cnd = cnd.and("code","like",code+"%");
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
			req.setAttribute("evaluateId",evaluateId);
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
				Evaluate_records record = evaluateRecordsService.fetch(Cnd.where("year","=",Globals.EvaluateYear).and("unitcode","=",sysUnit.getUnitcode()));
				req.setAttribute("evaluateId",record.getId());
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
		req.setAttribute("cityList",sysUnitService.query(Cnd.where("level","=",2).and("unitcode","like","33%").asc("unitcode")));
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
		MonitorStat monitorStat = evaluateIndexService.getScoreByXZQH(viewname, fieldnames, xzqh,"" ).get(0);
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
		MonitorStat monitorStat = evaluateIndexService.getScoreByXZQH(viewname, fieldnames, xzqh,"" ).get(0);
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
		MonitorStat monitorStat = evaluateIndexService.getScoreByXZQH(viewname, fieldnames, xzqh, "").get(0);
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
	//地市的总分排名
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getCityTotalScore(@Param("xzqhdm") String xzqh) {
		List<MonitorSumValue> monitorSumValueList = evaluateRecordsService.getCityAvgTotalScore(Globals.EvaluateYear,xzqh);

		return monitorSumValueList;
	}
	//特殊地区排名
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getTotalScoreByDevelop(@Param("develope") String develope) {
		List<MonitorSumValue> monitorSumValueList = evaluateRecordsService.getTotalScore(Globals.EvaluateYear,"develop",develope);
		return monitorSumValueList;
	}
	//重点帮扶县排名
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getTotalScoreByKey() {
		List<MonitorSumValue> monitorSumValueList = evaluateRecordsService.getTotalScore(Globals.EvaluateYear,"keynote",true);
		return monitorSumValueList;
	}
	//县排名
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object getTotalScoreByCity(@Param("year")int year,@Param("xzqhdm") String xzqh) {
		if (year == 0) {
			year = Globals.EvaluateYear;
		}
		List<MonitorSumValue> monitorSumValueList = evaluateRecordsService.getTotalScore(year,xzqh);
		return monitorSumValueList;
	}
	//导出县报告
	@At
	@Ok("void")
	public void exportCounty(HttpServletResponse resp, HttpServletRequest req, @Param("evaluateId") String evaluateId,
						  @Param("radar1") String picRadar1, @Param("bar2") String picBar2, @Param("barp") String picBarp, @Param("bark") String picBark) {
		log.debug("导出word文件开始>>>>>>>>>>>>>");
		//获取行政区划信息
		Evaluate_records record = evaluateRecordsService.fetch(evaluateId);
		Map<String,Object> paramsPara = evaluateIndexService.packageParaObject(evaluateId);
		Map<String,Object> paramsTable = evaluateIndexService.packageTableObject(record.getUnitcode(),record.getXzqhmc(),Globals.EvaluateYear);

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
		paramsPara.put("${radar1}", chart);
		chart = new StatChart();
		chart.setFilePath(picBar2Path);
		chart.setFileType("png");
		chart.setHeight(270);
		chart.setWidth(420);
		paramsPara.put("${bar2}", chart);
		chart = new StatChart();
		chart.setFilePath(picBar55Path);
		chart.setFileType("png");
		chart.setHeight(710);
		chart.setWidth(420);
		paramsPara.put("${barp}", chart);
		chart = new StatChart();
		chart.setFilePath(picBark);
		chart.setFileType("png");
		chart.setHeight(470);
		chart.setWidth(420);
		paramsPara.put("${bar_key}", chart);
		//读入word模板

		System.out.println(getClass().getClassLoader().getResource("template/CountyTemplate_"+Globals.EvaluateYear+".docx"));
		InputStream is = getClass().getClassLoader().getResourceAsStream("template/CountyTemplate_"+Globals.EvaluateYear+".docx");
		try {
			Evaluate_records rcd = evaluateRecordsService.fetch(evaluateId);
			String unitid = rcd.getUnitID();
			Sys_unit unit = sysUnitService.fetch(unitid);
			String xzqhmc = unit.getXzqhmc();
			String filename = "浙江省县（市、区）教育现代化发展水平报告_" + xzqhmc + ".docx";
			filename = URLEncoder.encode(filename, "UTF-8");

			XwpfUtil xwpfUtil = new XwpfUtil();
			xwpfUtil.exportWord(paramsPara,paramsTable,is,resp,xwpfUtil,filename);

			log.debug("导出word文件完成>>>>>>>>>>>>>");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//导出省报告
	@At
	@Ok("void")
	public void exportProvince(HttpServletResponse resp, HttpServletRequest req, @Param("xzqhdm") String xzqhdm,
							 @Param("radar1") String picRadar1, @Param("bar2") String picBar2, @Param("barp") String picBarp, @Param("bar_key") String picBarkey) {
		log.debug("导出word文件开始>>>>>>>>>>>>>");
		//获取行政区划信息
		//String xzqhdm="330000";
		Map<String,Object> paramsPara = new HashMap<String, Object>();
		//报告生成日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
		paramsPara.put("${date}",sdf.format(new Date()));


		// 传递过正中  "+" 变为了 " "
		picRadar1 = picRadar1.replaceAll(" ", "+");
		picBar2 = picBar2.replaceAll(" ", "+");
		picBarp = picBarp.replaceAll(" ", "+");
		picBarkey = picBarkey.replaceAll(" ", "+");

		ImgBase64Util imgUtil = new ImgBase64Util();
		String tempPath = req.getSession().getServletContext().getRealPath("/tmp");
		String picRadar1Path = imgUtil.decodeBase64(picRadar1, new File(tempPath));     // 读取图片信息，返回图片保存路径
		String picBar2Path = imgUtil.decodeBase64(picBar2, new File(tempPath));     // 读取图片信息，返回图片保存路径
		String picBarpPath = imgUtil.decodeBase64(picBarp, new File(tempPath));     // 读取图片信息，返回图片保存路径
		String picBarkeyPath = imgUtil.decodeBase64(picBarkey, new File(tempPath));     // 读取图片信息，返回图片保存路径

		StatChart chart = new StatChart();
		chart.setFilePath(picRadar1Path);
		chart.setFileType("png");
		chart.setHeight(350);
		chart.setWidth(350);
		paramsPara.put("${radar1}", chart);
		chart = new StatChart();
		chart.setFilePath(picBar2Path);
		chart.setFileType("png");
		chart.setHeight(270);
		chart.setWidth(420);
		paramsPara.put("${bar2}", chart);
		chart = new StatChart();
		chart.setFilePath(picBarpPath);
		chart.setFileType("png");
		chart.setHeight(710);
		chart.setWidth(420);
		paramsPara.put("${barp}", chart);
		chart = new StatChart();
		chart.setFilePath(picBarkeyPath);
		chart.setFileType("png");
		chart.setHeight(470);
		chart.setWidth(420);
		paramsPara.put("${bar_key}", chart);
		//读入word模板

		List<MonitorStat> result1 = new ArrayList<>();

		String viewname = "v_xzqh_evaluate1_" + Globals.EvaluateYear;
		//获取一级指标
		List<Monitor_catalog> catalogs = monitorCatalogService.query(Cnd.where("level", "=", 1).
				and("year", "=", Globals.EvaluateYear).and("isshow", "=", true).asc("catacode"));
		List<String> fieldnames = new ArrayList<>(catalogs.size());
		fieldnames.add("t_score");
		for (Monitor_catalog catalog : catalogs) {
			String fieldname = "index_" + catalog.getCatacode();
			fieldnames.add(fieldname);
		}
		//县得分
		result1 = evaluateIndexService.getScoreByXZQH(viewname, fieldnames, xzqhdm, "desc");


		List<MonitorStat> result2 = new ArrayList<>();

		viewname = "v_xzqh_evaluate2_" + Globals.EvaluateYear;
		//获取二级指标
		catalogs = monitorCatalogService.query(Cnd.where("level", "=", 2).
				and("year", "=", Globals.EvaluateYear).and("isshow", "=", true).asc("catacode"));
		fieldnames = new ArrayList<>(catalogs.size());
		fieldnames.add("t_score");
		for (Monitor_catalog catalog : catalogs) {
			String fieldname = "index_" + catalog.getCatacode();
			fieldnames.add(fieldname);
		}
		//县得分
		result2 = evaluateIndexService.getScoreByXZQH(viewname, fieldnames, xzqhdm,"desc" );



		InputStream is = getClass().getClassLoader().getResourceAsStream("template/Provincetemplate_"+Globals.EvaluateYear+".docx");
		try {
			String filename = "浙江省县（市、区）教育现代化发展水平报告_"+Globals.EvaluateYear+".docx";
			filename = URLEncoder.encode(filename, "UTF-8");

			XwpfUtil xwpfUtil = new XwpfUtil();
			//xwpfUtil.exportWord(resp);
			evaluateIndexService.exportWord(paramsPara,result1,result2,is,resp,xwpfUtil,filename);

			log.debug("导出word文件完成>>>>>>>>>>>>>");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@At
	@Ok("json")
	@RequiresAuthentication
	public Object get(@Param("unitcode") String unitcode, @Param("year") int year) {
		Cnd cnd = Strings.isBlank(unitcode) ? null : Cnd.where("unitcode", "=", unitcode.trim());
		if (year != 0) {
			if (cnd == null)
				cnd = Cnd.where("year", "=", year);
			else
				cnd = cnd.and("year", "=", year);
		}
		// 获得某年某行政区的所有监测指标值及得分
		List<Evaluate_index> list = dao.query(Evaluate_index.class, cnd, null);

		return list;
	}
}