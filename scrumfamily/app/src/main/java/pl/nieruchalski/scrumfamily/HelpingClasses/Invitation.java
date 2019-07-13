package pl.nieruchalski.scrumfamily.HelpingClasses;

/**
 * Created by michal on 08.02.17.
 */

public class Invitation {

    private Sprint sprint;

    public Invitation(String from, String sprintTitle)
    {
        sprint = new Sprint(from, sprintTitle);
    }

    public Sprint getSprint()
    {
        return sprint;
    }

}
