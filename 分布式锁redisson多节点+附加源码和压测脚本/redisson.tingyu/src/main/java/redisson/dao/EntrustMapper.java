package redisson.dao;

import redisson.bean.po.Entrust;

import java.util.List;

public interface EntrustMapper {

	int deleteByPrimaryKey(String id);

	int insert(Entrust record);

	int insertSelective(Entrust record);

	Entrust selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(Entrust record);

	int updateByPrimaryKey(Entrust record);


}