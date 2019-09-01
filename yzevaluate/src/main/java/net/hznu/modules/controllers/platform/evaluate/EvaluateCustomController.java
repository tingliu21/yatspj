package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.evaluate.Evaluate_appendix;
import net.hznu.modules.models.evaluate.Evaluate_custom;
import net.hznu.modules.models.evaluate.Evaluate_records;
import net.hznu.modules.services.evaluate.EvaluateAppendixService;
import net.hznu.modules.services.evaluate.EvaluateCustomService;
import net.hznu.modules.services.evaluate.EvaluateRecordsService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/evaluate/custom")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateCustomController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateCustomService evaluateCustomService;
	@Inject
	private EvaluateRecordsService evaluateRecordsService;
	@Inject
	private EvaluateAppendixService evaluateAppendixService;
/*	@At("")
	@Ok("beetl:/platform/evaluate/custom/index.html")
	@RequiresAuthentication
	public void index() {

	}*/
	@At({"","/?"})
	@Ok("beetl:/platform/evaluate/custom/${req_attr.type}/index.html")
	@RequiresAuthentication
	//type取值有2个，self和special
	public void index(String type,@Param("evaluateId") String evaluateId,@Param("unitType") String unitType,HttpServletRequest req) {
		Evaluate_records evaluate = evaluateRecordsService.fetch(evaluateId);
		evaluate = evaluateRecordsService.fetchLinks(evaluate,"school");
		req.setAttribute("schoolname",evaluate.getSchool().getName());
		req.setAttribute("evaluateId",evaluateId);
		req.setAttribute("unitType",unitType);
		req.setAttribute("type", type);

	}
	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("evaluateId") String evaluateId, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
		if (!Strings.isBlank(evaluateId) && !"0".equals(evaluateId)) {
			cnd.and("evaluateId", "like", "%" + evaluateId + "%").asc("location");;
    		return evaluateCustomService.data(length, start, draw, order, columns, cnd, null);

		}
		return null;
    }

    @At
    @Ok("beetl:/platform/evaluate/custom/add.html")
    @RequiresAuthentication
    public void add(@Param("evaluateId") String evaluateId, HttpServletRequest req) {
		req.setAttribute("evaluateId",evaluateId);
    }

    @At
    @Ok("json")
    @SLog(tag = "新建Evaluate_custom", msg = "")
	@AdaptBy(type = WhaleAdaptor.class)
    public Object addDo(@Param("..") Evaluate_custom evaluateCustom, @Param("apurls") String[] apurls, @Param("apnames") String[] apnames, HttpServletRequest req) {
		try {
			Evaluate_records evaluateRecords = evaluateRecordsService.fetch(evaluateCustom.getEvaluateId());
			if(evaluateRecords.isStatus_s()){
				return Result.error("已经提交审核，不能再修改评价了");
			}
			double totalCusWeights = evaluateCustomService.getTotalWeights(evaluateCustom.getEvaluateId(),"")+evaluateCustom.getWeights();
			if(totalCusWeights>10){
				return Result.error("发展性指标不能超过10分");
			}
			evaluateCustom.setOpBy(Strings.sNull(req.getAttribute("uid")));
			evaluateCustom.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateCustom.setSelfeva(true);//学校自评完成
			evaluateCustomService.insert(evaluateCustom);
			//上传附件 2019-7-04 Liut
			if(apurls!=null&&apurls.length>0) {
				for (int i=0;i<apurls.length;i++) {
					Evaluate_appendix appendix = new Evaluate_appendix();
					appendix.setEvaluateid(evaluateCustom.getEvaluateId());
					appendix.setRemarkid(evaluateCustom.getId());
					appendix.setApname(apnames[i]);
					appendix.setApurl(apurls[i]);
					evaluateAppendixService.insert(appendix);

				}
			}
			//修改records记录
			double progress = evaluateRecordsService.getProgress_s(evaluateCustom.getEvaluateId());
			evaluateRecords.setProgress_s(progress);

			//统计分数
			evaluateRecords.setScore_s(evaluateRecordsService.getTotalScore_s(evaluateCustom.getEvaluateId()));
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

    //自评
    @At("/edit/?")
    @Ok("beetl:/platform/evaluate/custom/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		Evaluate_custom custom = evaluateCustomService.fetch(id);
		return evaluateCustomService.fetchLinks(custom,"appendixList");
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Evaluate_custom", msg = "ID:${args[0].id}")
	@AdaptBy(type = WhaleAdaptor.class)
    public Object editDo(@Param("..") Evaluate_custom evaluateCustom, @Param("apurls") String[] apurls, @Param("apnames") String[] apnames,HttpServletRequest req) {
		try {
			Evaluate_records evaluateRecords = evaluateRecordsService.fetch(evaluateCustom.getEvaluateId());
			if(evaluateRecords.isStatus_s()){
				return Result.error("已经提交审核，不能再修改评价了");
			}
			double totalCusWeights = evaluateCustomService.getTotalWeights(evaluateCustom.getEvaluateId(),evaluateCustom.getId())+evaluateCustom.getWeights();
			if(totalCusWeights>10){
				return Result.error("发展性指标不能超过10分");
			}
			evaluateCustom.setOpBy(Strings.sNull(req.getAttribute("uid")));
			evaluateCustom.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateCustom.setSelfeva(true);//学校自评完成
			evaluateCustomService.updateIgnoreNull(evaluateCustom);
			//上传附件 2019-7-04 Liut
			if(apurls!=null&&apurls.length>0) {
				for (int i=0;i<apurls.length;i++) {
					Evaluate_appendix appendix = new Evaluate_appendix();
					appendix.setEvaluateid(evaluateCustom.getEvaluateId());
					appendix.setRemarkid(evaluateCustom.getId());
					appendix.setApname(apnames[i]);
					appendix.setApurl(apurls[i]);
					evaluateAppendixService.insert(appendix);

				}
			}
			//修改records记录
			double progress = evaluateRecordsService.getProgress_s(evaluateCustom.getEvaluateId());
			evaluateRecords.setProgress_s(progress);

			//统计分数
			evaluateRecords.setScore_s(evaluateRecordsService.getTotalScore_s(evaluateCustom.getEvaluateId()));
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
	//专家审核
	@At("/speceva/?")
	@Ok("beetl:/platform/evaluate/custom/special/edit.html")
	@RequiresAuthentication
	public Object edit_special(String id) {
		Evaluate_custom custom = evaluateCustomService.fetch(id);
		return evaluateCustomService.fetchLinks(custom,"appendixList");
	}
	//部门审核、专家审核
	@At("/deptevaDo")
	@Ok("json")
	@SLog(tag = "修改Evaluate_remark", msg = "ID:${args[0].id}")
	public Object editDo_verify(@Param("..") Evaluate_custom evaluateCustom, HttpServletRequest req) {
		try {
			evaluateCustom.setOpBy(Strings.sNull(req.getAttribute("uid")));
			evaluateCustom.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateCustom.setVerifyeva(true);//部门审核完成
			evaluateCustomService.updateIgnoreNull(evaluateCustom);

			//修改records记录
			Evaluate_records evaluateRecords = evaluateRecordsService.fetch(evaluateCustom.getEvaluateId());
			double progress = evaluateRecordsService.getProgress_p(evaluateCustom.getEvaluateId());
			evaluateRecords.setProgress_p(progress);

			//统计分数
			evaluateRecords.setScore_p(evaluateRecordsService.getTotalScore_p(evaluateCustom.getEvaluateId()));
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

    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Evaluate_custom", msg = "ID:")
    public Object delete(String id,HttpServletRequest req) {
		try {

			evaluateCustomService.deleteAndChild(id);
			req.setAttribute("id", id);

			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/evaluate/custom/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			Evaluate_custom custom = evaluateCustomService.fetch(id);
			return evaluateCustomService.fetchLinks(custom,"appendixList");
//			return evaluateCustomService.fetch(id);

		}
		return null;
    }

}
