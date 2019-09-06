package com.remote.common.dao;
import java.util.List;
/**
 * 公共dao
 * @author zsm
 * @param <M>
 * @param <QM>
 */
public interface BaseDao<M,QM> {
	public boolean create(M m);
	public boolean update(M m);
	public boolean delete(Long uuid);
	public M getByUuid(Long uuid);
	public List<M> getByConditionPage(QM qm);
}
