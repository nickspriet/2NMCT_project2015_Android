package be.howest.nmct.bob.admin;

/**
 * Created by Nick on 03/05/2015.
 */
public class PartyBob
{
    //properties
    private int PartyID;
    private int BobID;

    //empty constructor
    public PartyBob()
    {

    }

    //constructor with params
    public PartyBob(int partyID, int bobID)
    {
        PartyID = partyID;
        BobID = bobID;
    }


    //getters
    public int getPartyID()
    {
        return PartyID;
    }

    public int getBobID()
    {
        return BobID;
    }


    //setters

    public void setPartyID(int partyID)
    {
        PartyID = partyID;
    }

    public void setBobID(int bobID)
    {
        BobID = bobID;
    }
}
