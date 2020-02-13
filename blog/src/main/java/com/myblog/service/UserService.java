package com.myblog.service;

import com.myblog.po.User;


public interface UserService {

    User checkUser(String username, String password);
}
