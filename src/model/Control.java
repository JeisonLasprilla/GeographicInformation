package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Control {

    private ArrayList<Country> countries;
    private ArrayList<City> cities;
    private Matcher mat;
    private String [] arrValues;

    public Control() {
        countries = new ArrayList<>();
        cities = new ArrayList<>();
    }
    public String typo(){
        return "\n\tTypo\nInvalid command\n";
    }

    public String insertInto(String command, String values){

        String out = "";
        //INSERT INTO countries('6ec3e8ec-3dd0-11ed-b878-0242ac120002', 'Colombia', 50.2, '+57') VALUES
        Pattern intoCountries = Pattern.compile("INSERT INTO countries\\('([a-z0-9]|-)+', '([A-Z]|[a-z])*', ([0-9]|\\.)+, '\\+[0-9]+'\\) VALUES");
        //INSERT INTO cities('e4aa04f6-3dd0-11ed-b878-0242ac120002', 'Cali', '6ec3e8ec-3dd0-11ed-b878-0242ac120002', 2.2) VALUES
        Pattern intoCities = Pattern.compile("INSERT INTO cities\\('([a-z0-9]|-)+', '([A-Z]|[a-z])*', '([a-z0-9]|-)+', ([0-9]|\\.)+\\) VALUES");

        mat = intoCountries.matcher(command);
        if(mat.matches()){

            values = command.substring(22,command.length()-8);
            arrValues = values.split(",");
            String id = arrValues[0].substring(1, arrValues[0].length()-1);
            String name = arrValues[1].substring(2,arrValues[1].length()-1);
            String population = arrValues[2].substring(1);
            String countryCode = arrValues[3].substring(2, arrValues[3].length()-1);
            if(countryExists(id)){
                out = ("This country already exists");
            }else{
                countries.add(new Country(id, name, Double.valueOf(population), countryCode));
                out = countries.get(0).toPrint();
            }
        }

        mat = intoCities.matcher(command);
        if(mat.matches()){
            values = command.substring(19,command.length()-8);
            arrValues = values.split(",");
            String id = arrValues[0].substring(1,arrValues[0].length()-1);
            String name = arrValues[1].substring(2,arrValues[1].length()-1);
            String countryID = arrValues[2].substring(2,arrValues[2].length()-1);
            Integer population = 0;
            if(arrValues[3].contains(".")){
                String [] arrDouble = arrValues[3].split("\\.");
                System.out.println(arrDouble.length);

                if(arrDouble.length > 2){
                    out = "Double error";
                }else{
                    String p = arrDouble[0].substring(1);
                    population = Integer.valueOf(p);
                }
            }else{
                population = Integer.valueOf(arrValues[3].substring(1));
            }

            if(countryExists(countryID)){ // The country exists
                cities.add(new City(id, name, arrValues[2], population));
                out = cities.get(0).toPrint();
            }else{
                out = "The country does not exist";
            }
        }

        if(out.equals("")){
            out = typo();
        }

        return out;
    }

    public boolean countryExists(String countryID){

        boolean exist = false;
        for (Country c: countries) {
            if(countryID.equals(c.getId())){
                exist = true;
            }
        }
        return exist;
    }

    public void delete(String command, String values){

    }

    public void select(String command, String values){

    }

}
