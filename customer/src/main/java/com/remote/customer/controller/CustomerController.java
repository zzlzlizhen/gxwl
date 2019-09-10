package com.remote.customer.controller;

import com.remote.customer.model.CustomerModel;
import com.remote.customer.model.CustomerQueryModel;
import com.remote.customer.model.CustomerWebModel;
import com.remote.customer.service.impl.ICustomerService;
import com.remote.customer.util.JsonUtils;
import com.remote.pageutil.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RequestMapping("/customer")
@Controller
public class CustomerController {
    @Autowired
    private ICustomerService customerService = null;
    @RequestMapping(value = "/toAdd",method = RequestMethod.GET)
    public String toAdd(){
        return "/customer/add";
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String add(@ModelAttribute("cm") CustomerModel cm){
        cm.setRegisterTime(new Date());
        customerService.create(cm);
        return "/customer/sucess";
    }
    @RequestMapping(value = "/toUpdate/{uuid}",method = RequestMethod.GET)
    public String toUpdate(@PathVariable("uuid") Long uuid, Model model){
        CustomerModel customerModel = customerService.getByUuid(uuid);
        model.addAttribute("cm",customerModel);
        return "/customer/update";
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public String update(@ModelAttribute("cm") CustomerModel cm){
        customerService.update(cm);
        return "/customer/sucess";
    }
    @RequestMapping(value = "/toDelete/{uuid}",method = RequestMethod.GET)
    public String toDelete(Model model,@PathVariable("uuid") Long uuid){
        CustomerModel customerModel = customerService.getByUuid(uuid);
        model.addAttribute("cm",customerModel);
        return "/customer/delete";
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public String delete(@RequestParam("uuid") Long uuid){
        customerService.delete(uuid);
        return "/customer/sucess";
    }
    @RequestMapping(value = "/toList",method = RequestMethod.GET)
    public String toList(@ModelAttribute(value="cwm") CustomerWebModel cwm,Model model){
        CustomerQueryModel cqm = null;
        if(cwm.getQueryJsonStr() == null || cwm.getQueryJsonStr().trim().length() == 0){
            cqm = new CustomerQueryModel();
        }else{
            cqm = (CustomerQueryModel) JsonUtils.string2Obj(cwm.getQueryJsonStr(),CustomerQueryModel.class);
        }
        cqm.getPage().setNowPage(cwm.getNowPage());
        if(cwm.getShowPage() > 0){
            cqm.getPage().setPageShow(cwm.getShowPage());
        }
        Page dbPage = customerService.getByConditionPage(cqm);
		/* System.out.print("--------------" + dbPage); */
        model.addAttribute("cwm",cwm);
        model.addAttribute("page",dbPage);
        return "/customer/list";
    }
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(@RequestParam(value = "queryJsonStr",defaultValue = "")String queryJsonStr){
        CustomerQueryModel cqm = new CustomerQueryModel();
        customerService.getByConditionPage(cqm);
        return "/customer/sucess";
    }
}
