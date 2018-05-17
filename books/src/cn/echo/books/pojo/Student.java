package cn.echo.books.pojo;

import java.io.Serializable;

// 为了不让session移除属性
public class Student implements Serializable{
    private Long studentId;

    private Long password;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getPassword() {
        return password;
    }

    public void setPassword(Long password) {
        this.password = password;
    }
}