package be.howest.nmct.bob.admin;

import java.util.List;

/**
 * Created by Nick on 01/05/2015.
 */
public class PartyAdmin
{
    //property
    private static List<Party> parties;


    //getter
    public static List<Party> getParties()
    {
        return parties;
    }

    //setter
    public static void setParties(List<Party> lijstparties)
    {
        PartyAdmin.parties = lijstparties;
    }




    public static Party getPartyByID(int partyid)
    {
        for (Party party : getParties())
        {
            if (party.getID() == partyid) return party;
        }

        return null;
    }
}
