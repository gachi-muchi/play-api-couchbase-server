package service;

import service.impl.AuthServiceImpl;

import com.google.inject.ImplementedBy;

import entity.AuthToken;

@ImplementedBy(AuthServiceImpl.class)
public interface AuthService {

	AuthToken createToken(String mail, String password);

	AuthToken confirmToken(String token);

	boolean deleteToken(String token);

}