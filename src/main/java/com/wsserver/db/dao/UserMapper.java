package com.wsserver.db.dao;


import com.wsserver.db.model.Friend;
import com.wsserver.db.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper{

	public User getUser(@Param("account") String account);


	public List<Friend> fList(@Param("ownerId") String id,@Param("ts") Long ts);


}
