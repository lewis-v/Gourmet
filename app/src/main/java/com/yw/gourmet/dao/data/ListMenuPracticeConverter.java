package com.yw.gourmet.dao.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yw.gourmet.data.MenuPracticeData;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/22.
 */

public class ListMenuPracticeConverter implements PropertyConverter<List<MenuPracticeData<List<String>>>,String> {
    @Override
    public List<MenuPracticeData<List<String>>> convertToEntityProperty(String databaseValue) {
        return new Gson().fromJson(databaseValue,new TypeToken<List<MenuPracticeData<List<String>>>>(){}.getType());
    }

    @Override
    public String convertToDatabaseValue(List<MenuPracticeData<List<String>>> entityProperty) {
        return new Gson().toJson(entityProperty);
    }
}
