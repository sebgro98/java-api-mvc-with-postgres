package com.booleanuk.api.employe;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmployeeRepository {

    private DataSource datasource;
    private String dbUser;
    private String dbURL;
    private String dbPassword;
    private String dbDatabase;
    private Connection connection;

    public EmployeeRepository() throws SQLException {
        //get credentials
        this.getDatabaseCredentials();
        // set up the datasource
        this.datasource = this.createDataSource();
        // Set up connection
        this.connection = this.datasource.getConnection();
    }

    private void getDatabaseCredentials() {
        try(InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            this.dbUser = prop.getProperty("db.user");
            this.dbURL = prop.getProperty("db.url");
            this.dbPassword = prop.getProperty("db.password");
            this.dbDatabase = prop.getProperty("db.database");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    private DataSource createDataSource() {
        final String url = "jdbc:postgresql://"+ this.dbURL
                + ":5432/" + this.dbDatabase
                + "?user=" + this.dbUser
                + "&password=" + this.dbPassword;
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(url);
        return dataSource;
    }

    public List<Employee> getAll () throws SQLException {
        List<Employee> everyOne = new ArrayList<>();
        PreparedStatement statement = this.connection.
                prepareStatement("SELECT * FROM Employee");
        ResultSet results = statement.executeQuery();
        while(results.next()) {
            Employee employee = new Employee(
                    results.getInt("id"),
                    results.getString("name"),
                    results.getString("jobName"),
                    results.getString("salaryGrade"),
                    results.getString("department"));
            everyOne.add(employee);
        }
        return everyOne;
    }

    public Employee getOne (int id)  throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM Employee WHERE id= ?");
        statement.setInt(1, id);

        ResultSet results = statement.executeQuery();
        Employee employee = null;
        if(results.next()) {
            employee = new Employee(
                    results.getInt("id"),
                    results.getString("name"),
                    results.getString("jobName"),
                    results.getString("salaryGrade"),
                    results.getString("department"));
            return employee;
        }
        return employee;
    }

    public Employee update(int id, Employee employee) throws SQLException {
        String SQL = "UPDATE Employee " +
                "SET name = ? ," +
                "jobName = ? ," +
                "salaryGrade = ? ," +
                "department = ? " +
                "WHERE id = ? ";
        PreparedStatement statement = this.connection.prepareStatement(SQL);
        statement.setString(1, employee.getName());
        statement.setString(2, employee.getJobName());
        statement.setString(3, employee.getSalaryGrade());
        statement.setString(4, employee.getDepartment());
        statement.setInt(5, id);
        int rowsAffected = statement.executeUpdate();
        Employee updatedEmployee = null;
        if(rowsAffected > 0) {
            updatedEmployee = this.getOne(id);
        }
        return updatedEmployee;
    }

    public Employee delete(int id) throws SQLException{
        String SQL = "DELETE FROM Employee WHERE id=?";
        PreparedStatement statement = this.connection.prepareStatement(SQL);
        statement.setInt(1,id);
        Employee deletedEmployee = null;
        deletedEmployee = this.getOne(id);
        int rowsAffected = statement.executeUpdate();
        if(rowsAffected == 0 ) {
            deletedEmployee = null;
        }
        return deletedEmployee;
    }

    public Employee add(Employee employee) throws SQLException {
        String SQL = "INSERT INTO Employee (name, jobName, salaryGrade, department) VALUES (?,?,?,?)";
        PreparedStatement statement = this.connection.prepareStatement(SQL,
                Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, employee.getName());
        statement.setString(2, employee.getJobName());
        statement.setString(3, employee.getSalaryGrade());
        statement.setString(4, employee.getDepartment());
        int rowsAffected = statement.executeUpdate();
        int newID = 0;
        if(rowsAffected > 0) {
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if(rs.next()) {
                    newID = rs.getInt(1);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            employee.setId(newID);
        } else {
            employee = null;
        }
        return employee;


    }
}
