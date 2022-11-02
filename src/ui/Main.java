package ui;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;
import model.Control;
import model.Country;

public class Main {

    private Scanner sc;
    private Control control;
    private String databasePath = "Database.txt";
    private Gson gson;

    public static void main(String[] args) {
        Main main = new Main();
        String select = "";
        main.initDataBase();
        do{
            select = main.menu();
            main.select(select);

        }while(!select.equals("3"));
    }

    public Main() {
        sc = new Scanner(System.in);
        control = new Control();
        gson = new Gson();
    }

    public String menu(){
        System.out.println("(1) Insert command\n" +
                "(2) Import data from SQL file\n" +
                "(3) Exit");
        return sc.nextLine();
    }

    public void select(String select){
        String command;
        switch (select){
            case "1":
                command = sc.nextLine();
                redirect(command);
                writeJsonFile();
                break;

            case "2":
                System.out.println("Write the file's path");
                String filepath = sc.nextLine();
                readFile(filepath);
                writeJsonFile();
                break;

            case "3":
                System.out.println("Bye!");
                break;
            default:
                System.out.println("Typo\nInvalid option");
                break;
        }
    }

    public void redirect(String command){
        if(command.contains("INSERT INTO")){
            System.out.println(control.insertInto(command, ""));
        }else if(command.contains("SELECT * FROM")){
            System.out.println(control.select(command));
        }else if(command.contains("DELETE FROM")){
            System.out.println(control.delete(command));
        }else{
            control.typo();
        }
    }

    public void initDataBase() {
        File file = new File(databasePath);
        if (!file.exists()) {
            try {
                file = new File(databasePath);
                file.createNewFile();
                System.out.println("DataBase.txt Created");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            loadData();
            System.out.println("Database.txt read");
        }
    }

    // Read Json on the .txt file and Loads Data
    public void loadData(){
        if (new File(databasePath).exists()) {
            try {
                FileInputStream fis = new FileInputStream(databasePath);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                String line;

                while ((line = br.readLine()) != null) {
                    //control.countries = (ArrayList<Country>) gson.fromJson(line, ArrayList.class);
                    //control.countries.add(gson.fromJson(line, Country.class));
                    Country c = gson.fromJson(line, Country.class);
                    control.countries.add(c);
                }
                fis.close();
                br.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void readFile(String filePath) {
        if (new File(filePath).exists()) {
            try {
                FileInputStream fis = new FileInputStream(filePath);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                String line;

                while ((line = br.readLine()) != null) {
                    redirect(line);
                }
                fis.close();
                br.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeJsonFile(){
        File file = new File(databasePath);
        // Create file or delete its data
        try {
            if(!file.exists())
                file.createNewFile();
            else{
                //Clear data on DataBase.txt To Overwrite
                new FileWriter(databasePath, false).close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //write
        try {
            FileOutputStream fos = new  FileOutputStream(file);
            for (Country c : control.countries) {
                String json = gson.toJson(c);
                fos.write(json.getBytes(StandardCharsets.UTF_8));
                fos.write("\n".getBytes());
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
