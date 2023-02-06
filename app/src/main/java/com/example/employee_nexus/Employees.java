package com.example.employee_nexus;

public class Employees {
    private String building;
    private String dept;
    private String email;
    private int emp_id;
    private String name;
    private int paid_days;
    private int shift;
    private int unexcused;
    private int unpaid_days;

    public Employees() {
    }

    public Employees(String building, String dept, String email, int emp_id, String name,
                     int paid_days, int shift, int unexcused, int unpaid_days){
        this.building = building;
        this.dept = dept;
        this.email = email;
        this.emp_id = emp_id;
        this.name = name;
        this.paid_days = paid_days;
        this.shift = shift;
        this.unexcused = unexcused;
        this.unpaid_days = unpaid_days;
    }

    public String getBuilding() {
        return building;
    }

    public String getDept() {
        return dept;
    }

    public int getEmp_id() {
        return emp_id;
    }

    public String getName() {
        return name;
    }

    public int getShift() {
        return shift;
    }

    public int getPaid_days() {
        return paid_days;
    }

    public void setPaid_days(int paid_days) {
        this.paid_days = paid_days;
    }

    public int getUnexcused() {
        return unexcused;
    }

    public void setUnexcused(int unexcused) {
        this.unexcused = unexcused;
    }

    public int getUnpaid_days() {
        return unpaid_days;
    }

    public void setUnpaid_days(int unpaid_days) {
        this.unpaid_days = unpaid_days;
    }
}
