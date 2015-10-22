package tobikster.blstream.simplelist.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import tobikster.blstream.simplelist.R;

/**
 * Created by tobikster on 2015-10-21.
 */
public class CreateNoteDialog extends DialogFragment {
	public static final String ARG_EXISTING_NOTE_TITLE = "existing_note_title";
	public static final String ARG_EDIT_MODE = "edit_mode";
	private InteractionListener mListener;
	private String mExistingNoteTitle;
	private boolean mEditMode;

	public CreateNoteDialog() {
	}

	public static CreateNoteDialog newInstance() {
		return newInstance(null);
	}

	public static CreateNoteDialog newInstance(String existingNoteTitle) {
		Bundle args = new Bundle();
		CreateNoteDialog instance = new CreateNoteDialog();
		args.putString(ARG_EXISTING_NOTE_TITLE, existingNoteTitle);
		args.putBoolean(ARG_EDIT_MODE, existingNoteTitle != null);
		instance.setArguments(args);
		return instance;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (InteractionListener)activity;
		}
		catch(ClassCastException e) {
			throw new ClassCastException(activity.getClass().getSimpleName() + " must implement CreateNoteDialog.InteractionListener interface");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mExistingNoteTitle = args.getString(ARG_EXISTING_NOTE_TITLE);
		mEditMode = args.getBoolean(ARG_EDIT_MODE, false);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater layoutInflater = getActivity().getLayoutInflater();

		final View view = layoutInflater.inflate(R.layout.dialog_add_note, null);

		((EditText)view.findViewById(R.id.note_title_editor)).setText(mExistingNoteTitle);

		builder.setView(view).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText noteTitleEditor = (EditText)view.findViewById(R.id.note_title_editor);
				mListener.onCreateNoteConfirmed(CreateNoteDialog.this, noteTitleEditor.getText().toString(), mEditMode);
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onCreateDialogCanceled(CreateNoteDialog.this, mEditMode);
			}
		});
		return builder.create();
	}

	public interface InteractionListener {
		void onCreateNoteConfirmed(CreateNoteDialog dialog, String noteTitle, boolean editMode);

		void onCreateDialogCanceled(CreateNoteDialog dialog, boolean editMode);
	}
}
