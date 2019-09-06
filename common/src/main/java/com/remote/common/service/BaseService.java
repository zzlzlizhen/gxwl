package com.remote.common.service;

import java.util.List;

import com.remote.common.dao.BaseDao;
import com.remote.common.model.BaseModel;
import com.remote.pageutil.Page;
/**
 * 公共service接口实现类
 * @author zsm
 *
 * @param <M>
 * @param <QM>
 */
public class BaseService<M, QM extends BaseModel> implements IBaseServic<M, QM>{
	BaseDao dao = null;
	public void setDao(BaseDao dao) {
		this.dao = dao;
	}
	@Override
	public void create(M m) {
		// TODO Auto-generated method stub
		 dao.create(m);
	}

	@Override
	public void update(M m) {
		// TODO Auto-generated method stub
		dao.update(m);
	}

	@Override
	public void delete(Long uuid) {
		// TODO Auto-generated method stub
		dao.delete(uuid);
	}

	@Override
	public M getByUuid(Long uuid) {
		// TODO Auto-generated method stub
		return (M) dao.getByUuid(uuid);
	}

	@Override
	public Page<M> getByConditionPage(QM qm) {
		List<M> list = dao.getByConditionPage(qm);
		qm.getPage().setResult(list);;
		return qm.getPage();
	}

}
