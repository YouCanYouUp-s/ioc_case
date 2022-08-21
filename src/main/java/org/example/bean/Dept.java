package org.example.bean;

public class Dept {
    private int id;
    private String deptName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public String toString() {
        return "Dept{" +
                "id=" + id +
                ", deptName='" + deptName + '\'' +
                '}';
    }
}
