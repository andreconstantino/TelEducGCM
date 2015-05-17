package com.example.andre.teleducgcm;
import java.util.HashMap;
import java.util.Map;

public class testemap {

    public static void main(String[] args) {

        Map<Integer, String> mapaNomes = new HashMap<Integer, String>();
        mapaNomes.put(1, "João Delfino");
        mapaNomes.put(2, "Maria do Carmo");
        mapaNomes.put(3, "Claudinei Silva");

        System.out.println(mapaNomes);

        //resgatando o nome da posição 2
        System.out.println(mapaNomes.get(2));

    }
}