package be.howest.nmct.bob.admin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 03/05/2015.
 */
public class PartyBobAdmin
{
    //property
    private static List<PartyBob> partyBobs;

    //getter
    public static List<PartyBob> getPartyBobs()
    {
        return partyBobs;
    }

    //setter
    public static void setPartyBobs(List<PartyBob> partyBobs)
    {
        PartyBobAdmin.partyBobs = partyBobs;
    }


}
