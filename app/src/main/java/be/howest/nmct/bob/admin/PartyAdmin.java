package be.howest.nmct.bob.admin;

import java.util.List;

/**
 * Created by Nick on 01/05/2015.
 */
public class PartyAdmin
{
    private static List<Party> parties;

    public static List<Party> getParties()
    {
        return parties;
    }

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
