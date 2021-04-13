package hr.ferit.tivanovacmatija.pictureitpaklenica;

import java.io.Serializable;

public class Species implements Serializable {
    private String description;
    private String latin;
    private String name;
    private String url;

    public Species() {
    }

    public Species(String description, String latin, String name, String url) {
        this.setDescription(description);
        this.setLatin(latin);
        this.setName(name);
        this.setUrl(url);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatin() {
        return latin;
    }

    public void setLatin(String latin) {
        this.latin = latin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}



