package com.turksat46.freakslabor;

import androidx.annotation.NonNull;

public class Endpoint {
    @NonNull private final String id;
    @NonNull private final String name;

    public Endpoint(@NonNull String id, @NonNull String name){
        this.id = id;
        this.name = name;
    }

    public String getId(){return id;}

    public String getName(){return name;}

    public boolean equals(Object obj){
        if(obj instanceof Endpoint){
            Endpoint other = (Endpoint) obj;
            return id.equals(other.id);
        }
        return false;
    }

    public int hashCode(){return id.hashCode();}

    public String toString(){return String.format("Endpoint{id=%s, name=%s}", id, name);}
}
