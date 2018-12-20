package cn.wizzer.modules.controllers.platform.basicdata;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.basicdata.Bascidata_teachertraining;
import cn.wizzer.modules.services.basicdata.BascidataTeachertrainingService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
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
@At("/platform/basicdata/teachertraining")
@Filters({@By(type = PrivateFilter.class)})
public class BascidataTeachertrainingController {
	private static final Log log = Logs.get();
	@Inject
	private BascidataTeachertrainingService bascidataTeachertrainingService;

	@At("")
	@Ok("beetl:/platform/basicdata/teachertraining/index.html")
	@RequiresAuthentication
	public void index() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return bascidataTeachertrainingService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/basicdata/teachertraining/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建Bascidata_teachertraining", msg = "")
    public Object addDo(@Param("..") Bascidata_teachertraining bascidataTeachertraining, HttpServletRequest req) {
		try {
			bascidataTeachertrainingService.insert(bascidataTeachertraining);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/basicdata/teachertraining/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return bascidataTeachertrainingService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Bascidata_teachertraining", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Bascidata_teachertraining bascidataTeachertraining, HttpServletRequest req) {
		try {

			bascidataTeachertraining.setOpAt((int) (System.currentTimeMillis() / 1000));
			bascidataTeachertrainingService.updateIgnoreNull(bascidataTeachertraining);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Bascidata_teachertraining", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				bascidataTeachertrainingService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				bascidataTeachertrainingService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/basicdata/teachertraining/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return bascidataTeachertrainingService.fetch(id);

		}
		return null;
    }

}
