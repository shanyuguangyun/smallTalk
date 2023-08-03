package com.europa.smallTalk.im.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.Map;

/**
 * 由于使用了数据库，将dao层先简单提取到可取变量
 * @version 1.2
 */
@Slf4j
public class DaoFactory {

    public static Map<String, JpaRepository> daoMap;


    public static int register(Map<String, JpaRepository> map) {
        daoMap = map;
        Arrays.stream(map.keySet().toArray()).forEach(dao -> log.info("---------" + dao.toString() + "----------"));
        return map.size();
    }

    public static JpaRepository getByClass(Class clazz) {
        String simpleName = clazz.getSimpleName();
        return daoMap.get(simpleName);
    }

}
