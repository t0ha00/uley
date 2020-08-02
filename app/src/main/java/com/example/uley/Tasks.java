package com.example.uley;

public class Tasks {

    String name;
    String typeOfTask;

    public Tasks() {
    }

    public Tasks(String name, String typeOfTask) {
        this.name = name;
        this.typeOfTask = typeOfTask;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeOfTask() {
        return typeOfTask;
    }

    public void setTypeOfTask(String typeOfTask) {
        this.typeOfTask = typeOfTask;
    }
}
