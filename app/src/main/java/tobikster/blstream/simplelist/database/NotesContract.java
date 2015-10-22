package tobikster.blstream.simplelist.database;

import android.provider.BaseColumns;

/**
 * Created by tobikster on 2015-10-22.
 */
public final class NotesContract {
	public NotesContract() {
	}

	public static abstract class NotesEntry implements BaseColumns {
		public static final String TABLE_NAME = "Notes";
		public static final String COLUMN_NAME_NOTE_TITLE = "noteTitle";
		public static final String COLUMN_NAME_NOTE_CHECKED = "noteChecked";
	}
}
