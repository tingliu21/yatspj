package cn.wizzer.modules.controllers.platform.evaluate;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.evaluate.Evaluate_records;
import cn.wizzer.modules.models.evaluate.Evaluate_remark;
import cn.wizzer.modules.models.evaluate.Evaluate_special;
import cn.wizzer.modules.models.monitor.Monitor_catalog;
import cn.wizzer.modules.models.sys.Sys_role;
import cn.wizzer.modules.models.sys.Sys_user;
import cn.wizzer.modules.services.evaluate.EvaluateRecordsService;
import cn.wizzer.modules.services.evaluate.EvaluateRemarkService;
import cn.wizzer.modules.services.evaluate.EvaluateSpecialService;
import cn.wizzer.modules.services.monitor.MonitorCatalogService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@IocBean
@At("/platform/evaluate/remark")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateRemarkController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateRemarkService evaluateRemarkService;
	@Inject
	private EvaluateRecordsService evaluateRecordsService;
	@Inject
	private EvaluateSpecialService evaluateSpecialService;
	@Inject
	private MonitorCatalogService monitorCatalogService;

	@At({"","/?"})
	@Ok("beetl:/platform/evaluate/remark/${req_attr.type}/index.html")
	@RequiresAuthentication
	//type取值有2个，self和special
	//cType为指标目录类别，1为基础性指标，2为规范性指标，3为发展性指标
	public void index(String type,@Param("evaluateId") String evaluateId,@Param("unitType") String unitType,@Param("cType") String cType,HttpServletRequest req) {
		Evaluate_records evaluate = evaluateRecordsService.fetch(evaluateId);
		evaluate = evaluateRecordsService.fetchLinks(evaluate,"school");
		req.setAttribute("schoolname",evaluate.getSchool().getName());
		req.setAttribute("evaluateId",evaluateId);
		req.setAttribute("unitType",unitType);
		req.setAttribute("type", type);
		req.setAttribute("cType",cType);
	}
	@At({"index_standard","/?/index_standard"})
	@Ok("beetl:/platform/evaluate/remark/${req_attr.type}/index_standard.html")
	@RequiresAuthentication
	public void index_standard(String type,@Param("evaluateId") String evaluateId,@Param("unitType") String unitType,@Param("cType") String cType,HttpServletRequest req) {
		req.setAttribute("evaluateId",evaluateId);
		req.setAttribute("unitType",unitType);
		req.setAttribute("type", type);
		req.setAttribute("ctype",cType);
	}

	@At({"index_develop","/?/index_develop"})
	@Ok("beetl:/platform/evaluate/remark/${req_attr.type}/index_develop.html")
	@RequiresAuthentication
	public void index_develop(String type,@Param("evaluateId") String evaluateId,@Param("unitType") String unitType,@Param("cType") String cType,HttpServletRequest req) {
		req.setAttribute("evaluateId",evaluateId);
		req.setAttribute("unitType",unitType);
		req.setAttribute("type", type);
		req.setAttribute("ctype",3);
	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("catalogId") String catalogId,@Param("evaluateId") String evaluateId,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {

		Cnd cnd = Cnd.NEW();
		if (!Strings.isBlank(evaluateId) && !"0".equals(evaluateId)) {
			List<String> strids = new ArrayList<>();
			//先获取2级指标下的所有3级指标
			List<Monitor_catalog> catalogs = monitorCatalogService.query(Cnd.where("parentId", "=", catalogId));
			for (Monitor_catalog catalog:catalogs) {
				if (catalog.getLevel()==3){
					strids.add(catalog.getId());
				}
			}
			strids.add(catalogId);
			cnd.and("evaluateId", "like", "%" + evaluateId + "%").and("catalogId", "in", strids).asc("location");;
			return evaluateRemarkService.data(length, start, draw, order, columns, cnd, "index");
		}
		return null;

    }
    //专家评估指标，专家可看5个指标，但只能编辑自己负责的
    @At
	@Ok("json:full")
	@RequiresPermissions("evaluate.verify.special")
	public Object specialdata(@Param("evaluateId") String evaluateId,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns){

		if (!Strings.isBlank(evaluateId) && !"0".equals(evaluateId)) {

			Cnd cnd = Cnd.where("depttype","=","Special");
			cnd.and("evaluateId", "like", "%" + evaluateId + "%").asc("location");

			return evaluateRemarkService.data(length, start, draw, order, columns, cnd, "index");
		}
		return null;
	}
	//专家只列出自己负责的指标
	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object specialdata_old(@Param("evaluateId") String evaluateId,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns){

		if (!Strings.isBlank(evaluateId) && !"0".equals(evaluateId)) {
			//获取专家id
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();
			List<Evaluate_special> evaluate_specials = evaluateSpecialService.query(Cnd.where("specialid","=",user.getId()).and("evaluateid","=",evaluateId));
			Set<String> indexSet = new HashSet<String>();
			for(int i =0;i<evaluate_specials.size();i++){
				indexSet.add(evaluate_specials.get(i).getIndexId());
				//set可以去除重复的evaluateid
			}
			String[] indexids = new String[indexSet.size()];
			//Set-->数组
			indexSet.toArray(indexids);
			Cnd cnd = Cnd.where("indexid","in",indexids);
			cnd.and("evaluateId", "like", "%" + evaluateId + "%").asc("location");

			return evaluateRemarkService.data(length, start, draw, order, columns, cnd, "index");
		}
		return null;
	}
	//暂不新增，通过初始化新增
    @At
    @Ok("beetl:/platform/evaluate/remark/add.html")
    @RequiresAuthentication
    public void add() {

    }

	//暂不新增，通过初始化新增
    @At
    @Ok("json")
    @SLog(tag = "新建Evaluate_remark", msg = "")
    public Object addDo(@Param("..") Evaluate_remark evaluateRemark, HttpServletRequest req) {
		try {
			evaluateRemarkService.insert(evaluateRemark);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }
	//自评
	@At("/selfeva/?")
    @Ok("beetl:/platform/evaluate/remark/schooledit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		Evaluate_remark remark = evaluateRemarkService.fetch(id);
		return evaluateRemarkService.fetchLinks(remark,"index");
    }

	//自评
	@At("/selfevaDo")
    @Ok("json")
    @SLog(tag = "修改Evaluate_remark", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Evaluate_remark evaluateRemark,@Param("picurls") String[] picurls, HttpServletRequest req) {
		try {

			Evaluate_records records = evaluateRecordsService.fetch(evaluateRemark.getEvaluateId());
			if(records.isStatus_s()){
				return Result.error("已经提交审核，不能再修改评价了");
			}

			evaluateRemark.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateRemark.setSelfeva(true);//自评完成
			evaluateRemarkService.updateIgnoreNull(evaluateRemark);

			//上传附件，暂时没写好 2019-6-10 Liut

			//修改records记录
			Evaluate_records evaluateRecords = evaluateRecordsService.fetch(evaluateRemark.getEvaluateId());
			double progress = evaluateRecordsService.getProgress_s(evaluateRemark.getEvaluateId());
			evaluateRecords.setProgress_s(progress);

			//统计分数
			evaluateRecords.setScore_s(evaluateRecordsService.getTotalScore_s(evaluateRemark.getEvaluateId()));
//			//确定是否完成自评
//			if(progress==1.0){
//				evaluateRecords.setStatus_s(true);
//			}
			evaluateRecordsService.updateIgnoreNull(evaluateRecords);

			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

	//部门审核
	@At("/depteva/?")
	@Ok("beetl:/platform/evaluate/remark/edit.html")
	@RequiresAuthentication
	public Object edit_verify(String id) {
		Evaluate_remark remark = evaluateRemarkService.fetch(id);
		return evaluateRemarkService.fetchLinks(remark,"index");
	}
	//专家审核
	@At("/speceva/?")
	@Ok("beetl:/platform/evaluate/remark/special/edit.html")
	@RequiresAuthentication
	public Object edit_special(String id) {
		Evaluate_remark remark = evaluateRemarkService.fetch(id);
		return evaluateRemarkService.fetchLinks(remark,"index");
	}
	//部门审核、专家审核
	@At("/deptevaDo")
	@Ok("json")
	@SLog(tag = "修改Evaluate_remark", msg = "ID:${args[0].id}")
	public Object editDo_verify(@Param("..") Evaluate_remark evaluateRemark, HttpServletRequest req) {
		try {
			evaluateRemark.setOpBy(Strings.sNull(req.getAttribute("uid")));
			evaluateRemark.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateRemark.setVerifyeva(true);//部门审核完成
			evaluateRemarkService.updateIgnoreNull(evaluateRemark);

			//修改records记录
			Evaluate_records evaluateRecords = evaluateRecordsService.fetch(evaluateRemark.getEvaluateId());
			double progress = evaluateRecordsService.getProgress_p(evaluateRemark.getEvaluateId());
			evaluateRecords.setProgress_p(progress);

			//统计分数
			evaluateRecords.setScore_p(evaluateRecordsService.getTotalScore_p(evaluateRemark.getEvaluateId()));
			//确定是否完成自评
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
    @SLog(tag = "删除Evaluate_remark", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				evaluateRemarkService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				evaluateRemarkService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/evaluate/remark/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {

			Evaluate_remark remark = evaluateRemarkService.fetch(id);
			return evaluateRemarkService.fetchLinks(remark,"index");

		}
		return null;
    }

}
