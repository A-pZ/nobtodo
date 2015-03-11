/**
 *
 */
package lumi.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import lumi.service.AccountService;
import lumi.vo.AccountVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * SpringSecurity認証完了後の処理を実装するハンドラクラス。
 * @author A-pZ
 *
 */
@Slf4j
public class SuccessHandler implements AuthenticationSuccessHandler {

	/**
	 * 認証処理成功後の処理
	 *
	 * @param request サーブレットリクエスト
	 * @param response サーブレットレスポンス
	 * @param authentication SpringSecirity認証情報
	 * @throws IOException サーブレット情報に関するI/O例外
	 * @throws ServletException サーブレット情報に関するサーブレット例外
	 * @see org.springframework.security.web.authentication.AuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		// 認証したユーザID
		String userId = authentication.getName();
		log.debug("authenticated userId : " + userId);
		accountService.setUserId(userId);

		// ユーザの検索をする
		AccountVO vo = getAccountInfo();
		log.debug(" - accountVO : " + vo);

		// セッションにユーザ情報を格納する
		HttpSession session = request.getSession(false);
		if ( session == null) {
			throw new ServletException("HttpSession is null.");
		}

		session.setAttribute("displayName", vo.getDisplayName());

		response.sendRedirect(request.getContextPath().concat("/list"));
	}

	/**
	 * アカウント情報を取得する。
	 * @return アカウント情報(AccountVO)
	 * @throws ServletException
	 */
	AccountVO getAccountInfo() throws ServletException {
		AccountVO vo = null;

		try {
			vo = accountService.getAccountInfo();
		} catch(Exception ex) {
			throw new ServletException(ex);
		}

		return vo;
	}

	@Autowired
	AccountService accountService;
}
