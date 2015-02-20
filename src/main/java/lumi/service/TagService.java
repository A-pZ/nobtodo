package lumi.service;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import lumi.dao.DAO;
import lumi.vo.TagVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * タグServiceクラス。
 *
 * @author A-pZ ( Serendipity 3 ./ as sundome goes by. )
 *
 */
@Scope("singleton")
@Service
@Transactional(
	    propagation = Propagation.REQUIRED,
	    isolation = Isolation.DEFAULT,
	    readOnly = false,
	    rollbackFor = { RuntimeException.class, Exception.class })
@Slf4j
public class TagService extends LumiService {

	/**
	 * 指定したタスクがもつタグ一覧を返す。
	 * もしタスクを指定しない場合は、タスクに存在しうるすべてのタグを返す。
	 * @param vo 検索条件(TagVO)
	 * @return 検索結果のList
	 * @throws Exception
	 */
	public List<TagVO> getTaglistOfTask(TagVO vo) throws Exception {

		List<TagVO> result = dao.select(Query.getTagInTask.name(), vo);

		return result;
	}

	/**
	 * 新しいタグを登録する。
	 * @param vo 新しいタグ情報(表示名のみ)
	 * @return 登録成否のboolean
	 * @throws Exception
	 */
	public boolean registerTag(TagVO vo) throws Exception {

		// タグIDの生成
		vo.setTagid( generateNewTagId() );

		int count = dao.insert(Query.registerTag.name(), vo);

		//
		linkTagForTask(vo);

		return isSingleRow(count);
	}

	/**
	 * 1つのタスクにタグを登録する。
	 * @param vo 対象タスクとタグ情報
	 * @return 登録成否のboolean
	 * @throws Exception
	 */
	public boolean linkTagForTask(TagVO vo) throws Exception {

		int count = dao.insert(Query.registerTagForTask.name(), vo);

		return isSingleRow(count);
	}

	/**
	 * 1つのタスクから指定したタグを削除する。
	 * @param vo 対象タスクとタグ情報
	 * @return 削除成否のboolean
	 * @throws Exception
	 */
	public boolean dropTagForTask(TagVO vo) throws Exception {

		int count = dao.delete(Query.removeTagForTask.name(), vo);

		return isSingleRow(count);
	}

	/**
	 * 新しいタグIDを生成する。
	 * @return
	 * @throws Exception
	 */
	String generateTagId() throws Exception {

		Timestamp ts = systemTimestampService.getSystemTimestamp();

		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		sha256.update( ts.toString().getBytes());

		byte[] digest = sha256.digest();

		StringBuilder sb = new StringBuilder();
		for ( int i=0;i<digest.length;i++ ) {
			sb.append(String.format("%02x", digest[i]));
		}

		log.debug("- gen.:".concat(sb.toString()) );

		return sb.toString();
	}

	/**
	 * 重複しないタグIDを生成する。
	 * @return 新規生成したタグID
	 * @throws Exception
	 */
	String generateNewTagId() throws Exception {
		log.debug("TagId gen...");

		String genId = generateTagId();
		TagVO vo = new TagVO();
		vo.setTagid(genId);

		int exist = (Integer)dao.selectObject(Query.existTag.name(), vo);

		return ( exist != 0 ) ? generateNewTagId() : genId;
	}

	/**
	 * 単一行であればtrueを返す。
	 * @param count 登録・更新行数のint
	 * @return 1であればtrue
	 */
	boolean isSingleRow(int count) {
		return ( count == 1);
	}

	/**
	 * DAOの指定。Mybatisを利用してデータベースアクセスを実行する。
	 */
	@Autowired
	private DAO dao;

	/**
	 * Mybatisで定義するSQLのSQL-ID。
	 * @author A-pZ ( Serendipity 3 ./ as sundome goes by. )
	 *
	 */
	public enum Query {
		getTagInTask , registerTag , registerTagForTask , removeTagForTask , existTag ,
	}

	@Autowired
	private SystemTimestampService systemTimestampService;
}