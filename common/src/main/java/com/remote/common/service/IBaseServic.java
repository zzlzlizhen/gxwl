
package com.remote.common.service;

import com.remote.common.model.BaseModel;
import com.remote.pageutil.Page;

/**
 * 公共service接口
 * @author 86158
 *
 * @param <M>
 * @param <QM>
 */
public interface IBaseServic<M,QM extends BaseModel> {
	public void create(M m);
	public void update(M m);
	public void delete(Long uuid);
	public M getByUuid(Long uuid);
	public Page<M> getByConditionPage(QM qm);	
}
