package net.hznu.modules.controllers.platform.basicdata;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.basicdata.Bascidata_moraleducation;
import net.hznu.modules.services.basicdata.BascidataMoraleducationService;
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
@At("/platform/basicdata/moraleducation")
@Filters({@By(type = PrivateFilter.class)})
public class BascidataMoraleducationController {
	private static final Log log = Logs.get();
	@Inject
	private BascidataMoraleducationService bascidataMoraleducationService;

	@At("")
	@Ok("beetl:/platform/basicdata/moraleducation/index.html")
	@RequiresAuthentication
	public void index() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return bascidataMoraleducationService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/basicdata/moraleducation/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建Bascidata_moraleducation", msg = "")
    public Object addDo(@Param("..") Bascidata_moraleducation bascidataMoraleducation, HttpServletRequest req) {
		try {
			bascidataMoraleducationService.insert(bascidataMoraleducation);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/basicdata/moraleducation/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return bascidataMoraleducationService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Bascidata_moraleducation", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Bascidata_moraleducation bascidataMoraleducation, HttpServletRequest req) {
		try {

			bascidataMoraleducation.setOpAt((int) (System.currentTimeMillis() / 1000));
			bascidataMoraleducationService.updateIgnoreNull(bascidataMoraleducation);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Bascidata_moraleducation", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				bascidataMoraleducationService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				bascidataMoraleducationService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/basicdata/moraleducation/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return bascidataMoraleducationService.fetch(id);

		}
		return null;
    }

}
