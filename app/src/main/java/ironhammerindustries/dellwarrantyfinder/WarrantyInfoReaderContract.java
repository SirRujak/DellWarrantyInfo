package ironhammerindustries.dellwarrantyfinder;

import android.provider.BaseColumns;

/**
 * Created by Rujak on 2/19/2015.
 */
public final class WarrantyInfoReaderContract {
    public WarrantyInfoReaderContract() {}

    public static abstract class WarrantyFeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_END = "end";
        public static final String COLUMN_NAME_START = "start";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_LEVEL = "level";
    }
}
