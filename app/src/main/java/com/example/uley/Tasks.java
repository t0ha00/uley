package com.example.uley;

public class Tasks {

    String name;
    String color;
    String typeSubTask;

    public Tasks() {
    }

    public String getTypeSubTask() {
        return typeSubTask;
    }

    public void setTypeSubTask(String typeSubTask) {
        this.typeSubTask = typeSubTask;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Tasks(String name, String color, String typeSubTask) {
        this.name = name;
        this.color = color;
        this.typeSubTask = typeSubTask;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
