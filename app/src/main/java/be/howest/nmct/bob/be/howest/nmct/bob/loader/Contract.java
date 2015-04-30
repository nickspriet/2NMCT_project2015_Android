package be.howest.nmct.bob.be.howest.nmct.bob.loader;

import android.provider.BaseColumns;

/**
 * Created by Nick on 28/04/2015.
 */

//klasse voor de verschillende kolommen uit de MatrixCursor
public class Contract
{
    public interface PartyColumns extends BaseColumns
    {
        public static final String COLOMN_PARTY_NAME = "party_name";
        public static final String COLOMN_PARTY_PICTURE = "party_PICTURE";
    }
}
