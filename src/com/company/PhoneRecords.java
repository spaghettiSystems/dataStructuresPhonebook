package com.company;

import java.io.*;
import java.util.ArrayList;

public class PhoneRecords {
    ArrayList<Phone> phoneArrayList = new ArrayList<>();

    ArrayList<Phone> filteredPhoneArrayList = new ArrayList<>();

    public PhoneRecords(String file) throws IOException {
        openPhoneBook(file);
    }

    public PhoneRecords() {
    }

    public int findInOriginalList(int index) {
        for (int i = 0; i < phoneArrayList.size(); i++) {
            if (filteredPhoneArrayList.get(index) == phoneArrayList.get(i)) {
                return i;
            }
        }
        return -1;
    }

    public void initializeFilteredList(String filter){
        emptyFilteredList();
        for(Phone record: phoneArrayList){
            if(record.name.startsWith(filter) || record.phone.startsWith(filter)){
                filteredPhoneArrayList.add(record);
            }
        }
    }

    public void emptyFilteredList(){
        filteredPhoneArrayList.clear();
    }

    void deleteRecord(int toBeDeleted){
        filteredPhoneArrayList.remove(phoneArrayList.get(toBeDeleted));
        phoneArrayList.remove(toBeDeleted);
    }

    boolean addRecord(Phone toBeAdded){
        if (checkForDuplicates(toBeAdded)){
            return true;
        }
        phoneArrayList.add(toBeAdded);
        return false;
    }

    public boolean checkForDuplicates(Phone toBeAdded) {
        for(Phone record: phoneArrayList){
            if(record.phone.equals(toBeAdded.phone)){
                return true;
            }
        }
        return false;
    }

    void savePhoneBook(String fileAddress) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileAddress));
        for(Phone record: phoneArrayList){
            writer.write(record.phone);
            writer.newLine();
            writer.write(record.name);
            writer.newLine();
            writer.newLine();
            writer.newLine();
        }
        writer.close();
    }

    void openPhoneBook(String fileAddress) throws IOException {
        String tempName, tempPhone;
        BufferedReader reader = new BufferedReader(new FileReader(fileAddress));
        while( (tempPhone = reader.readLine())!=null ){
            tempName = reader.readLine();
            phoneArrayList.add(new Phone(tempPhone,tempName));

            reader.readLine();
            reader.readLine();
        }
        reader.close();
    }

    public static class Phone{
        String phone;
        String name;


        public Phone(String phone, String name) {
            this.phone = phone;
            this.name = name;
        }
    }
}
