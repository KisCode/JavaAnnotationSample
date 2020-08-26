package com.kiscode.annotation.pojo;


import com.kiscode.annotation.Hello;
import com.kiscode.annotation.ormlite.DataTable;
import com.kiscode.annotation.ormlite.SqlTypeEnum;
import com.kiscode.annotation.ormlite.TableColumn;

/****
 * Description: 
 * Author:  Administrator
 * CreateDate: 2020/8/24 23:24
 */

@DataTable(tableName = "tb_student")
public class Student extends Person {

    @TableColumn(name = "age", type = SqlTypeEnum.INTEGER)
    public int age;
}
