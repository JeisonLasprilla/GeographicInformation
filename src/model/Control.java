package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Control {

    //Todo
    // Test
    // UML
    // Readme
    // Terminar los métodos
    // Comparator
    // SQL & JSON

    private ArrayList<Country> countries;
    private Matcher mat;
    private String[] arrValues;

    private String action;

    public Control() {
        countries = new ArrayList<>();
    }

    public String typo() {
        return "\n\tTypo\nInvalid command\n";
    }

    public String insertInto(String command, String values) {

        System.out.println(command);

        String out = "";
        //INSERT INTO countries(id, name, population, countryCode) VALUES ('6ec3e8ec-3dd0-11ed-b878-0242ac120002', 'Colombia', 50.2, '+57')
        Pattern intoCountries = Pattern.compile("INSERT INTO countries\\(id, name, population, countryCode\\) VALUES \\('([a-z0-9]|-)+', '([A-Z]|[a-z])*', (([0-9])|(\\.))+, '\\+([0-9])+'\\)");

        //INSERT INTO cities(id, name, countryID, population) VALUES ('91346eb8-3dd2-11ed-b878-0242ac120002', 'Puebla', '83b3e642-3dd2-11ed-b878-0242ac120002', 3.2)
        Pattern intoCities = Pattern.compile("INSERT INTO cities\\(id, name, countryID, population\\) VALUES \\('([a-z0-9]|-)+', '([A-Z]|[a-z])*', '([a-z0-9]|-)+', ([0-9]|\\.)+\\)");

        mat = intoCountries.matcher(command);
        if (mat.matches()) {

            values = command.substring(65, command.length() - 1);
            arrValues = values.split(",");
            String id = arrValues[0].substring(1, arrValues[0].length() - 1);
            String name = arrValues[1].substring(2, arrValues[1].length() - 1);
            String population = arrValues[2].substring(1);
            String countryCode = arrValues[3].substring(2, arrValues[3].length() - 1);
            if (countryExists(id)!=null) {
                out = ("This country already exists");
            } else {
                Country country = new Country(id, name, Double.valueOf(population), countryCode);
                countries.add(country);
                out = country.toPrint();
                //out = countries.get(0).toPrint();
            }
        }

        mat = intoCities.matcher(command);
        if (mat.matches()) {
            values = command.substring(60, command.length() - 1);
            arrValues = values.split(",");
            String id = arrValues[0].substring(1, arrValues[0].length() - 1);
            String name = arrValues[1].substring(2, arrValues[1].length() - 1);
            String countryID = arrValues[2].substring(2, arrValues[2].length() - 1);
            Integer population = 0;
            if (arrValues[3].contains(".")) {
                String[] arrDouble = arrValues[3].split("\\.");
                System.out.println(arrDouble.length);

                if (arrDouble.length > 2) {
                    out = "Double error";
                } else {
                    String p = arrDouble[0].substring(1);
                    population = Integer.valueOf(p);
                }
            } else {
                population = Integer.valueOf(arrValues[3].substring(1));
            }

            if (countryExists(countryID) != null) { // The country exists

                City city = new City(id, name, arrValues[2], population);
                countryExists(countryID).getCities().add(city);
                out = city.toPrint();

            } else {
                out = "The country does not exist";
            }
        }

        if (out.equals("")) {
            out = typo();
        }

        return out;
    }

    public Country countryExists(String countryID) {
        for (Country c : countries) {
            if (countryID.equals(c.getId())) {
                return c;
            }
        }
        return null;
    }

    public String select(String command) {

        action = "PRINT";

        //Sort and print
        if(command.contains(" ORDER BY ")){

            //SELECT * FROM countries WHERE population > 100 ORDER BY name
            //By population
            Pattern byPopulation = Pattern.compile("SELECT \\* FROM countries WHERE population [<>=] [0-9]+ ORDER BY ((name)|(population)|(countryCode)|(id))");
            mat = byPopulation.matcher(command);
            if (mat.matches()) {

                String [] arr = command.split(" ORDER BY ");
                String sort = arr[1];
                String number = arr[0].substring(43);
                String symbol = arr[0].substring(41,42);

                return manageMatches(symbol, sort, number, action, "COUNTRIES");
            }

            //SELECT * FROM cities WHERE name = 'Guadalajara' ORDER BY population
            //By name
            Pattern byName = Pattern.compile("SELECT \\* FROM cities WHERE name = '([A-Z]|[a-z])*' ORDER BY ((population)|(countryID)|(name)|(id))");
            mat = byName.matcher(command);
            if(mat.matches()){

                String [] arr = command.split("' ORDER BY ");
                String sort = arr[1];
                String cityName = arr[0].substring(35);

                return manageMatches(cityName, sort, action, "CITIES");
            }

            //SELECT * FROM countries ORDER BY population
            //All countries in order
            Pattern allCountriesInOrder = Pattern.compile("SELECT \\* FROM countries ORDER BY ((name)|(population)|(countryCode)|(id))");
            mat = allCountriesInOrder.matcher(command);
            if (mat.matches()) {

                String order = command.substring(33);
                return manageMatches(order, action, "COUNTRIES");
            }

            return "Typo";

        }else{// Just print

            //SELECT * FROM countries
            //Show all countries
            Pattern allCountries = Pattern.compile("SELECT \\* FROM countries");
            mat = allCountries.matcher(command);
            if (mat.matches()) {
                return manageMatches(null, action, "COUNTRIES");
            }

            //SELECT * FROM cities
            //Show all cities
            Pattern allCities = Pattern.compile("SELECT \\* FROM cities");
            mat = allCities.matcher(command);
            if (mat.matches()) {
                return manageMatches(null, action, "CITIES");
            }

            //SELECT * FROM cities WHERE name = 'Colombia'
            //Show city
            Pattern machingNames = Pattern.compile("SELECT \\* FROM cities WHERE name = '([A-Z]|[a-z])+'");
            mat = machingNames.matcher(command);

            if (mat.matches()) {
                String cityName = command.substring(35,command.length()-1);
                return manageMatches(cityName, null, action, "CITIES");
            }

            //SELECT * FROM countries WHERE population > 100
            //Show by population
            Pattern byPopulation = Pattern.compile("SELECT \\* FROM countries WHERE population [<>=] [0-9]+");
            mat = byPopulation.matcher(command);
            String symbol = command.substring(41,42);
            String number = command.substring(43);

            if (mat.matches()) {
                return manageMatches(symbol,null, number, action, "COUNTRIES");
            }
            return null;
        }
    }

    //Manage with symbols
    public String manageMatches(String symbol, String sort, String number, String action, String range){

        System.out.println(symbol);
        System.out.println(sort);
        System.out.println(number);
        System.out.println(action);
        System.out.println(range);

        if(symbol.equals(">")){

        }else if(symbol.equals("<")){

        }else{// =

        }

        return null;
    }


    //Manage all countries
    public String manageMatches(String sort, String action, String range){

        System.out.println(sort);

        return null;
    }

    //Manage by name
    public String manageMatches(String name, String sort, String action, String range){

        System.out.println(name);
        System.out.println(sort);
        return null;
    }


    public String delete(String command) {

        action = "DELETE";

        //DELETE FROM cities WHERE country = 'Colombia'
        Pattern byCountry = Pattern.compile("DELETE FROM cities WHERE country = '([A-Z]|[a-z])+'");
        mat = byCountry.matcher(command);

        if (mat.matches()) {
            String countryName = command.substring(36,command.length()-1);
            manageMatches(countryName, null, action, "COUNTRIES");
        }

        //DELETE FROM cities WHERE population > 50
        Pattern byPopulationForCities = Pattern.compile("DELETE FROM cities WHERE population [<>=] [0-9]+");
        mat = byPopulationForCities.matcher(command);

        if (mat.matches()) {
            String symbol = command.substring(36,37);
            String number = command.substring(38);
            manageMatches(symbol, null, number, action, "CITIES");
        }

        //DELETE FROM cities WHERE name = 'Colombia'
        Pattern cityName = Pattern.compile("DELETE FROM cities WHERE name = '([A-Z]|[a-z])+'");
        mat = cityName.matcher(command);

        if (mat.matches()) {
            String CTName = command.substring(33,command.length()-1);
            manageMatches(CTName, null, action, "CITIES");
        }

        //DELETE FROM countries WHERE population > 100
        Pattern byPopulationForCountries = Pattern.compile("DELETE FROM countries WHERE population [<>=] [0-9]+");
        mat = byPopulationForCountries.matcher(command);

        if (mat.matches()) {
            String symbol = command.substring(39,40);
            String number = command.substring(41);

            manageMatches(symbol, null, number, action, "CITIES");
        }

        //DELETE FROM countries
        Pattern deleteAll = Pattern.compile("DELETE FROM countries");
        mat = deleteAll.matcher(command);

        if (mat.matches()) {
            manageMatches(null, action, "COUNTRIES");
        }

        //DELETE FROM cities
        Pattern deleteAllCT = Pattern.compile("DELETE FROM cities");
        mat = deleteAllCT.matcher(command);

        if (mat.matches()) {
            manageMatches(null, action, "CITIES");
        }

        return null;
    }

}
