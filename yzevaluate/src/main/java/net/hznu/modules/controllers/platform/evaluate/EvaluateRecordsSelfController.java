package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Globals;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.common.util.XwpfUtil;
import net.hznu.modules.models.evaluate.*;
import net.hznu.modules.models.evaluate.Evaluate_records_self;
import net.hznu.modules.models.evaluate.Evaluate_remark;
import net.hznu.modules.models.monitor.Monitor_catalog;
import net.hznu.modules.models.monitor.Monitor_index;
import net.hznu.modules.models.sys.Sys_user;
import net.hznu.modules.services.evaluate.EvaluateRecordsSelfService;
import net.hznu.modules.services.evaluate.EvaluateRecordsService;
import net.hznu.modules.services.evaluate.EvaluateRemarkService;
import net.hznu.modules.services.evaluate.EvaluateSummaryService;
import net.hznu.modules.services.evaluate.EvaluateCustomService;
import net.hznu.modules.services.monitor.MonitorCatalogService;
import net.hznu.modules.services.monitor.MonitorIndexService;
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
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

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
	private EvaluateRemarkService evaluateRemarkService;
	@Inject
	private EvaluateSummaryService evaluateSummaryService;
	@Inject
	private EvaluateCustomService evaluateCustomService;

	@At("")
	@Ok("beetl:/platform/evaluate/records/self/index.html")
	@RequiresAuthentication
	public void index() {

	}
	@At
	@Ok("beetl:/platform/evaluate/records/self/index_basic.html")
	@RequiresAuthentication
	public void index_basic(HttpServletRequest req) {

	}

	@At
	@Ok("beetl:/platform/evaluate/records/self/index_develop.html")
	@RequiresAuthentication
	public void index_develop(HttpServletRequest req) {

	}

	@At
	@Ok("beetl:/platform/evaluate/records/self/index_download.html")
	@RequiresAuthentication
	public void index_download() {

	}
	@At
	@Ok("beetl:/platform/evaluate/records/self/index_upload.html")
	@RequiresAuthentication
	public void index_upload() {

	}
	//学校自评列表
	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("year") int year, @Param("taskname") String taskname,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
		//获取当前用户
		Subject subject = SecurityUtils.getSubject();
		Sys_user user = (Sys_user) subject.getPrincipal();
		//评估单位仅显示自己单位的自评记录
		if(user!=null && user.getUnit().isEvaluate()){
			cnd = Cnd.where("schoolid", "=", user.getUnit().getId());
		}
		if (year!=0) {
			cnd.and("year", "=",  year );
		}
		if (!Strings.isBlank(taskname)) {
			cnd.and("taskname", "like", "%" + taskname + "%");
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
			int totalWeights = 100;
			//这里统一按100分处理
//					totalWeights= monitorIndexService.getTotalWeights(unitType);

				if(!Strings.isBlank(schoolId)){

					Evaluate_records_self records = new Evaluate_records_self();
					records.setYear(year);
					records.setSchoolId(schoolId);
					records.setWeights(totalWeights);
					//插入评估记录
					records = evaluateRecordsSelfService.insert(records);

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
					return Result.success("system.success");
				}

		} catch (Exception e) {
			return Result.error("system.error");
		}
		return Result.error("system.error");
    }

	/**
	 * 该方法测试用，用来给已有的评估记录插入评估指标；暂时不用了
	 * @param req
	 * @return
	 */
//	@At
//	@Ok("json")
//	public Object TestDo( HttpServletRequest req) {
//		Monitor_index monitorIndex = monitorIndexService.fetch("d313ff478f12422ba89b2b4bf6b7d543");
//		List<Evaluate_records_self> recordsList = evaluateRecordsSelfService.query(Cnd.NEW());
//		for (Evaluate_records_self recordsSelf:recordsList) {
//			Evaluate_remark remark = new Evaluate_remark();
//			remark.setEvaluateId(recordsSelf.getId());
//			remark.setIndexid(monitorIndex.getId());
//			remark.setScore_s(0.0);
//			//不需要学校自评指标,默认评估完成
//			remark.setSelfeva(true);
//
//			//插入监测指标记录
//			evaluateRemarkService.insert(remark);
//		}
//		return Result.success("system.success");
//
//	}
	@At({"/submit/?"})
	@Ok("json")
	@SLog(tag = "提交学校自评记录", msg = "ID:")
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
	@SLog(tag = "删除评估记录", msg = "ID:")
	public Object delete(String id ,HttpServletRequest req) {
		try {
			Evaluate_records_self records = evaluateRecordsSelfService.fetch(id);
			if(records.isStatus_s()){
				return Result.error("已经提交审核，不能再删除评估记录");
			}
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
	/*@At("/download/?")
	@Ok("void")
	@RequiresAuthentication
	public Object download(String id, HttpServletResponse resp) {
		if (!Strings.isBlank(id)) {
			Map<String,Object> wordDataMap = packageObject(id);
			XwpfUtil xwpfUtil = new XwpfUtil();
			//读入word模板
			InputStream is = getClass().getClassLoader().getResourceAsStream("template/SelfEvaluate-yz.docx");
			try {
				String filename = "鄞州区学校发展性评价自评表.docx";
				filename = URLEncoder.encode(filename, "UTF-8");

				xwpfUtil.exportWord(wordDataMap,is,resp,filename);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}*/


	//导出自评报告
	@At({"/download","/download/?"})
	@Ok("void")
	public void download(HttpServletResponse resp, HttpServletRequest req, @Param("evalId") String evalId) {
		log.debug("导出word文件开始>>>>>>>>>>>>>");
		//获取学校信息
		Map<String,Object> paramsPara = evaluateRecordsSelfService.packageParaObject(evalId);


		//获取发展性指标
		List<Evaluate_custom> customs = evaluateCustomService.query(Cnd.where("evaluateid","=",evalId));

		InputStream is = getClass().getClassLoader().getResourceAsStream("template/SelfEvaluate-yz.docx");
		try {
			String filename = "鄞州区学校发展性评价自评表.docx";
			filename = URLEncoder.encode(filename, "UTF-8");
			XwpfUtil xwpfUtil = new XwpfUtil();
			//xwpfUtil.exportWord(resp);
			evaluateRecordsSelfService.exportWord(paramsPara,customs,is,resp,xwpfUtil,filename);

			log.debug("导出word文件完成>>>>>>>>>>>>>");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//上传自评报告和3年规划
	@At("/upload/?")
	@Ok("beetl:/platform/evaluate/records/self/upload.html")
	@RequiresAuthentication
	public void upload(String id,HttpServletRequest req) {
		req.setAttribute("id", id);
	}
	@At("/uploadDo")
	@Ok("json")
	@AdaptBy(type = WhaleAdaptor.class)
	@RequiresAuthentication
	public Object uploadDo(@Param("id") String id,@Param("selfevaurl") String selfevaurl,@Param("planurl") String planurl, HttpServletResponse resp) {
		if (!Strings.isBlank(id)) {
			try {
				Evaluate_records_self record =evaluateRecordsSelfService.fetch(id);
				record.setId(id);
				if(!Strings.isBlank(selfevaurl)) record.setSelfevaurl(selfevaurl);
				if(!Strings.isBlank(planurl)) record.setPlanurl(planurl);
				evaluateRecordsSelfService.updateIgnoreNull(record);
//				evaluateRecordsSelfService.upload(id, selfevaurl, planurl);
				return Result.success("system.success");
			} catch (Exception e) {
				return Result.error("system.error");
			}
		}
		return null;
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
