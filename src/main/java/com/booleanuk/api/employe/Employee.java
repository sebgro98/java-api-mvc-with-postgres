package com.booleanuk.api.employe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Employee {
    private int id;
    private String name;
    private String jobName;
    private String salaryGrade;
    private String department;

    public Employee(String name, String jobName, String salaryGrade, String department) {
        this.name = name;
        this.jobName = jobName;
        this.salaryGrade = salaryGrade;
        this.department = department;
    }

    @Override
    public String toString() {
        String result = "";
        result += this.id + " - ";
        result += this.name + " - ";
        result += this.salaryGrade + " - ";
        result += this.department + " - ";
        return result;
    }

}
