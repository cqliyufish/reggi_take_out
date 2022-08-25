package com.yu.reggie_take_out.sevice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.reggie_take_out.entity.AddressBook;
import com.yu.reggie_take_out.mapper.AddressBookMapper;
import com.yu.reggie_take_out.sevice.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
