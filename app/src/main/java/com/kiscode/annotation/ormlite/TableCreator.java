package com.kiscode.annotation.ormlite;


import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/****
 * Description: 
 * Author:  Administrator
 * CreateDate: 2020/8/24 22:50
 */

public class TableCreator {
    private static final String TAG = "TableCreator";

    public static String createTable(String className) throws ClassNotFoundException {
        Class clz = Class.forName(className);
        DataTable dataTableAnnotation = (DataTable) clz.getAnnotation(DataTable.class);
        if (dataTableAnnotation == null) {
            return null;
        }
        Field[] declaredFields = clz.getFields();
        List<String> columnDefList = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            StringBuilder columnDef = new StringBuilder();
            if (declaredField.isAnnotationPresent(TableColumn.class)) {
                TableColumn tableColumn = declaredField.getAnnotation(TableColumn.class);
                assert tableColumn != null;
                Log.i(TAG, tableColumn.name());

                columnDef.append(tableColumn.name());

                //拼接sql字段类型
                if (tableColumn.type() == SqlTypeEnum.INTEGER) {
                    columnDef.append(" INTEGER");
                } else {
                    columnDef.append(" VARCHARE(")
                            .append(tableColumn.length())
                            .append(")");
                }

                //主键
                if (tableColumn.id()) {
                    columnDef.append(" PRIMARY KEY");
                }
                columnDefList.add(columnDef.toString());
            }
        }

        StringBuilder sqlStringBuilder = new StringBuilder();
        String tableName = dataTableAnnotation.tableName();
        sqlStringBuilder.append("CREATE TABLE ").append(tableName).append("(");
        for (int i = 0; i < columnDefList.size(); i++) {

            sqlStringBuilder.append(" ").append(columnDefList.get(i));
            if (i < columnDefList.size() - 1) {
                sqlStringBuilder.append(",");
            }
        }

        sqlStringBuilder.append(")");
        return sqlStringBuilder.toString();
    }
}
