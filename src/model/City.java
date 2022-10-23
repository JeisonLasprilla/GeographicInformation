package model;

public class City {

    private String id;
    private String name;
    private String countryID;
    private Integer population;

    public City(String id, String name, String countryID, Integer population) {
        this.id = id;
        this.name = name;
        this.countryID = countryID;
        this.population = population;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public String toPrint() {
        return "City{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", countryID='" + countryID + '\'' +
                ", population=" + population +
                '}';
    }
}
