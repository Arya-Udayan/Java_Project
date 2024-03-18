import java.sql.*;
import java.util.Scanner;

public class EmployeeManagementSystem {
    private static Connection connection;

    public static void main(String[] args) {
        connectToDatabase();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nEmployee Management System");
            System.out.println("1. Add Employee");
            System.out.println("2. View Employee Details");
            System.out.println("3. Update Employee Information");
            System.out.println("4. Delete Employee");
            System.out.println("5. View Total Salary Expenditure");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addEmployee(scanner);
                    break;
                case 2:
                    viewEmployeeDetails(scanner);
                    break;
                case 3:
                    updateEmployeeInformation(scanner);
                    break;
                case 4:
                    deleteEmployee(scanner);
                    break;
                case 5:
                    viewTotalSalaryExpenditure();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    closeConnection();
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void connectToDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java?characterEncoding=utf8", "root", "");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addEmployee(Scanner scanner) {
        System.out.println("\nAdding Employee");
        System.out.print("Enter employee name: ");
        String name = scanner.nextLine();
        System.out.print("Enter employee department: ");
        String department = scanner.nextLine();
        System.out.print("Enter employee salary: ");
        double salary = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO employee(name, department, salary) VALUES (?, ?, ?)"
            );
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, department);
            preparedStatement.setDouble(3, salary);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee added successfully!");
            } else {
                System.out.println("Failed to add employee.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewEmployeeDetails(Scanner scanner) {
        System.out.println("\nViewing Employee Details");
        System.out.print("Enter employee ID: ");
        int employeeId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM employee WHERE id = ?");
            preparedStatement.setInt(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String department = resultSet.getString("department");
                double salary = resultSet.getDouble("salary");
		System.out.println("\n---------------------------------------");
                System.out.println("Employee ID: " + employeeId);
                System.out.println("Name: " + name);
                System.out.println("Department: " + department);
                System.out.println("Salary: " + salary);
		System.out.println("---------------------------------------");
            } else {
                System.out.println("Employee not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

private static void updateEmployeeInformation(Scanner scanner) {
    System.out.println("\nUpdating Employee Information");
    System.out.print("Enter employee ID: ");
    int employeeId = scanner.nextInt();
    scanner.nextLine(); // Consume newline

    // Check if the employee with the provided ID exists
    if (!isEmployeeExists(employeeId)) {
        System.out.println("Employee with ID " + employeeId + " does not exist.");
        return;
    }

    // Proceed with updating employee information
    System.out.print("Enter new employee name: ");
    String name = scanner.nextLine();
    System.out.print("Enter new employee department: ");
    String department = scanner.nextLine();
    System.out.print("Enter new employee salary: ");
    double salary = scanner.nextDouble();
    scanner.nextLine(); // Consume newline

    try {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE employee SET name = ?, department = ?, salary = ? WHERE id = ?"
        );
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, department);
        preparedStatement.setDouble(3, salary);
        preparedStatement.setInt(4, employeeId);
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Employee information updated successfully!");
        } else {
            System.out.println("Failed to update employee information.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private static boolean isEmployeeExists(int employeeId) {
    try {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM employee WHERE id = ?");
        preparedStatement.setInt(1, employeeId);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    private static void deleteEmployee(Scanner scanner) {
        System.out.println("\nDeleting Employee");
        System.out.print("Enter employee ID: ");
        int employeeId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM employee WHERE id = ?");
            preparedStatement.setInt(1, employeeId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee deleted successfully!");
            } else {
                System.out.println("Failed to delete employee.Check the entered id.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

private static void viewTotalSalaryExpenditure() {
    System.out.println("\nViewing Total Salary Expenditure by Department");
	 System.out.println("\n---------------------------------------------------");
    double totalExpense = 0; // Initialize total expense variable

    try {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT department, SUM(salary) AS totalSalary FROM employee GROUP BY department");

        while (resultSet.next()) {
            String department = resultSet.getString("department");
            double totalSalary = resultSet.getDouble("totalSalary");
            System.out.println("Department: " + department + ", Total Salary Expenditure: " + totalSalary);
            totalExpense += totalSalary; // Update total expense
        }

        // Display total expense across all departments
	 System.out.println("\n---------------------------------------------------");
        System.out.println("\nTotal Expense Across All Departments: " + totalExpense);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

}
