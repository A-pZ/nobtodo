package lumi.action.tag;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lumi.service.TagService;
import lumi.vo.RegisterTagVO;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.annotations.Blocked;
import com.opensymphony.xwork2.validator.annotations.Validations;
import com.opensymphony.xwork2.validator.annotations.VisitorFieldValidator;

/**
 * タグを新規登録する。
 *
 * @author A-pZ ( Serendipity 3 ./ as sundome goes by. )
 */
@Namespace("/tag")
@ParentPackage("lumi-default")
@InterceptorRef("lumiStack")
@Results({
	// location属性に指定したhtmlファイル名は、/WEB-INF/content 以下からの相対パス。
	@Result(name = ActionSupport.SUCCESS, type="json" , params={"root","result"}),
	@Result(name=BaseTagAction.ACL_ERROR_RESULT , type="thymeleaf-spring" , location="application_error"),
	@Result(name=BaseTagAction.ACL_ERROR_AJAX_RESULT, type="json" , params={"root","message"}),
	@Result(name=ActionSupport.INPUT , type="dispatcher", location="none"),
})
@Controller
@Scope("prototype")
@Log4j2
@Validations(
	visitorFields={
		@VisitorFieldValidator(appendPrefix=false,fieldName="vo")
	}
)
public class NewTagRegisterAction extends BaseTagAction {

	/**
	 * デフォルトアクション。
	 */
	@Action(value="add" , interceptorRefs={@InterceptorRef("lumiStack")})
	public String addTag() throws Exception {
		if (! isAccessibleTask(vo)) {
			return accessControlErrorResult();
		}

		result = service.registerTag(vo);

		// Result値。ActionSupportの定数値を返すか、別途定義した値を返すこと。
		// この値は@Resultで指定したname値となる。
		return SUCCESS;
	}

	/**
	 * Actionクラスから実行する業務ロジックのServiceクラス。Autowiredがついたフィールドが自動的に対象となる。
	 */
	@Blocked
	@Autowired
	@Getter @Setter
	private TagService service;

	@Getter @Setter
	private RegisterTagVO vo;

	@Getter @Setter
	private boolean result;
}
