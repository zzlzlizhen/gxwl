package com.remote.customer.dao;
import org.springframework.stereotype.Repository;

import com.remote.common.dao.BaseDao;
import com.remote.customer.model.CustomerQueryModel;
import com.remote.customer.model.CustomerModel;

/**
 * 功能描述：客户dao
 * @author zsm
 *
 */
 @Repository 
public interface CustomerDao extends BaseDao<CustomerModel,CustomerQueryModel>{
	
}
