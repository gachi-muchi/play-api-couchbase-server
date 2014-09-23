package service;

import service.impl.GlobalCounterServiceImpl;

import com.google.inject.ImplementedBy;

@ImplementedBy(GlobalCounterServiceImpl.class)
public interface GlobalCounterService {

	String generateId();

}