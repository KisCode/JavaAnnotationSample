package com.kiscode.annotation.pojo;


import com.kiscode.annotation.ormlite.DataTable;
import com.kiscode.annotation.ormlite.SqlTypeEnum;
import com.kiscode.annotation.ormlite.TableColumn;

/****
 * Description: 
 * Author:  keno
 * CreateDate: 2020/8/24 22:07
 */
@DataTable(tableName = "tb_person")
public class Person {
    @TableColumn(id = true, name = "_id", type = SqlTypeEnum.INTEGER)
    public int id;

    @TableColumn(name = "name")
    public String name;
}
