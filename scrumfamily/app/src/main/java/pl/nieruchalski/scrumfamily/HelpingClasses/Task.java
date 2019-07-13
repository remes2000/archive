package pl.nieruchalski.scrumfamily.HelpingClasses;

/**
 * Created by michal on 02.02.17.
 */

public class Task {

    private String title;
    private String description;
    private int state;
    private String tookBy;
    private int id;


    public Task(String id, String title, String description, String state, String tookBy)
    {
        this.title = title;
        this.description = description;
        this.state = Integer.parseInt(state);
        this.id = Integer.parseInt(id);
        this.tookBy = tookBy;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public int getState(){return state;}

    public String getTookBy(){return  tookBy;}

    public int getId(){return id;}

}
