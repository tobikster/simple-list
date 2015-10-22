package tobikster.blstream.simplelist.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tobikster.blstream.simplelist.R;
import tobikster.blstream.simplelist.adapters.NotesListAdapterOld;
import tobikster.blstream.simplelist.database.NotesDataSource;
import tobikster.blstream.simplelist.dialogs.CreateNoteDialog;
import tobikster.blstream.simplelist.model.Note;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesListFragmentOld extends Fragment implements SearchView.OnQueryTextListener, NotesListAdapterOld.NoteViewHolder.OnInteractionListener, ActionMode.Callback {

	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "NotesListFragment";

	private RecyclerView mRecyclerView;
	private FloatingActionButton mAddNoteFAB;
	private NotesListAdapterOld mAdapter;
	private List<Note> mNotes;
	private String mActualQuery;
	private ActionMode mActionMode;
	private NotesDataSource mNotesDataSource;
	private Note mEditedNote;

	public NotesListFragmentOld() {
		// Required empty public constructor
	}

	public static NotesListFragmentOld newInstance() {
		NotesListFragmentOld newInstance = new NotesListFragmentOld();
		newInstance.mActualQuery = "";
		newInstance.mActionMode = null;
		newInstance.mEditedNote = null;
		return newInstance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_notes_list_old, container, false);

		mRecyclerView = (RecyclerView)view.findViewById(R.id.list_view);
		mAddNoteFAB = (FloatingActionButton)view.findViewById(R.id.add_note_fab);

		mNotesDataSource = new NotesDataSource(getActivity());
		mNotesDataSource.open();
		mNotes = mNotesDataSource.getAllNotes();

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setHasOptionsMenu(true);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		mAdapter = new NotesListAdapterOld(getActivity(), mNotes, this);
		mRecyclerView.setAdapter(mAdapter);

		mAddNoteFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CreateNoteDialog.newInstance().show(getFragmentManager(), "createNoteDialog");
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		mNotesDataSource.open();
	}

	@Override
	public void onPause() {
		super.onPause();
		mNotesDataSource.close();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.notes_list_fragment, menu);

		final MenuItem item = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView)MenuItemCompat.getActionView(item);
		searchView.setOnQueryTextListener(this);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String query) {
		mActualQuery = query;
		final List<Note> filteredNotesList = filter(mNotes);
		mAdapter.animateTo(filteredNotesList);
		mRecyclerView.scrollToPosition(0);
		return true;
	}

	private List<Note> filter(List<Note> notes) {
		String query = mActualQuery.toLowerCase();
		final List<Note> filteredNotesList = new ArrayList<>();
		for(Note note : notes) {
			final String title = note.getTitle().toLowerCase();
			if(title.contains(query)) {
				filteredNotesList.add(note);
			}
		}
		return filteredNotesList;
	}

	public void addOrModifyNote(String noteTitle) {
		Note note = new Note(noteTitle);
		if(mEditedNote != null) {
			note.setId(mEditedNote.getId());
			note.setChecked(mEditedNote.isChecked());
		}
		Note newNote = mNotesDataSource.createOrModifyNote(note);
		if(mEditedNote == null) {
			mNotes.add(newNote);
		}
		else{
			int index = mNotes.indexOf(newNote);
			mNotes.remove(index);
			mNotes.add(index, newNote);
		}
		if(newNote.getTitle().toLowerCase().contains(mActualQuery.toLowerCase())) {
			mAdapter.updateItems(newNote);
		}
		mEditedNote = null;
	}

	public void removeNote(Note note) {
		mNotesDataSource.deleteNote(note);
		mNotes.remove(note);
	}

	@Override
	public void onItemClicked(View view, int position) {
		selectOrDeselectItem(position);
	}

	@Override
	public boolean onItemLongClicked(View view, int position) {
		if(mActionMode == null) {
			mActionMode = getActivity().startActionMode(this);
			selectOrDeselectItem(position);
		}
		return true;
	}

	@Override
	public void onItemCheckboxChanged(CompoundButton v, int position, boolean state) {
		Note note = mAdapter.getItem(position);
		note.setChecked(state);
		mNotesDataSource.createOrModifyNote(note);
	}

	private void selectOrDeselectItem(int position) {
		if(mActionMode != null) {
			mAdapter.toggleSelection(position);
			if(mAdapter.getSelectedItemsCount() == 0) {
				mActionMode.finish();
			}
			else {
				mActionMode.invalidate();
			}
		}
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater menuInflater = mode.getMenuInflater();
		menuInflater.inflate(R.menu.note_context_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		menu.findItem(R.id.action_modify_note).setEnabled(!(mAdapter.getSelectedItemsCount() > 1));
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		boolean eventConsumed = false;
		switch(item.getItemId()) {
			case R.id.action_modify_note:
				mEditedNote = mAdapter.getItem(mAdapter.getSelectedItems().get(0));
				CreateNoteDialog.newInstance(mEditedNote.getTitle()).show(getActivity().getSupportFragmentManager(), "edit_note");
				mode.finish();
				eventConsumed = true;
				break;

			case R.id.action_delete_note:
				List<Integer> selectedItems = mAdapter.getSelectedItems();
				Collections.sort(selectedItems, new Comparator<Integer>() {
					@Override
					public int compare(Integer lhs, Integer rhs) {
						return rhs - lhs;
					}
				});
				for(int selectedNotePosition : selectedItems) {
					Note removedNote = mAdapter.removeItem(selectedNotePosition);
					removeNote(removedNote);
				}
				mode.finish();
				eventConsumed = true;
				break;
		}
		return eventConsumed;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mActionMode = null;
		mAdapter.clearSelections();
	}
}
