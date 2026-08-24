/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hospitalregistration;

/**
 *
 * @author molok
 */
import java.util.*;

public class HospitalRegistration {
    public static void main(String[] args) {
        Menu.startHospitalManagementMenu();
    }
}

enum PatientCategory {
    INPATIENT,
    OUTPATIENT,
    EMERGENCY
}

class Patient {
    private String patientId;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String medicalCondition;
    private PatientCategory category;

    public Patient(String patientId, String firstName, String lastName, int age, String gender, String medicalCondition, PatientCategory category) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.medicalCondition = medicalCondition;
        this.category = category;
    }

    public String getPatientId() { return patientId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getMedicalCondition() { return medicalCondition; }
    public void setMedicalCondition(String medicalCondition) { this.medicalCondition = medicalCondition; }
    public PatientCategory getCategory() { return category; }
    public void setCategory(PatientCategory category) { this.category = category; }

    public String getFormattedPatientDetailsForDisplay() {
        return patientId + " - " + firstName + " " + lastName + " | Age: " + age + " | Gender: " + gender + " | Condition: " + medicalCondition + " | Category: " + category;
    }

    @Override
    public String toString() {
        return getFormattedPatientDetailsForDisplay();
    }
}

class Inpatient extends Patient {
    private int wardNumber;
    private String bedNumber;

    public Inpatient(String patientId, String firstName, String lastName, int age, String gender, String medicalCondition, int wardNumber, String bedNumber) {
        super(patientId, firstName, lastName, age, gender, medicalCondition, PatientCategory.INPATIENT);
        if (wardNumber <= 0) {
            System.out.println("Error: Ward number must be greater than 0. Setting to 1.");
            this.wardNumber = 1;
        } else {
            this.wardNumber = wardNumber;
        }
        this.bedNumber = bedNumber;
    }

    public int getWardNumber() { return wardNumber; }
    public void setWardNumber(int wardNumber) {
        if (wardNumber <= 0) {
            System.out.println("Ward must be > 0");
        } else {
            this.wardNumber = wardNumber;
        }
    }
    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }

    public String getFormattedInpatientDetailsWithWardAndBed() {
        return getFormattedPatientDetailsForDisplay() + " | Ward: " + wardNumber + " | Bed: " + bedNumber;
    }

    @Override
    public String toString() {
        return getFormattedInpatientDetailsWithWardAndBed();
    }
}

class HospitalManager {
    private List<Patient> patients = new ArrayList<Patient>();
    private String[][] beds = new String[4][5];
    private Map<String, String> bedAllocation = new HashMap<String, String>();

    public HospitalManager() {
        int bedCount = 1;
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 5; column++) {
                if (bedCount < 10) {
                    beds[row][column] = "B0" + bedCount;
                } else {
                    beds[row][column] = "B" + bedCount;
                }
                bedCount = bedCount + 1;
            }
        }
    }

    public boolean registerNewPatientInHospitalSystem(Patient patientToRegister) {
        for (int index = 0; index < patients.size(); index++) {
            if (patients.get(index).getPatientId().equalsIgnoreCase(patientToRegister.getPatientId())) {
                return false;
            }
        }
        patients.add(patientToRegister);
        return true;
    }

    public Patient searchAndFindPatientByIdNumber(String patientIdToFind) {
        for (int index = 0; index < patients.size(); index++) {
            Patient currentPatient = patients.get(index);
            if (currentPatient.getPatientId().equalsIgnoreCase(patientIdToFind)) {
                return currentPatient;
            }
        }
        return null;
    }

    public boolean updateExistingPatientInformation(String patientIdToUpdate, String newFirstName, String newLastName, int newAge, String newGender, String newMedicalCondition) {
        Patient foundPatient = searchAndFindPatientByIdNumber(patientIdToUpdate);
        if (foundPatient == null) return false;
        foundPatient.setFirstName(newFirstName);
        foundPatient.setLastName(newLastName);
        foundPatient.setAge(newAge);
        foundPatient.setGender(newGender);
        foundPatient.setMedicalCondition(newMedicalCondition);
        return true;
    }

    public boolean deletePatientAndFreeTheirBed(String patientIdToDelete) {
        Patient patientToDelete = searchAndFindPatientByIdNumber(patientIdToDelete);
        if (patientToDelete == null) return false;
        if (patientToDelete instanceof Inpatient) {
            Inpatient inpatientToDelete = (Inpatient) patientToDelete;
            String bedNumberToFree = inpatientToDelete.getBedNumber();
            if (bedNumberToFree!= null) {
                if (!bedNumberToFree.equals("")) {
                    releaseOccupiedBedBackToAvailable(bedNumberToFree);
                }
            }
        }
        String bedKeyFound = "";
        for (String bedKey : bedAllocation.keySet()) {
            String allocatedPatientId = bedAllocation.get(bedKey);
            if (allocatedPatientId.equalsIgnoreCase(patientIdToDelete)) {
                bedKeyFound = bedKey;
            }
        }
        if (!bedKeyFound.equals("")) {
            bedAllocation.remove(bedKeyFound);
        }
        patients.remove(patientToDelete);
        return true;
    }

    public void displayAllRegisteredPatientsSortedById() {
        if (patients.size() == 0) {
            System.out.println("No patients registered.");
            return;
        }
        for (int i = 0; i < patients.size() - 1; i++) {
            for (int j = 0; j < patients.size() - 1 - i; j++) {
                String firstPatientId = patients.get(j).getPatientId();
                String secondPatientId = patients.get(j+1).getPatientId();
                if (firstPatientId.compareToIgnoreCase(secondPatientId) > 0) {
                    Patient temporaryPatient = patients.get(j);
                    patients.set(j, patients.get(j+1));
                    patients.set(j+1, temporaryPatient);
                }
            }
        }
        for (int index = 0; index < patients.size(); index++) {
            System.out.println(patients.get(index));
        }
    }

    public boolean allocateAvailableBedToInpatient(String patientIdToAllocate, String bedIdToAllocate) {
        Patient foundPatient = searchAndFindPatientByIdNumber(patientIdToAllocate);
        if (foundPatient == null) return false;
        if (foundPatient.getCategory()!= PatientCategory.INPATIENT) return false;
        if (bedAllocation.containsKey(bedIdToAllocate)) return false;
        for (String allocatedBedKey : bedAllocation.keySet()) {
            String allocatedPatientId = bedAllocation.get(allocatedBedKey);
            if (allocatedPatientId.equalsIgnoreCase(patientIdToAllocate)) {
                return false;
            }
        }
        if (bedAllocation.size() >= 20) {
            return false;
        }
        boolean isBedIdValid = false;
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 5; column++) {
                if (beds[row][column].equals(bedIdToAllocate)) {
                    isBedIdValid = true;
                }
            }
        }
        if (isBedIdValid == false) return false;
        bedAllocation.put(bedIdToAllocate, patientIdToAllocate);
        if (foundPatient instanceof Inpatient) {
            Inpatient inpatientFound = (Inpatient) foundPatient;
            inpatientFound.setBedNumber(bedIdToAllocate);
            inpatientFound.setWardNumber(1);
        }
        return true;
    }

    public boolean releaseOccupiedBedBackToAvailable(String bedIdToRelease) {
        if (bedAllocation.containsKey(bedIdToRelease) == false) return false;
        bedAllocation.remove(bedIdToRelease);
        return true;
    }

    public void displayListOfAvailableBedsForAllocation() {
        System.out.println("Available Beds:");
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 5; column++) {
                String currentBedId = beds[row][column];
                if (bedAllocation.containsKey(currentBedId) == false) {
                    System.out.print(currentBedId + " ");
                }
            }
            System.out.println();
        }
        if (bedAllocation.size() >= 20) {
            System.out.println("Ward is FULL - No beds available");
        }
    }

    public void displayListOfOccupiedBedsWithPatientIds() {
        if (bedAllocation.size() == 0) {
            System.out.println("No occupied beds");
        } else {
            System.out.println("Occupied Beds: " + bedAllocation);
        }
    }

    public int getTotalNumberOfRegisteredPatients() { return patients.size(); }
    public int getTotalNumberOfOccupiedBeds() { return bedAllocation.size(); }
    public double calculateBedOccupancyPercentage() {
        double occupancyPercent = (bedAllocation.size() / 20.0) * 100;
        return occupancyPercent;
    }

    public void testDuplicatePatientRegistration() {
        System.out.println("\n--- Test: Duplicate Patient Registration ---");
        HospitalManager hospitalManagerForTest = new HospitalManager();
        Patient firstPatientForTest = new Patient("P001", "John", "Doe", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        Patient secondPatientForTest = new Patient("P001", "Jane", "Doe", 25, "Female", "Cold", PatientCategory.OUTPATIENT);
        boolean firstRegistrationResult = hospitalManagerForTest.registerNewPatientInHospitalSystem(firstPatientForTest);
        boolean secondRegistrationResult = hospitalManagerForTest.registerNewPatientInHospitalSystem(secondPatientForTest);
        if (firstRegistrationResult == true && secondRegistrationResult == false) {
            System.out.println("PASS: Duplicate ID blocked");
        } else {
            System.out.println("FAIL: Duplicate ID not blocked");
        }
    }

    public void testAllocateOccupiedBed() {
        System.out.println("\n--- Test: Allocate Occupied Bed ---");
        HospitalManager hospitalManagerForTest = new HospitalManager();
        Patient firstInpatientForTest = new Inpatient("P001", "A", "B", 30, "M", "Fever", 1, "");
        Patient secondInpatientForTest = new Inpatient("P002", "C", "D", 40, "F", "Fever", 1, "");
        hospitalManagerForTest.registerNewPatientInHospitalSystem(firstInpatientForTest);
        hospitalManagerForTest.registerNewPatientInHospitalSystem(secondInpatientForTest);
        boolean firstBedAllocationResult = hospitalManagerForTest.allocateAvailableBedToInpatient("P001", "B01");
        boolean secondBedAllocationResult = hospitalManagerForTest.allocateAvailableBedToInpatient("P002", "B01");
        if (firstBedAllocationResult == true && secondBedAllocationResult == false) {
            System.out.println("PASS: Occupied bed blocked");
        } else {
            System.out.println("FAIL: Occupied bed not blocked");
        }
    }

    public void testWardFullAllocation() {
        System.out.println("\n--- Test: Ward Full Allocation (20 beds) ---");
        HospitalManager hospitalManagerForTest = new HospitalManager();
        for (int patientNumber = 1; patientNumber <= 20; patientNumber++) {
            String patientIdForLoop = "P0" + patientNumber;
            if (patientNumber < 10) patientIdForLoop = "P00" + patientNumber;
            Patient patientForLoop = new Inpatient(patientIdForLoop, "First", "Last", 20+patientNumber, "M", "Sick", 1, "");
            hospitalManagerForTest.registerNewPatientInHospitalSystem(patientForLoop);
            String bedIdForLoop = "";
            if (patientNumber < 10) bedIdForLoop = "B0" + patientNumber;
            else bedIdForLoop = "B" + patientNumber;
            hospitalManagerForTest.allocateAvailableBedToInpatient(patientIdForLoop, bedIdForLoop);
        }
        Patient extraPatientForTest = new Inpatient("P021", "Extra", "Patient", 30, "M", "Sick", 1, "");
        hospitalManagerForTest.registerNewPatientInHospitalSystem(extraPatientForTest);
        boolean extraBedAllocationResult = hospitalManagerForTest.allocateAvailableBedToInpatient("P021", "B01");
        if (extraBedAllocationResult == false && hospitalManagerForTest.getTotalNumberOfOccupiedBeds() == 20) {
            System.out.println("PASS: Ward full blocked - 20/20 beds");
        } else {
            System.out.println("FAIL: Ward full not blocked");
        }
    }

    public void runAllUnitTestsForVerification() {
        System.out.println("\n======== RUNNING UNIT TESTS ========");
        testDuplicatePatientRegistration();
        testAllocateOccupiedBed();
        testWardFullAllocation();
        System.out.println("\n======== TESTS FINISHED ========");
    }
}

class Menu {
    public static void startHospitalManagementMenu() {
        HospitalManager hospitalManager = new HospitalManager();
        Scanner inputScanner = new Scanner(System.in);
        int menuChoice = 0;
        do {
            System.out.println("\n======== HOSPITAL MANAGEMENT SYSTEM ========");
            System.out.println("1. Register Patient");
            System.out.println("2. Search Patient by ID");
            System.out.println("3. Update Patient Details");
            System.out.println("4. Delete Patient");
            System.out.println("5. Display All Patients");
            System.out.println("6. Allocate Bed (Inpatients Only)");
            System.out.println("7. Release Bed");
            System.out.println("8. Display Reports ");
            System.out.println("9. Run Unit Tests");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            menuChoice = inputScanner.nextInt();
            inputScanner.nextLine();

            if (menuChoice == 1) {
                System.out.print("Enter Patient ID: "); String patientIdInput = inputScanner.nextLine();
                System.out.print("Enter First Name: "); String firstNameInput = inputScanner.nextLine();
                System.out.print("Enter Last Name: "); String lastNameInput = inputScanner.nextLine();
                System.out.print("Enter Age: "); int ageInput = inputScanner.nextInt(); inputScanner.nextLine();
                System.out.print("Enter Gender: "); String genderInput = inputScanner.nextLine();
                System.out.print("Enter Medical Condition: "); String medicalConditionInput = inputScanner.nextLine();
                System.out.print("Enter Category (INPATIENT/OUTPATIENT/EMERGENCY): "); String categoryTextInput = inputScanner.nextLine();
                PatientCategory categoryInput = PatientCategory.valueOf(categoryTextInput.toUpperCase());
                if (categoryInput == PatientCategory.INPATIENT) {
                    System.out.print("Enter Ward Number: "); int wardNumberInput = inputScanner.nextInt(); inputScanner.nextLine();
                    Patient newPatientToRegister = new Inpatient(patientIdInput, firstNameInput, lastNameInput, ageInput, genderInput, medicalConditionInput, wardNumberInput, "");
                    if (hospitalManager.registerNewPatientInHospitalSystem(newPatientToRegister)) System.out.println("Patient Registered.");
                    else System.out.println("Error: Duplicate ID.");
                } else {
                    Patient newPatientToRegister = new Patient(patientIdInput, firstNameInput, lastNameInput, ageInput, genderInput, medicalConditionInput, categoryInput);
                    if (hospitalManager.registerNewPatientInHospitalSystem(newPatientToRegister)) System.out.println("Patient Registered.");
                    else System.out.println("Error: Duplicate ID.");
                }
            } else if (menuChoice == 2) {
                System.out.print("Enter Patient ID to Search: "); String searchIdInput = inputScanner.nextLine();
                Patient foundPatientResult = hospitalManager.searchAndFindPatientByIdNumber(searchIdInput);
                if (foundPatientResult!= null) System.out.println(foundPatientResult);
                else System.out.println("Patient not found.");
            } else if (menuChoice == 3) {
                System.out.print("Enter Patient ID to Update: "); String updatePatientIdInput = inputScanner.nextLine();
                System.out.print("Enter New First Name: "); String newFirstNameInput = inputScanner.nextLine();
                System.out.print("Enter New Last Name: "); String newLastNameInput = inputScanner.nextLine();
                System.out.print("Enter New Age: "); int newAgeInput = inputScanner.nextInt(); inputScanner.nextLine();
                System.out.print("Enter New Gender: "); String newGenderInput = inputScanner.nextLine();
                System.out.print("Enter New Medical Condition: "); String newConditionInput = inputScanner.nextLine();
                if (hospitalManager.updateExistingPatientInformation(updatePatientIdInput, newFirstNameInput, newLastNameInput, newAgeInput, newGenderInput, newConditionInput)) System.out.println("Updated.");
                else System.out.println("Patient not found.");
            } else if (menuChoice == 4) {
                System.out.print("Enter Patient ID to Delete: "); String deleteIdInput = inputScanner.nextLine();
                if (hospitalManager.deletePatientAndFreeTheirBed(deleteIdInput)) System.out.println("Deleted and bed freed.");
                else System.out.println("Patient not found.");
            } else if (menuChoice == 5) {
                hospitalManager.displayAllRegisteredPatientsSortedById();
            } else if (menuChoice == 6) {
                System.out.print("Enter Patient ID: "); String allocationPatientIdInput = inputScanner.nextLine();
                hospitalManager.displayListOfAvailableBedsForAllocation();
                System.out.print("Enter Bed ID to Allocate (e.g. B01): "); String bedIdToAllocateInput = inputScanner.nextLine();
                if (hospitalManager.allocateAvailableBedToInpatient(allocationPatientIdInput, bedIdToAllocateInput)) System.out.println("Bed Allocated.");
                else {
                    if (hospitalManager.getTotalNumberOfOccupiedBeds() >= 20) System.out.println("Failed - Hospital Full (20/20 beds).");
                    else System.out.println("Failed - Bed occupied / Patient already has bed / Not Inpatient.");
                }
            } else if (menuChoice == 7) {
                System.out.print("Enter Bed ID to Release (e.g. B01): "); String bedIdToReleaseInput = inputScanner.nextLine();
                if (hospitalManager.releaseOccupiedBedBackToAvailable(bedIdToReleaseInput)) System.out.println("Bed Released.");
                else System.out.println("Bed not occupied.");
            } else if (menuChoice == 8) {
                System.out.println("\n--- REPORTS ---");
                hospitalManager.displayAllRegisteredPatientsSortedById();
                System.out.println("\nTotal Patients: " + hospitalManager.getTotalNumberOfRegisteredPatients());
                hospitalManager.displayListOfAvailableBedsForAllocation();
                hospitalManager.displayListOfOccupiedBedsWithPatientIds();
                System.out.println("Total Occupied Beds: " + hospitalManager.getTotalNumberOfOccupiedBeds());
                System.out.println("Occupancy Percentage: " + hospitalManager.calculateBedOccupancyPercentage() + "%");
            } else if (menuChoice == 9) {
                hospitalManager.runAllUnitTestsForVerification();
            } else if (menuChoice == 0) {
                System.out.println("Exiting...");
            } else {
                System.out.println("Invalid choice.");
            }
        } while (menuChoice!= 0);
        inputScanner.close();
    }
}