package model;
import exception.DoubleError;
import exception.InvalidCommand;
import exception.NoCountry;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Control{

    //Todo
    // Test
    // UML
    // Readme
    // tempCountry
    // Comparator
    // SQL & JSON [x]

    public ArrayList<Country> countries;
    private Matcher mat;
    private String[] arrValues;
    private ArrayList<City> selectedCities;
    private ArrayList<Country> selectedCountries;

    private String action;


    public Control() {
        selectedCities = new ArrayList<>();
        selectedCountries = new ArrayList<>();
        countries = new ArrayList<>();
    }

    public void typo() {
        try {
            throw new InvalidCommand();
        } catch (InvalidCommand e) {
            e.printStackTrace();
        }
    }


    public String insertInto(String command, String values) {

        System.out.println(command);

        String out = "";
        //INSERT INTO countries(id, name, population, countryCode) VALUES ('83b3e642-3dd2-11ed-b878-0242ac120002', 'México', 128.9, '+59')
        Pattern intoCountries = Pattern.compile("INSERT INTO countries\\(id, name, population, countryCode\\) VALUES \\('([a-z0-9]|-)+', '([A-Za-zÀ-ÿ ])*', ([0-9]|\\.)+, '\\+[0-9]+'\\)");
        //INSERT INTO cities(id, name, countryID, population) VALUES ('e4aa04f6-3dd0-11ed-b878-0242ac120002', 'Cali', '6ec3e8ec-3dd0-11ed-b878-0242ac120002', 2.2)
        Pattern intoCities = Pattern.compile("INSERT INTO cities\\(id, name, countryID, population\\) VALUES \\('([a-z0-9]|-)+', '([A-Za-zÀ-ÿ ])*', '([a-z0-9]|-)+', ([0-9]|\\.)+\\)");

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
            }
        }

        mat = intoCities.matcher(command);
        if (mat.matches()) {
            values = command.substring(60, command.length() - 1);
            arrValues = values.split(",");
            String id = arrValues[0].substring(1, arrValues[0].length() - 1);
            String name = arrValues[1].substring(2, arrValues[1].length() - 1);
            String countryID = arrValues[2].substring(2, arrValues[2].length() - 1);
            Double population = 0.0;
            if (arrValues[3].contains(".")) {
                String[] arrDouble = arrValues[3].split("\\.");
                System.out.println(arrDouble.length);

                if (arrDouble.length > 2) {
                    try {
                        out = "Double error";
                        throw new DoubleError();
                    } catch (DoubleError e) {
                        e.printStackTrace();
                    }

                } else {
                    String p = arrDouble[0].substring(1);
                    population = Double.valueOf(p);
                }
            } else {
                population = Double.valueOf(arrValues[3].substring(1));
            }

            if (countryExists(countryID) != null) { // The country exists

                City city = new City(id, name, arrValues[2], population);
                countryExists(countryID).getCities().add(city);
                out = city.toPrint();

            } else {
                try {
                    out = "The country does not exist";
                    throw new NoCountry();
                } catch (NoCountry e) {
                    e.printStackTrace();
                }
            }
        }

        if (out.equals("")) {
            out = "Invalid Command";
            typo();
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
            //String id, String name, String countryID, Integer population
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

            typo();
            return "Invalid command";

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
                return manageMatches(symbol,null, number, action, "CITIES");
            }
            return null;
        }
    }

    //Manage by symbols
    public String manageMatches(String symbol, String sort, String number, String action, String range){

       if(range.equals("COUNTRIES")){//COUNTRIES


           if(action.equals("PRINT")){//SHOW SELECTED COUNTRIES

               selectedCountries.clear();

               //SELECT
               filterCountriesBySymbol(symbol, Double.valueOf(number));

               //SORT COUNTRIES
               sortCountries(sort);

               //PRINT
               return printCountries();

           }else{//DELETE COUNTRIES
               deleteCountriesBySymbol(symbol, Double.valueOf(number));
               return "SUCCESSFUL DELETION";
           }

        }else{//CITIES

           if(action.equals("PRINT")){//FILTER CITIES

               selectedCities.clear();

               //SELECT
               filterCitiesBySymbol(symbol, Double.valueOf(number));

               //SORT CITIES
                sortCities(sort);

               //PRINT
               return printCities();

           }else{//DELETE
               deleteCitiesBySymbol(symbol, Double.valueOf(number));
               return "SUCCESSFUL DELETION";
           }
        }
    }

    //Manage all countries
    public String manageMatches(String sort, String action, String range){

        if(range.equals("COUNTRIES")){
            if(action.equals("PRINT")){

                selectedCountries = countries;

                //SORT CITIES
                sortCountries(sort);

                //PRINT
                return printCountries();

            }else{//DELETE
                deleteAllCountries();
                return "SUCCESSFUL DELETION";
            }
        }else{//CITIES
            if(action.equals("PRINT")){

                selectedCities.clear();

                for (int i = 0; i < countries.size() && countries.get(i) != null; i++) {
                    for (int j = 0; j < countries.get(i).getCities().size() && countries.get(i).getCities().get(j)!=null; j++) {
                        selectedCities.add(countries.get(i).getCities().get(j));
                    }
                }

                //SORT CITIES
                sortCities(sort);

                //PRINT
                return printCities();

            }else{//DELETE
                deleteAllCities();
                return "SUCCESSFUL DELETION";
            }
        }
    }

    public void deleteAllCountries(){
        for (int i = 0; i < countries.size() && countries.get(i) != null; i++) {
            countries.remove(i);
        }
    }

    public void deleteAllCities(){
        for (int i = 0; i < countries.size() && countries.get(i) != null; i++) {
            for (int j = 0; j < countries.get(i).getCities().size() && countries.get(i).getCities().get(j)!=null; j++) {
                countries.remove(i);
            }
        }
    }

    //Manage by name
    public String manageMatches(String name, String sort, String action, String range){

        if(range.equals("COUNTRIES")){
            if(action.equals("PRINT")){

                selectedCountries.clear();

                //SELECT
                filterCountriesByName(name);

                //SORT CITIES
                sortCountries(sort);

                //PRINT
                return printCountries();

            }else{//DELETE
                deleteCountriesByName(name);
                return "SUCCESSFUL DELETION";
            }
        }else{//CITIES
            if(action.equals("PRINT")){

                selectedCities.clear();

                //SELECT
                filterCitiesByName(name);

                //SORT CITIES
                sortCities(sort);

                //PRINT
                return printCities();

            }else{//DELETE
                deleteCitiesByName(name);
                return "SUCCESSFUL DELETION";
            }
        }
    }

    public String printCities(){
        StringBuilder show = null;
        for (City city : selectedCities) {
            show.append(city.toPrint() + "\n");
        }
        return String.valueOf(show);
    }

    public String printCountries(){
        StringBuilder show = new StringBuilder();
        for (Country country : selectedCountries) {
            show.append(country.toPrint() + "\n");
        }
        return String.valueOf(show);
    }

    public void deleteCitiesByName(String name){
        for (int i = 0; i < countries.size() && countries.get(i) != null; i++) {
            for (int j = 0; j < countries.get(i).getCities().size() && countries.get(i).getCities().get(j)!=null; j++) {
                if(countries.get(i).getCities().get(j).getName().equals(name)){
                    countries.remove(i);
                }
            }
        }
    }

    public void deleteCountriesByName(String name){
        for (int i = 0; i < countries.size() && countries.get(i) != null; i++) {
            if(countries.get(i).getName().equals(name)){
                countries.remove(i);
            }
        }
    }

    public void filterCitiesByName(String name){
        for (int i = 0; i < countries.size() && countries.get(i) != null; i++) {
            for (int j = 0; j < countries.get(i).getCities().size() && countries.get(i).getCities().get(j)!=null; j++) {
                if(countries.get(i).getCities().get(j).getName().equals(name)){
                    selectedCities.add(countries.get(i).getCities().get(j));
                }
            }
        }
    }

    public void filterCountriesByName(String name){
        for (int i = 0; i < countries.size() && countries.get(i) != null; i++) {
            if(countries.get(i).getName().equals(name)){
                selectedCountries.add(countries.get(i));
            }
        }
    }

    public void sortCities(String sort){
        if(sort!=null){
            if(sort.equals("id")){
                Collections.sort(selectedCities, (a,b)-> {
                    int x = a.getId().compareTo(b.getId());
                    return x;
                });
            }else if(sort.equals("name")){
                Collections.sort(selectedCities, (a,b)-> {
                    int x = a.getName().compareTo(b.getName());
                    return x;
                });
            }else if(sort.equals("countryID")){
                Collections.sort(selectedCities, (a,b)-> {
                    int x = a.getCountryID().compareTo(b.getCountryID());
                    return x;
                });
            }else{ //population
                Collections.sort(selectedCities, (a,b)-> {
                    int x = Double.compare(a.getPopulation(), b.getPopulation());
                    return x;
                });
            }
        }
    }

    public void sortCountries(String sort){
        if(sort!=null){
            if(sort.equals("name")){

                Collections.sort(selectedCountries, (a,b)-> {
                    int x = a.getName().compareTo(b.getName());
                    return x;
                });

            }else if(sort.equals("population")){

                Collections.sort(selectedCountries, (a,b)-> {
                    int x = Double.compare(a.getPopulation(),b.getPopulation());
                    return x;
                });

            }else if(sort.equals("countryCode")){

                Collections.sort(selectedCountries, (a,b)-> {
                    int x = a.getCountryCode().compareTo(b.getCountryCode());
                    return x;
                });

            }else{//id
                Collections.sort(selectedCountries, (a,b)-> {
                    int x = a.getId().compareTo(b.getId());
                    return x;
                });
            }
        }
    }

    //By symbol
    public void deleteCitiesBySymbol(String symbol, Double number){
        for (int i = 0; i < countries.size(); i++) {
            for (int j = 0; j < countries.get(i).getCities().size() && countries.get(i).getCities().get(j)!=null; j++) {

                if(symbol.equals(">")){
                    if(countries.get(i).getCities().get(j).getPopulation() > number){
                        countries.get(i).getCities().remove(j);
                    }
                }else if(symbol.equals("<")){
                    if(countries.get(i).getCities().get(j).getPopulation() < number){
                        countries.get(i).getCities().remove(j);
                    }
                }else{// =
                    if(countries.get(i).getCities().get(j).getPopulation() == number){
                        countries.get(i).getCities().remove(j);
                    }
                }
            }
        }
    }

    //By symbol
    public void filterCitiesBySymbol(String symbol, Double number){
        for (int i = 0; i < countries.size() && countries.get(i)!=null; i++) {
            for (int j = 0; j < countries.get(i).getCities().size() && countries.get(i).getCities().get(j)!=null; j++) {

                if(symbol.equals(">")){
                    if(countries.get(i).getCities().get(j).getPopulation() > number){
                            selectedCities.add(countries.get(i).getCities().get(j));
                    }
                }else if(symbol.equals("<")){
                    if(countries.get(i).getCities().get(j).getPopulation() < number){
                            selectedCities.add(countries.get(i).getCities().get(j));
                    }
                }else{// =
                    if(countries.get(i).getCities().get(j).getPopulation() == number){
                            selectedCities.add(countries.get(i).getCities().get(j));
                    }
                }
            }
        }
    }

    public void deleteCountriesBySymbol(String symbol, Double number){
        for (int i = 0; i < countries.size() && countries.get(i)!=null; i++) {

            if(symbol.equals(">")){
                if(countries.get(i).getPopulation() > number){
                    countries.remove(countries.get(i));
                }
            }else if(symbol.equals("<")){
                if(countries.get(i).getPopulation() < number){
                    countries.remove(countries.get(i));
                }
            }else{// =
                if(countries.get(i).getPopulation() == number){
                    countries.remove(countries.get(i));
                }
            }
        }
    }

    public void filterCountriesBySymbol(String symbol, Double number){
        for (int i = 0; i < countries.size() && countries.get(i)!=null; i++) {

            if(symbol.equals(">")){
                if(countries.get(i).getPopulation() > number){
                    selectedCountries.add(countries.get(i));
                }
            }else if(symbol.equals("<")){
                if(countries.get(i).getPopulation() < number){
                    selectedCountries.add(countries.get(i));
                }
            }else{// =
                if(countries.get(i).getPopulation() == number){
                    selectedCountries.add(countries.get(i));
                }
            }
        }
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
