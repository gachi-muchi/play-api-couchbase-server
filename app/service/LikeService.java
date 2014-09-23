package service;

import service.impl.LikeServiceImpl;

import com.google.inject.ImplementedBy;

@ImplementedBy(LikeServiceImpl.class)
public interface LikeService {

	long like(String id, String userId);

	long unLike(String id, String userId);

	long getCount(String id);

}