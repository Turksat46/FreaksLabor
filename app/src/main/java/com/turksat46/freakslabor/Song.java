package com.turksat46.freakslabor;

public class Song {
    private String id;
    private String name;
    private String imageURL;

    public Song(String id, String name, String url) {
        this.name = name;
        this.id = id;
        this.imageURL = url;
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

    public String getImageURL(){
        return imageURL;
    }

    public void setImageURL(String url){
        this.imageURL = url;
    }
}
