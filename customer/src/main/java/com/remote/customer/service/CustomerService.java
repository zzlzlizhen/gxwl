package com.remote.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.remote.common.service.BaseService;
import com.remote.customer.dao.CustomerDao;
import com.remote.customer.model.CustomerModel;
import com.remote.customer.model.CustomerQueryModel;
import com.remote.customer.service.impl.ICustomerService;

@Service
/* @Transactional */
public class CustomerService extends BaseService<CustomerModel,CustomerQueryModel> implements ICustomerService{
	private CustomerDao dao;
	@Autowired
	private void setDao(CustomerDao dao) {
		this.dao = dao;
		super.setDao(dao);
	}
}
