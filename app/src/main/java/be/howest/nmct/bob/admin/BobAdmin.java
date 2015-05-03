package be.howest.nmct.bob.admin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 02/05/2015.
 */
public class BobAdmin
{
    //property
    private static List<Bob> bobs;


    //getter
    public static List<Bob> getBobs()
    {
        return bobs;
    }

    //setter
    public static void setBobs(List<Bob> bobs)
    {
        BobAdmin.bobs = bobs;
    }



    public static Bob getBobByID(int bobid)
    {
        for (Bob bob : getBobs())
        {
            if (bob.getID() == bobid) return bob;
        }

        return null;
    }


    //get all the bobs from 1 party
    public static List<Bob> getBobsFromPartyID(int parytid)
    {
        List<Bob> bobs = new ArrayList<>();

        for (PartyBob pb : PartyBobAdmin.getPartyBobs())
        {
            if (pb.getPartyID() == parytid)
            {
                bobs.add(BobAdmin.getBobByID(pb.getBobID()));
            }
        }

        return bobs;
    }
}