package be.howest.nmct.bob.admin;

import java.util.List;

/**
 * Created by Nick on 02/05/2015.
 */
public class BobAdmin
{
    private static List<Bob> bobs;

    public static List<Bob> getBobs()
    {
        return bobs;
    }

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
}