import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

// ===================== ABSTRACT CLASS =====================
abstract class Vehicle {
    protected String id;
    protected String brand;
    protected String model;
    protected double baseRate;

    public Vehicle(String id, String brand, String model, double baseRate) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.baseRate = baseRate;
    }

    public abstract double calculateRent(int days);

    public String getDetails() {
        return id + " - " + brand + " " + model;
    }
}

// ===================== CAR =====================
class Car extends Vehicle {
    private boolean isPremium;

    public Car(String id, String brand, String model, double baseRate, boolean isPremium) {
        super(id, brand, model, baseRate);
        this.isPremium = isPremium;
    }

    @Override
    public double calculateRent(int days) {
        return isPremium ? baseRate * days * 1.5 : baseRate * days;
    }
}

// ===================== TRUCK =====================
class Truck extends Vehicle {
    private double loadCapacity;

    public Truck(String id, String brand, String model, double baseRate, double loadCapacity) {
        super(id, brand, model, baseRate);
        this.loadCapacity = loadCapacity;
    }

    @Override
    public double calculateRent(int days) {
        return baseRate * days + (loadCapacity * 10);
    }
}

// ===================== MOTORCYCLE =====================
class Motorcycle extends Vehicle {

    public Motorcycle(String id, String brand, String model, double baseRate) {
        super(id, brand, model, baseRate);
    }

    @Override
    public double calculateRent(int days) {
        return baseRate * days * 0.8;
    }
}

// ===================== INTERFACE =====================
interface Rentable {
    void rent(int days);
    void returnVehicle();
}

// ===================== RENTAL =====================
class Rental implements Rentable {
    private Vehicle vehicle;
    private int days;
    private boolean driverRequired;
    private double totalCost;

    private static final double DRIVER_COST = 500;

    public Rental(Vehicle vehicle, int days, boolean driverRequired) {
        this.vehicle = vehicle;
        this.days = days;
        this.driverRequired = driverRequired;
        calculateTotal();
    }

    private void calculateTotal() {
        totalCost = vehicle.calculateRent(days);
        if (driverRequired) {
            totalCost += DRIVER_COST * days;
        }
    }

    public String getDetails() {
        return vehicle.getDetails() +
                " | Days: " + days +
                " | Driver: " + (driverRequired ? "Yes" : "No") +
                " | Total: ₹" + totalCost;
    }

    @Override
    public void rent(int days) {
        System.out.println("Vehicle rented for " + days + " days.");
    }

    @Override
    public void returnVehicle() {
        System.out.println("Vehicle returned.");
    }
}

// ===================== FILE HANDLER =====================
class FileHandler {
    private static final String FILE_NAME = "rentals.txt";

    public static void save(String data) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            fw.write(data + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// ===================== SERVICE =====================
class RentalService {
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private ArrayList<Rental> rentals = new ArrayList<>();

    public void addVehicle(Vehicle v) {
        vehicles.add(v);
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public Rental rentVehicle(int index, int days, boolean driver) {
        Vehicle v = vehicles.get(index);
        Rental rental = new Rental(v, days, driver);
        rentals.add(rental);

        FileHandler.save(rental.getDetails());
        return rental;
    }
}

// ===================== GUI =====================
class RentalUI {
    private JFrame frame;
    private JComboBox<String> vehicleBox;
    private JTextField daysField;
    private JCheckBox driverBox;
    private JTextArea output;

    private RentalService service;

    public RentalUI() {
        service = new RentalService();

        // Add Vehicles
        service.addVehicle(new Car("C1", "BMW", "X5", 3000, true));
        service.addVehicle(new Car("C2", "Maruti", "Swift", 1000, false));
        service.addVehicle(new Truck("T1", "Tata", "Truck", 2000, 5));
        service.addVehicle(new Motorcycle("M1", "Yamaha", "R15", 800));

        frame = new JFrame("Vehicle Rental System");
        frame.setSize(450, 450);
        frame.setLayout(new FlowLayout());

        vehicleBox = new JComboBox<>();
        for (Vehicle v : service.getVehicles()) {
            vehicleBox.addItem(v.getDetails());
        }

        daysField = new JTextField(10);
        driverBox = new JCheckBox("Include Driver");
        JButton rentBtn = new JButton("Rent Vehicle");

        output = new JTextArea(12, 35);
        output.setEditable(false);

        rentBtn.addActionListener(e -> processRental());

        frame.add(new JLabel("Select Vehicle:"));
        frame.add(vehicleBox);

        frame.add(new JLabel("Number of Days:"));
        frame.add(daysField);

        frame.add(driverBox);
        frame.add(rentBtn);

        frame.add(new JScrollPane(output));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void processRental() {
        try {
            int index = vehicleBox.getSelectedIndex();
            int days = Integer.parseInt(daysField.getText());
            boolean driver = driverBox.isSelected();

            Rental rental = service.rentVehicle(index, days, driver);
            output.append(rental.getDetails() + "\n");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Invalid Input!");
        }
    }
}

// ===================== MAIN =====================
public class VehicleRentalSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RentalUI());
    }
}