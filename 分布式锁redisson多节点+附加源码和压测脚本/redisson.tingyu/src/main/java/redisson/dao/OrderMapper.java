package redisson.dao;

import redisson.bean.po.Order;

import java.util.List;

public interface OrderMapper {

	int deleteByPrimaryKey(String id);

	int insert(Order record);

	int insertSelective(Order record);

	Order selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(Order record);

	int updateByPrimaryKey(Order record);

}