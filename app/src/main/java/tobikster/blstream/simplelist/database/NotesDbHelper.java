package tobikster.blstream.simplelist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tobikster on 2015-10-22.
 */
public class NotesDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "Notes.db";

	private static final String SQL_CREATE_NOTES =
			"CREATE TABLE " + NotesContract.NotesEntry.TABLE_NAME + " (" +
					NotesContract.NotesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					NotesContract.NotesEntry.COLUMN_NAME_NOTE_TITLE + " TEXT," +
					NotesContract.NotesEntry.COLUMN_NAME_NOTE_CHECKED + " INTEGER" +
					")";
	private static final String SQL_DELETE_NOTES =
			"DROP TABLE IF EXISTS " + NotesContract.NotesEntry.TABLE_NAME;

	public NotesDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_NOTES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_NOTES);
		onCreate(db);
	}
}
