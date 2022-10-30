package ui;

import java.util.Scanner;
import model.Control;

public class Main {

    private Scanner sc;
    private Control control;

    public static void main(String[] args) {

        Main main = new Main();
        String select = "";
        do{
            select = main.menu();
            main.select(select);

        }while(!select.equals("3"));
    }

    public Main() {
        sc = new Scanner(System.in);
        control = new Control();
    }

    public String menu(){
        System.out.println("(1) Insert command\n" +
                "(2) Import data from SQL file\n" +
                "(3) Exit");
        return sc.nextLine();
    }

    public void select(String select){
        switch (select){
            case "1":
                String command = sc.nextLine();
                redirect(command);
                break;

            case "2":
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
}
