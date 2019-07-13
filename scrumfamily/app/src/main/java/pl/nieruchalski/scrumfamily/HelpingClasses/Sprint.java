package pl.nieruchalski.scrumfamily.HelpingClasses;

/**
 * Created by michal on 04.02.17.
 */

public class Sprint {

    private String title;
    private String usernick;
    private String table_name;

    public Sprint(String usernick, String title) {
        this.title = title;
        this.usernick = usernick;
        this.table_name = usernick + "_" + title;
    }

    public Sprint(String table_name){

        int floorAtChar = 0;

        this.table_name = table_name;

        String usernick = "";

        for(int i=0; i<table_name.length(); i++){
            if(table_name.charAt(i) == '_'){floorAtChar = i; break;}
            else{usernick = usernick + table_name.charAt(i);}
        }

        String title = "";

        for(int i=floorAtChar+1; i<table_name.length(); i++){
            title = title + table_name.charAt(i);
        }

        this.usernick = usernick;
        this.title = title;

    }

    public String getTitle() {
        return title;
    }

    public String getUsernick() {
        return usernick;
    }

    public String getTable_name() {
        return table_name;
    }
}
