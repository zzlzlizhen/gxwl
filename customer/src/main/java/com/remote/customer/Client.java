package com.remote.customer;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.remote.customer.model.CustomerModel;
import com.remote.customer.model.CustomerQueryModel;
import com.remote.customer.service.impl.ICustomerService;
import com.remote.pageutil.Page;

@Service
public class Client {
	@Autowired
	private ICustomerService customerService;
	
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		Client t = (Client)ctx.getBean("client");
		
		/*
		 * CustomerModel cm = new CustomerModel(); cm.setCustomerId("c1");
		 * cm.setPwd("111111"); cm.setShowName("zsm"); cm.setTrueName("lz");
		 * cm.setRegisterTime(new Date());
		 */
//		c.customerDao.create(cm);
		CustomerQueryModel qm = new CustomerQueryModel();
	
		Page<CustomerModel> p = t.customerService.getByConditionPage(qm);
	 
		System.out.println("cvList"+ p);
		
		
	}

}