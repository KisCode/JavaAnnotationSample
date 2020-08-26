package com.kiscode.annotation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.kiscode.annotation.ormlite.DataTable;
import com.kiscode.annotation.ormlite.TableColumn;
import com.kiscode.annotation.ormlite.TableCreator;
import com.kiscode.annotation.pojo.Person;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


@Hello
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testAnnotation();

        try {
            String createTableSql = TableCreator.createTable("com.kiscode.annotation.pojo.Person");
            Log.i("createPersonTableSql", createTableSql);

            String createStudentTableSql = TableCreator.createTable("com.kiscode.annotation.pojo.Student");
            Log.i("createStudentTableSql", createStudentTableSql);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void testAnnotation() {
        Person person = new Person();

        Class personClass = person.getClass();

        //获取指定类型的注解
        DataTable dataTableAnnotation = (DataTable) personClass.getAnnotation(DataTable.class);
        Log.i("Annotation", dataTableAnnotation.getClass().getName() + "_" + dataTableAnnotation.annotationType());
        //判断 指定注解是否在对象所属类
        Log.i("Annotation", "isAnnotationPresent DataTable:" + personClass.isAnnotationPresent(DataTable.class));

        Log.i("Annotation", "--------------------");

        //获取该类的全部注解
        Annotation[] declaredAnnotations = personClass.getDeclaredAnnotations();
        for (Annotation annotation : declaredAnnotations) {
            Log.i("Annotation", annotation.getClass().getName() + "_" + annotation.annotationType());
        }


        Log.i("Annotation", "--------------------");

        Field[] declaredFields = personClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Annotation[] fieldDeclaredAnnotations = declaredField.getDeclaredAnnotations();
            for (Annotation annotation : fieldDeclaredAnnotations) {
                Log.i("Annotation", annotation.getClass().getName()
                        + "_" + annotation.annotationType()
                );
                if (annotation instanceof TableColumn) {
                    TableColumn tableColumn = (TableColumn) annotation;
                    Log.i("AnnotationTableColumn", "id=" + tableColumn.id()
                            + "\tname=" + tableColumn.name()
                            + "\ttype=" + tableColumn.type()
                    );
                }
            }
        }
    }
}