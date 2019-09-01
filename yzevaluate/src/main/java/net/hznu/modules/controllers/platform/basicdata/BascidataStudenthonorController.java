package net.hznu.modules.controllers.platform.basicdata;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.basicdata.Bascidata_studenthonor;
import net.hznu.modules.services.basicdata.BascidataStudenthonorService;
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
@At("/platform/basicdata/studenthonor")
@Filters({@By(type = PrivateFilter.class)})
public class BascidataStudenthonorController {
	private static final Log log = Logs.get();
	@Inject
	private BascidataStudenthonorService bascidataStudenthonorService;

	@At("")
	@Ok("beetl:/platform/basicdata/studenthonor/index.html")
	@RequiresAuthentication
	public void index() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return bascidataStudenthonorService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/basicdata/studenthonor/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建Bascidata_studenthonor", msg = "")
    public Object addDo(@Param("..") Bascidata_studenthonor bascidataStudenthonor, HttpServletRequest req) {
		try {
			bascidataStudenthonorService.insert(bascidataStudenthonor);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/basicdata/studenthonor/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return bascidataStudenthonorService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Bascidata_studenthonor", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Bascidata_studenthonor bascidataStudenthonor, HttpServletRequest req) {
		try {

			bascidataStudenthonor.setOpAt((int) (System.currentTimeMillis() / 1000));
			bascidataStudenthonorService.updateIgnoreNull(bascidataStudenthonor);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Bascidata_studenthonor", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				bascidataStudenthonorService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				bascidataStudenthonorService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/basicdata/studenthonor/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return bascidataStudenthonorService.fetch(id);

		}
		return null;
    }

}
