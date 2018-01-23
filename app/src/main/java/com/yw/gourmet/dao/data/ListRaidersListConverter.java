package com.yw.gourmet.dao.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yw.gourmet.data.RaidersListData;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/22.
 */

public class ListRaidersListConverter implements PropertyConverter<List<RaidersListData<List<String>>>,String> {
    @Override
    public List<RaidersListData<List<String>>> convertToEntityProperty(String databaseValue) {
        return new Gson().fromJson(databaseValue,new TypeToken<List<RaidersListData<List<String>>>>(){}.getType());
    }

    @Override
    public String convertToDatabaseValue(List<RaidersListData<List<String>>> entityProperty) {
        return new Gson().toJson(entityProperty);
    }
}
