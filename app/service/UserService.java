package service;

import service.impl.UserServiceImpl;

import com.google.inject.ImplementedBy;

import entity.User;

@ImplementedBy(UserServiceImpl.class)
public interface UserService {

	User create(User user);

	User read(String id);

	User read(String mail, String password);

	boolean update(String id, User update);

}