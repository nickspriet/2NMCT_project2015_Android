package be.howest.nmct.bob.loader;

import android.provider.BaseColumns;

/**
 * Created by Nick on 28/04/2015.
 */

//klasse voor de verschillende kolommen uit de MatrixCursor
public class Contract
{
    public interface PartyColumns extends BaseColumns
    {
        public static final String COLUMN_PARTY_ID = "party_id";
        public static final String COLUMN_PARTY_NAME = "party_name";
        public static final String COLUMN_PARTY_DESCRIPTION = "party_description";
        public static final String COLUMN_PARTY_PICTURE = "party_picture";
        public static final String COLUMN_PARTY_ADDRESS = "party_address";
        public static final String COLUMN_PARTY_ZIPCODE = "party_zipcode";
        public static final String COLUMN_PARTY_CITY = "party_city";
        public static final String COLUMN_PARTY_FROMDATE = "party_fromdate";
        public static final String COLUMN_PARTY_UNTILDATE = "party_untildate";
        public static final String COLUMN_PARTY_PRICEPRESALE = "party_pricepresale";
        public static final String COLUMN_PARTY_PRICEATTHEDOOR = "party_priceatthedoor";
        public static final String COLUMN_PARTY_DISKJOCKEY1 = "party_diskjockey1";
        public static final String COLUMN_PARTY_DISKJOCKEY2 = "party_diskjockey2";
        public static final String COLUMN_PARTY_DISKJOCKEY3 = "party_diskjockey3";
        public static final String COLUMN_PARTY_LATITUDE = "party_latitude";
        public static final String COLUMN_PARTY_LONGITUDE = "party_longitude";
    }
}
